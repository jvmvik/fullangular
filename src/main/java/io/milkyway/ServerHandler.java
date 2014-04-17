package io.milkyway;

import com.google.gson.*;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<Object>
{
  Router router = new Router();
  ServerStaticHandler serverStaticHandler = new ServerStaticHandler(false);

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
  {
    ctx.flush();
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Object msg)
  {
    Router.Result result = null;
    String uri;
    if(msg instanceof HttpRequest)
    {
      uri = ((HttpRequest) msg).getUri();
      if(uri.startsWith("/do/reload"))                 // Reload application backend
      {
        Log.info("Reload application backend...");
        Backend.reload();
      }
      else if((result = router.get(uri)) != null) // Execute function
      {
        try
        {
          //Object s = result.getFunction().apply(json.toString());
          //ctx.writeAndFlush(Rest.ok((String) s));
          Map<String, String> map = result.getParams();
          map.putAll(toParams((HttpRequest) msg));
          result.getFunction().apply(new Context(map, ctx, (HttpRequest) msg));
        }
        catch(Exception ex)
        {
          ex.printStackTrace();
          ctx.writeAndFlush(Rest.fail(ex));
        }
        finally
        {
          ctx.close();
        }
        return;
      }
      else if(msg instanceof HttpRequest)   // Serve web content
      {
        try
        {
          serverStaticHandler.messageReceived(ctx, (HttpRequest)msg);
        }
        catch(Exception e)
        {
          e.printStackTrace();
          serverStaticHandler.sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
      }
      else
      {
        Log.warning("Message is not handled..");
      }
    }

    /*
      HttpRequest request = this.request = (HttpRequest) msg;

      buf.append("VERSION: ").append(request.getProtocolVersion()).append("\r\n");
      buf.append("HOSTNAME: ").append(getHost(request, "unknown")).append("\r\n");
      buf.append("REQUEST_URI: ").append(request.getUri()).append("\r\n\r\n");

      HttpHeaders headers = request.headers();
      if(!headers.isEmpty())
      {
        for(Map.Entry<String, String> h : headers)
        {
          String key = h.getKey();
          String value = h.getValue();
          buf.append("HEADER: ").append(key).append(" = ").append(value).append("\r\n");
        }
        buf.append("\r\n");
      }

      QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
      Map<String, List<String>> params = queryStringDecoder.parameters();
      if(!params.isEmpty())
      {
        for(Entry<String, List<String>> p : params.entrySet())
        {
          String key = p.getKey();
          List<String> vals = p.getValue();
          for(String val : vals)
          {
            buf.append("PARAM: ").append(key).append(" = ").append(val).append("\r\n");
          }
        }
        buf.append("\r\n");
      }

      appendDecoderResult(buf, request);
    }
*/
  }

  private JsonObject mapAsJson(Map<String, String> params)
  {
    JsonObject json = new JsonObject();
    for(String key: params.keySet())
      json.addProperty(key, params.get(key));
    return json;
  }

  private Map<String, String> toParams(HttpRequest msg)
  {
    Map<String, String> map = new HashMap<>();
    QueryStringDecoder queryStringDecoder = new QueryStringDecoder(msg.getUri());
    Map<String, List<String>> params = queryStringDecoder.parameters();
    for(Entry<String, List<String>> p : params.entrySet())
      map.put(p.getKey(), p.getValue().get(0));
    return map;
  }

  private JsonElement createArray(List<String> values)
  {
    if(values.size() == 1)
      return new JsonPrimitive(values.get(0));

    JsonArray list = new JsonArray();
    for(String s: values)
      list.add(new JsonPrimitive(s));
    return list;
  }

  private static void appendDecoderResult(StringBuilder buf, HttpObject o)
  {
    DecoderResult result = o.getDecoderResult();
    if(result.isSuccess())
      return;

    buf.append(".. WITH DECODER FAILURE: ");
    buf.append(result.cause());
    buf.append("\r\n");
  }

  /*
  private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx)
  {
    // Decide whether to close the connection or not.
    boolean keepAlive = isKeepAlive(request);
    // Build the response object.
    FullHttpResponse response = new DefaultFullHttpResponse(
        HTTP_1_1, currentObj.getDecoderResult().isSuccess() ? OK : BAD_REQUEST,
        Unpooled.copiedBuffer(buf.toString(), CharsetUtil.UTF_8));

    response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");

    if(keepAlive)
    {
      // Add 'Content-Length' header only for a keep-alive connection.
      response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
      // Add keep alive header as per:
      // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
      response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
    }

    // Encode the cookie.
    String cookieString = request.headers().get(COOKIE);
    if(cookieString != null)
    {
      Set<Cookie> cookies = CookieDecoder.decode(cookieString);
      if(!cookies.isEmpty())
      {
        // Reset the cookies if necessary.
        for(Cookie cookie : cookies)
        {
          response.headers().add(SET_COOKIE, ServerCookieEncoder.encode(cookie));
        }
      }
    }
    else
    {
      // Browser sent no cookie.  Add some.
      response.headers().add(SET_COOKIE, ServerCookieEncoder.encode("key1", "value1"));
      response.headers().add(SET_COOKIE, ServerCookieEncoder.encode("key2", "value2"));
    }

    // Write the response.
    ctx.write(response);

    return keepAlive;
  }
  */


  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
  {
    cause.printStackTrace();
    ctx.close();
  }

}

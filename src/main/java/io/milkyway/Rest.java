package io.milkyway;

import com.google.gson.JsonObject;
import io.netty.buffer.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * Create REST response easily..
 *
 * @author victor@milkyway.io
 * @date 4/12/14
 */
public class Rest
{

  /***
   * @return ok http response
   */
  public static FullHttpResponse ok()
  {
    return build(HttpResponseStatus.OK);
  }

  /***
   * @param content message to render
   * @return ok http response with message
   */
  public static FullHttpResponse ok(String content)
  {
    return build(HttpResponseStatus.OK, content);
  }

  /***
   * Create a default http response without content
   *
   * @param status http response status
   * @return http response
   */
  public static FullHttpResponse build(HttpResponseStatus status)
  {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
  }

  public static FullHttpResponse fail(Exception exception)
  {
    JsonObject json = new JsonObject();
    json.addProperty("message", exception.getMessage());
    String stacktrace = "";
    for(StackTraceElement e: exception.getStackTrace())
    {
      if(stacktrace.length() > 0)
        stacktrace += "<br>";
      stacktrace += e.toString();
    }
    json.addProperty("stacktrace", stacktrace);
    return build(HttpResponseStatus.INTERNAL_SERVER_ERROR, json);
  }

  public static FullHttpResponse build(HttpResponseStatus status, JsonObject json)
  {
    return build(status, StringEncoder.encode(json.toString()));
  }

  public static FullHttpResponse build(HttpResponseStatus status, String content)
  {
    return build(status, StringEncoder.encode(content));
  }

  public static FullHttpResponse build(HttpResponseStatus status, ByteBuf byteBuf)
  {
    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, byteBuf);
  }


  static ByteBuf toMessage(String msg)
  {
    JsonObject json = new JsonObject();
    json.addProperty("msg", msg);
    return StringEncoder.encode(json.toString());
  }

}

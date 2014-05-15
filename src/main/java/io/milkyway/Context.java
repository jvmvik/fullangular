package io.milkyway;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

import java.util.Map;

/**
 * Request context
 *
 * @author victor@milkyway.io
 * @date 4/16/14
 */
public class Context
{
  private final String method;
  private final Map<String, String> params;
  private final ChannelHandlerContext chc;
  private final HttpRequest request;

  public Context(Map<String, String> params, ChannelHandlerContext chc, HttpRequest msg)
  {
    this.params = params;
    this.chc = chc;
    this.method = msg.getMethod().name();
    this.request = msg;
  }

  /***
   *
   * @param json
   */
  public void ok(String json)
  {
    chc.writeAndFlush(Rest.ok(json));
  }

  public void ok()
  {
   chc.writeAndFlush(Rest.ok());
  }

  public void fail(Exception exception)
  {
    chc.writeAndFlush(Rest.fail(exception));
  }

  /***
   * Method of request (GET, POST..)
   * @return "GET", "POST" ...
   */
  public String getMethod()
  {
    return method;
  }

  /***
   * @return all request parameters
   */
  public Map<String, String> getParams()
  {
    return params;
  }

  /***
   * Parameters as a JSON object
   * it's easy to transform to a POJO
   * @return string representing a JSON object;
   */
  public String getJson()
  {
    String j = "";
    for(String key: params.keySet())
    {
      if(j.length() > 0)
        j += ",\n";
      j += "\""+key+"\":\""+params.get(key)+"\"";
    }
    return "{\n" + j + "\n}";
  }

  public HttpRequest getRequest()
  {
    return request;
  }

  public ChannelHandlerContext getChc()
  {
    return chc;
  }
}

package io.milkyway;

import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMethod;
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
  final Map<String, String> params;
  private final ChannelHandlerContext ctx;
  private final HttpRequest request;

  public Context(Map<String, String> params, ChannelHandlerContext ctx, HttpRequest msg)
  {
    this.params = params;
    this.ctx = ctx;
    this.method = msg.getMethod().name();
    this.request = msg;
  }

  public void ok(String s)
  {
    ctx.writeAndFlush(Rest.ok(s));
  }

  public void fail(Exception exception)
  {
    ctx.writeAndFlush(Rest.fail(exception));
  }
}

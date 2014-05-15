package io.milkyway;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Forecast controller
 *
 * @author victor@milkyway.io
 * @date 4/12/14
 */
public class RestTest
{
  @Test
  public void testOk() throws Exception
  {
    HttpResponse response = Rest.ok();
    assertEquals(HttpResponseStatus.OK, response.getStatus());
  }

  @Test
  public void testBuild1() throws Exception
  {
    FullHttpResponse response = Rest.build(HttpResponseStatus.OK, Rest.toMessage("great!"), "json/application");
    assertEquals(HttpResponseStatus.OK, response.getStatus());
    assertEquals("{\"msg\":\"great!\"}", StringEncoder.decode(response.content()));
  }

  @Test
  public void testBuild() throws Exception
  {
    JsonObject json = new JsonObject();
    json.add("status", new JsonPrimitive(0));
    FullHttpResponse response = Rest.build(HttpResponseStatus.OK, json);
    assertEquals(HttpResponseStatus.OK, response.getStatus());
    assertEquals("{\"status\":0}", StringEncoder.decode(response.content()));
  }

  @Test
  public void testToMessage() throws Exception
  {
    ByteBuf buf = Rest.toMessage("hello world");
    assertEquals("{\"msg\":\"hello world\"}", StringEncoder.decode(buf));
  }
}

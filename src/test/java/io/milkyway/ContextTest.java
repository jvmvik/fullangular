package io.milkyway;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ContextTest
{

  @Test
  public void getJson() throws Exception
  {
    Map<String, String> map = new HashMap<>();
    map.put("a","1");
    map.put("b","2");
    Context context = new Context(map, null, new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, "/"));

    assertEquals("{\n" +
        "\"a\":\"1\",\n" +
        "\"b\":\"2\"\n" +
        "}", context.getJson());
  }
}
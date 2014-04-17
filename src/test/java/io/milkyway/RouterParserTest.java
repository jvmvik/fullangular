package io.milkyway;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author jvmvik@milkyway.io
 */
public class RouterParserTest
{
  RouterParser parser;

  @Before
  public void setUp() throws Exception
  {
    parser = new RouterParser();
  }

  @Test
  public void match() throws Exception
  {

  }

  @Test
  public void testMatch() throws Exception
  {
    Map<String, String> params = parser.match("/service", "/service");
    assertNotNull(params);
    assertEquals(0, params.size());

    params = parser.match("/service/:action", "/service/list");
    assertEquals(1, params.size());
    assertEquals("list", params.get("action"));

    params = parser.match("/service/:method/:id", "/service/show/101");
    assertEquals(2, params.size());
    assertEquals("show", params.get("method"));
    assertEquals("101", params.get("id"));

    try
    {
      parser.match("/service", "/anotherService");
      fail("this does not match");
    } catch (RouterException ex)
    {
    }

    try
    {
      parser.match("/service/method/:action", "/service/action");
      fail("this does not match");
    } catch (RouterException ex)
    {
    }

    try
    {
      parser.match("/service/method/:action", "/service/otherMethod/1");
      fail("this does not match");
    } catch (RouterException ex)
    {
    }
  }

  @Test
  public void testMachAll() throws RouterException
  {
    // include all
    Map<String, String> params = parser.match("/service/:method/*", "/service/show/all/1/10");

    assertEquals("show", params.get("method"));
    assertEquals(1, params.size());

    // exclusion
    params = parser.match("/service/:method/*", "/service/list");
    assertEquals("list", params.get("method"));
    assertEquals(1, params.size());
  }
}


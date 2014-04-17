package io.milkyway;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Router enables to route a http request
 *  and return the registered handler
 *
 * @author victor@milkyway.io
 * @date 4/13/14
 */
public class Router
{

  private final RouterParser parser;
  private Map<String, Function> requestHandlerMap;

  public Router()
  {
    requestHandlerMap = new HashMap<>();
    parser = new RouterParser();
  }

  /**
   * Add a requestHandler associated to an regexurl.
   *
   * Example of regexurl
   *
   *  - /serviceName/:action/:id
   *  - /serviceName/action/:objectType/:id
   *
   * @param regexurl pattern that provides concise and flexible syntax
   * @param requestHandler called when matching request found
   */
  public void add(String regexurl, Function requestHandler) throws RouterException
  {
    // Check regular regexurl if valid
    check(regexurl);

    // Verify collision
    if (collision(regexurl))
      throw new RouterException("regexurl has already been defined : " + regexurl);

    // if no exception then put request handler
    requestHandlerMap.put(regexurl, requestHandler);
  }

  /**
   * Check if the routing regexurl collide with a routing previously defined.
   *
   * @param regexurl routing key
   * @return true when collide with an other regexurl
   */
  public boolean collision(String regexurl)
  {
    return get(regexurl) != null;
  }

  /**
   * Check if regexurl is valid for URL parsing.
   *
   * @param regexurl regex like regexurl to analyze the url
   * @throws RouterException
   */
  public void check(String regexurl) throws RouterException
  {
    if (regexurl == null || regexurl.length() == 0)
      throw new RouterException("regexurl is empty");
    // Check syntax
    if (!regexurl.startsWith("/"))
      throw new RouterException("regexurl must start with /");
    if (regexurl.startsWith("/:"))
      throw new RouterException("regexurl syntax error : first element must be a constant, not a variable. like : /constant instead of /:variable");
    if (regexurl.matches("^/[\\w]+/\\*$"))
      throw new RouterException("regexurl must be at least : /service/action/* != " + regexurl);
  }

  /**
   * Find request handler by URL
   *
   * @param url requested URL
   * @return requestHandler which match with url
   * @throws RouterException when no handler match
   */
  public Result get(String url)
  {
    if (requestHandlerMap.size() == 0)
      return null;
      //throw new RouterException("No request handler defined !");

    // Iterate over regexurl stored
    for (String regexurl : requestHandlerMap.keySet())
    {
      Result result = new Result();
      try
      {
        result.setFunction(requestHandlerMap.get(regexurl));
        result.setParams(parser.match(regexurl, url));
        return result;
      }
      catch(RouterException ex)
      {
        // Skip
      }
    }
    return null;
    //throw new RouterException("No request handler matched with URL : " + url);
  }

  class Result
  {
    Function function;
    Map<String, String> params;

    public void setFunction(Function function)
    {
      this.function = function;
    }

    public void setParams(Map<String, String> params)
    {
      this.params = params;
    }

    public Function getFunction()
    {
      return function;
    }

    public Map<String, String> getParams()
    {
      return params;
    }
  }
}

package io.milkyway;

import java.util.*;

/**
 * Parse routing expression.
 *
 * @author vicben01
 */
public class RouterParser
{

  /**
   * Check if expression match an URL
   *
   * RegexUrl supported
   * ------
   * The following expression are supported:
   * /namedService
   * /namedService/:variable1
   * /namedService/action/variable
   * /namedService/:method/:variable
   * /namedService/action/*
   * /namedService/:action?param1=value1
   *
   * @param regexurl
   * @param url
   * @return
   */
  public Map<String, String> match(String regexurl, String url) throws RouterException
  {
    Map<String, String> params = new HashMap<>();
    String[] elements = new String[0];

    // Number of elements
    int j = 0;
    // Create list of variables
    List<String> variables = new ArrayList<>();

      // Expression analyse
      elements = regexurl.split("/");

      for(String e : elements)
      {
        if(e.startsWith(":")) // put variable
        {
          variables.add(e);
        }
        else if(!e.contains("*")) // exclude * char
        {
          j++;
        }
      }

    String rootURL;    // URL without parameters
    if (url.indexOf("?") > 0)
    {
      rootURL = url.substring(0, url.indexOf("?"));
    } else
    {
      rootURL = url;
    }

    // URL analyse
    String[] parts = rootURL.split("/");

    if (parts.length == elements.length && variables.size() == 0)
    {
      if (Arrays.equals(parts, elements))
        return params;

      throw new RouterException("URL does not match");
    } else if ((parts.length == elements.length
        && variables.size() > 0)
        || regexurl.endsWith("*"))
    {
      // Check size match
      if (!Arrays.equals(Arrays.copyOfRange(elements, 0, j), Arrays.copyOfRange(parts, 0, j)))
        throw new RouterException("URL does not match with expression : " + rootURL + " / " + regexurl);

      // Decode /:action/:option1/:option2
      int offset = elements.length - variables.size();
      int length = elements.length;
      if (regexurl.endsWith("*"))
      {
        offset--;
        length = Arrays.asList(elements).indexOf("*");
      }

      String[] keys = Arrays.copyOfRange(elements, offset, length);
      String[] values = Arrays.copyOfRange(parts, offset, length);
      for (int i = 0; i < length - offset; i++)
      {
        params.put(keys[i].substring(1), values[i]);
      }

      return params;
    }

    throw new RouterException("URL does not match");
  }
}


package io.milkyway;

import java.util.HashMap;
import java.util.Map;

/**
 * Forecast controller
 *
 * @author victor@milkyway.io
 * @date 5/14/14
 */
public class Errors extends Exception
{
  Map<String, String> errors;

  public Errors()
  {
    errors = new HashMap<>();
  }

  public Errors(String message)
  {
    super(message);
    errors = new HashMap<>();
  }

  public void add(String key, String message)
  {
    errors.put(key, message);
  }

  public Map<String, String> getErrors()
  {
    return errors;
  }
}

package io.milkyway;

/**
 * Generic log implementation for the backend
 *
 * @author victor@milkyway.io
 * @date 4/10/14
 */
public class Log
{
  static void error(String msg)
  {
    dump("ERROR", msg);
  }

  static void warning(String msg)
  {
    dump("WARNING", msg);
  }

  static void info(String msg)
  {
    dump("INFO", msg);
  }

  static void debug(String msg)
  {
    dump("DEBUG", msg);
  }

  static private void dump(String level, String msg)
  {
    String s = level + ": " + msg;
    if(msg.endsWith("/n"))
      System.out.print(s);
    else
      System.out.println(s);
  }
}

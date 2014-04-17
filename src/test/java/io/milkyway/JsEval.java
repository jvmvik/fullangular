package io.milkyway;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;

/**
 * Evaluate JavaScript from Java
 */
public class JsEval
{
  static ScriptEngineManager manager;
  static ScriptEngine engine;

  static
  {
    manager = new ScriptEngineManager();
    engine = manager.getEngineByName("nashorn");
  }

  public static ScriptEngine eval(String s) throws ScriptException
  {
    engine.eval(s);
    return engine;
  }

  public static Invocable eval(Path path) throws FileNotFoundException, ScriptException
  {
    engine.eval(new FileReader(path.toFile()));
    return (Invocable)engine;
  }

  // Pre-load nashorn
  public static void load()
  {
    try
    {
      engine.eval("");
    }
    catch(ScriptException e)
    {
      e.printStackTrace();
    }
  }
}

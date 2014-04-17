package io.milkyway;


import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
//TOOD Use import lombok.Setter;

/**
 * Start server backend
 *
 * @author victor@milkyway.io
 * @date 4/7/14
 */
public class Backend
{
  static final String LOCATION = "src/main/resources/backend";
  static final String NASHORN = "nashorn";
  static ScriptEngine engine;
  static ScriptEngineManager manager;

  static
  {
    manager = new ScriptEngineManager();
    engine = manager.getEngineByName(NASHORN);
  }

  static Backend backend;

  //@Setter
  Path frameworkPath;

  //@Setter
  Path appPath;

  //TODO reload back on demand
  public static void reload()
  {
    if(backend == null)
      throw new RuntimeException("Backend is not initialed");

    //TODO Clear engine previous execution
    backend.clear();
    backend.load();
  }

  public static void start(Path appPath, Path frameworkPath)
  {
    if(backend == null)
      backend = new Backend();

    backend.setAppPath(appPath);
    backend.setFrameworkPath(frameworkPath);
    backend.load();
  }

  private void clear()
  {
    engine = manager.getEngineByName(NASHORN);
  }

  public void load()
  {
    // Load framework
    load(frameworkPath);
    // Load backend script for the
    load(appPath);
  }

  static void load(Path root)
  {
    root = root.resolve(LOCATION);
    try
    {
      Files.list(root).filter(p -> p.getFileName().toString().endsWith(".js")).forEach(js -> {
        FileInputStream fis = null;
        try
        {
          fis = new FileInputStream(js.toFile());
          engine.eval(new InputStreamReader(fis));
        }
        catch(FileNotFoundException e)
        {
          e.printStackTrace();
        }
        catch(ScriptException e)
        {
          System.err.println(js.getFileName() + " fail: " + e.getMessage());
        }
        System.out.println("Load::" + js);
      });
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }

  public void setFrameworkPath(Path frameworkPath)
  {
    this.frameworkPath = frameworkPath;
  }

  public void setAppPath(Path appPath)
  {
    this.appPath = appPath;
  }

}

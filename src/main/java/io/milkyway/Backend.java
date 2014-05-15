package io.milkyway;


import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
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
  private static final String MILKYWAY = "milkyway";
  private static final String SERVER = "server";
  static ScriptEngine engine;
  static ScriptEngineManager manager;
  static Backend backend;

  Path frameworkPath;
  Path applicationPath;


  static
  {
    manager = new ScriptEngineManager();
    engine = manager.getEngineByName(NASHORN);
  }

  /*
   * Reload back on demand
   */
  public static void reload() throws BackendException
  {
    Log.info("Reloading application on demand...");
    if(backend == null)
      throw new RuntimeException("Backend is not initialized...");

    // Attach server context
    Server server = (Server)backend.engine.getContext().getAttribute(SERVER);

    // Stop server
    server.stop();

    // Clear code previously load
    backend.clear();

    Log.info("Re-starting server...");

    // Load the backend again
    backend.load();

    // Start the application
    backend.bootstrap();
  }

  /***
   * Start backend with applicationPath and frameworkPath
   *
   * @param applicationPath
   * @param frameworkPath
   * @return
   */
  public static Backend start(Path applicationPath, Path frameworkPath) throws BackendException
  {
    if(backend == null)
      backend = new Backend();

    backend.setApplicationPath(applicationPath);
    backend.setFrameworkPath(frameworkPath);
    backend.load();
    backend.bootstrap();
    return backend;
  }

  private void bootstrap() throws BackendException
  {
    Object server = engine.getContext().getAttribute(SERVER);
    if(server == null)
      throw new BackendException("milkyway is not available in the javascript execution context.");
    ((Server)server).start();
  }

  private void clear()
  {
    Log.debug("Clear environment context...");
    engine = manager.getEngineByName(NASHORN);
  }

  /***
   * Load application
   */
  public void load() throws BackendException
  {
    // Load framework
    load(frameworkPath);
    // Load backend script for the
    load(applicationPath);
  }

  /***
   * Load set of javascript present in the root directory
   *
   * @param root
   */
  static void load(Path root) throws BackendException
  {
    root = root.resolve(LOCATION);
    if(!Files.isDirectory(root))
      throw new BackendException("Directory must be present: " + root);

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
          Log.error(js.toAbsolutePath() + " fail: " + e.getMessage());
        }
        Log.info("Load::" + js.toAbsolutePath());
      });
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }

  /***
   * Set the location of the framework library
   *
   * @param frameworkPath
   */
  public void setFrameworkPath(Path frameworkPath)
  {
    this.frameworkPath = frameworkPath;
  }

  /***
   * Set the location of the application
   *
   * @param applicationPath
   */
  public void setApplicationPath(Path applicationPath)
  {
    this.applicationPath = applicationPath;
  }

  public static Path getApplicationPath()
  {
    return backend.applicationPath;
  }

  public static Path getFrameworkPath()
  {
    return backend.frameworkPath;
  }
}

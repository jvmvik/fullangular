package io.milkyway;

import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Application boostrap
 */
public class Application
{
  public static void main(String[] args) throws FileNotFoundException, ScriptException, BackendException
  {
    Path current = Paths.get(".");
    Path framework = current.resolve("../../");
    Backend.start(current, framework);
  }
}

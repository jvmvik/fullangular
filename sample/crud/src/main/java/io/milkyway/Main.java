package io.milkyway;

import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

/**
 *
 */
public class Main
{
  public static void main(String[] args) throws FileNotFoundException, ScriptException
  {
      Backend.start(Paths.get("."), Paths.get("../../"));
  }
}

package io.milkyway;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Server
 *
 * @author victor@milkyway.io
 * @date 4/21/14
 */
public class BackendTest
{

  Path application = Paths.get("sample/crud");

  @Test
  public void reload() throws BackendException
  {
    Backend.start(application, Paths.get("."));

    Backend.reload();
  }
}

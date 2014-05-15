import io.milkyway.Backend;
import io.milkyway.BackendException;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Forecast controller
 *
 * @author victor@milkyway.io
 * @date 4/21/14
 */
public class Application
{
  public static void main(String[] args) throws BackendException
  {
    Path current = Paths.get(".");
    Path framework = current.resolve("../../");
    Backend.start(current, framework);
  }
}


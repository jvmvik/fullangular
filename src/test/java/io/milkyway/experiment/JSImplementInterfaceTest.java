package io.milkyway.experiment;

import io.milkyway.JsEval;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Try to implement a Java interface in JavaScript
 *
 * @author victor@milkyway.io
 * @date 5/14/14
 */
public class JSImplementInterfaceTest
{
  @Test
  public void test() throws ScriptException
  {

    String s =
        "var FCriteria = Java.type('io.milkyway.experiment.FCriteria');\n" +
        "var f = function(item){\n" +
        " return item == 1;\n" +
        "};\n" +
        "var fc = new FCriteria(f);\n" +
        "\n" +
        "var a = fc.test(1);\n" +
        "var b = fc.test(0);\n";

    ScriptEngine se = JsEval.eval(s);
    assertTrue((boolean)se.get("a"));
    assertFalse((boolean) se.get("b"));
  }

}

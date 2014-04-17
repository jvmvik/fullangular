package io.milkyway;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.script.ScriptException;
import javax.script.ScriptEngine;

import static org.junit.Assert.*;
/**
 * Check how fast is nashorn JSON parser implementation.
 *
 * Measure resource usage for decoding / encoding
 *
 * What method to evaluate the performance ?
 * Which unit to choose ?
 */
public class JsonBenchTest
{
  @Before
  public void setUp()
  {
    JsEval.load();
  }

  @Test
  public void parse() throws ScriptException
  {
    //data = "{\\\"data\\\":0}";
    //TODO Write a regression form instead of that..
    int x = 1000;
    String code = "var json = JSON.parse(\"" + data(x) + "\");";

    long start = System.currentTimeMillis();
    JsEval.eval(code);
    long delta = System.currentTimeMillis() - start;

    System.out.println("Parse " + x + " objects in " + delta + "ms");
    assertTrue("Too slow to parse", delta < x * 0.2);
    // 20000 elements decoded in 271ms
    // 10000 elements decoded in 205ms
    // 1000 element decoded in 58ms
    // 50 elements decoded in 14ms
    // 10 elements decoded in 13ms
    //TOOD Check if java does better than this?

    // The performance are not that great for a single object.
    // Very large array cannot be decoded in a realistic amount of time.
  }

  @Test
  @Ignore
  public void gsonParse() throws ScriptException
  {
    String lib = "var json = {};\n" +
        "\n" +
        "// google gson mapper\n" +
        "var MAPPER = new com.google.gson.Gson();\n" +
        "\n" +
        "/**\n" +
        " * Converts object to a json string.\n" +
        " * @param object - the object to convert.\n" +
        " * @return {String} the resultant json.\n" +
        " */\n" +
        "json.toJson = function (object) {\n" +
        "    return MAPPER.toJson(object);\n" +
        "};\n" +
        "\n" +
        "\n" +
        "json.roundtripJson = function (object) {\n" +
        "    return JSON.parse(json.toJson(object));\n" +
        "};";
    JsEval.eval(lib);
    int x = 1;
    String code = "var r = json.roundtripJson(\"{\\\"id\\\":1}\");\n" +
        "print(r.id);";

    long start = System.currentTimeMillis();
    JsEval.eval(code);
    long delta = System.currentTimeMillis() - start;

    System.out.println("Parse " + x + " objects in " + delta + "ms");
    assertTrue("Too slow to parse", delta < x * 0.2);

  }

  @Test
  public void stringify() throws ScriptException
  {
    int x = 1;
    String code = "var json = JSON.parse(\"" + data(x) + "\");";
    ScriptEngine engine = JsEval.eval(code);

    assertNotNull(engine.get("json"));
    long start = System.currentTimeMillis();
    JsEval.eval("JSON.stringify(json);");
    long delta = System.currentTimeMillis() - start;
    System.out.println("stringify " + x + " objects in " + delta + "ms");
    assertTrue("Too slow to stringify", delta < 20);
  }


  String data(int size)
  {

    String data = "";
    for(int i = 0; i < size; i++)
    {
      if(data.length() > 0)
        data += ",";
      data += "{\\\"id\\\":"+i+", \\\"name\\\": \\\"object named: "+i+"\\\"}";
    }
    data = "{\\\"data\\\":[" + data + "]}";
    return data;
  }

}

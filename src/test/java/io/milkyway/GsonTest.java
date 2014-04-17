package io.milkyway;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.junit.Ignore;
import org.junit.Test;

import javax.script.ScriptException;

import static org.junit.Assert.fail;

/**
 * Experiment with GSON
 *
 *  - Gson convert JSON to Object in Java and JavaScript
 *  - Convert Object to JSON
 */
public class GsonTest
{
  Gson gson = new Gson();

  @Test
  public void test()
  {
    String s = gson.toJson(new Cat(2, "kitty", true));

    //System.out.println(s);

    Cat cat = gson.fromJson(s, Cat.class);
    //System.out.println(cat.toString());
    assert 2 == cat.age;
    assert "kitty".equals(cat.name);
    assert cat.male;

    String ss = "\""+s.replaceAll("\"","\\\\\"")+"\"";
    String code = "var Cat = Java.type(\"io.milkyway.Cat\");\n" +
        "var Gson = com.google.gson.Gson;\n" +
        "var gson = new Gson();\n" +
        "var cat = gson.fromJson("+ss+", Cat.class);\n" +
        "print(cat.toString());\n" +
        "var json = gson.toJson(cat);\n" +
        "print(json.toString());\n" +
        "var j = JSON.parse(json);" +
        "print(JSON.stringify(j));";
    try
    {
      JsEval.eval(code);
    }
    catch(ScriptException e)
    {
      fail(e.getCause().getMessage());
    }
  }
}


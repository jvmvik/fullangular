package io.milkyway;

/**
 * Simple Java Object
 */
public class Cat
{
  int age;
  String name;
  boolean male;

  public Cat(int age, String name, boolean male)
  {
    this.age = age;
    this.name = name;
    this.male = male;
  }

  public String getName()
  {
    return name;
  }

  @Override
  public String toString()
  {
    return "Cat{" +
        "age=" + age +
        ", name='" + name + '\'' +
        ", male=" + male +
        '}';
  }
}
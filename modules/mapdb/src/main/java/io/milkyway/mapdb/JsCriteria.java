package io.milkyway.mapdb;

import java.util.function.Function;

/**
 * Execute JS function to check the condition.
 *
 * @author victor@milkyway.io
 * @date 5/14/14
 */
public class JsCriteria implements Criteria<Object>
{
  final Function<Object, Boolean> function;

  public JsCriteria(Function<Object, Boolean> function)
  {
    this.function = function;
  }

  @Override
  public boolean test(Object item)
  {
    return function.apply(item);
  }
}

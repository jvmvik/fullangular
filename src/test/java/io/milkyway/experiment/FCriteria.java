package io.milkyway.experiment;

import java.util.function.Function;

/**
 * FCriteria
 *
 * @author victor@milkyway.io
 * @date 5/14/14
 */
public class FCriteria implements Criteria<Object>
{

  final Function function;

  public FCriteria(Function function)
  {
    this.function = function;
  }

  @Override
  public boolean test(Object item)
  {
    return (Boolean)function.apply(item);
  }
}


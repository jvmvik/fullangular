package io.milkyway.mapdb;

import java.util.Map;
import java.util.function.Function;

/**
 * Forecast controller
 *
 * @author victor@milkyway.io
 * @date 5/14/14
 */
public class JsMapDS extends MapDS
{

  /***
   *
   * @param collectionName
   * @param function
   * @return
   * @throws DSException
   */
  public MapDS removeFirst(String collectionName, Function<Object, Boolean> function) throws DSException
  {
    return removeFirst(collectionName, new JsCriteria(function));
  }

  public MapDS removeAll(String collectionName, Function<Object, Boolean> function, int offset, int length) throws DSException
  {
    return removeAll(collectionName, new JsCriteria(function), offset, length);
  }

  public Map<Long, Object> findAllBy(String collectionName, Function<Object, Boolean> function) throws DSException
  {
    return findAllBy(collectionName, new JsCriteria(function));
  }

  public Object findOne(String collectionName, Function<Object, Boolean> function)
      throws DSException
  {
    return findOne(collectionName, new JsCriteria(function));
  }

  //TODO Add support for sorting..
}

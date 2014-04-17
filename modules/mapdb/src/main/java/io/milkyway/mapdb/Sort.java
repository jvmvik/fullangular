package io.milkyway.mapdb;

/***
 * Sort interface
 *
 * @param <K> POJO class name
 * @param <T> return type class
 */
public interface Sort<K, T>
{
  /**
   * Select sorting parameters
   *
   * @param item element to sort
   * @return key used for sorting
   */
  public T key(K item);

}

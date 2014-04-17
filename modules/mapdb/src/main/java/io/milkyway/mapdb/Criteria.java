package io.milkyway.mapdb;

/***
 * Criteria
 *
 * Enable to filter
 *
 * @param <K> POJO class name
 */
public interface Criteria<K>
{
  /***
   * Test what item must be selected
   *
   * @param item available
   * @return true if item must be selected
   */
  public boolean test(K item);
}

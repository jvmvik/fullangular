package io.milkyway.mapdb;

import org.mapdb.Atomic;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentNavigableMap;

/**
 * DataStore
 * <p/>
 * <code>
 * MapDS ds = new MapDS();
 * ds.start(Paths.get("ds.ds"));
 * ds.insert("planets", new String[]{"earth","mars","moon"});
 * ds.save();
 * <p/>
 * assert 3 == ds.getCollection("planets").size()
 * </code>
 *
 * @author vicben01
 */
public class MapDS
{

  DB ds;
  Path dbPath;

  public void start(String path)
  {
    try
    {
      start(Paths.get(path));
    }
    catch(DSException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Start database connection
   *
   * @param path database file location
   * @throws DSException
   */
  public void start(Path path) throws DSException
  {
    if(ds != null && !ds.isClosed())
      throw new DSException("Connection is already started");

    this.dbPath = path;

    ds = DBMaker.newFileDB(dbPath.toFile())
        .closeOnJvmShutdown()
        .make();
  }

  /**
   * Stop the current database
   *
   * @throws DSException
   */
  public void stop() throws DSException
  {
    isConnected();
    ds.commit();
    ds.close();
  }

  /**
   * Delete database on disk
   *
   * @throws DSException
   */
  public void cleanUp() throws DSException
  {
    try
    {
      isConnected();
      stop();
    }
    catch(DSException ex)
    {
    }

    try
    {
      Files.deleteIfExists(dbPath);
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Retrieve or create a collection
   * <p/>
   * This operation is automatically applied to the datastore.
   *
   * @param collectionName name of the collection
   * @return collection
   * @throws DSException
   */
  public ConcurrentNavigableMap getCollection(String collectionName) throws DSException
  {
    isConnected();
    return ds.getTreeMap(collectionName);
  }

  /**
   * Delete a collection from the DB
   * <p/>
   * The datastore is automatically updated.
   *
   * @param collectionName name of the collection
   * @return true if collection has been deleted successfully
   * @throws DSException
   */
  public boolean delete(String collectionName) throws DSException
  {
    isConnected();
    if(!ds.exists(collectionName))
      return false;

    ds.delete(collectionName);
    return true;
  }

  /**
   * Delete a set of collection
   *
   * @param collectionNames
   * @throws DSException
   */
  public void delete(String... collectionNames) throws DSException
  {
    for(String collectionName : collectionNames)
      delete(collectionName);
  }

  /**
   * List of collections available
   *
   * @return names of the collection available
   */
  public Set<String> getCollections()
  {
    return ds.getAll().keySet();
  }

  /**
   * Save current changes on disk.
   * This flush the content of the buffer.
   *
   * @throws DSException
   */
  public void save() throws DSException
  {
    isConnected();
    ds.commit();
  }

  /**
   * Rollback all changes made since last saving.
   * This clean the content of the buffer.
   *
   * @throws DSException
   */
  public void rollback() throws DSException
  {
    isConnected();
    ds.rollback();
  }

  /**
   * Check if the database is connected...
   *
   * @throws DSException
   */
  private void isConnected() throws DSException
  {
    if(ds == null)
      throw new DSException("Database has not been started yet...");
    if(ds.isClosed())
      throw new DSException("Database connection is already stopped...");
  }

  /**
   * Insert one or more elements into a collection
   *
   * @param collectionName collection name
   * @param objects        element to insert
   * @throws DSException
   */
  public MapDS insert(String collectionName, Object... objects) throws DSException
  {
    Atomic.Long counter = ds.getAtomicLong("id");
    ConcurrentNavigableMap col = getCollection(collectionName);
    for(Object o : objects)
      col.put(counter.getAndIncrement(), o);
    return this;
  }

  /**
   * Update an element
   *
   * @param collectionName name for the collection
   * @param elements       elements to update
   * @return true when update is success otherwise false
   * @throws DSException
   */
  public void update(String collectionName, Object... elements) throws DSException
  {
    for(final Object element : elements)
    {
      update(collectionName, element);
    }
  }

  private void update(String collectionName, final Object element) throws DSException
  {
    Long key = findKey(collectionName, new Criteria<Serializable>()
    {
      @Override
      public boolean test(Serializable item)
      {
        return item.equals(element);
      }
    });

    if(key == -1L)
      throw new DSException("Entry is not found: " + element);

    getCollection(collectionName).put(key, element);
  }

  /***
   * Remove first element matching
   *
   * @param collectionName
   * @param criteria
   * @return
   * @throws DSException
   */
  public MapDS removeFirst(String collectionName, Criteria criteria) throws DSException
  {
    Map<Long, Object> col = findAllBy(collectionName, criteria, 0, 1);
    return remove(collectionName, col.keySet().iterator().next());
  }

  /***
   * Remove an item by index
   *
   * @param collectionName
   * @param index
   * @return
   */
  public MapDS remove(String collectionName, Long index)
  {
    ConcurrentNavigableMap<Long, Object> co = ds.getTreeMap(collectionName);
    co.remove(index);
    return this;
  }

  /***
   * Remove all item in the collection that matches the criteria
   *
   * @param collectionName name of the collection
   * @param criteria
   * @param offset
   * @param length
   * @return
   * @throws DSException
   */
  public MapDS removeAll(String collectionName, Criteria criteria, int offset, int length) throws DSException
  {
    ConcurrentNavigableMap<Long, Object> co = ds.getTreeMap(collectionName);
    Map<Long, Object> col = findAllBy(collectionName, criteria, offset, length);
    Iterator<Long> it = col.keySet().iterator();
    while(it.hasNext())
    {
      co.remove(it.next());
    }
    return this;
  }

  /**
   * Check if a collection is already been created.
   *
   * @param collectionName collection identifier
   * @return
   */
  public boolean exist(String collectionName)
  {
    return ds.exists(collectionName);
  }

  /**
   * Find all
   *
   * @param collectionName collection identifier
   * @return
   */
  public Map<Long, Object> findAll(String collectionName) throws DSException
  {
    return findAllBy(collectionName, null, -1, Integer.MAX_VALUE);
  }

  /**
   * Return the collection of object without keys
   *
   * @param collectionName
   * @return
   * @throws DSException
   */
  public Collection<Object> findAllAsList(String collectionName) throws DSException
  {
    Map<Long, Object> list = findAllBy(collectionName, null, -1, Integer.MAX_VALUE);
    return list.values();
  }

  /**
   * Find all by 'criteria'
   *
   * @param collectionName collection identifier
   * @param criteria       search criteria
   * @return
   */
  public Map<Long, Object> findAllBy(String collectionName, Criteria<?> criteria) throws DSException
  {
    return findAllBy(collectionName, criteria, -1, Integer.MAX_VALUE);
  }

  /**
   * Find one element that match the criteria.
   *
   * @param collectionName name of the collection
   * @param criteria       search criteria
   * @return first element matching the criteria, or null if no match found
   */
  public Object findOne(String collectionName, Criteria<?> criteria)
      throws DSException
  {
    Map<Long, Object> r = findAllBy(collectionName, criteria);
    Iterator<Object> it = r.values().iterator();
    if(it.hasNext())
      return it.next();
    return null;
  }

  /**
   * Find all item in a range between offset and limit
   *
   * @param collectionName name of the collection
   * @param offset         skip number of elements
   * @param limit          number of element to return
   * @return result
   * @throws DSException
   */
  public Map<Long, Object> findAll(String collectionName, int offset, int limit) throws DSException
  {
    return findAllBy(collectionName, null, offset, limit);
  }

  /**
   * Find all by 'collection' and 'criteria'
   *
   * @param collectionName collection identifier
   * @param criteria       search criteria
   * @param offset         skip number of elements
   * @param limit          number of element to return
   * @return result
   */
  public Map<Long, Object> findAllBy(String collectionName, Criteria criteria, int offset, int limit) throws DSException
  {
    ConcurrentNavigableMap<Long, Object> co = ds.getTreeMap(collectionName);
    if(co == null)
      throw new DSException("Collection is not found: " + collectionName);

    Map<Long, Object> r = new HashMap<>();
    Object ob;
    int i = 0;
    int j = 0;
    for(Long key : co.keySet())
    {
      ob = co.get(key);
      if(criteria == null
          || (criteria != null && criteria.test(ob)))
      {
        if(i >= offset)
        {
          if(j >= limit)
          {
            return r;
          }
          else
          {
            r.put(key, ob);
            j++;
          }
        }
        else
        {
          i++;
        }
      }
    }
    return r;
  }

  /**
   * Find all by order
   *
   * @param collectionName name of the collection
   * @param sort           method to sort
   * @return
   */
  public Map<Long, Object> findAllByOrder(String collectionName, Sort<?, ?> sort) throws DSException
  {
    return findAllByOrder(collectionName, null, sort, false);
  }

  /**
   * Find all by order
   *
   * @param collectionName
   * @param sort
   * @param descending
   * @return
   */
  public Map<Long, Object> findAllByOrder(String collectionName, Sort<?, ?> sort, boolean descending) throws DSException
  {
    return findAllByOrder(collectionName, null, sort, descending);
  }

  /**
   * Find all by order
   *
   * @param collectionName
   * @param criteria
   * @param sort
   * @param descending
   * @return
   */
  public Map<Long, Object> findAllByOrder(String collectionName, Criteria<?> criteria, Sort sort, boolean descending) throws DSException
  {
    Map<Long, Object> map = findAllBy(collectionName, criteria);
    SortedMap<Object, Long> sortedMap = new TreeMap<>();

    if(descending)
      sortedMap = ((TreeMap) sortedMap).descendingMap();

    Object object;
    for(Long k : map.keySet())
    {
      object = map.get(k);
      sortedMap.put(sort.key(object), k);
    }

    Map<Long, Object> r = new LinkedHashMap<>();
    for(Long v : sortedMap.values())
    {
      r.put(v, map.get(v));
    }
    return r;
  }

  /**
   * Count element in the collection
   *
   * @param collectionName name of the collection
   * @param criteria       filter criteria
   * @return number of matches in this collection
   */
  public int countBy(String collectionName, Criteria criteria) throws DSException
  {
    Map<Long, Object> result = findAllBy(collectionName, criteria);
    if(result == null)
      return 0;
    return result.size();
  }

  /**
   * Count all elements in the collection
   *
   * @param collectionName name of the collection
   * @return number of elements for this collection
   * @throws DSException
   */
  public int count(String collectionName) throws DSException
  {
    Map<Long, Object> result = findAll(collectionName);
    if(result == null)
      return 0;
    return result.size();
  }

  /**
   * Garbage collection the Database
   */
  public MapDS gc()
  {
    try
    {
      isConnected();
      ds.commit();
      ds.compact();
    }
    catch(DSException ex)
    {
      System.out.println(ex.getMessage());
    }
    return this;
  }

  /**
   * Find a key for associated to an element in the collection.
   *
   * @param collectionName
   * @param earth
   * @return -1L if not found
   */
  public Long findKey(String collectionName, Criteria<?> earth) throws DSException
  {
    Map<Long, Object> col = findAllBy(collectionName, earth, -1, Integer.MAX_VALUE);
    Iterator<Long> it = col.keySet().iterator();
    if(it.hasNext())
      return it.next();
    return -1L;
  }

  public boolean isRunning()
  {
    return ds == null || !ds.isClosed();
  }


}

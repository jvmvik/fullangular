package io.milkyway.mapdb;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mapdb.Utils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentNavigableMap;

import static org.junit.Assert.*;

/**
 * DataStore Test
 *
 * @author vicben01
 */
public class MapDSTest
{
  static long start;
  private MapDS mapDS;
  private File dbFile;

  @Before
  public void setUp() throws DSException
  {
    //Configure and open database using builder pattern.
    //All options are available with code auto-completion.
    dbFile = Utils.tempDbFile();
    mapDS = new MapDS();
    mapDS.start(dbFile.toPath());
  }

  @After
  public void tearDown() throws DSException, IOException
  {
    mapDS.stop();
    Files.delete(dbFile.toPath());
  }

  @Test
  public void all() throws DSException, IOException
  {
    start = System.currentTimeMillis();
    t("init");

    //open an collection, TreeMap has better performance then HashMap
    ConcurrentNavigableMap<Integer,String> map = mapDS.getCollection("items");

    for(int i = 0; i < 1000; i++)
      map.put(i,"item"+i);

    t("add 1000 items");
    //map.keySet() is now [1,2] even before commit

    mapDS.save();  //persist changes into disk

    t("commit");

    for(int i = 0; i < 1000; i++)
      map.put(i,"item"+i);

    t("add");
    //map.keySet() is now [1,2,3]
    mapDS.rollback(); //revert recent changes

    t("rollback");
    //map.keySet() is now [1,2]

    // close connection
    mapDS.stop();
    t("close");
    // Reload DB
    mapDS.start(dbFile.toPath());

    ConcurrentNavigableMap<Integer,String> m = mapDS.getCollection("items");
    t("read");
  }

  @Test
  public void delete() throws DSException
  {
    mapDS.getCollection("hello");
    assertTrue(mapDS.exist("hello"));

    mapDS.delete("hello");
    assertFalse(mapDS.exist("hello"));
  }

  @Test
  public void getCollections() throws DSException
  {
    mapDS.getCollection("hello");
    mapDS.getCollection("world");

    assertTrue(mapDS.exist("hello"));
    assertTrue(mapDS.exist("world"));

    Set<String> names = mapDS.getCollections();
    assertEquals("hello", names.toArray()[0]);
    assertEquals("world", names.toArray()[1]);
  }

  @Test
  public void insert() throws DSException
  {
    Planet earth = new Planet("earth", 0);
    mapDS.insert("planets", earth);

    assertTrue(mapDS.exist("planets"));
    assertEquals(1, mapDS.getCollection("planets").size());
    System.out.println(mapDS.getCollection("planets").toString());
  }

  @Test
  public void insertMultiple() throws DSException
  {
    Planet earth = new Planet("earth", 0);
    Planet mars = new Planet("earth", 10000);
    mapDS.insert("planets", earth, mars);
    mapDS.save();

    assertTrue(mapDS.exist("planets"));
    assertEquals(2, mapDS.getCollection("planets").size());
    System.out.println(mapDS.getCollection("planets").toString());
  }

  @Test
  public void update() throws DSException
  {
    Planet earth = new Planet("earth", 0);
    mapDS.insert("planets", earth);
    mapDS.save();

    assertNotNull(mapDS.getCollection("planets").firstEntry());

    ConcurrentNavigableMap planets = mapDS.getCollection("planets");
    Planet o = (Planet)planets.get(planets.firstKey());
    o.distanceToEarth = 10000;

    mapDS.update("planets", o);

    ConcurrentNavigableMap p = mapDS.getCollection("planets");

    assertEquals(10000, ((Planet)p.get(p.firstKey())).distanceToEarth);
    System.out.println(mapDS.getCollection("planets").toString());
  }

  @Test
  public void findAll() throws DSException
  {
    Planet earth = new Planet("earth", 0);
    Planet moon  = new Planet("moon", 384400);
    Planet mars  = new Planet("mars", 225000000);

    mapDS.insert("planets", new Planet[]{earth, moon, mars});
    assertEquals(3, mapDS.getCollection("planets").size());
    mapDS.save();

    // Exclude earth from the list
    Criteria<Planet> c = new Criteria<Planet>()
    {
      @Override
      public boolean test(Planet planet)
      {
        if(planet.name.equals("earth"))
          return false;
        return true;
      }
    };

    Map<Long, Object> map1  = mapDS.findAllBy("planets", c);
    assertEquals(2, map1.size());
    assertFalse(map1.containsValue(earth));
  }

  @Test
  public void findAllLimit() throws DSException
  {
    Planet earth = new Planet("earth", 0);
    Planet moon  = new Planet("moon", 384400);
    Planet mars  = new Planet("mars", 225000000);

    mapDS.insert("planets", new Planet[]{earth, moon, mars})
        .save();

    Map<Long, Object> map1  = mapDS.findAll("planets", 0, 1);
    assertEquals(1, map1.size());
    assertEquals(earth, map1.get(0L));
  }

  @Test
  public void countBy() throws DSException
  {
    Planet earth = new Planet("earth", 0);
    Planet moon  = new Planet("moon", 384400);
    Planet mars  = new Planet("mars", 225000000);
    Planet mars2  = new Planet("mars", 225000000);

    mapDS.insert("planets", new Planet[]{earth, moon, mars, mars2});

    Criteria<Planet> c = new Criteria<Planet>()
    {
      @Override
      public boolean test(Planet item)
      {
        return item.name.equals("mars");
      }
    };
    assertEquals(2, mapDS.countBy("planets", c));

    Criteria<Planet> c2 = new Criteria<Planet>()
    {
      @Override
      public boolean test(Planet item)
      {
        return item.name.equals("pluton");
      }
    };
    assertEquals(0, mapDS.countBy("planets", c2));
  }

  @Test
  public void remove() throws DSException
  {
    Planet earth = new Planet("earth", 0);
    Planet moon  = new Planet("moon", 384400);
    mapDS.insert("planets", new Planet[]{earth, moon}).save();

    assertEquals(2, mapDS.count("planets"));

    mapDS.remove("planets", 0L);

    assertEquals(1, mapDS.count("planets"));
  }

  @Test
  public void removeAll() throws DSException
  {
    Planet earth = new Planet("earth", 0);
    Planet moon  = new Planet("moon", 384400);
    mapDS.insert("planets", new Planet[]{earth, moon}).save();

    assertEquals(2, mapDS.count("planets"));

    Criteria<Planet> c = new Criteria<Planet>()
    {
      @Override
      public boolean test(Planet item)
      {
        return true;
      }
    };
    mapDS.removeAll("planets", c, 0, 100);

    assertEquals(0, mapDS.count("planets"));
  }

  @Test
  public void removeFirst() throws DSException
  {
    Planet earth = new Planet("earth", 0);
    Planet earth2  = new Planet("earth", 1);
    Planet earth3  = new Planet("earth", 2);
    mapDS.insert("planets", new Planet[]{earth, earth2, earth3}).save();

    assertEquals(3, mapDS.count("planets"));

    Criteria<Planet> c = new Criteria<Planet>()
    {
      @Override
      public boolean test(Planet planet)
      {
        return planet.name.equals("earth");
      }
    };
    mapDS.removeFirst("planets", c);

    assertEquals(2, mapDS.count("planets"));

    mapDS.removeFirst("planets", c);

    assertEquals(1, mapDS.count("planets"));
  }

  @Test
  public void count() throws DSException
  {
    assertEquals(0, mapDS.count("planets"));

    Planet earth = new Planet("earth", 0);
    Planet moon  = new Planet("moon", 384400);

    mapDS.insert("planets", new Planet[]{earth, moon});

    assertEquals(2, mapDS.count("planets"));

    Planet mars  = new Planet("mars", 225000000);
    mapDS.insert("planets", mars);

    assertEquals(3, mapDS.count("planets"));
  }

  @Test
  public void findAndSortBy() throws DSException
  {
    Planet earth = new Planet("earth", 0);
    Planet moon  = new Planet("moon", 384400);
    Planet mars  = new Planet("mars", 225000000);

    mapDS.insert("planets", new Planet[]{mars, earth, moon});
    assertEquals(3, mapDS.getCollection("planets").size());
    mapDS.save();

    // Exclude earth from the list
    Sort<Planet, Long> sort = new Sort<Planet, Long>()
    {
      @Override
      public Long key(Planet planet)
      {
        return planet.distanceToEarth;
      }
    };

    Map<Long, Object> r  = mapDS.findAllByOrder("planets", sort);
    assertEquals(3, r.size());

    // Check order
    Object[] planets = r.values().toArray();
    assertEquals(earth, planets[0]);
    assertEquals(moon, planets[1]);
    assertEquals(mars, planets[2]);


    Map<Long, Object> result  = mapDS.findAllByOrder("planets", sort, true);
    assertEquals(3, r.size());

    // Check order
    planets = result.values().toArray();
    assertEquals(mars, planets[0]);
    assertEquals(moon, planets[1]);
    assertEquals(earth, planets[2]);
  }

  @Test
  public void findKey() throws DSException
  {
    // Init
    Planet earth = new Planet("earth", 0);
    Planet moon  = new Planet("moon", 384400);
    Planet mars  = new Planet("mars", 225000000);

    mapDS.insert("planets", new Planet[]{earth, mars, moon});

    // Find a key
    Long key = mapDS.findKey("planets", new Criteria<Planet>(){

      @Override
      public boolean test(Planet item)
      {
        return item.name.equals("earth");
      }
    });

    assertEquals(new Long(0L), key);

    mapDS.getCollection("planets").remove(key);
    assertEquals(2, mapDS.getCollection("planets").size());
  }

  @Test
  public void gc() throws DSException
  {
    mapDS.insert("planets", new Planet[]{new Planet("earth", 0L)});
    mapDS.gc();

    assertEquals(1, mapDS.getCollection("planets").size());
  }

  /***
   * Planet
   */
  static class Planet implements Serializable
  {
    // planet name in english
    final String name;

    //miles
    long distanceToEarth;

    Planet(String name, long distanceToEarth)
    {
      this.name = name;
      this.distanceToEarth = distanceToEarth;
    }

    @Override
    public boolean equals(Object o)
    {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Planet planet = (Planet) o;

      if (distanceToEarth != planet.distanceToEarth) return false;
      if (name != null ? !name.equals(planet.name) : planet.name != null) return false;

      return true;
    }

    @Override
    public int hashCode()
    {
      int result = name != null ? name.hashCode() : 0;
      return result;
    }
  }

  // Test findOne
  @Test
  public void findOne() throws DSException
  {
    Planet earth = new Planet("earth", 0);
    Planet mars  = new Planet("mars", 10);
    Planet jupiter  = new Planet("moon", 10); // wrong distance for test only

    mapDS.insert("planets", earth, mars, jupiter);

    // Find the first planet where the distance matches
    Planet planet = (Planet) mapDS.findOne("planets", new Criteria<Planet>(){

      @Override
      public boolean test(Planet item)
      {
        return item.distanceToEarth == 10;
      }
    });

    assertTrue(mars.equals(planet));
  }

  // Clean up
  @Test
  public void CleanUp() throws DSException
  {
    Planet earth = new Planet("earth", 0);
    Planet mars  = new Planet("mars", 10);
    Planet jupiter  = new Planet("moon", 10); // wrong distance for test only

    mapDS.insert("planets", earth, mars, jupiter);
    mapDS.save();
    assertEquals(3, mapDS.getCollection("planets").size());

    assertTrue(mapDS.isRunning());

    mapDS.cleanUp();

    assertFalse(mapDS.isRunning());

    // Restart database
    mapDS.start(dbFile.toPath());
    assertEquals(0, mapDS.getCollection("planets").size());
  }

  void t(String name)
  {
    System.out.println(":" + name + " in " + (System.currentTimeMillis() - start) + "ms.");
    start = System.currentTimeMillis();
  }
}

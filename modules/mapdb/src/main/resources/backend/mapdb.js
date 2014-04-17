// DataStore build with MapDB


// Import Java class
var MapDB = Java.type("io.milkyway.MapDB");

/* NO need of a wrapper class
var ds = {
    mapdb: new MapDB();
    start: function(port)
    {
       mapdb.start(port);
    },
    handler: function(url, callback)
    {
       server = new Server
       server.addHandler(url, callback);
    },
    ds: mapdb.ds
}
*/
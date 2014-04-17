/***
 * Example of Application...
 */

// Load MapDB module
var MapDS = Java.type("io.milkyway.mapdb.MapDS");

// Load model object
var Account = Java.type("io.milkyway.model.Account");

// Add handler
milkyway.handler('/list', function(ctx)
{
    print("Execute uri: /list");
    var accounts = ds.findAllAsList("Account");
    ctx.ok(gson.toJson(accounts));
});

// Add resource
milkyway.resource("/Users/victor/workspace/milkyway/fullangular/sample/crud/src/main/resources/frontend")

// Add wire

// BootStrap
var account1 = new Account("account 1");
var account2 = new Account("account 2");

var ds = new MapDS();
ds.start("temp.ds");
ds.delete("Account");
ds.insert("Account", account1);
ds.insert("Account", account2);
ds.save();

print("Populate account: " + ds.count("Account"));

// Start server on port 8080
milkyway.start(8080);



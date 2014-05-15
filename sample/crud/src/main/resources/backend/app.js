/***
 * Example of Application...
 */

// Load MapDB module
var MapDS = Java.type("io.milkyway.mapdb.JsMapDS");

// Load model object
var Account = Java.type("io.milkyway.model.Account");


var UUID = Java.type("java.util.UUID");

server.get('/account', function(ctx){
   var accounts = ds.findAllAsList("Account");
   ctx.ok(gson.toJson(accounts));
});

server.post('/account', function(ctx)
{
 try
 {
    // Validate and create
    var account = Account.make(gson, ctx.json);
    ds.save("Account", account);
 }
 catch(ex)
 {
    ctx.fail(json.toJson(ex.errors));
 }
 ctx.ok();
});

server.get('/account/select/:uuid', function(ctx)
{
   var uuid = UUID.fromString(ctx.params.uuid);
   var account = ds.findOne("Account", function(account){
    return account.uuid.compareTo(uuid) == 0;
   });
   if(!account)
    ctx.fail("No account found for UUID: " + ctx.params.uuid);
   else
    ctx.ok(gson.toJson(account));
});

// Add resource
var location = "src/main/resources/frontend";
server.resource(location);

// Set port number
server.port(8080);

// The server automatically start..

// TODO Attache a wire between milkyway and netty web socket

// BootStrap
var google = new Account("Google");
google.description = "Personal account: Google+, Gmail...";
google.category = "perso";
google.url = "http://google.com";
google.login = "victor";
google.password = "hello";

var twitter = new Account("Twitter");
twitter.description = "Developer life and notes..";
twitter.category = "developer";
twitter.url = "http://twitter.com";
twitter.login = "jvmvik";
twitter.password = "";

var netflix = new Account("Netflix");
netflix.description = "Watch movie ..";
netflix.category = "perso";
netflix.url = "http://netflix.com";
netflix.login = "victor";
netflix.password = "";


var ds = new MapDS();
ds.start("temp.ds");
ds.delete("Account");
ds.insert("Account", google, twitter, netflix);
ds.save();

print("Populate account: " + ds.count("Account"));




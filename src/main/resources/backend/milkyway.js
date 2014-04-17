/**
 * Implement netty in javascript
 * and provide a interface similar to netty.
 *
 * http://www.phpied.com/3-ways-to-define-a-javascript-class/
 */

// Import Java class
var Server = Java.type("io.milkyway.Server");
var server = new Server();

// Milkyway class equivalent to angularjs for the front end
var milkyway = {
    start: function(port)
    {
       server.start(port);
    },
    handler: function(url, callback)
    {
       server.rest(url, callback);
    },
    resource: function(path)
    {
        server.resource(path);
    },
    wire: function(socket, callback)
    {
        server.wire(socket, callback);
    }
}

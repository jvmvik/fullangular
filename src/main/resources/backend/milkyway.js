/**
 * Implement netty in javascript
 * and provide a interface similar to netty.
 *
 * http://www.phpied.com/3-ways-to-define-a-javascript-class/
 */

// Import Java class
var Server = Java.type("io.milkyway.Server");
var server = new Server();

// Module
var Module = {

    /*
     * Controller
     *
     * app.controller('MyController', ['$service', '$handler', 'ctx', function($service, $handler, ctx){}])
     */
    controller: function(name, args)
    {

    },

    /*
     * Service
     *
     * app.service('MyService', ['$service', '$handler', 'ctx', function($service, $handler, ctx){}])
     */
     service: function(name, args)
     {

     }
}


/*
// Milkyway class equivalent to angularJS for the front end
var milkyway = {
    start: function(port)
    {
       server.start(port);
    },
    get: function(url, callback)
    {
       server.handler(url, callback);
    },
    port: function(port)
    {
        server.port(port);
    },
    resource: function(path)
    {
        server.resource(path);
    },
    wire: function(socket, callback)
    {
        server.wire(socket, callback);
    },
    module: function(name, args)
    {
        //TODO Add name

        return
    }
}*/

var $handler = {
    set: function(url, callback)
    {
        server.handler(url, callback);
    }
}

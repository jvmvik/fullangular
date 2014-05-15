DRAFT
=====

Example of code on the backend.
===

How writing a request handler ?
---


How accessing a datastore ?
---

How writing a controller ?
---
var app = milkyway.module('application', ['']) // name, and list of plugins
app.controller('MyController', ['$service', '$handler', function(ctx)
{
 $handler.set('list', function(ctx)
 {
    return ctx.hello;
 });
}
])

Controller
{
action2(ctx)
{
 return
}
}

How writing a service ?
---

How wiring a web socket topic ?
---


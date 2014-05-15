
var app = angular.module("application", ['ngResource','ngRoute', 'controllers', 'services', 'milkyway']);

/**
 * Multiple views
 */
app.config(['$routeProvider', '$httpProvider',
    function($routeProvider, $httpProvider)
    {
     $routeProvider.when('/account', {
         templateUrl: 'list.html',
         controller: 'AccountController'
       }).
       when('/edit/:uuid', {
         templateUrl: 'editor.html',
         controller: 'AccountEditorController'
       }).
       otherwise({
          // redirectTo: '/vault'
          templateUrl: 'list.html',
          controller: 'AccountController'
       });

       // HTTP Provider
    $httpProvider.defaults.headers.patch = {
            'Content-Type': 'application/json;charset=utf-8'
       };

   console.log("Application is configured..");
}]);

app.factory('$account', function ($resource)
{
    return $resource('account', {}, {
        list:   { method: 'GET', isArray: true },
        get:    { url:'account/select/:uuid', method: 'GET'},
        save:   { method: 'POST' },
        remove: { method: 'DELETE'}
    })
});

console.log("Application loaded...");



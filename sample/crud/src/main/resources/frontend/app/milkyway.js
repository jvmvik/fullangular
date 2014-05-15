/**
 * Add some extra features to angular
 * like: Milkyway services
 */

var m = angular.module('milkyway',[])
m.factory('$milkyway',['$window','$timeout', '$http', function($window, $timeout, $http)
{
return {
 reload: function()
 {
    var callback = function()
    {
        console.log("Reload current page...");
        $window.location.reload(true);
    };

    $http.get("/do/reload");
    $timeout(callback, 1000);
 }
};
}]);




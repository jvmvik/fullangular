
//Application controller
var c = angular.module('controllers',[]);

c.controller('ApplicationController', ['$scope', '$http', '$milkyway',
function($scope, $http, $milkyway)
{
  $scope.reload = function()
  {
    $milkyway.reload();
  };

}]);

c.controller('AccountController', ['$scope', '$http', '$account',
function($scope, $http, $account)
{
    $scope.accounts = [];

    $account.list(function(data)
    {
        $scope.accounts = data;
    },
    function(data, status)
    {
        alert("Error: " + status);
    });
}]);

c.controller('AccountEditorController', ['$scope', '$routeParams', '$account',
function($scope, $routeParams, $account)
{
    $scope.uuid = $routeParams.uuid;
    $scope.account = {};

    $scope.passwordCheck = "";

    $account.get(
        {uuid:$scope.uuid},
        function(data)
        {
            $scope.account = data;
            $scope.passwordCheck = data.password;
        },
        function(status, data)
        {
            alert(data);
        });
}]);
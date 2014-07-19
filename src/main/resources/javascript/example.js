
var MongoDBService = require('mongodb-js/mongo_dbservice.js');

console.log("Starting");

vertx.deployVerticle("java:io.vertx.mongodb.MongoDBServiceVerticle", {}, function(deploymentID, err) {
  console.log("deployment id is " + deploymentID);
  var mongoDBService = MongoDBService.create(vertx, {});
  mongoDBService.find("foo", {}, function(res, err2) {
    console.log("result is: " + JSON.stringify(res));
  });
});

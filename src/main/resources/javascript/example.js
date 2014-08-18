
var MongoDBService = require('mongodb-js/mongo_dbservice.js');

console.log("Starting");

// First lets deploy our MongoDB module
vertx.deployVerticle("java:io.vertx.mongodb.MongoDBServiceVerticle", function(deploymentID, err) {
  console.log("deployment id is " + deploymentID);


  // Now lets create a local proxy to it, which works by sending messages to and from the actual verticle over the
  // event bus, but you interact with it using a rich idiomatic API
  var mongoDBService = MongoDBService.createEventBusProxy(vertx, "vertx.mongodb");

  console.log("got mongodb service proxy");

  // Execute an operation on the service
  mongoDBService.findOne('myCollection', {foo: 'bar'}, {}, function(res, err2) {
    console.log("result is: " + JSON.stringify(res));
  });
});

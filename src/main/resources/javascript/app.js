/*
We use this script to start up all the verticles in this application.
This is quite a common pattern.
 */

console.log("Starting example application");

var config = vertx.getOrCreateContext().config();
console.log("Config: " + config);

// Start our stock ticker
startVerticle("groovy:io.vertx.example.StockTicker");

// Start out MongoService verticle and when that's started, start our web server verticle
startVerticle("service:io.vertx:mongo-service", config.mongoServiceConfig, function() {
  startVerticle("java:io.vertx.example.WebServer", null, function() {
    moduleStarted(true);
  });
});

// Little helper function to start verticle and handle failures
function startVerticle(verticleName, config, onComplete) {
  vertx.deployVerticle(verticleName, config ? {config: config} : {}, function(deploymentID, err) {
    if (err != null) {
      console.error("Failed to start " + verticleName + ":" + err);
      err.printStackTrace();
    } else {
      console.log(verticleName + " started ok!");
      if (onComplete) onComplete();
    }
  })
}

moduleStarted(false);


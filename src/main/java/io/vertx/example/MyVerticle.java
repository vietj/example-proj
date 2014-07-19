package io.vertx.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.mongodb.MongoDBService;
import io.vertx.mongodb.MongoDBServiceVerticle;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class MyVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {

    // Deploy a mongo persistor

    vertx.deployVerticle("java:" + MongoDBServiceVerticle.class.getName(), new DeploymentOptions(), ar -> {
      if (ar.succeeded()) {
        System.out.println("Succeeded in deploying mongo verticle");

        MongoDBService service = vertx.eventBus().createProxy(MongoDBService.class, "vertx.mongodb");

        service.find("foo", new JsonObject(), ar2 -> {
          System.out.println("Got result: " + ar2.result());
        });

      } else {
        ar.cause().printStackTrace();
      }
    });
  }
}

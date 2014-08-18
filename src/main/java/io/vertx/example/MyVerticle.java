package io.vertx.example;

import io.vertx.core.AbstractVerticle;
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

    vertx.deployVerticle("java:" + MongoDBServiceVerticle.class.getName(), ar -> {
      if (ar.succeeded()) {
        System.out.println("Succeeded in deploying mongo verticle");

        MongoDBService mongo = vertx.eventBus().createProxy(MongoDBService.class, "vertx.mongodb");
        JsonObject doc = new JsonObject().putString("foo", "bar").putNumber("age", 43);
        mongo.insert("mycollection", doc, "SAFE", ar2 -> {
          if (ar2.succeeded()) {
            System.out.println("succeeded");
            mongo.findOne("mycollection", new JsonObject().putString("foo", "bar"), null, ar3 -> {
              if (ar3.succeeded()) {
                JsonObject result = ar3.result();
                System.out.println("age is " + result.getInteger("age"));
              } else {
                ar3.cause().printStackTrace();
              }
            });
          } else {
            ar2.cause().printStackTrace();
          }
        });




      } else {
        ar.cause().printStackTrace();
      }
    });
  }
}

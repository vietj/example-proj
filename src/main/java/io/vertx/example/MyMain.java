package io.vertx.example;

import io.vertx.core.Vertx;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class MyMain {

  public static void main(String[] args) {
    Vertx vertx = Vertx.newVertx();

    //vertx.deployVerticle("java:" + MyVerticle.class.getName());
    vertx.deployVerticle("js:javascript/example.js");

    try {
      Thread.sleep(10000);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}

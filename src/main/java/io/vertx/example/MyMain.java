package io.vertx.example;

import io.vertx.core.Vertx;

/**
 *
 * This is a convenience class that's useful, if you want to run the app inside your IDE, for debugging, for example.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class MyMain {

  public static void main(String[] args) {
    Vertx.vertx().deployVerticle("js:javascript/app.js");
  }

}

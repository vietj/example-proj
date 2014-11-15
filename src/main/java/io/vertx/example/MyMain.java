package io.vertx.example;

import io.vertx.core.Vertx;

/**
 *
 * This is a convenience class that's useful, if you want to run the app inside your IDE, for debugging, for example.
 *
 * Although you can just as easily create a run configuration in your IDE, and specify
 * main class as io.vertx.core.Starter and arguments "run javascript/apps.js" and then you won't need this class at all
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class MyMain {

  public static void main(String[] args) {
    // We disable file caching - this means if you have the server running and update a file resource e.g. index.html
    // then rebuild your project the server will see the new index.html immediately
    System.setProperty("vertx.disableFileCaching", "true");
    Vertx.vertx().deployVerticle("javascript/app.js");
  }

}

package io.vertx.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoService;
import io.vertx.ext.routematcher.RouteMatcher;
import io.vertx.ext.sockjs.BridgeOptions;
import io.vertx.ext.sockjs.SockJSServer;
import io.vertx.ext.sockjs.SockJSServerOptions;

import static io.vertx.ext.routematcher.RouteMatcher.*;


/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class WebServer extends AbstractVerticle {

  private String webRoot;
  private MongoService mongoService;

  @Override
  public void start(Future<Void> startFuture) {

    JsonObject config = config();

    webRoot = config.getString("webroot", "web");

    // Create a proxy to an already deployed MongoService verticle so we can access it without having
    // to manually send messages over the event bus
    // We are going to use this to persist orders
    mongoService = MongoService.createEventBusProxy(vertx, "vertx.mongo");

    // Create the HTTP server
    HttpServer server = vertx.createHttpServer(new HttpServerOptions().setHost(config.getString("host", "localhost"))
                                                        .setPort(config.getInteger("port", 8080)));

    // Setup the routematcher - this is used to route requests on different paths to different handlers
    RouteMatcher rm = routeMatcher();

    // Requests to /api/* correspond to our (minimal) REST API
    rm.matchMethod(HttpMethod.POST, "/api/order", this::handleOrder);

    // We'll consider anything else to be a web request
    rm.noMatch(this::handleWebRequest);

    server.requestHandler(rm::accept);

    setupSockJSBridge(server);

    server.listen(res -> {
      // When the web server is listening we'll say that the start of this verticle is complete
      if (res.succeeded()) {
        startFuture.complete();
      } else {
        startFuture.fail(res.cause());
      }
    });

    System.out.println("Server is listening");
  }

  /*
  Handle an in-coming request to the path /web - this is used to serve standard web resources
  index.html and such-like
   */
  private void handleWebRequest(HttpServerRequest request) {
    if (request.path().contains("..")) {
      // Disallow urls crafted to see resources outside of the webroot - you may want to do some more sophisticated
      // checks here
      request.response().setStatusCode(403).end();
    } else if (request.path().equals("/")) {
      // Request for root, so send the index
      request.response().sendFile(webRoot + "/index.html");
    } else {
      // Send the requested resource
      request.response().sendFile(webRoot + request.path());
    }
  }

  /*
  Handle an order posted to the REST API
   */
  private void handleOrder(HttpServerRequest request) {
    System.out.println("In handler order");
    // When the entire request body is read call processOrder with it
    request.bodyHandler(buff -> processOrder(buff, request.response()));
  }

  private void processOrder(Buffer rawOrder, HttpServerResponse response) {
    System.out.println("Got order: " + rawOrder.toString());

    JsonObject order = new JsonObject(rawOrder.toString());

    System.out.println("Received an order!");
    System.out.println("Order is for stock symbol " + order.getString("symbol") + " quantity " +
                                                     order.getInteger("quantity") + " price " +
                                                     order.getDouble("price"));

    // Now persist it in Mongo
    mongoService.save("exampleCollection", order, result -> {
      if (result.succeeded()) {
        response.end(new JsonObject().put("ok", "order saved ok").encode());
      } else {
        System.err.println("Uh oh! Problem in saving order!: " + result.cause().getMessage());
        result.cause().printStackTrace();
        response.setStatusCode(500).end("Failed to process order");
      }
    });
  }

  private void setupSockJSBridge(HttpServer server) {
    // We start a SockJS bridge - this allows the event bus to be used in client side JavaScript!
    SockJSServer sockJSServer = SockJSServer.sockJSServer(vertx, server);

    // We setup the bridge to not allow any inbound messages on the event bus from the client and only outbound
    // messages from the address "example.stocks"
    sockJSServer.bridge(new SockJSServerOptions().setPrefix("/eventbus"),
      new BridgeOptions().addOutboundPermitted(new JsonObject().put("address", "example.stocks")));

  }

}

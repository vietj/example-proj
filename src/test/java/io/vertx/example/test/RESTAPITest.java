package io.vertx.example.test;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 *
 * A simple test of the REST API.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class RESTAPITest extends VertxTestBase {

  @Test
  public void testPlaceOrder() {

    vertx.deployVerticle("js:javascript/app.js", onSuccess(deploymentID -> {

      System.out.println("Creating client");

      HttpClient client = vertx.createHttpClient(new HttpClientOptions());

      HttpClientRequest req = client.request(HttpMethod.POST, 8080, "localhost", "/api/order", resp -> {
        System.out.println("Got response");
        assertEquals(200, resp.statusCode());
        testComplete();
      });
      req.setChunked(true);
      JsonObject order = new JsonObject().putString("symbol", "RHT").putNumber("quantity", 100).putNumber("price", 123.34);
      req.write(order.toString()).end();
      System.out.println("Wrote request");
    }));

    await();
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    CountDownLatch latch = new CountDownLatch(1);
    System.out.println("Starting MONGO!!");
    vertx.deployVerticle("java:io.vertx.ext.embeddedmongo.EmbeddedMongoVerticle", new DeploymentOptions().setWorker(true), res -> {
      if (res.failed()) {
        res.cause().printStackTrace();
      }
      System.out.println("Mongo Started");
      latch.countDown();
    });
    awaitLatch(latch);
  }
}

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

    JsonObject config = new JsonObject();
    // Change the port to 27018 to connect to the embedded Mongo
    JsonObject mongoServiceConfig =
      new JsonObject().putString("address", "example.mongoservice").putString("connection_string", "mongodb://localhost:27018");
    config.putObject("mongoServiceConfig", mongoServiceConfig);

    vertx.deployVerticle("js:javascript/app.js", new DeploymentOptions().setConfig(config), onSuccess(deploymentID -> {

      HttpClient client = vertx.createHttpClient(new HttpClientOptions());

      HttpClientRequest req = client.request(HttpMethod.POST, 8080, "localhost", "/api/order", resp -> {
        assertEquals(200, resp.statusCode());
        testComplete();
      });
      req.setChunked(true);
      JsonObject order = new JsonObject().putString("symbol", "RHT").putNumber("quantity", 100).putNumber("price", 123.34);
      req.write(order.toString()).end();
    }));

    await();
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    CountDownLatch latch = new CountDownLatch(1);
    vertx.deployVerticle("service:io.vertx:ext-mongo-embedded-db", onSuccess(res -> {
      System.out.println("Mongo Started");
      latch.countDown();
    }));
    awaitLatch(latch);
  }
}

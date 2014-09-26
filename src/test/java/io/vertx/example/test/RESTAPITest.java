package io.vertx.example.test;

import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;

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

      HttpClientRequest req = client.post(new RequestOptions().setHost("localhost").setPort(8080).setRequestURI("/api/order"), resp -> {
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
}

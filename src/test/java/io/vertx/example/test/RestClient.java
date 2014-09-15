package io.vertx.example.test;

import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;

import static io.vertx.core.http.HttpClientOptions.*;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class RestClient extends VertxTestBase {

  @Test
  public void testPlaceOrder() {
    HttpClient client = vertx.createHttpClient(options());

    HttpClientRequest req = client.post(RequestOptions.options().setHost("localhost").setPort(8080).setRequestURI("/api/order"), resp -> {
      System.out.println("Got response: " + resp.statusCode());
      testComplete();
    });
    req.setChunked(true);
    JsonObject order = new JsonObject().putString("symbol", "RHT").putNumber("quantity", 100).putNumber("price", 123.34);
    req.write(order.toString()).end();
    await();

  }
}

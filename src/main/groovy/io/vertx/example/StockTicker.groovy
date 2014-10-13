package io.vertx.example

import io.vertx.groovy.core.eventbus.EventBus
import io.vertx.lang.groovy.GroovyVerticle

class StockTicker extends GroovyVerticle {

  EventBus eb;

  def symbols = ['RHT', 'ORCL', 'VMW', 'MSFT', 'GOOGL']

  void start() {
    eb = vertx.eventBus()

    def rand = new Random();

    vertx.setPeriodic(1000, { id ->
      symbols.forEach { symb ->
        def price = 100 + rand.nextInt(50)
        eb.publish('example.stocks', [
          'symbol' : symb,
          'price' : price
        ])
        //println "published: $symb at $price"
      };

    })
  }


}
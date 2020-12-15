package wang.vertdemo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import sun.applet.Main;


/**
 * @author wangjunchao1
 * helloword demo
 */
public class HelloVertx extends AbstractVerticle {
//  @Override
//  public void start() {
//    vertx.createHttpServer()
//        .requestHandler(req -> req.response().end("Hello Vert.x!"))
//        .listen(8090);
//  }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new HelloVertx());
    }

  /**
   * @param fut
   */
  @Override
  public void start(Future<Void> fut) {
    vertx
            .createHttpServer()
            .requestHandler( r -> {
              r.response().end("<h1>Hello from my first Vert.x 3 application</h1>");
            })
            .listen(8080);
  }

}

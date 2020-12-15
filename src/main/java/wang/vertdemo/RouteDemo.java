package wang.vertdemo;

import io.vertx.core.*;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

/**
 * @author wangjunchao1
 * @package wang.vertwebdemo
 * @date 2020/12/8 17:45
 * web基本概念：路由 Router 是 Vert.x Web 的核心概念之一。它是一个维护了零或多个 Route 的对象。
 * 　　Router 接收 HTTP 请求，并查找首个匹配该请求的 Route，然后将请求传递给这个 Route。
 * 　　Route 可以持有一个与之关联的处理器用于接收请求。您可以通过这个处理器对请求做一些事情，然后结束响应或者把请求传递给下一个匹配的处理器。
 * 我们创建了一个 HTTP 服务器，然后创建了一个 Router。在这之后，我们创建了一个没有匹配条件的 Route，这个 route 会匹配所有到达这个服务器的请求。
 * 之后，我们为这个 route 指定了一个处理器，所有的请求都会调用这个处理器处理。
 * 调用处理器的参数是一个 RoutingContext 对象。它不仅包含了 Vert.x 中标准的 HttpServerRequest 和HttpServerResponse，还包含了各种用于简化 Vert.x Web 使用的东西。
 * 每一个被路由的请求对应一个唯一的 RoutingContext，这个实例会被传递到所有处理这个请求的处理器上。
 * 　　以下是一个简单的路由示例：
 */
public class  RouteDemo extends AbstractVerticle {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(40));
        vertx.deployVerticle(RouteDemo.class.getName());
        System.out.println("demo1 start " + vertx.toString());
    }

    /**
     * @throws Exception 在start或其他方法中通过调用vertx.close()方法，关闭当前vertx
     *                   关闭后调用该方法
     */
    @Override
    public void stop() throws Exception {
        System.out.println("Verticle Stop");
    }

    /**
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
        HttpServer httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.get("/hello").handler(routingContext -> {
            System.out.println("Verticle start");
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/plain");
            response.end("hello demo1");
        });
        // 当 Vert.x Web 决定路由一个请求到匹配的 route 上，它会使用一个 RoutingContext 调用对应处理器。
        //    　　如果您不在处理器里结束这个响应，您需要调用 next 方法让其他匹配的 Route 来处理请求（如果有）。
        //    　　您不需要在处理器执行完毕时调用 next 方法。您可以在之后您需要的时间点调用它：
        Route route1 = router.route(HttpMethod.GET, "/some/path").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            // 由于我们会在不同的处理器里写入响应，因此需要启用分块传输
            // 仅当需要通过多个处理器输出响应时才需要
            response.setChunked(true);
            response.write("route1\n");
            // 5 秒后调用下一个处理器
            routingContext.vertx().setTimer(5000, tid -> routingContext.next());
        });

        Route route2 = router.route("/some/path").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.write("route2\n");
            routingContext.vertx().setTimer(5000, tid -> routingContext.next());
        });

        Route route3 = router.route("/some/path").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.write("route3\n");
            routingContext.response().end();
        });
//        Route route4 = router.get("/wang/demo1").handler(routingContext ->{
//            Adder adder = new Adder();
//            HttpServerResponse response = routingContext.response();
//            response.end(adder.add());
//        });
        httpServer.requestHandler(router::accept).listen(8080);
    }
}


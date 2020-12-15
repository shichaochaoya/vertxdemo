package wang.vertdemo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

/**
 * @author wangjunchao1
 * @package wang.vertdemo
 * @date 2020/12/9 11:28
 */
public class BodyHandler {
    //    private Vertx vertx;
    /**
     * 实例化对象
     */
    private static BodyHandler bodyHandler = new BodyHandler();
    public Logger logger = LoggerFactory.getLogger(BodyHandler.class);

    public static BodyHandler bodyHandler() {
        return bodyHandler;
    }

    public static void getStr(RoutingContext rc) {
        rc.response().end("Hello world! 我!");
    }

    public static void getStr_1(RoutingContext rc) {
        HttpServerResponse response = rc.response();
        // 由于我们会在不同的处理器里写入响应，因此需要启用分块传输,仅当需要通过多个处理器输出响应时才需要
        response.setChunked(true);
        response.write("我");
        rc.next();
        // 5 秒后调用下一个处理器
//        rc.vertx().setTimer(5000, tid ->  rc.next());
    }

    public static void getStr_2(RoutingContext rc) {
        HttpServerResponse response = rc.response();
        response.write("和");
        rc.next();
    }

    public static void getStr_3(RoutingContext rc) {
        HttpServerResponse response = rc.response();
        response.write("你");
        rc.response().end();
    }

    public void getStr_4(RoutingContext rc) {
        ObjectMapper om = new ObjectMapper();
        if ("1".equals("1")) {
            getOrder("1", res -> {
                if (res.succeeded()) {
                    try {
                        rc.response().putHeader("Content-type", "application/json; charset=UTF-8");
                        rc.response().end(om.writeValueAsString(res.result()));
                        System.out.println("我是第四个打印!");
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                } else {
                    rc.response().setStatusCode(500)
                            .putHeader("Content-type", "application/json; charset=UTF-8")
                            .end((Buffer) new JsonObject().put("status", 0).put("des", "fali"));
                }
            });
            System.out.println("我是第一个打印!");
        }
    }


    private void getOrder(String orderId, Handler<AsyncResult<String>> handler) {
        Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(40));
        vertx.<String>executeBlocking(future -> {
            try {
                Thread.sleep(1);
                System.out.println("我是第二个打印!");
                future.complete("成功！");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, false, res -> {
            if (res.succeeded()) {
                System.out.println("我是第三个打印!");
                handler.handle(Future.succeededFuture(res.result()));
            } else {
                handler.handle(Future.failedFuture(res.cause()));
            }
        });
    }

}

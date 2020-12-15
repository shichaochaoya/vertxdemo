package wang.vertdemo;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.client.WebClient;

import java.lang.management.BufferPoolMXBean;
import java.util.concurrent.TimeUnit;

/**
 * @author wangjunchao1
 * @package wang.vertdemo
 * @date 2020/12/14 17:07
 * Vert客户端向百度发送请求
 * 使用HttpClient，HTTPClient这个接口也可以发送HTTP请求，
 * 只是它比较低级，如果在请求中带复杂数据，附带请求头，封装响应结果都需要自己来处理。
 * 因此，Vert.x提供了vertx-web-client的支持。
 */
public class MyHttpClient {
    public static void main(String[] args) throws InterruptedException {
        Vertx vertx = Vertx.vertx();
        HttpClient client = vertx.createHttpClient();
        Handler<Buffer> bodyHandler = buffer -> System.out.println(buffer.toString());

        /*向百度发送请求并返回状态码*/
        client.getNow("www.baidu.com", "/", resp -> System.out.println(resp.statusCode()));
        TimeUnit.SECONDS.sleep(10);


        /*向百度发送请求并将相应封装进处理器对象并打印出来*/
        client.post("www.baidu.com", resp -> resp.bodyHandler(bodyHandler));


        client.post("www.baidu.com", resp -> resp.bodyHandler(bodyHandler)) // (0)
                .setTimeout(1000)
                .exceptionHandler(Throwable::printStackTrace)
                .putHeader("My-Header", "HelloWorld") // (1)
                .setChunked(true) // (2)
                .write("Hi~") // (3)
                .end();
    }
}

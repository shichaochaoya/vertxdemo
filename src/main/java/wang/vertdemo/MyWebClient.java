package wang.vertdemo;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;

/**
 * @author wangjunchao1
 * @package wang.vertdemo
 * @date 2020/12/15 11:02
 * WebClient的API非常简单，
 */
public class MyWebClient {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        WebClient webClient = WebClient.create(vertx);

        webClient.post(8080,"localhost","/hang/add").send(handler -> {
            if (handler.succeeded()) {
                System.out.println(handler.result().bodyAsString());
            }
        });

//         * 发送GET请求，可以使用getAbs。
// * Abs是绝对地址的意思，除了使用绝对地址，还可以使用域名+端口号+请求地址的方式，代码如下：
        webClient.getAbs("http://localhost:8080/hang/some").send(handler -> {
            if (handler.succeeded()) {
                System.out.println(handler.result().bodyAsString());
            }
        });

//        HTTP协议包含三部分，请求行、请求头和请求体。一般来讲，不建议在GET请求的请求体中带数据，
//        但请求头数据是每个请求都有的，比如使用User-Agent指定设备的类型以及操作系统等等。
//        我们在请求远程服务的时候，如何设置请求头呢，非常简单，代码如下

        webClient.getAbs("http://localhost:8080/hang/some")
                .putHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:65.0) Gecko/20100101 Firefox/65.0")
                .send(handle -> {
                    // 处理响应的结果
                    if (handle.succeeded()) {
                        // 这里拿到的结果就是一个HTML文本，直接打印出来
                        System.out.println(handle.result().bodyAsString());
                    }
                });

//        上面的代码是我要请求新浪的主页，可以看到，
//        只需要在调用send方法之前，调用ssl方法，并指定为true即可。
        webClient.getAbs("https://www.sina.com")
                .ssl(true)
                .send(handle -> {
                    // 处理响应的结果
                    if (handle.succeeded()) {
                        // 这里拿到的结果就是一个HTML文本，直接打印出来
                        System.out.println(handle.result().bodyAsString());
                    }
                });
    }
}

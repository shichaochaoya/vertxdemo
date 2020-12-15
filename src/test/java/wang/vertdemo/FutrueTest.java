package wang.vertdemo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.MessageDigest;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


/**
 * @author wangjunchao1
 * @package io.vertx.starter
 * @date 2020/12/7 10:04
 * 对Futrue的简单使用，包含初始化，future的完成，设置回调，trycomplete方法，map的使用，compose方法
 */
@RunWith(VertxUnitRunner.class)
public class FutrueTest extends AbstractVerticle {

    private Vertx vertx;
    private FileSystem fs;

    /**
     * @param context 启动前调用
     */
    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(FutrueTest.class.getName(), context.asyncAssertSuccess());
        System.out.println("FutrueTest start  " + vertx.toString());
    }

    /**
     * @param context 销毁前调用
     */
    @After
    public void after(TestContext context) {
        System.out.println("after  ...");
        vertx.close(context.asyncAssertSuccess());
    }

    /**
     * @param testContext 创建http客户端，使用getNow方法向指定主机和端口发送get请求，请求路径为localhost/8080/some/path/
     *                    通过contains()方法断言响应体中是否存在”route2“字段
     *                    如果存在则顺利运行，如果不存在则抛出断言错误
     */
    @Test
    public void testMainVerticle(TestContext testContext) {
        final Async async = testContext.async();
        vertx.createHttpClient().getNow(8080, "localhost", "/some/path/",
                response -> {
                    response.bodyHandler(body -> {
                        testContext.assertTrue(body.toString().contains("route2"));
                        async.complete();
                    });
                });
    }


    /**
     * @param testContext 通过result返回参数传递信息
     *                    asynchronousMethod()方法调用future，在等待一秒后完成并调用future中的handler方法
     *                    future的handler获取到返回的参数并打印。
     */
    @Test
    public void testsetHandler(TestContext testContext) {
        Async async = testContext.async();
        //Vert.x 中的 Future 可以用来协调多个异步操作的结果。
        // 它支持并发组合（并行执行多个异步调用）和顺序组合（依次执行异步调用）。
        Future<String> future = Future.future();
        future.setHandler(asyncResult -> {
            if (asyncResult.succeeded()) {
                System.out.println("result = " + asyncResult.result());
            } else {
                Throwable cause = asyncResult.cause();
                System.out.println("异常 ：" + cause);
            }
            async.complete();
        });
        //异步执行其他操作
        asynchronousMethod(future);
        System.out.println("handle设置完成");


    }

    private void asynchronousMethod(Future<String> future) {
        CompletableFuture.runAsync(() -> {
            System.out.println("asynchronousMethod....");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            future.complete("haha");
        });
    }

    /**
     * @param context 测试异步执行操作
     *                tryComplete方法顾名思义,就是尝试成功,
     *                如果回调还没有执行完,然后被tryComplete执行了,那就返回true,
     *                如果,回调已经执行完了,那就返回false.这个比较简单,感觉使用的也少;
     */
    @Test
    public void testTryComplete(TestContext context) {
        Async async = context.async();
        Future<String> future = Future.future();
        future.setHandler(r -> {
            if (r.succeeded()) {
                //成功执行
                String result = r.result();
                System.out.println("result is:" + result);
            } else {
                //失败执行
                Throwable cause = r.cause();
                System.out.println("异常:" + cause);
            }
            async.complete();
        });
        //异步执行其他操作,操作完成执行future.complete方法.
        asynchronousMethod(future);
        System.out.println("handler设置完成...");
        //如果不进行sleep,会先进入b这段代码中，返回的就是ccc
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean b = future.tryComplete("ccc");
        System.out.println(b);

    }


    /**
     * @param context 如果,我们使用map,尽量就不要设置给map设置handler了，避免重复，可以一起在map中进行处理
     *                因为我实在找不出理由,你想对返回结果进行转换还要进行处理,这样就会出现问题,如果,我们在执行map之前时间很长,那么两个handler都会执行了:
     *                map方法比较简单,就是返回新的数据类型, 举个例子,好比一开始返回的是haha,然后要返回haha + “aaa”;
     *                就是要对原先返回的进行处理一下在返回:可以是其他数据类型,
     */
    @Test
    public void testMap(TestContext context) {
        Async async = context.async();
        Future<String> future = Future.future();
        future.setHandler(r -> {
            if (r.succeeded()) {
                //成功执行
                String result = r.result();
                System.out.println("result is:" + result);
            } else {
                //失败执行
                Throwable cause = r.cause();
                System.out.println("异常:" + cause);
            }
        });
        //异步执行其他操作,操作完成执行future.complete方法.
        asynchronousMethod(future);
        System.out.println("handler设置完成...");
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*Future<Object> objectFuture = future.mapEmpty();

        System.out.println(objectFuture);*/

//        以下两种方法等效
//        Future<String> map = future.map(x -> {
//            System.out.println("map result :" + "fuction...return" + x);
//            async.complete();
//            return null ;
//        });
        Future<String> map = future.map(x -> {
            System.out.println(x);
            return "fuction...return" + x;
        });
        map.setHandler(x -> {
            System.out.println("map result :" + x.result());
            async.complete();
        });
    }


    /**
     * @param context
     * 传了两个参数,第一个是执行compose的handler,第二个参数是下一个future,然后将下一个future返回回去,写个demo:
     *有效的解决了回调地狱，采用链式回调
     */
    @Test
    public void testCompose(TestContext context) {
        Future<String> f1 = Future.future();
        Future<Integer> f2 = Future.future();

        f1.complete("f1's result");
//    回调地狱如下
//    f1.setHandler(x -> {
//      System.out.println("f1 handler:" + x);
//      f2.complete(123);
//    });
//    f2.setHandler(x -> {
//      System.out.println("f2 handler:" + x);
//    });

        //f1调用compose方法，完成链式回调
        // 参数一是一个handler，相当于f1需要进行的操作
        //将该操作的执行结果传入（next）中，相当于f2
        // 该compose方法返回的是f2，后面设置f2的handler
        f1.compose(r -> {
            System.out.println("f1 handler:" + r);
            f2.complete(123);
        }, f2).setHandler(r -> {
            System.out.println("f2 handler:" + r.result());
        });
    }

    @Test
    public void testCompose2(TestContext context) {
        Future<String> f1 = Future.future();
        f1.complete("f1's result");
        f1.compose(r -> {
            System.out.println("f1  hander :" + r);
            Future<String> f2 = Future.future();
            f2.complete("f2's result");
            //返回的f2,下一个componse的执行者
            return f2;
        }).compose(r -> {
            System.out.println("f2  hander :" + r);
            Future<String> f3 = Future.future();
            f3.complete("f3's result");
            //返回的f3,setHandler
            return f3;
        }).setHandler(r -> {
            System.out.println("f3  hander :" + r);
        });

    }

    @Test
    public void test2(TestContext context) {
        Async async = context.async();

        vertx.executeBlocking(future -> {
            // 调用一些需要耗费显著执行时间返回结果的阻塞式API
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            future.complete("complete...over...");
        }, res -> {
            System.out.println("The result is: " + res.result());
            async.complete();
        });

    }

    @Test

    public void test3() {
        Future future = Future.future();
        Future<JsonObject> fut1 = Future.future();
        Future<JsonObject> fut2 = Future.future();
        Future<JsonObject> fut3 = Future.future();
        Future<JsonObject> fut4 = Future.future();
        Future<JsonObject> fut5 = Future.future();
        Future<JsonObject> fut6 = Future.future();
        //阻塞代码以及在阻塞代码执行返回异步结果处理程序来完成的,开启一个线程异步执行
        vertx.executeBlocking(fut -> {
            //内容
            fut1.complete(new JsonObject().put("wang",1));
            fut.complete();
        }, false, null);
        vertx.executeBlocking(fut -> {
            //内容
            fut2.complete(new JsonObject().put("wang",2));
            fut.complete();
        }, false, null);
        vertx.executeBlocking(fut -> {
            //内容
            fut3.complete(new JsonObject().put("wang",3));
            fut.complete();
        }, false, null);
        vertx.executeBlocking(fut -> {
            //内容
            fut4.complete(new JsonObject().put("wang",4));
            fut.complete();
        }, false, null);
        vertx.executeBlocking(fut -> {
            //内容
            fut5.complete(new JsonObject().put("wang",5));
            fut.complete();
        }, false, null);
        vertx.executeBlocking(fut -> {
            //内容
            fut6.complete(new JsonObject().put("wang",6));
            fut.complete();
        }, false, null);
        //等所有的都结束后，合并在一起统一输出
        CompositeFuture.all(fut1, fut2, fut3, fut4, fut5, fut6).setHandler(ar -> {
            if (ar.succeeded()) {
                JsonObject r0 = ar.result().resultAt(0);
                JsonObject r1 = ar.result().resultAt(1);
                JsonObject r2 = ar.result().resultAt(2);
                JsonObject r3 = ar.result().resultAt(3);
                JsonObject r4 = ar.result().resultAt(4);
                JsonObject r5 = ar.result().resultAt(5);
                future.complete(r0.mergeIn(r1.mergeIn(r2.mergeIn(r3.mergeIn(r4.mergeIn(r5))))));
                System.out.println("successed"+future.result().toString());
            } else {
                future.complete(new JsonObject());
                System.out.println("failed");
            }
        });
    }


}

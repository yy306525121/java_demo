package util.concurrent;

import cn.hutool.core.thread.ThreadUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.*;

/**
 * @author yangzy
 */
public class CompletableFutureTest {
    CompletableFuture<String> successF1;
    CompletableFuture<String> successF2;
    CompletableFuture<String> successF3;
    CompletableFuture<String> failureF1;
    CompletableFuture<String> failureF2;
    @Before
    public void init() {
        successF1 = CompletableFuture.supplyAsync(() -> "successF1");
        successF2 = CompletableFuture.supplyAsync(() -> "successF2");
        successF3 = CompletableFuture.supplyAsync(() -> "successF3");
        failureF1 = CompletableFuture.supplyAsync(() -> 1/0 + "");
        failureF2 = CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException("F2错误");
        });
    }

    @Test
    public void toCompletableFuture() {
    }

    @Test
    public void allOfTest() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> f4 = CompletableFuture.allOf(successF1, successF2, successF3);
        CompletableFuture<String> f5 = f4.thenApply(v -> {
            String f1Result = successF1.join();
            String f2Result = successF2.join();
            String f3Result = successF3.join();
            return f1Result + ":" + f2Result + ":" + f3Result;
        });

        System.out.println(f5.get());
    }

    @Test
    public void anyOfTest() throws ExecutionException, InterruptedException {
        CompletableFuture<Object> f4 = CompletableFuture.anyOf(successF1, successF2, failureF1, failureF2);
        System.out.println(f4.join());
    }

    @Test
    public void exceptionallyComposeTest() {
        CompletableFuture<String> f4 = failureF1.exceptionallyComposeAsync(err -> {
            System.out.println(err);
            return successF1;
        }, ThreadUtil.newExecutor());
        System.out.println(f4.join());
    }

    @Test
    public void exceptionallyAsyncTest() {
        CompletableFuture<String> f4 = failureF1.exceptionallyAsync(err -> {
            System.out.println(err);
            return "hello";
        }, ThreadUtil.newExecutor());
        System.out.println(f4.join());
    }

    @Test
    public void handleAsyncTest() throws ExecutionException, InterruptedException {
        CompletableFuture<String> f4 = successF1.handleAsync((value, error) -> {
            System.out.println("value: " + value);
            System.out.println("error: " + error);
            return "成功了";
        });
        System.out.println("f4结果：" + f4.get());
    }

    @Test
    public void handleTest() {
        CompletableFuture<String> f4 = failureF1.handle((value, error) -> {
            System.out.println("value: " + value);
            System.out.println("error: " + error);
            return "成功了";
        });
        System.out.println("f4结果：" + f4.join());
    }

    @Test
    public void thenComposeAsyncTest() {
        CompletableFuture<String> f4 = failureF1.thenComposeAsync((value) -> {
            System.out.println("value: " + value);
            return successF1;
        }, ThreadUtil.newExecutor());
        System.out.println("f4结果：" + f4.join());
    }

    @Test
    public void failedFutureTest() {
        CompletableFuture<Object> f4 = CompletableFuture.failedFuture(new Exception("失败了"));
        System.out.println(f4.join());
    }

    @Test
    public void completedStageTest() {
        CompletionStage<String> f4 = CompletableFuture.completedStage("hello");
        System.out.println(f4.thenApply(v -> v + " world").toCompletableFuture().join());
    }

    @Test
    public void delayedExecutorTest1() {
        Executor executor = CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS);
        executor.execute(() -> System.out.println("hello world"));
        ThreadUtil.sleep(10, TimeUnit.SECONDS);
    }

    @Test
    public void delayedExecutorTest2() {
        Executor executor = CompletableFuture.delayedExecutor(2, TimeUnit.SECONDS, Executors.newSingleThreadExecutor());
        executor.execute(() -> System.out.println("hello world"));
        executor.execute(() -> System.out.println("hello world2"));
        ThreadUtil.sleep(10, TimeUnit.SECONDS);
    }
}

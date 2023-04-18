package util.concurrent;

import cn.hutool.core.thread.ThreadUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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
    public void exceptionalTest() {
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
            return successF1;
        }, ThreadUtil.newExecutor());
        System.out.println(f4.join());
    }

}

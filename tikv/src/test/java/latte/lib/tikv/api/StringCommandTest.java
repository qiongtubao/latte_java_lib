package latte.lib.tikv.api;


import latte.lib.tikv.KVClientFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StringCommandTest {

    @Test
    public void set_test() {
        StringCommand client = KVClientFactory.getKVClientByClusterName("local");
        client.set("aa", "bb");
        Assert.assertEquals("bb",client.get("aa"));
    }

    @Test
    public void mset_test() {
        StringCommand client = KVClientFactory.getKVClientByClusterName("local");
        client.mset("aa", "v1", "bb", "v11");
        List<String> values = client.mget("aa", "bb");
        Assert.assertEquals(2, values.size());
        Assert.assertEquals("v1", values.get(0));
        Assert.assertEquals("v11", values.get(1));
    }

    @Test
    public void futureAsync() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CompletableFuture<String> futures[] = new CompletableFuture[3];
        futures[0] = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Result 1";
        }, executorService);
        futures[1] = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Result 2";
        }, executorService);
        futures[2] = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Result 3";
        }, executorService);

        CompletableFuture a = CompletableFuture.allOf(futures);
        long stateTime = System.currentTimeMillis();
        a.get();
        System.out.println(System.currentTimeMillis() - stateTime);

    }

}

package latte.lib.tikv.api;

import latte.lib.tikv.AbstractKVServer;
import latte.lib.tikv.KVClient;
import latte.lib.tikv.KVClientFactory;
import latte.lib.tikv.api.transactional.TransactionAction;
import latte.lib.tikv.api.transactional.TransactionalStringCommand;
import latte.lib.tikv.plugin.TiKVComponents;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionCommandTest extends AbstractKVServer {

    @Test
    public void get() {
        KVClient client = KVClientFactory.getKVClient(pdAddr);
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        CompletableFuture<String> futures[] = new CompletableFuture[3];
        futures[0] = CompletableFuture.supplyAsync(() -> {
            try {
                client.exec(new TransactionAction() {
                    @Override
                    public void doTxnAction(TransactionalCommands cmd) {
                        try {
                            Thread.sleep(2000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        cmd.set("k", "v");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "v";
        }, executorService);
        futures[1] = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return client.get("k");
        }, executorService);

        CompletableFuture a = CompletableFuture.allOf(futures);
        try {
            a.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

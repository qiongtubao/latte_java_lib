package latte.lib.stablize.task.impl.tikv;

import latte.lib.common.utils.RandomUtils;
import latte.lib.stablize.task.AbstractTask;
import latte.lib.stablize.task.AbstractUnlimitedTask;
import latte.lib.tikv.KVClient;
import latte.lib.tikv.KVClientFactory;
import latte.lib.tikv.api.StoreType;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class OneKeyConflictTask extends AbstractUnlimitedTask {

    int conflictCount;
    KVClient client;
    String key;
    public OneKeyConflictTask(int threadnum, String host, int port, int conflictCount, ScheduledExecutorService scheduledExecutorService, ExecutorService executorService) {
        super(threadnum, scheduledExecutorService, executorService);
        this.conflictCount = conflictCount;
        client = KVClientFactory.getKVClient(host + ":" + port);
        key = new String(RandomUtils.randomBytes(5));
    }

    String getHostAddr() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String ipAddress = localHost.getHostAddress();
            return ipAddress;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    @Override
    public void doTest() {
        long start = System.currentTimeMillis();
        System.out.println("test start " + start);
        CompletableFuture<Object>[] futures = new CompletableFuture[conflictCount];
        String currentKey =  getHostAddr() + "-" + Thread.currentThread().getName() + key;
        for(int i = 0; i < this.conflictCount; i++) {
            futures[i] = CompletableFuture.supplyAsync(()-> {
                String value = RandomUtils.randomString(5);
                try {
                    client.set(key, value);
                    return value;
                } catch (Exception e) {
                    return e;
                }
            }, this.executorService);
        }
        CompletableFuture<Void> joinFuture = CompletableFuture.allOf(futures);
        try {
            joinFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("OneKeyConflict join all future fail");
        }
        boolean success = false;
        Object[] results = new Object[conflictCount];
        for(int i = 0; i < this.conflictCount; i++) {
            try {
                Object result = futures[i].get();
                if (result.getClass() == String.class) {
                    success = true;
                    String r = client.get(currentKey);
                    if (r == null) {
                        System.out.println( String.format("get wrong key(%s) value %s != null", currentKey, result));
                    } else if (!result.equals(r)) {
                        System.out.println(String.format("get wrong key(%s) value %s != %s", currentKey, result, r));
                    }
                }
                results[i] = result;
            } catch (Exception e) {
                logger.error("[singleOneKey] wait join alll future fail");
            }
        }
        if (success == false) {
            System.out.println("put " + currentKey + " fail " + System.currentTimeMillis());

            Exception last = null;
            while(true) {
                try {
                    client.del(StoreType.String, currentKey);
                    break;
                } catch (Exception e) {
                    if (last == null || !last.equals(e)) {
                        logger.error("[singleOneKey][[title='self-healing']] fail", e);
                        last = e;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e1) {

                    }
                }
            }
        }
        System.out.println("end use " + (System.currentTimeMillis()-start) + "ms");
    }
}

package latte.lib.db.example.tikv;

import latte.lib.tikv.KVClient;
import latte.lib.tikv.KVClientFactory;
import latte.lib.tikv.api.TransactionalCommands;
import latte.lib.tikv.api.transactional.TransactionAction;
import latte.lib.tikv.impl.DefaultKVClient;
import lombok.Getter;
import lombok.Setter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.tikv.common.PDClient;
import org.tikv.common.TiConfiguration;
import org.tikv.common.TiSession;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class KVClientTest extends AbstractTiKVServer{
    @Before
    public void before() throws Exception {
        startServer();
    }

    @Getter
    @Setter
    static public class KV {
        String key;
        String value;
        public KV(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    @After
    public void after() throws IOException, InterruptedException {
        stopServer();
    }

    @Test
    public void set() throws Exception {
        Thread.sleep(8000);
        TiConfiguration conf = TiConfiguration.createDefault(PD_ADDR);
        conf.setApiVersion(TiConfiguration.ApiVersion.V2);
        TiSession session = TiSession.create(conf);
        KVClient client = new DefaultKVClient(session);
        client.set("a", "b");
        System.out.println(client.get("a"));
        Assert.assertEquals("b", client.get("a"));
    }

    @Test
    public void sets() {
        TiConfiguration conf = TiConfiguration.createDefault(PD_ADDR);
        conf.setApiVersion(TiConfiguration.ApiVersion.V2);
        TiSession session = TiSession.create(conf);
        KVClient client = new DefaultKVClient(session);
        String value = Utils.randomKey(1<<10);
        String key = Utils.randomKey(10);
        int keysize = 20;
        for (int i = 0; i < keysize; i++) {
            client.set(key + i, value);
        }

        for(int i = 0; i < keysize; i++) {
            String result = client.get(key + i);
            if(!value.equals(result)) {
                System.out.println("fail i=" + i + "," + value + " != " + client.get(key + i));
                Assert.fail();
            }
        }
    }
    @Test
    public void asyncSet() throws Exception {
        startTiKVServer(TIKV_PORT_1 + 1);
        startTiKVServer(TIKV_PORT_1 + 2);
        String key = Utils.randomKey(10);
        String value = Utils.randomKey(1<<10);
        int THREAD_POOL_SIZE = 10;
        BlockingQueue<KV> kvQueue = new LinkedBlockingQueue<>();
        int WRITE_COUNT = 100000;
        Thread.sleep(5000);
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        TiConfiguration conf = TiConfiguration.createDefault(PD_ADDR);
        conf.setApiVersion(TiConfiguration.ApiVersion.V2);
        TiSession tmpSession = null;
        int wait_start_count = 0;
        while(tmpSession == null) {
            try {
                tmpSession =TiSession.create(conf);
                break;
            } catch (Exception e) {
                System.out.println("wait pd and tikv server start" +wait_start_count++);
            }
            Thread.sleep(1000);
        }

        TiSession session = tmpSession;
        AtomicInteger write_success_count = new AtomicInteger(0);
        AtomicInteger write_fail_count = new AtomicInteger(0);
        long start_time = System.currentTimeMillis();
        // 定义写入 Redis 的任务
        Runnable task = () -> {
            KVClient client1 = new DefaultKVClient(session);
            while (true) {
                try {
                    // 从队列获取键值对
                    KV kv = kvQueue.take();
                    if (kv == null) {
                        break;
                    }
                    String key1 = kv.getKey();
                    String value1 = kv.getValue();
                    // 写入 Redis
                    try {
                        client1.set(key1, value1);
                        write_success_count.incrementAndGet();
                    } catch (Exception e) {
                        System.out.println(Thread.currentThread().getName() + " wrote " + key1 + " to tikv fail");
                        e.printStackTrace();
                        write_fail_count.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    write_fail_count.incrementAndGet();
                }
            }
        };

        // 提交多个任务到线程池
        for (int i = 0; i < 10; i++) {
            executorService.submit(task);
        }


        // 主进程向队列存入键值对
        for (int i = 0; i < WRITE_COUNT; i++) {
            kvQueue.put(new KV(key + i, value));
        }
        while(true) {
            //wait async write end
            if ((write_success_count.get() + write_fail_count.get()) == WRITE_COUNT) {
                if (write_fail_count.get() != 0) {
                    System.out.println("fail:"+write_fail_count.get() + "/" + WRITE_COUNT);
                }
                break;
            }
            Thread.sleep(100);
        }
        stopTiKVServer(TIKV_PORT_1);
        System.out.println("write:" + (System.currentTimeMillis() - start_time) + "ms/" + WRITE_COUNT);
        start_time = System.currentTimeMillis();
        KVClient client = new DefaultKVClient(session);
        int fail_count = 0;
        for(int i = 0; i < WRITE_COUNT; i++) {
            if (!client.get(key + i).equals(value)) {
                System.out.println("get fail: " + i);
                fail_count++;
            }
        }
        Assert.assertEquals(fail_count, write_fail_count.get());
        System.out.println("read:" + (System.currentTimeMillis() - start_time )+ "ms/" + WRITE_COUNT);
    }

    @Test
    public void transaction() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        TiConfiguration conf = TiConfiguration.createDefault(PD_ADDR);
        conf.setApiVersion(TiConfiguration.ApiVersion.V2);
        TiSession tmpSession = null;
        int wait_start_count = 0;
        while(tmpSession == null) {
            try {
                tmpSession =TiSession.create(conf);
                break;
            } catch (Exception e) {
                System.out.println("wait pd and tikv server start" +wait_start_count++);
            }
            Thread.sleep(1000);
        }
        TiSession session = tmpSession;
        executorService.submit(() -> {
            KVClient client1 = new DefaultKVClient(session);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            client1.set("a", "v2");
        });

        KVClient client = new DefaultKVClient(session);
        boolean hadError = false;
        try {
            client.exec(new TransactionAction() {
                @Override
                public void doTxnAction(TransactionalCommands cmd) {
                    System.out.println("1:" + cmd.get("a"));
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("2:" + cmd.get("a"));
                    cmd.set("a", "v1");
                }
            });
        } catch (Exception e) {
            hadError = true;
        }
        Assert.assertEquals(true, hadError);
        System.out.println("3:"+ client.get("a"));
    }

}

package latte.lib.db.example.tikv;

import latte.lib.tikv.KVClient;
import latte.lib.tikv.impl.DefaultKVClient;
import lombok.Getter;
import lombok.Setter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.tikv.common.TiConfiguration;
import org.tikv.common.TiSession;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class RestartTiKvTest extends AbstractTiKVServer{
    @Before
    public void before() throws Exception {
        startServer();
    }



    @After
    public void after() throws IOException, InterruptedException {
        stopServer();
    }

    @Test
    public void asyncSet() throws Exception {
        startTiKVServer(TIKV_PORT_1 + 1);
        startTiKVServer(TIKV_PORT_1 + 2);
        String key = Utils.randomKey(10);
        String value = Utils.randomKey(1<<10);
        int THREAD_POOL_SIZE = 10;
        BlockingQueue<KVClientTest.KV> kvQueue = new LinkedBlockingQueue<>();
        int WRITE_COUNT = 20000;
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
                    KVClientTest.KV kv = kvQueue.take();
                    if (kv == null) {
                        break;
                    }
                    String key1 = kv.getKey();
                    String value1 = kv.getValue();
                    int retry = 0;
                    // 写入 Redis
                    while(true) {
                        try {
                            client1.set(key1, value1);
                            if (retry != 0) System.out.println(Thread.currentThread().getName() + " " + System.currentTimeMillis() + " wrote " + key1 + " to tikv ok");
                            break;
                        } catch (Exception e) {
                            if (retry < 2) System.out.println(Thread.currentThread().getName() + " " + System.currentTimeMillis() + " wrote " + key1 + " to tikv fail:" + e.getMessage());
                            retry ++;
                            Thread.sleep(100);
                        }
                    }
                    write_success_count.incrementAndGet();
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

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            int index = 0;
            @Override
            public void run() {
                try {
                    restartTiKVServer(TIKV_PORT_1 + ((index++)%3));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        // 延迟 10 秒后开始执行任务，然后每隔 5 秒执行一次
        timer.schedule(timerTask, 10000, 30000);

        // 主进程向队列存入键值对
        for (int i = 0; i < WRITE_COUNT; i++) {
            kvQueue.put(new KVClientTest.KV(key + i, value));
        }
        KVClient client = new DefaultKVClient(session);
        while(true) {
            //wait async write end
            for(int i = 0; i < (write_success_count.get() + write_fail_count.get() - THREAD_POOL_SIZE * 10); i++) {
                String result = null;
                int retry = 0;
                while(true) {
                    try {
                        result = client.get(key + i);
                        break;
                    } catch (Exception e) {
                        retry++;
                        if (retry % 10 == 0) {
                            System.out.println("get i = " + i + " get1 fail");
                        }
                    }
                }
                if (!value.equals(result)) {
                    System.out.println(System.currentTimeMillis() + " get1 fail: " + i);
                }
            }
            if ((write_success_count.get() + write_fail_count.get()) == WRITE_COUNT) {
                if (write_fail_count.get() != 0) {
                    System.out.println("fail:"+write_fail_count.get() + "/" + WRITE_COUNT);
                }
                break;
            }
            Thread.sleep(100);
        }

        System.out.println("write:" + (System.currentTimeMillis() - start_time) + "ms/" + WRITE_COUNT);
        start_time = System.currentTimeMillis();

        int fail_count = 0;
        for(int i = 0; i < WRITE_COUNT; i++) {
            String result = null;
            int retry = 0;
            while(true) {
                try {
                    result = client.get(key + i);
                    break;
                } catch (Exception e) {
                    retry++;
                    if (retry % 10 == 0) {
                        System.out.println("get i = " + i + " get2 fail");
                    }
                }
                Thread.sleep(100);
            }
            if (!value.equals(result)) {
                System.out.println("get2 fail: " + i);
                Assert.fail();
            }
        }
        Assert.assertEquals(fail_count, write_fail_count.get());
        System.out.println("read:" + (System.currentTimeMillis() - start_time )+ "ms/" + WRITE_COUNT);
        timerTask.cancel();
    }
}

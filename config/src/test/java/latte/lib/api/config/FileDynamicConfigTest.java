package latte.lib.api.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.junit.Assert;
import org.junit.Test;

public class FileDynamicConfigTest {

  public boolean rewriteFile(String filename, String content) throws IOException {
    File file = new File("/tmp/file_dynamic_config.json");
    if (file.exists()) {
      boolean isDeleted = file.delete();
      Assert.assertEquals(true, isDeleted);
    }
    Assert.assertEquals(true, file.createNewFile());
    try (FileOutputStream fos = new FileOutputStream(file)) {
      fos.write(content.getBytes());
      fos.flush();
    } catch (IOException e) {
      throw e;
    }
    return true;
  }
    @Test
    public void testFileDynamicConfig() throws Exception {
      String filename = "/tmp/file_dynamic_config.json";
      rewriteFile(filename, "{\"a\":1}");
      ScheduledThreadPoolExecutor configExecutor = new ScheduledThreadPoolExecutor(2);
      DynamicConfig config =new FileDynamicConfig(filename, 1, configExecutor);
      Assert.assertTrue(1 == config.get("a", Integer.class));
      final CountDownLatch latch = new CountDownLatch(1); // 初始化计数器为1
      config.addListen("a", Integer.class, (old, now) -> {
          Assert.assertTrue(old == 1);
          Assert.assertTrue(now == 2);
          latch.countDown();
      });
      rewriteFile(filename, "{\"a\":2}");
      latch.await();
      Assert.assertTrue(config.get("a", int.class) == 2);
      configExecutor.shutdown();
    }
}

package latte.lib.kv.obkv.impl;
import com.alipay.sofa.common.log.LoggerSpaceManager;
import latte.lib.kv.AbstractKVTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

public class DefaultObkvClientTest extends AbstractKVTest implements  CreateObkvClientTest {

  @Before
  public void before() throws Exception {
//    System.setProperty("logging.level.oceanbase-table-client", "off");
    super.before();
  }

  @Test
  public void get() throws Exception {
    super.get();
  }

  @Test
  public void scan() throws Exception {
    super.scan();
  }

  @Test
  public void mget() throws Exception {
    super.mget();
  }

  @Test
  public void mset() throws Exception {
    super.mset();
  }
}

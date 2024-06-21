package latte.lib.kv.tikv.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import latte.lib.kv.AbstractKVTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



public class DefaultTikvClientTest extends AbstractKVTest implements CreateTikvClientTest {

  @Before
  public void before() throws Exception {
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

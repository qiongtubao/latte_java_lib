package latte.lib.stabilize.task.util.pool;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import latte.lib.common.utils.RandomUtils;
import latte.lib.stabilize.task.util.RandomPool;
public class DefaultRandomPool<T> implements RandomPool<T> {

  List<T> pool;

  public DefaultRandomPool(int size, Supplier<T> supplier) {
      pool = new LinkedList<>();
      for(int i = 0; i < size; i++) {
        pool.add(supplier.get());
      }
  }
  @Override
  public T random() {
    return pool.get(RandomUtils.randomInt(0, pool.size()));
  }
}

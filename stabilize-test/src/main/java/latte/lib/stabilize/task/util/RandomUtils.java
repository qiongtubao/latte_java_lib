package latte.lib.stabilize.task.util;

import java.util.Random;
import java.util.function.Supplier;
import latte.lib.stabilize.task.util.pool.DefaultRandomPool;

public class RandomUtils {
  static Random random = new Random();
  static public byte[] randomBytes(int size) {
    byte[] result = new byte[size];
    random.nextBytes(result);
    return result;
  }

  static public int randomInt(int min, int max) {
      return random.nextInt(max - min) + min;
  }

  static public String randomString(int size) {
    return new String(randomBytes(size));
  }
  static public <T> RandomPool<T> createPool(int poolSize, Supplier<T> supplier) {
    return new DefaultRandomPool<>(poolSize, supplier);
  }
}

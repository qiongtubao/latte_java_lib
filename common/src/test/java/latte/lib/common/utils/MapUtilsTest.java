package latte.lib.common.utils;

import static java.util.Objects.requireNonNull;

//import com.google.common.collect.Maps;
//import com.google.common.collect.Range;
//import com.google.common.collect.RangeMap;
//import com.google.common.collect.TreeRangeMap;
//import com.google.common.primitives.Bytes;
//import com.google.protobuf.ByteString;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
//import javax.annotation.Nonnull;
import org.junit.Test;

public class MapUtilsTest {

//  @Test
//  public void rangeMapTest() {
//    RangeMap<Integer, Long> rangeMap = TreeRangeMap.create();
//    Range<Integer> range = Range.closedOpen(1, 5);
//    rangeMap.put(range, 1L);
//    Range<Integer> range2 = Range.closedOpen(3, 6);
//    rangeMap.put(range2, 2L);
//    System.out.println(rangeMap.asMapOfRanges().values());
//    Long result = rangeMap.get(4);
//    System.out.println(result);
//  }


    public String incrementLastCharacter(String str) {
      char[] chars = str.toCharArray();
      int lastIndex = chars.length - 1;

      if (chars[lastIndex] == 'Z') {
        chars[lastIndex] = 'A';
        for (int i = lastIndex - 1; i >= 0; i--) {
          if (chars[i] != 'Z') {
            chars[i]++;
            break;
          } else {
            chars[i] = 'A';
          }
        }
      } else {
        chars[lastIndex]++;
      }

      return new String(chars);
    }

  public List<Long> getExtraElements1(Long[] A, Long[] B) {
    List<Long> extraElements = new ArrayList<>();
    Arrays.sort(B); // 对数组 B 进行排序

    for (long num : A) {
      if (Arrays.binarySearch(B, num) < 0) { // 使用二分查找算法查找 num 是否存在于数组 B 中
        extraElements.add(num);
      }
    }

    return extraElements;
  }

  public Long[] getExtraElements2(Long[] A, Long[] B) {
    Set<Long> extraElements = new HashSet<>();
    Set<Long> set = new HashSet<>();

    for (long num : B) {
      set.add(num);
    }

    for (long num : A) {
      if (!set.contains(num)) {
        extraElements.add(num);
      }
    }

    return extraElements.toArray(new Long[0]);
  }

  public List<Long> getExtraElements(Long[] A, Long[] B) {
    List<Long> extraElements = new ArrayList<>();

    for (long num : A) {
      boolean found = false;
      for (long b : B) {
        if (num == b) {
          found = true;
          break;
        }
      }
      if (!found) {
        extraElements.add(num);
      }
    }

    return extraElements;
  }
//  @Test
//  public void rangeMapTest2() {
//    Random random = new Random();
//    for (int k = 0 ; k < 10; k++) {
//      RangeMap<String, Long> rangeMap = TreeRangeMap.create();
//
//      String start = "AAA";
//      for (long i = 0; i < 17575; i++) {
//        String end = incrementLastCharacter(start);
//        Range<String> range = Range.closedOpen(start, end);
//        rangeMap.put(range, random.nextLong());
//        start = end;
//      }
//
//      long start_time = System.currentTimeMillis();
//      //sorted 21ms +
//      // no sorted 14ms
//      Long[] r = rangeMap.asMapOfRanges().values().toArray(new Long[0]);
//      System.out.println("use " + (System.currentTimeMillis() - start_time) + "ns");
//      rangeMap.put(Range.closedOpen("ABC", "BCA"), random.nextLong());
//      start_time = System.currentTimeMillis();
//      //sorted 21ms +
//      // no sorted 14ms
//      Long[] r2 = rangeMap.asMapOfRanges().values().toArray(new Long[0]);
//      System.out.println("use2 " + (System.currentTimeMillis() - start_time) + "ns");
//
//      start_time = System.currentTimeMillis();
//      Long[] extraElements = getExtraElements2(r, r2);
//      System.out.println("use3 " + (System.currentTimeMillis() - start_time) + "ns");
//
//    }
//
//
//  }
//
//  @Test
//  public void rangeMapTest3() {
//    Random random = new Random();
//    for (int k = 0 ; k < 10; k++) {
//      Map<String, Long> rangeMap = Maps.newConcurrentMap();
//
//      String start = "AAA";
//      for (long i = 0; i < 17575; i++) {
//        String end = incrementLastCharacter(start);
////      Range<String> range = Range.closedOpen(start, end);
//        rangeMap.put(start, random.nextLong());
//        start = end;
//      }
//
//      long start_time = System.nanoTime();
//      List<Long> r = rangeMap.values().stream().sorted().collect(Collectors.toList());
//      System.out.println("use " + (System.nanoTime() - start_time) + "ns");
//    }
//
//
//
//  }
//
//  @Test
//  public void test3() {
//    ByteString TXN_DEFAULT_PREFIX =
//        ByteString.copyFrom(new byte[] {'x', 0, 0, 0});
//    System.out.println(TXN_DEFAULT_PREFIX.toString());
//  }
//
//  @Test
//  public void String() {
//    String hexString = "7200000100000000FB"; // 十六进制字符串
//    byte[] byteArray = new byte[hexString.length() / 2];
//
//    for (int i = 0; i < byteArray.length; i++) {
//      int index = i * 2;
//      int intValue = Integer.parseInt(hexString.substring(index, index + 2), 16);
//      byteArray[i] = (byte) intValue;
//    }
//
//    System.out.println(ByteString.copyFrom(byteArray));
//  }
}

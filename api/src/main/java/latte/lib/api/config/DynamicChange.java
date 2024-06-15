package latte.lib.api.config;

public interface DynamicChange {

  /**
   * 动态配置变更的时候 需要注册事件和触发   还没相好怎么样的接口比较好
   *  难点是属性各种class 复杂class是嵌套的
   *  其中map是其中一个变更，或者list其中一个变更，class中某个属性中属性·变更
   */

  /**
   * DynamicConfig 就只通知第一层属性
   *   这里对比只是字符串对比，
   *   比如 json如何加了个空格其实是一样 这里也会通知DynamicConfigClass
   */


  /**
   * DynamicConfigClass 触发change自己去判断
   */

}

package cn.spring;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mr
 */
public class SingletonPool {
  /** 懒汉式 */
  private static Map<String, Object> SingletonPoolMap;

  private SingletonPool() {}

  public static Map<String, Object> getSingletonPoolMap() {
    if (SingletonPoolMap == null) {
      synchronized (SingletonPool.class) {
        if (SingletonPoolMap == null) {
          SingletonPoolMap = new HashMap<>();
        }
      }
    }
    return SingletonPoolMap;
  }
}

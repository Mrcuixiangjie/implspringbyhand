package cn.spring;

import java.util.HashMap;

/**
 * @author mr
 */
public class BeanDefinitionMap {
  private BeanDefinitionMap() {}

  private static HashMap<String, BeanDefinition> beanMap = null;
  /** 懒汉模式,单例map */
  public static HashMap<String, BeanDefinition> getBeanMap() {
    if (beanMap == null) {
      synchronized (BeanDefinitionMap.class) {
        if (beanMap == null) {
          beanMap = new HashMap<>();
        }
      }
    }
    return beanMap;
  }
}

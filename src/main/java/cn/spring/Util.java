package cn.spring;

import java.lang.annotation.AnnotationFormatError;

/**
 * @author mr
 */
public class Util {
  private Util() {}

  public static boolean isSingleton(String scopeValue) {
    if (ConstantPool.PROTOTYPE.equalsIgnoreCase(scopeValue)) {
      // 多例bean
      return false;
    } else if (ConstantPool.SINGLETON.equalsIgnoreCase(scopeValue)) {
      // 单例bean
      return true;
    } else {
      throw new AnnotationFormatError("@Scope的值必须为Singleton或Prototype,默认为Singleton");
    }
  }
}

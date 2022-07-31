package cn.spring;

/**
 * @author mr
 */
public class BeanDefinition {
  /** 类的类型 */
  private Class type;
  /** 类的@Scope为Singleton还是Prototype,作用域 */
  private String scope;
  /** bean是否是懒加载的 */
  private boolean isLazy;

  public Class getType() {
    return type;
  }

  public void setType(Class type) {
    this.type = type;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public boolean isLazy() {
    return isLazy;
  }

  public void setLazy(boolean lazy) {
    isLazy = lazy;
  }
}

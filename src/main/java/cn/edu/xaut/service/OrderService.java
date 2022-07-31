package cn.edu.xaut.service;

import cn.spring.Component;
import cn.spring.Lazy;
import cn.spring.Scope;
import cn.spring.Value;

/**
 * @author mr
 */
@Lazy
@Component(value = "orderService")
@Scope(value = "prototype")
public class OrderService {
  private String constructArgument;
  private String constructorArgument2;

  public OrderService(
      @Value(value = "我是orderService的有参构造方法") String constructArgument,
      @Value(value = "我是参数二") String constructorArgument2) {
    this.constructArgument = constructArgument;
    this.constructorArgument2 = constructorArgument2;
  }

  public String getConstructArgument() {
    return constructArgument;
  }

  public String getConstructorArgument2() {
    return constructorArgument2;
  }
}

package cn.edu.xaut.service;

import cn.edu.xaut.Appconfig;
import cn.spring.*;

/**
 * @author mr
 */
@Lazy
@Component(value = "testAutowired")
public class TestAutowired implements InitializingBean {
  static {
    /*初始化springIoc容器*/
    CxjApplicationContext springIoc = new CxjApplicationContext(Appconfig.class);
  }

  @Autowired private static OrderService orderService;
  @Autowired private static UserService userService;
  @Autowired private static TestAutowired testAutowired;

  public static void main(String[] args) {
    testAutowired.execute();
  }

  public void execute() {
    System.out.println(orderService.getConstructArgument());
    System.out.println(orderService.getConstructorArgument2());
    userService.test();
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    System.out.println("I am initialing!");
  }
}

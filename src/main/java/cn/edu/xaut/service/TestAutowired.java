package cn.edu.xaut.service;

import cn.edu.xaut.Appconfig;
import cn.spring.*;

/**
 * @author mr
 */
@Lazy
@Component
public class TestAutowired implements InitializingBean, BeanNameAware {
  static {
    /*初始化springIoc容器*/
    CxjApplicationContext springIoc = new CxjApplicationContext(Appconfig.class);
  }

  @Autowired private static OrderService orderService;
  @Autowired private static UserServiceInterface userService;
  private static String beanName;

  @CXJInitValue(value = "hello")
  public static String initValue;

  public static void main(String[] args) {
    execute();
  }

  public static void execute() {
    System.out.println(orderService.getConstructArgument());
    System.out.println(orderService.getConstructorArgument2());
    userService.test();
    System.out.println(initValue);
    System.out.println(beanName);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    System.out.println("I am initialing!");
  }

  @Override
  public void setBeanName(String beanName) {
    this.beanName = beanName;
  }
}

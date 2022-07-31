package cn.edu.xaut.service;

import cn.edu.xaut.Appconfig;
import cn.spring.CxjApplicationContext;

/**
 * @author mr
 */
public class Test {
  public static void main(String[] args) {
    /*
     *手写spring源码
     */
    CxjApplicationContext springIoc = new CxjApplicationContext(Appconfig.class);
    System.out.println(springIoc.getBean("userService"));
    System.out.println(springIoc.getBean("userService"));
    System.out.println(springIoc.getBean("userService"));
    System.out.println(((OrderService) springIoc.getBean("orderService")));
    System.out.println(((OrderService) springIoc.getBean("orderService")).getConstructArgument());
    System.out.println(((OrderService) springIoc.getBean("orderService")));
    System.out.println(
        ((OrderService) springIoc.getBean("orderService")).getConstructorArgument2());
  }
}

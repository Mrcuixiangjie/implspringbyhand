package cn.edu.xaut.service;

import cn.spring.Component;
import cn.spring.Lazy;
import cn.spring.Scope;

/**
 * @author mr
 */
@Lazy
@Scope(value = "prototype")
@Component(value = "userService")
public class UserService {
  public void test() {
    System.out.println(UserService.class.getResource(""));
  }
}

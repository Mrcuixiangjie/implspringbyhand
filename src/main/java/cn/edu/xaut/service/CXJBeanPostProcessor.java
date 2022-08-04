package cn.edu.xaut.service;

import cn.spring.BeanPostProcessor;
import cn.spring.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author mr
 */
@Component
public class CXJBeanPostProcessor implements BeanPostProcessor {
  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) {
    for (Field field : bean.getClass().getDeclaredFields()) {
      if (field.isAnnotationPresent(CXJInitValue.class)) {
        CXJInitValue cxjInitValue = field.getAnnotation(CXJInitValue.class);
        String value = cxjInitValue.value();
        try {
          field.setAccessible(true);
          field.set(bean, value);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) {
    if (bean instanceof UserService) {
      Object proxyInstance =
          Proxy.newProxyInstance(
              CXJBeanPostProcessor.class.getClassLoader(),
              bean.getClass().getInterfaces(),
              new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                  /*切面逻辑*/
                  System.out.println("切面逻辑");
                  return method.invoke(bean, args);
                }
              });
      return proxyInstance;
    }
    return bean;
  }
}

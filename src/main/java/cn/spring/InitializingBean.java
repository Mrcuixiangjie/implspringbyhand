package cn.spring;

/**
 * @author mr
 */
public interface InitializingBean {
  void afterPropertiesSet() throws Exception;
}

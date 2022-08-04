package cn.spring;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;

/**
 * @author mr
 */
public class CxjApplicationContext {
  private final Class appconfigClass;
  private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

  public CxjApplicationContext(Class appconfigClass) {
    this.appconfigClass = appconfigClass;
    /* 扫描并装载BeanDefinition */
    scan();
    /* 装载非懒加载的单例Bean */
    createAllSingletonNoLazyBean();
  }
  /** 扫描并装载BeanDefinition */
  public void scan() {
    /*获取BeanDefinitionMap对象，即BeanDefinition池*/
    HashMap<String, BeanDefinition> beanDefinitionMap = BeanDefinitionMap.getBeanMap();
    /* 判断传入配置类的上面是否有ComponentScan.class注解 */
    if (appconfigClass.isAnnotationPresent(ComponentScan.class)) {
      /* 取出配置类上面的ComponentScan.class的注解对象 */
      ComponentScan componentScanAnnotation =
          (ComponentScan) appconfigClass.getAnnotation(ComponentScan.class);
      /*
       * 获取扫描路径-->获取Appconfig.class类上面ComponentScan(value)的value
       * cn.edu.xaut.service-->cn/edu/xaut/service
       */
      String path = componentScanAnnotation.value().replace(".", "/");
      /* 获取类加载器jvm相关知识 */
      ClassLoader classLoader = CxjApplicationContext.class.getClassLoader();
      /* resuorce为:/home/mr/IdeaProjects/implspringbyhand/target/classes/cn/edu/xaut/service */
      URL resource = classLoader.getResource(path);
      /* 加载目录下的所有文件，也就是该目录下的类对应的字节码文件 */
      File file = new File(resource.getFile());
      if (file.isDirectory()) {
        for (File oneFile : file.listFiles()) {
          /*
           * 绝对路径:
           * /home/mr/IdeaProjects/implspringbyhand/target/classes/cn/edu/xaut/service/UserService.class
           * /home/mr/IdeaProjects/implspringbyhand/target/classes/cn/edu/xaut/service/OrderService.class
           */
          String absolutePath = oneFile.getAbsolutePath();
          /* 截取相对路径，为加载类的字节码做准备 */
          int start = absolutePath.indexOf("classes");
          /* relativePath： cn.edu.xaut.service.UserService cn.edu.xaut.service.OrderService */
          String relativePath =
              absolutePath.substring(start + 8).replace("/", ".").replace(".class", "");
          try {
            Class<?> clazz = classLoader.loadClass(relativePath);
            /* 定义bean对象 */
            BeanDefinition currentBeanDefinition = new BeanDefinition();
            /* 封装当前被加载类的Class类型 */
            currentBeanDefinition.setType(clazz);
            /* 判断被加载类对象的上面是否存在@Component注解 */
            /* 判断当前被加载类为单例还是多例 */
            if (clazz.isAnnotationPresent(Component.class)) {
              if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                try {
                  BeanPostProcessor beanPostProcessor =
                      (BeanPostProcessor) clazz.getDeclaredConstructor().newInstance();
                  beanPostProcessorList.add(beanPostProcessor);
                } catch (InstantiationException e) {
                  e.printStackTrace();
                } catch (IllegalAccessException e) {
                  e.printStackTrace();
                } catch (InvocationTargetException e) {
                  e.printStackTrace();
                } catch (NoSuchMethodException e) {
                  e.printStackTrace();
                }
              }

              /* 获取@Component注解对象 */
              Component componentAnnotation = clazz.getAnnotation(Component.class);
              /* 获取@Component注解对象里的value值 */
              String beanName = componentAnnotation.value();
              /*构造默认的bean名字*/
              if (beanName.equals("")) {
                beanName = Introspector.decapitalize(clazz.getSimpleName());
              }

              /* 判断是单例bean还是多例bean，判断被加载类上是否存在@Scope注解 */
              if (clazz.isAnnotationPresent(Scope.class)) {
                /* 被加载类上存在@Scope注解，需要判断注解里的值来判定是单例bean还是多例bean */
                Scope scopeAnnotation = clazz.getAnnotation(Scope.class);
                /* 获取被加载类上@Scope注解的value为prototype(多例)还是Singleton(单例) */
                String scopeValue = scopeAnnotation.value();
                /* 判断当前被加载类的@Scope是单例还是多例 */
                if (Util.isSingleton(scopeValue)) {
                  /* 单例 */
                  currentBeanDefinition.setScope(ConstantPool.SINGLETON);
                } else {
                  /* 多例 */
                  currentBeanDefinition.setScope(ConstantPool.PROTOTYPE);
                }
                /* 判断当前类是否为懒加载 */
                if (clazz.isAnnotationPresent(Lazy.class)) {
                  /* 懒加载 */
                  currentBeanDefinition.setLazy(true);
                } else {
                  /* 非懒加载 */
                  currentBeanDefinition.setLazy(false);
                }
              } else {
                /* 被加载类上不存在@Scope注解，默认单例bean */
                currentBeanDefinition.setScope(ConstantPool.SINGLETON);
              }
              /* 将创建的bean对象放入beanDefinitionMap */
              beanDefinitionMap.put(beanName, currentBeanDefinition);
            }
          } catch (ClassNotFoundException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  /** 初始化装载非懒加载的所有单例Bean */
  public void createAllSingletonNoLazyBean() {
    /* 找出所有的单例bean，创建单例对象 */
    Map<String, Object> singletonPoolMap = SingletonPool.getSingletonPoolMap();
    HashMap<String, BeanDefinition> beanDefinitionMap = BeanDefinitionMap.getBeanMap();
    beanDefinitionMap.entrySet().stream()
        .forEach(
            stringBeanDefinitionEntry -> {
              /* 找出单例bean */
              BeanDefinition beanDefinition = stringBeanDefinitionEntry.getValue();
              if (beanDefinition.getScope().equals(ConstantPool.SINGLETON)
                  && !beanDefinition.isLazy()) {
                /* 创建对象,存入SingletonPoolMap */
                String beanName = stringBeanDefinitionEntry.getKey();
                /* 创建指定Bean*/
                Class clazz = beanDefinition.getType();
                Object object = createOneSpecificBean(beanName, clazz);
                singletonPoolMap.put(beanName, object);
              }
            });
  }

  /** 获取指定bean */
  public Object getBean(String beanName) {
    Object o = null;
    HashMap<String, BeanDefinition> beanDefinitionMap = BeanDefinitionMap.getBeanMap();
    if (!beanDefinitionMap.containsKey(beanName)) {
      /* 未定义或注册bean，没有@Component注解 */
      throw new NullPointerException();
    }
    /* 获取beanName对应的BeanDefinition对象 */
    BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
    /* 判断是单例还是多例 */
    if (ConstantPool.SINGLETON.equals(beanDefinition.getScope())) {
      /* 单例的 */
      Map<String, Object> singletonPoolMap = SingletonPool.getSingletonPoolMap();
      o = singletonPoolMap.get(beanName);
      if (o == null) {
        o = createOneSpecificBean(beanName, beanDefinition.getType());
        singletonPoolMap.put(beanName, o);
      }
    } else {
      /* 原型的 */
      o = createOneSpecificBean(beanName, beanDefinition.getType());
    }
    return o;
  }

  /** 创建指定class的bean对象 */
  public Object createOneSpecificBean(String beanName, Class clazz) {
    /*前期准备，获取BeanDefinitionMap池和SingletonPoolMap池*/
    Map<String, Object> singletonPoolMap = SingletonPool.getSingletonPoolMap();
    Object bean = null;
    /* 1、实例化bean */
    Constructor constructor = clazz.getConstructors()[0];
    /* 无参构造器 */
    if (constructor.getParameterCount() == 0) {
      try {
        bean = clazz.getDeclaredConstructor().newInstance();
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      }
    } else {
      Class[] parameterTypes = constructor.getParameterTypes();
      Object[] parameterValues =
          Arrays.stream(constructor.getParameters())
              .map(
                  parameter -> {
                    if (parameter.isAnnotationPresent(Value.class)) {
                      return parameter.getAnnotation(Value.class).value();
                    } else {
                      return " ";
                    }
                  })
              .toArray();
      try {
        bean = clazz.getDeclaredConstructor(parameterTypes).newInstance(parameterValues);
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      }
    }
    /* 2、属性填充即@Autowired依赖注入的自动装配 */
    for (Field field : clazz.getDeclaredFields()) {
      if (field.isAnnotationPresent(Autowired.class)) {
        Object beInjected = getBean(field.getName());
        try {
          field.setAccessible(true);
          field.set(bean, beInjected);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }

    /* beanNameAware */
    if (bean instanceof BeanNameAware) {
      ((BeanNameAware) bean).setBeanName(beanName);
    }
    /* 4、处理器增强beanPostProcessor的postProcessBeforeInitialization()方法 */
    for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
      bean = beanPostProcessor.postProcessBeforeInitialization(bean, beanName);
    }
    /*3、初始化工作*/
    if (bean instanceof InitializingBean) {
      try {
        ((InitializingBean) bean).afterPropertiesSet();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    /* 4、处理器增强beanPostProcessor的postProcessAfterInitialization()方法 */
    for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
      bean = beanPostProcessor.postProcessAfterInitialization(bean, beanName);
    }
    return bean;
  }
}

package dev.local.java.samples.app.fabric;

import dev.local.java.samples.app.fabric.annotations.AppBean;
import dev.local.java.samples.app.fabric.annotations.BeanDestroy;
import dev.local.java.samples.app.fabric.annotations.BeanInit;
import dev.local.java.samples.app.fabric.exceptions.AppFabricExceptions;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AppFabric {

  private static final Logger log = LoggerFactory.getLogger(AppFabric.class);

  private static AppFabric instance;

  private Map<String, Object> beans;

  private AppFabric() throws AppFabricExceptions {

    beans = new HashMap<>();

    try {
      String path = AppProperties.getProp("scan.package");

      Reflections reflections = new Reflections(path, new TypeAnnotationsScanner());
      Set<Class<?>> classes = reflections.getTypesAnnotatedWith(AppBean.class, true);

      for (Class<?> bean : classes) {
        String beanName = bean.getSimpleName().substring(0, 1).toLowerCase() + bean.getSimpleName().substring(1);
        java.lang.reflect.Constructor constructor = bean.getConstructor(new Class[]{});
        beans.put(beanName, constructor.newInstance());
      }
    } catch (Exception e) {
      throw new AppFabricExceptions("AppFabric init error: " + e);
    }
  }

  private void init() throws InvocationTargetException, IllegalAccessException {
    for (Object bean : beans.values()) {
      Method[] methods = bean.getClass().getDeclaredMethods();
      for (Method method : methods) {
        Annotation annotations = method.getAnnotation(BeanInit.class);
        if (annotations != null) {
          method.invoke(bean);
          break;
        }
      }
    }

  }

  public void destory() throws AppFabricExceptions {
    try {
      for (Object bean : beans.values()) {
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
          Annotation annotations = method.getAnnotation(BeanDestroy.class);
          if (annotations != null) {
            method.invoke(bean);
            break;
          }
        }
      }
    } catch (Exception e) {
      throw new AppFabricExceptions("AppFabric exception on destroy: " + e);
    }
  }


  /**
   * return bean
   *
   * @param name
   * @return
   * @throws AppFabricExceptions
   */
  public static Object getBean(String name) throws AppFabricExceptions {

    try {
      if (instance == null) {
        instance = new AppFabric();
        instance.init();
        log.info("AppFabric init");
      }
    } catch (Exception e) {
      throw new AppFabricExceptions("AppFabric error: " + e);
    }

    if (instance.beans.containsKey(name)) {
      return instance.beans.get(name);
    }
    throw new AppFabricExceptions("AppFabric not contain bean whith name " + name);
  }
}

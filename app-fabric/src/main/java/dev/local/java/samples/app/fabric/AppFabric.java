package dev.local.java.samples.app.fabric;

import dev.local.java.samples.app.fabric.annotations.AppBean;
import dev.local.java.samples.app.fabric.exceptions.AppFabricExceptions;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AppFabric {

  private static final Logger log = LoggerFactory.getLogger(AppFabric.class);

  private static AppFabric instance;

  private Map<String, Object> beans;

  private AppFabric() {
    beans = new HashMap<>();

    Reflections reflections = new Reflections("", new TypeAnnotationsScanner());
    Set<Class<?>> classes = reflections.getTypesAnnotatedWith(AppBean.class, true);

    for (Class<?> aClass : classes) {
      System.out.println(aClass.getName());

      Method[] methods = aClass.getDeclaredMethods();
      for (Method m : methods) {
        System.out.println("m " + m);
        Annotation[] annotations = m.getAnnotations();
        System.out.println("size " + annotations.length);
        for (Annotation a : annotations) {
          System.out.println("a " + a.annotationType().getName());
        }
      }
    }
  }

  private void init() {

  }

  /**
   * return bean
   *
   * @param name
   * @return
   * @throws AppFabricExceptions
   */
  public static Object getBean(String name) throws AppFabricExceptions {
    if (instance == null) {
      instance = new AppFabric();
      instance.init();
    }

    if (instance.beans.containsKey(name)) {
      return instance.beans.get(name);
    }
    throw new AppFabricExceptions("AppFabric not contain bean whith name " + name);
  }
}

package dev.local.java.samples.app.fabric;

import dev.local.java.samples.app.fabric.annotations.AppBean;
import dev.local.java.samples.app.fabric.annotations.BeanDestroy;
import dev.local.java.samples.app.fabric.annotations.BeanInit;
import dev.local.java.samples.app.fabric.exceptions.AppFabricExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class AppFabric {

  private static final Logger log = LoggerFactory.getLogger(AppFabric.class);

  private static final String CLASS_FILE_SUFFIX = ".class";

  private static AppFabric instance;
  private Map<String, Object> beans;

  private AppFabric() throws AppFabricExceptions {

    try {
      beans = new HashMap<>();
      String path = AppProperties.getProp("scan.package");

      if (path == null) {
        throw new AppFabricExceptions("can't find value for 'scan.package'");
      }

      List<Class<?>> classes = find(path);

      for (Class<?> bean : classes) {
        if (bean.isAnnotationPresent(AppBean.class)) {
          String beanName = bean.getSimpleName().substring(0, 1).toLowerCase() + bean.getSimpleName().substring(1);
          Constructor constructor = bean.getConstructor(new Class[]{});
          beans.put(beanName, constructor.newInstance());
        }
      }
      log.debug("init beans: {}", beans.keySet());
    } catch (Exception e) {
      throw new AppFabricExceptions(e);
    }
  }

  public static void destory() throws AppFabricExceptions {

    if (instance.beans == null) {
      return;
    }
    try {
      for (Object bean : instance.beans.values()) {
        instance.closeBean(bean);
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
        long start = System.currentTimeMillis();
        instance = new AppFabric();
        instance.init();
        long started = System.currentTimeMillis() - start;
        log.info("AppFabric init on {} ms, initialized {} beans", started, instance.beans.size());
      }
    } catch (Exception e) {
      throw new AppFabricExceptions("AppFabric error: ", e);
    }

    if (instance.beans.containsKey(name)) {
      return instance.beans.get(name);
    }
    throw new AppFabricExceptions("AppFabric not contain bean with name: " + name);
  }

  /* Class util methods */
  public List<Class<?>> find(String scannedPackage) throws IOException {

    String scannedPath = scannedPackage.replace('.', '/');
    Enumeration<URL> urls = ClassLoader.getSystemClassLoader().getResources(scannedPath);
    List<Class<?>> classes = new ArrayList();

    while (urls.hasMoreElements()) {
      File file = new File(urls.nextElement().getFile());
      classes.addAll(find(file, scannedPath));
    }
    return classes;
  }

  private List<Class<?>> find(File file, String scannedPackage) {

    List<Class<?>> classes = new ArrayList();

    System.err.println("package: " + scannedPackage);

    if (file.isDirectory()) {
      for (File child : file.listFiles()) {
        String path = scannedPackage + "/" + child.getName();
        classes.addAll(find(child, path));
      }
    } else if (scannedPackage.endsWith(CLASS_FILE_SUFFIX)) {
      int endIndex = scannedPackage.length() - CLASS_FILE_SUFFIX.length();
      String className = scannedPackage.substring(0, endIndex).replace('/', '.');
      try {
        System.err.println("new class: " + className);
        Class bean = ClassLoader.getSystemClassLoader().loadClass(className);
        if (bean.isAnnotationPresent(AppBean.class)) {
          classes.add(bean);
        }
      } catch (ClassNotFoundException e) {
        log.debug("load class error: {}", e);
      }
    }
    return classes;
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

  private void closeBean(Object bean) throws InvocationTargetException, IllegalAccessException {
    Method[] methods = bean.getClass().getDeclaredMethods();
    for (Method method : methods) {
      Annotation annotations = method.getAnnotation(BeanDestroy.class);
      if (annotations != null) {
        method.invoke(bean);
        break;
      }
    }
  }
}

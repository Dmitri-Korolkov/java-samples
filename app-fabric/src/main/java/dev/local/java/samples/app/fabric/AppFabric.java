package dev.local.java.samples.app.fabric;

import dev.local.java.samples.app.fabric.annotations.BeanDestroy;
import dev.local.java.samples.app.fabric.annotations.BeanInit;
import dev.local.java.samples.app.fabric.exceptions.AppFabricExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class AppFabric {

  private static final Logger log = LoggerFactory.getLogger(AppFabric.class);

  private static AppFabric instance;

  private Map<String, Object> beans;

  private AppFabric() throws AppFabricExceptions {

    beans = new HashMap<>();

    try {
      String path = AppProperties.getProp("scan.package");

      Iterable<Class> classes = getClasses(path);

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
        long start = System.currentTimeMillis();
        instance = new AppFabric();
        instance.init();
        long started = System.currentTimeMillis() - start;
        log.info("AppFabric init on {} ms", started);
      }
    } catch (Exception e) {
      throw new AppFabricExceptions("AppFabric error: " + e);
    }

    if (instance.beans.containsKey(name)) {
      return instance.beans.get(name);
    }
    throw new AppFabricExceptions("AppFabric not contain bean whith name " + name);
  }

  /* Class util methods */
  private Iterable<Class> getClasses(String packageName) throws ClassNotFoundException, IOException, URISyntaxException {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    String path = packageName.replace('.', '/');
    Enumeration<URL> resources = classLoader.getResources(path);
    List<File> dirs = new ArrayList();
    while (resources.hasMoreElements()) {
      URL resource = resources.nextElement();
      URI uri = new URI(resource.toString());
      dirs.add(new File(uri.getPath()));
    }
    List<Class> classes = new ArrayList();
    for (File directory : dirs) {
      classes.addAll(findClasses(directory, packageName));
    }
    return classes;
  }


  private List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
    List<Class> classes = new ArrayList();
    if (!directory.exists()) {
      return classes;
    }
    File[] files = directory.listFiles();
    for (File file : files) {
      if (file.isDirectory()) {
        classes.addAll(findClasses(file, packageName + "." + file.getName()));
      } else if (file.getName().endsWith(".class")) {
        classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
      }
    }
    return classes;
  }

}

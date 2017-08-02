package dev.local.java.samples.app.fabric;

import dev.local.java.samples.app.fabric.annotations.AppBean;
import dev.local.java.samples.app.fabric.annotations.BeanDestroy;
import dev.local.java.samples.app.fabric.annotations.BeanInit;
import dev.local.java.samples.app.fabric.exceptions.AppFabricExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AppFabric {

  private static final Logger log = LoggerFactory.getLogger(AppFabric.class);

  private static final String CLASS_FILE_SUFFIX = ".class";

  private static AppFabric instance;
  private Map<String, Object> beans;

  private AppFabric() throws AppFabricExceptions, NoSuchFieldException, IllegalAccessException,
      URISyntaxException, IOException, InvocationTargetException, InstantiationException, NoSuchMethodException, ClassNotFoundException {

    beans = new HashMap<>();
    String scanPath = AppProperties.getProp("scan.package");
    String pathDec;
    File jarFile;
    List<Class<?>> classes = new ArrayList<>();

    if (scanPath == null) {
      throw new AppFabricExceptions("can't find value for 'scan.package'");
    }

//    if (ClassLoader.getSystemClassLoader().getResource("") == null) {
    if (AppFabric.class.getClassLoader().getResource("") == null) {

      pathDec = URLDecoder.decode(AppFabric.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");

      ClassLoader loader = AppFabric.class.getClassLoader();

//      System.err.println("2: " + ClassLoader.getSystemClassLoader().getResource("").getPath());//null
      jarFile = new File(pathDec);

      URL[] urls = {new URL("jar:" + jarFile.toURI().toURL() + "!/")};

      try (
          FileInputStream fileInputStream = new FileInputStream(pathDec);
          ZipInputStream zip = new ZipInputStream(fileInputStream);
          URLClassLoader ucl = new URLClassLoader(urls)
      ) {
        String scanPathTmp = scanPath.replace('.', '/');
        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
          if (!entry.isDirectory() && entry.getName().startsWith(scanPathTmp)
              && entry.getName().endsWith(CLASS_FILE_SUFFIX)) {
            createBean(entry.getName(), classes, ucl);
          }
        }
      }
    } else {
      ClassLoader loader = AppFabric.class.getClassLoader();

      //find classes in folder
//      jarFile = new File(ClassLoader.getSystemClassLoader().getResource("").getPath());
      jarFile = new File(AppFabric.class.getClassLoader().getResource("").getPath());
      File[] files = jarFile.listFiles();
      for (File file : files) {
        classes.addAll(find(file, file.getName(), scanPath));
      }
    }

    for (Class<?> bean : classes) {
      if (bean.isAnnotationPresent(AppBean.class)) {
        String beanName = bean.getSimpleName().substring(0, 1).toLowerCase() + bean.getSimpleName().substring(1);
        Constructor constructor = bean.getConstructor(new Class[]{});
        beans.put(beanName, constructor.newInstance());
      }
    }

    log.debug("init beans: {}", beans.keySet());
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
   * @param aClass
   * @param <T>
   * @return
   * @throws AppFabricExceptions
   */
  public static <T> T getBean(Class aClass) throws AppFabricExceptions {
    return (T) getBean(aClass.getSimpleName().substring(0, 1).toLowerCase() + aClass.getSimpleName().substring(1));
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
      initFabric();
    }

    if (instance.beans.containsKey(name)) {
      return instance.beans.get(name);
    }

    throw new AppFabricExceptions("AppFabric not contain bean with name: " + name);
  }

  public static void initFabric() throws AppFabricExceptions {

    try {
      long start = System.currentTimeMillis();
      instance = new AppFabric();
      instance.init();
      long started = System.currentTimeMillis() - start;

      if (AppProperties.getProp("banner") != null && AppProperties.getProp("banner").equals("true")) {
        log.info("   ###    ########  ########  ########    ###    ########  ########  ####  ######  ");
        log.info("  ## ##   ##     ## ##     ## ##         ## ##   ##     ## ##     ##  ##  ##    ## ");
        log.info(" ##   ##  ##     ## ##     ## ##        ##   ##  ##     ## ##     ##  ##  ##       ");
        log.info("##     ## ########  ########  ######   ##     ## ########  ########   ##  ##       ");
        log.info("######### ##        ##        ##       ######### ##     ## ##   ##    ##  ##       ");
        log.info("##     ## ##        ##        ##       ##     ## ##     ## ##    ##   ##  ##    ## ");
        log.info("##     ## ##        ##        ##       ##     ## ########  ##     ## ####  ######  ");
      }

      log.info("AppFabric init on {} ms, initialized {} beans", started, instance.beans.size());
    } catch (Exception e) {
      throw new AppFabricExceptions("error on init AppFabric: " + e);
    }
  }

  /* Class util methods */
  private List<Class<?>> find(File file, String scannedPackage, String scanPath) {

    List<Class<?>> classes = new ArrayList();

    if (file.isDirectory()) {
      for (File child : file.listFiles()) {
        String path = scannedPackage + "." + child.getName();
        classes.addAll(find(child, path, scanPath));
      }
    } else if (scannedPackage.endsWith(CLASS_FILE_SUFFIX) && scannedPackage.startsWith(scanPath)) {
      int endIndex = scannedPackage.length() - CLASS_FILE_SUFFIX.length();
      String className = scannedPackage.substring(0, endIndex).replace('/', '.');
      try {
//        Class bean = ClassLoader.getSystemClassLoader().loadClass(className);
        Class bean = AppFabric.class.getClassLoader().loadClass(className);
        if (bean.isAnnotationPresent(AppBean.class)) {
          classes.add(bean);
        }
      } catch (Exception e) {
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

  private void createBean(String path, List<Class<?>> classes, URLClassLoader ucl) {
    try {
      int endIndex = path.length() - CLASS_FILE_SUFFIX.length();
      String className = path.substring(0, endIndex).replace('/', '.');
      Class<?> bean = Class.forName(className, true, ucl);
      classes.add(bean);
    } catch (Exception e) {
      log.error("create bean error: {}", e);
    }
  }
}

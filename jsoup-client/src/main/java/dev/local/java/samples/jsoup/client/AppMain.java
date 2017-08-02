package dev.local.java.samples.jsoup.client;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class AppMain {

  private static final Logger log = LoggerFactory.getLogger(AppMain.class);
  private static final String URL_MAIN = "http://localhost";

  private Set<String> paginator = new HashSet<>();
  private Set<String> paginatorPageParsed = new HashSet<>();
  private Set<String> pagesUrls = new HashSet<>();

  private File uploadFolder;

  public static void main(String[] args) throws IOException {

    AppMain main = new AppMain();
    main.parseCategoryPage(URL_MAIN);

    main.uploadFolder = new File("downloads");

    if (main.uploadFolder == null) {
      main.uploadFolder.mkdir();
    }

    while (true) {
      log.info("step {}, {}, {}", main.paginator.size(), main.paginatorPageParsed.size(),
          main.paginatorPageParsed.containsAll(main.paginator));

      boolean end = true;
      Set<String> tmp = new HashSet<>(main.paginator);

      for (String url : tmp) {
        if (!main.paginatorPageParsed.contains(url)) {
          end = false;
          main.parseCategoryPage(url);
        }
      }
      if (end) break;
    }

    log.info("pages: {}", main.pagesUrls);

    for (String url : main.pagesUrls) {
      main.loadPdf(url);
    }

  }

  private void parseCategoryPage(String url) {

    log.info("parse: {}", url);

    try {
      Connection.Response responseMain = response(url);

      if (responseMain == null) return;

      Elements elementsPages = responseMain.parse().select(".post h2 a");
      Elements elementsPagination = responseMain.parse().select(".pagination a");

      for (Element element : elementsPages) {
        pagesUrls.add(element.attr("href"));
      }

      for (Element element : elementsPagination) {
        paginator.add(element.attr("href"));
      }

      paginatorPageParsed.add(url);

    } catch (Exception e) {
      log.error("{}", e);
    }
  }

  private Connection.Response response(String url) {
    try {
      return Jsoup.connect(url)
          .timeout(30000)
          .followRedirects(true)
          .ignoreContentType(true)
          .ignoreHttpErrors(true)
          .method(Connection.Method.GET)
          .execute();
    } catch (Exception e) {
      log.error("{}", e);
    }
    return null;
  }

  private void loadPdf(String parseUrl) {
    Connection.Response response = response(parseUrl);

    InputStream is = null;
    OutputStream os = null;
    try {
      Elements elements = response.parse().select("footer .download-links a");

      Element element = elements.get(0);

      if (element == null) {
        log.error("element null, {}", parseUrl);
        return;
      }


      String loadUrl = element.attr("href");
      String title = response.parse().select("h1.single-title").get(0).text();

      log.info("title: {}", title);

      URL url = new URL(loadUrl);
      String destName = uploadFolder.getAbsolutePath() + File.separator + title + ".pdf";

      File[] files = uploadFolder.listFiles();


      String tmpTitle = title + ".pdf";
      for (File file : files) {
        if (file.getName().equals(tmpTitle)) {
          log.info("downloaded later: {}", title);
          return;
        }

      }

      is = url.openStream();
      os = new FileOutputStream(destName);


      byte[] b = new byte[2048];
      int length;

      while ((length = is.read(b)) != -1) {
        os.write(b, 0, length);
      }


    } catch (Exception e) {
      log.error("{}", e);
    } finally {
      close(is, os);
    }
  }

  private void close(Closeable... closeables) {
    for (Closeable closeable : closeables) {
      if (closeable != null) {
        try {
          closeable.close();
        } catch (Exception e) {
          log.error("{}", e);
        }
      }
    }
  }

}
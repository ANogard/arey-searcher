package ru.skillbox.areysearcher.service.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RecursiveTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.skillbox.areysearcher.service.Constants;

public class Crawler extends RecursiveTask<Set<String>> {

  private static final Set<String> pages = new HashSet<>();
  private static String root;
  private final String path;

  public Crawler(String path) {
    this.path = path;
    if (root == null) {
      root = CrawlerUtils.getRootPath(path);
    }
  }

  public static void flush() {
    Crawler.pages.clear();
    root = null;
  }

  @Override
  protected Set<String> compute() {
    Set<String> out = new HashSet<>();
    List<Crawler> tasks = new ArrayList<>();
    List<String> linksOnCurrentPage = new ArrayList<>();

    String pagePath = CrawlerUtils.getRelativePath(path, root);
    Crawler.pages.add(pagePath);

    Response response = null;
    try {
      Thread.sleep(500);
      response = Jsoup.connect(path).userAgent(Constants.USER_AGENT).timeout(10000).execute();
    } catch (HttpStatusException e) {
      out.add(pagePath);
    } catch (InterruptedException | IOException e) {
      e.printStackTrace();
    }

    if (response != null) {
      try {
        Document doc = response.parse();
        linksOnCurrentPage.addAll(getLinkListFromDocument(doc));
        out.add(pagePath);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (!linksOnCurrentPage.isEmpty()) {
      for (String url : linksOnCurrentPage) {
        Crawler task = new Crawler(root + url);
        task.fork();
        tasks.add(task);
      }
    }
    for (Crawler task : tasks) {
      out.addAll(task.join());
    }
    return out;
  }

  /* Возвращает url, в приведенном виде,
  фильтрует ссылки на внутренние элементы и файлы,
  добавляет хост для ссылок с абсолютным путём,
  добавляет префикс к относительному пути. */
  private String validateLink(String url) {
    if (url.equals(path)) {
      return "/";
    }

    Pattern extensions = Pattern.compile(".+\\.(" +
        "(jpg)|(jpeg)|(gif)|(png)|(bmp)" +
        "|(flv)|(avi)|(mp4)|(mp3)|(wav)" +
        "|(txt)|(pdf)|(doc[x]?)|(xls[x]?)|(xml)" +
        "|(rar)|(zip)" +
        ")$");
    Pattern contact = Pattern.compile(".*(tel:)|(mailto:).*");
    Matcher files = extensions.matcher(url);
    Matcher contacts = contact.matcher(url);

    url = CrawlerUtils.getRelativePath(url, root);

    if (files.find() || contacts.find() || url.contains("#")) {
      return "";
    } else if (!url.startsWith("/")) {
      String pageRelative = CrawlerUtils.getRelativePath(path, root);
      if (pageRelative.equals("/")) {
        url = "/" + url;
      } else {
        url = pageRelative + "/" + url;
      }
    }
    return (url.endsWith("/") && url.length() > 1) ? url.substring(0, url.length() - 1) : url;
  }

  private List<String> getLinkListFromDocument(Document document) {
    Elements urls = document.getElementsByTag("a");
    List<String> out = new ArrayList<>();
    for (Element url : urls) {
      String elementUrl = validateLink(url.attr("href"));
      if (!elementUrl.isEmpty() && !Crawler.pages.contains(elementUrl)) {
        out.add(elementUrl);
        Crawler.pages.add(elementUrl);
      }
    }
    return out;
  }
}

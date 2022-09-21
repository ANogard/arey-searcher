package ru.skillbox.areysearcher.service.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import ru.skillbox.areysearcher.model.entity.Page;
import ru.skillbox.areysearcher.service.Constants;

public class Crawler extends RecursiveTask<Map<Page, Map<String, List<String>>>> {

  private static final Set<String> pages = new HashSet<>();
  private static String root;
  private final String path;

  public Crawler(String path) {
    this.path = path;
    if (root == null) {
      root = getRootPath(path);
    }
  }

  public static void flush() {
    Crawler.pages.clear();
    root = null;
  }

  //Выводит коллекцию со страницами и списком исходных слов, объединенных по полям
  @Override
  protected Map<Page, Map<String, List<String>>> compute() {
    Map<Page, Map<String, List<String>>> out = new HashMap<>();
    List<Crawler> tasks = new ArrayList<>();
    List<String> linksOnCurrentPage = new ArrayList<>();
    String pagePath = getRelativePath(path);

    Page page = new Page(pagePath);
    Crawler.pages.add(pagePath);
    Response response = null;
    try {
      Thread.sleep(500);
      response = Jsoup.connect(path).userAgent(Constants.USER_AGENT).timeout(10000).execute();
    } catch (HttpStatusException e) {
      page.setCode(e.getStatusCode());
      page.setContent("");
      out.put(page, null);
    } catch (InterruptedException | IOException e) {
      e.printStackTrace();
    }

    if (response != null) {
      try {
        Document doc = response.parse();
        Map<String, List<String>> wordsByFields = new HashMap<>();

        List<String> titleWords =
            new ArrayList<>(Arrays.asList
                (doc.getElementsByTag("title").text().split("\\s")));
        wordsByFields.put("title", titleWords);

        List<String> bodyWords = getWordListFromDocument(doc);
        wordsByFields.put("body", bodyWords);
        linksOnCurrentPage.addAll(getLinkListFromDocument(doc));

        page.setCode(response.statusCode());
        page.setContent(doc.html());
        out.put(page, wordsByFields);
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
      out.putAll(task.join());
    }
    return out;
  }

  public Map<Page, Map<String, List<String>>> crawlPage() {
    Map<Page, Map<String, List<String>>> out = new HashMap<>();
    Page page = new Page(getRelativePath(path));

    Response response = null;
    try {
      Thread.sleep(500);
      response = Jsoup.connect(path).userAgent(Constants.USER_AGENT).timeout(10000).execute();
    } catch (HttpStatusException e) {
      page.setCode(e.getStatusCode());
      page.setContent("");
      out.put(page, null);
    } catch (InterruptedException | IOException e) {
      e.printStackTrace();
    }

    if (response != null) {
      try {
        Document doc = response.parse();
        Map<String, List<String>> wordsByFields = new HashMap<>();

        List<String> titleWords =
            new ArrayList<>(Arrays.asList
                (doc.getElementsByTag("title").text().split("\\s")));
        wordsByFields.put("title", titleWords);

        List<String> bodyWords = getWordListFromDocument(doc);
        wordsByFields.put("body", bodyWords);

        page.setCode(response.statusCode());
        page.setContent(doc.html());
        out.put(page, wordsByFields);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    root = null;

    return out;
  }

  /* Возвращает url, в приведенном виде,
  фильтрует ссылки на внутренние элементы и файлы,
  добавляет хост для ссылок с абсолютным путём,
  добавляет префикс к относительному пути.
  */
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

    url = getRelativePath(url);

    if (files.find() || contacts.find() || url.contains("#")) {
      return "";
    } else if (!url.startsWith("/")) {
      String pageRelative = getRelativePath(path);
      if (pageRelative.equals("/")) {
        url = "/" + url;
      } else {
        url = pageRelative + "/" + url;
      }
    }
    return (url.endsWith("/") && url.length() > 1) ? url.substring(0, url.length() - 1) : url;
  }

  private String getRelativePath(String url) {
    Pattern siteRoot = Pattern.compile("((http[s]?://)([\\w-\\.]+\\.\\w+))(.*)");
    Matcher root = siteRoot.matcher(url);
    if (root.find()) {
      if (!(root.group(1)).equals(Crawler.root)) {
        return "#";
      } else {
        return root.group(4);
      }
    } else {
      return url;
    }
  }

  public String getRootPath(String url) {
    Pattern siteRoot = Pattern.compile("(http[s]?://[\\w-\\.]+\\.\\w+)(.*)");
    Matcher root = siteRoot.matcher(url);
    return (root.find()) ? root.group(1) : "";
  }

  private List<String> getWordListFromDocument(Document document) {
    Elements bodyElements = document.getElementsByTag("body").first().getAllElements();
    List<String> bodyWords = new ArrayList<>();
    for (Element element : bodyElements) {
      if ((element.tagName().equals("a") || element.tagName().equals("span") ||
          element.tagName().equals("p") || element.tagName().equals("img")) ||
          element.tagName().equals("div")
              && element.hasText()) {
        bodyWords.addAll(Arrays.asList(element.text().split("\\s")));
      }
    }
    return bodyWords;
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

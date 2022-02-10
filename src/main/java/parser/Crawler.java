package parser;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler extends RecursiveTask<Set<String>> {

  private static final long SerialVersionUID = 1L;
  private static final Set<String> pages = new HashSet<>();
  private static String root;
  private final String path;

  public Crawler(String path){
    this.path = path;
    if(root == null){
      root = path;
    }
  }

  @Override
  protected Set<String> compute() {
    Set<String> pages = new HashSet<>(); //Выводимая коллекция
    List<Crawler> tasks = new ArrayList<>();
    List<String> currentLinks = new ArrayList<>();

    try {
      Thread.sleep(500);
      Connection.Response response = Jsoup.connect(path)
              .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.81 Safari/537.36")
              .timeout(10000)
              .execute();

      Document doc = response.parse();
      int statusCode = response.statusCode();
      String content = doc.html();

      Page page = new Page(path, statusCode, content);

      Elements urls = doc.getElementsByTag("a");

      for(Element url : urls){
        String elementUrl = validateLink(url.attr("href"));
        if(!elementUrl.isEmpty() && !Crawler.pages.contains(elementUrl)) {
          currentLinks.add(elementUrl);
          pages.add(elementUrl);
          Crawler.pages.add(elementUrl);
        }
      }
    }
    catch (Exception e){
      e.printStackTrace(); //TODO: добавить логгирование
    }
    if(!currentLinks.isEmpty()){
      for(String url : currentLinks){
        Crawler task = new Crawler(path + url); //TODO: Определить получение префикса
        task.fork();
        tasks.add(task);
      }
    }
    for(Crawler task : tasks){
      pages.addAll(task.join());
    }
    return pages;
  }

  /* Возвращает url, в приведенном виде,
  фильтрует ссылки на внутренние элементы и файлы,
  добавляет хост для ссылок с абсолютным путём,
  добавляет префикс к относительному пути.
  */
  private String validateLink (String url){
    Pattern pageExt = Pattern.compile(".+\\.(" +
            "(jpg)|(jpeg)|(gif)|(png)|(bmp)" +
            "|(flv)|(avi)|(mp4)|(mp3)|(wav)" +
            "|(txt)|(pdf)|(doc[x]?)|(xls[x]?)|(xml)" +
            ")$");
    Pattern siteRoot = Pattern.compile("(http[s]?://[\\w-]+\\.\\w+)(.*)");
    Pattern contact = Pattern.compile(".*(tel:)|(mailto:).*");
    Matcher files = pageExt.matcher(url);
    Matcher root = siteRoot.matcher(url);
    Matcher contacts = contact.matcher(url);

    //Отсечение корня
    if(root.find()){
      if(!root.group(1).equals(Crawler.root))
      {
        url = "";
      } else {
        url = root.group(2);
      }
    }

    if(files.find() || url.isEmpty() || contacts.find() || url.matches(".*#.*")){
      return "";
    }
    else if(!url.startsWith("/")){
      url = "/" + url;
    }
    return (url.endsWith("/")) ? url.substring(0, url.length() - 1) : url;
  }
}

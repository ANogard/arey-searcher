package parser;

import entity.Field;
import entity.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import service.HibernateSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler extends RecursiveTask<Map<Page, Map<Field, List<String>>>> {

  private static final long SerialVersionUID = 1L;
  private static final Set<String> pages = new HashSet<>();
  private static String root;
  private static final Logger LOGGER = LogManager.getLogger();

  private final String path;

  public Crawler(String path){
    this.path = path;
    if(root == null){
      root = path;
    }
  }

  @Override
  protected Map<Page, Map<Field, List<String>>>  compute() {
    Map<Page, Map<Field, List<String>>> pages = new HashMap<>(); //Выводимая коллекция
    List<Crawler> tasks = new ArrayList<>();

    List<String> linksOnCurrentPage = new ArrayList<>();
    Page page = new Page(path);
    Map<Field, List<String>> wordsByFields = new HashMap<>();

    if(!Crawler.pages.contains(page.getPath())) {
      Connection.Response response = null;
      int statusCode = 0;
      String content = null, pageURL = null;

      try {
        Thread.sleep(500);
        response = Jsoup.connect(path)
                .userAgent(
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.81 Safari/537.36")
                .timeout(10000)
                .execute();
      } catch (HttpStatusException e) {
        LOGGER.catching(e);
        statusCode = e.getStatusCode();
        page.setCode(statusCode);
        page.setContent("");

        Session session = HibernateSession.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(page);
        transaction.commit();
        session.close();
      } catch (InterruptedException | IOException e){
        LOGGER.catching(e);
      }

      if(response != null){
        try {
          Document doc = response.parse();
          statusCode = response.statusCode();
          content = doc.html();
          pageURL = validateLink(path);
          LOGGER.info("Get page at: '" + pageURL + "', Status: " + statusCode);

          Elements urls = doc.getElementsByTag("a");
          Elements title = doc.getElementsByTag("title");

          Elements bodyElements = doc.getElementsByTag("body").first().getAllElements();

          Session session = HibernateSession.getSessionFactory().openSession();
          Field titleField = (Field) session.createQuery("FROM " + Field.class.getSimpleName() + " WHERE selector = 'title'").uniqueResult();
          Field bodyField = (Field) session.createQuery("FROM " + Field.class.getSimpleName() + " WHERE selector = 'body'").uniqueResult();
          session.close();

          List<String> titleWords = new ArrayList<>(Arrays.asList(title.text().split("\\s")));
          wordsByFields.put(titleField, titleWords);

          List<String> bodyWords = new ArrayList<>();
          for(Element element : bodyElements){
            if((element.tagName().equals("a") || element.tagName().equals("p") || element.tagName().equals("img")) && element.hasText()) {
              bodyWords.addAll(Arrays.asList(element.text().split("\\s")));
            }
          }
          wordsByFields.put(bodyField, bodyWords);

          for (Element url : urls) {
            String elementUrl = validateLink(url.attr("href"));
            if (!elementUrl.isEmpty() && !Crawler.pages.contains(elementUrl)) {
              linksOnCurrentPage.add(elementUrl);
              Crawler.pages.add(elementUrl);
            }
          }

        } catch (IOException e) {
          e.printStackTrace();
        }
        if (pageURL != null && !pageURL.isEmpty() && !pageExists(page.getPath())) {
          page.setCode(statusCode);
          page.setContent(content);

          Session session = HibernateSession.getSessionFactory().openSession();
          Transaction transaction = session.beginTransaction();
          session.save(page);
          transaction.commit();
          session.close();

          pages.put(page, wordsByFields);
        }
      }
    }

    if(!linksOnCurrentPage.isEmpty()){
      for(String url : linksOnCurrentPage){
        Crawler task = new Crawler(root + url);
        task.fork();
        tasks.add(task);
      }
    }

    for(Crawler task : tasks){
      pages.putAll(task.join());
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

    Pattern contact = Pattern.compile(".*(tel:)|(mailto:).*");
    Matcher files = pageExt.matcher(url);
    Matcher contacts = contact.matcher(url);

    url = removeRoot(url);

    if(files.find() || url.isEmpty() || contacts.find() || url.matches(".*#.*")){
      return "";
    }
    else if(!url.startsWith("/")){
      url = removeRoot(path) + "/" + url;
    }
    return (url.endsWith("/")) ? url.substring(0, url.length() - 1) : url;
  }

  private String removeRoot(String url){
    Pattern siteRoot = Pattern.compile("(http[s]?://[\\w-]+\\.\\w+)(.*)");
    Matcher root = siteRoot.matcher(url);

    if(root.find()){
      if(root.group(1).equals(Crawler.root)) {
        return root.group(2);
      }
      else {
        return "";
      }
    }
    return url;
  }

  private static boolean pageExists(String page){
    Session session = HibernateSession.getSessionFactory().openSession();
    boolean out = !session.createQuery("FROM Page P WHERE P.path = '" + page + "'").list().isEmpty();
    session.close();
    return out;
  }
}

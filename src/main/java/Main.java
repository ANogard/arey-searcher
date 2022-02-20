import java.util.Map;
import morpology.Lemm;
import org.hibernate.Session;
import org.hibernate.Transaction;
import parser.Crawler;
import parser.Page;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

public class Main {
  private static final String PATH = "https://severts.ru";

  public static void main(String[] args) {
    //Проверка парсера
    Set<Page> list = new ForkJoinPool().invoke(new Crawler(PATH));
    try(Session session = HibernateSession.getSessionFactory().openSession()) {
      Transaction transaction = session.beginTransaction();
      for (Page page : list) {
        session.save(page);
      }
      transaction.commit();
    }

    // Проверка лемматизации
    Lemm lemm = new Lemm();
    Map<String, Integer> words = lemm.getEntries("");
    for(Map.Entry<String, Integer> word : words.entrySet()){
      System.out.println(word.getKey() + " - " + word.getValue());
    }
  }
}

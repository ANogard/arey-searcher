import org.hibernate.Session;
import org.hibernate.Transaction;
import parser.Crawler;
import parser.Page;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

public class Main {
  private static final String PATH = "";

  public static void main(String[] args) {
    Set<Page> list = new ForkJoinPool().invoke(new Crawler(PATH));
    try(Session session = HibernateSession.getSessionFactory().openSession()) {
      Transaction transaction = session.beginTransaction();
      for (Page page : list) {
        session.save(page);
      }
      transaction.commit();
    }
  }
}

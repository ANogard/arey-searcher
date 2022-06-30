import entity.Field;
import entity.Index;
import entity.Lemma;
import entity.Page;
import morphology.Lemm;
import org.hibernate.Session;
import org.hibernate.Transaction;
import parser.Crawler;
import service.HibernateSession;

import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class Main {
  private static final String PATH = "https://severts.ru";

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    while(true){
      String input = scanner.nextLine();
    }
  }

  private static Set<Lemma> getLemmasFromInput(String text){
    Lemm lemm = new Lemm();

  }

  private static Map<Page, Map<Field, Map<Lemma, Integer>>> stringToLemmas(Map<Page, Map<Field, List<String>>> content){
    Lemm lemm = new Lemm();
    Map<Page, Map<Field, Map<Lemma, Integer>>> contentLemma = new HashMap<>();
    for(Map.Entry<Page, Map<Field, List<String>>> page : content.entrySet()){
      Map<Field, Map<Lemma, Integer>> lemmasByPage = new HashMap<>();
      Map<Index, Float> indexRank = new HashMap<>();

      for(Map.Entry<Field, List<String>> field : page.getValue().entrySet()) {
        Map<Lemma, Integer> lemmasInField = new HashMap<>(lemm.getLemmasFromText(field.getValue()));
        for(Map.Entry<Lemma, Integer> lemma : lemmasInField.entrySet()){
          Index index = new Index(page.getKey().getId(), lemma.getKey().getId());
          float rank = 0;
          if(indexRank.containsKey(index)){
            rank += field.getKey().getWeight() * lemma.getValue();
          } else {
            rank = field.getKey().getWeight() * lemma.getValue();
          }
          indexRank.put(index, rank);
        }
        lemmasByPage.put(field.getKey(), lemmasInField);
      }
      contentLemma.put(page.getKey(), lemmasByPage);

      Session session = HibernateSession.getSessionFactory().openSession();
      for(Map.Entry<Index, Float> index : indexRank.entrySet()) {
        Transaction transaction = session.beginTransaction();
        Index currentIndex = index.getKey();
        currentIndex.setRank(index.getValue());
        session.save(currentIndex);
        transaction.commit();
      }
      session.close();
    }
    return contentLemma;
  }

  private static Map<Page, Map<Field, List<String>>> crawl(){
    return new ForkJoinPool().invoke(new Crawler(PATH));
  }

  private static void setFields(){
    Session session = HibernateSession.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();
    Field field1 = new Field("title", "title", 1);
    Field field2 = new Field("body", "body", 0.8f);
    session.save(field1);
    session.save(field2);
    transaction.commit();
    session.close();
  }
}

package morphology;

import java.io.IOException;
import java.util.*;

import entity.Lemma;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.hibernate.Session;
import org.hibernate.Transaction;
import service.HibernateSession;

public class Lemm {
  private LuceneMorphology luceneMorphology;

  public Lemm(){
    try {
      luceneMorphology = new RussianLuceneMorphology();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //Подсчет количества вхождений слов текста
  public Map<Lemma, Integer> getLemmasFromText(String text){
    List<String> words = getNormalFormsFromText(text);
    Map<Lemma, Integer> out = new HashMap<>();

    for(String word : words){
      List<String> wordInfo = luceneMorphology.getMorphInfo(word);
      if(wordInfo.get(0).contains("l ПРЕДЛ") || wordInfo.get(0).contains("o МЕЖД") ||
          wordInfo.get(0).contains("n СОЮЗ") ||wordInfo.get(0).contains("p ЧАСТ")) {
        continue;
      }

      String entry = wordInfo.get(0).split("\\|")[0];

      Session session = HibernateSession.getSessionFactory().openSession();
      Transaction transaction = session.beginTransaction();

      Lemma lemma;
      if(lemmaExists(entry)){
        lemma = (Lemma) session.createQuery("FROM Lemma L WHERE L.lemma = '" + entry + "'").getSingleResult();
        lemma.setFrequency(lemma.getFrequency() + 1);
      } else {
        lemma = new Lemma(entry, 1);
      }

      session.save(lemma);
      transaction.commit();
      session.close();
      out.put(lemma, Collections.frequency(words, word));
    }
    return out;
  }

  public Set<Lemma> getLemmasFromInput

  public Map<Lemma, Integer> getLemmasFromText(List<String> words){
    StringBuilder builder = new StringBuilder();
    for(String word : words){
      builder.append(word).append(" ");
    }
    return getLemmasFromText(builder.toString());
  }

  private List<String> getNormalFormsFromText(String text){
    String[] words = text.split("\\s");
    List<String> normalForms = new ArrayList<>();
    for(String word : words){
      try {
        word = word.toLowerCase(Locale.ROOT).replaceAll("\\p{Punct}", "");
        List<String> morph = luceneMorphology.getNormalForms(word);
        if(morph.isEmpty()) continue;
        normalForms.add(morph.get(0));
      } catch (Exception e){
        System.err.println(e.getClass().getSimpleName() + " - " + word);
        System.err.println("\t" + e.getMessage());
      }
    }
    return normalForms;
  }

  private static boolean lemmaExists(String lemma){
    Session session = HibernateSession.getSessionFactory().openSession();
    boolean out = !session.createQuery("FROM Lemma L WHERE L.lemma = '" + lemma + "'").list().isEmpty();
    session.close();
    return out;
  }
}

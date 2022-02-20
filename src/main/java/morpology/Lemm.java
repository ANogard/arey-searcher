package morpology;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

public class Lemm {
  private LuceneMorphology luceneMorphology;

  public Lemm(){
    try {
      luceneMorphology = new RussianLuceneMorphology();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private List<String> getLems(String text){
    String[] words = text.split("\\s");
    List<String> normalForms = new ArrayList<>();
    for(String word : words){
      word = word.toLowerCase(Locale.ROOT).replaceAll("\\p{Punct}", "");
      List<String> morph = luceneMorphology.getNormalForms(word);
      normalForms.add(morph.get(0));
    }
    return normalForms;
  }

  //Подсчет количества вхождений слов текста
  public Map<String, Integer> getEntries(String text){
    List<String> words = getLems(text);
    Map<String, Integer> out = new HashMap<>();
    for(String word : words){
      List<String> wordInfo = luceneMorphology.getMorphInfo(word);
      if(wordInfo.get(0).contains("l ПРЕДЛ") || wordInfo.get(0).contains("o МЕЖД") ||
          wordInfo.get(0).contains("n СОЮЗ") ||wordInfo.get(0).contains("p ЧАСТ")) {
        continue;
      }
      String entry = wordInfo.get(0).split("\\|")[0];
      if(out.containsKey(entry)){
        out.put(entry, out.get(entry) + 1);
      } else {
        out.put(entry, 1);
      }
    }
    return out;
  }
}

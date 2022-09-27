package ru.skillbox.areysearcher.service.morphology;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.WrongCharaterException;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.dao.DataAccessException;
import ru.skillbox.areysearcher.model.entity.Lemma;
import ru.skillbox.areysearcher.repository.LemmaRepository;

public class Lemm {

  private final LemmaRepository lemmaRepository;
  private final int siteId;
  private LuceneMorphology luceneMorphology;

  public Lemm(LemmaRepository lemmaRepository, int siteId) {
    try {
      luceneMorphology = new RussianLuceneMorphology();
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.lemmaRepository = lemmaRepository;
    this.siteId = siteId;
  }

  public Map<Lemma, Integer> getLemmasFromText(List<String> text) {
    StringBuilder builder = new StringBuilder();
    for (String word : text) {
      builder.append(word).append(" ");
    }
    List<String> words = getNormalFormsFromText(builder.toString());
    Map<Lemma, Integer> out = new HashMap<>();
    for (String word : words) {
      List<String> wordInfo = luceneMorphology.getMorphInfo(word);
      if (wordInfo.get(0).contains("l ПРЕДЛ") || wordInfo.get(0).contains("o МЕЖД") ||
          wordInfo.get(0).contains("n СОЮЗ") || wordInfo.get(0).contains("p ЧАСТ") ||
          wordInfo.get(0).contains("a ДЕЕПРИЧАСТИЕ")) {
        continue;
      }
      Lemma lemma = lemmaRepository.getLemmaOrSave(
          new Lemma(wordInfo.get(0).split("\\|")[0], siteId));
      out.put(lemma, Collections.frequency(words, word));
    }
    return out;
  }

  public List<Lemma> searchLemmas(String text) {
    List<String> words = getNormalFormsFromText(text);
    List<Lemma> out = new ArrayList<>();
    for (String word : words) {
      List<String> wordInfo = luceneMorphology.getMorphInfo(word);
      if (wordInfo.get(0).contains("l ПРЕДЛ") || wordInfo.get(0).contains("o МЕЖД") ||
          wordInfo.get(0).contains("n СОЮЗ") || wordInfo.get(0).contains("p ЧАСТ") ||
          wordInfo.get(0).contains("a ДЕЕПРИЧАСТИЕ")) {
        continue;
      }
      try {
        Lemma lemma = lemmaRepository.get(new Lemma(wordInfo.get(0).split("\\|")[0], siteId));
        out.add(lemma);
      } catch (DataAccessException e) {
        System.err.println("Lemma '" + wordInfo.get(0).split("\\|")[0] + "' not found.");
      }
    }
    Collections.sort(out);
    return out;
  }

  private List<String> getNormalFormsFromText(String text) {
    String[] words = text.split("\\s");
    List<String> normalForms = new ArrayList<>();
    for (String word : words) {
      word = word.toLowerCase(Locale.ROOT).replaceAll("\\p{Punct}", "");
      if (word.isEmpty()) {
        continue;
      }
      try {
        List<String> morph = luceneMorphology.getNormalForms(word);
        normalForms.add(morph.get(0));
      } catch (WrongCharaterException ignored) {
      }
    }
    return normalForms;
  }
}

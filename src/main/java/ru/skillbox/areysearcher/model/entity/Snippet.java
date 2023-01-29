package ru.skillbox.areysearcher.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Snippet {

  private Integer id;
  private Integer pageId;
  private List<String> lemmas;
  private List<String> words;

  public Snippet(List<String> lemmas, List<String> words, Integer pageId) {
    this.lemmas = lemmas;
    this.words = words;
    this.pageId = pageId;
  }
}

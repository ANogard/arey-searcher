package ru.skillbox.areysearcher.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Lemma {

  private Integer id;
  private String lemma;
  private Integer frequency;
  private Integer siteId;

  public Lemma(String lemma, Integer siteId) {
    this.lemma = lemma;
    this.siteId = siteId;
    frequency = 0;
  }
}

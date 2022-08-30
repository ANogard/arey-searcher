package ru.skillbox.areysearcher.model.entity;

import lombok.Data;

@Data
public class Lemma {
  private Integer id;
  private String lemma;
  private Integer frequency;
  private Integer siteId;
}

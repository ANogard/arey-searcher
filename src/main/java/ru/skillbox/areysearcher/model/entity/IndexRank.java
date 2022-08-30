package ru.skillbox.areysearcher.model.entity;

import lombok.Data;

@Data
public class IndexRank {
  private Integer id;
  private Integer pageId;
  private Integer lemmaId;
  private Float rank;
}

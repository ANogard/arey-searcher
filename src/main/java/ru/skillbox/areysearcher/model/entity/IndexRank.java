package ru.skillbox.areysearcher.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IndexRank {

  private Integer id;
  private Integer pageId;
  private Integer lemmaId;
  private Float rank;

  public IndexRank(Integer pageId, Integer lemmaId) {
    this.pageId = pageId;
    this.lemmaId = lemmaId;
    rank = 0f;
  }
}

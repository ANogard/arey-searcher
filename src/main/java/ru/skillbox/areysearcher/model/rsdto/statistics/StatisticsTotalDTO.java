package ru.skillbox.areysearcher.model.rsdto.statistics;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StatisticsTotalDTO {

  private Integer sites;
  private Integer pages;
  private Integer lemmas;
  private Boolean isIndexing;
}

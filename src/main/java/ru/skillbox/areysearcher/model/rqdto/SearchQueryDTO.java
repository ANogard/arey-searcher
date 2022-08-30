package ru.skillbox.areysearcher.model.rqdto;

import lombok.Data;

@Data
public class SearchQueryDTO {
  private String query;
  private String site;
  private Integer offset;
  private Integer limit;
}

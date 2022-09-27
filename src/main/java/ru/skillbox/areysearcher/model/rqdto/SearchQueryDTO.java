package ru.skillbox.areysearcher.model.rqdto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchQueryDTO {

  private String query;
  private String site;
  private Integer offset;
  private Integer limit;
}

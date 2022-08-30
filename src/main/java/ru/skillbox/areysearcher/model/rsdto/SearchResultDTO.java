package ru.skillbox.areysearcher.model.rsdto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchResultDTO {
  private String site;
  private String siteName;
  private String uri;
  private String title;
  private String snippet;
  private Double relevance;
}
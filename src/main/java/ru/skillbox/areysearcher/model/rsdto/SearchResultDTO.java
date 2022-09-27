package ru.skillbox.areysearcher.model.rsdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.areysearcher.model.entity.Site;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultDTO {

  private String site;
  private String siteName;
  private String uri;
  private String title;
  private String snippet;
  private Double relevance;

  public SearchResultDTO (Site site){
    this.site = site.getUrl();
    siteName = site.getName();
  }
}

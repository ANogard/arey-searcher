package ru.skillbox.areysearcher.model.entity;

import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Site {
  private Integer id;
  private Status status;
  private Date statusTime;
  private String lastError;
  private String url;
  private String name;

  public Site(String url){
    this.url = url;
    lastError = "";
  }

  public Site(String url, String name){
    this(url);
    this.name = name;
  }
}

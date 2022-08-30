package ru.skillbox.areysearcher.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Page {
  private Integer id;
  private String path;
  private Integer code;
  private String content;
  private Integer siteId;

  public Page(String path){
    this.path = path;
  }

  public Page(String path, Integer siteId){
    this(path);
    this.setSiteId(siteId);
  }
}

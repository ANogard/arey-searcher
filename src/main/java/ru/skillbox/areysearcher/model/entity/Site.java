package ru.skillbox.areysearcher.model.entity;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Site {

  private Integer id;
  private Status status;
  private Date statusTime;
  private String lastError;
  private String url;
  private String name;
}

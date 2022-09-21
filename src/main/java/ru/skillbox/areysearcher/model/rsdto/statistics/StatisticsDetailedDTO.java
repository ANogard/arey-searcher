package ru.skillbox.areysearcher.model.rsdto.statistics;

import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.areysearcher.model.entity.Status;

@Data
@NoArgsConstructor
public class StatisticsDetailedDTO {

  private String url;
  private String name;
  private Status status;
  private Date statusTime;
  private String error;
  private Integer pages;
  private Integer lemmas;
}

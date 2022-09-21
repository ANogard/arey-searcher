package ru.skillbox.areysearcher.model.rsdto.statistics;

import java.util.List;
import lombok.Data;

@Data
public class StatisticsDTO {

  private StatisticsTotalDTO total;
  private List<StatisticsDetailedDTO> detailed;
}

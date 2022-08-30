package ru.skillbox.areysearcher.model.rsdto.statistics;

import java.util.List;
import lombok.Data;
import ru.skillbox.areysearcher.model.entity.Site;

@Data
public class StatisticsDTO {
  private StatisticsTotalDTO total;
  private List<Site> detailed;
}

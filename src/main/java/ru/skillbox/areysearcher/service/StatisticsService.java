package ru.skillbox.areysearcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.areysearcher.model.rsdto.statistics.StatisticsDTO;

@Service
@RequiredArgsConstructor
public class StatisticsService {

  public StatisticsDTO getStatistics(){
    return new StatisticsDTO();
  }
}

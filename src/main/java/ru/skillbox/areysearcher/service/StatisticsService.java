package ru.skillbox.areysearcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.areysearcher.model.rsdto.statistics.StatisticsDTO;
import ru.skillbox.areysearcher.repository.SiteRepository;

@Service
@RequiredArgsConstructor
public class StatisticsService {

  private final SiteRepository siteRepository;

  public StatisticsDTO getStatistics() {
    StatisticsDTO statisticsDTO = new StatisticsDTO();
    statisticsDTO.setTotal(siteRepository.getStatisticsTotal());
    statisticsDTO.setDetailed(siteRepository.getStatisticsDetailed());
    return statisticsDTO;
  }
}

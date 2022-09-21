package ru.skillbox.areysearcher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.areysearcher.model.rsdto.StatisticsResponse;
import ru.skillbox.areysearcher.model.rsdto.statistics.StatisticsDTO;
import ru.skillbox.areysearcher.service.StatisticsService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

  private final StatisticsService statisticsService;

  @GetMapping
  public ResponseEntity<StatisticsResponse<StatisticsDTO>> getStatistics() {
    return ResponseEntity.ok(new StatisticsResponse<>(statisticsService.getStatistics()));
  }
}

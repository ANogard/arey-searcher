package ru.skillbox.areysearcher.controller;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.areysearcher.model.rsdto.GeneralResponse;
import ru.skillbox.areysearcher.model.rsdto.SearchResultDTO;
import ru.skillbox.areysearcher.service.SearchService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/search")
public class SearchController {

  private final SearchService searchService;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<GeneralResponse<List<SearchResultDTO>>> search(
      @RequestParam Map<String, Object> params) {

    return ResponseEntity.ok(new GeneralResponse<>(searchService.search(params)));
  }

}

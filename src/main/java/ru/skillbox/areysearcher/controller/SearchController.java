package ru.skillbox.areysearcher.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.areysearcher.model.rqdto.SearchQueryDTO;
import ru.skillbox.areysearcher.model.rsdto.GeneralResponse;
import ru.skillbox.areysearcher.model.rsdto.SearchResultDTO;
import ru.skillbox.areysearcher.service.SearchService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/search")
public class SearchController {

  SearchService searchService;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<GeneralResponse<List<SearchResultDTO>>> search(@RequestBody SearchQueryDTO body){

    return ResponseEntity.ok(new GeneralResponse<>(searchService.search(body)));
  }

}

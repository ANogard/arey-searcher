package ru.skillbox.areysearcher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.areysearcher.model.rsdto.GeneralResponse;
import ru.skillbox.areysearcher.service.IndexService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/")
public class IndexController {

  private final IndexService indexService;

  @GetMapping("startIndexing")
  public ResponseEntity<GeneralResponse<Object>> startIndexing(){
    return ResponseEntity.ok(new GeneralResponse(indexService.startIndexing()));
 }

  @GetMapping("stopIndexing")
  public ResponseEntity<GeneralResponse<Object>> stopIndexing(){
    return ResponseEntity.ok(new GeneralResponse(indexService.stopIndexing()));
  }

  @PostMapping("indexPage")
  public ResponseEntity<GeneralResponse<Object>> indexPage(@RequestParam String url){
    return ResponseEntity.ok(new GeneralResponse<>(indexService.indexPage(url)));
  }
}

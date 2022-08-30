package ru.skillbox.areysearcher.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.areysearcher.model.entity.Field;
import ru.skillbox.areysearcher.model.entity.Page;
import ru.skillbox.areysearcher.service.crawler.Crawler;

@Service
@RequiredArgsConstructor
public class IndexService {

  public boolean startIndexing(){
    Map<Page, Map<Field, List<String>>> crawler =
        new ForkJoinPool().invoke(new Crawler());
    return true;
  }

  public boolean stopIndexing(){
    return true;
  }

  public boolean indexPage(String page){
    return true;
  }
}

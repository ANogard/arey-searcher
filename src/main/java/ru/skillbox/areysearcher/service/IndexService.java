package ru.skillbox.areysearcher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.areysearcher.exception.IndexException;
import ru.skillbox.areysearcher.service.crawler.CrawlerService;

@Service
@RequiredArgsConstructor
public class IndexService {

  private final CrawlerService crawlerService;

  public boolean startIndexing() throws IndexException {
    if (CrawlerService.isIndexing()) {
      throw new IndexException("Индексация уже запущена");
    }
    CrawlerService.setIndexing(true);
    return true;
  }

  public boolean stopIndexing() throws IndexException {
    if (!CrawlerService.isIndexing()) {
      throw new IndexException("Индексация не запущена");
    }
    CrawlerService.setIndexing(false);
    return true;
  }

  public boolean indexPage(String page) throws IndexException {
    crawlerService.crawlPage(page);
    return true;
  }
}

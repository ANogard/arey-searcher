package ru.skillbox.areysearcher.service.indexator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.areysearcher.exception.IndexException;
import ru.skillbox.areysearcher.service.crawler.CrawlerService;

@Service
@RequiredArgsConstructor
public class IndexService {

  private final CrawlerService crawlerService;

  public boolean startIndexing() throws IndexException {
    if (Indexator.isIndexing()) {
      throw new IndexException("Индексация уже запущена");
    }
    Indexator.setIndexing(true);
    return true;
  }

  public boolean stopIndexing() throws IndexException {
    if (!Indexator.isIndexing()) {
      throw new IndexException("Индексация не запущена");
    }
    Indexator.setIndexing(false);
    return true;
  }

  public boolean indexPage(String page) throws IndexException {
    crawlerService.addPage(page);
    return true;
  }
}

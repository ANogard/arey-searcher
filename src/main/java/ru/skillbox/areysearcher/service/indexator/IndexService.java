package ru.skillbox.areysearcher.service.indexator;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.skillbox.areysearcher.exception.GlobalException;
import ru.skillbox.areysearcher.service.crawler.CrawlerService;

@Service
@RequiredArgsConstructor
public class IndexService {

  private final CrawlerService crawlerService;
  private final Logger logger = LoggerFactory.getLogger("Index Logger");

  public boolean startIndexing() throws GlobalException {
    if (Indexator.isIndexing()) {
      throw new GlobalException("Индексация уже запущена");
    }
    Indexator.setIndexing(true);
    logger.info("Индексация запущена");
    return true;
  }

  public boolean stopIndexing() throws GlobalException {
    if (!Indexator.isIndexing()) {
      throw new GlobalException("Индексация не запущена");
    }
    Indexator.setIndexing(false);
    logger.info("Индексация остановлена");
    return true;
  }

  public boolean indexPage(String page) throws GlobalException {
    crawlerService.addPage(page);
    logger.info("Страница '" + page + "' добавлена к индексации");
    return true;
  }
}

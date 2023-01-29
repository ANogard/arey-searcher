package ru.skillbox.areysearcher.service.crawler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.skillbox.areysearcher.exception.GlobalException;
import ru.skillbox.areysearcher.model.entity.Page;
import ru.skillbox.areysearcher.model.entity.Site;
import ru.skillbox.areysearcher.repository.PageRepository;
import ru.skillbox.areysearcher.repository.SiteRepository;

@Service
@RequiredArgsConstructor
public class CrawlerService {

  private static Map<Site, Set<String>> preparedPages = new HashMap<>();
  private static Map<Site, Set<String>> indexedPages = new HashMap<>();
  private final PageRepository pageRepository;
  private final SiteRepository siteRepository;
  private final CrawlerUtils crawlerUtils;

  @Scheduled(initialDelay = 1000, fixedDelay=Long.MAX_VALUE)
  private void init() {
    List<Site> savedSites = siteRepository.getAll();
    for(Site site : savedSites) {
      List<Page> pages = pageRepository.getBySite(site);
      Set<String> paths = new HashSet<>();
      for(Page page : pages) {
        paths.add(site.getUrl().concat(page.getPath().substring(1)));
      }
      indexedPages.put(site, paths);
    }
    startCrawl();
  }

  @Scheduled(initialDelayString = "PT5M", fixedRateString = "PT5M")
  private void startCrawl(){
    for (Site site : crawlerUtils.getSites()) {
      Site siteToCrawl = siteRepository.getSiteOrSave(site);
      Set<String> pages = new ForkJoinPool().invoke(new Crawler(siteToCrawl.getUrl()));
      Set<String> indexedPaths = indexedPages.get(siteToCrawl);
      Set<String> paths = new HashSet<>();
      for(String page : pages){
        String path = siteToCrawl.getUrl().concat(page.substring(1));
        if(indexedPaths == null || indexedPaths.isEmpty() || !indexedPaths.contains(path)) {
          paths.add(path);
        }
      }
      if(!paths.isEmpty()) {
        preparedPages.put(siteToCrawl, paths);
      }
      Crawler.flush();
    }
  }

  public void addPage(String url) throws GlobalException {
    String siteURL = CrawlerUtils.getRootPath(url) + "/";
    if (!siteRepository.isSiteExists(siteURL)) {
      throw new GlobalException("Страница за пределами индексируемых сайтов");
    }
    Site site = siteRepository.getByUrl(siteURL);
    Set<String> paths = new HashSet<>();
    if(preparedPages.containsKey(site)) {
       paths = preparedPages.get(site);
    }
    paths.add(url);
    preparedPages.put(site, paths);
  }

  public static Map<Site, Set<String>> getPreparedPages() {
    return preparedPages;
  }

  public static void setPreparedPages(
      Map<Site, Set<String>> preparedPages) {
    CrawlerService.preparedPages = preparedPages;
  }

  public static Map<Site, Set<String>> getIndexedPages() {
    return indexedPages;
  }

  public static void setIndexedPages(
      Map<Site, Set<String>> indexedPages) {
    CrawlerService.indexedPages = indexedPages;
  }
}

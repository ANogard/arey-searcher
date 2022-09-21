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
import ru.skillbox.areysearcher.exception.IndexException;
import ru.skillbox.areysearcher.model.entity.Field;
import ru.skillbox.areysearcher.model.entity.IndexRank;
import ru.skillbox.areysearcher.model.entity.Lemma;
import ru.skillbox.areysearcher.model.entity.Page;
import ru.skillbox.areysearcher.model.entity.Site;
import ru.skillbox.areysearcher.model.entity.Status;
import ru.skillbox.areysearcher.repository.FieldRepository;
import ru.skillbox.areysearcher.repository.IndexRankRepository;
import ru.skillbox.areysearcher.repository.LemmaRepository;
import ru.skillbox.areysearcher.repository.PageRepository;
import ru.skillbox.areysearcher.repository.SiteRepository;
import ru.skillbox.areysearcher.service.morphology.Lemm;
import ru.skillbox.areysearcher.service.morphology.MorphologyType;

@Service
@RequiredArgsConstructor
public class CrawlerService {

  private static boolean isIndexing = false;
  private final FieldRepository fieldRepository;
  private final LemmaRepository lemmaRepository;
  private final IndexRankRepository indexRankRepository;
  private final PageRepository pageRepository;
  private final SiteRepository siteRepository;
  private final CrawlerProps crawlerProps;

  public static boolean isIndexing() {
    return isIndexing;
  }

  public static void setIndexing(boolean indexing) {
    isIndexing = indexing;
  }

  @Scheduled(fixedRate = 10000)
  private void startCrawling() {
    List<Site> sites = crawlerProps.getSites();
    for (Site site : crawlerProps.getSites()) {
      if (!isIndexing) {
        continue;
      }
      Site siteToCrawl = siteRepository.getSiteOrSave(site);
      Map<Page, Map<Field, List<String>>> crawledPages = crawlSite(siteToCrawl);
      if (!indexPages(crawledPages, siteToCrawl)) {
        continue;
      }
      siteRepository.updateStatus(siteToCrawl, Status.INDEXED);
      Crawler.flush();
    }
    isIndexing = false;
  }

  private Map<Page, Map<Field, List<String>>> crawlSite(Site site) throws IndexException {
    Map<Page, Map<String, List<String>>> crawler = new ForkJoinPool().invoke(
        new Crawler(site.getUrl()));

    Map<Page, Map<Field, List<String>>> out = new HashMap<>();
    Field titleField = fieldRepository.getFieldByName("title");
    Field bodyField = fieldRepository.getFieldByName("body");

    for (Map.Entry<Page, Map<String, List<String>>> entry : crawler.entrySet()) {
      if (entry.getValue() != null) {
        Map<Field, List<String>> fields = new HashMap<>();
        for (Map.Entry<String, List<String>> page : entry.getValue().entrySet()) {
          if (page.getKey().equals("title")) {
            fields.put(titleField, page.getValue());
          } else {
            fields.put(bodyField, page.getValue());
          }
          out.put(entry.getKey(), fields);
        }
      } else {
        out.put(entry.getKey(), new HashMap<>());
      }
    }
    return out;
  }

  public void crawlPage(String url) throws IndexException {
    Crawler crawler = new Crawler(url);
    String siteURL = crawler.getRootPath(url) + "/";

    if (!siteRepository.isSiteExists(siteURL)) {
      throw new IndexException("Страница за пределами индексируемых сайтов");
    }
    Site site = siteRepository.getByUrl(siteURL);

    Map<Page, Map<String, List<String>>> pageContent = crawler.crawlPage();
    Map<Page, Map<Field, List<String>>> pageWithFields = new HashMap<>();

    Field titleField = fieldRepository.getFieldByName("title");
    Field bodyField = fieldRepository.getFieldByName("body");

    for (Map.Entry<Page, Map<String, List<String>>> entry : pageContent.entrySet()) {
      if (entry.getValue() != null) {
        Map<Field, List<String>> fields = new HashMap<>();
        for (Map.Entry<String, List<String>> page : entry.getValue().entrySet()) {
          if (page.getKey().equals("title")) {
            fields.put(titleField, page.getValue());
          } else {
            fields.put(bodyField, page.getValue());
          }
          pageWithFields.put(entry.getKey(), fields);
        }
      } else {
        pageWithFields.put(entry.getKey(), new HashMap<>());
      }
    }
    indexPages(pageWithFields, site);
  }

  //Convert words to lemmas and index pages
  private boolean indexPages(Map<Page, Map<Field, List<String>>> content, Site site) {
    Lemm lemm = new Lemm(lemmaRepository, site.getId());

    for (Map.Entry<Page, Map<Field, List<String>>> page : content.entrySet()) {
      Page currentPage = pageRepository.getPageOrSave(page.getKey(),
          site.getId()); //TODO: add status changing if page already exists

      if (page.getKey().getCode() != 200) {
        pageRepository.updateCode(page.getKey().getCode(), currentPage.getId());
        if (currentPage.getPath().equals("/")) {
          siteRepository.updateError(site, "Site unavailable, code: " + currentPage.getCode());
          return false;
        }
        continue;
      }

      Map<IndexRank, String> indexRank = new HashMap<>();
      Set<Lemma> lemmas = new HashSet<>();

      for (Map.Entry<Field, List<String>> field : page.getValue().entrySet()) {
        Map<Lemma, Integer> lemmasInField =
            lemm.getLemmasFromText(field.getValue(), MorphologyType.INDEX);
        lemmas.addAll(lemmasInField.keySet());

        for (Map.Entry<Lemma, Integer> lemma : lemmasInField.entrySet()) {
          Lemma currentLemma = lemma.getKey();
          IndexRank index = indexRankRepository.getIndexRankOrSave(
              currentPage.getId(), currentLemma.getId());

          if (indexRank.containsValue(currentLemma.getLemma())) {
            index.setRank(index.getRank() + field.getKey().getWeight() * lemma.getValue());
          } else {
            index.setRank(field.getKey().getWeight() * lemma.getValue());
          }
          indexRankRepository.updateRank(index);
          indexRank.put(index, currentLemma.getLemma());
        }
      }
      for (Lemma lemma : lemmas) {
        lemmaRepository.increaseFrequency(lemma);
      }
    }
    return true;
  }
}

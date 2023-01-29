package ru.skillbox.areysearcher.service.indexator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.skillbox.areysearcher.model.entity.*;
import ru.skillbox.areysearcher.repository.*;
import ru.skillbox.areysearcher.service.Constants;
import ru.skillbox.areysearcher.service.crawler.CrawlerService;
import ru.skillbox.areysearcher.service.crawler.CrawlerUtils;
import ru.skillbox.areysearcher.service.morphology.Lemm;

@Service
@RequiredArgsConstructor
public class Indexator {

  private static boolean indexing = false;
  private final LemmaRepository lemmaRepository;
  private final FieldRepository fieldRepository;
  private final PageRepository pageRepository;
  private final SiteRepository siteRepository;
  private final IndexRankRepository indexRankRepository;
  private final SnippetRepository snippetRepository;
  private final Logger logger = LoggerFactory.getLogger("Index Logger");

  @Scheduled(initialDelayString = "PT5M", fixedRateString = "PT5M")
  private void index() {
    if(indexing) {
      Map<Site, Set<String>> preparedPages = CrawlerService.getPreparedPages();
      indexSite(preparedPages);
      Map<Site, Set<String>> indexedPages = CrawlerService.getIndexedPages();
      indexSite(indexedPages);
      setIndexing(false);
      logger.info("Индексирование закончено");
    }
  }

  private void indexSite(Map<Site, Set<String>> pages){
    if(!pages.isEmpty()){
      for(Entry<Site, Set<String>> entry : pages.entrySet()){
        logger.info("Индексирование сайта '" + entry.getKey().getUrl() + "'");
        siteRepository.updateStatus(entry.getKey(), Status.INDEXING);
        for (String path : entry.getValue()){
          indexPage(entry.getKey(), path);
        }
        siteRepository.updateStatus(entry.getKey(), Status.INDEXED);
        logger.info("Индексирование сайта '" + entry.getKey().getUrl() + "' завершено");
      }
    }
  }

  private void indexPage(Site site, String path) {
    if(indexing) {
      String root = site.getUrl().substring(0, site.getUrl().length() - 1);
      Page page = pageRepository.getPageOrSave(CrawlerUtils.getRelativePath(path, root),
          site.getId());
      logger.info("\tИндексирование страницы '" + page.getPath() + "'");
      try {
        Thread.sleep(500);
        Response response = Jsoup.connect(path).userAgent(Constants.USER_AGENT).timeout(10000)
            .execute();

        Document doc = response.parse();
        Map<Field, List<String>> wordsByFields = new HashMap<>();

        List<String> titleWords = new ArrayList<>(
            Arrays.asList(doc.getElementsByTag("title").text().split("\\s")));
        wordsByFields.put(fieldRepository.getFieldByName("title"), titleWords);

        List<String> bodyWords = getWordListFromDocument(doc);
        wordsByFields.put(fieldRepository.getFieldByName("body"), bodyWords);

        calculate(wordsByFields, site.getId(), page.getId());

        page.setCode(response.statusCode());
        page.setContent(doc.html());
        pageRepository.updatePage(page);
      } catch (HttpStatusException e) {
        page.setCode(e.getStatusCode());
        page.setContent("");
        pageRepository.updatePage(page);
        if (page.getPath().equals("/")) {
          siteRepository.updateStatus(site, Status.FAILED);
          siteRepository.updateError(site, "Site unavailable, code: " + page.getCode());
        }
        logger.info("\tИндексирование страницы '" + page.getPath() + "' завершено");
      } catch (InterruptedException | IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void calculate(Map<Field, List<String>> page, Integer siteId, Integer pageId){
    Lemm lemm = new Lemm(lemmaRepository, siteId);
    Map<IndexRank, String> indexRank = new HashMap<>();
    Set<Lemma> lemmas = new HashSet<>();
    List<String> snippetLemmas = new ArrayList<>();

    for (Map.Entry<Field, List<String>> field : page.entrySet()) {
      Map<Lemma, Integer> lemmasInField =
          lemm.getLemmasFromText(field.getValue());
      lemmas.addAll(lemmasInField.keySet());
      if(field.getKey().getName().equals("body")) {
        List<String> lemmasFromField = lemm.getLemmasListFromText(field.getValue());
        snippetRepository.updateSnippetOrSave(new Snippet(lemmasFromField, field.getValue().stream().map(w -> w.replaceAll(",", "")).collect(Collectors.toList()), pageId));
      }

      for (Map.Entry<Lemma, Integer> lemma : lemmasInField.entrySet()) {
        Lemma currentLemma = lemma.getKey();
        IndexRank index = indexRankRepository.getIndexRankOrSave(
            pageId, currentLemma.getId());

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

  private List<String> getWordListFromDocument(Document document) {
    Elements bodyElements = document.getElementsByTag("body").first().getAllElements();
    List<String> bodyWords = new ArrayList<>();
    for (Element element : bodyElements) {
      if ((element.tagName().equals("h1") ||element.tagName().equals("h2") ||
          element.tagName().equals("h3") || element.tagName().equals("h4") ||
          element.tagName().equals("h5") || element.tagName().equals("a") ||
          element.tagName().equals("span") || element.tagName().equals("p") ||
          element.tagName().equals("img")) || element.tagName().equals("div")
              && element.hasText()) {
        bodyWords.addAll(Arrays.asList(element.text().split("\\s")));
      }
    }
    return bodyWords;
  }

  public static boolean isIndexing() {
    return indexing;
  }

  public static void setIndexing(boolean indexing) {
    Indexator.indexing = indexing;
  }
}

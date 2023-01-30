package ru.skillbox.areysearcher.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.skillbox.areysearcher.exception.GlobalException;
import ru.skillbox.areysearcher.model.entity.*;
import ru.skillbox.areysearcher.model.rsdto.SearchResultDTO;
import ru.skillbox.areysearcher.repository.*;
import ru.skillbox.areysearcher.service.crawler.CrawlerUtils;
import ru.skillbox.areysearcher.service.morphology.Lemm;

@Service
@RequiredArgsConstructor
public class SearchService {
  private final LemmaRepository lemmaRepository;
  private final SiteRepository siteRepository;
  private final PageRepository pageRepository;
  private final IndexRankRepository indexRankRepository;
  private final SnippetRepository snippetRepository;
  private final Logger logger = LoggerFactory.getLogger("Search Logger");

  public List<SearchResultDTO> search(Map<String, Object> params) throws GlobalException {

    if(params.get("query").toString().isEmpty()) throw new GlobalException("Задан пустой поисковый запрос");
    List<SearchResultDTO> out = new ArrayList<>();
    Lemm lemm;

    if(params.containsKey("site")){
      Site site = siteRepository.getByUrl(params.get("site").toString());
      lemm = new Lemm(lemmaRepository, site.getId());
      logger.info("Поиск по фразе '" + params.get("query").toString() + "' на сайте '" + params.get("site").toString());
    } else {
      lemm = new Lemm(lemmaRepository);
      logger.info("Поиск по фразе '" + params.get("query").toString() + "'");
    }

    List<Lemma> lemmas = lemm.searchLemmas(params.get("query").toString());

    List<Page> pages = new ArrayList<>();
    for(Lemma lemma : lemmas){
      pages.addAll(getPages(lemma, pages));
    }
    if(pages.isEmpty()) {
      return new ArrayList<>();
    }

    double maxRank = getMaxRank(pages, lemmas);
    for(Page page : pages){
      Site site = siteRepository.getById(page.getSiteId());
      SearchResultDTO searchResultDTO = new SearchResultDTO(site);
      searchResultDTO.setUri(page.getPath().substring(1));
      String uri = site.getUrl() + page.getPath().substring(1);
      searchResultDTO.setTitle(CrawlerUtils.getTitle(uri));
      searchResultDTO.setSnippet(getSnippet(page, lemmas));
      searchResultDTO.setRelevance(getRank(page, lemmas) / maxRank);
      out.add(searchResultDTO);
    }
    Collections.sort(out);
    logger.info("Найдено " + out.size() + " результатов");
    return out;
  }

  private List<Page> getPages(Lemma lemma, List<Page> pages){
    List<Page> out = new ArrayList<>();
    List<Page> pagesWithLemmas = pageRepository.getByLemma(lemma);
    if(pages.isEmpty()){
      return pagesWithLemmas;
    }
    for(Page page : pagesWithLemmas){
      if(pages.contains(page) || !page.getSiteId().equals(pages.get(0).getSiteId())){
        out.add(page);
      }
    }
    return out;
  }

  private Double getMaxRank(List<Page> pages, List<Lemma> lemmas){
    double max = 0.;
    for (Page page : pages) {
      Double rank = 0.;
      for (Lemma lemma : lemmas) {
        if(!lemma.getSiteId().equals(page.getSiteId())) continue;
        IndexRank indexRank = indexRankRepository.get(page.getId(), lemma.getId());
        rank += indexRank.getRank();
      }
      max = Math.max(max, rank);
    }
    return max;
  }

  private Double getRank(Page page, List<Lemma> lemmas){
    Double rank = 0.;
    for (Lemma lemma : lemmas) {
      if(!lemma.getSiteId().equals(page.getSiteId())) continue;
      IndexRank indexRank = indexRankRepository.get(page.getId(), lemma.getId());
      rank += indexRank.getRank();
    }
    return rank;
  }

  private String getSnippet(Page page, List<Lemma> lemmas){
    StringBuilder builder = new StringBuilder();
    Snippet snippet = snippetRepository.getSnippetByPageId(page.getId());
    List <String> lemmasString = lemmas.stream().map(Lemma::getLemma).collect(Collectors.toList());
    int fromIndex = snippet.getLemmas().indexOf(lemmasString.get(0));
    lemmasString.clear();
    int toIndex = fromIndex + lemmas.size() + 5;
    if(snippet.getLemmas().size() < fromIndex + lemmas.size() + 6) {
      toIndex = snippet.getLemmas().size() - fromIndex;
    }
    if(fromIndex < 5) {
      fromIndex = 5;
    }

    List<String> words = snippet.getWords().subList(fromIndex - 5, toIndex);
    for (int i = 0; i < words.size(); i++){
      if(i == 5){
        builder.append("<strong>");
      }
      String word = words.get(i);
      builder.append(word).append(' ');
      if(i == 4 + lemmas.size()){
        builder.append("</strong>");
      }
    }
    return builder.toString();
  }
}

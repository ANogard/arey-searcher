package ru.skillbox.areysearcher.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.areysearcher.model.entity.IndexRank;
import ru.skillbox.areysearcher.model.entity.Lemma;
import ru.skillbox.areysearcher.model.entity.Page;
import ru.skillbox.areysearcher.model.entity.Site;
import ru.skillbox.areysearcher.model.rsdto.SearchResultDTO;
import ru.skillbox.areysearcher.repository.IndexRankRepository;
import ru.skillbox.areysearcher.repository.LemmaRepository;
import ru.skillbox.areysearcher.repository.PageRepository;
import ru.skillbox.areysearcher.repository.SiteRepository;
import ru.skillbox.areysearcher.service.crawler.CrawlerUtils;
import ru.skillbox.areysearcher.service.morphology.Lemm;

@Service
@RequiredArgsConstructor
public class SearchService {
  private final LemmaRepository lemmaRepository;
  private final SiteRepository siteRepository;
  private final PageRepository pageRepository;
  private final IndexRankRepository indexRankRepository;

  public List<SearchResultDTO> search(Map<String, Object> params) {
    Site site = siteRepository.getByUrl(params.get("site").toString());
    List<SearchResultDTO> out = new ArrayList<>();
    Lemm lemm = new Lemm(lemmaRepository, site.getId());

    List<Lemma> lemmas = lemm.searchLemmas(params.get("query").toString());
    List<Page> pages = new ArrayList<>();
    for(Lemma lemma : lemmas){
      pages = getPages(lemma, pages);
    }
    if(pages.isEmpty()) {
      return new ArrayList<>();
    }

    double maxRank = getMaxRank(pages, lemmas);
    for(Page page : pages){
      SearchResultDTO searchResultDTO = new SearchResultDTO(site);
      searchResultDTO.setUri(page.getPath().substring(1));
      String uri = site.getUrl() + page.getPath().substring(1);
      searchResultDTO.setTitle(CrawlerUtils.getTitle(uri));
      searchResultDTO.setSnippet("Temp snippet text");
      searchResultDTO.setRelevance(getRank(page, lemmas) / maxRank);
      out.add(searchResultDTO);
    }
    Collections.sort(out);
    return out;
  }

  private List<Page> getPages(Lemma lemma, List<Page> pages){
    List<Page> out = new ArrayList<>();
    List<Page> pagesWithLemmas = pageRepository.getByLemma(lemma);
    if(pages.isEmpty()){
      return pagesWithLemmas;
    }
    for(Page page : pages){
      if(pagesWithLemmas.contains(page)){
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
      IndexRank indexRank = indexRankRepository.get(page.getId(), lemma.getId());
      rank += indexRank.getRank();
    }
    return rank;
  }
}

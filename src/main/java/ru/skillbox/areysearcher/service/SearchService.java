package ru.skillbox.areysearcher.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.areysearcher.model.rqdto.SearchQueryDTO;
import ru.skillbox.areysearcher.model.rsdto.SearchResultDTO;

@Service
@RequiredArgsConstructor
public class SearchService {

  public List<SearchResultDTO> search(SearchQueryDTO body){
    return new ArrayList<>();
  }
}

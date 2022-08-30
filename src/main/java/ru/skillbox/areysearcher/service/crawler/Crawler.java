package ru.skillbox.areysearcher.service.crawler;

import java.util.concurrent.RecursiveTask;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.skillbox.areysearcher.model.entity.Field;
import ru.skillbox.areysearcher.model.entity.Page;


@Component
@RequiredArgsConstructor
public class Crawler extends RecursiveTask<Map<Page, Map<Field, List<String>>>> {

  @Override
  protected Map<Page, Map<Field, List<String>>>  compute() {
    Map<Page, Map<Field, List<String>>> out = new HashMap<>();
    return out;
  }
}

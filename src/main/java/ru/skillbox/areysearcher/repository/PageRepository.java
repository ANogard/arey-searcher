package ru.skillbox.areysearcher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.skillbox.areysearcher.model.entity.Page;
import ru.skillbox.areysearcher.model.mapper.PageMapper;

@Repository
@RequiredArgsConstructor
public class PageRepository {

  private final JdbcTemplate jdbc;

  public void savePage(Page page){
    String sql = "insert into page (id, path, code, content, siteId) " +
        "values (?, ?, ?, ?, ?)";
    jdbc.update(sql,
        page.getId(),
        page.getPath(),
        page.getCode(),
        page.getContent(),
        page.getSiteId());
  }

  public Page getPageByUrl(String url){
    String sql = "select * from page where page.url = ?";
    return jdbc.queryForObject(sql, new PageMapper(), url);
  }

  public boolean isPageExists(String url) {
    return getPageByUrl(url) != null;
  }
}

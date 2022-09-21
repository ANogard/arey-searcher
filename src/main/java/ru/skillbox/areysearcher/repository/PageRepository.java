package ru.skillbox.areysearcher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.skillbox.areysearcher.model.entity.Page;
import ru.skillbox.areysearcher.model.mapper.PageMapper;

@Repository
@RequiredArgsConstructor
public class PageRepository {

  private final JdbcTemplate jdbc;

  public Page savePage(Page page) {
    String sql = "INSERT INTO page (path, code, content, site_id) " +
        "VALUES (?, ?, ?, ?) RETURNING *";
    return jdbc.queryForObject(sql, new PageMapper(),
        page.getPath(),
        page.getCode(),
        page.getContent(),
        page.getSiteId());
  }

  public Page getPageByUrl(String url, Integer siteId) {
    String sql = "SELECT * FROM page WHERE page.path = ? AND page.site_id = ?";
    return jdbc.queryForObject(sql, new PageMapper(), url, siteId);
  }

  public Page getPageOrSave(Page page, Integer siteId) {
    try {
      return getPageByUrl(page.getPath(), siteId);
    } catch (DataAccessException e) {
      page.setSiteId(siteId);
      return savePage(page);
    }
  }

  public void updateCode(Integer code, Integer pageId) {
    String sql = "UPDATE page SET code = ? WHERE page.id = ?";
    jdbc.update(sql, code, pageId);
  }
}

package ru.skillbox.areysearcher.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.skillbox.areysearcher.model.entity.Lemma;
import ru.skillbox.areysearcher.model.entity.Page;
import ru.skillbox.areysearcher.model.entity.Site;
import ru.skillbox.areysearcher.model.mapper.PageMapper;

@Repository
@RequiredArgsConstructor
public class PageRepository {

  private final JdbcTemplate jdbc;

  public List<Page> getBySite(Site site){
    String sql = "SELECT * FROM page WHERE page.site_id = ?";
    return jdbc.query(sql, new PageMapper(), site.getId());
  }

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

  public Page getPageOrSave(String path, Integer siteId) {
    try {
      return getPageByUrl(path, siteId);
    } catch (DataAccessException e) {
      Page page = new Page(path, siteId);
      return savePage(page);
    }
  }

  public void updatePage(Page page) {
    String sql = "UPDATE page SET (path, code, content, site_id) = (?, ?, ?, ?) WHERE page.id = ?";
    jdbc.update(sql,
        page.getPath(),
        page.getCode(),
        page.getContent(),
        page.getSiteId(),
        page.getId());
  }

  public List<Page> getByLemma(Lemma lemma){
    String sql = "SELECT page.id, page.path, page.code, page.content, page.site_id FROM page " +
    "JOIN index_rank ON index_rank.page_id=page.id " +
    "JOIN lemma ON lemma.id=index_rank.lemma_id " +
    "WHERE page.site_id = ? AND lemma.lemma = ?";
    return jdbc.query(sql, new PageMapper(), lemma.getSiteId(), lemma.getLemma());
  }
}
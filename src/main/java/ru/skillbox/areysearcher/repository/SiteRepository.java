package ru.skillbox.areysearcher.repository;

import java.util.Date;
import liquibase.pro.packaged.I;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.skillbox.areysearcher.model.entity.Site;
import ru.skillbox.areysearcher.model.entity.Status;
import ru.skillbox.areysearcher.model.mapper.SiteMapper;

@Repository
@RequiredArgsConstructor
public class SiteRepository {

  private final JdbcTemplate jdbc;

  public Site getByUrl(String url){
    String sql = "SELECT * FROM site WHERE site.url = ?";
    return jdbc.queryForObject(sql, new SiteMapper(), url);

  }
  public Integer getIdByUrl(String url){
    String sql = "select id from site where site.url = ?";
    return jdbc.queryForObject(sql, (rs, rowNum) -> rs.getInt("id"), url);
  }

  public boolean isSiteExists(String url) {
    return getIdByUrl(url) != null;
  }

  public Site save(Site site) {
    String sql = "insert into site (status, status_time, last_error, url, name) " +
        "values (?, ?, ?, ?) RETURNING *";

    return jdbc.queryForObject(sql, new SiteMapper(),
        Status.INDEXING,
        new Date(),
        site.getLastError(),
        site.getUrl(),
        site.getName());
  }

  public void saveStatus(Integer id, Status status){
    String sql = "UPDATE site SET (status, status_time) = (?, ?) WHERE site.id = ?";
    jdbc.update(sql, status, new Date(), id);
  }
}

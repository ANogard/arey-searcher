package ru.skillbox.areysearcher.repository;

import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.skillbox.areysearcher.model.entity.Site;
import ru.skillbox.areysearcher.model.entity.Status;
import ru.skillbox.areysearcher.model.mapper.SiteMapper;
import ru.skillbox.areysearcher.model.mapper.StatisticsDetailedMapper;
import ru.skillbox.areysearcher.model.mapper.StatisticsTotalMapper;
import ru.skillbox.areysearcher.model.rsdto.statistics.StatisticsDetailedDTO;
import ru.skillbox.areysearcher.model.rsdto.statistics.StatisticsTotalDTO;

@Repository
@RequiredArgsConstructor
public class SiteRepository {

  private final JdbcTemplate jdbc;

  public List<Site> getAll(){
    String sql = "SELECT * FROM site";
    return jdbc.query(sql, new SiteMapper());
  }

  public Site getByUrl(String url) {
    String sql = "SELECT * FROM site WHERE site.url = ?";
    return jdbc.queryForObject(sql, new SiteMapper(), url);
  }

  public Site getById(int id) {
    String sql = "SELECT * FROM site WHERE site.id = ?";
    return jdbc.queryForObject(sql, new SiteMapper(), id);
  }

  public boolean isSiteExists(String url) {
    String sql = "SELECT * FROM site WHERE site.url = ?";
    try {
      jdbc.queryForObject(sql, new SiteMapper(), url);
      return true;
    } catch (DataAccessException e) {
      return false;
    }
  }

  public Site save(Site site) {
    String sql = "INSERT INTO site (status, status_time, last_error, url, name) " +
        "VALUES (?::status_type, ?, ?, ?, ?) RETURNING *";
    return jdbc.queryForObject(sql, new SiteMapper(),
        Status.INDEXING.toString(),
        new Date(),
        site.getLastError(),
        site.getUrl(),
        site.getName());
  }

  public Site getSiteOrSave(Site site) {
    try {
      return getByUrl(site.getUrl());
    } catch (DataAccessException e) {
      return save(site);
    }
  }

  public void updateStatus(Site site, Status status) {
    String sql = "UPDATE site SET (status, status_time) = (?::status_type, ?) WHERE site.id = ?";
    jdbc.update(sql, status.toString(), new Date(), site.getId());
  }

  public void updateError(Site site, String error) {
    String sql = "UPDATE site SET last_error = ? WHERE site.id = ?";
    jdbc.update(sql, error, site.getId());
  }

  public StatisticsTotalDTO getStatisticsTotal() {
    String sql = "SELECT COUNT(site.id) as sites, "
        + "(SELECT COUNT(page.id) FROM page) as pages, "
        + "(SElECT COUNT(lemma.id) FROM lemma) as lemmas "
        + "FROM site;";
    return jdbc.queryForObject(sql, new StatisticsTotalMapper());
  }

  public List<StatisticsDetailedDTO> getStatisticsDetailed() {
    String sql = "SELECT site.url, site.name, site.status, site.status_time, site.last_error, "
        + "(SELECT COUNT(*) FROM page WHERE page.site_id=site.id) as pages, "
        + "(SELECT COUNT(*) FROM lemma WHERE lemma.site_id=site.id) as lemmas "
        + "FROM site "
        + "JOIN lemma ON site.id=lemma.site_id "
        + "GROUP BY site.id";
    return jdbc.query(sql, new StatisticsDetailedMapper());
  }

  public List<Site> getSitesByLemma(String lemma){
    String sql ="SELECT site.id, site.status, site.status_time, site.last_error, site.url, site.name " +
            "FROM site " +
            "JOIN lemma ON lemma.site_id=site.id " +
            "WHERE lemma.lemma = ?";
    return jdbc.query(sql, new SiteMapper(), lemma);
  }
}

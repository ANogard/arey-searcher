package ru.skillbox.areysearcher.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import ru.skillbox.areysearcher.model.rsdto.statistics.StatisticsTotalDTO;
import ru.skillbox.areysearcher.service.crawler.CrawlerService;

public class StatisticsTotalMapper implements RowMapper<StatisticsTotalDTO> {

  @Override
  public StatisticsTotalDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
    StatisticsTotalDTO mapper = new StatisticsTotalDTO();
    mapper.setSites(rs.getInt("sites"));
    mapper.setPages(rs.getInt("pages"));
    mapper.setLemmas(rs.getInt("lemmas"));
    mapper.setIsIndexing(CrawlerService.isIndexing());
    return mapper;
  }
}

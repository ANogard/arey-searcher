package ru.skillbox.areysearcher.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import ru.skillbox.areysearcher.model.entity.Status;
import ru.skillbox.areysearcher.model.rsdto.statistics.StatisticsDetailedDTO;

public class StatisticsDetailedMapper implements RowMapper<StatisticsDetailedDTO> {

  @Override
  public StatisticsDetailedDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
    StatisticsDetailedDTO mapper = new StatisticsDetailedDTO();
    mapper.setUrl(rs.getString("url"));
    mapper.setName(rs.getString("name"));
    mapper.setStatus(Status.valueOf(rs.getString("status")));
    mapper.setStatusTime(rs.getDate("status_time"));
    mapper.setError(rs.getString("last_error"));
    mapper.setPages(rs.getInt("pages"));
    mapper.setLemmas(rs.getInt("lemmas"));
    return mapper;
  }
}

package ru.skillbox.areysearcher.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import ru.skillbox.areysearcher.model.entity.Field;
import ru.skillbox.areysearcher.model.entity.Site;
import ru.skillbox.areysearcher.model.entity.Status;

public class SiteMapper implements RowMapper<Site> {

  @Override
  public Site mapRow(ResultSet rs, int rowNum) throws SQLException {
    Site mapper = new Site();
    mapper.setId(rs.getInt("id"));
    mapper.setStatus((Status) rs.getObject("status"));
    mapper.setStatusTime(rs.getDate("status_time"));
    mapper.setLastError(rs.getString("last_error"));
    mapper.setUrl(rs.getString("url"));
    mapper.setName(rs.getString("name"));
    return mapper;
  }
}

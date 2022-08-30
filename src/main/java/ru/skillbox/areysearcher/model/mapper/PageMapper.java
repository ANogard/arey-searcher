package ru.skillbox.areysearcher.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import ru.skillbox.areysearcher.model.entity.Page;

public class PageMapper implements RowMapper<Page> {

  @Override
  public Page mapRow(ResultSet rs, int rowNum) throws SQLException {
    Page mapper = new Page();
    mapper.setId(rs.getInt("id"));
    mapper.setPath(rs.getString("path"));
    mapper.setCode(rs.getInt("code"));
    mapper.setContent(rs.getString("content"));
    mapper.setSiteId(rs.getInt("site_id"));
    return mapper;
  }
}

package ru.skillbox.areysearcher.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import ru.skillbox.areysearcher.model.entity.Field;

public class FieldMapper implements RowMapper<Field> {

  @Override
  public Field mapRow(ResultSet rs, int rowNum) throws SQLException{
    Field mapper = new Field();
    mapper.setId(rs.getInt("id"));
    mapper.setName(rs.getString("name"));
    mapper.setSelector(rs.getString("name"));
    mapper.setWeight(rs.getFloat("weight"));
    return mapper;
  }
}

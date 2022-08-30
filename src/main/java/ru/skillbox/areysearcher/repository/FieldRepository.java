package ru.skillbox.areysearcher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.skillbox.areysearcher.model.entity.Field;
import ru.skillbox.areysearcher.model.mapper.FieldMapper;

@Repository
@RequiredArgsConstructor
public class FieldRepository {

  private final JdbcTemplate jdbc;

  public Field getFieldByName(String name){
    String sql = "select * from field where field.name = ?";
    return jdbc.queryForObject(sql, new FieldMapper(), name);
  }
}

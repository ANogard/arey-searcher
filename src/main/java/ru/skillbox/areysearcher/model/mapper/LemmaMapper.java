package ru.skillbox.areysearcher.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import ru.skillbox.areysearcher.model.entity.Lemma;

public class LemmaMapper implements RowMapper<Lemma> {

  @Override
  public Lemma mapRow(ResultSet rs, int rowNum) throws SQLException {
    Lemma mapper = new Lemma();
    mapper.setId(rs.getInt("id"));
    mapper.setLemma(rs.getString("lemma"));
    mapper.setFrequency(rs.getInt("frequency"));
    mapper.setSiteId(rs.getInt("site_id"));
    return mapper;
  }
}

package ru.skillbox.areysearcher.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import ru.skillbox.areysearcher.model.entity.IndexRank;

public class IndexRankMapper implements RowMapper<IndexRank> {

  @Override
  public IndexRank mapRow(ResultSet rs, int rowNum) throws SQLException {
    IndexRank mapper = new IndexRank();
    mapper.setId(rs.getInt("id"));
    mapper.setPageId(rs.getInt("page_id"));
    mapper.setLemmaId(rs.getInt("lemma_id"));
    mapper.setRank(rs.getFloat("rank"));
    return mapper;
  }
}

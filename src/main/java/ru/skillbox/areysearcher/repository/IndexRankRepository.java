package ru.skillbox.areysearcher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.skillbox.areysearcher.model.entity.IndexRank;
import ru.skillbox.areysearcher.model.mapper.IndexRankMapper;

@Repository
@RequiredArgsConstructor
public class IndexRankRepository {

  private final JdbcTemplate jdbc;

  public IndexRank get(int pageId, int lemmaId) {
    String sql = "SELECT * FROM index_rank WHERE index_rank.page_id = ? AND index_rank.lemma_id = ?";
    return jdbc.queryForObject(sql, new IndexRankMapper(), pageId, lemmaId);

  }

  public IndexRank save(IndexRank indexRank) {
    String sql = "INSERT INTO index_rank (page_id, lemma_id, rank) " +
        "VALUES (?, ?, ?) RETURNING *";

    return jdbc.queryForObject(sql, new IndexRankMapper(),
        indexRank.getPageId(),
        indexRank.getLemmaId(),
        indexRank.getRank());
  }

  public IndexRank getIndexRankOrSave(int pageId, int lemmaId) {
    try {
      return get(pageId, lemmaId);
    } catch (DataAccessException e) {
      return save(new IndexRank(pageId, lemmaId));
    }
  }

  public void updateRank(IndexRank indexRank) {
    String sql = "UPDATE index_rank SET rank = ? WHERE index_rank.id = ?";
    jdbc.update(sql,
        indexRank.getRank(),
        indexRank.getId());
  }

}

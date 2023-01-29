package ru.skillbox.areysearcher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.skillbox.areysearcher.model.entity.Lemma;
import ru.skillbox.areysearcher.model.mapper.LemmaMapper;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LemmaRepository {

  private final JdbcTemplate jdbc;

  public Lemma get(Lemma lemma) {
    String sql = "SELECT * FROM lemma WHERE lemma.lemma = ? AND lemma.site_id = ?";
    return jdbc.queryForObject(sql, new LemmaMapper(), lemma.getLemma(), lemma.getSiteId());
  }
  public List<Lemma> getAll(String lemma) {
    String sql = "SELECT * FROM lemma WHERE lemma.lemma = ? ";
    return jdbc.query(sql, new LemmaMapper(), lemma);
  }

  public Lemma save(Lemma lemma) {
    String sql = "INSERT INTO lemma (lemma, frequency, site_id) " +
        "VALUES (?, ?, ?) RETURNING *";

    return jdbc.queryForObject(sql, new LemmaMapper(),
        lemma.getLemma(),
        lemma.getFrequency(),
        lemma.getSiteId());
  }

  public void increaseFrequency(Lemma lemma) {
    String sql = "UPDATE lemma SET frequency = ? WHERE lemma.lemma = ?";
    jdbc.update(sql,
        lemma.getFrequency() + 1,
        lemma.getLemma());
  }

  public Lemma getLemmaOrSave(Lemma lemma) {
    try {
      return get(lemma);
    } catch (DataAccessException e) {
      return save(lemma);
    }
  }
}

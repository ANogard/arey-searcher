package ru.skillbox.areysearcher.model.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.skillbox.areysearcher.model.entity.Snippet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SnippetMapper implements RowMapper<Snippet> {

  @Override
  public Snippet mapRow(ResultSet rs, int rowNum) throws SQLException {
    Snippet mapper = new Snippet();
    mapper.setId(rs.getInt("id"));
    mapper.setPageId(rs.getInt("page_id"));
    mapper.setWords(Stream.of(Collections.singletonList(rs.getArray("words"))
            .toString().split(",")).map(w -> w.replaceAll("\"", "")).collect(Collectors.toList()));
    mapper.setLemmas(Stream.of(Collections.singletonList(rs.getArray("lemmas"))
            .toString().split(",")).map(w -> w.replaceAll("\"", "")).collect(Collectors.toList()));
    return mapper;
  }
}

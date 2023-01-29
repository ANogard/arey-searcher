package ru.skillbox.areysearcher.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.skillbox.areysearcher.model.entity.Snippet;
import ru.skillbox.areysearcher.model.mapper.SnippetMapper;

@Repository
@RequiredArgsConstructor
public class SnippetRepository {

    private final JdbcTemplate jdbc;

    public Snippet saveSnippet(Snippet snippet){
        String sql = "INSERT INTO snippet (words, lemmas, page_id) VALUES (?, ?, ?) RETURNING *";
        return jdbc.queryForObject(sql, new SnippetMapper(),
                snippet.getWords().toArray(new String[0]),
                snippet.getLemmas().toArray(new String[0]),
                snippet.getPageId());
    }

    public Snippet getSnippetByPageId(int pageId){
        String sql = "SELECT * FROM snippet WHERE snippet.page_id = ?";
        return jdbc.queryForObject(sql, new SnippetMapper(), pageId);
    }

    public void updateSnippetOrSave(Snippet snippet) {
        try {
            Snippet getSnippet = getSnippetByPageId(snippet.getPageId());
            updateSnippet(snippet);
        } catch (DataAccessException e) {
            saveSnippet(snippet);
        }
    }

    public void updateSnippet(Snippet snippet){
        String sql = "UPDATE snippet SET (words, lemmas) = (?, ?) WHERE snippet.page_id = ? RETURNING *";
        jdbc.queryForObject(sql, new SnippetMapper(),
                snippet.getWords().toArray(new String[0]),
                snippet.getLemmas().toArray(new String[0]),
                snippet.getPageId());
    }
}

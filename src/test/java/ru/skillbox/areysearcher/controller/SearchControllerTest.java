package ru.skillbox.areysearcher.controller;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application.yml")
@AutoConfigureEmbeddedDatabase
        (provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY,
                refresh = AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_CLASS)
@Sql(value = {"/sql/001-create-and-fill-db.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/002-clear-db.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class SearchControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void emptySearchTest() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/search?query="))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void singleWordSearchTest() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/search?query=ехал"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content()
                    .json(Files.readString(Paths.get("src/test/resources/json/single-search.json"))));
  }

  @Test
  void coupleWordsSearchTest() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/search?query=ехал грека"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content()
                    .json(Files.readString(Paths.get("src/test/resources/json/couple-search.json"))));
  }
}

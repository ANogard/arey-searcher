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
        (provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY)
@Sql(value = {"/sql/001-create-and-fill-db.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/002-clear-db.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class StatisticsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void getStatisticsTest() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/statistics"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content()
                    .json(Files.readString(Paths.get("src/test/resources/json/statistics.json"))));
  }
}

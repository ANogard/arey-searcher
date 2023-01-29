package ru.skillbox.areysearcher.controller;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.skillbox.areysearcher.service.indexator.Indexator;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application.yml")
@AutoConfigureEmbeddedDatabase(provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY)
public class IndexControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void startIndexingTest() throws Exception {
    Indexator.setIndexing(false);
    this.mockMvc.perform(MockMvcRequestBuilders.get("/api/startIndexing"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void stopIndexingTest() throws Exception {
    Indexator.setIndexing(true);
    this.mockMvc.perform(MockMvcRequestBuilders.get("/api/stopIndexing"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk());
  }
}

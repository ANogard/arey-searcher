package ru.skillbox.areysearcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AreySearcherApplication {

  public static void main(String[] args) {
    SpringApplication.run(AreySearcherApplication.class, args);
  }

}

package ru.skillbox.areysearcher.model.entity;

import lombok.Data;

@Data
public class Field {
  private Integer id;
  private String name;
  private String selector;
  private Float weight;
}

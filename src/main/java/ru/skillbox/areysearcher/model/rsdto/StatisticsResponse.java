package ru.skillbox.areysearcher.model.rsdto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class StatisticsResponse<T> {

  private Boolean result;
  private String error;
  private T statistics;

  public StatisticsResponse(String error) {
    this.result = false;
    this.error = error;
  }

  public StatisticsResponse(T data) {
    this.result = true;
    this.statistics = data;
  }
}

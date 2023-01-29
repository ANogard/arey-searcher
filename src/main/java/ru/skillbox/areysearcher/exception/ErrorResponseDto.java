package ru.skillbox.areysearcher.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ErrorResponseDto {

  private Boolean result;
  @JsonProperty("error")
  private String error;

  public ErrorResponseDto(String message) {
    result = false;
    error = message;
  }
}

package ru.skillbox.areysearcher.model.rsdto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class GeneralResponse<T> {
  private Boolean result;
  private String error;
  private T data;

  public GeneralResponse(){
    this.result = true;
  }

  public GeneralResponse(boolean result){
    this.result = result;
  }

  public GeneralResponse(String error){
    this.result = false;
    this.error = error;
  }
  public GeneralResponse(T data){
    this.result = true;
    this.data = data;
  }
}

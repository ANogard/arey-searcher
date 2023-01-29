package ru.skillbox.areysearcher.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandlerController {

  private final Logger logger = LoggerFactory.getLogger("Exception Logger");

  @ExceptionHandler(GlobalException.class)
  public ResponseEntity<ErrorResponseDto> handleInvalidRequestException(GlobalException ex) {
    logger.info(ex.getMessage());
    return ResponseEntity.badRequest().body(new ErrorResponseDto(ex.getMessage()));
  }

}

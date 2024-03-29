package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

  @ExceptionHandler
  public ResponseEntity<?> handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
      log.warn(String.valueOf(e));
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler
  public ResponseEntity<?> handleUserNotFoundException(final NotFoundException e) {
      log.warn(String.valueOf(e));
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler
  public ResponseEntity<?> handleThrowable(final Throwable e) {
      log.warn(String.valueOf(e));
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

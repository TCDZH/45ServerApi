package com.TCDZH.server.controller;

import com.TCDZH.server.exceptions.ClientErrorException;
import com.TCDZH.server.exceptions.InvalidGameIdException;
import com.TCDZH.server.exceptions.ServiceException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      final HttpRequestMethodNotSupportedException exception, final HttpHeaders headers,
      final HttpStatusCode status, final WebRequest request) {
    return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
  }

  @ResponseBody
  @ExceptionHandler(InvalidGameIdException.class)
  public ResponseEntity<String> handleInvalidGameIdException(InvalidGameIdException exception){
    return new ResponseEntity<>(exception.getMessage(),HttpStatus.BAD_REQUEST);
  }

  @ResponseBody
  @ExceptionHandler(ClientErrorException.class)
  public ResponseEntity<String> handleClientErorr(ClientErrorException exception){
    return new ResponseEntity<>(exception.getMessage() + " Broadcast failed", HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ResponseBody
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleServiceError(ServiceException exception){
    return new ResponseEntity<>(exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

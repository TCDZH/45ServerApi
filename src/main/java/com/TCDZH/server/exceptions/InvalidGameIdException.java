package com.TCDZH.server.exceptions;

import java.security.Provider.Service;

public class InvalidGameIdException extends ServiceException {

  /**
   * Constructor for Invalid Game Id Errors.
   *
   * @param message Error message to include in exception
   */
  public InvalidGameIdException(String message) {
    super(message);
  }
}

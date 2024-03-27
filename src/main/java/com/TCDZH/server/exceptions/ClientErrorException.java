package com.TCDZH.server.exceptions;

public class ClientErrorException extends ServiceException{

  /**
   * Constructor for Client Error Errors.
   *
   * @param message Error message to include in exception
   */
  public ClientErrorException(String message) {
    super(message);
  }
}

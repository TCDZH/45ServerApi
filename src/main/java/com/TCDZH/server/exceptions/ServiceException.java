package com.TCDZH.server.exceptions;

public class ServiceException extends Exception {

  /**
   * Constructor for Service Errors.
   *
   * @param message Error message to include in exception
   */
  public ServiceException(final String message) {
    super(message);
  }

}

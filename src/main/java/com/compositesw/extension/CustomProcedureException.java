/**
 * Copyright (c) 2004, 2008
 * Composite Software, Inc.
 * All Rights Reserved
 */

package com.compositesw.extension;

/**
 * Exception throw by methods in the Java Composition interfaces.
 */
public class CustomProcedureException
  extends Exception
{
  public CustomProcedureException() {
    super();
  }

  public CustomProcedureException(String message) {
    super(message);
  }

  public CustomProcedureException(String message, Throwable cause) {
    super(message, cause);
  }

  public CustomProcedureException(Throwable cause) {
    super(cause);
  }
}

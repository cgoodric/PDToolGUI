/**
 * COPYRIGHT:
 *  (C)Copyright 2002 Composite Software, Inc.
 *  All Rights Reserved.
 *
 * @author Patrick Chan
 * 
 */

package com.compositesw.common;

import java.util.*;
import com.compositesw.extension.CustomProcedureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This exception is a super class of all runtime exceptions thrown 
 * by composite software. The exception must include a message code 
 * and a set of values which together form a message suitable for display
 * to a user. 
 */
public class CompositeRuntimeException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger (CompositeRuntimeException.class.getName());

    /**
     * Default module name if not specified.
     */
    public static final String DEFAULT_MODULE_NAME = "common";

    /* Non-null name of the module that has an exception. */
    protected String module;

    /* Numeric code that represents a specific message. */
    protected int code = -1;

    /* The arguments for the message. */
    protected Object[] args;

    /* Used to locate resource bundle. */
    ResourceBundleFactory bundleFactory;


    /**
     * Construct a new exception that wraps another.
     *
     * @param module non-null string specifying the module that
     *               originated the exception.
     * @param cause  a possibly-null exception.
     * @param code   code of the message format
     * @param args   a non-null list of values to be formatted 
     */
    public CompositeRuntimeException(String module, Throwable cause, 
                                     int code, Object... args) {
        super(cause);
        this.code = code;
        this.args = args;
        this.module = module;
    }

    public CompositeRuntimeException(ResourceBundleFactory bundleFactory, String module, Throwable cause,
                                     int code, Object... args) {

        this(module, cause, code, args);
        this.bundleFactory = bundleFactory;
    }

    /******************************************************************/

    /**
     * Convenience constructor equivalent to 
     *  CompositeRuntimeException(DEFAULT_MODULE_NAME, null, code, 
     *                            new Object[]{arg1});
     * 
     * @param code   code for the message format;
     *                    -1 makes the exception transaprent
     */
    public CompositeRuntimeException(int code, Object... arg1) {
        this(DEFAULT_MODULE_NAME, null, code, arg1);
    }

    /**
     * Convenience constructor equivalent to 
     *   CompositeRuntimeException(module, null, code, 
     *                             new Object[]{arg1}).
     *
     * @param module non-null string representing the module that
     *               originated the exception.
     * @param code   code for the message format;
     *                    -1 makes the exception transaprent
     */
    public CompositeRuntimeException(String module, int code, Object... arg1) {
        this(module, null, code, arg1);
    }

    /******************************************************************/

    /**
     * Convenience constructor equivalent to 
     *  Composite(DEFAULT_MODULE_NAME, cause, code, new Object[]{arg1}).
     *
     * @param code   code for the message format;
     *                    -1 makes the exception transaprent
     */
    public CompositeRuntimeException(Throwable cause, int code, Object... arg1) {
        this(DEFAULT_MODULE_NAME, cause, code, arg1);
    }

    /******************************************************************/

    /**
     * Convenience constructor equivalent to 
     *  Composite(DEFAULT_MODULE_NAME, cause, -1, new Object[0]).
     *
     * @param cause  previous cause of exception.
     */
    public CompositeRuntimeException(Throwable cause) {
        this(DEFAULT_MODULE_NAME, cause, -1);
    }

    /**
     * This method should be used when an exception is caught and is rethrown
     * without any new additional information. If the cause is already a
     * CompositeRuntimeException, the cause will not be wrapped by another
     * CompositeRuntimeException and is simply thrown. If the cause is not
     * a CompositeRuntimeException, it will be wrapped by a CompositeRuntimeException.
     *
     * Using this method helps to reduce the noise in stack traces by eliminating
     * unnecessary stacks.
     *
     * @param cause  previous cause of exception.
     */
    public static CompositeRuntimeException rethrow(Throwable cause) {
        if (cause instanceof CompositeRuntimeException) {
            throw (CompositeRuntimeException)cause;
        }
        throw new CompositeRuntimeException(cause);
    }
    
    /**
     * Convenience constructor equivalent to 
     *  Composite(module, cause, -1, new Object[0]).
     *
     * @param cause  previous cause of exception.
     */
    public CompositeRuntimeException(String module, Throwable cause) {
        this(module, cause, -1);
    }

    /******************************************************************/

    /**
     * Displays a formatted exception message.
     *
     * @return String  Formatted exception message. 
     */
    public String getMessage() {
        return getMessage(Locale.getDefault());
    }

    /**
     * Displays a formatted exception message.
     *
     * @return String  Formatted exception message. 
     */
    public String getMessage(Locale locale) {
        if (code == -1) {
            if (args != null && args.length > 0 && args[0] != null)
              return args[0].toString();
            else if (getCause() != null)
              return getCause().getMessage();
            else
              return "";
        } else if (getBundleFactory() != null) {
            return MsgFormatter.format(getBundleFactory(), locale, module, code, args);
        } 
        return MsgFormatter.format(module, code, args);
    }

    /**
     * Returns the module
     *
     * @return the non-null module as specified in the constructor.
     */
    public String getModule() {
        return module;
    }

    /**
     * Returns the message code
     *
     * @return the message code as supplied in the constructor.
     */
    public int getCode() {
        return code;
    }

    /**
     * Returns the arguments of the exception
     *
     * @return the non-null arguments code as specified in the constructor.
     */
    public Object[] getArgs() {
        return args;
    }

    protected void setBundleFactory(ResourceBundleFactory bundleFactory) {
        this.bundleFactory = bundleFactory;
    }

    public ResourceBundleFactory getBundleFactory() {
        return bundleFactory;
    }

    /**
     * This method should be over-rided for any CompositeRuntimeException that never
     * prints its stack trace.
     * @return true if the stack trace is to be printed in the log.  false 
     * otherwise
     */
    public boolean isPrintStackTrace() {
        if (this.code != -1) {
            int digit = this.code / 1000000;

            // If the code is 3, don't print the exception unless there's an
            // even code further down the chain.
            if (code / 1000000 == 3) {
                // Check for any even's. If any, return true.
                Throwable t = this.getCause();
                while (t != null) {
                    int cd = -1;
                    if (t instanceof CompositeRuntimeException) {
                        cd = ((CompositeRuntimeException) t).getCode();
                    } else if (t instanceof CompositeException) {
                        cd = ((CompositeException) t).getErrorCode();
                    } else {
                        return false;
                    }

                    // Check code
                    digit = cd / 1000000;
                    if (digit % 2 == 0) {
                        return true;
                    }
                    t = t.getCause();
                }
                return false;
            }

            if (digit % 2 == 0) {
                return true;
            }
        }
        Throwable t = this.getCause();
        if (t == null) {
            return this.code == -1;
        } else if (t instanceof CompositeRuntimeException) {
            CompositeRuntimeException c = (CompositeRuntimeException) t;
            return c.isPrintStackTrace();
        } else if (t instanceof CompositeException) {
            return ((CompositeException) t).isPrintStackTrace();
        } else if (t instanceof CustomProcedureException) {
            Throwable t2 = t.getCause();
            if (t2 != null) {
                return MsgFormatter.isPrintStackTrace(t2);
            } else {
                return false;
            }
        } else {
            return true;
        }
//        int numEntries = 0;
//        Throwable t = this;
//        while (t != null) {
//            int code = -1;
//            if (t instanceof CompositeRuntimeException) {
//                CompositeRuntimeException ce = (CompositeRuntimeException)t;
//                code = ce.getCode();
//            } else if (t instanceof CompositeException) {
//                CompositeException ce = (CompositeException)t;
//                code = ce.getErrorCode();
//            } else {
//                return true;
//            }
//            if (code != -1) {
//                int digit = code / 1000000;
//                if (digit % 2 == 0) {
//                    return true;
//                }
//                numEntries++;
//            }
//            t = t.getCause();
//        }
//        return numEntries == 0;
    }

    public boolean isServerError() {
        return (getCode() / 1000000) == 2;
    }

    public boolean isAuditError() {
        return (getCode() / 1000000) == 1;
    }

    public boolean isWarning() {
        return false;
    }

    @Override
    public String toString() {
        // This works around a bug in DefaultMBeanServerInterceptor which calls toString() on
        // exceptions it catches and re-throws.  Thus, if we didn't have this work around in place
        // the exception would look like: <fullyQualifiedExceptionClassName>: <exceptionMessage>",
        // which isn't very pretty for end users.
        return getMessage();
    }
}

//

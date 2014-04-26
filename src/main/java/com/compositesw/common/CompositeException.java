/**
 * COPYRIGHT:
 *  (C)Copyright 2002 Composite Software, Inc.
 *  All Rights Reserved.
 *
 * @author Ringo Law
 * 
 */

package com.compositesw.common;

import com.compositesw.extension.CustomProcedureException;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This exception is a super class of all exceptions thrown by CDMS.
 * Each CDMS domain can create its own exception object that is
 * inherited from this class.
 */
public class CompositeException extends Exception 
{
    private static final Logger logger = LoggerFactory.getLogger (CompositeException.class.getName());

    /** Name of the CDMS domain that has an exception. */
    private String domain;

    /** Numeric code that represents a specific execption. */
    private int errorCode = -1;

    /** The arguments of the exception. */
    private Object[] args;

    /** The domain resource bundle */
    private ResourceBundleFactory bundleFactory;

    /**
     * Construct a new Composite exception with its cause.
     *
     * @param cause  previous cause of exception.
     */
    public CompositeException(Throwable cause) {
        super(cause);
    }

    /**
     * Construct a new Composite exception with its cause.
     *
     * @param cause  previous cause of exception.
     */
    public CompositeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct a new Composite exception with an error code from a
     * domain.
     *
     * @param domain - a CDMS domain that throws this exception.
     * @param errorCode - a numeric code that represents a specific
     *                    exception that is thrown.
     */
    public CompositeException(String domain, int errorCode, Object... args) 
    {
        super();
        setDomain( domain);
        setErrorCode( errorCode);
        setArgs( args);
    }

    /**
     * Construct a new Composite exception with an error code and
     * a resource bundle.
     *
     * @param bundleFactory - a factory used to create a resource bundle
     * @param errorCode - a numeric code that represents a specific
     *                    exception that is thrown.
     */
    public CompositeException(ResourceBundleFactory bundleFactory,
                              int errorCode, Object... args)
    {
        super();
        setBundleFactory( bundleFactory);
        setErrorCode( errorCode);
        setArgs( args);
    }

    /**
     * Constructs a new Composite exception with an error code from a
     * domain and a previous cause.
     *
     * @param domain  a CDMS domain that throws this exception.
     * @param errorCode  a numeric code that represents a specific
     *                   exception that is thrown.
     * @param cause  previous cause of exception.
     */
    public CompositeException(String domain, int errorCode, Object[] args,
            Throwable cause) 
    {
        super(cause);
        setDomain(domain);
        setErrorCode(errorCode);
        setArgs(args);
    }

    /**
     * Constructs a new Composite exception with an error code from a
     * domain and a previous cause.
     *
     * @param bundleFactory - a factory used to create a resource bundle
     * @param errorCode - a numeric code that represents a specific
     *                    exception that is thrown.
     * @param errorCode  a numeric code that represents a specific
     *                   exception that is thrown.
     * @param cause  previous cause of exception.
     */
    public CompositeException( ResourceBundleFactory bundleFactory,
                               int errorCode, Object[] args,
                               Throwable cause)
    {
        super( cause);
        setBundleFactory( bundleFactory);
        setErrorCode( errorCode);
        setArgs( args);
    }

    /**
     * Displays a formatted exception message.
     *
     * @return String  Formatted exception message. 
     */
    public String getMessage() 
    {
        return getMessage(Locale.getDefault());
    }

    /**
     * Displays a formatted exception message in the given locale.
     *
     * @return String  Formatted exception message. 
     */
    public String getMessage(Locale locale) 
    {
        if (errorCode == -1) {
            if (args != null && args.length > 0 && args[0] != null)
              return args[0].toString();
            else if (getCause() != null)
              return getCause().getMessage();
            else
              return super.getMessage();
        } else if (getBundleFactory() != null) {
            return MsgFormatter.format(getBundleFactory(), locale, domain, errorCode, args);
        }
        return MsgFormatter.format(domain, errorCode, args);
    }

    /**
     * Returns the domain
     *
     * @return the non-null domain as specified in the constructor.
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Returns the error code
     *
     * @return the error code as specified in the constructor.
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Returns the arguments of the exception
     *
     * @return the arguments code as specified in the constructor.
     */
    public Object[] getArgs() {
        return args;
    }

    protected void setDomain(String domain) {
        this.domain = domain;
    }

    protected void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    protected void setArgs(Object[] args) {
        this.args = args;
    }

    protected void setBundleFactory(ResourceBundleFactory bundleFactory) {
        this.bundleFactory = bundleFactory;
    }

    public ResourceBundleFactory getBundleFactory() {
        return bundleFactory;
    }
    
    /**
     * This method should be over-rided for any CompositeException that never
     * prints its stack trace.
     * @return true if the stack trace is to be printed in the log.  false 
     * otherwise
     */
    public boolean isPrintStackTrace() {
        if (this.errorCode != -1) {
            int digit = this.errorCode / 1000000;
            
            // If the code is 3, don't print the exception unless there's an
            // even code further down the chain.
            if (errorCode / 1000000 == 3) {
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
            return this.errorCode == -1;
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
}

//

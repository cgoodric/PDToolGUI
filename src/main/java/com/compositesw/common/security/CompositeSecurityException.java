/**
 * Copyright 2005 Composite Software, Inc. All Rights Reserved.
 */
package com.compositesw.common.security;

import com.compositesw.common.*;

/**
 * CompositeSecurityException is responsible for organizing all security
 * related exceptions for Composite.
 *
 * @author sahuero
 */
public class CompositeSecurityException extends CompositeException {
    /**
     * Construct a new Composite exception with its cause.
     *
     * @param cause previous cause of exception.
     */
    public CompositeSecurityException(Throwable cause) {
        super(cause);
    }

    /**
     * Construct a new Composite exception with an error code from a
     * domain.
     *
     * @param errorCode - a numeric code that represents a specific
     *                  exception that is thrown.
     */
    public CompositeSecurityException(int errorCode, Object[] args) {
        super(SecurityConstants.DOMAIN, errorCode, args);
    }

    /**
     * Construct a new Composite exception with an error code and
     * a resource bundle.
     *
     * @param bundleParams - a set of parameters used to load a resource
     *                     bundle
     * @param errorCode    - a numeric code that represents a specific
     *                     exception that is thrown.
     */
    public CompositeSecurityException(ResourceBundleParams bundleParams, int errorCode, Object[] args) {
        super(bundleParams, errorCode, args);
    }

    /**
     * Constructs a new Composite exception with an error code from a
     * domain and a previous cause.
     *
     * @param errorCode a numeric code that represents a specific
     *                  exception that is thrown.
     * @param cause     previous cause of exception.
     */
    public CompositeSecurityException(int errorCode, Object[] args, Throwable cause) {
        super(SecurityConstants.DOMAIN, errorCode, args, cause);
    }

    /**
     * Constructs a new Composite exception with an error code from a
     * domain and a previous cause.
     *
     * @param bundleParams - a set of parameters used to load a resource
     *                     bundle
     * @param errorCode    - a numeric code that represents a specific
     *                     exception that is thrown.
     * @param errorCode    a numeric code that represents a specific
     *                     exception that is thrown.
     * @param cause        previous cause of exception.
     */
    public CompositeSecurityException(ResourceBundleParams bundleParams, int errorCode, Object[] args, Throwable cause) {
        super(bundleParams, errorCode, args, cause);
    }

    public CompositeSecurityException(String message, Exception e) {
        super( message, e );
    }
}

  /**
 * Copyright 2003 Composite Software, Inc. All Rights Reserved.
 */
package com.compositesw.common;

import java.text.*;
import java.util.*;

/**
 * @author kevin
 */
public abstract class CompositeResourceBundle extends ListResourceBundle {

    // NOTE: the error code parts should certainly be bitmasked.
    // instead of using poisitional digits.

    public static final int LOG_BUCKET_AUDIT      = 1000000;
    public static final int LOG_BUCKET_SERVER     = 2000000;
    public static final int LOG_BUCKET_DATASOURCE = 3000000;
    public static final int LOG_BUCKET_MANAGER    = 4000000;
    public static final int LOG_BUCKET_MESSAGE    = 9000000;

    public static final int LOG_LEVEL_DEBUG = 0;
    public static final int LOG_LEVEL_INFO = 300000;
    public static final int LOG_LEVEL_WARNING = 600000;
    public static final int LOG_LEVEL_ERROR = 900000;

    public static final int LOG_AUDIT_DEBUG   = LOG_BUCKET_AUDIT + LOG_LEVEL_DEBUG;
    public static final int LOG_AUDIT_INFO    = LOG_BUCKET_AUDIT + LOG_LEVEL_INFO;
    public static final int LOG_AUDIT_WARNING = LOG_BUCKET_AUDIT + LOG_LEVEL_WARNING;
    public static final int LOG_AUDIT_ERROR   = LOG_BUCKET_AUDIT + LOG_LEVEL_ERROR;

    public static final int LOG_SERVER_DEBUG   = LOG_BUCKET_SERVER + LOG_LEVEL_DEBUG;
    public static final int LOG_SERVER_INFO    = LOG_BUCKET_SERVER + LOG_LEVEL_INFO;
    public static final int LOG_SERVER_WARNING = LOG_BUCKET_SERVER + LOG_LEVEL_WARNING;
    public static final int LOG_SERVER_ERROR   = LOG_BUCKET_SERVER + LOG_LEVEL_ERROR;

    public static final int LOG_DATASOURCE_DEBUG   = LOG_BUCKET_DATASOURCE + LOG_LEVEL_DEBUG;
    public static final int LOG_DATASOURCE_INFO    = LOG_BUCKET_DATASOURCE + LOG_LEVEL_INFO;
    public static final int LOG_DATASOURCE_WARNING = LOG_BUCKET_DATASOURCE + LOG_LEVEL_WARNING;
    public static final int LOG_DATASOURCE_ERROR   = LOG_BUCKET_DATASOURCE + LOG_LEVEL_ERROR;

    public static final int LOG_MANAGER_DEBUG   = LOG_BUCKET_MANAGER + LOG_LEVEL_DEBUG;
    public static final int LOG_MANAGER_INFO    = LOG_BUCKET_MANAGER + LOG_LEVEL_INFO;
    public static final int LOG_MANAGER_WARNING = LOG_BUCKET_MANAGER + LOG_LEVEL_WARNING;
    public static final int LOG_MANAGER_ERROR   = LOG_BUCKET_MANAGER + LOG_LEVEL_ERROR;

    public static final int LOG_MESSAGE_DEBUG   = LOG_BUCKET_MESSAGE + LOG_LEVEL_DEBUG;
    public static final int LOG_MESSAGE_INFO    = LOG_BUCKET_MESSAGE + LOG_LEVEL_INFO;
    public static final int LOG_MESSAGE_WARNING = LOG_BUCKET_MESSAGE + LOG_LEVEL_WARNING;
    public static final int LOG_MESSAGE_ERROR   = LOG_BUCKET_MESSAGE + LOG_LEVEL_ERROR;

    private String domain;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * Return a formated message from this bundle
     *
     * @param id The message identifier.
     * @param args (Optional) The message arguments.
     * @return the formatted message.
     */
    public String getMessage(int id,
                             Object... args) {
        return getMessage(this, id, args);
    }

    /**
     * Returns a formatted message from the specified bundle.
     *
     * @param bundle  The resource bundle that contains the message.
     * @param id The message identifier.
     * @param args (Optional) The message arguments.
     * @return
     */
    public static String getMessage(CompositeResourceBundle bundle,
                                    int id,
                                    Object... args) {
        try {
            String format = bundle.getString(Integer.toString(id));
            return MessageFormat.format(format, args);
        } catch (Throwable ignore) {
            String sep = System.getProperty("line.separator");
            StringBuffer message = new StringBuffer();
            message.append("Error ").append(bundle.getDomain()).append("-").append(id).append(". ");
            message.append("No description is available. ").append(sep);
            if (args != null) {
                message.append("Error arguments [");
                for (int i = 0; i < args.length - 1; i++)
                    message.append('\"').append(args[i]).append('\"').append(",");
                message.append('\"').append(args[args.length - 1]).append('\"').append("]").append(sep);
            }
            return message.toString();
        }
    }
}

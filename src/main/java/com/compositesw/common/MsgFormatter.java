/**
 * COPYRIGHT:
 *  (C)Copyright 2002 Composite Software, Inc.
 *  All Rights Reserved.
 *
 * @author Patrick Chan
 * 
 */

package com.compositesw.common;

import com.compositesw.extension.CustomProcedureException;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to locate a message format pattern and apply it to
 * a set of values to produce a formatted message.
 */
public class MsgFormatter {
    private static final Logger logger = LoggerFactory.getLogger (MsgFormatter.class);

    static final String bundleDir = "com.compositesw.common.logging";

    static String defaultMessage = "Error [{0}-{1}]: {2}";

    /**
     * Returns a summary for the supplied throwable suitable for display to
     * the user.
     */
    public static String format(Throwable t) {
        int numEntries = 0;
        StringBuffer buf = new StringBuffer();
        while (t != null) {
            if (t instanceof CompositeRuntimeException) {
                CompositeRuntimeException ce = (CompositeRuntimeException)t;
                int code = ce.getCode();
                if (code >= 0) {
                    if (numEntries > 0) {
                        buf.append("\n");
                    }
                    String s = format(ce.getBundleFactory(), null,
                                      ce.getModule(), code, ce.getArgs());
                    buf.append(s);
                    numEntries++;
                }
            } else if (t instanceof CompositeException) {
                // TODO: remove this later
                CompositeException ce = (CompositeException)t;
                int code = ce.getErrorCode();
                if (code >= 0) {
                    if (numEntries > 0) {
                        buf.append("\n");
                    }
                    String s = format(ce.getBundleFactory(), null,
                                      ce.getDomain(), code, ce.getArgs());
                    buf.append(s);
                    numEntries++;
                }
            } else {
                if (numEntries == 0) {
                    String s = format(null, null, "common", -1, null);
                    buf.append(s);
                }
                buf.append("\n");
                if (t.getMessage() != null) {
                    buf.append(breakLines("Cause: "
                        +t.getMessage()));
                } else {
                    buf.append(breakLines("Cause: "
                        +t.getClass().getName()));
                }
                numEntries++;
                break;
            }
            t = t.getCause();
        }
        if (numEntries == 0) {
            String s = format(null, null, "common", -1, null);
            buf.append(s);
        }
        return buf.toString();
    }

    /**
     * The message format string is located using the resource bundle
     * params and code.  The values in args are applied to the format
     * string and the resulting formatted string is returned.  If
     * bundle is null, a default mechanism is used to find the format
     * string.
     *
     * @param bundleFactory possibly-null
     * @param module non-null 
     * @param args non-null array of arguments for the message format string.
     */
    public static String format(ResourceBundleFactory bundleFactory,
                                Locale locale, String module, int code, 
                                Object[] args) {
        ResourceBundle bundle;
        try {
            if (bundleFactory == null) {
                // TODO: remove the hardcoded package name
                String name = bundleDir+"."+module;
                if (locale == null)
                  bundle = ResourceBundle.getBundle(name);
                else
                  bundle = ResourceBundle.getBundle(name, locale);
            }
            else {
                bundle = bundleFactory.getBundle(locale);
            }
        } catch (Throwable e) {
            return error(2900101, module, e, code, args);
        }
        try {
            if(module == null && bundle instanceof CompositeResourceBundle){
                module = ((CompositeResourceBundle)bundle).getDomain();
            }
            String format = Integer.toString(code);
            do {
                format = bundle.getString(format).trim();
            } while (format.matches("\\d+"));

            String s = MessageFormat.format(format, args);
            int firstDigit = code / 1000000;
            if (code >= 0 && firstDigit != 9) {
                s += "  ["+module+"-"+code+"]";
            }
            if (firstDigit != 9) {
                s = breakLines(s);
            }
            return s;
        } catch (Throwable e) {
            return error(2900100, module, e, code, args);
        }
    }

    static int MAXLEN = 100;

    /*
     * Breaks a string so that each line is <= MAXLEN.
     */
    static public String breakLines(String s) {
        if ((s.indexOf('\n') < 0 || s.indexOf('\n') > MAXLEN) && s.length() > MAXLEN) {
            s = s.trim();
            StringBuffer buf = new StringBuffer();
            int maxlen = MAXLEN;

            while (s.length() > maxlen) {
                int ix = s.lastIndexOf(' ', maxlen);
                if (ix <= 0) {
                    ix = s.indexOf(' ', maxlen);
                    if (ix <= 0) {
                        break;
                    }
                }
                if (buf.length() > 0) {
                    buf.append("\n    ");
                }
                buf.append(s.substring(0, ix).trim());
                s = s.substring(ix+1).trim();
                maxlen = MAXLEN-4;
            }

            // Append the last bit
            if (s.length() > 0) {
                if (buf.length() > 0) {
                    buf.append("\n    ");
                }
                buf.append(s);
            }
            s = buf.toString();
        }
        return s;
    }

    /**
     * Convenience method that is equivalent to
     *     format(null, module, code, args).
     */
    public static String format(String module, int code, Object...args) {
        return format(null, null, module, code, args);
    }

    /**
     * Convenience method that is equivalent to
     *     format(null, locale, module, code, args).
     */
    public static String format(Locale locale, String module, int code, Object[] args) {
        return format(null, locale, module, code, args);
    }

    /*
     * The returned string is used if there is a formatting problem.
     */
    static String error(int newCode, String module, 
                        Throwable e, int code, Object[] args) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0 ; args != null && i < args.length ; i++) {
            if (i > 0) {
                buf.append(", ");
            }
            if (args[i] == null) {
                buf.append("NULL");
            } else {
                buf.append(args[i].toString());
            }
        }
        args = new Object[]{module, ""+code, buf.toString()};
        return MessageFormat.format(defaultMessage, args);
    }

    /**
     * @see CompositeRuntimeException#isPrintStackTrace()
     * @see CompositeException#isPrintStackTrace()
     */
    public static boolean isPrintStackTrace(Throwable t) {
        if (t instanceof CompositeRuntimeException) {
            CompositeRuntimeException ce = (CompositeRuntimeException)t;
            return ce.isPrintStackTrace();
        } else if (t instanceof CompositeException) {
            CompositeException ce = (CompositeException)t;
            return ce.isPrintStackTrace();
        } else if (t instanceof CustomProcedureException) {
            Throwable t2 = t.getCause();
            if (t2 != null) {
                return isPrintStackTrace(t2);
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    static {
        try {
            // Get the default message
            ResourceBundle bundle = ResourceBundle.getBundle(bundleDir+".common");
            defaultMessage = bundle.getString("2900100").trim();
        } catch (Throwable e) {
            logger.info(e.getMessage());
        }
    }
}

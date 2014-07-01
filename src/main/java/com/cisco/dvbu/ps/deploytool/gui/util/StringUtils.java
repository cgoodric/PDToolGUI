package com.cisco.dvbu.ps.deploytool.gui.util;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.compositesw.common.security.EncryptionManager;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.PreferencesManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StringUtils {
    private StringUtils() {}

    private static final Logger log = LoggerFactory.getLogger (StringUtils.class);
    private static List<String> indents = new ArrayList<String>();
    
    static {
        String indent = "", indentStr = "";
        
        for (int i = 0; i < PreferencesManager.getInstance().getXmlIndentWidth(); i++)
            indent += " ";
            
        for (int i = 0; i < 20; i++) {
            if (i > 0)
                indentStr += indent;
            
            indents.add (indentStr);
        }
    }
    
    /**
     * Returns a precalculated indent.
     * 
     * @param indent  The number of indents.
     * @return        A string containing spaces numbering indent width X indents.
     */
    public static String getIndent (int indent) {
        return indents.get (indent);
    }

    /**
     * Finds an occurrence of a regular expression match in a {@link String} and returns the match.
     * The value of the occurrence input value determines which occurrence to return (numbered
     * starting at 1 from left to right. Use negative values to number occurrences from right
     * to left.) If no match is found, then null is returned. If a null value is passed in 
     * as the value of any of the inputs, a null is returned. Zero may not be used as a value
     * for an occurrence. Throws a {@link PatternSyntaxException} if the input regular express
     * cannot be validated/compiled.
     *   
     * @param inputText  The text to search.
     * @param regex      The regular expression to apply.
     * @param occurrence The numbered occurrence left to right (negative number indicates start from right and go left.)
     * @return           The string matching the regular expression.
     * @throws PatternSyntaxException
     */
    public static String regexFind (
        String inputText,
        String regex,
        int occurrence
    ) throws PatternSyntaxException {
        Pattern p;
        Matcher m;
        String result = null;
        boolean found = false;
        
        // sanity check the inputs.
        //
        if (inputText == null || regex == null || occurrence == 0)
            return null;
        
        p = Pattern.compile (regex);
        m = p.matcher (inputText);
    
        // the Matcher object doesn't allow for starting at the end of the text and
        // working backwards, so we'll have to find out how many occurrences there
        // are, do a little math, and reset the Matcher object.
        //
        if (occurrence < 0) {
          int maxOccurrences = 0;
          
          while (m.find()) 
            maxOccurrences++;
          
          if (maxOccurrences - occurrence + 1 < 1) {
            return null;
          } else {
            occurrence = maxOccurrences + occurrence + 1;
          }
          
          m.reset();
        }
    
        for (int i = 0; i < occurrence; i++)
          found = m.find();
          
        if (found)
          result = m.group();
        
        return result;
    }

    /**
     * Returns the base file name of a full operating system path. It is OS agnostic.
     * 
     * @param path The path string
     * @return     The file name
     */
    public static String basename (String path) {
        try {
            return regexFind (path, "(?<=[/\\\\])[^/\\\\]+(?=[/\\\\]?)", -1);
        } catch (Exception e) {
            log.error ("Error compiling pattern for basename() method!");
            return null;
        }
    }

    /**
     * Returns the parent folder of a full operating system path. It is OS agnostic.
     * 
     * @param path The path string
     * @return     The parent folder path
     */
    public static String dirname (String path) {
        try {
            return regexFind (path, ".*(?=[\\\\/][^\\\\/]{1,255}[\\\\/]?$)", 1);
        } catch (Exception e) {
            log.error ("Error compiling pattern for dirname() method!");
            return null;
        }
    }

    /**
     * Returns the parent folder of a full operating system path. It is OS agnostic.
     * 
     * @param filename The file's name
     * @return         The file's name without the suffix
     */
    public static String removeFileSuffix (String filename) {
        return filename.replaceAll ("\\.[^\\.]*", "");
    }

    /**
     * <p>
     * Used to encrypt the password field. if the password is null or has already been encrypted
     * the password is simply returned.
     * </p>
     *
     * @param p The string to encrypt
     * @return The encrypted string.
     */
    public static String encryptPassword (String p) {
        if (p != null && !p.startsWith ("Encrypted:")) {
            try {
                p = "Encrypted:" + EncryptionManager.encrypt (p);
            } catch (Exception e) {
                log.error (e.getMessage ());
            }
        }

        return p;
    }

    /**
     * <p>
     * Converts a StackTraceElement array to a string.
     * </p>
     *
     * @param e The exception thrown.
     * @return  The exception's stack trace as a string.
     */
    public static String stackTraceToString(Throwable e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}

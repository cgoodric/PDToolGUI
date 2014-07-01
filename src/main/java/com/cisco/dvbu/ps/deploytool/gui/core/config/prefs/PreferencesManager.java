package com.cisco.dvbu.ps.deploytool.gui.core.config.prefs;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.cisco.dvbu.ps.deploytool.gui.PDToolGUIConfiguration;

import com.cisco.dvbu.ps.deploytool.gui.core.runtime.file.FilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Manager/factory class for preferences. Seems kinda dumb to have both a DAO and a manager class,
 * but the DAO is for GUI interactions and the manager works with the other java classes.
 * </p>
 * <p>
 * This class is designed to be worked with statically (hence, no public constructor.) It provides
 * access to a single Preferences object (initialized from the pdtoolgui.yml file) using the getInstance()
 * method. Updates to preferences should be done through the provided Preferences instance and written
 * back to the pdtoolgui.yml file using the serialize() method.
 * </p>
 *
 * @author Calvin Goodrich
 * @version 1.0
 */
public class PreferencesManager {
    
    private static Preferences p = null;
    private static final Logger log = LoggerFactory.getLogger (PreferencesManager.class);
    private static Pattern variableNameREPattern;

    static {
        try {
            variableNameREPattern = Pattern.compile ("^\\w+(?=:)"); // i.e. lines that start with "var:" but leaving off the "="
        } catch (Exception e) {
            log.error ("Unable to compile regular expression pattern: " + e.getMessage());
        }
    }

    /**
     * <p>
     * Private constructor. This class will never be instantiated. Use the static methods.
     * </p>
     */
    private PreferencesManager () {}
    
    /**
     * <p>
     * Initializes the Preferences static class.
     * </p>
     * 
     * @param  conf  The {@link PDToolGUIConfiguration} class to use to initialize the preferences.
     */
    public static void init (PDToolGUIConfiguration conf) {
        p = new Preferences (conf);
    }
    
    /**
     * <p>
     * Returns the current set of preferences.
     * </p>
     * 
     * @return     The value.
     */
    public static Preferences getInstance() {
        return p;
    }
    
    public static ResultMessage serialize() {
        final String ymlPath = FilesDAO.getPdtHome() + "/gui" + File.separatorChar + "pdtoolgui.yml";
        File f;
        BufferedReader br = null;
        BufferedWriter bw = null;
        Matcher m = null;
        
        f = new File (ymlPath);

        if (! f.exists()) {
            return new ResultMessage ("error", "Error retrieving file \"" + ymlPath + "\": file does not exist.", null);
        }
        
        if (! f.canWrite()) {
            return new ResultMessage ("error", "Error retrieving file \"" + ymlPath + "\": file is not writeable.", null);
        }
        
        try {
            File fb = new File (ymlPath + ".bak");
            String line;
            int lineNum = 0;
            
            log.debug ("Copying \"" + f.getAbsolutePath() + "\" to \"" + fb.getAbsolutePath() + "\".");
            
            // windows doesn't let you copy a file to an existing file's name
            //
            if (fb.exists() && fb.canWrite())
                FileUtils.deleteQuietly (fb);

            FileUtils.copyFile (f, fb);
    
            // open the source file for reading
            //
            br = new BufferedReader (new FileReader (fb));
            bw = new BufferedWriter (new FileWriter (f, false));
            
            while ((line = br.readLine()) != null) {
                lineNum++;
                
                String varName = null;
            
                // extract the variable name out of the line.
                //
                m = variableNameREPattern.matcher (line);
                if (m.find()) {
                    varName = m.group();
                
                    // if the beginning part of the line didn't look like a variable, skip it.
                    //
                    if (varName == null) {
                        log.debug ("unrecognized line in \"" + ymlPath + "\" line number " + lineNum + ": " + line);
                        continue;
                    } else if (varName.equals ("backupFiles")) {
                        bw.write ("backupFiles: " + p.getBackupFiles());
                        bw.newLine();
                    } else if (varName.equals ("defaultProfile")) {
                        bw.write ("defaultProfile: " + p.getDefaultProfile());
                        bw.newLine();
                    } else if (varName.equals ("defaultServer")) {
                        bw.write ("defaultServer: " + p.getDefaultServer());
                        bw.newLine();
                    } else if (varName.equals ("restrictAccessToLocalhost")) {
                        bw.write ("restrictAccessToLocalhost: " + p.getRestrictAccessToLocalhost());
                        bw.newLine();
                    } else if (varName.equals ("xmlIndentWidth")) {
                        bw.write ("xmlIndentWidth: " + p.getXmlIndentWidth());
                        bw.newLine();
                    } else {
                        bw.write (line);
                        bw.newLine();
                    }
                        
                } else {
                    bw.write (line);
                    bw.newLine();
                }
            }
            
            bw.flush();

            // if user preferences indicate not to save backups of edited files, ditch the backup.
            //
            if (PreferencesManager.getInstance().getBackupFiles().equals ("false")) {
                if (fb.exists() && fb.canWrite())
                    FileUtils.deleteQuietly (fb);
            }

        } catch (Exception e) {
            return new ResultMessage ("error", "Error: " + e.getMessage(), null);
        } finally {
            try {
                if (bw != null) bw.close();
            } catch (Exception e2) {
                return new ResultMessage ("error", "Error closing temp file \"" + ymlPath + "_tmp\": " + e2.getMessage(), null);
            }

            try {
                if (br != null) br.close();
            } catch (Exception e2) {
                return new ResultMessage ("error", "Error closing properties file \"" + ymlPath + "\": " + e2.getMessage(), null);
            }

        }
        
        return new ResultMessage ("success", "Preferences successfully updated.", null);
    }
}

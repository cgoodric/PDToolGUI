package com.cisco.dvbu.ps.deploytool.gui.core.config.prefs;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.cisco.dvbu.ps.deploytool.gui.PDToolGUIConfiguration;

/**
 * <p>
 * Bean object for user preferences. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 *
 * @author Calvin Goodrich
 * @version 1.0
 */
public class Preferences {
    String backupFiles;
    String defaultProfile;
    String defaultServer;
    boolean updateDSAttributes = false; // this is a flag passed by the GUI. not a saved preference.
    String restrictAccessToLocalhost;
    int xmlIndentWidth = 4;

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public Preferences () {}

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public Preferences (PDToolGUIConfiguration conf) {
        this.backupFiles = "" + conf.getBackupFiles().matches ("(?i)^(yes|true|on|1)$");
        this.defaultProfile = conf.getDefaultProfile();
        this.defaultServer = conf.getDefaultServer();
        this.restrictAccessToLocalhost = "" + conf.getRestrictAccessToLocalhost().matches ("(?i)^(yes|true|on|1)$");
        this.xmlIndentWidth = conf.getXmlIndentWidth();
    }

    /**
     * <p>
     * Sets the <code>backupFiles</code> field.
     * </p>
     * 
     * @param  backupFiles  Indicates whether a backup file should be created before serializing modules, configuration files, etc.
     */
    public void setBackupFiles (String backupFiles) {
        this.backupFiles = (backupFiles != null) ? "" + backupFiles.matches ("(?i)^(yes|true|on|1)$") : null;
    }

    /**
     * <p>
     * Returns the value of the <code>backupFiles</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getBackupFiles () {
        return backupFiles;
    }

    /**
     * <p>
     * Sets the <code>defaultProfile</code> field.
     * </p>
     * 
     * @param  defaultProfile  The deployment profile to use when gathering data source types, attribute definitions, etc.
     */
    public void setDefaultProfile (String defaultProfile) {
        this.defaultProfile = defaultProfile;
    }

    /**
     * <p>
     * Returns the value of the <code>defaultProfile</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getDefaultProfile () {
        return defaultProfile;
    }

    /**
     * <p>
     * Sets the <code>defaultServer</code> field.
     * </p>
     * 
     * @param  defaultServer  The server to use when gathering data source types, attribute definitions, etc.
     */
    public void setDefaultServer (String defaultServer) {
        this.defaultServer = defaultServer;
    }

    /**
     * <p>
     * Returns the value of the <code>defaultServer</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getDefaultServer () {
        return defaultServer;
    }

    /**
     * <p>
     * Sets the <code>updateDSAttributes</code> field.
     * </p>
     * 
     * @param  updateDSAttributes  The server to use when gathering data source types, attribute definitions, etc.
     */
    public void setUpdateDSAttributes (boolean updateDSAttributes) {
        this.updateDSAttributes = updateDSAttributes;
    }

    /**
     * <p>
     * Returns the value of the <code>updateDSAttributes</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isUpdateDSAttributes () {
        return updateDSAttributes;
    }

    /**
     * <p>
     * Sets the <code>restrictAccessToLocalhost</code> field.
     * </p>
     * 
     * @param  restrictAccessToLocalhost  Indicates whether access to browsers running on the local host.
     */
    public void setRestrictAccessToLocalhost (String restrictAccessToLocalhost) {
        this.restrictAccessToLocalhost = (restrictAccessToLocalhost != null) ? "" + restrictAccessToLocalhost.matches ("(?i)^(yes|true|on|1)$") : null;
    }

    /**
     * <p>
     * Returns the value of the <code>restrictAccessToLocalhost</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getRestrictAccessToLocalhost () {
        return restrictAccessToLocalhost;
    }

    /**
     * <p>
     * Sets the <code>xmlIndentWidth</code> field.
     * </p>
     * 
     * @param  xmlIndentWidth  The indent width used for serializing XML documents.
     */
    public void setXmlIndentWidth (int xmlIndentWidth) {
        this.xmlIndentWidth = xmlIndentWidth;
    }

    /**
     * <p>
     * Returns the value of the <code>xmlIndentWidth</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getXmlIndentWidth () {
        return xmlIndentWidth;
    }
}

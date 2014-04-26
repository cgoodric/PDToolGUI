package com.cisco.dvbu.ps.deploytool.gui.core.runtime.file;

import com.cisco.dvbu.ps.deploytool.gui.core.config.server.Server;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Bean object for servers. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class FileRecord {
    private static final Logger log = LoggerFactory.getLogger (Server.class);
    
    private String operation;
    private String origname;
    private String name;
    private String path;
    private String dateModified;
    private int fileType;

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public FileRecord () {}

    /**
     * <p>
     * Sets the <code>operation</code> field.
     * </p>
     * 
     * @param  operation  The operation that will be performed with this bean: "add", "edit", "copy", or "delete".
     */
    public void setOperation (String operation) {
        this.operation = operation;
    }

    /**
     * <p>
     * Returns the value of the <code>operation</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getOperation () {
        return operation;
    }

    /**
     * <p>
     * Sets the <code>origname</code> field.
     * </p>
     *
     * @param origname The operation that will be performed with this bean: "add", "edit", "copy", or "delete".
     */
    public void setOrigname (String origname) {
        this.origname = origname;
    }

    /**
     * <p>
     * Returns the value of the <code>origname</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getOrigname () {
        return origname;
    }

    /**
     * <p>
     * Sets the <code>name</code> field.
     * </p>
     * 
     * @param  name  The operation that will be performed with this bean: "add", "edit", "copy", or "delete".
     */
    public void setName (String name) {
        this.name = name;
    }

    /**
     * <p>
     * Returns the value of the <code>name</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getName () {
        return name;
    }

    /**
     * <p>
     * Sets the <code>path</code> field.
     * </p>
     * 
     * @param  path  The path to the file on the filesystem.
     */
    public void setPath (String path) {
        this.path = path;
    }

    /**
     * <p>
     * Returns the value of the <code>path</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getPath () {
        return path;
    }

    /**
     * <p>
     * Sets the <code>dateModified</code> field.
     * </p>
     * 
     * @param  dateModified  The date the file was last modified.
     */
    public void setDateModified (String dateModified) {
        this.dateModified = dateModified;
    }

    /**
     * <p>
     * Returns the value of the <code>dateModified</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getDateModified () {
        return dateModified;
    }

    /**
     * <p>
     * Sets the <code>fileType</code> field.
     * </p>
     * 
     * @param  fileType  The operation that will be performed with this bean: "add", "edit", "copy", or "delete".
     */
    public void setFileType (int fileType) {
        this.fileType = fileType;
    }

    /**
     * <p>
     * Returns the value of the <code>fileType</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getFileType () {
        return fileType;
    }
}

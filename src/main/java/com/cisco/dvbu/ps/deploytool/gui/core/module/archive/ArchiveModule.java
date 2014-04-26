package com.cisco.dvbu.ps.deploytool.gui.core.module.archive;

import java.util.List;

/**
 * <p>
 * Bean object for archive module records. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class ArchiveModule {
    String path;
    List<Archive> archives;
    
    /**
     * <p>
     * Constructor.
     * </p>
     */
    public ArchiveModule() {}

    /**
     * <p>
     * Sets the <code>path</code> field.
     * </p>
     * 
     * @param  path  The filesystem path to this archive module.
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
     * Sets the <code>archives</code> field.
     * </p>
     * 
     * @param  archives  The list of archive records.
     */
    public void setArchives (List<Archive> archives) {
        this.archives = archives;
    }

    /**
     * <p>
     * Returns the value of the <code>archives</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<Archive> getArchives () {
        return archives;
    }
    
    /**
     * <p>
     * Locates a specific archive record.
     * </p>
     * <p>
     * This SHOULD be returning a defensive copy of the archive, but for performance reasons it simply returns a reference.
     * </p>
     * 
     * @param  id  The id of the archive to look for.
     * @return     The archive.
     */
    public Archive findById (String id) {
        if (this.archives == null)
            return null;

        for (Archive tmp : this.archives) {
            if (tmp.getId().equals (id))
                return tmp;
        }
        
        return null;
    }
}

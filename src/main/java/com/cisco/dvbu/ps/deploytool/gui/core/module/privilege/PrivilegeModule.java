package com.cisco.dvbu.ps.deploytool.gui.core.module.privilege;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import java.util.List;

/**
 * <p>
 * Bean object for privilege module records. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class PrivilegeModule {
    String path;
    List<Privilege> privileges;
    
    /**
     * <p>
     * Constructor.
     * </p>
     */
    public PrivilegeModule () {
        super ();
    }

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
     * Sets the <code>privileges</code> field.
     * </p>
     * 
     * @param  privileges  The list of archive records.
     */
    public void setPrivileges (List<Privilege> privileges) {
        this.privileges = privileges;
    }

    /**
     * <p>
     * Returns the value of the <code>privileges</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<Privilege> getPrivileges () {
        return privileges;
    }
    
    /**
     * <p>
     * Locates a specific privilege record.
     * </p>
     * <p>
     * This SHOULD be returning a defensive copy of the archive, but for performance reasons it simply returns a reference.
     * </p>
     * 
     * @param  id  The id of the privilege to look for.
     * @return     The privilege.
     */
    public Privilege findById (String id) {
        if (this.privileges == null)
            return null;

        for (Privilege tmp : this.privileges) {
            if (tmp.getId().equals (id))
                return tmp;
        }
        
        return null;
    }
}

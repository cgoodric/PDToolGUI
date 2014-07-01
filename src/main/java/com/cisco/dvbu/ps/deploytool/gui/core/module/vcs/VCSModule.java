package com.cisco.dvbu.ps.deploytool.gui.core.module.vcs;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import java.util.List;

/**
 * <p>
 * Bean object for VCS module records. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class VCSModule {

    String path;
    List<VCS> vcsList;
    
    /**
     * <p>
     * Constructor.
     * </p>
     */
    public VCSModule () {
        super ();
    }
    /**
     * <p>
     * Sets the <code>path</code> field.
     * </p>
     * 
     * @param  path  The filesystem path to this VCS module.
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
     * Sets the <code>vcsList</code> field.
     * </p>
     * 
     * @param  vcsList  The list of VCS records.
     */
    public void setVcsList (List<VCS> vcsList) {
        this.vcsList = vcsList;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsList</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<VCS> getVcsList () {
        return vcsList;
    }
    
    /**
     * <p>
     * Locates a specific VCS record.
     * </p>
     * <p>
     * This SHOULD be returning a defensive copy of the VCS, but for performance reasons it simply returns a reference.
     * </p>
     * 
     * @param  id  The id of the VCS record to look for.
     * @return     The VCS record.
     */
    public VCS findById (String id) {
        if (this.vcsList == null)
            return null;

        for (VCS tmp : this.vcsList) {
            if (tmp.getId().equals (id))
                return tmp;
        }
        
        return null;
    }
}

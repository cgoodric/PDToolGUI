package com.cisco.dvbu.ps.deploytool.gui.core.module.rebind;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import java.util.List;

/**
 * <p>
 * Bean object for rebind module records. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class RebindModule {
    String path;
    List<Rebind> rebinds;

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public RebindModule () {
        super ();
    }
    
    /**
     * <p>
     * Sets the <code>path</code> field.
     * </p>
     * 
     * @param  path  The filesystem path to this data source module.
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
     * Sets the <code>rebinds</code> field.
     * </p>
     * 
     * @param  rebinds  The list of rebind records.
     */
    public void setRebinds (List<Rebind> rebinds) {
        this.rebinds = rebinds;
    }

    /**
     * <p>
     * Returns the value of the <code>rebinds</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<Rebind> getRebinds () {
        return rebinds;
    }
    
    /**
     * <p>
     * Locates a specific rebind record.
     * </p>
     * <p>
     * This SHOULD be returning a defensive copy of the rebind, but for performance reasons it simply returns a reference.
     * </p>
     * 
     * @param  id  The id of the rebind to look for.
     * @return     The rebind.
     */
    public Rebind findById (String id) {
        if (this.rebinds == null)
            return null;

        for (Rebind tmp : this.rebinds) {
            if (tmp.getId().equals (id))
                return tmp;
        }
        
        return null;
    }
}

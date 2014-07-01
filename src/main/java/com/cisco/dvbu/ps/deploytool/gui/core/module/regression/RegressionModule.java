package com.cisco.dvbu.ps.deploytool.gui.core.module.regression;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import java.util.List;

/**
 * <p>
 * Bean object for regression module records. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class RegressionModule {

    String path;
    List<Regression> regressions;

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public RegressionModule () {
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
     * Sets the <code>regressions</code> field.
     * </p>
     * 
     * @param  regressions  The list of regression records.
     */
    public void setRegressions (List<Regression> regressions) {
        this.regressions = regressions;
    }

    /**
     * <p>
     * Returns the value of the <code>regressions</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<Regression> getRegressions () {
        return regressions;
    }
    
    /**
     * <p>
     * Locates a specific regression record.
     * </p>
     * <p>
     * This SHOULD be returning a defensive copy of the rebind, but for performance reasons it simply returns a reference.
     * </p>
     * 
     * @param  id  The id of the regression to look for.
     * @return     The rebind.
     */
    public Regression findById (String id) {
        if (this.regressions == null)
            return null;

        for (Regression tmp : this.regressions) {
            if (tmp.getId().equals (id))
                return tmp;
        }
        
        return null;
    }
}

package com.cisco.dvbu.ps.deploytool.gui.core.module.data_source;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import java.util.List;

/**
 * <p>
 * Bean object for data sources module records. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class DataSourceModule {
    String path;
    List<DataSource> dataSources;
    
    /**
     * <p>
     * Constructor.
     * </p>
     */
    public DataSourceModule() {}

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
     * Sets the <code>dataSources</code> field.
     * </p>
     * 
     * @param  dataSources  The list of data source records.
     */
    public void setDataSources (List<DataSource> dataSources) {
        this.dataSources = dataSources;
    }

    /**
     * <p>
     * Returns the value of the <code>dataSources</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<DataSource> getDataSources () {
        return dataSources;
    }
    
    /**
     * <p>
     * Locates a specific data source record.
     * </p>
     * <p>
     * This SHOULD be returning a defensive copy of the data source, but for performance reasons it simply returns a reference.
     * </p>
     * 
     * @param  id  The id of the data source to look for.
     * @return     The archive.
     */
    public DataSource findById (String id) {
        if (this.dataSources == null)
            return null;

        for (DataSource tmp : this.dataSources) {
            if (tmp.getId().equals (id))
                return tmp;
        }
        
        return null;
    }
}

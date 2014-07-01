package com.cisco.dvbu.ps.deploytool.gui.core.module.server_attribute;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import java.util.List;

/**
 * <p>
 * Bean object for server attribute module records. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class ServerAttributeModule {

    String path;
    List<ServerAttribute> serverAttributes;
    
    /**
     * <p>
     * Constructor.
     * </p>
     */
    public ServerAttributeModule () {
        super ();
    }

    /**
     * <p>
     * Sets the <code>path</code> field.
     * </p>
     * 
     * @param  path  The filesystem path to this server attribute module.
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
     * Sets the <code>serverAttributes</code> field.
     * </p>
     * 
     * @param  serverAttributes  The list of server attributes records.
     */
    public void setServerAttributes (List<ServerAttribute> serverAttributes) {
        this.serverAttributes = serverAttributes;
    }

    /**
     * <p>
     * Returns the value of the <code>serverAttributes</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<ServerAttribute> getServerAttributes () {
        return serverAttributes;
    }
    
    /**
     * <p>
     * Locates a specific server attribute record.
     * </p>
     * <p>
     * This SHOULD be returning a defensive copy of the server attribute, but for performance reasons it simply returns a reference.
     * </p>
     * 
     * @param  id  The id of the group to look for.
     * @return     The archive.
     */
    public ServerAttribute findById (String id) {
        if (this.serverAttributes == null)
            return null;

        for (ServerAttribute tmp : this.serverAttributes) {
            if (tmp.getId().equals (id))
                return tmp;
        }
        
        return null;
    }
}

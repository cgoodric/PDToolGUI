package com.cisco.dvbu.ps.deploytool.gui.core.module.resource_cache;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import java.util.List;

/**
 * <p>
 * Bean object for resource cache module records. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class ResourceCacheModule {

    String path;
    List<ResourceCache> resourceCaches;

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public ResourceCacheModule () {
        super ();
    }

    /**
     * <p>
     * Sets the <code>path</code> field.
     * </p>
     * 
     * @param  path  The filesystem path to this resource cache module.
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
     * Sets the <code>resourceCaches</code> field.
     * </p>
     * 
     * @param  resourceCaches  The list of resource cache definitions.
     */
    public void setResourceCaches (List<ResourceCache> resourceCaches) {
        this.resourceCaches = resourceCaches;
    }

    /**
     * <p>
     * Returns the value of the <code>resourceCaches</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<ResourceCache> getResourceCaches () {
        return resourceCaches;
    }
    
    /**
     * <p>
     * Locates a specific resource cache record.
     * </p>
     * <p>
     * This SHOULD be returning a defensive copy of the resource cache, but for performance reasons it simply returns a reference.
     * </p>
     * 
     * @param  id  The id of the resource to look for.
     * @return     The resource.
     */
    public ResourceCache findById (String id) {
        if (this.resourceCaches == null)
            return null;

        for (ResourceCache tmp : this.resourceCaches) {
            if (tmp.getId().equals (id))
                return tmp;
        }
        
        return null;
    }
}

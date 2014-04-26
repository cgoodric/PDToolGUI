package com.cisco.dvbu.ps.deploytool.gui.core.module.resource;

import java.util.List;

/**
 * <p>
 * Bean object for resource module records. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class ResourceModule {

    String path;
    List<Resource> resources;

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public ResourceModule () {
        super ();
    }

    /**
     * <p>
     * Sets the <code>path</code> field.
     * </p>
     * 
     * @param  path  The filesystem path to this resource module.
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
     * Sets the <code>resources</code> field.
     * </p>
     * 
     * @param  resources  The list of resource records.
     */
    public void setResources (List<Resource> resources) {
        this.resources = resources;
    }

    /**
     * <p>
     * Returns the value of the <code>resources</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<Resource> getResources () {
        return resources;
    }
    
    /**
     * <p>
     * Locates a specific resource record.
     * </p>
     * <p>
     * This SHOULD be returning a defensive copy of the resource, but for performance reasons it simply returns a reference.
     * </p>
     * 
     * @param  id  The id of the resource to look for.
     * @return     The resource.
     */
    public Resource findById (String id) {
        if (this.resources == null)
            return null;

        for (Resource tmp : this.resources) {
            if (tmp.getId().equals (id))
                return tmp;
        }
        
        return null;
    }
}

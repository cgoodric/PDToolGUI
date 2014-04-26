package com.cisco.dvbu.ps.deploytool.gui.core.module.group;

import java.util.List;

/**
 * <p>
 * Bean object for group module records. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class GroupModule {

    String path;
    List<Group> groups;
    
    /**
     * <p>
     * Constructor.
     * </p>
     */
    public GroupModule () {
        super ();
    }

    /**
     * <p>
     * Sets the <code>path</code> field.
     * </p>
     * 
     * @param  path  The filesystem path to this group module.
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
     * Sets the <code>groups</code> field.
     * </p>
     * 
     * @param  groups  The list of group records.
     */
    public void setGroups (List<Group> groups) {
        this.groups = groups;
    }

    /**
     * <p>
     * Returns the value of the <code>groups</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<Group> getGroups () {
        return groups;
    }
    
    /**
     * <p>
     * Locates a specific group record.
     * </p>
     * <p>
     * This SHOULD be returning a defensive copy of the group, but for performance reasons it simply returns a reference.
     * </p>
     * 
     * @param  id  The id of the group to look for.
     * @return     The group.
     */
    public Group findById (String id) {
        if (this.groups == null)
            return null;

        for (Group tmp : this.groups) {
            if (tmp.getId().equals (id))
                return tmp;
        }
        
        return null;
    }
}

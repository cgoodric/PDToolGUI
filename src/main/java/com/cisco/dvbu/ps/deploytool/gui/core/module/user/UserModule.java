package com.cisco.dvbu.ps.deploytool.gui.core.module.user;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import java.util.List;

/**
 * <p>
 * Bean object for user module records. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class UserModule {

    String path;
    List<User> users;
    
    /**
     * <p>
     * Constructor.
     * </p>
     */
    public UserModule () {
        super ();
    }

    /**
     * <p>
     * Sets the <code>path</code> field.
     * </p>
     * 
     * @param  path  The filesystem path to this user module.
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
     * Sets the <code>users</code> field.
     * </p>
     * 
     * @param  users  The list of user records.
     */
    public void setUsers (List<User> users) {
        this.users = users;
    }

    /**
     * <p>
     * Returns the value of the <code>groups</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<User> getUsers () {
        return users;
    }
    
    /**
     * <p>
     * Locates a specific user record.
     * </p>
     * <p>
     * This SHOULD be returning a defensive copy of the user, but for performance reasons it simply returns a reference.
     * </p>
     * 
     * @param  id  The id of the user to look for.
     * @return     The archive.
     */
    public User findById (String id) {
        if (this.users == null)
            return null;

        for (User tmp : this.users) {
            if (tmp.getId().equals (id))
                return tmp;
        }
        
        return null;
    }
}

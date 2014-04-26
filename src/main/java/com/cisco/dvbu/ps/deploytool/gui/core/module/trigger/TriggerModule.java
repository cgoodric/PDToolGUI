package com.cisco.dvbu.ps.deploytool.gui.core.module.trigger;

import java.util.List;

/**
 * <p>
 * Bean object for trigger module records. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class TriggerModule {

    String path;
    List<Trigger> triggers;
    
    /**
     * <p>
     * Constructor.
     * </p>
     */
    public TriggerModule () {
        super ();
    }

    /**
     * <p>
     * Sets the <code>path</code> field.
     * </p>
     * 
     * @param  path  The filesystem path to this trigger module.
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
     * Sets the <code>triggers</code> field.
     * </p>
     * 
     * @param  triggers  The list of trigger records.
     */
    public void setTriggers (List<Trigger> triggers) {
        this.triggers = triggers;
    }

    /**
     * <p>
     * Returns the value of the <code>triggers</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<Trigger> getTriggers () {
        return triggers;
    }
    
    /**
     * <p>
     * Locates a specific group record.
     * </p>
     * <p>
     * This SHOULD be returning a defensive copy of the trigger, but for performance reasons it simply returns a reference.
     * </p>
     * 
     * @param  id  The id of the group to look for.
     * @return     The archive.
     */
    public Trigger findById (String id) {
        if (this.triggers == null)
            return null;

        for (Trigger tmp : this.triggers) {
            if (tmp.getId().equals (id))
                return tmp;
        }
        
        return null;
    }
}

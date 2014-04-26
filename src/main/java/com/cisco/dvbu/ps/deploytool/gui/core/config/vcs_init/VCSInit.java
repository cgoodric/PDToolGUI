package com.cisco.dvbu.ps.deploytool.gui.core.config.vcs_init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Bean object for VCS initialization. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class VCSInit {
    private static final Logger log = LoggerFactory.getLogger (VCSInit.class);

    private String status;

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public VCSInit () {
        super ();
    }

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public VCSInit (String status) {
        super ();
        
        this.status = status;
    }

    /**
     * <p>
     * Sets the <code>status</code> field.
     * </p>
     * 
     * @param  status  The status of the VCS initialization.
     */
    public void setStatus (String status) {
        this.status = status;
    }

    /**
     * <p>
     * Returns the value of the <code>status</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getStatus () {
        return status;
    }
}

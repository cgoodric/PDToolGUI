package com.cisco.dvbu.ps.deploytool.gui.resources;

import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;
import com.cisco.dvbu.ps.deploytool.gui.util.SecurityManager;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.GET;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Servlet for working on lists of servers.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
@Path("/security")
public class SecurityResource {

    private static final Logger log = LoggerFactory.getLogger (SecurityResource.class);

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public SecurityResource () {}

    /**
     * <p>
     * Tests whether the client is allowed to access the PDTool GUI application server.
     * </p>
     * 
     * @param req      Servlet request object containing client request parameters.
     * @return         A {@link ResultMessage} object containing the results of the security test. Serialized by Jackson into JSON.
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public ResultMessage testAccess(
        @Context HttpServletRequest req
    ) {
        ResultMessage rm = null;
        
        try {
            SecurityManager.testAccess (req);
            
            rm = new ResultMessage ("success", "Access permitted.", null);
        } catch (SecurityException se) {
            rm = new ResultMessage ("error", "Access denied.", null);
        }
        
        return rm;
    }    
}

package com.cisco.dvbu.ps.deploytool.gui.resources;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.cisco.dvbu.ps.deploytool.gui.core.module.rebind.Rebind;
import com.cisco.dvbu.ps.deploytool.gui.core.module.rebind.RebindsDAO;

import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;
import com.cisco.dvbu.ps.deploytool.gui.util.SecurityManager;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Servlet for working on archive records within archive modules.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
@Path("/rebind")
public class RebindResource {

    private static final Logger log = LoggerFactory.getLogger (RebindResource.class);
    RebindsDAO dao = new RebindsDAO();

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public RebindResource () {
        super ();
    }

    /**
     * <p>
     * Searches for a single {@link Rebind} object given a URL encoded ID string.
     * </p>
     * 
     * @param  path  The URL encoded path to look for.
     * @param  id    The ID to look for.
     * @param  req   Servlet request object containing client request parameters.
     * @return       The requested {@link Rebind} as an object. Serialized by Jackson into JSON.
     */
    @GET @Path("{path}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Rebind findById (
        @PathParam ("path") String path,
        @QueryParam("id") String id,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        try {
            path = (path != null) ? URLDecoder.decode (path.replace('+', ' '), "UTF-8") : null;
            id = (id != null) ? URLDecoder.decode (id.replace('+', ' '), "UTF-8") : null;
        } catch (Exception ignored) { ; }
        
        return dao.findById (path, id);
    }

    /**
     * <p>
     * Adds or copies a {@link Rebind} object to the specified rebind module.
     * </p>
     * 
     * @param  path     The URL encoded path to look for.
     * @param  ids      The list of rebind IDs to copy.
     * @param  rebind   The {@link Rebind} object to add. Ignored if the "ids" parameter is not null.
     * @param  req      Servlet request object containing client request parameters.
     * @return          A {@link ResultMessage} object containing the results of the add request. Serialized by Jackson into JSON.
     */
    @POST @Path("{path}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public ResultMessage create (
        @PathParam ("path") String path,
        @QueryParam("ids") String ids,
        Rebind rebind,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        try {
            path = (path != null) ? URLDecoder.decode (path.replace('+', ' '), "UTF-8") : null;
            ids = (ids != null) ? URLDecoder.decode (ids.replace('+', ' '), "UTF-8") : null;
        } catch (Exception ignored) { ; }
        
        if (ids == null || ids.length() == 0) {
            return dao.add (path, rebind);
        } else {
            return dao.copy (path, ids);
        }
    }
  
    
    /**
     * <p>
     * Updates a {@link Rebind} object in the specified rebind module.
     * </p>
     * 
     * @param  path     The URL encoded path to look for.
     * @param  rebind   The {@link Rebind} object to edit.
     * @param  req      Servlet request object containing client request parameters.
     * @return          A {@link ResultMessage} object containing the results of the edit request. Serialized by Jackson into JSON.
     */
    @PUT @Path("{path}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public ResultMessage update (
        @PathParam ("path") String path,
        Rebind rebind,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        log.debug("archive update servlet called");
        try {
            path = (path != null) ? URLDecoder.decode (path.replace('+', ' '), "UTF-8") : null;
        } catch (Exception ignored) { ; }
        
        return dao.edit (path, rebind);
    }
    
    /**
     * <p>
     * Deletes a list of {@link Rebind} objects in the specified rebind module.
     * </p>
     * 
     * @param  path  The URL encoded path to look for.
     * @param  ids   A comma separated list of rebind IDs to delete.
     * @param  req   Servlet request object containing client request parameters.
     * @return       A {@link ResultMessage} object containing the results of the delete request. Serialized by Jackson into JSON.
     */
    @DELETE @Path("{path}")
    @Produces({ MediaType.APPLICATION_JSON })
    public ResultMessage remove (
        @PathParam ("path") String path,
        @QueryParam("ids") String ids,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        try {
            path = (path != null) ? URLDecoder.decode (path.replace('+', ' '), "UTF-8") : null;
            ids = (ids != null) ? URLDecoder.decode (ids.replace('+', ' '), "UTF-8") : null;
        } catch (Exception ignored) { ; }
        
        return dao.delete (path, ids);
    }
}

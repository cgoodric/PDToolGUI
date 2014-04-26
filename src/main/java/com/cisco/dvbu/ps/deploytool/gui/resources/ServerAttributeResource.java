package com.cisco.dvbu.ps.deploytool.gui.resources;

import com.cisco.dvbu.ps.deploytool.gui.core.module.server_attribute.ServerAttribute;
import com.cisco.dvbu.ps.deploytool.gui.core.module.server_attribute.ServerAttributesDAO;

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
 * Servlet for working on group records within group modules.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
@Path("/server_attribute")
public class ServerAttributeResource {

    private static final Logger log = LoggerFactory.getLogger (ServerAttributeResource.class);
    ServerAttributesDAO dao = new ServerAttributesDAO();

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public ServerAttributeResource () {
        super ();
    }

    /**
     * <p>
     * Searches for a single {@link ServerAttribute} object given a URL encoded ID string.
     * </p>
     * 
     * @param  path  The URL encoded path to look for.
     * @param  id    The ID to look for.
     * @param  req   Servlet request object containing client request parameters.
     * @return       The requested {@link ServerAttribute} as an object. Serialized by Jackson into JSON.
     */
    @GET @Path("{path}")
    @Produces({ MediaType.APPLICATION_JSON })
    public ServerAttribute findById (
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
     * Adds or copies a {@link ServerAttribute} object to the specified group module.
     * </p>
     * 
     * @param  path             The URL encoded path to look for.
     * @param  ids              The list of group IDs to copy.
     * @param  serverAttribute  The {@link ServerAttribute} object to add. Ignored if the "ids" parameter is not null.
     * @param  req              Servlet request object containing client request parameters.
     * @return                  A {@link ResultMessage} object containing the results of the add request. Serialized by Jackson into JSON.
     */
    @POST @Path("{path}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public ResultMessage create (
        @PathParam ("path") String path,
        @QueryParam("ids") String ids,
        ServerAttribute serverAttribute,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        try {
            path = (path != null) ? URLDecoder.decode (path.replace('+', ' '), "UTF-8") : null;
            ids = (ids != null) ? URLDecoder.decode (ids.replace('+', ' '), "UTF-8") : null;
        } catch (Exception ignored) { ; }
        
        if (ids == null || ids.length() == 0) {
            return dao.add (path, serverAttribute);
        } else {
            return dao.copy (path, ids);
        }
    }
  
    
    /**
     * <p>
     * Updates a {@link ServerAttribute} object in the specified group module.
     * </p>
     * 
     * @param  path             The URL encoded path to look for.
     * @param  serverAttribute  The {@link ServerAttribute} object to edit.
     * @param  req              Servlet request object containing client request parameters.
     * @return                  A {@link ResultMessage} object containing the results of the edit request. Serialized by Jackson into JSON.
     */
    @PUT @Path("{path}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public ResultMessage update (
        @PathParam ("path") String path,
        ServerAttribute serverAttribute,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        log.debug("group update servlet called");
        try {
            path = (path != null) ? URLDecoder.decode (path.replace('+', ' '), "UTF-8") : null;
        } catch (Exception ignored) { ; }
        
        return dao.edit (path, serverAttribute);
    }
    
    /**
     * <p>
     * Deletes a list of {@link ServerAttribute} objects in the specified group module.
     * </p>
     * 
     * @param  path  The URL encoded path to look for.
     * @param  ids   A comma separated list of server attribute IDs to delete.
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

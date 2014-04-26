package com.cisco.dvbu.ps.deploytool.gui.resources;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.cisco.dvbu.ps.deploytool.gui.core.config.server.Server;
import com.cisco.dvbu.ps.deploytool.gui.core.config.server.ServersDAO;
import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;
import com.cisco.dvbu.ps.deploytool.gui.util.SecurityManager;

import java.net.URLDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Servlet for working on individual servers.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
@Path("/server")
public class ServerResource {

    private static final Logger log = LoggerFactory.getLogger (ServerResource.class);
    private ServersDAO dao = new ServersDAO();
    
    /**
     * <p>
     * Constructor.
     * </p>
     */
    public ServerResource () {}

    /**
     * <p>
     * Searches for a single {@link Server} object given a URL encoded ID string.
     * </p>
     * 
     * @param  id  The URL encoded ID to look for.
     * @param  req Servlet request object containing client request parameters.
     * @return     The requested {@link Server} as an object. Serialized by Jackson into JSON.
     */
    @GET @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Server findById (
        @PathParam ("id") String id,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        try {
            id = (id != null) ? URLDecoder.decode (id.replace ('+', ' '), "UTF-8") : null;
        } catch (Exception ignored) { ; }
        
        return dao.findById (id);
    }

    /**
     * <p>
     * Adds a {@link Server} object to the servers list.
     * </p>
     * 
     * @param  server  The {@link Server} object to add.
     * @param  req     Servlet request object containing client request parameters.
     * @return         A {@link ResultMessage} object containing the results of the add request. Serialized by Jackson into JSON.
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public ResultMessage create (
        Server server,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        return dao.add (server);
    }
  
    
    /**
     * <p>
     * Updates a {@link Server} object in the servers list.
     * </p>
     * 
     * @param  server  The {@link Server} object to update.
     * @param  req     Servlet request object containing client request parameters.
     * @return         A {@link ResultMessage} object containing the results of the edit request. Serialized by Jackson into JSON.
     */
    @PUT
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public ResultMessage update (
        Server server,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        return dao.edit (server);
    }
    
    /**
     * <p>
     * Copies a list of {@link Server} objects in the servers list.
     * </p>
     * 
     * @param  ids A comma separated list of server IDs to copy.
     * @param  req Servlet request object containing client request parameters.
     * @return     A {@link ResultMessage} object containing the results of the copy request. Serialized by Jackson into JSON.
     */
    @POST @Path("{ids}")
    @Produces({ MediaType.APPLICATION_JSON })
    public ResultMessage copy (
        @PathParam("ids") String ids,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        try {
            ids = (ids != null) ? URLDecoder.decode (ids.replace ('+', ' '), "UTF-8") : null;
        } catch (Exception ignored) { ; }
        
        return dao.copy (ids);
    }

    /**
     * <p>
     * Deletes a list of {@link Server} objects in the servers list.
     * </p>
     * 
     * @param  ids A comma separated list of server IDs to delete.
     * @param  req Servlet request object containing client request parameters.
     * @return     A {@link ResultMessage} object containing the results of the delete request. Serialized by Jackson into JSON.
     */
    @DELETE @Path("{ids}")
    @Produces({ MediaType.APPLICATION_JSON })
    public ResultMessage remove (
        @PathParam("ids") String ids,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        try {
            ids = (ids != null) ? URLDecoder.decode (ids.replace ('+', ' '), "UTF-8") : null;
        } catch (Exception ignored) { ; }
        
        return dao.delete (ids);
    }
}

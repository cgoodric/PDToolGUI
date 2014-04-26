package com.cisco.dvbu.ps.deploytool.gui.resources;

import com.cisco.dvbu.ps.deploytool.gui.core.module.data_source.DataSource;
import com.cisco.dvbu.ps.deploytool.gui.core.module.data_source.DataSourcesDAO;

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
@Path("/data_source")
public class DataSourceResource {

    private static final Logger log = LoggerFactory.getLogger (DataSourceResource.class);
    DataSourcesDAO dao = new DataSourcesDAO();

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public DataSourceResource () {
        super ();
    }
    /**
     * <p>
     * Searches for a single {@link DataSource} object given a URL encoded ID string.
     * </p>
     * 
     * @param  path  The URL encoded path to look for.
     * @param  id    The ID to look for.
     * @param  req   Servlet request object containing client request parameters.
     * @return       The requested {@link DataSource} as an object. Serialized by Jackson into JSON.
     */
    @GET @Path("{path}")
    @Produces({ MediaType.APPLICATION_JSON })
    public DataSource findById (
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
     * Adds or copies a {@link DataSource} object to the specified archive module.
     * </p>
     * 
     * @param  path     The URL encoded path to look for.
     * @param  ids      The list of archive IDs to copy.
     * @param  ds       The {@link DataSource} object to add. Ignored if the "ids" parameter is not null.
     * @param  req      Servlet request object containing client request parameters.
     * @return          A {@link ResultMessage} object containing the results of the add request. Serialized by Jackson into JSON.
     */
    @POST @Path("{path}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public ResultMessage create (
        @PathParam ("path") String path,
        @QueryParam("ids") String ids,
        DataSource ds,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        try {
            path = (path != null) ? URLDecoder.decode (path.replace('+', ' '), "UTF-8") : null;
            ids = (ids != null) ? URLDecoder.decode (ids.replace('+', ' '), "UTF-8") : null;
        } catch (Exception ignored) { ; }
        
        if (ids == null || ids.length() == 0) {
            return dao.add (path, ds);
        } else {
            return dao.copy (path, ids);
        }
    }
  
    
    /**
     * <p>
     * Updates a {@link DataSource} object in the specified archive module.
     * </p>
     * 
     * @param  path     The URL encoded path to look for.
     * @param  ds       The {@link DataSource} object to edit.
     * @param  req      Servlet request object containing client request parameters.
     * @return          A {@link ResultMessage} object containing the results of the edit request. Serialized by Jackson into JSON.
     */
    @PUT @Path("{path}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public ResultMessage update (
        @PathParam ("path") String path,
        DataSource ds,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        log.debug("archive update servlet called");
        try {
            path = (path != null) ? URLDecoder.decode (path.replace('+', ' '), "UTF-8") : null;
        } catch (Exception ignored) { ; }
        
        return dao.edit (path, ds);
    }
    
    /**
     * <p>
     * Deletes a list of {@link DataSource} objects in the specified archive module.
     * </p>
     * 
     * @param  path  The URL encoded path to look for.
     * @param  ids   A comma separated list of archive IDs to delete.
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

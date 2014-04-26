package com.cisco.dvbu.ps.deploytool.gui.resources;

import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfile;
import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;

import com.cisco.dvbu.ps.deploytool.gui.util.SecurityManager;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Servlet for working on deployment profiles.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
@Path("/deployment_profile")
public class DeploymentProfileResource {

    private static final Logger log = LoggerFactory.getLogger (ServerResource.class);
    DeploymentProfilesDAO dao = new DeploymentProfilesDAO();

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public DeploymentProfileResource () {}

    /**
     * <p>
     * Searches for a single {@link DeploymentProfile} object given a URL encoded ID string.
     * </p>
     * 
     * @param  id   The URL encoded ID to look for.
     * @param  req  Servlet request object containing client request parameters.
     * @return      The requested {@link DeploymentProfile} as an object. Serialized by Jackson into JSON.
     */
    @GET @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    public DeploymentProfile findById (
        @PathParam ("id") String id,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        try {
            id = (id != null) ? URLDecoder.decode (id.replace('+', ' '), "UTF-8") : null;
        } catch (Exception ignored) { ; }

        return dao.getById (id);
    }

    /**
     * <p>
     * Updates a {@link DeploymentProfile} object in the servers list.
     * </p>
     * 
     * @param  dp   The {@link DeploymentProfile} object to update.
     * @param  req  Servlet request object containing client request parameters.
     * @return      A {@link ResultMessage} object containing the results of the edit request. Serialized by Jackson into JSON.
     */
    @PUT
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public ResultMessage update (
        DeploymentProfile dp,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        return dao.edit (dp);
    }
}

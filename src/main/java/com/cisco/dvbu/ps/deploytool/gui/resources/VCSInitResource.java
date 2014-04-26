package com.cisco.dvbu.ps.deploytool.gui.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.cisco.dvbu.ps.deploytool.gui.core.config.vcs_init.VCSInitDAO;

import com.cisco.dvbu.ps.deploytool.gui.core.runtime.execute.ExecutePDTool;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.execute.ExecuteResult;

import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;
import com.cisco.dvbu.ps.deploytool.gui.util.SecurityManager;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.core.Context;

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
@Path("/vcs_init")
public class VCSInitResource {

    private static final Logger log = LoggerFactory.getLogger (VCSInitResource.class);
    private VCSInitDAO dao = new VCSInitDAO();
    
    /**
     * <p>
     * Constructor.
     * </p>
     */
    public VCSInitResource() {}

    /**
     * <p>
     * Returns the VCS initialization status for the given deployment profile.
     * </p>
     * 
     * @param  profilePath  The deployment profile to use for determining initialization status.
     * @param  req          Servlet request object containing client request parameters.
     * @return              The requested initialization state as a {@link ResultMessage} object. Serialized by Jackson into JSON.
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public ResultMessage getStatus (
        @QueryParam ("profilePath")  String profilePath,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        try {
            profilePath = (profilePath != null) ? URLDecoder.decode (profilePath.replace ('+', ' '), "UTF-8") : null;
        } catch (Exception ignored) { ; }
        
        return dao.getStatus (profilePath);
    }

    /**
     * <p>
     * Initializes the PDTool VCS workspace using the given deployment profile.
     * </p>
     * 
     * @param profilePath  The deployment profile to use for initializing the VCS workspace.
     * @param req          Servlet request object containing client request parameters.
     * @return             A {@link ResultMessage} object containing the results of the initialize request. Serialized by Jackson into JSON.
     */
    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    public ExecuteResult initialize (
        @QueryParam ("profilePath")  String profilePath,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        ExecuteResult er = new ExecuteResult();
        
        try {
            profilePath = (profilePath != null) ? URLDecoder.decode (profilePath.replace ('+', ' '), "UTF-8") : null;
        } catch (Exception ignored) { ; }

        // launch the ExecutePDTool script
        //
        ExecutePDTool epdt = new ExecutePDTool (ExecutePDTool.TYPE_VCS_INIT, null, profilePath, null, null);
        
        // check the launch status. poll every 1/10th of a second until the status is no longer NOT_READY.
        //
        while (epdt.getLaunchResult() == ExecutePDTool.NOT_READY) {
            try {
                Thread.sleep (100);
            } catch (Exception ignored) { ; }
        }
        
        er.setResultCode (epdt.getLaunchResult());
        er.setLogFilePath (epdt.getLogFilePath());
        
        return er;
   }
}

package com.cisco.dvbu.ps.deploytool.gui.resources;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.cisco.dvbu.ps.deploytool.gui.core.runtime.execute.ExecuteResult;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.execute.ExecutePDTool;

import com.cisco.dvbu.ps.deploytool.gui.util.SecurityManager;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Servlet for executing PDTool deployment plans.
 * </p>
 * <p>
 * Ideally, a PDTool execution would be performed synchronously and the output streamed back to
 * the browser client. At this point, streaming communications between browser and server are not
 * well defined by the W3C so the idea here is to make an asynchronous call to execute a PDTool plan, then
 * pass back the launch results to the browser. The browser will then use the log servlet (see 
 * {@link LogResource}) to poll periodically to get log messages until the execution is complete 
 * (effectively "tail"ing the log file.) 
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
@Path("/execute")
public class ExecuteResource {

    private static final Logger log = LoggerFactory.getLogger (ExecuteResource.class);
    
    /**
     * <p>
     * Constructor.
     * </p>
     */
    public ExecuteResource () {}
    
    /**
     * <p>
     * Executes a deployment plan.
     * </p>
     * 
     * @param  planPath The URL encoded path to look for.
     * @param  confPath An integer indicating what file type is being used.
     * @param  req      Servlet request object containing client request parameters.
     * @return          An {@link ExecuteResult} object. Indicates success (0) or failure (1) to launch ExecutePDTool.
     */
    @POST @Path("{planPath}")
    @Produces({ MediaType.APPLICATION_JSON })
    public ExecuteResult executePDTool (
        @PathParam("planPath") String planPath,
        @QueryParam("confPath") String confPath,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        ExecuteResult er = new ExecuteResult();
        
        try {
            planPath = (planPath != null) ? URLDecoder.decode (planPath.replace('+', ' '), "UTF-8") : null;
            confPath = (confPath != null) ? URLDecoder.decode (confPath.replace('+', ' '), "UTF-8") : null;
        } catch (Exception ignored) { ; }

        // launch the ExecutePDTool script
        //
        ExecutePDTool epdt = new ExecutePDTool (ExecutePDTool.TYPE_EXECUTE, planPath, confPath, null, null);
        
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

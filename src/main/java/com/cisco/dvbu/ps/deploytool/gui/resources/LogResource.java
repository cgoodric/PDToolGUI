package com.cisco.dvbu.ps.deploytool.gui.resources;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.cisco.dvbu.ps.deploytool.gui.core.runtime.log.LogMessages;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.log.LogsDAO;

import com.cisco.dvbu.ps.deploytool.gui.util.SecurityManager;
import com.cisco.dvbu.ps.deploytool.gui.util.StringUtils;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.QueryParam;

import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Servlet for polling log files.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
@Path("/log")
public class LogResource {

    private static final Logger log = LoggerFactory.getLogger (LogResource.class);
    private LogsDAO dao = new LogsDAO();
    
    /**
     * <p>
     * Constructor.
     * </p>
     */
    public LogResource () {}

    /**
     * <p>
     * Returns the log messages from an ExecutePDTool execution beginning at a particular line. The returned object contains an array with
     * the requested lines. The caller then uses the array size to adjust the starting line of the next call. The returned object also
     * contains a boolean indicating that ExecutePDTool has completed its execution and polling for additional messages is no longer necessary.
     * </p>
     * 
     * @param  id         The URL encoded path to look for.
     * @param  startLine  The line number of the log file to start from. Method returns all the lines from this line to the end of the file.
     * @param  usePool    Indicates whether to use the in-memory log pool.
     * @param  req        Servlet request object containing client request parameters.
     * @return            The requested {@link LogMessages} as an object. Serialized by Jackson into JSON.
     */
    @GET @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    public LogMessages getLogMessages (
        @PathParam ("id") String id,
        @QueryParam ("startLine") int startLine,
        @QueryParam ("usePool") int usePool,
        @Context HttpServletRequest req
    ) {
        LogMessages result = null;
        SecurityManager.testAccess (req);

        try {
            id = (id != null) ? URLDecoder.decode (id.replace ('+', ' '), "UTF-8") : null;
        } catch (Exception ignored) { ; }
        
        log.debug ("id = " + id + ", startLine = " + startLine + ", usePool = " + usePool);

        try {
            result = dao.getLogMessages (id, startLine, usePool);
        } catch (Exception e) {
            log.error ("Exception thrown while gathering log messages: " + e.getMessage() + "\n" +
                    StringUtils.stackTraceToString (e));
        }

        return result;
    }
}

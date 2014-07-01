package com.cisco.dvbu.ps.deploytool.gui.resources;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;

import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.file.FileRecord;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.file.FilesDAO;

import com.cisco.dvbu.ps.deploytool.gui.util.SecurityManager;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.QueryParam;

import javax.ws.rs.core.Context;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * <p>
 * Servlet for working on files.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
@Path("/file")
public class FileResource {

//    private static final Logger log = LoggerFactory.getLogger (FileResource.class);
    private FilesDAO dao = new FilesDAO();
    
    /**
     * <p>
     * Constructor.
     * </p>
     */
    public FileResource () {}

    /**
     * <p>
     * Searches for a single {@link FileRecord} object given a URL encoded ID string.
     * </p>
     * 
     * @param  id       The URL encoded path to look for.
     * @param  fileType An integer indicating what file type is being used.
     * @param  req      Servlet request object containing client request parameters.
     * @return          The requested {@link FileRecord} as an object. Serialized by Jackson into JSON.
     */
    @GET @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    public FileRecord findById (
        @PathParam ("id") String id,
        @QueryParam ("fileType") int fileType,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        try {
            id = (id != null) ? URLDecoder.decode (id.replace ('+', ' '), "UTF-8") : null;
        } catch (Exception ignored) { ; }
        
        return dao.findById (id, fileType);
    }

    /**
     * <p>
     * Creates a new file from a {@link FileRecord} object.
     * </p>
     * 
     * @param  fileRecord  The {@link FileRecord} object to add.
     * @param  req         Servlet request object containing client request parameters.
     * @return             A {@link ResultMessage} object containing the results of the add request. Serialized by Jackson into JSON.
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public ResultMessage create (
        FileRecord fileRecord,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        return dao.add (fileRecord);
    }
  
    
    /**
     * <p>
     * Copies a list of files.
     * </p>
     * 
     * @param  ids      A comma separated list of file paths to copy.
     * @param  fileType An integer indicating what file type is being used.
     * @param  req      Servlet request object containing client request parameters.
     * @return          A {@link ResultMessage} object containing the results of the copy request. Serialized by Jackson into JSON.
     */
    @POST @Path("{ids}")
    @Produces({ MediaType.APPLICATION_JSON })
    public ResultMessage copy (
        @PathParam("ids") String ids,
        @QueryParam("fileType") int fileType,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        try {
            ids = (ids != null) ? URLDecoder.decode (ids.replace ('+', ' '), "UTF-8") : null;
        } catch (Exception ignored) { ; }
        
        return dao.copy (ids, fileType);
    }

    /**
     * <p>
     * Renames a file based on a {@link FileRecord} object.
     * </p>
     * 
     * @param  fileRecord  The {@link FileRecord} object to add.
     * @param  req         Servlet request object containing client request parameters.
     * @return             A {@link ResultMessage} object containing the results of the add request. Serialized by Jackson into JSON.
     */
    @PUT
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public ResultMessage edit (
        FileRecord fileRecord,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        return dao.edit (fileRecord);
    }
  
    /**
     * <p>
     * Deletes a list of files.
     * </p>
     * 
     * @param  ids      A comma separated list of file paths to delete.
     * @param  fileType An integer indicating what file type is being used.
     * @param  req      Servlet request object containing client request parameters.
     * @return          A {@link ResultMessage} object containing the results of the delete request. Serialized by Jackson into JSON.
     */
    @DELETE @Path("{ids}")
    @Produces({ MediaType.APPLICATION_JSON })
    public ResultMessage remove (
        @PathParam("ids") String ids,
        @QueryParam("fileType") int fileType,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        try {
            ids = (ids != null) ? URLDecoder.decode (ids.replace ('+', ' '), "UTF-8") : null;
        } catch (Exception ignored) { ; }
        
        return dao.delete (ids, fileType);
    }
}

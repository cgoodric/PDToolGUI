package com.cisco.dvbu.ps.deploytool.gui.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.cisco.dvbu.ps.deploytool.gui.util.ListResult;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.file.FilesDAO;

import com.cisco.dvbu.ps.deploytool.gui.util.SecurityManager;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.core.Context;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * <p>
 * Servlet for working on lists of servers.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
@Path("/file_list")
public class FileListResource {

//    private static final Logger log = LoggerFactory.getLogger (ServerListResource.class);
    private FilesDAO dao = new FilesDAO();

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public FileListResource () {}
    /**
     * <p>
     * Returns a {@link ListResult} object
     * containing the requested page of information.
     * </p>
     * 
     * @param isSearch     Indicates whether the request is a search request ("true" or "false".)
     * @param numRows      Number of rows to return.
     * @param pageNum      Page number to return (based on numRows.)
     * @param sortIndex    Data element to sort on (as represented by a column in the UI.)
     * @param sortOrder    Sort order ("asc" or "desc".)
     * @param filters      Multi-row filtering (not currently used.)
     * @param searchField  Search field to search on (isSearch = "true".)
     * @param searchString String to search for (isSearch = "true".)
     * @param searchOper   Search operation (equals or "eq", not equals or "ne", etc.)
     * @param param        Parameter name identifier to pass back when called to generate parameter value pick lists.
     * @param req          Servlet request object containing client request parameters.
     * @return             The requested list of items.
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public ListResult list(
        @QueryParam("fileType")     int fileType,
        @QueryParam("_search")      String isSearch,
        @QueryParam("rows")         int numRows,
        @QueryParam("page")         int pageNum,
        @QueryParam("sidx")         String sortIndex,
        @QueryParam("sord")         String sortOrder,
        @QueryParam("filters")      String filters,
        @QueryParam("searchField")  String searchField,
        @QueryParam("searchString") String searchString,
        @QueryParam("searchOper")   String searchOper,
        @QueryParam("param")        String param,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        return dao.list(
                       fileType,
                       isSearch,
                       numRows,
                       pageNum,
                       sortIndex,
                       sortOrder,
                       filters,
                       searchField,
                       searchString,
                       searchOper,
                       param
                   );
    }
}

package com.cisco.dvbu.ps.deploytool.gui.resources;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.cisco.dvbu.ps.deploytool.gui.core.module.data_source.DataSource;
import com.cisco.dvbu.ps.deploytool.gui.core.module.data_source.DataSourceModulesDAO;

import com.cisco.dvbu.ps.deploytool.gui.util.ListResult;
import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;
import com.cisco.dvbu.ps.deploytool.gui.util.SecurityManager;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * <p>
 * Servlet for working on archive modules.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
@Path("/data_source_module")
public class DataSourceModuleResource {

//    private static final Logger log = LoggerFactory.getLogger (DataSourceModuleResource.class);
    DataSourceModulesDAO dao = new DataSourceModulesDAO();

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public DataSourceModuleResource () {
        super ();
    }

    /**
     * <p>
     * Returns a {@link ListResult} object
     * containing the requested page of information.
     * </p>
     * 
     * @param id           Path to the archive module file.
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
    @GET @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    public ListResult list(
        @PathParam ("id")           String id,
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
        @Context                    HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        try {
            id = URLDecoder.decode (id.replace('+', ' '), "UTF-8");
        } catch (Exception ignored) { ; }
        
        return dao.list(
                       id,
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

    /**
     * <p>
     * Returns a {@link DataSource} object containing the list of default data source types or a specific data
     * source type's attributes for the current default server. These types and attributes are updated when the
     * user updates his/her default server on the preferences page.
     * </p>
     * 
     * @param dsTypeName   The data source type to look for. If null, then return the list of data source types.
     * @param req          Servlet request object containing client request parameters.
     * @return             The data source types or attributes in a list contained in a {@link DataSource} object.
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public DataSource getDefaultDataSources (
        @QueryParam("dsTypeName") String dsTypeName,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);
        
        try {
            dsTypeName = (dsTypeName != null) ? URLDecoder.decode (dsTypeName.replace ('+', ' '), "UTF-8") : null;
        } catch (Exception ignored) { ; }
        
        return dao.getDsTypesOrAttrs (dsTypeName);
    }
    
    /**
     * <p>
     * Uses the auto-generate facility to generate data sources from a server based on a domain.
     * </p>
     * 
     * @param id           Path to the data source module file.
     * @param serverId     The server from which to generate the data sources.
     * @param profilePath  The profile path to use for generating data sources.
     * @param startPath    The starting path in which to search for data sources.
     * @param req          Servlet request object containing client request parameters.
     * @return             A {@link ResultMessage} object containing the results of the generate request. Serialized by Jackson into JSON.
     */
    @POST @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    public ResultMessage generate(
        @PathParam ("id")           String id,
        @QueryParam("profilePath")  String profilePath,
        @QueryParam("serverId")     String serverId,
        @QueryParam("startPath")    String startPath,
        @Context                    HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        try {
            id = (id != null) ? URLDecoder.decode (id.replace ('+', ' '), "UTF-8") : null;
            profilePath = (profilePath != null) ? URLDecoder.decode (profilePath.replace ('+', ' '), "UTF-8") : null;
            serverId = (serverId != null) ? URLDecoder.decode (serverId.replace ('+', ' '), "UTF-8") : null;
        } catch (Exception ignored) { ; }
        
        return dao.generate (id, profilePath, serverId, startPath);
    }

}

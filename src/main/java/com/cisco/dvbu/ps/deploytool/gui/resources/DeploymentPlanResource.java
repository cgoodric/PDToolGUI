package com.cisco.dvbu.ps.deploytool.gui.resources;

import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_plan.DeploymentPlan;
import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_plan.DeploymentPlansDAO;
import com.cisco.dvbu.ps.deploytool.gui.util.ListResult;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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
@Path("/deployment_plan_list")
public class DeploymentPlanResource {

    private static final Logger log = LoggerFactory.getLogger (ServerResource.class);
    DeploymentPlansDAO dao = new DeploymentPlansDAO();

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public DeploymentPlanResource () {
        super ();
    }

    /**
     * <p>
     * Returns a {@link ListResult} object
     * containing the requested page of information.
     * </p>
     * 
     * @param id           Path to the deployment plan file.
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
            id = (id != null) ? URLDecoder.decode (id.replace('+', ' '), "UTF-8") : null;
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
     * Updates a {@link DeploymentPlan} object in the servers list.
     * </p>
     * 
     * @param  dp   The {@link DeploymentPlan} object to update.
     * @param  req  Servlet request object containing client request parameters.
     * @return      A {@link ResultMessage} object containing the results of the edit request. Serialized by Jackson into JSON.
     */
    @PUT
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public ResultMessage update (
        DeploymentPlan dp,
        @Context HttpServletRequest req
    ) {
        SecurityManager.testAccess (req);

        return dao.edit (dp);
    }
}

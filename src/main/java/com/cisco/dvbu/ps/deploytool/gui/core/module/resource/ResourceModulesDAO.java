package com.cisco.dvbu.ps.deploytool.gui.core.module.resource;

import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfile;
import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.Preferences;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.PreferencesManager;
import com.cisco.dvbu.ps.deploytool.gui.resources.ResourceModuleResource;

import com.cisco.dvbu.ps.deploytool.gui.util.DAOConstants;
import com.cisco.dvbu.ps.deploytool.gui.util.ListColumnInfo;
import com.cisco.dvbu.ps.deploytool.gui.util.ListResult;
import com.cisco.dvbu.ps.deploytool.gui.util.ListResultRowComparator;
import com.cisco.dvbu.ps.deploytool.gui.util.ListUtils;
import com.cisco.dvbu.ps.deploytool.gui.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

import org.apache.commons.io.FileUtils;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Data access object for resource modules. Used by {@link ResourceModuleResource} servlet.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class ResourceModulesDAO {

    private Map<String, ListColumnInfo> listColumnsInfo = new HashMap<String, ListColumnInfo>();

    private static final Logger log = LoggerFactory.getLogger (ResourceModulesDAO.class);

    private XMLOutputter xmlo = new XMLOutputter();

    public ResourceModulesDAO () {
        super ();

        // set up the column list for server lists
        //
        listColumnsInfo.put ("id",              new ListColumnInfo (0, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("resourcePath",    new ListColumnInfo (1, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("resourceType",    new ListColumnInfo (2, ListResultRowComparator.SORT_TYPE_STRING));
    }

    /*
     * Servlet methods
     */

    /**
     * <p>
     * Sorts and filters the list of {@link Resource} objects and returns a {@link ListResult} object
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
     * @return             The requested list of items.
     * @see                ResourceModuleResource
     */
    public ListResult list (
        String path,
        String isSearch,
        int    numRows,
        int    pageNum,
        String sortIndex,
        String sortOrder,
        String filters,
        String searchField,
        String searchString,
        String searchOper,
        String param
    ) {
        List<ListResult.Row> resultList;
        
        // if the "param" input is set, then there's the potential that the input path
        // has variable references in it. we'll need to evaluate these references before
        // proceeding.
        //
        if (param != null) {
            
            // get the default deployment profile from the preferences manager.
            //
            Preferences p = PreferencesManager.getInstance();
            if (p.getDefaultProfile() != null) {
            
                // locate the deployment profile
                //
                DeploymentProfilesDAO dpDao = new DeploymentProfilesDAO();
                DeploymentProfile dp = dpDao.getById (p.getDefaultProfile());

                // if the deployment profile exists, evaluate any variable references with it.
                //
                if (dp != null) {
                    path = dp.evaluateVariables (path);
                } else {
                    log.error ("Default profile, \"" + p.getDefaultProfile() + "\", does not exist!");
                }
            } else {
                log.info ("Default profile not set.");
            }
        }
        
        log.debug ("Getting list of rebinds in " + path);
        ResourceModule rm = findByPath (path);

        if (rm == null) {
            log.error ("parsed resource module is null.");
            return null;
        }
        
        // get current list of rebind modules in display list form
        //
        resultList = getResourceList (rm);
        
        // apply search rules, if any
        //
        if (isSearch != null && isSearch.equalsIgnoreCase ("true")) {
            resultList = ListUtils.applySearchRules (
                             resultList, 
                             listColumnsInfo, 
                             searchField, 
                             searchString, 
                             searchOper
                         );
        }
        
        // apply sorting to the list
        //
        if (sortIndex != null && sortOrder != null) {
            Collections.sort (resultList, new ListResultRowComparator (
                                                  listColumnsInfo.get(sortIndex).getIndex(), 
                                                  sortOrder, 
                                                  listColumnsInfo.get(sortIndex).getSortType()
                                              )
            );
        }
        
        log.debug ("numRows = " + numRows + "; pageNum = " + pageNum);
        
        return ListUtils.getListResult (
                   resultList, 
                   numRows, 
                   pageNum,
                   param
               );
    }
    
    /**
     * <p>
     * Searches for a rebind module based on a URL encoded path and populates a {@link ResourceModule} object.
     * </p>
     * 
     * @param  path  The URL encoded ID to look for.
     * @return       The requested resource module as a {@link ResourceModule} object.
     * @see          ResourceModuleResource
     */
    public ResourceModule findByPath (String path) {
        ResourceModule rm = new ResourceModule();
        File f;
        SAXBuilder builder = new SAXBuilder();
        Document rmDoc;
        
        log.debug ("Parsing XML of " + path);

        if (path == null) {
            log.error ("Error: \"path\" input may not be NULL.");
            return null;
        }
        
        f = new File (path);

        if (! f.exists()) {
            log.error ("Error retrieving file \"" + path + "\": file does not exist.");
            return null;
        }
        
        if (! f.canRead()) {
            log.error ("Error retrieving file \"" + path + "\": file is not readable.");
            return null;
        }
        
        log.debug ("Sanity checks passed.");
        
        rm.setPath (path);
        
        try {
            rmDoc = builder.build (path);
            Element rootNode = rmDoc.getRootElement();
            Namespace ns = rmDoc.getNamespacesInScope().get(0); // should only be one namespace
            List<Resource> resources = new ArrayList<Resource>();
            List<Element> rmList = rootNode.getChildren();
            
            log.debug ("Located " + rmList.size() + " child elements.");

            // iterate over all the "resource" elements
            //
            log.debug ("Iterating over child elements.");
            for (Element rmChild : rmList) {
                resources.add (new Resource (rmChild));
            }
            
            rm.setResources (resources);

        } catch (IOException io) {
            log.error ("getById() unable to load " + path + ":" + io.getMessage());
        } catch (JDOMException jdomex) {
            log.error ("getById() unable to load " + path + ":" + jdomex.getMessage());
        }

        return rm;
    }
    
    /**
     * <p>
     * Writes the resource module to disk as an XML file.
     * </p>
     * 
     * @param  rm  The {@link ResourceModule} to write out.
     */
    public void serialize (ResourceModule rm) throws Exception {
        if (rm.getPath() == null)
            throw new IllegalArgumentException ("Resource module's path may not be null.");
        
        File f = new File (rm.getPath());

        // if user preferences indicate to save backups of edited files, back up the original file.
        //
        if (PreferencesManager.getInstance().getBackupFiles().equals ("true")) {
            FileUtils.copyFile (f, new File (rm.getPath() + ".bak"));
        }
        
        Document rmDoc = new Document (new Element ("ResourceModule", "ns1", DAOConstants.MODULES_NS));
        rmDoc.getRootElement().addContent ("\n");
        
        String indent = StringUtils.getIndent (1);
        
        if (rm.getResources() != null) {
            for (Resource r : rm.getResources()) {
                Element rNode = r.toElement ("resource", 2).addContent ("\n" + indent);

                rmDoc.getRootElement().addContent (indent);
                rmDoc.getRootElement().addContent (rNode);
                rmDoc.getRootElement().addContent ("\n");
            }
        }
        
        xmlo.output (rmDoc, new FileOutputStream (rm.getPath()));
    }
    
    // assembles the list result row list
    //
    private List<ListResult.Row> getResourceList (ResourceModule rm) {
        List<ListResult.Row> rl = new ArrayList<ListResult.Row>();
        
        if (rm.getResources() != null && rm.getResources().size() > 0) {
            for (Resource r : rm.getResources()) {

                List<String> cell = new ArrayList<String>();
                
                cell.add (r.getId());
                cell.add (r.getResourcePath());
                cell.add (r.getResourceType());
                
                rl.add (new ListResult.Row (r.getId(), cell));
            }
        }
                
        return rl;
    }
}

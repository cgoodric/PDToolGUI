package com.cisco.dvbu.ps.deploytool.gui.core.module.resource_cache;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfile;
import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.Preferences;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.PreferencesManager;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.log.LogsDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.config.server.Server;
import com.cisco.dvbu.ps.deploytool.gui.core.config.server.ServersDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.file.FilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.resources.ResourceCacheModuleResource;

import com.cisco.dvbu.ps.deploytool.gui.core.runtime.execute.ExecutePDTool;
import com.cisco.dvbu.ps.deploytool.gui.util.DAOConstants;
import com.cisco.dvbu.ps.deploytool.gui.util.ListColumnInfo;
import com.cisco.dvbu.ps.deploytool.gui.util.ListResult;
import com.cisco.dvbu.ps.deploytool.gui.util.ListResultRowComparator;
import com.cisco.dvbu.ps.deploytool.gui.util.ListUtils;
import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;
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
//import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Data access object for group modules. Used by {@link ResourceCacheModuleResource} servlet.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class ResourceCacheModulesDAO {

    private Map<String, ListColumnInfo> listColumnsInfo = new HashMap<String, ListColumnInfo>();

    private static final Logger log = LoggerFactory.getLogger (ResourceCacheModulesDAO.class);

    private XMLOutputter xmlo = new XMLOutputter();

    public ResourceCacheModulesDAO () {
        super ();
        
        // set up the column list for group lists
        //
        listColumnsInfo.put ("id",           new ListColumnInfo (0, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("configured",   new ListColumnInfo (1, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("enabled",      new ListColumnInfo (2, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("resourcePath", new ListColumnInfo (3, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("resourceType", new ListColumnInfo (4, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("storageMode",  new ListColumnInfo (5, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("refreshMode",  new ListColumnInfo (6, ListResultRowComparator.SORT_TYPE_STRING));
    }

    /*
     * Servlet methods
     */

    /**
     * <p>
     * Sorts and filters the list of {@link ResourceCache} objects and returns a {@link ListResult} object
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
     * @see                ResourceCacheModuleResource
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
        
        log.debug ("Getting list of groups in " + path);
        ResourceCacheModule rm = findByPath (path);

        if (rm == null) {
            log.error ("parsed resource cache module is null.");
            return null;
        }
        
        // get current list of resource cache modules in display list form
        //
        resultList = getResourceCacheList (rm);
        
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
        
        return ListUtils.getListResult (
                   resultList, 
                   numRows, 
                   pageNum,
                   param
               );
    }
    
    /**
     * <p>
     * Searches for a resource cache module based on a URL encoded path and populates a {@link ResourceCacheModule} object.
     * </p>
     * 
     * @param  path  The URL encoded ID to look for.
     * @return       The requested group module as an {@link ResourceCacheModule} object.
     * @see          ResourceCacheModuleResource
     */
    public ResourceCacheModule findByPath (String path) {
        ResourceCacheModule rm = new ResourceCacheModule();
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
//            Namespace ns = rmDoc.getNamespacesInScope().get(0); // should only be one namespace
            List<ResourceCache> resourceCaches = new ArrayList<ResourceCache>();
            List<Element> rmList = rootNode.getChildren();
            
            log.debug ("Located " + rmList.size() + " child elements.");

            // iterate over all the "group" elements
            // 
            int i = 0;
            log.debug ("Iterating over child elements.");
            for (Element rmChild : rmList) {
                
                log.debug ("Examining child #" + i++ + ": " + rmChild.getName() + "{" + rmChild.getNamespace().getURI() + "}");
                
                resourceCaches.add (new ResourceCache (rmChild));
            }
            
            rm.setResourceCaches (resourceCaches);

        } catch (IOException io) {
            log.error ("findByPath() unable to load " + path + ":" + io.getMessage());
        } catch (JDOMException jdomex) {
            log.error ("findByPath() unable to load " + path + ":" + jdomex.getMessage());
        }

        return rm;
    }
    
    /**
     * <p>
     * Writes the resource cache module to disk as an XML file.
     * </p>
     * 
     * @param  rm  The resource cache module to write out.
     */
    public void serialize (ResourceCacheModule rm) throws Exception {
        String indent = StringUtils.getIndent (1);
        if (rm.getPath() == null)
            throw new IllegalArgumentException ("Resource Cache module's path may not be null.");
        
        File f = new File (rm.getPath());

        // if user preferences indicate to save backups of edited files, back up the original file.
        //
        if (PreferencesManager.getInstance().getBackupFiles().equals ("true")) {
            FileUtils.copyFile (f, new File (rm.getPath() + ".bak"));
        }
        
        Document rmDoc = new Document (new Element ("ResourceCacheModule", "ns1", DAOConstants.MODULES_NS));
        rmDoc.getRootElement().addContent ("\n");

        if (rm.getResourceCaches() != null) {
            for (ResourceCache r : rm.getResourceCaches()) {
                Element rNode = r.toElement ("resourceCache", 2).addContent ("\n" + indent);

                rmDoc.getRootElement().addContent (indent);
                rmDoc.getRootElement().addContent (rNode);
                rmDoc.getRootElement().addContent ("\n");
            }
        }

        xmlo.output (rmDoc, new FileOutputStream (rm.getPath()));
    }
    
    public ResultMessage generate (
        String path,
        String profilePath,
        String serverId,
        String startPath,
        String options
    ) {
        ResultMessage result;
        List<ResultMessage.MessageItem> validationItems = new ArrayList<ResultMessage.MessageItem>();
        ResourceCacheModule rm;
        Server s;

        log.debug ("generate received path = \"" + path + "\", profilePath = \"" + profilePath + "\", serverId = \"" + serverId + "\", startPath = \"" + startPath + "\", options = \"" + options + "\".");
        
        // validate inputs
        //
        log.debug ("validating inputs ...");

        if (path == null || path.length() == 0) {
            validationItems.add (new ResultMessage.MessageItem ("id", "Resource Cache module path may not be null!"));
        } else {
            rm = findByPath (path);
            if (rm == null) {
                validationItems.add (new ResultMessage.MessageItem ("id", "Resource Cache module, " + path + ", does not exist!"));
            }
        }

        if (profilePath == null || profilePath.length() == 0) {
            validationItems.add (new ResultMessage.MessageItem ("profile_list", "Configuration profile path may not be null!"));
        }

        if (serverId == null || serverId.length() == 0) {
            validationItems.add (new ResultMessage.MessageItem ("server_list", "Server identifier may not be null!"));
        } else {
            s = (new ServersDAO ()).findById (serverId);
            if (s == null) {
                validationItems.add (new ResultMessage.MessageItem ("server_list", "Server, " + serverId + ", is not defined!"));
            }
        }

        if (startPath == null || startPath.length() == 0) {
            validationItems.add (new ResultMessage.MessageItem ("startPath", "Resource Cache startPath may not be null!"));
        }

        log.debug ("done.");

        log.debug ("validationItems.size() = " + validationItems.size());
        if (validationItems.size() > 0) {
            return new ResultMessage ("error", null, validationItems);
        }

        // write out temporary generate plan
        //
        String generatedXmlFileName = "tmpResourceCaches_" + System.currentTimeMillis() + ".xml";
        String planPath = writeGenerationPlan (serverId, startPath, options, generatedXmlFileName);
        if (planPath == null) {
            return new ResultMessage ("error", "Unable to write out temporary resource cache generation plan!", null);
        }
        log.debug ("wrote out temporary plan " + planPath);
        
        // execute pdtool against the temp plan
        //
        log.debug ("launching pdtool ...");
        ExecutePDTool epdt = new ExecutePDTool (planPath, profilePath);
        
        // check the launch status. poll every 1/10th of a second until the status is no longer NOT_READY.
        //
        log.debug ("waiting for pdtool to finish launching ...");
        while (epdt.getLaunchResult() == ExecutePDTool.NOT_READY) {
            try {
                Thread.sleep (100);
            } catch (Exception ignored) { ; }
        }
        log.debug ("done.");
        
        if (epdt.getLaunchResult() == ExecutePDTool.FAILURE) {
            return new ResultMessage ("error", "Unable to launch PDTool command line to generate groups!", null);
        }
        
        // wait for execute to complete. poll every 1/10th of a second until the log message response indicates execution is completed.
        //
        log.debug ("waiting for pdtool to finish executing.");
        String logFilePath = epdt.getLogFilePath();
        LogsDAO logsDAO = new LogsDAO ();
        while (! logsDAO.getLogMessages (logFilePath, 0, 1).isExecutionCompleted()) {
            try {
                Thread.sleep (100);
            } catch (Exception ignored) { ; }
        }
        log.debug ("done.");
        
        // delete the temporary generate plan
        //
        (new File (planPath)).delete();
        
        // make sure the resource caches xml got created.
        //
        String generatedXmlPath = FilesDAO.getPdtHome() + "/resources/modules/" + generatedXmlFileName;
        if (! (new File (generatedXmlPath).exists())) {
            return new ResultMessage ("error", "Resource Caches did not get generated! Please see latest PDTool log for more information.", null);
        }
        
        // merge result module with input module
        //
        log.debug ("merging generated resource caches with original resource caches ...");
        ResourceCacheModule mergedRm = mergeResourceCacheModules (path, generatedXmlPath);
        
        // delete the generated resoruce cache module
        //
        log.debug ("deleting generated resource caches file ...");
        (new File (generatedXmlPath)).delete();

        // write out the merged resource cache module
        //
        log.debug ("writing out merged resource caches ...");
        try {
            serialize (mergedRm);
            result = new ResultMessage ("success", "Resource Caches from server " + serverId + ", startPath " + startPath + ", options " + options + " successfully added.", null);
        } catch (Exception e) {
            result = new ResultMessage ("error", "Unable serialize merged original and generated resource caches!", null);
        }
        
        log.debug ("done.");
        return result;
    }
    
    // assembles the list result row list
    //
    private List<ListResult.Row> getResourceCacheList (ResourceCacheModule rm) {
        List<ListResult.Row> rl = new ArrayList<ListResult.Row>();
        
        if (rm.getResourceCaches() != null && rm.getResourceCaches().size() > 0) {
            for (ResourceCache r : rm.getResourceCaches()) {
                List<String> cell = new ArrayList<String>();
                
                cell.add (r.getId());
                cell.add ("" + r.isConfigured());
                cell.add ("" + r.isEnabled());
                cell.add (r.getResourcePath());
                cell.add (r.getResourceType());
                cell.add (ResourceCache.STORAGE_MODE_LABELS[r.getStorageMode()]);
                cell.add (ResourceCache.REFRESH_MODE_LABELS[r.getRefreshMode()]);
                
                rl.add (new ListResult.Row (r.getId(), cell));
            }
        }
        
        return rl;
    }
    
    // writes out a temporary resource cache generation plan
    //
    private String writeGenerationPlan (
        String serverId,
        String startPath,
        String options,
        String generatedXmlPath
    ) {
        return FilesDAO.writeTempGenerationPlan(
            "PASS	TRUE	ExecuteAction	generateResourceCacheXML	\"" + serverId + "\"	\"" + startPath + "\"	\"$MODULE_HOME/" + generatedXmlPath + "\"	\"$MODULE_HOME/servers.xml\"	\"" + options + "\""
        );
    }
    
    private ResourceCacheModule mergeResourceCacheModules (
        String origPath,
        String toMergePath
    ) {
        ResourceCacheModule origRm = findByPath (origPath);
        ResourceCacheModule toMergeRm = findByPath (toMergePath);
        int i;
        
        for (ResourceCache r : toMergeRm.getResourceCaches()) {
            
            for (ResourceCache or : origRm.getResourceCaches()) {
                i = 1;
                
                while (r.getId().equals (or.getId())) {
                    r.setId (or.getId() + "-" + i++);
                }
            }
        }
        
        origRm.getResourceCaches().addAll(toMergeRm.getResourceCaches());
        
        return origRm;        
    }
}

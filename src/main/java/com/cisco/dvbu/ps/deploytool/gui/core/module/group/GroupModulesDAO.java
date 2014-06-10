package com.cisco.dvbu.ps.deploytool.gui.core.module.group;

import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfile;
import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.Preferences;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.PreferencesManager;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.log.LogsDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.config.server.Server;
import com.cisco.dvbu.ps.deploytool.gui.core.config.server.ServersDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.file.FilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.resources.GroupModuleResource;

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
 * Data access object for group modules. Used by {@link GroupModuleResource} servlet.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class GroupModulesDAO {

    private Map<String, ListColumnInfo> listColumnsInfo = new HashMap<String, ListColumnInfo>();

    private static final Logger log = LoggerFactory.getLogger (GroupModulesDAO.class);

    private XMLOutputter xmlo = new XMLOutputter();

    public GroupModulesDAO () {
        super ();
        
        // set up the column list for group lists
        //
        listColumnsInfo.put ("id",          new ListColumnInfo (0, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("groupName",   new ListColumnInfo (1, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("groupDomain", new ListColumnInfo (2, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("privileges",  new ListColumnInfo (3, ListResultRowComparator.SORT_TYPE_STRING));
    }

    /*
     * Servlet methods
     */

    /**
     * <p>
     * Sorts and filters the list of {@link Group} objects and returns a {@link ListResult} object
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
     * @see                GroupModuleResource
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
        GroupModule gm = findByPath (path);

        if (gm == null) {
            log.error ("parsed group module is null.");
            return null;
        }
        
        // get current list of group modules in display list form
        //
        resultList = getGroupList (gm);
        
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
     * Searches for a group module based on a URL encoded path and populates a {@link GroupModule} object.
     * </p>
     * 
     * @param  path  The URL encoded ID to look for.
     * @return       The requested group module as an {@link GroupModule} object.
     * @see          GroupModuleResource
     */
    public GroupModule findByPath (String path) {
        GroupModule gm = new GroupModule();
        File f;
        SAXBuilder builder = new SAXBuilder();
        Document gmDoc;
        
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
        
        gm.setPath (path);
        
        try {
            gmDoc = builder.build (path);
            Element rootNode = gmDoc.getRootElement();
//            Namespace ns = gmDoc.getNamespacesInScope().get(0); // should only be one namespace
            List<Group> groups = new ArrayList<Group>();
            List<Element> gmList = rootNode.getChildren();
            
            log.debug ("Located " + gmList.size() + " child elements.");

            // iterate over all the "group" elements
            // 
            int i = 0;
            log.debug ("Iterating over child elements.");
            for (Element gmChild : gmList) {
                
                log.debug ("Examining child #" + i++ + ": " + gmChild.getName() + "{" + gmChild.getNamespace().getURI() + "}");
                
                groups.add (new Group (gmChild));
            }
            
            gm.setGroups (groups);

        } catch (IOException io) {
            log.error ("findByPath() unable to load " + path + ":" + io.getMessage());
        } catch (JDOMException jdomex) {
            log.error ("findByPath() unable to load " + path + ":" + jdomex.getMessage());
        }

        return gm;
    }
    
    /**
     * <p>
     * Writes the group module to disk as an XML file.
     * </p>
     * 
     * @param  gm  The group module to write out.
     */
    public void serialize (GroupModule gm) throws Exception {
        String indent = StringUtils.getIndent (1);
        if (gm.getPath() == null)
            throw new IllegalArgumentException ("Group module's path may not be null.");
        
        File f = new File (gm.getPath());

        // if user preferences indicate to save backups of edited files, back up the original file.
        //
        if (PreferencesManager.getInstance().getBackupFiles().equals ("true")) {
            FileUtils.copyFile (f, new File (gm.getPath() + ".bak"));
        }
        
        Document gmDoc = new Document (new Element ("GroupModule", "ns1", DAOConstants.MODULES_NS));
        gmDoc.getRootElement().addContent ("\n");

        if (gm.getGroups() != null) {
            for (Group g : gm.getGroups()) {
                Element gNode = g.toElement ("group", 2).addContent ("\n" + indent);

                gmDoc.getRootElement().addContent (indent);
                gmDoc.getRootElement().addContent (gNode);
                gmDoc.getRootElement().addContent ("\n");
            }
        }

        xmlo.output (gmDoc, new FileOutputStream (gm.getPath()));
    }
    
    public ResultMessage generate (
        String path,
        String profilePath,
        String serverId,
        String domain
    ) {
        ResultMessage result;
        List<ResultMessage.MessageItem> validationItems = new ArrayList<ResultMessage.MessageItem>();
        GroupModule gm;
        Server s;

        log.debug ("generate received path = \"" + path + "\", profilePath = \"" + profilePath + "\", serverId = \"" + serverId + "\", domain = \"" + domain + "\".");
        
        // validate inputs
        //
        log.debug ("validating inputs ...");

        if (path == null || path.length() == 0) {
            validationItems.add (new ResultMessage.MessageItem ("id", "Group module path may not be null!"));
        } else {
            gm = findByPath (path);
            if (gm == null) {
                validationItems.add (new ResultMessage.MessageItem ("id", "Group module, " + path + ", does not exist!"));
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

        if (domain == null || domain.length() == 0) {
            validationItems.add (new ResultMessage.MessageItem ("domain", "Group domain may not be null!"));
        }

        log.debug ("done.");

        log.debug ("validationItems.size() = " + validationItems.size());
        if (validationItems.size() > 0) {
            return new ResultMessage ("error", null, validationItems);
        }

        // write out temporary generate plan
        //
        String generatedXmlFileName = "tmpGroups_" + System.currentTimeMillis() + ".xml";
        String planPath = writeGenerationPlan (serverId, domain, generatedXmlFileName);
        if (planPath == null) {
            return new ResultMessage ("error", "Unable to write out temporary group generation plan!", null);
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
        
        // make sure the groups xml got created.
        //
        String generatedXmlPath = FilesDAO.getPdtHome() + "/resources/modules/" + generatedXmlFileName;
        if (! (new File (generatedXmlPath).exists())) {
            return new ResultMessage ("error", "Groups did not get generated! Please see latest PDTool log for more information.", null);
        }
        
        // merge result module with input module
        //
        log.debug ("merging generated groups with original groups ...");
        GroupModule mergedGm = mergeGroupModules (path, generatedXmlPath);
        
        // delete the generated group module
        //
        log.debug ("deleting generated groups file ...");
        (new File (generatedXmlPath)).delete();

        // write out the merged group module
        //
        log.debug ("writing out merged groups ...");
        try {
            serialize (mergedGm);
            result = new ResultMessage ("success", "Groups from server " + serverId + ", domain " + domain + " successfully added.", null);
        } catch (Exception e) {
            result = new ResultMessage ("error", "Unable serialize merged original and generated groups!", null);
        }
        
        log.debug ("done.");
        return result;
    }
    
    // assembles the list result row list
    //
    private List<ListResult.Row> getGroupList (GroupModule gm) {
        List<ListResult.Row> gl = new ArrayList<ListResult.Row>();
        
        if (gm.getGroups() != null && gm.getGroups().size() > 0) {
            for (Group g : gm.getGroups()) {
                List<String> cell = new ArrayList<String>();
                
                cell.add (g.getId());
                cell.add (g.getGroupName());
                cell.add (g.getGroupDomain());
                cell.add (g.getPrivilege());
                
                gl.add (new ListResult.Row (g.getId(), cell));
            }
        }
        
        return gl;
    }
    
    // writes out a temporary group generation plan
    //
    private String writeGenerationPlan (
        String serverId,
        String domain,
        String generatedXmlPath
    ) {
        return FilesDAO.writeTempGenerationPlan(
            "PASS	TRUE	ExecuteAction	generateGroupsXML	\"" + serverId + "\"	\"" + domain + "\"	\"$MODULE_HOME/" + generatedXmlPath + "\"	\"$MODULE_HOME/servers.xml\""
        );
    }
    
    private GroupModule mergeGroupModules (
        String origPath,
        String toMergePath
    ) {
        GroupModule origGm = findByPath (origPath);
        GroupModule toMergeGm = findByPath (toMergePath);
        int i;
        
        for (Group g : toMergeGm.getGroups()) {
            
            for (Group og : origGm.getGroups()) {
                i = 1;
                
                while (g.getId().equals (og.getId())) {
                    g.setId (og.getId() + "-" + i++);
                }
            }
        }
        
        origGm.getGroups().addAll(toMergeGm.getGroups());
        
        return origGm;        
    }
}

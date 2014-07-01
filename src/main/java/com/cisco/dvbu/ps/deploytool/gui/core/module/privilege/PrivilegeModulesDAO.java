package com.cisco.dvbu.ps.deploytool.gui.core.module.privilege;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.cisco.dvbu.ps.deploytool.gui.PDToolGUIConfiguration;
import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfile;
import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.Preferences;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.PreferencesManager;
import com.cisco.dvbu.ps.deploytool.gui.core.config.server.Server;
import com.cisco.dvbu.ps.deploytool.gui.core.config.server.ServersDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.file.FilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.log.LogsDAO;
import com.cisco.dvbu.ps.deploytool.gui.resources.PrivilegeModuleResource;

import com.cisco.dvbu.ps.deploytool.gui.core.runtime.execute.ExecutePDTool;
import com.cisco.dvbu.ps.deploytool.gui.util.DAOConstants;
import com.cisco.dvbu.ps.deploytool.gui.util.ListColumnInfo;
import com.cisco.dvbu.ps.deploytool.gui.util.ListResult;
import com.cisco.dvbu.ps.deploytool.gui.util.ListResultRowComparator;
import com.cisco.dvbu.ps.deploytool.gui.util.ListUtils;
import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;

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
 * Data access object for privilege modules. Used by {@link PrivilegeModuleResource} servlet.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class PrivilegeModulesDAO {

    private Map<String, ListColumnInfo> listColumnsInfo = new HashMap<String, ListColumnInfo>();

    private static final Logger log = LoggerFactory.getLogger (PrivilegeModulesDAO.class);

    private PDToolGUIConfiguration conf = new PDToolGUIConfiguration();
    private String indent; //, indentX2, indentX3, indentX4;
    private XMLOutputter xmlo = new XMLOutputter();

    public PrivilegeModulesDAO () {
        super ();
        
        // set up the column list for archive lists
        //
        listColumnsInfo.put ("id",              new ListColumnInfo (0, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("resourcePath",    new ListColumnInfo (1, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("resourceType",    new ListColumnInfo (2, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("recurse",         new ListColumnInfo (3, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("mode",            new ListColumnInfo (4, ListResultRowComparator.SORT_TYPE_STRING));

        indent = "";
        for (int i = 0; i < conf.getXmlIndentWidth(); i++)
            indent += " ";

//        indentX2 = indent + indent;
//        indentX3 = indent + indent + indent;
//        indentX4 = indent + indent + indent + indent;
    }

    /*
     * Servlet methods
     */

    /**
     * <p>
     * Sorts and filters the list of {@link Privilege} objects and returns a {@link ListResult} object
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
     * @see                PrivilegeModuleResource
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
        
        log.debug ("Getting list of privileges in " + path);
        PrivilegeModule pm = findByPath (path);

        if (pm == null) {
            log.error ("parsed privilege module is null.");
            return null;
        }
        
        // get current list of privilege modules in display list form
        //
        resultList = getPrivilegeList (pm);
        
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
        
        log.debug ("Returning " + resultList.size() + " items in result list.");
        
        return ListUtils.getListResult (
                   resultList, 
                   numRows, 
                   pageNum,
                   param
               );
    }
    
    /**
     * <p>
     * Searches for an privilege module based on a URL encoded path and populates an {@link PrivilegeModule} object.
     * </p>
     * 
     * @param  path  The URL encoded ID to look for.
     * @return       The requested privilege module as an {@link PrivilegeModule} object.
     * @see          PrivilegeModuleResource
     */
    public PrivilegeModule findByPath (String path) {
        PrivilegeModule pm = new PrivilegeModule();
        File f;
        SAXBuilder builder = new SAXBuilder();
        Document pmDoc;
        
        log.debug ("Parsing XML of " + path);

        if (path == null) {
            log.error ("Error: \"path\" input may not be NULL.");
            return null;
        }
        
        path = path.replace ('+', ' '); // javascript encodes spaces as "+" characters. Java's URL decoder doesn't, so we have to correct javascript's behavior.

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
        
        pm.setPath (path);
        
        try {
            pmDoc = builder.build (path);
            Element rootNode = pmDoc.getRootElement();
//            Namespace ns = pmDoc.getNamespacesInScope().get(0); // should only be one namespace
            List<Privilege> privileges = new ArrayList<Privilege>();
            List<Element> pmList = rootNode.getChildren();
            
            log.debug ("Located " + pmList.size() + " child elements.");

            // iterate over all the "privilege" elements
            //
            int i = 0;
            log.debug ("Iterating over child elements.");
            for (Element pmChild : pmList) {
                log.debug ("Examining child #" + i++ + ": " + pmChild.getName() + "{" + pmChild.getNamespace().getURI() + "}");
                
                privileges.add (new Privilege (pmChild));
            }
            
            pm.setPrivileges (privileges);
            
        } catch (IOException io) {
            log.error ("findByPath() unable to load " + path + ":" + io.getMessage());
        } catch (JDOMException jdomex) {
            log.error ("findByPath() unable to load " + path + ":" + jdomex.getMessage());
        }

        return pm;
    }
    
    /**
     * <p>
     * Writes the privilege module to disk as an XML file.
     * </p>
     * 
     * @param  pm  The privilege module to write out.
     */
    public void serialize (PrivilegeModule pm) throws Exception {
        if (pm.getPath() == null)
            throw new IllegalArgumentException ("Privilege module's path may not be null.");
        
        File f = new File (pm.getPath());

        // if user preferences indicate to save backups of edited files, back up the original file.
        //
        if (PreferencesManager.getInstance().getBackupFiles().equals ("true")) {
            FileUtils.copyFile (f, new File (pm.getPath() + ".bak"));
        }
        
        Document pmDoc = new Document (new Element ("PrivilegeModule", "ns1", DAOConstants.MODULES_NS));
        pmDoc.getRootElement().addContent ("\n");
        
        if (pm.getPrivileges() != null) {
            for (Privilege p : pm.getPrivileges()) {
                Element pNode = p.toElement ("resourcePrivilege", 2).addContent ("\n" + indent);

                pmDoc.getRootElement().addContent (indent);
                pmDoc.getRootElement().addContent (pNode);
                pmDoc.getRootElement().addContent ("\n");
            }
        }
        
        xmlo.output (pmDoc, new FileOutputStream (pm.getPath()));
    }
    
    public ResultMessage generate (
        String path,
        String profilePath,
        String serverId,
        String startPath,
        String filter,
        String options,
        String domainList
    ) {
        ResultMessage result;
        List<ResultMessage.MessageItem> validationItems = new ArrayList<ResultMessage.MessageItem>();
        PrivilegeModule pm;
        Server s;

        log.debug ("generate received path = \"" + path + "\", profilePath = \"" + profilePath + "\", serverId = \"" + serverId + "\", startPath = \"" + startPath + "\", filter = \"" + filter + "\", options = \"" + options + "\", domainList = \"" + domainList + "\".");
        
        // validate inputs
        //
        log.debug ("validating inputs ...");

        if (path == null || path.length() == 0) {
            validationItems.add (new ResultMessage.MessageItem ("id", "Privilege module path may not be null!"));
        } else {
            pm = findByPath (path);
            if (pm == null) {
                validationItems.add (new ResultMessage.MessageItem ("id", "Privilege module, " + path + ", does not exist!"));
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
            validationItems.add (new ResultMessage.MessageItem ("startPath", "Start Path may not be null!"));
        }

        log.debug ("done.");

        log.debug ("validationItems.size() = " + validationItems.size());
        if (validationItems.size() > 0) {
            return new ResultMessage ("error", null, validationItems);
        }

        // write out temporary generate plan
        //
        String generatedXmlFileName = "tmpPrivileges_" + System.currentTimeMillis() + ".xml";
        String planPath = writeGenerationPlan (serverId, startPath, filter, options, domainList, generatedXmlFileName);
        if (planPath == null) {
            return new ResultMessage ("error", "Unable to write out temporary user generation plan!", null);
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
            return new ResultMessage ("error", "Privileges did not get generated! Please see latest PDTool log for more information.", null);
        }
        
        // merge result module with input module
        //
        log.debug ("merging generated privileges with original privileges ...");
        PrivilegeModule mergedPm = mergePrivilegeModules (path, generatedXmlPath);
        
        // delete the generated privilege module
        //
        log.debug ("deleting generated users file ...");
        (new File (generatedXmlPath)).delete();

        // write out the merged users module
        //
        log.debug ("writing out merged users ...");
        try {
            serialize (mergedPm);
            result = new ResultMessage ("success", "Privileges from server " + serverId + ", starting path " + startPath + " successfully added.", null);
        } catch (Exception e) {
            result = new ResultMessage ("error", "Unable serialize merged original and generated privileges!", null);
        }
        
        log.debug ("done.");
        return result;
    }
    
    // assembles the list result row list
    //
    private List<ListResult.Row> getPrivilegeList (PrivilegeModule pm) {
        List<ListResult.Row> al = new ArrayList<ListResult.Row>();
        
        if (pm.getPrivileges() != null && pm.getPrivileges().size() > 0) {
            for (Privilege p : pm.getPrivileges()) {
                List<String> cell = new ArrayList<String>();
                
                cell.add (p.getId());
                cell.add (p.getResourcePath());
                cell.add (p.getResourceType());
                cell.add ("" + p.isRecurse());
                cell.add (p.getMode());
                
                al.add (new ListResult.Row (p.getId(), cell));
            }
        }
        
        return al;
    }
    
    // writes out a temporary group generation plan
    //
    private String writeGenerationPlan (
        String serverId,
        String startingPath,
        String filter,
        String options,
        String domainList,
        String generatedXmlPath
    ) {
        filter = (filter == null) ? "" : filter;
        options = (options == null) ? "" : options;
        domainList = (domainList == null) ? "" : domainList;
        
        return FilesDAO.writeTempGenerationPlan(
            "PASS	TRUE	ExecuteAction	generatePrivilegesXML	\"" + serverId + "\"	\"" + startingPath + "\"	\"$MODULE_HOME/" + generatedXmlPath + "\"	\"$MODULE_HOME/servers.xml\"	\"" + filter + "\"	\"" + options + "\"	\"" + domainList + "\""
        );
    }
    
    private PrivilegeModule mergePrivilegeModules (
        String origPath,
        String toMergePath
    ) {
        PrivilegeModule origPm = findByPath (origPath);
        PrivilegeModule toMergePm = findByPath (toMergePath);
        int i;
        
        for (Privilege p : toMergePm.getPrivileges()) {
            
            for (Privilege op : origPm.getPrivileges()) {
                i = 1;
                
                while (p.getId().equals (op.getId())) {
                    p.setId (op.getId() + "-" + i++);
                }
            }
        }
        
        origPm.getPrivileges().addAll(toMergePm.getPrivileges());
        
        return origPm;
    }
}

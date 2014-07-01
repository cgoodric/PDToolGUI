package com.cisco.dvbu.ps.deploytool.gui.core.module.user;

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
import com.cisco.dvbu.ps.deploytool.gui.resources.UserModuleResource;

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
 * Data access object for user modules. Used by {@link UserModuleResource} servlet.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class UserModulesDAO {

    private Map<String, ListColumnInfo> listColumnsInfo = new HashMap<String, ListColumnInfo>();

    private static final Logger log = LoggerFactory.getLogger (UserModulesDAO.class);

    private XMLOutputter xmlo = new XMLOutputter();

    public UserModulesDAO () {
        super ();
        
        // set up the column list for user lists
        //
        listColumnsInfo.put ("id",         new ListColumnInfo (0, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("userName",   new ListColumnInfo (1, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("domainName", new ListColumnInfo (2, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("privileges", new ListColumnInfo (3, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("annotation", new ListColumnInfo (4, ListResultRowComparator.SORT_TYPE_STRING));
    }

    /*
     * Servlet methods
     */

    /**
     * <p>
     * Sorts and filters the list of {@link User} objects and returns a {@link ListResult} object
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
     * @see                UserModuleResource
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
        
        log.debug ("Getting list of users in " + path);
        UserModule um = findByPath (path);

        if (um == null) {
            log.error ("parsed user module is null.");
            return null;
        }
        
        // get current list of user modules in display list form
        //
        resultList = getUserList (um);
        
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
     * Searches for a user module based on a URL encoded path and populates a {@link UserModule} object.
     * </p>
     * 
     * @param  path  The URL encoded ID to look for.
     * @return       The requested group module as an {@link UserModule} object.
     * @see          UserModuleResource
     */
    public UserModule findByPath (String path) {
        UserModule um = new UserModule();
        File f;
        SAXBuilder builder = new SAXBuilder();
        Document umDoc;
        
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
        
        um.setPath (path);
        
        try {
            umDoc = builder.build (path);
            Element rootNode = umDoc.getRootElement();
//            Namespace ns = umDoc.getNamespacesInScope().get(0); // should only be one namespace
            List<User> users = new ArrayList<User>();
            List<Element> umList = rootNode.getChildren();
            
            log.debug ("Located " + umList.size() + " child elements.");

            // iterate over all the "user" elements
            // 
            int i = 0;
            log.debug ("Iterating over child elements.");
            for (Element umChild : umList) {
                
                log.debug ("Examining child #" + i++ + ": " + umChild.getName() + "{" + umChild.getNamespace().getURI() + "}");
                
                users.add (new User (umChild));
            }
            
            um.setUsers (users);

        } catch (IOException io) {
            log.error ("findByPath() unable to load " + path + ":" + io.getMessage());
        } catch (JDOMException jdomex) {
            log.error ("findByPath() unable to load " + path + ":" + jdomex.getMessage());
        }

        return um;
    }
    
    /**
     * <p>
     * Writes the user module to disk as an XML file.
     * </p>
     * 
     * @param  um  The user module to write out.
     */
    public void serialize (UserModule um) throws Exception {
        if (um.getPath() == null)
            throw new IllegalArgumentException ("User module's path may not be null.");
        
        String indent = StringUtils.getIndent (1);
        
        File f = new File (um.getPath());

        // if user preferences indicate to save backups of edited files, back up the original file.
        //
        if (PreferencesManager.getInstance().getBackupFiles().equals ("true")) {
            FileUtils.copyFile (f, new File (um.getPath() + ".bak"));
        }
        
        Document umDoc = new Document (new Element ("UserModule", "ns1", DAOConstants.MODULES_NS));
        umDoc.getRootElement().addContent ("\n");

        if (um.getUsers() != null) {
            for (User u : um.getUsers()) {
                Element uNode = u.toElement ("user", 2).addContent ("\n" + indent);

                umDoc.getRootElement().addContent (indent);
                umDoc.getRootElement().addContent (uNode);
                umDoc.getRootElement().addContent ("\n");
            }
        }

        xmlo.output (umDoc, new FileOutputStream (um.getPath()));
    }
    
    public ResultMessage generate (
        String path,
        String profilePath,
        String serverId,
        String domain
    ) {
        ResultMessage result;
        List<ResultMessage.MessageItem> validationItems = new ArrayList<ResultMessage.MessageItem>();
        UserModule um;
        Server s;

        log.debug ("generate received path = \"" + path + "\", profilePath = \"" + profilePath + "\", serverId = \"" + serverId + "\", domain = \"" + domain + "\".");
        
        // validate inputs
        //
        log.debug ("validating inputs ...");

        if (path == null || path.length() == 0) {
            validationItems.add (new ResultMessage.MessageItem ("id", "User module path may not be null!"));
        } else {
            um = findByPath (path);
            if (um == null) {
                validationItems.add (new ResultMessage.MessageItem ("id", "User module, " + path + ", does not exist!"));
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
            validationItems.add (new ResultMessage.MessageItem ("domain", "User domain may not be null!"));
        }

        log.debug ("done.");

        log.debug ("validationItems.size() = " + validationItems.size());
        if (validationItems.size() > 0) {
            return new ResultMessage ("error", null, validationItems);
        }

        // write out temporary generate plan
        //
        String generatedXmlFileName = "tmpUsers_" + System.currentTimeMillis() + ".xml";
        String planPath = writeGenerationPlan (serverId, domain, generatedXmlFileName);
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
            return new ResultMessage ("error", "Users did not get generated! Please see latest PDTool log for more information.", null);
        }
        
        // merge result module with input module
        //
        log.debug ("merging generated users with original users ...");
        UserModule mergedUm = mergeUserModules (path, generatedXmlPath);
        
        // delete the generated user module
        //
        log.debug ("deleting generated users file ...");
        (new File (generatedXmlPath)).delete();

        // write out the merged users module
        //
        log.debug ("writing out merged users ...");
        try {
            serialize (mergedUm);
            result = new ResultMessage ("success", "Users from server " + serverId + ", domain " + domain + " successfully added.", null);
        } catch (Exception e) {
            result = new ResultMessage ("error", "Unable serialize merged original and generated users!", null);
        }
        
        log.debug ("done.");
        return result;
    }
    
    // assembles the list result row list
    //
    private List<ListResult.Row> getUserList (UserModule um) {
        List<ListResult.Row> ul = new ArrayList<ListResult.Row>();
        
        if (um.getUsers() != null && um.getUsers().size() > 0) {
            for (User u : um.getUsers()) {
                List<String> cell = new ArrayList<String>();
                
                cell.add (u.getId());
                cell.add (u.getUserName());
                cell.add (u.getDomainName());
                cell.add (u.getPrivilege());
                cell.add (u.getAnnotation());
                
                ul.add (new ListResult.Row (u.getId(), cell));
            }
        }
        
        return ul;
    }
    
    // writes out a temporary user generation plan
    //
    private String writeGenerationPlan (
        String serverId,
        String domain,
        String generatedXmlPath
    ) {
        return FilesDAO.writeTempGenerationPlan(
            "PASS	TRUE	ExecuteAction	generateUsersXML	\"" + serverId + "\"	\"" + domain + "\"	\"$MODULE_HOME/" + generatedXmlPath + "\"	\"$MODULE_HOME/servers.xml\""
        );
    }
    
    private UserModule mergeUserModules (
        String origPath,
        String toMergePath
    ) {
        UserModule origUm = findByPath (origPath);
        UserModule toMergeUm = findByPath (toMergePath);
        int i;
        
        for (User u : toMergeUm.getUsers()) {
            
            for (User ou : origUm.getUsers()) {
                i = 1;
                
                while (u.getId().equals (ou.getId())) {
                    u.setId (ou.getId() + "-" + i++);
                }
            }
        }
        
        origUm.getUsers().addAll(toMergeUm.getUsers());
        
        return origUm;        
    }
}

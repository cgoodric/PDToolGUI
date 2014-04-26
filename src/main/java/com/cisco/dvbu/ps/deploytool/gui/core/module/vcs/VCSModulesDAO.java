package com.cisco.dvbu.ps.deploytool.gui.core.module.vcs;

import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfile;
import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.Preferences;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.PreferencesManager;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.log.LogsDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.config.server.Server;
import com.cisco.dvbu.ps.deploytool.gui.core.config.server.ServersDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.file.FilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.resources.VCSModuleResource;

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
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Data access object for group modules. Used by {@link VCSModuleResource} servlet.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class VCSModulesDAO {

    private Map<String, ListColumnInfo> listColumnsInfo = new HashMap<String, ListColumnInfo>();

    private static final Logger log = LoggerFactory.getLogger (VCSModulesDAO.class);

    private XMLOutputter xmlo = new XMLOutputter();

    public VCSModulesDAO () {
        super ();
        
        // set up the column list for VCS lists
        //
        listColumnsInfo.put ("id",          new ListColumnInfo (0, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("type",        new ListColumnInfo (1, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("conResType",  new ListColumnInfo (2, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("urlPath",     new ListColumnInfo (3, ListResultRowComparator.SORT_TYPE_STRING));
    }

    /*
     * Servlet methods
     */

    /**
     * <p>
     * Sorts and filters the list of {@link VCS} objects and returns a {@link ListResult} object
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
     * @see                VCSModuleResource
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
        
        log.debug ("Getting list of VCS records in " + path);
        VCSModule vm = findByPath (path);

        if (vm == null) {
            log.error ("parsed VCS module is null.");
            return null;
        }
        
        // get current list of group modules in display list form
        //
        resultList = getVCSList (vm);
        
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
     * Searches for a VCS module based on a URL encoded path and populates a {@link VCSModule} object.
     * </p>
     * 
     * @param  path  The URL encoded ID to look for.
     * @return       The requested VCS module as an {@link VCSModule} object.
     * @see          VCSModuleResource
     */
    public VCSModule findByPath (String path) {
        VCSModule vm = new VCSModule();
        File f;
        SAXBuilder builder = new SAXBuilder();
        Document vmDoc;
        
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
        
        vm.setPath (path);
        
        try {
            vmDoc = builder.build (path);
            Element rootNode = vmDoc.getRootElement();
            Namespace ns = vmDoc.getNamespacesInScope().get(0); // should only be one namespace
            List<VCS> vcsList = new ArrayList<VCS>();
            List<Element> vmList = rootNode.getChildren();
            
            log.debug ("Located " + vmList.size() + " child elements.");

            // iterate over all the "VCS" elements
            // 
            int i = 0;
            log.debug ("Iterating over child elements.");
            for (Element vmChild : vmList) {
                
                log.debug ("Examining child #" + i++ + ": " + vmChild.getName() + "{" + vmChild.getNamespace().getURI() + "}");
                
                if (vmChild.getName().equals ("vcsConnections")) {
                    for (Element vcNode : vmChild.getChildren()) {
                        vcsList.add (new VCS (vcNode));
                    }
                }
                
                if (vmChild.getName().equals ("vcsResource"))
                    vcsList.add (new VCS (vmChild));
            }
            
            vm.setVcsList (vcsList);

        } catch (IOException io) {
            log.error ("findByPath() unable to load " + path + ":" + io.getMessage());
        } catch (JDOMException jdomex) {
            log.error ("findByPath() unable to load " + path + ":" + jdomex.getMessage());
        }

        return vm;
    }
    
    /**
     * <p>
     * Writes the VCS module to disk as an XML file.
     * </p>
     * 
     * @param  vm  The VCS module to write out.
     */
    public void serialize (VCSModule vm) throws Exception {
        String indentStr = StringUtils.getIndent (1);
        String indentStr2 = StringUtils.getIndent (2);
        if (vm.getPath() == null)
            throw new IllegalArgumentException ("VCS module's path may not be null.");
        
        File f = new File (vm.getPath());

        // if user preferences indicate to save backups of edited files, back up the original file.
        //
        if (PreferencesManager.getInstance().getBackupFiles().equals ("true")) {
            FileUtils.copyFile (f, new File (vm.getPath() + ".bak"));
        }
        
        Document vmDoc = new Document (new Element ("VCSModule", "ns1", DAOConstants.MODULES_NS));
        vmDoc.getRootElement().addContent ("\n");

        if (vm.getVcsList() != null) {
            List<VCS> connections = new ArrayList<VCS>();
            List<VCS> resources = new ArrayList<VCS>();
            
            for (VCS v : vm.getVcsList()) {
                if (v.type == VCS.TYPE_CONNECTION)
                    connections.add (v);
                else
                    resources.add (v);
            }
            
            if (connections.size() > 0) {
                Element vcNode = new Element ("vcsConnections");
                
                for (VCS vc : connections) {
                    vcNode.addContent ("\n" + indentStr2);
                    vcNode.addContent (vc.toElement ("vcsConnection", 3).addContent ("\n" + indentStr2));
                }
                
                vcNode.addContent ("\n" + indentStr);
                
                vmDoc.getRootElement().addContent (indentStr);
                vmDoc.getRootElement().addContent (vcNode);
                vmDoc.getRootElement().addContent ("\n");
            }

            for (VCS vr : resources) {
                vmDoc.getRootElement().addContent (indentStr);
                vmDoc.getRootElement().addContent (vr.toElement ("vcsResource", 2).addContent ("\n" + indentStr));
                vmDoc.getRootElement().addContent ("\n");
            }
        }

        xmlo.output (vmDoc, new FileOutputStream (vm.getPath()));
    }
    
    public ResultMessage generate (
        String path,
        String profilePath,
        String serverId,
        String vcsConnectionId,
        String startPath
    ) {
        ResultMessage result;
        List<ResultMessage.MessageItem> validationItems = new ArrayList<ResultMessage.MessageItem>();
        VCSModule vm = null, mergedVm = null;
        VCS vconn;
        Server s;

        log.debug ("generate received path = \"" + path + "\", profilePath = \"" + profilePath + "\", serverId = \"" + serverId + "\", vcsConnectionId = \"" + vcsConnectionId + "\", startPath = \"" + startPath + "\".");
        
        // validate inputs
        //
        log.debug ("validating inputs ...");

        if (path == null || path.length() == 0) {
            validationItems.add (new ResultMessage.MessageItem ("id", "VCS module path may not be null!"));
        } else {
            vm = findByPath (path);
            if (vm == null) {
                validationItems.add (new ResultMessage.MessageItem ("id", "VCS module, " + path + ", does not exist!"));
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

        if (vcsConnectionId == null || vcsConnectionId.length() == 0) {
            validationItems.add (new ResultMessage.MessageItem ("vcsConnectionId", "VCS Connection Id may not be null!"));
        }
        vconn = vm.findById (vcsConnectionId);

        if (vconn == null) {
            validationItems.add (new ResultMessage.MessageItem ("vcsConnectionId", "VCS Connection Id, " + vcsConnectionId + ", not found!"));
        }

        if (startPath == null || startPath.length() == 0) {
            validationItems.add (new ResultMessage.MessageItem ("startPath", "Starting Path may not be null!"));
        }

        log.debug ("done.");

        log.debug ("validationItems.size() = " + validationItems.size());
        if (validationItems.size() > 0) {
            return new ResultMessage ("error", null, validationItems);
        }

        // write out temporary generate plan
        //
        String generatedXmlFileName = "tmpVCS_" + System.currentTimeMillis() + ".xml";

        // create the generate module XML file from the VCS module template
        //
        String templateXmlPath = FilesDAO.getPdtHome() + "/" + FilesDAO.fileTypeProperties.get (FilesDAO.FILE_TYPE_MODULE_VCS).getTemplate();
        String generatedXmlPath = FilesDAO.getPdtHome() + "/resources/modules/" + generatedXmlFileName;
        try {
            FileUtils.copyFile (
                new File (templateXmlPath), 
                new File (generatedXmlPath)
            );
        } catch (Exception e) {
            return new ResultMessage ("error", "Unable to copy temporary VCS module: " + e.getMessage(), null);
        }
        
        // populate the generate module XML with the VCS connection entry specified by the user.
        // the connection entry will then be replaced with the generated VCS records.
        //
        (new VCSDAO()).add (generatedXmlPath, vconn);
        
        // create a temporary generation deployment plan
        //
        String planPath = writeGenerationPlan (serverId, vcsConnectionId, startPath, generatedXmlFileName);
        if (planPath == null) {
            return new ResultMessage ("error", "Unable to write out temporary VCS generation plan!", null);
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
            return new ResultMessage ("error", "Unable to launch PDTool command line to generate VCS records!", null);
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
        
        // make sure the vcs resource entries got created (the connection entry identified by vcsConnectionId
        // should have been replaced.)
        //
        if (findByPath (generatedXmlPath).findById (vcsConnectionId) != null) {
            // delete the generated VCS module
            //
            log.debug ("deleting generated VCS file ...");
            (new File (generatedXmlPath)).delete();
    
            return new ResultMessage ("error", "VCS entities did not get generated! Please see latest PDTool log for more information.", null);
        } else {
            // merge result module with input module
            //
            log.debug ("merging generated VCS records with original VCS module entries ...");
            mergedVm = mergeVCSModules (path, generatedXmlPath);
            
            // delete the generated VCS module
            //
            log.debug ("deleting generated VCS file ...");
            (new File (generatedXmlPath)).delete();
        }

        // write out the merged VCS module
        //
        log.debug ("writing out merged VCS ...");
        try {
            serialize (mergedVm);
            result = new ResultMessage ("success", "VCS from server " + serverId + ", VCS Connection Id " + vcsConnectionId + ", Starting Path " + startPath + " successfully added.", null);
        } catch (Exception e) {
            result = new ResultMessage ("error", "Unable serialize merged original and generated VCS lists!", null);
        }
        
        log.debug ("done.");
        return result;
    }
    
    // assembles the list result row list
    //
    private List<ListResult.Row> getVCSList (VCSModule vm) {
        List<ListResult.Row> vl = new ArrayList<ListResult.Row>();
        
        if (vm.getVcsList() != null && vm.getVcsList().size() > 0) {
            for (VCS v : vm.getVcsList()) {
                List<String> cell = new ArrayList<String>();
                
                cell.add (v.getId());
                cell.add ((v.getType() == VCS.TYPE_CONNECTION) ? "Connection" : "Resource");
                cell.add ((v.getType() == VCS.TYPE_CONNECTION) ? VCS.VCS_TYPE_LABELS[v.getVcsType()] : v.getResourceType());
                cell.add ((v.getType() == VCS.TYPE_CONNECTION) ? v.getVcsRepositoryUrl() : v.getResourcePath());
                
                vl.add (new ListResult.Row (v.getId(), cell));
            }
        }
        
        return vl;
    }
    
    // writes out a temporary group generation plan
    //
    private String writeGenerationPlan (
        String serverId,
        String vcsConnectionId,
        String startPath,
        String generatedXmlPath
    ) {
        return FilesDAO.writeTempGenerationPlan(
            "PASS	TRUE	ExecuteAction	generateVCSXML2	\"" + serverId + "\"	\"" + vcsConnectionId + "\"	\"" + startPath + "\"	\"$MODULE_HOME/" + generatedXmlPath + "\"	\"$MODULE_HOME/servers.xml\""
        );
    }
    
    private VCSModule mergeVCSModules (
        String origPath,
        String toMergePath
    ) {
        VCSModule origVm = findByPath (origPath);
        VCSModule toMergeVm = findByPath (toMergePath);
        int i;
        
        for (VCS v : toMergeVm.getVcsList()) {
            
            for (VCS ov : origVm.getVcsList()) {
                i = 1;
                
                while (v.getId().equals (ov.getId())) {
                    v.setId (ov.getId() + "-" + i++);
                }
            }
        }
        
        origVm.getVcsList().addAll(toMergeVm.getVcsList());
        
        return origVm;        
    }
}

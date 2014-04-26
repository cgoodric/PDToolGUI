package com.cisco.dvbu.ps.deploytool.gui.core.module.server_attribute;

import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfile;
import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.Preferences;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.PreferencesManager;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.log.LogsDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.config.server.Server;
import com.cisco.dvbu.ps.deploytool.gui.core.config.server.ServersDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.file.FilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.resources.ServerAttributeModuleResource;

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
 * Data access object for server attribute modules. Used by {@link ServerAttributeModuleResource} servlet.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class ServerAttributeModulesDAO {

    private Map<String, ListColumnInfo> listColumnsInfo = new HashMap<String, ListColumnInfo>();

    private static final Logger log = LoggerFactory.getLogger (ServerAttributeModulesDAO.class);

    private XMLOutputter xmlo = new XMLOutputter();

    public ServerAttributeModulesDAO () {
        super ();
        
        // set up the column list for group lists
        //
        listColumnsInfo.put ("id",       new ListColumnInfo (0, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("type",     new ListColumnInfo (1, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("name",     new ListColumnInfo (2, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("dataType", new ListColumnInfo (3, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("value",    new ListColumnInfo (4, ListResultRowComparator.SORT_TYPE_STRING));
    }

    /*
     * Servlet methods
     */

    /**
     * <p>
     * Sorts and filters the list of {@link ServerAttribute} objects and returns a {@link ListResult} object
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
     * @see                ServerAttributeModuleResource
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
        
        log.debug ("Getting list of server attributes in " + path);
        ServerAttributeModule sam = findByPath (path);

        if (sam == null) {
            log.error ("parsed server attributes module is null.");
            return null;
        }
        
        // get current list of group modules in display list form
        //
        resultList = getServerAttributeList (sam);
        
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
     * Searches for a group module based on a URL encoded path and populates a {@link ServerAttributeModule} object.
     * </p>
     * 
     * @param  path  The URL encoded ID to look for.
     * @return       The requested group module as an {@link ServerAttributeModule} object.
     * @see          ServerAttributeModuleResource
     */
    public ServerAttributeModule findByPath (String path) {
        ServerAttributeModule sam = new ServerAttributeModule();
        File f;
        SAXBuilder builder = new SAXBuilder();
        Document samDoc;
        
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
        
        sam.setPath (path);
        
        try {
            samDoc = builder.build (path);
            Element rootNode = samDoc.getRootElement();
            Namespace ns = samDoc.getNamespacesInScope().get(0); // should only be one namespace
            List<ServerAttribute> serverAttributes = new ArrayList<ServerAttribute>();
            List<Element> samList = rootNode.getChildren();
            
            log.debug ("Located " + samList.size() + " child elements.");

            // iterate over all the "server attribute" elements
            // 
            int i = 0;
            log.debug ("Iterating over child elements.");
            for (Element samChild : samList) {
                
                log.debug ("Examining child #" + i++ + ": " + samChild.getName() + "{" + samChild.getNamespace().getURI() + "}");
                
                serverAttributes.add (new ServerAttribute (samChild));
            }
            
            sam.setServerAttributes (serverAttributes);

        } catch (IOException io) {
            log.error ("findByPath() unable to load " + path + ":" + io.getMessage());
        } catch (JDOMException jdomex) {
            log.error ("findByPath() unable to load " + path + ":" + jdomex.getMessage());
        }

        return sam;
    }
    
    /**
     * <p>
     * Writes the server attribute module to disk as an XML file.
     * </p>
     * 
     * @param  sam  The group module to write out.
     */
    public void serialize (ServerAttributeModule sam) throws Exception {
        String indent = StringUtils.getIndent (1);
        if (sam.getPath() == null)
            throw new IllegalArgumentException ("Server attribute module's path may not be null.");
        
        File f = new File (sam.getPath());

        // if user preferences indicate to save backups of edited files, back up the original file.
        //
        if (PreferencesManager.getInstance().getBackupFiles().equals ("true")) {
            FileUtils.copyFile (f, new File (sam.getPath() + ".bak"));
        }
        
        Document samDoc = new Document (new Element ("ServerAttributeModule", "ns1", DAOConstants.MODULES_NS));
        samDoc.getRootElement().addContent ("\n");

        if (sam.getServerAttributes() != null) {
            for (ServerAttribute sa : sam.getServerAttributes()) {
                Element saNode = sa.toElement ("ignored", 2).addContent ("\n" + indent); // element name is ignored as the element will choose its own name.

                samDoc.getRootElement().addContent (indent);
                samDoc.getRootElement().addContent (saNode);
                samDoc.getRootElement().addContent ("\n");
            }
        }

        xmlo.output (samDoc, new FileOutputStream (sam.getPath()));
    }
    
    public ResultMessage generate (
        String path,
        String profilePath,
        String serverId,
        String startPath
    ) {
        ResultMessage result;
        List<ResultMessage.MessageItem> validationItems = new ArrayList<ResultMessage.MessageItem>();
        ServerAttributeModule sam;
        Server s;

        log.debug ("generate received path = \"" + path + "\", profilePath = \"" + profilePath + "\", serverId = \"" + serverId + "\", startPath = \"" + startPath + "\".");
        
        // validate inputs
        //
        log.debug ("validating inputs ...");

        if (path == null || path.length() == 0) {
            validationItems.add (new ResultMessage.MessageItem ("id", "Server attribute module path may not be null!"));
        } else {
            sam = findByPath (path);
            if (sam == null) {
                validationItems.add (new ResultMessage.MessageItem ("id", "Server attribute module, " + path + ", does not exist!"));
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
            validationItems.add (new ResultMessage.MessageItem ("domain", "Server attribute starting path may not be null!"));
        }

        log.debug ("done.");

        log.debug ("validationItems.size() = " + validationItems.size());
        if (validationItems.size() > 0) {
            return new ResultMessage ("error", null, validationItems);
        }

        // write out temporary generate plan
        //
        String generatedXmlFileName = "tmpServerAttributes_" + System.currentTimeMillis() + ".xml";
        String planPath = writeGenerationPlan (serverId, startPath, generatedXmlFileName);
        if (planPath == null) {
            return new ResultMessage ("error", "Unable to write out temporary server attribute generation plan!", null);
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
            return new ResultMessage ("error", "Unable to launch PDTool command line to generate server attributes!", null);
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
        
        // make sure the server attributes xml got created.
        //
        String generatedXmlPath = FilesDAO.getPdtHome() + "/resources/modules/" + generatedXmlFileName;
        if (! (new File (generatedXmlPath).exists())) {
            return new ResultMessage ("error", "Server attributes did not get generated! Please see latest PDTool log for more information.", null);
        }
        
        // merge result module with input module
        //
        log.debug ("merging generated server attributes with original server attributes ...");
        ServerAttributeModule mergedSam = mergeServerAttributeModules (path, generatedXmlPath);
        
        // delete the generated server attributes module
        //
        log.debug ("deleting generated server attirbutes file ...");
        (new File (generatedXmlPath)).delete();

        // write out the merged server attributes module
        //
        log.debug ("writing out merged server attributes ...");
        try {
            serialize (mergedSam);
            result = new ResultMessage ("success", "Server attributes from server " + serverId + ", start path " + startPath + " successfully added.", null);
        } catch (Exception e) {
            result = new ResultMessage ("error", "Unable to serialize merged original and generated server attributes!", null);
        }
        
        log.debug ("done.");
        return result;
    }
    
    // assembles the list result row list
    //
    private List<ListResult.Row> getServerAttributeList (ServerAttributeModule sam) {
        List<ListResult.Row> sal = new ArrayList<ListResult.Row>();
        
        if (sam.getServerAttributes() != null && sam.getServerAttributes().size() > 0) {
            for (ServerAttribute sa : sam.getServerAttributes()) {
                if (sa.getType() == ServerAttribute.TYPE_ATTRIBUTE_DEF)
                    continue;
                
                List<String> cell = new ArrayList<String>();
                
                String value = sa.getAttribute().getValue();
                
                if (sa.getAttribute().getValueArray() != null) {
                    value = "<Array Value>";
                } else if (sa.getAttribute().getValueList() != null) {
                    value = "<List Value>";
                } else if (sa.getAttribute().getValueMap() != null) {
                    value = "<Map Value>";
                } else if (sa.getAttribute().getType().equals ("PASSWORD_STRING")) {
                    value = value.replaceAll (".", "x");
                }
                
                cell.add (sa.getId());
                cell.add (sa.getAttribute().getName());
                cell.add (sa.getAttribute().getType());
                cell.add (value);
                
                sal.add (new ListResult.Row (sa.getId(), cell));
            }
        }
        
        return sal;
    }
    
    // writes out a temporary group generation plan
    //
    private String writeGenerationPlan (
        String serverId,
        String startPath,
        String generatedXmlPath
    ) {
        return FilesDAO.writeTempGenerationPlan(
            "PASS	TRUE	ExecuteAction	generateServerAttributesXML	\"" + serverId + "\"	\"" + startPath + "\"	\"$MODULE_HOME/" + generatedXmlPath + "\"	\"$MODULE_HOME/servers.xml\" READ_WRITE"
        );
    }
    
    private ServerAttributeModule mergeServerAttributeModules (
        String origPath,
        String toMergePath
    ) {
        ServerAttributeModule origSam = findByPath (origPath);
        ServerAttributeModule toMergeSam = findByPath (toMergePath);
        int i;
        
        for (ServerAttribute sa : toMergeSam.getServerAttributes()) {
            
            for (ServerAttribute osa : origSam.getServerAttributes()) {
                i = 1;
                
                while (sa.getId().equals (osa.getId())) {
                    sa.setId (osa.getId() + "-" + i++);
                }
            }
        }
        
        origSam.getServerAttributes().addAll(toMergeSam.getServerAttributes());
        
        return origSam;        
    }
}
package com.cisco.dvbu.ps.deploytool.gui.core.module.trigger;

import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfile;
import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.Preferences;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.PreferencesManager;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.log.LogsDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.config.server.Server;
import com.cisco.dvbu.ps.deploytool.gui.core.config.server.ServersDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.file.FilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.resources.TriggerModuleResource;

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
 * Data access object for trigger modules. Used by {@link TriggerModuleResource} servlet.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class TriggerModulesDAO {

    private Map<String, ListColumnInfo> listColumnsInfo = new HashMap<String, ListColumnInfo>();

    private static final Logger log = LoggerFactory.getLogger (TriggerModulesDAO.class);

    private XMLOutputter xmlo = new XMLOutputter();

    public TriggerModulesDAO () {
        super ();
        
        // set up the column list for group lists
        //
        listColumnsInfo.put ("id",           new ListColumnInfo (0, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("type",         new ListColumnInfo (1, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("resourcePath", new ListColumnInfo (2, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("condition",    new ListColumnInfo (3, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("action",       new ListColumnInfo (4, ListResultRowComparator.SORT_TYPE_STRING));
    }

    /*
     * Servlet methods
     */

    /**
     * <p>
     * Sorts and filters the list of {@link Trigger} objects and returns a {@link ListResult} object
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
     * @see                TriggerModuleResource
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
        
        log.debug ("Getting list of triggers in " + path);
        TriggerModule tm = findByPath (path);

        if (tm == null) {
            log.error ("parsed trigger module is null.");
            return null;
        }
        
        // get current list of trigger modules in display list form
        //
        resultList = getTriggerList (tm);
        
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
     * Searches for a trigger module based on a URL encoded path and populates a {@link TriggerModule} object.
     * </p>
     * 
     * @param  path  The URL encoded ID to look for.
     * @return       The requested trigger module as an {@link TriggerModule} object.
     * @see          TriggerModuleResource
     */
    public TriggerModule findByPath (String path) {
        TriggerModule tm = new TriggerModule();
        File f;
        SAXBuilder builder = new SAXBuilder();
        Document tmDoc;
        
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
        
        tm.setPath (path);
        
        try {
            tmDoc = builder.build (path);
            Element rootNode = tmDoc.getRootElement();
            Namespace ns = tmDoc.getNamespacesInScope().get(0); // should only be one namespace
            List<Trigger> triggers = new ArrayList<Trigger>();
            List<Element> tmList = rootNode.getChildren();
            
            log.debug ("Located " + tmList.size() + " child elements.");

            // iterate over the "trigger" and "schedule" elements
            // 
            int i = 0, j = 0;
            log.debug ("Iterating over child elements.");
            for (Element tmChild : tmList) {
                
                log.debug ("Examining child #" + i++ + ": " + tmChild.getName() + "{" + tmChild.getNamespace().getURI() + "}");
                
                // iterate over the "trigger" and "schedule" children
                //
                for (Element tChild : tmChild.getChildren()) {
                    
                    log.debug ("Examining grandchild #" + j++ + ": " + tChild.getName() + "{" + tChild.getNamespace().getURI() + "}");
                    triggers.add (new Trigger (tChild));
                }
            }
            
            tm.setTriggers (triggers);

        } catch (IOException io) {
            log.error ("findByPath() unable to load " + path + ":" + io.getMessage());
        } catch (JDOMException jdomex) {
            log.error ("findByPath() unable to load " + path + ":" + jdomex.getMessage());
        }

        return tm;
    }
    
    /**
     * <p>
     * Writes the trigger module to disk as an XML file.
     * </p>
     * 
     * @param  tm  The trigger module to write out.
     */
    public void serialize (TriggerModule tm) throws Exception {
        String indent = StringUtils.getIndent (1);
        String indent2 = StringUtils.getIndent (2);

        if (tm.getPath() == null)
            throw new IllegalArgumentException ("Trigger module's path may not be null.");
        
        File f = new File (tm.getPath());

        // if user preferences indicate to save backups of edited files, back up the original file.
        //
        if (PreferencesManager.getInstance().getBackupFiles().equals ("true")) {
            FileUtils.copyFile (f, new File (tm.getPath() + ".bak"));
        }
        
        Document tmDoc = new Document (new Element ("TriggerModule", "ns1", DAOConstants.MODULES_NS));
        tmDoc.getRootElement().addContent ("\n");
        
        if (tm.getTriggers() != null) {
            
            List<Trigger> triggers = new ArrayList<Trigger>();
            List<Trigger> schedules = new ArrayList<Trigger>();
        
            // separate entries into triggers and schedules lists
            //
            for (Trigger t : tm.getTriggers()) {
                if (t.type == Trigger.TYPE_TRIGGER)
                    triggers.add (t);
                else
                    schedules.add (t);
            }
            
            Element tlNode = new Element ("triggerList");
            
            for (Trigger t : triggers) {
                tlNode.addContent ("\n" + indent2);
                tlNode.addContent (t.toElement ("trigger", 3).addContent ("\n" + indent2));
            }
            
            tlNode.addContent ("\n" + indent);

            tmDoc.getRootElement().addContent (indent);
            tmDoc.getRootElement().addContent (tlNode);
            tmDoc.getRootElement().addContent ("\n");
            
            Element slNode = new Element ("scheduleList");
            
            for (Trigger t : schedules) {
                slNode.addContent ("\n" + indent2);
                slNode.addContent (t.toElement ("schedule", 3).addContent ("\n" + indent2));
            }

            slNode.addContent ("\n" + indent);

            tmDoc.getRootElement().addContent (indent);
            tmDoc.getRootElement().addContent (slNode);
            tmDoc.getRootElement().addContent ("\n");
        }

        xmlo.output (tmDoc, new FileOutputStream (tm.getPath()));
    }
    
    public ResultMessage generate (
        String path,
        String profilePath,
        String serverId,
        String startPath
    ) {
        ResultMessage result;
        List<ResultMessage.MessageItem> validationItems = new ArrayList<ResultMessage.MessageItem>();
        TriggerModule tm;
        Server s;

        log.debug ("generate received path = \"" + path + "\", profilePath = \"" + profilePath + "\", serverId = \"" + serverId + "\", startPath = \"" + startPath + "\".");
        
        // validate inputs
        //
        log.debug ("validating inputs ...");

        if (path == null || path.length() == 0) {
            validationItems.add (new ResultMessage.MessageItem ("id", "Trigger module path may not be null!"));
        } else {
            tm = findByPath (path);
            if (tm == null) {
                validationItems.add (new ResultMessage.MessageItem ("id", "Trigger module, " + path + ", does not exist!"));
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
            validationItems.add (new ResultMessage.MessageItem ("domain", "Trigger domain may not be null!"));
        }

        log.debug ("done.");

        log.debug ("validationItems.size() = " + validationItems.size());
        if (validationItems.size() > 0) {
            return new ResultMessage ("error", null, validationItems);
        }

        // write out temporary generate plan
        //
        String generatedXmlFileName = "tmpTriggers_" + System.currentTimeMillis() + ".xml";
        String planPath = writeGenerationPlan (serverId, startPath, generatedXmlFileName);
        if (planPath == null) {
            return new ResultMessage ("error", "Unable to write out temporary trigger generation plan!", null);
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
            return new ResultMessage ("error", "Unable to launch PDTool command line to generate trigger!", null);
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
        
        // make sure the trigger xml got created.
        //
        String generatedXmlPath = FilesDAO.getPdtHome() + "/resources/modules/" + generatedXmlFileName;
        if (! (new File (generatedXmlPath).exists())) {
            return new ResultMessage ("error", "Triggers did not get generated! Please see latest PDTool log for more information.", null);
        }
        
        // merge result module with input module
        //
        log.debug ("merging generated groups with original groups ...");
        TriggerModule mergedTm = mergeTriggerModules (path, generatedXmlPath);
        
        // delete the generated trigger module
        //
        log.debug ("deleting generated trigger file ...");
        (new File (generatedXmlPath)).delete();

        // write out the merged group module
        //
        log.debug ("writing out merged groups ...");
        try {
            serialize (mergedTm);
            result = new ResultMessage ("success", "Trigger from server " + serverId + ", startPath " + startPath + " successfully added.", null);
        } catch (Exception e) {
            result = new ResultMessage ("error", "Unable serialize merged original and generated trigger!", null);
        }
        
        log.debug ("done.");
        return result;
    }
    
    // assembles the list result row list
    //
    private List<ListResult.Row> getTriggerList (TriggerModule tm) {
        List<ListResult.Row> tl = new ArrayList<ListResult.Row>();
        
        if (tm.getTriggers() != null && tm.getTriggers().size() > 0) {
            for (Trigger t : tm.getTriggers()) {
                List<String> cell = new ArrayList<String>();
                
                cell.add (t.getId());
                cell.add ((t.getType() == Trigger.TYPE_TRIGGER) ? "Trigger" : "Schedule");
                cell.add (t.getResourcePath());
                cell.add ((t.getType() == Trigger.TYPE_TRIGGER && t.getCondition() != null) ? Trigger.Condition.TYPE_LABELS[t.getCondition().getType()] : null);
                cell.add ((t.getType() == Trigger.TYPE_TRIGGER && t.getAction() != null) ? Trigger.Action.TYPE_LABELS[t.getAction().getType()] : null);
                
                tl.add (new ListResult.Row (t.getId(), cell));
            }
        }
        
        return tl;
    }
    
    // writes out a temporary trigger generation plan
    //
    private String writeGenerationPlan (
        String serverId,
        String startPath,
        String generatedXmlPath
    ) {
        return FilesDAO.writeTempGenerationPlan(
            "PASS	TRUE	ExecuteAction	generateTriggersXML	\"" + serverId + "\"	\"" + startPath + "\"	\"$MODULE_HOME/" + generatedXmlPath + "\"	\"$MODULE_HOME/servers.xml\""
        );
    }
    
    private TriggerModule mergeTriggerModules (
        String origPath,
        String toMergePath
    ) {
        TriggerModule origTm = findByPath (origPath);
        TriggerModule toMergeTm = findByPath (toMergePath);
        int i;
        
        for (Trigger t : toMergeTm.getTriggers()) {
            
            for (Trigger ot : origTm.getTriggers()) {
                i = 1;
                
                while (t.getId().equals (ot.getId())) {
                    t.setId (ot.getId() + "-" + i++);
                }
            }
        }
        
        origTm.getTriggers().addAll(toMergeTm.getTriggers());
        
        return origTm;        
    }
}

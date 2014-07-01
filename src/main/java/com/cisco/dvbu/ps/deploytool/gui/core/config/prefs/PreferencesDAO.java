package com.cisco.dvbu.ps.deploytool.gui.core.config.prefs;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfile;
import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.config.server.Server;
import com.cisco.dvbu.ps.deploytool.gui.core.config.server.ServersDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.module.data_source.DataSource;
import com.cisco.dvbu.ps.deploytool.gui.core.module.data_source.DataSourceModulesDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.execute.ExecutePDTool;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.file.FilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.log.LogsDAO;
import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;
import com.cisco.dvbu.ps.deploytool.gui.resources.PreferenceResource;
import com.cisco.dvbu.ps.deploytool.gui.util.DAOConstants;

import java.io.File;

import java.net.URLDecoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Data access object for user preferences. Used by {@link PreferenceResource}.
 * </p>
 *
 * @author Calvin Goodrich
 * @version 1.0
 */
public class PreferencesDAO {
    
    private static final Logger log = LoggerFactory.getLogger (PreferencesDAO.class);

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public PreferencesDAO () {}
    
    /**
     * <p>
     * Returns the static preferences object as generated from the "pdtoolgui.yml" file found in $PDTOOL_HOME/gui.
     * </p>
     * 
     * @return     The requested {@link Preferences} as an object.
     * @see        PreferenceResource
     */
    public Preferences get() {
        return PreferencesManager.getInstance();
    }
    
    public ResultMessage edit (Preferences prefs) {
        ResultMessage result = null;

        // validate the preferences submitted by the UI before writing them to disk. Return
        // any validation errors.
        //
        log.debug ("validating input preferences");
        List<ResultMessage.MessageItem> tmpMsgList = validate (prefs);
        if (tmpMsgList.size() > 0)
            return new ResultMessage ("error", null, tmpMsgList);
        
        // set the new preferences in the static Preferences instance
        //
        log.debug ("updating in-memory preferences");
        PreferencesManager.getInstance().setBackupFiles (prefs.getBackupFiles());
        PreferencesManager.getInstance().setDefaultProfile (prefs.getDefaultProfile());
        PreferencesManager.getInstance().setDefaultServer (prefs.getDefaultServer());
        PreferencesManager.getInstance().setRestrictAccessToLocalhost (prefs.getRestrictAccessToLocalhost());
        PreferencesManager.getInstance().setXmlIndentWidth (prefs.getXmlIndentWidth());
        
        // write the new preferences out to disk.
        //
        log.debug ("writing out preferences to YML file");
        result = PreferencesManager.serialize();
        if (result.getStatus().equals ("error"))
            return result;
        
        // if the user wants to update the data source attributes from the current default server
        // (this will take a few minutes.)
        //
        if (prefs.isUpdateDSAttributes()) {
            log.debug ("generating new data source types and attributes from " + prefs.defaultServer);

            result = generateDSTypesAndAttributes (prefs);
        }
        return result;
    }

    /**
     * <p>
     * Initializes the Preferences static class.
     * </p>
     * 
     * @param  prefs  The {@link Preferences} class to validate.
     * @return        A {@link ResultMessage.MessageItem} class containing the result of the validation.
     */
    protected static List<ResultMessage.MessageItem> validate (Preferences prefs) {
        List<ResultMessage.MessageItem> result = new ArrayList<ResultMessage.MessageItem>();
        
        if (prefs.getBackupFiles() == null)
            result.add (new ResultMessage.MessageItem ("backupFiles", "Backup Files may not be null."));

        if (prefs.defaultProfile == null)
            result.add (new ResultMessage.MessageItem ("defaultProfile", "Default deployment profile may not be null."));
        
        try {
            DeploymentProfile dp = new DeploymentProfilesDAO().getById (URLDecoder.decode (prefs.getDefaultProfile().replace('+', ' '), "UTF-8"));
            if (dp == null)
                result.add (new ResultMessage.MessageItem ("defaultProfile", "Default deployment profile does not exist."));
        } catch (Exception ignored) {;}
        
        if (prefs.defaultServer == null)
            result.add (new ResultMessage.MessageItem ("defaultServer", "Default Server may not be null."));
        
        try {
            Server s = new ServersDAO().findById (URLDecoder.decode (prefs.getDefaultServer().replace('+', ' '), "UTF-8"));
            if (s == null)
                result.add (new ResultMessage.MessageItem ("defaultServer", "Default Server does not exist."));
        } catch (Exception ignored) {;}
        
        if (prefs.getRestrictAccessToLocalhost() == null)
            result.add (new ResultMessage.MessageItem ("restrictAccessToLocalhost", "Restrict Access To Localhost may not be null."));

        if (prefs.getXmlIndentWidth() <= 0)
            result.add (new ResultMessage.MessageItem ("xmlIndentWidth", "XML Indent Width must be a positive integer."));

        return result;
    }
    
    // regenerate data source types and attributes for the default server
    //
    private ResultMessage generateDSTypesAndAttributes (Preferences prefs) {
        String planPath;
        ResultMessage result = null;
        
        // delete any existing type and attribute files
        //
        log.debug ("cleaning up previous type and attribute files");
        File dir = new File (FilesDAO.getPdtHome() + "/" + DAOConstants.TEMPLATES_REL_PATH);
        
        Collection<File> fileNames = FileUtils.listFiles (
                                         dir, 
                                         new RegexFileFilter ("^default.*\\.xml$"), 
                                         DirectoryFileFilter.DIRECTORY
                                     );

        if (fileNames != null) {
            for (Iterator<File> itr = fileNames.iterator(); itr.hasNext(); ) {
                File f = itr.next();
                log.debug ("deleting file \"" + f.getPath() + "\"");
                f.delete();
            }
        }
        
        // write out temp plan to update data source type list
        //
        planPath = writeDSTypesGenerationPlan (prefs.getDefaultServer());
        if (planPath == null) {
            return new ResultMessage ("error", "Unable to write out temporary data source type generation plan!", null);
        }
        log.debug ("wrote out temporary data source types plan " + planPath);
        
        // execute types plan
        //
        log.debug ("executing data source types plan.");
        result = executePlan (prefs, planPath, true);
        if (result.getStatus().equals ("error"))
            return result;
        
        // iterate over ds type list and generate temp plan to update data source attribute lists
        //
        planPath = writeDSAttributesGenerationPlan (prefs.getDefaultServer());
        if (planPath == null) {
            return new ResultMessage ("error", "Unable to write out temporary data source attribute generation plan!", null);
        }
        log.debug ("wrote out temporary data source attributes plan " + planPath);
                
        // execute attributes plan
        //
        log.debug ("executing data source attributes plan.");
        result = executePlan (prefs, planPath, false);
        if (result.getStatus().equals ("error"))
            return result;
        
        return new ResultMessage (
                      "success", 
                      null, 
                      Arrays.asList (
                          new ResultMessage.MessageItem (null, "Preferences successfully updated."), 
                          new ResultMessage.MessageItem (null, "Data source types and attributes updated.")
                      )
                   );
    }

    // writes out a temporary data source types generation plan
    //
    private String writeDSTypesGenerationPlan (
        String serverId
    ) {
        return FilesDAO.writeTempGenerationPlan (
            "PASS	TRUE	ExecuteAction	generateDataSourceTypes	\"" + serverId + "\"	\"$PROJECT_HOME/" + DAOConstants.DS_TYPES_REL_PATH + "\"	\"$MODULE_HOME/servers.xml\""
        );
    }
    
    // writes out a temporary data source attributes generation plan
    //
    private String writeDSAttributesGenerationPlan (
        String serverId
    ) {
        Map<String, String> dsTypes = getDSTypes();
        String plan = "";
        
        // iterate over map entries and build up the plan
        //
        for (Map.Entry<String, String> e : dsTypes.entrySet()) {
            String relPath = DAOConstants.DS_ATTRIBUTES_REL_PATH.replaceAll ("<TYPE>", e.getKey());
            log.debug ("writing out plan entry for " + e.getKey() + ": XML relative path = " + relPath);
            plan += "PASS	TRUE	ExecuteAction	generateDataSourceAttributeDefsByDataSourceType	\"" + serverId + "\"	\"" + e.getValue() + "\"	\"$PROJECT_HOME/" + relPath + "\"	\"$MODULE_HOME/servers.xml\"\n";
        }
        return FilesDAO.writeTempGenerationPlan (plan);
    }
    
    // get map of data source basic types from data source types XML
    //
    private Map<String, String> getDSTypes() {
        DataSourceModulesDAO dsDao = new DataSourceModulesDAO();
        Map<String, String> dsTypes = new HashMap<String, String>();
        
        // gather data source types into a hashmap by basic type (this will reduce the time it takes to 
        // generate as there can be multiple data source types for a basic type (i.e. Oracle 9, 10, 11, 
        // and 12 all have a basic type of Oracle.)
        //
        for (DataSource.DataSourceType dst : dsDao.findByPath (FilesDAO.getPdtHome() + "/" + DAOConstants.DS_TYPES_REL_PATH).getDataSources().get (0).getDataSourceTypes()) {
            dsTypes.put(dst.getType().replaceAll ("[\\(\\)]", ""), dst.getName()); // MsExcel(POI) is a type. >:(
        }
        
        return dsTypes;
    }
    
    private ResultMessage executePlan(
        Preferences prefs,
        String planPath,
        boolean isTypesPlan
    ) {
        // execute pdtool against the temp plan
        //
        log.debug ("launching pdtool ...");
        ExecutePDTool epdt = new ExecutePDTool (planPath, prefs.getDefaultProfile());
        
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
            return new ResultMessage ("error", "Unable to launch PDTool command line to generate data source XML!", null);
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
        
        // make sure the data source xml(s) got created.
        //
        if (isTypesPlan) {
            String generatedXmlPath = FilesDAO.getPdtHome() + "/" + DAOConstants.DS_TYPES_REL_PATH;
            if (! (new File (generatedXmlPath).exists())) {
                return new ResultMessage ("error", "Data source types did not get generated! Please see latest PDTool log for more information.", null);
            }
        } else {
            Map<String, String> dsTypes = getDSTypes();
            List<ResultMessage.MessageItem> tmpMsgList = new ArrayList<ResultMessage.MessageItem>();

            // iterate over map entries and build up the plan
            //
            for (Map.Entry<String, String> e : dsTypes.entrySet()) {
                String generatedXmlPath = FilesDAO.getPdtHome() + "/" + DAOConstants.DS_ATTRIBUTES_REL_PATH.replaceAll ("<TYPE>", e.getKey());
                if (! (new File (generatedXmlPath).exists())) {
                    tmpMsgList.add (new ResultMessage.MessageItem (null, "Data source attributes for " + e.getKey() + " did not get generated! "));
                }
            }
            
            if (tmpMsgList.size() > 0) {
                tmpMsgList.add(new ResultMessage.MessageItem (null, "Please see latest PDTool log for more information."));
                
                return new ResultMessage ("error", null, tmpMsgList);
            }
        }
        
        return new ResultMessage ("success", "Data source module(s) generated.", null);
    }
}

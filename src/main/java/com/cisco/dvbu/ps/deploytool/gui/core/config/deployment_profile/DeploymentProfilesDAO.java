package com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.PreferencesManager;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.file.FilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.shared.EnvironmentVariable;
import com.cisco.dvbu.ps.deploytool.gui.resources.DeploymentProfileResource;
import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Data access object for deployment profiles. Used by {@link DeploymentProfileResource} servlet.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class DeploymentProfilesDAO {

    private static final Logger log = LoggerFactory.getLogger (DeploymentProfilesDAO.class);
    private static Pattern knownPropertiesREPattern;
    private static Pattern variableNameREPattern;
    
    static {
        try {
            knownPropertiesREPattern = Pattern.compile(
                "^(?:SUPPRESS_COMMENTS|DEBUG1|DEBUG2|DEBUG3|DIFFMERGER_VERBOSE|propertyOrderPrecedence|allResourcesIndicator|" +
                "exculdeResourcesIndiator|excludeResourcesIndicator|userOptionThreshold|DataSourceModule_NonUpdateableAttributes|" +
                "ServerAttributeModule_NonUpdateableAttributes|DomainModule_NonUpdateableAttributes|" +
                "VCSModule_ExternalVcsResourceTypeList|CIS_PING_SERVER|CIS_CONNECT_RETRY|CIS_CONNECT_RETRY_SLEEP_MILLIS|" +
                "VCS_MULTI_USER_TOPOLOGY|VCS_TYPE|VCS_BASE_TYPE|VCS_HOME|VCS_COMMAND|VCS_EXEC_FULL_PATH|VCS_OPTIONS|VCS_WORKSPACE_INIT_NEW_OPTIONS|" +
                "VCS_WORKSPACE_INIT_LINK_OPTIONS|VCS_WORKSPACE_INIT_GET_OPTIONS|VCS_BASE_FOLDER_INIT_ADD|VCS_CHECKIN_OPTIONS|" + 
                "VCS_CHECKIN_OPTIONS_REQUIRED|VCS_CHECKOUT_OPTIONS|VCS_CHECKOUT_OPTIONS_REQUIRED|VCS_CIS_IMPORT_OPTIONS|VCS_CIS_EXPORT_OPTIONS|" +
                "VCS_REPOSITORY_URL|VCS_PROJECT_ROOT|VCS_WORKSPACE_HOME|VCS_WORKSPACE_NAME|VCS_WORKSPACE_DIR|VCS_TEMP_DIR|VCS_USERNAME|" +
                "VCS_PASSWORD|VCS_IGNORE_MESSAGES|VCS_MESSAGE_PREPEND|SVN_EDITOR|SVN_ENV|P4EDITOR|P4CLIENT|P4PORT|P4USER|" +
                "P4PASSWD|P4_ENV|P4DEL_LINK_OPTIONS|CVSROOT|CVS_RSH|CVS_ENV|TFS_EDITOR|TFS_ENV|TFS_CHECKIN_OPTIONS|" +
                "TFS_SERVER_URL|MODULE_HOME|SUMMARY_LOG|SCHEMA_LOCATION|nonRebindableResourceSubTypeList)$"
            );
            
            variableNameREPattern = Pattern.compile ("^\\w+(?==)"); // i.e. lines that start with "var=" but leaving off the "="
        } catch (Exception e) {
            log.error ("Unable to compile regular expression pattern: " + e.getMessage());
        }
    }

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public DeploymentProfilesDAO () {}

    /*
     * Servlet methods
     */

    /**
     * <p>
     * Searches for a single {@link DeploymentProfile} object given a URL encoded file path.
     * </p>
     * 
     * @param  path  The URL encoded ID to look for.
     * @return       The requested {@link DeploymentProfile} as an object.
     * @see          DeploymentProfileResource
     */
    public DeploymentProfile getById (String path) {
        File f;
        Properties p = new Properties();
        DeploymentProfile dp = new DeploymentProfile();
        Matcher m;
        List<EnvironmentVariable> customVars = new ArrayList<EnvironmentVariable>();
        
        log.debug ("getById: Sanity checking input file path: " + path);
        
        if (path == null) {
            log.error ("getById: Error: \"path\" input may not be NULL.");
            return null;
        }
        
        f = new File (path);

        if (! f.exists()) {
            log.error ("getById: Error retrieving file \"" + path + "\": file does not exist.");
            return null;
        }
        
        if (! f.canRead()) {
            log.error ("getById: Error retrieving file \"" + path + "\": file is not readable.");
            return null;
        }
        
        log.debug ("getById: Opening input stream and reading properties.");
        try {
            p.load (new FileInputStream (f));
        } catch (Exception e) { 
            log.error ("Error retrieving file \"" + path + "\": " + e.getMessage());
            return null;
        }

        log.debug ("getById: Extracting known properties.");
        dp.setSuppressComments (Boolean.parseBoolean (p.getProperty ("SUPPRESS_COMMENTS")));
        dp.setDebug1 (Boolean.parseBoolean (p.getProperty ("DEBUG1")));
        dp.setDebug2 (Boolean.parseBoolean (p.getProperty ("DEBUG2")));
        dp.setDebug3 (Boolean.parseBoolean (p.getProperty ("DEBUG3")));
        dp.setDiffmergerVerbose (Boolean.parseBoolean (p.getProperty ("DIFFMERGER_VERBOSE")));
        dp.setPropertyOrderPrecedence (p.getProperty("propertyOrderPrecedence"));
        dp.setAllResourcesIndicator (p.getProperty("allResourcesIndicator"));
        if (p.getProperty ("excludeResourcesIndicator") != null) {
            dp.setExcludeResourcesIndicator (p.getProperty("excludeResourcesIndicator"));
        } else {
            dp.setExcludeResourcesIndicator (p.getProperty("exculdeResourcesIndiator"));
        }
        dp.setUserOptionThreshold (Integer.parseInt (p.getProperty("userOptionThreshold")));
        dp.setDataSourceModuleNonUpdateableAttributes (p.getProperty ("DataSourceModule_NonUpdateableAttributes"));
        dp.setServerAttributeModuleNonUpdateableAttributes (p.getProperty ("ServerAttributeModule_NonUpdateableAttributes"));
        dp.setDomainModuleNonUpdateableAttributes (p.getProperty ("DomainModule_NonUpdateableAttributes"));
        dp.setVcsModuleExternalVcsResourceTypeList (p.getProperty ("VCSModule_ExternalVcsResourceTypeList"));
        dp.setCisPingServer (Boolean.parseBoolean (p.getProperty ("CIS_PING_SERVER")));
        dp.setCisConnectRetry (Integer.parseInt (p.getProperty ("CIS_CONNECT_RETRY")));
        dp.setCisConnectRetrySleepMillis (Integer.parseInt (p.getProperty ("CIS_CONNECT_RETRY_SLEEP_MILLIS")));
        dp.setVcsMultiUserTopology (Boolean.parseBoolean (p.getProperty ("VCS_MULTI_USER_TOPOLOGY")));
        dp.setVcsType (p.getProperty ("VCS_TYPE"));
        dp.setVcsType (p.getProperty ("VCS_BASE_TYPE"));
        dp.setVcsHome (p.getProperty ("VCS_HOME"));
        dp.setVcsCommand (p.getProperty ("VCS_COMMAND"));
        dp.setVcsExecFullPath (Boolean.parseBoolean (p.getProperty ("VCS_EXEC_FULL_PATH")));
        dp.setVcsOptions (p.getProperty ("VCS_OPTIONS"));
        dp.setVcsWorkspaceInitNewOptions (p.getProperty ("VCS_WORKSPACE_INIT_NEW_OPTIONS"));
        dp.setVcsWorkspaceInitLinkOptions (p.getProperty ("VCS_WORKSPACE_INIT_LINK_OPTIONS"));
        dp.setVcsWorkspaceInitGetOptions (p.getProperty ("VCS_WORKSPACE_INIT_GET_OPTIONS"));
        dp.setVcsBaseFolderInitAdd (p.getProperty ("VCS_BASE_FOLDER_INIT_ADD"));
        dp.setVcsCheckinOptions (p.getProperty ("VCS_CHECKIN_OPTIONS"));
        dp.setVcsCheckinOptionsRequired (p.getProperty ("VCS_CHECKIN_OPTIONS_REQUIRED"));
        dp.setVcsCheckoutOptions (p.getProperty ("VCS_CHECKOUT_OPTIONS"));
        dp.setVcsCheckoutOptionsRequired (p.getProperty ("VCS_CHECKOUT_OPTIONS_REQUIRED"));
        dp.setVcsCisImportOptions (p.getProperty ("VCS_CIS_IMPORT_OPTIONS"));
        dp.setVcsCisExportOptions (p.getProperty ("VCS_CIS_EXPORT_OPTIONS"));
        dp.setVcsRepositoryUrl (p.getProperty ("VCS_REPOSITORY_URL"));
        dp.setVcsProjectRoot (p.getProperty ("VCS_PROJECT_ROOT"));
        dp.setVcsWorkspaceHome (p.getProperty ("VCS_WORKSPACE_HOME"));
        dp.setVcsWorkspaceName (p.getProperty ("VCS_WORKSPACE_NAME"));
        dp.setVcsWorkspaceDir (p.getProperty ("VCS_WORKSPACE_DIR"));
        dp.setVcsTempDir (p.getProperty ("VCS_TEMP_DIR"));
        dp.setVcsUsername (p.getProperty ("VCS_USERNAME"));
        dp.setVcsPassword (p.getProperty ("VCS_PASSWORD"));
        dp.setVcsIgnoreMessages (p.getProperty ("VCS_IGNORE_MESSAGES"));
        dp.setVcsMessagePrepend (p.getProperty ("VCS_MESSAGE_PREPEND"));
        dp.setSvnEditor (p.getProperty ("SVN_EDITOR"));
        dp.setSvnEnv (p.getProperty ("SVN_ENV"));
        dp.setP4Editor (p.getProperty ("P4EDITOR"));
        dp.setP4Client (p.getProperty ("P4CLIENT"));
        dp.setP4Port (p.getProperty ("P4PORT"));
        dp.setP4User (p.getProperty ("P4USER"));
        dp.setP4Passwd (p.getProperty ("P4PASSWD"));
        dp.setP4Env (p.getProperty ("P4_ENV"));
        dp.setP4DelLinkOptions (p.getProperty ("P4DEL_LINK_OPTIONS"));
        dp.setCvsRoot (p.getProperty ("CVSROOT"));
        dp.setCvsRsh (p.getProperty ("CVS_RSH"));
        dp.setCvsEnv (p.getProperty ("CVS_ENV"));
        dp.setTfsEditor (p.getProperty ("TFS_EDITOR"));
        dp.setTfsEnv (p.getProperty ("TFS_ENV"));
        dp.setTfsCheckinOptions (p.getProperty ("TFS_CHECKIN_OPTIONS"));
        dp.setTfsServerUrl (p.getProperty ("TFS_SERVER_URL"));
        
        log.debug ("getById: Looking for custom variables.");
        for (Enumeration<?> e = p.keys(); e.hasMoreElements(); ) {
            String key = (String) e.nextElement();
            m = knownPropertiesREPattern.matcher (key);
            
            if (! m.matches()) {
                log.debug ("getById: located custom variable: \"" + key + "\", value = \"" + p.getProperty (key) + "\"");
                customVars.add (new EnvironmentVariable (key, p.getProperty (key)));
            }
        }
        
        dp.setEnvVars (customVars);

        log.debug ("getById: Exiting.");
        return dp;
    }

    /**
     * <p>
     * Updates a {@link DeploymentProfile} properties file and writes it to disk. Attempts to preserve any comments.
     * </p>
     * 
     * @param  dp  The {@link DeploymentProfile} object to update.
     * @return     A {@link ResultMessage} object containing the results of the edit request.
     * @see        DeploymentProfileResource
     */
    public ResultMessage edit (DeploymentProfile dp) {
        File f;
        BufferedReader br = null;
        BufferedWriter bw = null;
        Matcher m = null;
        
        log.debug ("edit: Sanity checking input file path.");

        if (dp == null) {
            return new ResultMessage ("error", "Error: deployment profile may not be NULL.", null);
        }
        
        if (dp.getPath() == null) {
            return new ResultMessage ("error", "Error: \"path\" attribute may not be NULL.", null);
        }
        
        f = new File (dp.getPath());

        if (! f.exists()) {
            return new ResultMessage ("error", "Error retrieving file \"" + dp.getPath() + "\": file does not exist.", null);
        }
        
        if (! f.canWrite()) {
            return new ResultMessage ("error", "Error retrieving file \"" + dp.getPath() + "\": file is not writeable.", null);
        }
        
        try {
            File fb = new File (dp.getPath() + ".bak");
            String line;
            int lineNum = 0;
            boolean inCustomVarsSection = false;
            int numSeparators = 0; // keep track of the number of "#====" lines after encountering the custom variable section. the second one indicates the end of the section.
            Map<String, String> writtenCustomVars = new HashMap<String, String>();
            
            log.debug ("Moving \"" + f.getAbsolutePath() + "\" to \"" + fb.getAbsolutePath() + "\".");
            
            // windows doesn't let you copy a file to an existing file's name
            //
            if (fb.exists() && fb.canWrite())
                FileUtils.deleteQuietly (fb);

            FileUtils.copyFile (f, fb);
    
            // open the template file for reading
            //
            File ft = new File (FilesDAO.getPdtHome() + "/" + FilesDAO.fileTypeProperties.get (FilesDAO.FILE_TYPE_DEPLOY_CONFIG).getTemplate());

            log.debug ("Opening \"" + ft.getAbsolutePath() + "\" for reading.");
            br = new BufferedReader (new FileReader (ft));

            log.debug ("Opening \"" + f.getAbsolutePath() + "\" for writing.");
            bw = new BufferedWriter (new FileWriter (f, false));
            
            while ((line = br.readLine()) != null) {
                lineNum++;
                
                if (! line.startsWith("#") && ! line.matches("\\s+")) {
                    String varName = null;
                
                    // extract the variable name out of the line.
                    //
                    m = variableNameREPattern.matcher (line);
                    if (m.find()) {
                        varName = m.group();
                    }
                    
                    // if the beginning part of the line didn't look like a variable, skip it.
                    //
                    if (varName == null) {
                        log.debug ("unrecognized line in \"" + dp.getPath() + "\" line number " + lineNum + ": " + line);
                        continue;
                    }
                    log.debug ("Found variable \"" + varName + "\".");

                    if (inCustomVarsSection) {
                        log.debug ("Matching variable \"" + varName + "\" to known custom variables.");

                        if (dp.getEnvVars() != null) {
                            
                            // see if this is an existing environment variable. if not in the envVars list then it's being
                            // deleted and shouldn't be written out.
                            //
                            for (Iterator<EnvironmentVariable> i = dp.getEnvVars().iterator(); i.hasNext(); ) {
                                EnvironmentVariable ev = i.next();
                                
                                if (varName.equals(ev.getVariable())) {
                                    log.debug ("Setting custom variable \"" + varName + "\" to \"" + ev.getValue() + "\".");
                                    bw.write (ev.getVariable() + "=" + ev.getValue());
                                    bw.newLine();
                                    
                                    writtenCustomVars.put (ev.getVariable(), "true"); // keep track of custom variables written out already.
                                    
                                    break;
                                }
                            }
                        }
                    } else { // (! inCustomVarsSection)
                        String outValue = null;
                        
                        log.debug ("Matching variable \"" + varName + "\" to known PDTool variables.");

                        if (varName.equals ("SUPPRESS_COMMENTS")) {
                            outValue = "" + dp.isSuppressComments();
                        }
                        
                        if (varName.equals ("DEBUG1")) {
                            outValue = "" + dp.isDebug1();
                        }
                        
                        if (varName.equals ("DEBUG2")) {
                            outValue = "" + dp.isDebug2();
                        }
                        
                        if (varName.equals ("DEBUG3")) {
                            outValue = "" + dp.isDebug3();
                        }
                        
                        if (varName.equals ("DIFFMERGER_VERBOSE")) {
                            outValue = "" + dp.isDiffmergerVerbose();
                        }
                        
                        if (varName.equals ("propertyOrderPrecedence")) {
                            outValue = dp.getPropertyOrderPrecedence();
                        }
                        
                        if (varName.equals ("allResourcesIndicator")) {
                            outValue = dp.getAllResourcesIndicator();
                        }
                        
                        if (varName.equals ("exculdeResourcesIndiator")) {
                            outValue = dp.getExcludeResourcesIndicator();
                        }
                        
                        if (varName.equals ("userOptionThreshold")) {
                            outValue = "" + dp.getUserOptionThreshold();
                        }
                        
                        if (varName.equals ("DataSourceModule_NonUpdateableAttributes")) {
                            outValue = dp.getDataSourceModuleNonUpdateableAttributes();
                        }
                        
                        if (varName.equals ("ServerAttributeModule_NonUpdateableAttributes")) {
                            outValue = dp.getServerAttributeModuleNonUpdateableAttributes();
                        }
                        
                        if (varName.equals ("DomainModule_NonUpdateableAttributes")) {
                            outValue = dp.getDomainModuleNonUpdateableAttributes();
                        }
                        
                        if (varName.equals ("VCSModule_ExternalVcsResourceTypeList")) {
                            outValue = dp.getVcsModuleExternalVcsResourceTypeList();
                        }
                        
                        if (varName.equals ("CIS_PING_SERVER")) {
                            outValue = "" + dp.isCisPingServer();
                        }
                        
                        if (varName.equals ("CIS_CONNECT_RETRY")) {
                            outValue = "" + dp.getCisConnectRetry();
                        }
                        
                        if (varName.equals ("CIS_CONNECT_RETRY_SLEEP_MILLIS")) {
                            outValue = "" + dp.getCisConnectRetrySleepMillis();
                        }
                        
                        if (varName.equals ("VCS_MULTI_USER_TOPOLOGY")) {
                            outValue = "" + dp.isVcsMultiUserTopology();
                        }
                        
                        if (varName.equals ("VCS_TYPE")) {
                            outValue = dp.getVcsType();
                        }
                        
                        if (varName.equals ("VCS_BASE_TYPE")) {
                            outValue = dp.getVcsBaseType();
                        }
                        
                        if (varName.equals ("VCS_HOME")) {
                            outValue = dp.getVcsHome();
                        }
                        
                        if (varName.equals ("VCS_COMMAND")) {
                            outValue = dp.getVcsCommand();
                        }
                        
                        if (varName.equals ("VCS_EXEC_FULL_PATH")) {
                            outValue = "" + dp.isVcsExecFullPath();
                        }
                        
                        if (varName.equals ("VCS_OPTIONS")) {
                            outValue = dp.getVcsOptions();
                        }
                        
                        if (varName.equals ("VCS_WORKSPACE_INIT_NEW_OPTIONS")) {
                            outValue = dp.getVcsWorkspaceInitNewOptions();
                        }
                        
                        if (varName.equals ("VCS_WORKSPACE_INIT_LINK_OPTIONS")) {
                            outValue = dp.getVcsWorkspaceInitLinkOptions();
                        }
                        
                        if (varName.equals ("VCS_WORKSPACE_INIT_GET_OPTIONS")) {
                            outValue = dp.getVcsWorkspaceInitGetOptions();
                        }
                        
                        if (varName.equals ("VCS_BASE_FOLDER_INIT_ADD")) {
                            outValue = dp.getVcsBaseFolderInitAdd();
                        }
                        
                        if (varName.equals ("VCS_CHECKIN_OPTIONS")) {
                            outValue = dp.getVcsCheckinOptions();
                        }
                        
                        if (varName.equals ("VCS_CHECKIN_OPTIONS_REQUIRED")) {
                            outValue = dp.getVcsCheckinOptionsRequired();
                        }
                        
                        if (varName.equals ("VCS_CHECKOUT_OPTIONS")) {
                            outValue = dp.getVcsCheckoutOptions();
                        }
                        
                        if (varName.equals ("VCS_CHECKOUT_OPTIONS_REQUIRED")) {
                            outValue = dp.getVcsCheckoutOptionsRequired();
                        }
                        
                        if (varName.equals ("VCS_CIS_IMPORT_OPTIONS")) {
                            outValue = dp.getVcsCisImportOptions();
                        }
                        
                        if (varName.equals ("VCS_CIS_EXPORT_OPTIONS")) {
                            outValue = dp.getVcsCisExportOptions();
                        }
                        
                        if (varName.equals ("VCS_REPOSITORY_URL")) {
                            outValue = dp.getVcsRepositoryUrl();
                        }
                        
                        if (varName.equals ("VCS_PROJECT_ROOT")) {
                            outValue = dp.getVcsProjectRoot();
                        }
                        
                        if (varName.equals ("VCS_WORKSPACE_HOME")) {
                            outValue = dp.getVcsWorkspaceHome();
                        }
                        
                        if (varName.equals ("VCS_WORKSPACE_NAME")) {
                            outValue = dp.getVcsWorkspaceName();
                        }
                        
                        if (varName.equals ("VCS_WORKSPACE_DIR")) {
                            outValue = dp.getVcsWorkspaceDir();
                        }
                        
                        if (varName.equals ("VCS_TEMP_DIR")) {
                            outValue = dp.getVcsTempDir();
                        }
                        
                        if (varName.equals ("VCS_USERNAME")) {
                            outValue = dp.getVcsUsername();
                        }
                        
                        if (varName.equals ("VCS_PASSWORD")) {
                            outValue = dp.getVcsPassword();
                        }
                        
                        if (varName.equals ("VCS_IGNORE_MESSAGES")) {
                            outValue = dp.getVcsIgnoreMessages();
                        }
                        
                        if (varName.equals ("VCS_MESSAGE_PREPEND")) {
                            outValue = dp.getVcsMessagePrepend();
                        }
                        
                        if (varName.equals ("SVN_EDITOR")) {
                            outValue = dp.getSvnEditor();
                        }
                        
                        if (varName.equals ("SVN_ENV")) {
                            outValue = dp.getSvnEnv();
                        }
                        
                        if (varName.equals ("P4EDITOR")) {
                            outValue = dp.getP4Editor();
                        }
                        
                        if (varName.equals ("P4CLIENT")) {
                            outValue = dp.getP4Client();
                        }
                        
                        if (varName.equals ("P4PORT")) {
                            outValue = dp.getP4Port();
                        }
                        
                        if (varName.equals ("P4USER")) {
                            outValue = dp.getP4User();
                        }
                        
                        if (varName.equals ("P4PASSWD")) {
                            outValue = dp.getP4Passwd();
                        }
                        
                        if (varName.equals ("P4_ENV")) {
                            outValue = dp.getP4Env();
                        }
                        
                        if (varName.equals ("P4DEL_LINK_OPTIONS")) {
                            outValue = dp.getP4DelLinkOptions();
                        }
                        
                        if (varName.equals ("CVSROOT")) {
                            outValue = dp.getCvsRoot();
                        }
                        
                        if (varName.equals ("CVS_RSH")) {
                            outValue = dp.getCvsRsh();
                        }
                        
                        if (varName.equals ("CVS_ENV")) {
                            outValue = dp.getCvsEnv();
                        }
                        
                        if (varName.equals ("TFS_EDITOR")) {
                            outValue = dp.getTfsEditor();
                        }
                        
                        if (varName.equals ("TFS_ENV")) {
                            outValue = dp.getTfsEnv();
                        }
                        
                        if (varName.equals ("TFS_CHECKIN_OPTIONS")) {
                            outValue = dp.getTfsCheckinOptions();
                        }
                        
                        if (varName.equals ("TFS_SERVER_URL")) {
                            outValue = dp.getTfsServerUrl();
                        }
                        
                        // these variables do not change so overriding possible user changes
                        //
                        if (varName.equals ("MODULE_HOME") || varName.equals ("SUMMARY_LOG") || varName.equals ("SCHEMA_LOCATION") || varName.equals ("nonRebindableResourceSubTypeList")) {
                            log.debug ("Setting PDTool variable \"" + varName + "\" to default value.");

                            bw.write (line);
                        } else {
                            log.debug ("Setting PDTool variable \"" + varName + "\" to \"" + outValue + "\".");

                            bw.write (varName + "=" + outValue);
                        }

                        bw.newLine();
                    }
                    
                } else { // (line.startsWith("#") || line.matches("\\s+"))

                    if (line.matches ("^.*Customer-defined Environment Variables.*$"))
                        inCustomVarsSection = true;
                    
                    if (inCustomVarsSection && line.matches ("^#===================.*$")) {
                        numSeparators++;
                        
                        if (numSeparators == 2) {
                            inCustomVarsSection = false;
                        
                            // write out any new custom vars that haven't already been written.
                            //
                            for (Iterator<EnvironmentVariable> i = dp.getEnvVars().iterator(); i.hasNext(); ) {
                                EnvironmentVariable ev = i.next();
                                
                                if (writtenCustomVars.get (ev.getVariable()) == null) {
                                    log.debug ("Setting new custom variable \"" + ev.getVariable() + "\" to \"" + ev.getValue() + "\".");
                                    bw.write (ev.getVariable() + "=" + ev.getValue());
                                    bw.newLine();
                                }
                            }
                        }
                    }
                    
                    bw.write (line);
                    bw.newLine();
                }
                
            }
            
            bw.flush();
            
            // if user preferences indicate not to save backups of edited files, ditch the backup.
            //
            if (PreferencesManager.getInstance().getBackupFiles().equals ("false")) {
                if (fb.exists() && fb.canWrite())
                    FileUtils.deleteQuietly (fb);
            }

        } catch (Exception e) {
            return new ResultMessage ("error", "Error: " + e.getMessage(), null);
        } finally {
            try {
                if (bw != null) bw.close();
            } catch (Exception e2) {
                return new ResultMessage ("error", "Error closing temp file \"" + dp.getPath() + "_tmp\": " + e2.getMessage(), null);
            }

            try {
                if (br != null) br.close();
            } catch (Exception e2) {
                return new ResultMessage ("error", "Error closing properties file \"" + dp.getPath() + "\": " + e2.getMessage(), null);
            }

        }
        
        return new ResultMessage ("success", "Deployment profile updated.", null);
    }
}

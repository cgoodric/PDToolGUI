package com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.cisco.dvbu.ps.deploytool.gui.core.shared.EnvironmentVariable;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.file.FilesDAO;

import com.cisco.dvbu.ps.deploytool.gui.util.StringUtils;

import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Bean object for deployment profiles. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class DeploymentProfile {
    private static final Logger log = LoggerFactory.getLogger (DeploymentProfile.class);
    
    private String path;
    private boolean suppressComments;
    private boolean debug1;
    private boolean debug2;
    private boolean debug3;
    private boolean diffmergerVerbose;
    private String allResourcesIndicator;
    private String excludeResourcesIndicator;
    private int userOptionThreshold;
    private String dataSourceModuleNonUpdateableAttributes;
    private String serverAttributeModuleNonUpdateableAttributes;
    private String domainModuleNonUpdateableAttributes;
    private String vcsModuleExternalVcsResourceTypeList;
    private boolean cisPingServer;
    private int cisConnectRetry;
    private int cisConnectRetrySleepMillis;
    private boolean vcsMultiUserTopology;
    private String vcsType;
    private String vcsHome;
    private String vcsCommand;
    private boolean vcsExecFullPath;
    private String vcsOptions;
    private String vcsWorkspaceInitNewOptions;
    private String vcsWorkspaceInitLinkOptions;
    private String vcsWorkspaceInitGetOptions;
    private String vcsCheckinOptions;
    private String vcsCheckinOptionsRequired;
    private String vcsCheckoutOptions;
    private String vcsCheckoutOptionsRequired;
    private String vcsRepositoryUrl;
    private String vcsProjectRoot;
    private String vcsWorkspaceHome;
    private String vcsWorkspaceName;
    private String vcsWorkspaceDir;
    private String vcsTempDir;
    private String vcsUsername;
    private String vcsPassword;
    private String vcsIgnoreMessages;
    private String vcsMessagePrepend;
    private String svnEditor;
    private String svnEnv;
    private String p4Editor;
    private String p4Client;
    private String p4Port;
    private String p4User;
    private String p4Passwd;
    private String p4Env;
    private String p4DelLinkOptions;
    private String cvsRoot;
    private String cvsRsh;
    private String cvsEnv;
    private String tfsEditor;
    private String tfsEnv;
    private String tfsCheckinOptions;
    private String tfsServerUrl;
    private List<EnvironmentVariable> envVars;
    
    private static Pattern variableRE; // pattern for finding variable references in string values.
    static {
        try {
            variableRE = Pattern.compile ("\\$\\w+\\$|\\$\\w+|%\\w+%|%\\w+");
        } catch (Exception ignored) { ; }
    }

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public DeploymentProfile() {}


    /**
     * <p>
     * Returns the value of the specified profile property.
     * </p>
     * 
     * @param  prop               The profile property to look up, i.e. SUPPRESS_COMMENTS, DEBUG1, userOptionThreshold, etc.
     * @param  evaluateVariables  Indicates whether any variables used in the value string should be evaluated/expanded.
     * @return                    The value. An empty string if <code>prop</code> is null.
     */
    public String getProperty (
        String prop,
        boolean evaluateVariables
    ) {
        log.debug ("getProperty: looking up property \"" + prop + "\"");

        String value = null;

        if (prop == null || prop.length() == 0) return "";

        // see if there is an environment variable with this name.
        //
        log.debug ("getProperty(\"" + prop + "\"): System.getenv (prop) = \"" + System.getenv (prop) + "\"");
        value = System.getenv (prop);
        
        // see if a property was passed to the java engine (via -D) with this name
        //
        log.debug ("getProperty(\"" + prop + "\"): System.getProperty (prop) = \"" + System.getProperty (prop) + "\"");
        if (System.getProperty (prop) != null)
            value = System.getProperty (prop);
        
        // see if this is a variable in this deployment properties file
        //
        if (prop.equals ("PROJECT_HOME"))
            value = FilesDAO.getPdtHome();
        else if (prop.equals ("SUPPRESS_COMMENTS"))
            value = "" + this.suppressComments;
        else if (prop.equals ("DEBUG1"))
            value = "" + this.debug1;
        else if (prop.equals ("DEBUG2"))
            value = "" + this.debug2;
        else if (prop.equals ("DEBUG3"))
            value = "" + this.debug3;
        else if (prop.equals ("DIFFMERGER_VERBOSE"))
            value = "" + this.diffmergerVerbose;
        else if (prop.equals ("allResourcesIndicator"))
            value = this.allResourcesIndicator;
        else if (prop.equals ("exculdeResourcesIndiator") || prop.equals ("excludeResourcesIndicator"))
            value = this.excludeResourcesIndicator;
        else if (prop.equals ("userOptionThreshold"))
            value = "" + this.userOptionThreshold;
        else if (prop.equals ("DataSourceModule_NonUpdateableAttributes"))
            value = this.dataSourceModuleNonUpdateableAttributes;
        else if (prop.equals ("ServerAttributeModule_NonUpdateableAttributes"))
            value = this.serverAttributeModuleNonUpdateableAttributes;
        else if (prop.equals ("DomainModule_NonUpdateableAttributes"))
            value = this.domainModuleNonUpdateableAttributes;
        else if (prop.equals ("VCSModule_ExternalVcsResourceTypeList"))
            value = this.vcsModuleExternalVcsResourceTypeList;
        else if (prop.equals ("CIS_PING_SERVER"))
            value = "" + this.cisPingServer;
        else if (prop.equals ("CIS_CONNECT_RETRY"))
            value = "" + this.cisConnectRetry;
        else if (prop.equals ("CIS_CONNECT_RETRY_SLEEP_MILLIS"))
            value = "" + this.cisConnectRetrySleepMillis;
        else if (prop.equals ("VCS_MULTI_USER_TOPOLOGY"))
            value = "" + this.vcsMultiUserTopology;
        else if (prop.equals ("VCS_TYPE"))
            value = this.vcsType;
        else if (prop.equals ("VCS_HOME"))
            value = this.vcsHome;
        else if (prop.equals ("VCS_COMMAND"))
            value = this.vcsCommand;
        else if (prop.equals ("VCS_EXEC_FULL_PATH"))
            value = "" + this.vcsExecFullPath;
        else if (prop.equals ("VCS_OPTIONS"))
            value = this.vcsOptions;
        else if (prop.equals ("VCS_REPOSITORY_URL"))
            value = this.vcsRepositoryUrl;
        else if (prop.equals ("VCS_PROJECT_ROOT"))
            value = this.vcsProjectRoot;
        else if (prop.equals ("VCS_WORKSPACE_HOME"))
            value = this.vcsWorkspaceHome;
        else if (prop.equals ("VCS_WORKSPACE_NAME"))
            value = this.vcsWorkspaceName;
        else if (prop.equals ("VCS_WORKSPACE_DIR"))
            value = this.vcsWorkspaceDir;
        else if (prop.equals ("VCS_TEMP_DIR"))
            value = this.vcsTempDir;
        else if (prop.equals ("VCS_USERNAME"))
            value = this.vcsUsername;
        else if (prop.equals ("VCS_PASSWORD"))
            value = this.vcsPassword;
        else if (prop.equals ("VCS_IGNORE_MESSAGES"))
            value = this.vcsIgnoreMessages;
        else if (prop.equals ("VCS_MESSAGE_PREPEND"))
            value = this.vcsMessagePrepend;
        else if (prop.equals ("SVN_EDITOR"))
            value = this.svnEditor;
        else if (prop.equals ("SVN_ENV"))
            value = this.svnEnv;
        else if (prop.equals ("P4EDITOR"))
            value = this.p4Editor;
        else if (prop.equals ("P4CLIENT"))
            value = this.p4Client;
        else if (prop.equals ("P4PORT"))
            value = this.p4Port;
        else if (prop.equals ("P4USER"))
            value = this.p4User;
        else if (prop.equals ("P4PASSWD"))
            value = this.p4Passwd;
        else if (prop.equals ("P4_ENV"))
            value = this.p4Env;
        else if (prop.equals ("P4DEL_LINK_OPTIONS"))
            value = this.p4Env;
        else if (prop.equals ("CVSROOT"))
            value = this.cvsRoot;
        else if (prop.equals ("CVS_RSH"))
            value = this.cvsRsh;
        else if (prop.equals ("CVS_ENV"))
            value = this.cvsEnv;
        else if (prop.equals ("TFS_EDITOR"))
            value = this.tfsEditor;
        else if (prop.equals ("TFS_ENV"))
            value = this.tfsEnv;
        else if (prop.equals ("TFS_CHECKIN_OPTIONS"))
            value = this.tfsCheckinOptions;
        else if (prop.equals ("TFS_SERVER_URL"))
            value = this.tfsServerUrl;
        else if (prop.equals ("MODULE_HOME"))
            value = "$PROJECT_HOME/resources/modules";
        else if (prop.equals ("SUMMARY_LOG"))
            value = "$PROJECT_HOME/logs/summary.log";
        else if (prop.equals ("SCHEMA_LOCATION"))
            value = "$PROJECT_HOME/resources/schema/deployToolModules.xsd";
        else if (prop.equals ("nonRebindableResourceSubTypeList"))
            value = "DATABASE_TABLE";
        else if (this.envVars != null) {
            for (EnvironmentVariable ev : this.envVars) {
                if (ev.getVariable().equals (prop)) {
                    value = ev.getValue();
                    break;
                }
            }
        }

        log.debug ("getProperty(\"" + prop + "\"): value = \"" + value + "\"");

        // do we need to evaluate any variables embedded in the value text?
        //
        if (evaluateVariables) {
            log.debug ("getProperty: evaluating value");
            log.debug ("getProperty(\"" + prop + "\"): evaluating value");
            value = evaluateVariables (value);
        }
        
        return value;
    }
    
    public String evaluateVariables (String value) {
        log.debug ("evaluateVariables(\"" + value + "\"): evaluating variables in \"" + value + "\"");
        
        if (value == null) return null;

        StringBuffer sb = new StringBuffer();
        Matcher m = variableRE.matcher (value);
        
        // for each variable reference found in the value, recursively call getProperty() until all variable references are resolved.
        //
        while (m.find()) {
            log.debug ("evaluateVariables(\"" + value + "\"): located variable \"" + m.group() + "\"");
            
            // strip off the variable indicators (leading and/or trailing '$' and '%' characters) from the variable
            //
            String subProp = m.group().replaceFirst ("^\\$([^$]+)\\$$", "$1").replaceFirst ("^\\$", "").replaceFirst ("^%([^%]+)%$", "$1").replaceFirst ("^%", "");
            log.debug ("evaluateVariables(\"" + value + "\"): variable with variable indicators stripped =  \"" + subProp + "\"");

            // append any unmatched text ahead of the variable reference in the value string, then append the text that replaces the variable.
            //
            String subPropValue = getProperty (subProp, true);
            m.appendReplacement (sb, (subPropValue != null) ? subPropValue : ""); 
            log.debug ("evaluateVariables(\"" + value + "\"): sb = \"" + sb + "\"");
        }
        
        // append any remaining unmatched text
        //
        m.appendTail (sb);
        
        log.debug ("evaluateVariables(\"" + value + "\"): value = \"" + sb + "\"");
        return sb.toString();
    }

    /**
     * <p>
     * Sets the <code>path</code> field.
     * </p>
     * 
     * @param  path  The path to the deployment profile file.
     */
    public void setPath (String path) {
        this.path = path;
    }

    /**
     * <p>
     * Returns the value of the <code>path</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getPath () {
        return path;
    }

    /**
     * <p>
     * Sets the <code>suppressComments</code> field.
     * </p>
     * 
     * @param  suppressComments  Suppress [true|false] printing PDTool orchestration comments to the log.
     */
    public void setSuppressComments (boolean suppressComments) {
        this.suppressComments = suppressComments;
    }

    /**
     * <p>
     * Returns the value of the <code>suppressComments</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isSuppressComments () {
        return suppressComments;
    }

    /**
     * <p>
     * Sets the <code>debug1</code> field.
     * </p>
     * 
     * @param  debug1  Debug Level 1: Debug PDTool script only.
     */
    public void setDebug1 (boolean debug1) {
        this.debug1 = debug1;
    }

    /**
     * <p>
     * Returns the value of the <code>debug1</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isDebug1 () {
        return debug1;
    }

    /**
     * <p>
     * Sets the <code>debug2</code> field.
     * </p>
     * 
     * @param  debug2  Debug Level 2: Debug ExecuteAction, executeVcs
     */
    public void setDebug2 (boolean debug2) {
        this.debug2 = debug2;
    }

    /**
     * <p>
     * Returns the value of the <code>debug2</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isDebug2 () {
        return debug2;
    }

    /**
     * <p>
     * Sets the <code>debug3</code> field.
     * </p>
     * 
     * @param  debug3  Debug Level 3: Debug 3rd level scripts invoked from ExecuteAction and executeVcs.
     */
    public void setDebug3 (boolean debug3) {
        this.debug3 = debug3;
    }

    /**
     * <p>
     * Returns the value of the <code>debug3</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isDebug3 () {
        return debug3;
    }

    /**
     * <p>
     * Sets the <code>diffmergerVerbose</code> field.
     * </p>
     * 
     * @param  diffmergerVerbose  Diffmerger Verbose allows the VCS Diffmerger process to output more information when set to true [Default=true]
     */
    public void setDiffmergerVerbose (boolean diffmergerVerbose) {
        this.diffmergerVerbose = diffmergerVerbose;
    }

    /**
     * <p>
     * Returns the value of the <code>diffmergerVerbose</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isDiffmergerVerbose () {
        return diffmergerVerbose;
    }

    /**
     * <p>
     * Sets the <code>allResourcesIndicator</code> field.
     * </p>
     * 
     * @param  allResourcesIndicator  Used when parsing property file and processing the moduleId list.
     */
    public void setAllResourcesIndicator (String allResourcesIndicator) {
        this.allResourcesIndicator = allResourcesIndicator;
    }

    /**
     * <p>
     * Returns the value of the <code>allResourcesIndicator</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getAllResourcesIndicator () {
        return allResourcesIndicator;
    }

    /**
     * <p>
     * Sets the <code>excludeResourcesIndicator</code> field.
     * </p>
     * 
     * @param  excludeResourcesIndicator  Used when parsing property file and processing the moduleId list.
     */
    public void setExcludeResourcesIndicator (String excludeResourcesIndicator) {
        this.excludeResourcesIndicator = excludeResourcesIndicator;
    }

    /**
     * <p>
     * Returns the value of the <code>excludeResourcesIndicator</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getExcludeResourcesIndicator () {
        return excludeResourcesIndicator;
    }

    /**
     * <p>
     * Sets the <code>userOptionThreshold</code> field.
     * </p>
     * 
     * @param  userOptionThreshold  The threshold of the number of users where it is more efficient to retrieve all users at once.
     */
    public void setUserOptionThreshold (int userOptionThreshold) {
        this.userOptionThreshold = userOptionThreshold;
    }

    /**
     * <p>
     * Returns the value of the <code>userOptionThreshold</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getUserOptionThreshold () {
        return userOptionThreshold;
    }

    /**
     * <p>
     * Sets the <code>dataSourceModuleNonUpdateableAttributes</code> field.
     * </p>
     * 
     * @param  dataSourceModuleNonUpdateableAttributes  Listing of DataSourceModule generic attributes that are not updateable.
     */
    public void setDataSourceModuleNonUpdateableAttributes (String dataSourceModuleNonUpdateableAttributes) {
        this.dataSourceModuleNonUpdateableAttributes = dataSourceModuleNonUpdateableAttributes;
    }

    /**
     * <p>
     * Returns the value of the <code>dataSourceModuleNonUpdateableAttributes</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getDataSourceModuleNonUpdateableAttributes () {
        return dataSourceModuleNonUpdateableAttributes;
    }

    /**
     * <p>
     * Sets the <code>serverAttributeModuleNonUpdateableAttributes</code> field.
     * </p>
     * 
     * @param  serverAttributeModuleNonUpdateableAttributes  Listing of ServerAttributeModul generic attributes that are not updateable.
     */
    public void setServerAttributeModuleNonUpdateableAttributes (String serverAttributeModuleNonUpdateableAttributes) {
        this.serverAttributeModuleNonUpdateableAttributes = serverAttributeModuleNonUpdateableAttributes;
    }

    /**
     * <p>
     * Returns the value of the <code>serverAttributeModuleNonUpdateableAttributes</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getServerAttributeModuleNonUpdateableAttributes () {
        return serverAttributeModuleNonUpdateableAttributes;
    }

    /**
     * <p>
     * Sets the <code>domainModuleNonUpdateableAttributes</code> field.
     * </p>
     * 
     * @param  domainModuleNonUpdateableAttributes  Listing of DomainModule generic attributes that are not updateable.
     */
    public void setDomainModuleNonUpdateableAttributes (String domainModuleNonUpdateableAttributes) {
        this.domainModuleNonUpdateableAttributes = domainModuleNonUpdateableAttributes;
    }

    /**
     * <p>
     * Returns the value of the <code>domainModuleNonUpdateableAttributes</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getDomainModuleNonUpdateableAttributes () {
        return domainModuleNonUpdateableAttributes;
    }

    /**
     * <p>
     * Sets the <code>vcsModuleExternalVcsResourceTypeList</code> field.
     * </p>
     * 
     * @param  vcsModuleExternalVcsResourceTypeList  This provides an externalized mechanism to teach PD Tool about new Resource Types and how 
     *                                               they are associated with the basic VCS Resource Types.
     */
    public void setVcsModuleExternalVcsResourceTypeList (String vcsModuleExternalVcsResourceTypeList) {
        this.vcsModuleExternalVcsResourceTypeList = vcsModuleExternalVcsResourceTypeList;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsModuleExternalVcsResourceTypeList</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsModuleExternalVcsResourceTypeList () {
        return vcsModuleExternalVcsResourceTypeList;
    }

    /**
     * <p>
     * Sets the <code>cisPingServer</code> field.
     * </p>
     * 
     * @param  cisPingServer  Allows the user to control whether the server performs a ping on CIS prior to executing the actual command.
     */
    public void setCisPingServer (boolean cisPingServer) {
        this.cisPingServer = cisPingServer;
    }

    /**
     * <p>
     * Returns the value of the <code>cisPingServer</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isCisPingServer () {
        return cisPingServer;
    }

    /**
     * <p>
     * Sets the <code>cisConnectRetry</code> field.
     * </p>
     * 
     * @param  cisConnectRetry  Allows the user to set the number of retries to connect to CIS before throwing an error.
     */
    public void setCisConnectRetry (int cisConnectRetry) {
        this.cisConnectRetry = cisConnectRetry;
    }

    /**
     * <p>
     * Returns the value of the <code>cisConnectRetry</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getCisConnectRetry () {
        return cisConnectRetry;
    }

    /**
     * <p>
     * Sets the <code>cisConnectRetrySleepMillis</code> field.
     * </p>
     * 
     * @param  cisConnectRetrySleepMillis  Number of milliseconds to sleep on connection retry.
     */
    public void setCisConnectRetrySleepMillis (int cisConnectRetrySleepMillis) {
        this.cisConnectRetrySleepMillis = cisConnectRetrySleepMillis;
    }

    /**
     * <p>
     * Returns the value of the <code>cisConnectRetrySleepMillis</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getCisConnectRetrySleepMillis () {
        return cisConnectRetrySleepMillis;
    }

    /**
     * <p>
     * Sets the <code>vcsMultiUserTopology</code> field.
     * </p>
     * 
     * @param  vcsMultiUserTopology  Whether the VCS Multi-User [Direct VCS Access] Topology is being used (true) or not (false).
     */
    public void setVcsMultiUserTopology (boolean vcsMultiUserTopology) {
        this.vcsMultiUserTopology = vcsMultiUserTopology;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsMultiUserTopology</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isVcsMultiUserTopology () {
        return vcsMultiUserTopology;
    }

    /**
     * <p>
     * Sets the <code>vcsType</code> field.
     * </p>
     * 
     * @param  vcsType  The type of VCS being used [svn, p4, cvs, tfs2005, tfs2010, etc].
     */
    public void setVcsType (String vcsType) {
        this.vcsType = vcsType;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsType</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsType () {
        return vcsType;
    }

    /**
     * <p>
     * Sets the <code>vcsHome</code> field.
     * </p>
     * 
     * @param  vcsHome  VCS Client Home directory where the VCS executable lives.
     */
    public void setVcsHome (String vcsHome) {
        this.vcsHome = vcsHome;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsHome</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsHome () {
        return vcsHome;
    }

    /**
     * <p>
     * Sets the <code>vcsCommand</code> field.
     * </p>
     * 
     * @param  vcsCommand  The actual command for the given VCS Type [svn,p4,cvs].
     */
    public void setVcsCommand (String vcsCommand) {
        this.vcsCommand = vcsCommand;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsCommand</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsCommand () {
        return vcsCommand;
    }

    /**
     * <p>
     * Sets the <code>vcsExecFullPath</code> field.
     * </p>
     * 
     * @param  vcsExecFullPath  Execute the VCS command with the full path (true) or the VCS command only (false).
     */
    public void setVcsExecFullPath (boolean vcsExecFullPath) {
        this.vcsExecFullPath = vcsExecFullPath;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsExecFullPath</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isVcsExecFullPath () {
        return vcsExecFullPath;
    }

    /**
     * <p>
     * Sets the <code>vcsOptions</code> field.
     * </p>
     * 
     * @param  vcsOptions  Options that are specific to the VCS type being used and are included in the command line.
     */
    public void setVcsOptions (String vcsOptions) {
        this.vcsOptions = vcsOptions;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsOptions</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsOptions () {
        return vcsOptions;
    }

    /**
     * <p>
     * Sets the <code>vcsWorkspaceInitNewOptions</code> field.
     * </p>
     * 
     * @param  vcsWorkspaceInitNewOptions  Options for initializing a new workspace.
     */
    public void setVcsWorkspaceInitNewOptions (String vcsWorkspaceInitNewOptions) {
        this.vcsWorkspaceInitNewOptions = vcsWorkspaceInitNewOptions;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsWorkspaceInitNewOptions</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsWorkspaceInitNewOptions () {
        return vcsWorkspaceInitNewOptions;
    }

    /**
     * <p>
     * Sets the <code>vcsWorkspaceInitLinkOptions</code> field.
     * </p>
     * 
     * @param  vcsWorkspaceInitLinkOptions  Options for linking to a newly initialized workspace.
     */
    public void setVcsWorkspaceInitLinkOptions (String vcsWorkspaceInitLinkOptions) {
        this.vcsWorkspaceInitLinkOptions = vcsWorkspaceInitLinkOptions;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsWorkspaceInitLinkOptions</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsWorkspaceInitLinkOptions () {
        return vcsWorkspaceInitLinkOptions;
    }

    /**
     * <p>
     * Sets the <code>vcsWorkspaceInitGetOptions</code> field.
     * </p>
     * 
     * @param  vcsWorkspaceInitGetOptions  Options for retrieving objects from the newly initialized workspace.
     */
    public void setVcsWorkspaceInitGetOptions (String vcsWorkspaceInitGetOptions) {
        this.vcsWorkspaceInitGetOptions = vcsWorkspaceInitGetOptions;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsWorkspaceInitGetOptions</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsWorkspaceInitGetOptions () {
        return vcsWorkspaceInitGetOptions;
    }

    /**
     * <p>
     * Sets the <code>vcsCheckinOptions</code> field.
     * </p>
     * 
     * @param  vcsCheckinOptions  Options to use when checking in resources.
     */
    public void setVcsCheckinOptions (String vcsCheckinOptions) {
        this.vcsCheckinOptions = vcsCheckinOptions;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsCheckinOptions</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsCheckinOptions () {
        return vcsCheckinOptions;
    }

    /**
     * <p>
     * Sets the <code>vcsCheckinOptionsRequired</code> field.
     * </p>
     * 
     * @param  vcsCheckinOptionsRequired  Options that are required for checking in resources. <code>vcsCheckinOptions</code> is validated against this.
     */
    public void setVcsCheckinOptionsRequired (String vcsCheckinOptionsRequired) {
        this.vcsCheckinOptionsRequired = vcsCheckinOptionsRequired;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsCheckinOptionsRequired</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsCheckinOptionsRequired () {
        return vcsCheckinOptionsRequired;
    }

    /**
     * <p>
     * Sets the <code>vcsCheckoutOptions</code> field.
     * </p>
     * 
     * @param  vcsCheckoutOptions  Options to use when checking out resources.
     */
    public void setVcsCheckoutOptions (String vcsCheckoutOptions) {
        this.vcsCheckoutOptions = vcsCheckoutOptions;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsCheckoutOptions</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsCheckoutOptions () {
        return vcsCheckoutOptions;
    }

    /**
     * <p>
     * Sets the <code>vcsCheckoutOptionsRequired</code> field.
     * </p>
     * 
     * @param  vcsCheckoutOptionsRequired  Options that are required for checking out resources. <code>vcsCheckoutOptions</code> is validated against this.
     */
    public void setVcsCheckoutOptionsRequired (String vcsCheckoutOptionsRequired) {
        this.vcsCheckoutOptionsRequired = vcsCheckoutOptionsRequired;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsCheckoutOptionsRequired</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsCheckoutOptionsRequired () {
        return vcsCheckoutOptionsRequired;
    }

    /**
     * <p>
     * Sets the <code>vcsRepositoryUrl</code> field.
     * </p>
     * 
     * @param  vcsRepositoryUrl  This is the base URL to identify the VCS server.
     */
    public void setVcsRepositoryUrl (String vcsRepositoryUrl) {
        this.vcsRepositoryUrl = vcsRepositoryUrl;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsRepositoryUrl</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsRepositoryUrl () {
        return vcsRepositoryUrl;
    }

    /**
     * <p>
     * Sets the <code>vcsProjectRoot</code> field.
     * </p>
     * 
     * @param  vcsProjectRoot  This is root name of the project on the VCS Server.
     */
    public void setVcsProjectRoot (String vcsProjectRoot) {
        this.vcsProjectRoot = vcsProjectRoot;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsProjectRoot</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsProjectRoot () {
        return vcsProjectRoot;
    }

    /**
     * <p>
     * Sets the <code>vcsWorkspaceHome</code> field.
     * </p>
     * 
     * @param  vcsWorkspaceHome  This is the CIS VCS Workspace Home.
     */
    public void setVcsWorkspaceHome (String vcsWorkspaceHome) {
        this.vcsWorkspaceHome = vcsWorkspaceHome;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsWorkspaceHome</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsWorkspaceHome () {
        return vcsWorkspaceHome;
    }

    /**
     * <p>
     * Sets the <code>vcsWorkspaceName</code> field.
     * </p>
     * 
     * @param  vcsWorkspaceName  The name of the workspace folder.
     */
    public void setVcsWorkspaceName (String vcsWorkspaceName) {
        this.vcsWorkspaceName = vcsWorkspaceName;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsWorkspaceName</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsWorkspaceName () {
        return vcsWorkspaceName;
    }

    /**
     * <p>
     * Sets the <code>vcsWorkspaceDir</code> field.
     * </p>
     * 
     * @param  vcsWorkspaceDir  VCS Workspace Dir is a combination of the VCS_WORKSPACE_HOME and a workspace directory name "VCS_WORKSPACE_NAME".
     */
    public void setVcsWorkspaceDir (String vcsWorkspaceDir) {
        this.vcsWorkspaceDir = vcsWorkspaceDir;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsWorkspaceDir</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsWorkspaceDir () {
        return vcsWorkspaceDir;
    }

    /**
     * <p>
     * Sets the <code>vcsTempDir</code> field.
     * </p>
     * 
     * @param  vcsTempDir  VCS Temp Dir is a combination of the VCS_WORKSPACE_HOME and a temp dir name such as $VCS_TYPE$_temp.
     */
    public void setVcsTempDir (String vcsTempDir) {
        this.vcsTempDir = vcsTempDir;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsTempDir</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsTempDir () {
        return vcsTempDir;
    }

    /**
     * <p>
     * Sets the <code>vcsUsername</code> field.
     * </p>
     * 
     * @param  vcsUsername  This is the username for the user logging into the VCS Server.
     */
    public void setVcsUsername (String vcsUsername) {
        this.vcsUsername = vcsUsername;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsUsername</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsUsername () {
        return vcsUsername;
    }

    /**
     * <p>
     * Sets the <code>vcsPassword</code> field.
     * </p>
     * 
     * @param  vcsPassword  This is the password for the user logging into the VCS Server.
     */
    public void setVcsPassword (String vcsPassword) {
        if (vcsPassword != null && vcsPassword.length() > 0 && ! vcsPassword.equals("<VCS_PASSWORD>"))
            vcsPassword = StringUtils.encryptPassword (vcsPassword);

        this.vcsPassword = vcsPassword;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsPassword</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsPassword () {
        return vcsPassword;
    }

    /**
     * <p>
     * Sets the <code>vcsIgnoreMessages</code> field.
     * </p>
     * 
     * @param  vcsIgnoreMessages  A comma separated list of messages for the VCS Module to ignore upon execution.
     */
    public void setVcsIgnoreMessages (String vcsIgnoreMessages) {
        this.vcsIgnoreMessages = vcsIgnoreMessages;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsIgnoreMessages</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsIgnoreMessages () {
        return vcsIgnoreMessages;
    }

    /**
     * <p>
     * Sets the <code>vcsMessagePrepend</code> field.
     * </p>
     * 
     * @param  vcsMessagePrepend  A static message that gets prepended onto all check-in or forced check-in messages.
     */
    public void setVcsMessagePrepend (String vcsMessagePrepend) {
        this.vcsMessagePrepend = vcsMessagePrepend;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsMessagePrepend</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsMessagePrepend () {
        return vcsMessagePrepend;
    }

    /**
     * <p>
     * Sets the <code>svnEditor</code> field.
     * </p>
     * 
     * @param  svnEditor  Subversion editor for messages.
     */
    public void setSvnEditor (String svnEditor) {
        this.svnEditor = svnEditor;
    }

    /**
     * <p>
     * Returns the value of the <code>svnEditor</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getSvnEditor () {
        return svnEditor;
    }

    /**
     * <p>
     * Sets the <code>svnEnv</code> field.
     * </p>
     * 
     * @param  svnEnv  Tells the system which SVN environment variables need to be set at execution time.
     */
    public void setSvnEnv (String svnEnv) {
        this.svnEnv = svnEnv;
    }

    /**
     * <p>
     * Returns the value of the <code>svnEnv</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getSvnEnv () {
        return svnEnv;
    }

    /**
     * <p>
     * Sets the <code>p4Editor</code> field.
     * </p>
     * 
     * @param  p4Editor  Perforce editor for messages.
     */
    public void setP4Editor (String p4Editor) {
        this.p4Editor = p4Editor;
    }

    /**
     * <p>
     * Returns the value of the <code>p4Editor</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getP4Editor () {
        return p4Editor;
    }

    /**
     * <p>
     * Sets the <code>p4Client</code> field.
     * </p>
     * 
     * @param  p4Client  P4CLIENT must contain "exactly" the same folder name that is defined at the end of VCS_WORKSPACE_DIR which is also VCS_WORKSPACE_NAME.
     */
    public void setP4Client (String p4Client) {
        this.p4Client = p4Client;
    }

    /**
     * <p>
     * Returns the value of the <code>p4Client</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getP4Client () {
        return p4Client;
    }

    /**
     * <p>
     * Sets the <code>p4Port</code> field.
     * </p>
     * 
     * @param  p4Port  The Perforce "port".
     */
    public void setP4Port (String p4Port) {
        this.p4Port = p4Port;
    }

    /**
     * <p>
     * Returns the value of the <code>p4Port</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getP4Port () {
        return p4Port;
    }

    /**
     * <p>
     * Sets the <code>p4User</code> field.
     * </p>
     * 
     * @param  p4User  The Perforce user.
     */
    public void setP4User (String p4User) {
        this.p4User = p4User;
    }

    /**
     * <p>
     * Returns the value of the <code>p4User</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getP4User () {
        return p4User;
    }

    /**
     * <p>
     * Sets the <code>p4Passwd</code> field.
     * </p>
     * 
     * @param  p4Passwd  The Perforce user's password.
     */
    public void setP4Passwd (String p4Passwd) {
        this.p4Passwd = StringUtils.encryptPassword (p4Passwd);
    }

    /**
     * <p>
     * Returns the value of the <code>p4Passwd</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getP4Passwd () {
        return p4Passwd;
    }

    /**
     * <p>
     * Sets the <code>p4Env</code> field.
     * </p>
     * 
     * @param  p4Env  Tells the system which P4 environment variables need to be set at execution time.
     */
    public void setP4Env (String p4Env) {
        this.p4Env = p4Env;
    }

    /**
     * <p>
     * Returns the value of the <code>p4Env</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getP4Env () {
        return p4Env;
    }

    /**
     * <p>
     * Sets the <code>p4DelLinkOptions</code> field.
     * </p>
     * 
     * @param  p4DelLinkOptions  Space separated list of options to pass into the command to delete the workspace link between the file system and Perforce Depot repository.
     */
    public void setP4DelLinkOptions (String p4DelLinkOptions) {
        this.p4DelLinkOptions = p4DelLinkOptions;
    }

    /**
     * <p>
     * Returns the value of the <code>p4DelLinkOptions</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getP4DelLinkOptions () {
        return p4DelLinkOptions;
    }

    /**
     * <p>
     * Sets the <code>cvsRoot</code> field.
     * </p>
     * 
     * @param  cvsRoot  The CVS repository URL.
     */
    public void setCvsRoot (String cvsRoot) {
        this.cvsRoot = cvsRoot;
    }

    /**
     * <p>
     * Returns the value of the <code>cvsRoot</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getCvsRoot () {
        return cvsRoot;
    }

    /**
     * <p>
     * Sets the <code>cvsRsh</code> field.
     * </p>
     * 
     * @param  cvsRsh  Sets the remote shell login when logging into a remote host.
     */
    public void setCvsRsh (String cvsRsh) {
        this.cvsRsh = cvsRsh;
    }

    /**
     * <p>
     * Returns the value of the <code>cvsRsh</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getCvsRsh () {
        return cvsRsh;
    }

    /**
     * <p>
     * Sets the <code>cvsEnv</code> field.
     * </p>
     * 
     * @param  cvsEnv  Tells the system which CVS environment variables need to be set at execution time.
     */
    public void setCvsEnv (String cvsEnv) {
        this.cvsEnv = cvsEnv;
    }

    /**
     * <p>
     * Returns the value of the <code>cvsEnv</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getCvsEnv () {
        return cvsEnv;
    }

    /**
     * <p>
     * Sets the <code>tfsEditor</code> field.
     * </p>
     * 
     * @param  tfsEditor  TFS editor for messages
     */
    public void setTfsEditor (String tfsEditor) {
        this.tfsEditor = tfsEditor;
    }

    /**
     * <p>
     * Returns the value of the <code>tfsEditor</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getTfsEditor () {
        return tfsEditor;
    }

    /**
     * <p>
     * Sets the <code>tfsEnv</code> field.
     * </p>
     * 
     * @param  tfsEnv  Tells PD Tool which TFS environment variables need to be set at execution time.
     */
    public void setTfsEnv (String tfsEnv) {
        this.tfsEnv = tfsEnv;
    }

    /**
     * <p>
     * Returns the value of the <code>tfsEnv</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getTfsEnv () {
        return tfsEnv;
    }

    /**
     * <p>
     * Sets the <code>tfsCheckinOptions</code> field.
     * </p>
     * 
     * @param  tfsCheckinOptions  Any additional options that should be passed for checkin only.
     */
    public void setTfsCheckinOptions (String tfsCheckinOptions) {
        this.tfsCheckinOptions = tfsCheckinOptions;
    }

    /**
     * <p>
     * Returns the value of the <code>tfsCheckinOptions</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getTfsCheckinOptions () {
        return tfsCheckinOptions;
    }

    /**
     * <p>
     * Sets the <code>tfsServerUrl</code> field.
     * </p>
     * 
     * @param  tfsServerUrl  TFS Server URL.
     */
    public void setTfsServerUrl (String tfsServerUrl) {
        this.tfsServerUrl = tfsServerUrl;
    }

    /**
     * <p>
     * Returns the value of the <code>tfsServerUrl</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getTfsServerUrl () {
        return tfsServerUrl;
    }

    /**
     * <p>
     * Sets the <code>envVars</code> field.
     * </p>
     * 
     * @param  envVars  The status of the request. Typically "success" or "error".
     */
    public void setEnvVars (List<EnvironmentVariable> envVars) {
        this.envVars = envVars;
    }

    /**
     * <p>
     * Returns the value of the <code>envVars</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<EnvironmentVariable> getEnvVars () {
        return envVars;
    }
}

package com.cisco.dvbu.ps.deploytool.gui.core.module.vcs;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.cisco.dvbu.ps.deploytool.gui.core.shared.EnvironmentVariable;
import com.cisco.dvbu.ps.deploytool.gui.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Bean object for VCS module VCS records. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class VCS {
    public static final int TYPE_CONNECTION = 0;
    public static final int TYPE_RESOURCE = 1;
    
    public static final int VCS_TYPE_NULL = 0;
    public static final int VCS_TYPE_SVN = 1;
    public static final int VCS_TYPE_P4 = 2;
    public static final int VCS_TYPE_CVS = 3;
    public static final int VCS_TYPE_TFS2005 = 4;
    public static final int VCS_TYPE_TFS2010 = 5;
    public static final int VCS_TYPE_TFS2012 = 6;
    public static final int VCS_TYPE_TFS2013 = 7;
    public static final String[] VCS_TYPE_LABELS = {
        "",
        "svn",
        "p4",
        "cvs",
        "tfs2005",
        "tfs2010",
        "tfs2012",
        "tfs2013"
    };
    public static final int[] VCS_TYPE_BASES = {
        0,
        1,
        2,
        3,
        4,
        4,
        4,
        4
    };

    public static final int VCS_BASE_TYPE_NULL = 0;
    public static final int VCS_BASE_TYPE_SVN = 1;
    public static final int VCS_BASE_TYPE_P4 = 2;
    public static final int VCS_BASE_TYPE_CVS = 3;
    public static final int VCS_BASE_TYPE_TFS = 4;
    public static final String[] VCS_BASE_TYPE_LABELS = {
        "",
        "SVN",
        "P4",
        "CVS",
        "TFS"
    };

    private static final Logger log = LoggerFactory.getLogger (VCS.class);

    // attributes used by the UI
    //
    String operation;
    String origId;
    
    // module specific attributes
    //
    String id;
    int type = -1;
    
    // connection specific attributes
    //
    int vcsType = 0;
    int vcsBaseType = 0;
    String vcsHome;
    String vcsCommand;
    boolean vcsExecFullPath = false;
    String vcsOptions;
    String vcsWorkspaceInitNewOptions;
    String vcsWorkspaceInitLinkOptions;
    String vcsWorkspaceInitGetOptions;
    String vcsBaseFolderInitAdd;
    String vcsCheckinOptions;
    String vcsCheckinOptionsRequired;
    String vcsCheckoutOptions;
    String vcsCheckoutOptionsRequired;
    String vcsCisImportOptions;
    String vcsCisExportOptions;
    String vcsRepositoryUrl;
    String vcsProjectRoot;
    String vcsWorkspaceHome;
    String vcsWorkspaceName;
    String vcsWorkspaceDir;
    String vcsTempDir;
    String vcsUsername;
    String vcsPassword;
    String vcsIgnoreMessages;
    String vcsMessagePrepend;
    String svnEditor;
    String svnEnv;
    String p4Editor;
    String p4Client;
    String p4Port;
    String p4User;
    String p4Passwd;
    String p4Env;
    String p4DelLinkOptions;
    String cvsRoot;
    String cvsRsh;
    String cvsEnv;
    String tfsEditor;
    String tfsEnv;
    String tfsCheckinOptions;
    String tfsServerUrl;
    private List<EnvironmentVariable> envVars;
    
    // resource specific attributes
    //
    String resourcePath;
    String resourceType;
    String vcsLabel;
    String revision;
    String message;

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public VCS () {
        super ();
    }

    /**
     * <p>
     * Copy constructor. Used for making/serializing copies, where only the ID is updated.
     * </p>
     * <p>
     * WARNING!!! - This makes "shallow" copies of the List objects (meaning that changing List elements
     * in the copy will update List elements in the original.)
     * </p>
     */
    public VCS (VCS v) {
        if (v != null) {
            this.operation = v.getOperation();
            this.origId = v.getOrigId();
            this.id = v.getId();
            this.type = v.getType();
            this.vcsType = v.getVcsType();
            this.vcsBaseType = v.getVcsBaseType();
            this.vcsHome = v.getVcsHome();
            this.vcsCommand = v.getVcsCommand();
            this.vcsExecFullPath = v.isVcsExecFullPath();
            this.vcsOptions = v.getVcsOptions();
            this.vcsWorkspaceInitNewOptions = v.getVcsWorkspaceInitNewOptions();
            this.vcsWorkspaceInitLinkOptions = v.getVcsWorkspaceInitLinkOptions();
            this.vcsWorkspaceInitGetOptions = v.getVcsWorkspaceInitGetOptions();
            this.vcsBaseFolderInitAdd = v.getVcsBaseFolderInitAdd();
            this.vcsCheckinOptions = v.getVcsCheckinOptions();
            this.vcsCheckinOptionsRequired = v.getVcsCheckinOptionsRequired();
            this.vcsCheckoutOptions = v.getVcsCheckoutOptions();
            this.vcsCheckoutOptionsRequired = v.getVcsCheckoutOptionsRequired();
            this.vcsCisImportOptions = v.getVcsCisImportOptions();
            this.vcsCisExportOptions = v.getVcsCisExportOptions();
            this.vcsRepositoryUrl = v.getVcsRepositoryUrl();
            this.vcsProjectRoot = v.getVcsProjectRoot();
            this.vcsWorkspaceHome = v.getVcsWorkspaceHome();
            this.vcsWorkspaceName = v.getVcsWorkspaceName();
            this.vcsWorkspaceDir = v.getVcsWorkspaceDir();
            this.vcsTempDir = v.getVcsTempDir();
            this.vcsUsername = v.getVcsUsername();
            this.vcsPassword = v.getVcsPassword();
            this.vcsIgnoreMessages = v.getVcsIgnoreMessages();
            this.vcsMessagePrepend = v.getVcsMessagePrepend();
            this.svnEditor = v.getSvnEditor();
            this.svnEnv = v.getSvnEnv();
            this.p4Editor = v.getP4Editor();
            this.p4Client = v.getP4Client();
            this.p4Port = v.getP4Port();
            this.p4User = v.getP4User();
            this.p4Passwd = v.getP4Passwd();
            this.p4Env = v.getP4Env();
            this.p4DelLinkOptions = v.getP4DelLinkOptions();
            this.cvsRoot = v.getCvsRoot();
            this.cvsRsh = v.getCvsRsh();
            this.cvsEnv = v.getCvsEnv();
            this.tfsEditor = v.getTfsEditor();
            this.tfsEnv = v.getTfsEnv();
            this.tfsCheckinOptions = v.getTfsCheckinOptions();
            this.tfsServerUrl = v.getTfsServerUrl();
            this.envVars = (v.getEnvVars() != null) ? new ArrayList<EnvironmentVariable> (v.getEnvVars()) : null;
        }
    }

    /**
     * <p>
     * Constructor. Extracts attribute values from {@link Element} object.
     * </p>
     */
    public VCS (Element vNode) {
        int i = 0;
        
        if (vNode.getName().equals("vcsConnection"))
            this.type = TYPE_CONNECTION;
        else
            this.type = TYPE_RESOURCE;

        for (Element vChild : vNode.getChildren()) {

            log.debug ("Located child #" + i++ + ": " + vChild.getName() + "{" + vChild.getNamespace().getURI() + "}");
            
            if (vChild.getName().equals ("id")) {
                log.debug ("Located group ID \"" + vChild.getText() + "\".");
                this.id = vChild.getText();
            }

            if (vChild.getName().equals ("VCS_TYPE")) {
                for (int t = 1; t < VCS_TYPE_LABELS.length; t++) {
                    if (vChild.getText().equalsIgnoreCase (VCS_TYPE_LABELS[t]))
                        this.vcsType = t;
                }
                
                this.vcsBaseType = VCS_TYPE_BASES[this.vcsType]; // earlier versions of PDTool do not have this attribute.
            }

            if (vChild.getName().equals ("VCS_BASE_TYPE")) {
                for (int t = 1; t < VCS_BASE_TYPE_LABELS.length; t++) {
                    if (vChild.getText().equalsIgnoreCase (VCS_BASE_TYPE_LABELS[t]))
                        this.vcsBaseType = t;
                }
            }

            if (vChild.getName().equals ("VCS_HOME"))
                this.vcsHome = vChild.getText();

            if (vChild.getName().equals ("VCS_COMMAND"))
                this.vcsCommand = vChild.getText();

            if (vChild.getName().equals ("VCS_EXEC_FULL_PATH"))
                this.vcsExecFullPath = (vChild.getText() == null) ? false : vChild.getText().matches ("(?i)^(yes|true|on|1)$");

            if (vChild.getName().equals ("VCS_OPTIONS"))
                this.vcsOptions = vChild.getText();

            if (vChild.getName().equals ("VCS_WORKSPACE_INIT_NEW_OPTIONS"))
                this.vcsWorkspaceInitNewOptions = vChild.getText();

            if (vChild.getName().equals ("VCS_WORKSPACE_INIT_LINK_OPTIONS"))
                this.vcsWorkspaceInitLinkOptions = vChild.getText();

            if (vChild.getName().equals ("VCS_WORKSPACE_INIT_GET_OPTIONS"))
                this.vcsWorkspaceInitGetOptions = vChild.getText();

            if (vChild.getName().equals ("VCS_BASE_FOLDER_INIT_ADD"))
                this.vcsBaseFolderInitAdd = vChild.getText();

            if (vChild.getName().equals ("VCS_CHECKIN_OPTIONS"))
                this.vcsCheckinOptions = vChild.getText();

            if (vChild.getName().equals ("VCS_CHECKIN_OPTIONS_REQUIRED"))
                this.vcsCheckinOptionsRequired = vChild.getText();

            if (vChild.getName().equals ("VCS_CHECKOUT_OPTIONS"))
                this.vcsCheckoutOptions = vChild.getText();

            if (vChild.getName().equals ("VCS_CHECKOUT_OPTIONS_REQUIRED"))
                this.vcsCheckoutOptionsRequired = vChild.getText();

            if (vChild.getName().equals ("VCS_CIS_IMPORT_OPTIONS"))
                this.vcsCisImportOptions = vChild.getText();

            if (vChild.getName().equals ("VCS_CIS_EXPORT_OPTIONS"))
                this.vcsCisExportOptions = vChild.getText();

            if (vChild.getName().equals ("VCS_REPOSITORY_URL"))
                this.vcsRepositoryUrl = vChild.getText();

            if (vChild.getName().equals ("VCS_PROJECT_ROOT"))
                this.vcsProjectRoot = vChild.getText();

            if (vChild.getName().equals ("VCS_WORKSPACE_HOME"))
                this.vcsWorkspaceHome = vChild.getText();

            if (vChild.getName().equals ("VCS_WORKSPACE_NAME"))
                this.vcsWorkspaceName = vChild.getText();

            if (vChild.getName().equals ("VCS_WORKSPACE_DIR"))
                this.vcsWorkspaceDir = vChild.getText();

            if (vChild.getName().equals ("VCS_TEMP_DIR"))
                this.vcsTempDir = vChild.getText();

            if (vChild.getName().equals ("VCS_USERNAME"))
                this.vcsUsername = vChild.getText();

            if (vChild.getName().equals ("VCS_PASSWORD"))
                this.vcsPassword = vChild.getText();

            if (vChild.getName().equals ("VCS_IGNORE_MESSAGES"))
                this.vcsIgnoreMessages = vChild.getText();

            if (vChild.getName().equals ("VCS_MESSAGE_PREPEND"))
                this.vcsMessagePrepend = vChild.getText();
            
            if (vChild.getName().equals ("vcsSpecificEnvVars")) {
                for (Element eNode : vChild.getChildren()) {
                    if (eNode.getName().equals ("envVar")) {
                        EnvironmentVariable ev = new EnvironmentVariable (eNode);
                        
                        if (ev.getVariable().equals ("SVN_EDITOR"))
                            this.svnEditor = ev.getValue();
                        else if (ev.getVariable().equals ("SVN_ENV"))
                            this.svnEnv = ev.getValue();
                        else if (ev.getVariable().equals ("P4EDITOR"))
                            this.p4Editor = ev.getValue();
                        else if (ev.getVariable().equals ("P4CLIENT"))
                            this.p4Client = ev.getValue();
                        else if (ev.getVariable().equals ("P4PORT"))
                            this.p4Port = ev.getValue();
                        else if (ev.getVariable().equals ("P4USER"))
                            this.p4User = ev.getValue();
                        else if (ev.getVariable().equals ("P4PASSWD"))
                            this.p4Passwd = ev.getValue();
                        else if (ev.getVariable().equals ("P4DEL_LINK_OPTIONS"))
                            this.p4DelLinkOptions = ev.getValue();
                        else if (ev.getVariable().equals ("CVSROOT"))
                            this.cvsRoot = ev.getValue();
                        else if (ev.getVariable().equals ("CVS_RSH"))
                            this.cvsRsh = ev.getValue();
                        else if (ev.getVariable().equals ("CVS_ENV"))
                            this.svnEnv = ev.getValue();
                        else if (ev.getVariable().equals ("TFS_EDITOR"))
                            this.tfsEditor = ev.getValue();
                        else if (ev.getVariable().equals ("TFS_ENV"))
                            this.tfsEnv = ev.getValue();
                        else if (ev.getVariable().equals ("TFS_CHECKIN_OPTIONS"))
                            this.tfsCheckinOptions = ev.getValue();
                        else if (ev.getVariable().equals ("TFS_SERVER_URL"))
                            this.tfsServerUrl = ev.getValue();
                        else {
                            if (envVars == null)
                                envVars = new ArrayList<EnvironmentVariable>();
                            
                            envVars.add (ev);
                        }
                    }
                }
            }
            
            if (vChild.getName().equals ("resourcePath"))
                this.resourcePath = vChild.getText();
            
            if (vChild.getName().equals ("resourceType"))
                this.resourceType = vChild.getText().toUpperCase();
            
            if (vChild.getName().equals ("vcsLabel"))
                this.vcsLabel = vChild.getText();
            
            if (vChild.getName().equals ("revision"))
                this.revision = vChild.getText();
            
            if (vChild.getName().equals ("message"))
                this.message = vChild.getText();
            
        }
    }

    /**
     * <p>
     * Returns the object as a JDom Element.
     * </p>
     * 
     * @param  ignored  Normally this would be the name of the element to use. In this case, it's ignored because the record type determines the name.
     * @param  indent   The number of tabs (spaces) to indent the child elements.
     * @return          The value.
     */
    public Element toElement (
        String ignored,
        int indent
    ) {
        Element result = null;
        String indentStr = StringUtils.getIndent (indent);
        String indentStr2 = StringUtils.getIndent (indent + 1);
        
        if (this.type == TYPE_CONNECTION) {
            result = new Element ("vcsConnection");

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("id").setText (this.id));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_TYPE").setText (VCS_TYPE_LABELS [this.vcsType]));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_BASE_TYPE").setText (VCS_BASE_TYPE_LABELS [this.vcsBaseType]));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_HOME").setText (this.vcsHome));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_COMMAND").setText (this.vcsCommand));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_EXEC_FULL_PATH").setText ("" + this.vcsExecFullPath));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_OPTIONS").setText (this.vcsOptions));

            // these are optional according to the XML schema, but it's to support backwards compatibility.
            //
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_WORKSPACE_INIT_NEW_OPTIONS").setText (this.vcsWorkspaceInitNewOptions));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_WORKSPACE_INIT_LINK_OPTIONS").setText (this.vcsWorkspaceInitLinkOptions));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_WORKSPACE_INIT_GET_OPTIONS").setText (this.vcsWorkspaceInitGetOptions));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_BASE_FOLDER_INIT_ADD").setText (this.vcsBaseFolderInitAdd));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_CHECKIN_OPTIONS").setText (this.vcsCheckinOptions));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_CHECKIN_OPTIONS_REQUIRED").setText (this.vcsCheckinOptionsRequired));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_CHECKOUT_OPTIONS").setText (this.vcsCheckoutOptions));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_CHECKOUT_OPTIONS_REQUIRED").setText (this.vcsCheckoutOptionsRequired));            //

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_CIS_IMPORT_OPTIONS").setText (this.vcsCisImportOptions));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_CIS_EXPORT_OPTIONS").setText (this.vcsCisExportOptions));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_REPOSITORY_URL").setText (this.vcsRepositoryUrl));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_PROJECT_ROOT").setText (this.vcsProjectRoot));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_WORKSPACE_HOME").setText (this.vcsWorkspaceHome));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_WORKSPACE_NAME").setText (this.vcsWorkspaceName));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_WORKSPACE_DIR").setText (this.vcsWorkspaceDir));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_TEMP_DIR").setText (this.vcsTempDir));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_USERNAME").setText (this.vcsUsername));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_PASSWORD").setText (this.vcsPassword));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_IGNORE_MESSAGES").setText (this.vcsIgnoreMessages));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("VCS_MESSAGE_PREPEND").setText (this.vcsMessagePrepend));
            
            
            // add VCS specific environment variables. in this module, they are serialized as environment
            // variables under the <vcsSpecificEnvVars> tag.
            //
            Element evNode = null;
            
            switch (this.vcsType) {
                case (VCS_BASE_TYPE_SVN):
                    if (this.svnEditor != null && this.svnEditor.length() > 0 ||
                        this.svnEnv != null && this.svnEnv.length() > 0
                    ) {
                        evNode = new Element ("vcsSpecificEnvVars");
                        
                        if (this.svnEditor != null && this.svnEditor.length() > 0) {
                            evNode.addContent ("\n" + indentStr2);
                            evNode.addContent (new EnvironmentVariable ("SVN_EDITOR", this.svnEditor).toElement ("envVar", indent + 2).addContent ("\n" + indentStr2));
                        }
                        
                        if (this.svnEnv != null && this.svnEnv.length() > 0) {
                            evNode.addContent ("\n" + indentStr2);
                            evNode.addContent (new EnvironmentVariable ("SVN_ENV", this.svnEnv).toElement ("envVar", indent + 2).addContent ("\n" + indentStr2));
                        }
                    }
                
                    break;
                    
                case (VCS_BASE_TYPE_P4):
                    if (this.p4Editor != null && this.p4Editor.length() > 0 ||
                        this.p4Client != null && this.p4Client.length() > 0 ||
                        this.p4Port != null && this.p4Port.length() > 0 ||
                        this.p4User != null && this.p4User.length() > 0 ||
                        this.p4Passwd != null && this.p4Passwd.length() > 0 ||
                        this.p4Env != null && this.p4Env.length() > 0
                    ) {
                        evNode = new Element ("vcsSpecificEnvVars");
                        
                        if (this.p4Editor != null && this.p4Editor.length() > 0) {
                            evNode.addContent ("\n" + indentStr2);
                            evNode.addContent (new EnvironmentVariable ("P4EDITOR", this.p4Editor).toElement ("envVar", indent + 2).addContent ("\n" + indentStr2));
                        }
                        
                        if (this.p4Client != null && this.p4Client.length() > 0) {
                            evNode.addContent ("\n" + indentStr2);
                            evNode.addContent (new EnvironmentVariable ("P4CLIENT", this.p4Client).toElement ("envVar", indent + 2).addContent ("\n" + indentStr2));
                        }
                        
                        if (this.p4Port != null && this.p4Port.length() > 0) {
                            evNode.addContent ("\n" + indentStr2);
                            evNode.addContent (new EnvironmentVariable ("P4PORT", this.p4Port).toElement ("envVar", indent + 2).addContent ("\n" + indentStr2));
                        }
                        
                        if (this.p4User != null && this.p4User.length() > 0) {
                            evNode.addContent ("\n" + indentStr2);
                            evNode.addContent (new EnvironmentVariable ("P4USER", this.p4User).toElement ("envVar", indent + 2).addContent ("\n" + indentStr2));
                        }
                        
                        if (this.p4Passwd != null && this.p4Passwd.length() > 0) {
                            evNode.addContent ("\n" + indentStr2);
                            evNode.addContent (new EnvironmentVariable ("P4PASSWD", this.p4Passwd).toElement ("envVar", indent + 2).addContent ("\n" + indentStr2));
                        }
                        
                        if (this.p4Env != null && this.p4Env.length() > 0) {
                            evNode.addContent ("\n" + indentStr2);
                            evNode.addContent (new EnvironmentVariable ("P4_ENV", this.p4Env).toElement ("envVar", indent + 2).addContent ("\n" + indentStr2));
                        }
                    }
                    
                    break;

                case (VCS_BASE_TYPE_CVS):
                    if (this.cvsRoot != null && this.cvsRoot.length() > 0 ||
                        this.cvsRsh != null && this.cvsRsh.length() > 0 ||
                        this.cvsEnv != null && this.cvsEnv.length() > 0
                    ) {
                        evNode = new Element ("vcsSpecificEnvVars");
                        
                        if (this.cvsRoot != null && this.cvsRoot.length() > 0) {
                            evNode.addContent ("\n" + indentStr2);
                            evNode.addContent (new EnvironmentVariable ("CVSROOT", this.cvsRoot).toElement ("envVar", indent + 2).addContent ("\n" + indentStr2));
                        }
                        
                        if (this.cvsRsh != null && this.cvsRsh.length() > 0) {
                            evNode.addContent ("\n" + indentStr2);
                            evNode.addContent (new EnvironmentVariable ("CVS_RSH", this.cvsRsh).toElement ("envVar", indent + 2).addContent ("\n" + indentStr2));
                        }
                        
                        if (this.cvsEnv != null && this.cvsEnv.length() > 0) {
                            evNode.addContent ("\n" + indentStr2);
                            evNode.addContent (new EnvironmentVariable ("CVS_ENV", this.cvsEnv).toElement ("envVar", indent + 2).addContent ("\n" + indentStr2));
                        }
                    }
                
                    break;
                    
                case (VCS_BASE_TYPE_TFS):
                    if (this.tfsEditor != null && this.tfsEditor.length() > 0 ||
                        this.tfsEnv != null && this.tfsEnv.length() > 0 ||
                        this.tfsCheckinOptions != null && this.tfsCheckinOptions.length() > 0 ||
                        this.tfsServerUrl != null && this.tfsServerUrl.length() > 0
                    ) {
                        evNode = new Element ("vcsSpecificEnvVars");
                        
                        if (this.tfsEditor != null && this.tfsEditor.length() > 0) {
                            evNode.addContent ("\n" + indentStr2);
                            evNode.addContent (new EnvironmentVariable ("TFS_EDITOR", this.tfsEditor).toElement ("envVar", indent + 2).addContent ("\n" + indentStr2));
                        }
                        
                        if (this.tfsEnv != null && this.tfsEnv.length() > 0) {
                            evNode.addContent ("\n" + indentStr2);
                            evNode.addContent (new EnvironmentVariable ("TFS_ENV", this.tfsEnv).toElement ("envVar", indent + 2).addContent ("\n" + indentStr2));
                        }
                        
                        if (this.tfsCheckinOptions != null && this.tfsCheckinOptions.length() > 0) {
                            evNode.addContent ("\n" + indentStr2);
                            evNode.addContent (new EnvironmentVariable ("TFS_CHECKIN_OPTIONS", this.tfsCheckinOptions).toElement ("envVar", indent + 2).addContent ("\n" + indentStr2));
                        }
                        
                        if (this.tfsServerUrl != null && this.tfsServerUrl.length() > 0) {
                            evNode.addContent ("\n" + indentStr2);
                            evNode.addContent (new EnvironmentVariable ("TFS_SERVER_URL", this.tfsServerUrl).toElement ("envVar", indent + 2).addContent ("\n" + indentStr2));
                        }
                    }
                
                    break;
                    
            }
            
            // add any other unrecognized environment variables.
            //
            if (this.envVars != null && this.envVars.size() > 0) {
                if (evNode == null)
                    evNode = new Element ("vcsSpecificEnvVars");
                        
                for (EnvironmentVariable ev : this.envVars) {
                    evNode.addContent ("\n" + indentStr2);
                    evNode.addContent (ev.toElement ("envVar", indent + 1));
                }
            }

            // if any environment variables were added, attach them to the result.
            //
            if (evNode != null) {
                evNode.addContent ("\n" + indentStr);
                
                result.addContent ("\n" + indentStr);
                result.addContent (evNode);
            }

        } else {
            result = new Element ("vcsResource");

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("id").setText (this.id));

            if (this.resourcePath != null && this.resourcePath.length() > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("resourcePath").setText (this.resourcePath));
            }

            if (this.resourceType != null && this.resourceType.length() > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("resourceType").setText (this.resourceType));
            }

            if (this.vcsLabel != null && this.vcsLabel.length() > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("vcsLabel").setText (this.vcsLabel));
            }

            if (this.revision != null && this.revision.length() > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("revision").setText (this.revision));
            }

            if (this.message != null && this.message.length() > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("message").setText (this.message));
            }

        }

        return result;
    }

    /**
     * <p>
     * Sets the <code>operation</code> field.
     * </p>
     * 
     * @param  operation  The operation to be performed on the VCS record.
     */
    public void setOperation (String operation) {
        this.operation = operation;
    }

    /**
     * <p>
     * Returns the value of the <code>operation</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getOperation () {
        return operation;
    }

    /**
     * <p>
     * Sets the <code>origId</code> field.
     * </p>
     * 
     * @param  origId  For edit/copy operations, this contains the original ID (for 
     *                 cases where the user wants to change the ID field.)
     */
    public void setOrigId (String origId) {
        this.origId = origId;
    }

    /**
     * <p>
     * Returns the value of the <code>origId</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getOrigId () {
        return origId;
    }

    /**
     * <p>
     * Sets the <code>id</code> field.
     * </p>
     * 
     * @param  id  The ID of the VCS record.
     */
    public void setId (String id) {
        this.id = id;
    }

    /**
     * <p>
     * Returns the value of the <code>id</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getId () {
        return id;
    }

    /**
     * <p>
     * Sets the <code>type</code> field.
     * </p>
     * 
     * @param  type  The type of VCS record. See TYPE_* constants above.
     */
    public void setType (int type) {
        this.type = type;
    }

    /**
     * <p>
     * Returns the value of the <code>type</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getType () {
        return type;
    }

    /**
     * <p>
     * Sets the <code>vcsType</code> field.
     * </p>
     * 
     * @param  vcsType  The type of VCS being used [svn, p4, cvs, tfs2005, tfs2010, tfs2012, etc].
     */
    public void setVcsType (int vcsType) {
        this.vcsType = vcsType;
        
        // earlier versions of PDTool do not have the VCS_BASE_TYPE attribute
        if (this.vcsType >= 0 && this.vcsType < VCS_TYPE_BASES.length) {
        	this.vcsBaseType = VCS_TYPE_BASES[this.vcsType];
        }
    }

    /**
     * <p>
     * Returns the value of the <code>vcsType</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getVcsType () {
        return vcsType;
    }

    /**
     * <p>
     * Sets the <code>vcsBaseType</code> field.
     * </p>
     * 
     * @param  vcsBaseType  The base type of VCS being used [svn, p4, cvs, tfs, etc].
     */
    public void setVcsBaseType (int vcsBaseType) {
        this.vcsBaseType = vcsBaseType;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsBaseType</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getVcsBaseType () {
        return vcsBaseType;
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
     * Sets the <code>vcsBaseFolderInitAdd</code> field.
     * </p>
     * 
     * @param  vcsBaseFolderInitAdd  Options to use when performing an add operation after initialization.
     */
    public void setVcsBaseFolderInitAdd (String vcsBaseFolderInitAdd) {
        this.vcsBaseFolderInitAdd = vcsBaseFolderInitAdd;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsBaseFolderInitAdd</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsBaseFolderInitAdd () {
        return vcsBaseFolderInitAdd;
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
     * Sets the <code>vcsCisImportOptions</code> field.
     * </p>
     * 
     * @param  vcsCisImportOptions  Options to use when importing resources into CIS.
     */
    public void setVcsCisImportOptions (String vcsCisImportOptions) {
        this.vcsCisImportOptions = vcsCisImportOptions;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsCisImportOptions</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsCisImportOptions () {
        return vcsCisImportOptions;
    }

    /**
     * <p>
     * Sets the <code>vcsCisExportOptions</code> field.
     * </p>
     * 
     * @param  vcsCisExportOptions  Options to use when exporting resources from CIS.
     */
    public void setVcsCisExportOptions (String vcsCisExportOptions) {
        this.vcsCisExportOptions = vcsCisExportOptions;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsCisExportOptions</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsCisExportOptions () {
        return vcsCisExportOptions;
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
        this.vcsPassword = StringUtils.encryptPassword (vcsPassword);
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

    /**
     * <p>
     * Sets the <code>resourcePath</code> field.
     * </p>
     * 
     * @param  resourcePath  The path of the resource.
     */
    public void setResourcePath (String resourcePath) {
        this.resourcePath = resourcePath;
    }

    /**
     * <p>
     * Returns the value of the <code>resourcePath</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getResourcePath () {
        return resourcePath;
    }

    /**
     * <p>
     * Sets the <code>resourceType</code> field.
     * </p>
     * 
     * @param  resourceType  The type of the resource.
     */
    public void setResourceType (String resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * <p>
     * Returns the value of the <code>resourceType</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getResourceType () {
        return resourceType;
    }

    /**
     * <p>
     * Sets the <code>vcsLabel</code> field.
     * </p>
     * 
     * @param  vcsLabel  The label that the VCS system uses for this resource.
     */
    public void setVcsLabel (String vcsLabel) {
        this.vcsLabel = vcsLabel;
    }

    /**
     * <p>
     * Returns the value of the <code>vcsLabel</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVcsLabel () {
        return vcsLabel;
    }

    /**
     * <p>
     * Sets the <code>revision</code> field.
     * </p>
     * 
     * @param  revision  The revision code or branch for this resource.
     */
    public void setRevision (String revision) {
        this.revision = revision;
    }

    /**
     * <p>
     * Returns the value of the <code>revision</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getRevision () {
        return revision;
    }

    /**
     * <p>
     * Sets the <code>message</code> field.
     * </p>
     * 
     * @param  message  The message used to check in the resource. This should not contain any quote characters or line breaks.
     */
    public void setMessage (String message) {
        this.message = message;
    }

    /**
     * <p>
     * Returns the value of the <code>message</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getMessage () {
        return message;
    }
}

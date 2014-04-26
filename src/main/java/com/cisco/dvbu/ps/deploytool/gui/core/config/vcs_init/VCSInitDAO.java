package com.cisco.dvbu.ps.deploytool.gui.core.config.vcs_init;

import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfile;
import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.resources.VCSInitResource;
import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;

import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage.MessageItem;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Data access object for VCS initialization. Used by {@link VCSInitResource} servlets.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class VCSInitDAO {

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public VCSInitDAO () {
        super ();
    }
    
    /**
     * <p>
     * Examines the filesystem using the provided deployment path to determine VCS initialization status.
     * </p>
     * 
     * @param  profilePath  The path to the deployment profile to use for initialization status.
     * @return              A {@link ResultMessage} object containing the initialization status.
     * @see                 VCSInitResource
     */
    public ResultMessage getStatus(
        String profilePath
    ) {
        List<ResultMessage.MessageItem> messageItems = new ArrayList<ResultMessage.MessageItem>();
        
        DeploymentProfile dp = new DeploymentProfilesDAO().getById (profilePath);
        if (dp == null)
            return new ResultMessage ("error", "Deployment profile \"" + profilePath + "\" does not exist!", null);
        
        if (dp.getVcsType() == null)
            messageItems.add (new ResultMessage.MessageItem (null, "VCS type not set."));
        
        // verify VCS path and executable (can't really verify VCS executable without the full path, though.)
        // Using DepolymentProfile's getProperty() method to resolve all variable references in the property values
        // so we have a full filesystem path to work with.
        //
        if (dp.isVcsExecFullPath()) {
            if (dp.getVcsHome() == null) {
                messageItems.add (new ResultMessage.MessageItem (null, "VCS home not set."));
            } else {
                
                String vcsHomePath = dp.getProperty ("VCS_HOME", true);
                File vcsHomeFile = new File (vcsHomePath);

                if (! vcsHomeFile.isDirectory()) {
                    messageItems.add (new ResultMessage.MessageItem (null, "VCS home (" + vcsHomePath + ") does not exist."));
                } else {
                    if (! vcsHomeFile.canRead())
                        messageItems.add (new ResultMessage.MessageItem (null, "VCS home (" + vcsHomePath + ") missing read permission."));
                    
                    if (dp.getVcsCommand() == null) {
                        messageItems.add (new ResultMessage.MessageItem (null, "VCS command not set."));
                    } else {
                        
                        String vcsCommandPath = vcsHomePath + "/" + dp.getProperty ("VCS_COMMAND", true);
                        File vcsCommandFile = new File (vcsCommandPath);
                        
                        if (! vcsCommandFile.isFile()) {
                            messageItems.add (new ResultMessage.MessageItem (null, "VCS command (" + vcsCommandPath + ") does not exist."));
                        } else {
                            if (! vcsCommandFile.canRead())
                                messageItems.add (new ResultMessage.MessageItem (null, "VCS command missing (" + vcsCommandPath + ") read permission."));
  
                            if (! vcsCommandFile.canExecute())
                                messageItems.add (new ResultMessage.MessageItem (null, "VCS command missing (" + vcsCommandPath + ") execute permission."));
                        }
                    }
                }
            }
        }
        
        // check the VCS repository URL
        //
        if (dp.getVcsRepositoryUrl() == null)
            messageItems.add (new ResultMessage.MessageItem (null, "VCS repository URL not set."));
        
        // check the VCS workspace home (where all the workspaces and temp directories will be created. typically $PDTOOL_HOME / $PROJECT_HOME )
        //
        if (dp.getVcsWorkspaceHome() == null) {
            messageItems.add (new ResultMessage.MessageItem (null, "VCS workspace home not set."));
        } else {
            
            String vcsWorkspaceHomePath = dp.getProperty ("VCS_WORKSPACE_HOME", true);
            File vcsWorkspaceHomeFile = new File (vcsWorkspaceHomePath);
            
            if (! vcsWorkspaceHomeFile.exists()) {
                messageItems.add (new ResultMessage.MessageItem (null, "VCS workspace home (" + vcsWorkspaceHomePath + ") does not exist."));
            } else {
                if (! vcsWorkspaceHomeFile.isDirectory())
                    messageItems.add (new ResultMessage.MessageItem (null, "VCS workspace home (" + vcsWorkspaceHomePath + ") is not a directory."));

                if (! vcsWorkspaceHomeFile.canRead())
                    messageItems.add (new ResultMessage.MessageItem (null, "VCS workspace home (" + vcsWorkspaceHomePath + ") missing read permission."));

                if (! vcsWorkspaceHomeFile.canWrite())
                    messageItems.add (new ResultMessage.MessageItem (null, "VCS workspace home (" + vcsWorkspaceHomePath + ") missing write permission."));
            }
        }
        
        // check the VCS workspace directory
        //
        if (dp.getVcsWorkspaceDir() == null) {
            messageItems.add (new ResultMessage.MessageItem (null, "VCS workspace directory not set."));
        } else {
            
            String vcsWorkspaceDirPath = dp.getProperty ("VCS_WORKSPACE_DIR", true);
            File vcsWorkspaceDirFile = new File (vcsWorkspaceDirPath);
            
            if (! vcsWorkspaceDirFile.exists()) {
                messageItems.add (new ResultMessage.MessageItem (null, "VCS workspace directory (" + vcsWorkspaceDirPath + ") does not exist. (Initialization will create this.)"));
            } else {
                if (! vcsWorkspaceDirFile.isDirectory())
                    messageItems.add (new ResultMessage.MessageItem (null, "VCS workspace directory (" + vcsWorkspaceDirPath + ") is not a directory."));

                if (! vcsWorkspaceDirFile.canRead())
                    messageItems.add (new ResultMessage.MessageItem (null, "VCS workspace directory (" + vcsWorkspaceDirPath + ") missing read permission."));

                if (! vcsWorkspaceDirFile.canWrite())
                    messageItems.add (new ResultMessage.MessageItem (null, "VCS workspace directory (" + vcsWorkspaceDirPath + ") missing write permission."));
                
                String sharedCmfPath = vcsWorkspaceDirPath + "/" + 
                                       dp.getProperty ("VCS_PROJECT_ROOT", true) + 
                                       "/shared/shared.cmf";
                File sharedCmfFile = new File (sharedCmfPath);
                
                if (! sharedCmfFile.exists()) {
                    messageItems.add (new ResultMessage.MessageItem (null, "No resources under /shared checked out from VCS."));
                }
            }
        }
        
        // check the VCS temporary folder
        //
        if (dp.getVcsTempDir() == null) {
            messageItems.add (new ResultMessage.MessageItem (null, "VCS temporary directory not set."));
        } else {

            String vcsTempDirPath = dp.getProperty ("VCS_TEMP_DIR", true);
            File vcsTempDirFile = new File (vcsTempDirPath);
            
            if (! vcsTempDirFile.exists()) {
                messageItems.add (new ResultMessage.MessageItem (null, "VCS temporary directory (" + vcsTempDirPath + ") does not exist. (Initialization will create this.)"));
            } else {
                if (! vcsTempDirFile.isDirectory())
                    messageItems.add (new ResultMessage.MessageItem (null, "VCS temporary directory (" + vcsTempDirPath + ") is not a directory."));

                if (! vcsTempDirFile.canRead())
                    messageItems.add (new ResultMessage.MessageItem (null, "VCS temporary directory (" + vcsTempDirPath + ") missing read permission."));

                if (! vcsTempDirFile.canWrite())
                    messageItems.add (new ResultMessage.MessageItem (null, "VCS temporary directory (" + vcsTempDirPath + ") missing write permission."));
            }
        }

        if (messageItems.size() == 0) {
            return new ResultMessage ("success", "VCS workspace is initialized.", null);
        } else {
            return new ResultMessage ("error", null, messageItems);
        }
    }
}

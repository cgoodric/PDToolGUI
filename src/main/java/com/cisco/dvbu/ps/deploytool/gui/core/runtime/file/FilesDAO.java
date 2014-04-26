package com.cisco.dvbu.ps.deploytool.gui.core.runtime.file;

import com.cisco.dvbu.ps.deploytool.gui.util.DAOConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_plan.DeploymentPlan;
import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_plan.DeploymentPlansDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.Preferences;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.PreferencesDAO;
import com.cisco.dvbu.ps.deploytool.gui.resources.FileResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.FileListResource;
import com.cisco.dvbu.ps.deploytool.gui.util.ListColumnInfo;
import com.cisco.dvbu.ps.deploytool.gui.util.ListResult;
import com.cisco.dvbu.ps.deploytool.gui.util.ListResultRowComparator;
import com.cisco.dvbu.ps.deploytool.gui.util.ListUtils;
import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;
import com.cisco.dvbu.ps.deploytool.gui.util.StringUtils;

import java.util.Arrays;

/**
 * <p>
 * Data access object for file lists. Used by {@link FileResource} and {@link FileListResource} servlets.
 * </p>
 *
 * @author Calvin Goodrich
 * @version 1.0
 */
public class FilesDAO {

    private static final Logger log = LoggerFactory.getLogger (FilesDAO.class);
    
    public static final String MODULES_REL_PATH = "resources/modules";

    public static final int FILE_TYPE_FOLDER = 0;
    public static final int FILE_TYPE_DEPLOY_CONFIG = 1;
    public static final int FILE_TYPE_SERVERS = 2;
    public static final int FILE_TYPE_DEPLOY_PLAN = 3;
    public static final int FILE_TYPE_MODULE_ARCHIVE = 4;
    public static final int FILE_TYPE_MODULE_DATA_SOURCE = 5;
    public static final int FILE_TYPE_MODULE_GROUP = 6;
    public static final int FILE_TYPE_MODULE_PRIVILEGE = 7;
    public static final int FILE_TYPE_MODULE_REBIND = 8;
    public static final int FILE_TYPE_MODULE_REGRESSION = 9;
    public static final int FILE_TYPE_MODULE_RESOURCE_CACHE = 10;
    public static final int FILE_TYPE_MODULE_RESOURCE = 11;
    public static final int FILE_TYPE_MODULE_SERVER_ATTRIBUTE = 12;
    public static final int FILE_TYPE_MODULE_SERVER_MANAGER = 13;
    public static final int FILE_TYPE_MODULE_TRIGGER = 14;
    public static final int FILE_TYPE_MODULE_USER = 15;
    public static final int FILE_TYPE_MODULE_VCS = 16;
    public static final int FILE_TYPE_LOG = 17;
    
    public static final List<FileTypeProperty> fileTypeProperties = Arrays.asList (
        new FileTypeProperty ("",                         "",                                                                       "",                 "",           0, ""), // folder
        new FileTypeProperty ("Deployment Configuration", DAOConstants.TEMPLATES_REL_PATH + "/deploy.properties.tmpl",              "resources/config", "properties", 2, ".*PDTool Project Environment Variables.*"),
        new FileTypeProperty ("Servers List",             DAOConstants.TEMPLATES_REL_PATH + "/servers.xml.tmpl",                    MODULES_REL_PATH,   "xml",        2, "^<servers>$"),
        new FileTypeProperty ("Deployment Plan",          DAOConstants.TEMPLATES_REL_PATH + "/emptyPlan.dp.tmpl",                   "resources/plans",  "dp",         1, ".*"),
        new FileTypeProperty ("Archive Module",           DAOConstants.TEMPLATES_REL_PATH + "/emptyArchiveModule.xml.tmpl",         MODULES_REL_PATH,   "xml",        2, "^<(\\w+:)?ArchiveModule.*"),
        new FileTypeProperty ("Data Source Module",       DAOConstants.TEMPLATES_REL_PATH + "/emptyDataSourceModule.xml.tmpl",      MODULES_REL_PATH,   "xml",        2, "^<(\\w+:)?DatasourceModule.*"),
        new FileTypeProperty ("Group Module",             DAOConstants.TEMPLATES_REL_PATH + "/emptyGroupModule.xml.tmpl",           MODULES_REL_PATH,   "xml",        2, "^<(\\w+:)?GroupModule.*"),
        new FileTypeProperty ("Privilege Module",         DAOConstants.TEMPLATES_REL_PATH + "/emptyPrivilegeModule.xml.tmpl",       MODULES_REL_PATH,   "xml",        2, "^<(\\w+:)?PrivilegeModule.*"),
        new FileTypeProperty ("Rebind Module",            DAOConstants.TEMPLATES_REL_PATH + "/emptyRebindModule.xml.tmpl",          MODULES_REL_PATH,   "xml",        2, "^<(\\w+:)?RebindModule.*"),
        new FileTypeProperty ("Regression Module",        DAOConstants.TEMPLATES_REL_PATH + "/emptyRegressionModule.xml.tmpl",      MODULES_REL_PATH,   "xml",        2, "^<(\\w+:)?RegressionModule.*"),
        new FileTypeProperty ("Resource Cache Module",    DAOConstants.TEMPLATES_REL_PATH + "/emptyResourceCacheModule.xml.tmpl",   MODULES_REL_PATH,   "xml",        2, "^<(\\w+:)?ResourceCacheModule.*"),
        new FileTypeProperty ("Resource Module",          DAOConstants.TEMPLATES_REL_PATH + "/emptyResourceModule.xml.tmpl",        MODULES_REL_PATH,   "xml",        2, "^<(\\w+:)?ResourceModule.*"),
        new FileTypeProperty ("Server Attribute Module",  DAOConstants.TEMPLATES_REL_PATH + "/emptyServerAttributeModule.xml.tmpl", MODULES_REL_PATH,   "xml",        2, "^<(\\w+:)?ServerAttributeModule.*"),
        new FileTypeProperty ("Server Manager Module",    DAOConstants.TEMPLATES_REL_PATH + "/emptyServerManagerModule.xml.tmpl",   MODULES_REL_PATH,   "xml",        2, "^<(\\w+:)?ServerManagerModule.*"),
        new FileTypeProperty ("Trigger Module",           DAOConstants.TEMPLATES_REL_PATH + "/emptyTriggerModule.xml.tmpl",         MODULES_REL_PATH,   "xml",        2, "^<(\\w+:)?TriggerModule.*"),
        new FileTypeProperty ("User Module",              DAOConstants.TEMPLATES_REL_PATH + "/emptyUserModule.xml.tmpl",            MODULES_REL_PATH,   "xml",        2, "^<(\\w+:)?UserModule.*"),
        new FileTypeProperty ("VCS Module",               DAOConstants.TEMPLATES_REL_PATH + "/emptyVCSModule.xml.tmpl",             MODULES_REL_PATH,   "xml",        2, "^<(\\w+:)?VCSModule.*"),
        new FileTypeProperty ("Log File",                 "",                                                                       "logs",             "log",        1, ".*")
    );
    
    // contains the ordered list of display columns and sorting methods.
    //
    private Map<String, ListColumnInfo> listColumnsInfo = new HashMap<String, ListColumnInfo>();

    // contains the lists of files for returning in calls to list() method
    //
    private List<List<ListResult.Row>> filesLists = new ArrayList<List<ListResult.Row>>();
    
    // contains the maps of files used for validating and CRUD operations of files
    //
    private List<Map<String, FileRecord>> filesMaps = new ArrayList<Map<String, FileRecord>>();
    
    private static final String pdtHome = System.getProperty ("apps.install.dir").replaceAll ("\\\\", "/");
    
    /**
     * <p>
     * Constructor.
     * </p>
     */
    public FilesDAO() {
        super();
        
        // set up the column list for server lists
        //
        listColumnsInfo.put ("name",         new ListColumnInfo (0, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("path",         new ListColumnInfo (1, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("dateModified", new ListColumnInfo (2, ListResultRowComparator.SORT_TYPE_STRING));
        
        // initialize file Lists and Maps
        //
        for (int i = 0; i < fileTypeProperties.size(); i++) {
            filesLists.add (new ArrayList<ListResult.Row>());
            filesMaps.add (new HashMap<String, FileRecord>());
        }
    }

    /*
     * Servlet methods
     */

    /**
     * <p>
     * Sorts and filters the list of {@link FileRecord} objects and returns a {@link ListResult} object
     * containing the requested page of information.
     * </p>
     * 
     * @param fileType     Indicates the file type list to retrieve.
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
     * @see                FileListResource
     */
    public ListResult list(
        int    fileType,
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
        
        log.debug ("list: fileType = " + fileType);
        
        // sanity check the inputs
        //
        if (numRows <= 0) {
            log.error ("Number of rows requested is out of range: " + numRows);
            return null;
        }
        
        if (pageNum <= 0) {
            log.error ("Requested page number is out of range: " + pageNum);
        }

        // refresh file list for the file type
        //
        getFiles (fileType);
        
        // get current list of files for the file type
        //
        resultList = this.filesLists.get (fileType);
        
        if (resultList == null)
            return new ListResult (1, 1, 0, new ArrayList<ListResult.Row>(), null);
        
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
        
        // apply sorting to the servers list
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
     * Searches for a single {@link FileRecord} object given a URL encoded ID string.
     * </p>
     *
     * @param  path The file path to look for.
     * @return      The requested {@link FileRecord} as an object.
     * @see         FileResource
     */
    public FileRecord findById (
        String path,
        int fileType
    ) {
        getFiles (fileType);
        
        return filesMaps.get (fileType).get (path);
    }

    /**
     * <p>
     * Creates a new file from a {@link FileRecord} object.
     * </p>
     *
     * @param  fileRecord The {@link FileRecord} object to add.
     * @return            A {@link ResultMessage} object containing the results of the add request.
     * @see               FileResource
     */
    public ResultMessage add (FileRecord fileRecord) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;

        // make sure we have the latest version of the servers.xml file
        //
        getFiles (fileRecord.getFileType());
        
        // sanity check the inputs for an "add" operation
        //
        msgList = validateFile (fileRecord, DAOConstants.OPERATION_ADD);

        if (msgList.size() == 0) {
            String newPath = null;
            String tmplPath = null;
            
            try {
                newPath = pdtHome + "/" +                                                                        // PDTool home folder
                          fileTypeProperties.get (fileRecord.getFileType()).defaultFilesLocation + "/" +         // relative file location for this file type
                          fileRecord.getName() + "." + fileTypeProperties.get (fileRecord.getFileType()).suffix; // the user's submitted file name plus the suffix for the file type

                tmplPath = pdtHome + "/" +                                             // PDTool home folder
                           fileTypeProperties.get (fileRecord.getFileType()).template; // relative path to template file for this file type
                
                // copy the file
                //
                FileUtils.copyFile (new File (tmplPath), new File (newPath));
                
                result = new ResultMessage ("success", "Created file \"" + newPath + "\" from \"" + tmplPath + "\"", null);

            } catch (Exception e) {
                log.error ("Error creating file \"" + newPath + "\": " + e.getMessage());
                result = new ResultMessage ("error", "Error creating file \"" + newPath + "\": " + e.getMessage(), null);
            }

        } else { // the fileRecord didn't validate
            
            result = new ResultMessage ("error", null, msgList);
        }

        return result;
    }

    /**
     * <p>
     * Renames a file based on a {@link FileRecord} object.
     * </p>
     *
     * @param  fileRecord The {@link FileRecord} object to rename.
     * @return            A {@link ResultMessage} object containing the results of the add request.
     * @see               FileResource
     */
    public ResultMessage edit (FileRecord fileRecord) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;

        // make sure we have the latest version of the servers.xml file
        //
        getFiles (fileRecord.getFileType());
        
        // sanity check the inputs for an "edit" operation
        //
        msgList = validateFile (fileRecord, DAOConstants.OPERATION_EDIT);

        if (msgList.size() == 0) {
            String newPath = null;
            String origPath = null;
            
            try {
                newPath = pdtHome + "/" +                                                                        // PDTool home folder
                          fileTypeProperties.get (fileRecord.getFileType()).defaultFilesLocation + "/" +         // relative file location for this file type
                          fileRecord.getName() + "." + fileTypeProperties.get (fileRecord.getFileType()).suffix; // the user's submitted new file name plus the suffix for the file type

                origPath = pdtHome + "/" +                                                                            // PDTool home folder
                           fileTypeProperties.get (fileRecord.getFileType()).defaultFilesLocation + "/" +             // relative file location for this file type
                           fileRecord.getOrigname() + "." + fileTypeProperties.get (fileRecord.getFileType()).suffix; // the user's submitted original file name plus the suffix for the file type
                
                // copy the file
                //
                //FileUtils.moveFile (new File (origPath), new File (newPath)); // fails in Windows due to UAC
                FileUtils.copyFile (new File (origPath), new File (newPath));
                FileUtils.deleteQuietly (new File (origPath));
                
                result = new ResultMessage ("success", "Renamed file \"" + origPath + "\" to \"" + newPath + "\"", null);
            
                // now that we've renamed it, we need to deal with where the original file name was referenced.
                //
                switch (fileRecord.getFileType()) {
                    
                    // update the user's default profile if it was renamed.
                    //
                    case FILE_TYPE_DEPLOY_CONFIG:
                        PreferencesDAO prefsDAO = new PreferencesDAO();
                        Preferences prefs = prefsDAO.get();

                        if (prefs.getDefaultProfile().equals (origPath)) {
                            prefs.setDefaultProfile (newPath);
                            ResultMessage prefsResult = prefsDAO.edit (prefs);
                            
                            if (! prefsResult.getStatus().equals ("success")) {
                                result = new ResultMessage ("error", "Deployment profile successfully renamed, but there was an error resetting the default profile: " + prefsResult.getMessage(), null);
                            }
                        }
    
                        break;

                    // update all the deployment plans when a module name changes
                    //
                    case FILE_TYPE_MODULE_ARCHIVE:
                    case FILE_TYPE_MODULE_DATA_SOURCE:
                    case FILE_TYPE_MODULE_GROUP:
                    case FILE_TYPE_MODULE_PRIVILEGE:
                    case FILE_TYPE_MODULE_REBIND:
                    case FILE_TYPE_MODULE_REGRESSION:
                    case FILE_TYPE_MODULE_RESOURCE:
                    case FILE_TYPE_MODULE_RESOURCE_CACHE:
                    case FILE_TYPE_MODULE_SERVER_ATTRIBUTE:
                    case FILE_TYPE_MODULE_TRIGGER:
                    case FILE_TYPE_MODULE_USER:
                    case FILE_TYPE_MODULE_VCS:
                        
                        DeploymentPlansDAO dPlansDAO = new DeploymentPlansDAO();
                        String modHomePathRE = "([$%])MODULE_HOME(\\1)?[/\\\\]" + fileRecord.getOrigname().replaceAll ("[/\\\\]", "[/\\\\]") + "\\.xml";
                        String fullPathRE = pdtHome.replaceAll ("[/\\\\]", "[/\\\\\\\\]") + "[/\\\\]resources[/\\\\]modules[/\\\\]" + fileRecord.getOrigname().replaceAll ("[/\\\\]", "[/\\\\\\\\]") + "\\.xml";
                        String combinedRE = "(?:" + modHomePathRE + "|" + fullPathRE + ")";

                        // iterate over all the deployment plan files
                        //
                        for (ListResult.Row row : list (FILE_TYPE_DEPLOY_PLAN, "false", 10000, 1, "path", "asc", null, null, null, null, null).getRows()) {
                            String path = row.getId();
                            
                            DeploymentPlan dp = dPlansDAO.getById (path);
                            boolean planUpdated = false;
                            
                            for (DeploymentPlan.Step step : dp.getSteps()) {
                                for (int p = 1; p <= 9; p++) {
                                    switch (p) {
                                        case 1:
                                            if (step.getParam1() != null && step.getParam1().matches (combinedRE)) {
                                                step.setParam1 (step.getParam1().replaceFirst (fileRecord.getOrigname() + "\\.xml$", fileRecord.getName() + ".xml"));
                                                planUpdated = true;
                                            }
                                            
                                            break;
                                        
                                        case 2:
                                            if (step.getParam2() != null && step.getParam2().matches (combinedRE)) {
                                                step.setParam2 (step.getParam2().replaceFirst (fileRecord.getOrigname() + "\\.xml$", fileRecord.getName() + ".xml"));
                                                planUpdated = true;
                                            }
                                            
                                            break;
                                        
                                        case 3:
                                            if (step.getParam3() != null && step.getParam3().matches (combinedRE)) {
                                                step.setParam3 (step.getParam3().replaceFirst (fileRecord.getOrigname() + "\\.xml$", fileRecord.getName() + ".xml"));
                                                planUpdated = true;
                                            }
                                            
                                            break;
                                        
                                        case 4:
                                            if (step.getParam4() != null && step.getParam4().matches (combinedRE)) {
                                                step.setParam4 (step.getParam4().replaceFirst (fileRecord.getOrigname() + "\\.xml$", fileRecord.getName() + ".xml"));
                                                planUpdated = true;
                                            }
                                            
                                            break;
                                        
                                        case 5:
                                            if (step.getParam5() != null && step.getParam5().matches (combinedRE)) {
                                                step.setParam5 (step.getParam5().replaceFirst (fileRecord.getOrigname() + "\\.xml$", fileRecord.getName() + ".xml"));
                                                planUpdated = true;
                                            }
                                            
                                            break;
                                        
                                        case 6:
                                            if (step.getParam6() != null && step.getParam6().matches (combinedRE)) {
                                                step.setParam6 (step.getParam6().replaceFirst (fileRecord.getOrigname() + "\\.xml$", fileRecord.getName() + ".xml"));
                                                planUpdated = true;
                                            }
                                            
                                            break;
                                        
                                        case 7:
                                            if (step.getParam7() != null && step.getParam7().matches (combinedRE)) {
                                                step.setParam7 (step.getParam7().replaceFirst (fileRecord.getOrigname() + "\\.xml$", fileRecord.getName() + ".xml"));
                                                planUpdated = true;
                                            }
                                            
                                            break;
                                        
                                        case 8:
                                            if (step.getParam8() != null && step.getParam8().matches (combinedRE)) {
                                                step.setParam8 (step.getParam8().replaceFirst (fileRecord.getOrigname() + "\\.xml$", fileRecord.getName() + ".xml"));
                                                planUpdated = true;
                                            }
                                            
                                            break;
                                        
                                        case 9:
                                            if (step.getParam9() != null && step.getParam9().matches (combinedRE)) {
                                                step.setParam9 (step.getParam9().replaceFirst (fileRecord.getOrigname() + "\\.xml$", fileRecord.getName() + ".xml"));
                                                planUpdated = true;
                                            }
                                            
                                            break;
                                        
                                    }
                                }
                            }
                            
                            if (planUpdated) {
                                dPlansDAO.edit (dp);
                            }
                        }
                        
                        break;
                }

            } catch (Exception e) {
                log.error ("Error renaming file \"" + newPath + "\": " + e.getMessage());
                e.printStackTrace();
                result = new ResultMessage ("error", "Error renaming file \"" + newPath + "\": " + e.getMessage(), null);
            }

        } else { // the fileRecord didn't validate
            
            result = new ResultMessage ("error", null, msgList);
        }

        return result;
    }

    /**
     * <p>
     * Copies a list of files.
     * </p>
     *
     * @param paths    A comma separated list of file names to copy.
     * @param fileType The file type of the file to copy
     * @return         A {@link ResultMessage}object containing the results of the copy request.
     * @see            FileResource
     */
    public ResultMessage copy (
        String paths,
        int fileType
    ) {
        List<ResultMessage.MessageItem> msgList = new ArrayList<ResultMessage.MessageItem>();
        boolean atLeastOneFileCopied = false;
        ResultMessage result;
        
        // make sure we have the latest list of files for this file type
        //
        getFiles (fileType);
        
        // iterate over all the input file paths
        //
        String[] pathArray = paths.split (",");
        for (int i = 0; i < pathArray.length; i++) {
            List<ResultMessage.MessageItem> tmpMsgList;
            FileRecord fileRecord = new FileRecord();

            fileRecord.setName (StringUtils.basename (pathArray[i]));
            fileRecord.setPath (pathArray[i]); 
            fileRecord.setFileType (fileType);
            
            String fileDir = StringUtils.dirname (fileRecord.getPath());

            // make sure the source server exists.
            //
            tmpMsgList = validateFile (fileRecord, DAOConstants.OPERATION_COPY);

            // if so, then make the copy
            //
            if (tmpMsgList.size() == 0) {

                FileRecord newFileRecord = null;
                
                // make a copy of the original file's record.
                //
                try {
                   newFileRecord = findById (fileRecord.getPath(), fileType);
                } catch (Exception e) {
                    log.error ("error encoding file path \"" + fileRecord.getPath() + "\" for lookup of file to copy.");
                }
                
                // report an error if the original file wasn't in the file list
                //
                if (newFileRecord == null) {
                    tmpMsgList.add (new ResultMessage.MessageItem ("path", "Unable to locate file at \"" + fileRecord.getPath() + "\"."));
                    continue;
                }
                
                // generate a new server id for the copy. start with the original id + "_copy", then look for
                // id + "_copy1", id + "_copy2", etc.
                //
                int j = 0;
                do {
                    String newName = StringUtils.removeFileSuffix (fileRecord.getName()) + "_copy" + ((j == 0) ? "" : j) + "." + fileTypeProperties.get (fileType).suffix;
                    String newPath = fileDir + "/" + newName;

                    newFileRecord.setName (newName);
                    newFileRecord.setPath (newPath);
                    newFileRecord.setFileType (fileType);

                    j++;
                } while (validateFile (newFileRecord, DAOConstants.OPERATION_COPY).size() == 0);
    
                // copy the file
                //
                try {
                    FileUtils.copyFile(new File (fileRecord.getPath()), new File (newFileRecord.getPath()));
                } catch (Exception e) {
                    tmpMsgList.add (new ResultMessage.MessageItem ("path", "Unable to copy \"" + fileRecord.getPath() + "\" to \"" + newFileRecord.getPath() + "\": " + e.getMessage()));
                }
                
                atLeastOneFileCopied = true;
                tmpMsgList.add (new ResultMessage.MessageItem ("path", "File \"" + fileRecord.getPath() + "\" copied to \"" + newFileRecord.getPath() + "\"."));

            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneFileCopied) {
            try {
                result = new ResultMessage ("success", null, msgList);
            } catch (Exception e) {
                log.error ("Error copying file(s): " + e.getMessage());
                result = new ResultMessage ("error", "Error copying file(s): " + e.getMessage(), null);
            }
        } else {
            result = new ResultMessage ("error", null, msgList);
        }

        return result;
    }

    /**
     * <p>
     * Deletes a list of files.
     * </p>
     *
     * @param paths    A comma separated list of file names to delete.
     * @param fileType The file type of the file to delete
     * @return         A {@link ResultMessage}object containing the results of the delete request.
     * @see            FileResource
     */
    public ResultMessage delete (
        String paths,
        int fileType
    ) {
        List<ResultMessage.MessageItem> msgList = new ArrayList<ResultMessage.MessageItem>();
        boolean atLeastOneFileDeleted = false;
        ResultMessage result;
        
        // make sure we have the latest list of files for this file type
        //
        getFiles (fileType);
        
        // iterate over all the input server ids
        //
        String[] pathArray = paths.split (",");
        for (int i = 0; i < pathArray.length; i++) {
            List<ResultMessage.MessageItem> tmpMsgList;
            FileRecord fileRecord = new FileRecord();

            fileRecord.setName (StringUtils.basename (pathArray[i]));
            fileRecord.setPath (pathArray[i]);
            fileRecord.setFileType (fileType);
            
            // make sure the source server exists.
            //
            tmpMsgList = validateFile (fileRecord, DAOConstants.OPERATION_COPY);

            // if so, then make sure it doesn't :)
            //
            if (tmpMsgList.size() == 0) {
    
                // delete the file
                //
                try {
                    FileUtils.deleteQuietly (new File (fileRecord.getPath()));
                } catch (Exception e) {
                    tmpMsgList.add (new ResultMessage.MessageItem ("path", "Unable to delete \"" + fileRecord.getPath() + "\": " + e.getMessage()));
                }
                
                atLeastOneFileDeleted = true;
                tmpMsgList.add (new ResultMessage.MessageItem ("path", "File \"" + fileRecord.getPath() + "\" deleted."));

            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneFileDeleted) {
            try {
                result = new ResultMessage ("success", null, msgList);
            } catch (Exception e) {
                log.error ("Error deleting file(s): " + e.getMessage());
                result = new ResultMessage ("error", "Error deleting file(s): " + e.getMessage(), null);
            }
        } else {
            result = new ResultMessage ("error", null, msgList);
        }

        return result;
    }
    
    /**
     * <p>
     * Returns the location on the filesystem of PDTool
     * </p>
     *
     * @return         A {@link String} containing the PDTool location.
     */
    public static String getPdtHome() {
        return pdtHome;
    }

    /**
     * <p>
     * Writes out a temporary group generation plan
     * </p>
     *
     * @return         A {@link String} containing the path to the temporary plan.
     */
    public static String writeTempGenerationPlan (
        String planText
    ) {
        String planFilePath = getPdtHome() + "/resources/plans/" + "tmp_" + System.currentTimeMillis() + ".dp";
        
        File planFile = new File (planFilePath);
        
        if (planFile.exists()) {
            if (! planFile.canWrite()) {
                log.error ("Error opening temp file \"" + planFilePath + "\" for writing: file is not writeable.");
                return null;
            }
        } else {
            if (! planFile.getParentFile().canWrite()) {
                log.error ("Error opening temp file \"" + planFilePath + "\" for writing: parent folder is not writeable.");
                return null;
            }
        }        
        
        try {
            FileWriter fw = new FileWriter (planFile, false);
            fw.write (planText);
            fw.flush();
            fw.close();
        } catch (Exception e) {
            log.error ("Error writing temp file \"" + planFilePath + "\": " + e.getMessage());
            return null;
        }
        return planFilePath;
    }

    /**
     * <p>
     * Examines the module XML files and updates their XML namespaces. In the 2014-03-12 release of PDTool, the
     * XML namespace used was changed from "www.compositesw.com" to "www.dvbu.cisco.com". An bug in PDTool also
     * used the incorrect namespace "www.cisco.dvbu.com".
     * </p>
     *
     * @return         The number of errors encountered.
     */
    public static int updateModuleXmlFiles() {
        int errorCount = 0;

        log.debug ("Looking for module XML files still using the Composite namespace ... ");
        log.debug ("PDTool home = " + pdtHome);
        
        if (pdtHome == null || pdtHome.length() == 0) {
            log.error ("System property apps.install.dir not set!");
            return 1;
        }

        // retrieve the list of files from the default file location for the file type
        //
        File dir = new File (pdtHome + "/" + MODULES_REL_PATH);
        
        Collection<File> fileNames = FileUtils.listFiles (
                                         dir, 
                                         new RegexFileFilter (".*\\.xml$"), 
                                         DirectoryFileFilter.DIRECTORY
                                     );

        for (File f : fileNames) {
            log.debug ("Updating " + f.getAbsolutePath());

            try {

                FileInputStream fis = new FileInputStream (f);
                String content = IOUtils.toString (fis, (String) null); // casting null as String to remove ambiguous reference.
                fis.close();

                content = content.replaceAll ("www.composite.com", "www.dvbu.cisco.com");
                content = content.replaceAll ("www.cisco.dvbu.com", "www.dvbu.cisco.com");

                FileOutputStream fos = new FileOutputStream (f);
                IOUtils.write(content, fos, (String) null); // casting null as String to remove ambiguous reference.
                fos.close();

            } catch (Exception e) {
                log.error ("Error encountered updating file " + f.getAbsolutePath() + ": " + e.getMessage());
                errorCount++;
            }
        }
        
        return errorCount;
    }

    /*
     * Supplemental methods.
     */
    private void getFiles (int fileType) {
        List<ListResult.Row> tmpFilesList = new ArrayList<ListResult.Row>();
        Map<String, FileRecord> tmpFilesMap = new HashMap<String, FileRecord>();
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        
        log.debug ("PDTool home = " + pdtHome);
        
        if (pdtHome == null || pdtHome.length() == 0) {
            log.error ("System property apps.install.dir not set!");
            return;
        }
        
        if (fileType < 0 || fileType >= fileTypeProperties.size() || fileTypeProperties.get (fileType) == null) {
            log.warn ("Attempted to retrieve files for non-existent file type: " + fileType);
            return;
        }
        
        log.debug ("getting recursive directory list of \"" + pdtHome + "/" + fileTypeProperties.get (fileType).defaultFilesLocation + "\"");
        
        // retrieve the list of files from the default file location for the file type
        //
        File dir = new File (pdtHome + "/" + fileTypeProperties.get (fileType).defaultFilesLocation);
        
        Collection<File> fileNames = FileUtils.listFiles (
                                         dir, 
                                         new RegexFileFilter (".*\\." + fileTypeProperties.get (fileType).suffix + "$"), 
                                         DirectoryFileFilter.DIRECTORY
                                     );

        if (fileNames != null) {
            String formattedDate;

            for (Iterator<File> itr = fileNames.iterator(); itr.hasNext(); ) {
                File f = itr.next();
                log.debug ("found file \"" + f.getPath() + "\"");
                try {
                    
                    // search the file for the title comment on the second line
                    //
                    BufferedReader br = new BufferedReader (new FileReader (f));
                    String line = null;
                    
                    // skip the correct number of lines for the file type
                    //
                    for (int i = 0; i < fileTypeProperties.get (fileType).matchLineNumber; i++)
                        line = br.readLine();

                    br.close();
                    
                    // make sure the line is not null and matches the pattern for the file type.
                    //
                    if (line != null && line.matches (fileTypeProperties.get (fileType).matchRE)) {
                        log.debug ("found " + f.getPath());

                        List<String> tmpCell = new ArrayList<String>();
                        FileRecord fileRecord = new FileRecord();
                        
                        String name = StringUtils.removeFileSuffix (f.getName());
                        log.debug ("name = " + name);

                        tmpCell.add (name);
                        fileRecord.setName (name);
                        
                        tmpCell.add (f.getPath());
                        fileRecord.setPath (f.getPath());

                        formattedDate = sdf.format (new Date (f.lastModified()));
                        tmpCell.add (formattedDate);
                        fileRecord.setDateModified (formattedDate);
    
                        tmpFilesList.add (new ListResult.Row (fileRecord.getPath(), tmpCell));
                        tmpFilesMap.put (fileRecord.getPath(), fileRecord);
                    } else {
                        log.debug ("file \"" + f.getPath() + "\" does not match criteria.");
                    }
                } catch (Exception e) {
                    log.error (e.getMessage());
                    continue;
                }
            }
        }

        this.filesLists.set (fileType, tmpFilesList);
        this.filesMaps.set (fileType, tmpFilesMap);

        return;
    }

    // validates an incoming deployment configuration name payload based on the requested operation
    //
    private List<ResultMessage.MessageItem> validateFile (
        FileRecord fileRecord,
        int operation
    ) {
        List<ResultMessage.MessageItem> result = new ArrayList<ResultMessage.MessageItem>();
        log.debug ("validate:\n  name = " + fileRecord.getName() + "\n  path = " + fileRecord.getPath() + "\n  type = " + fileRecord.getFileType());
        
        if (fileRecord.getName() == null || fileRecord.getName().length() == 0) {
            result.add (new ResultMessage.MessageItem ("name", fileTypeProperties.get (fileRecord.getFileType()).nameLabel + " may not be empty."));
        } else if (operation == DAOConstants.OPERATION_ADD || operation == DAOConstants.OPERATION_EDIT){

            // path isn't set on add (yet) so we construct it here.
            //
            String path = pdtHome + "/" + fileTypeProperties.get (fileRecord.getFileType()).defaultFilesLocation + "/" + fileRecord.getName();

            if (filesMaps.get (fileRecord.getFileType()).get (path) != null) {
                result.add (new ResultMessage.MessageItem ("name", fileTypeProperties.get (fileRecord.getFileType()).nameLabel +" \"" + path + "\" already exists."));
            }
        } else if (operation == DAOConstants.OPERATION_EDIT || operation == DAOConstants.OPERATION_COPY || operation == DAOConstants.OPERATION_DELETE){

            // path isn't set on edit (yet) so we construct it here.
            //
            String path = (operation == DAOConstants.OPERATION_EDIT) 
                              ? pdtHome + "/" + fileTypeProperties.get (fileRecord.getFileType()).defaultFilesLocation + "/" + fileRecord.getOrigname()
                              : fileRecord.getPath();

            if (filesMaps.get (fileRecord.getFileType()).get (path) == null) {
                result.add (new ResultMessage.MessageItem ("path", fileTypeProperties.get (fileRecord.getFileType()).nameLabel + " \"" + path + "\" does not exist."));
            }
        }
        
        return result;
    }
    
    // yeah, yeah. not good practice to make these attributes accessible (even if it's package protected), but I can't
    // be bothered to make and document accessors for this internal class that's only ever used within the parent.
    //
    public static class FileTypeProperty {
        String nameLabel;
        String template;
        String defaultFilesLocation;
        String suffix;
        int matchLineNumber;
        String matchRE;

        FileTypeProperty(
            String nameLabel,
            String template,
            String defaultFilesLocation,
            String suffix,
            int matchLineNumber,
            String matchRE
        ) {
            this.nameLabel = nameLabel;
            this.template = template;
            this.defaultFilesLocation = defaultFilesLocation;
            this.suffix = suffix;
            this.matchLineNumber = matchLineNumber;
            this.matchRE = matchRE;
        }

        public void setNameLabel (String nameLabel) {
            this.nameLabel = nameLabel;
        }

        public String getNameLabel () {
            return nameLabel;
        }

        public void setTemplate (String template) {
            this.template = template;
        }

        public String getTemplate () {
            return template;
        }

        public void setDefaultFilesLocation (String defaultFilesLocation) {
            this.defaultFilesLocation = defaultFilesLocation;
        }

        public String getDefaultFilesLocation () {
            return defaultFilesLocation;
        }

        public void setSuffix (String suffix) {
            this.suffix = suffix;
        }

        public String getSuffix () {
            return suffix;
        }

        public void setMatchLineNumber (int matchLineNumber) {
            this.matchLineNumber = matchLineNumber;
        }

        public int getMatchLineNumber () {
            return matchLineNumber;
        }

        public void setMatchRE (String matchRE) {
            this.matchRE = matchRE;
        }

        public String getMatchRE () {
            return matchRE;
        }
    }
}

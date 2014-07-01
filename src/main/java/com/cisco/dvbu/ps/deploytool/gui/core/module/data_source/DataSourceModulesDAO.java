package com.cisco.dvbu.ps.deploytool.gui.core.module.data_source;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfile;
import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.Preferences;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.PreferencesManager;
import com.cisco.dvbu.ps.deploytool.gui.core.config.server.Server;
import com.cisco.dvbu.ps.deploytool.gui.core.config.server.ServersDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.execute.ExecutePDTool;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.file.FilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.log.LogsDAO;
import com.cisco.dvbu.ps.deploytool.gui.resources.DataSourceModuleResource;

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
 * Data access object for data source modules. Used by {@link DataSourceModuleResource} servlet.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class DataSourceModulesDAO {
    
    private Map<String, ListColumnInfo> listColumnsInfo = new HashMap<String, ListColumnInfo>();

    private static final Logger log = LoggerFactory.getLogger (DataSourceModulesDAO.class);

    private XMLOutputter xmlo = new XMLOutputter();

    public DataSourceModulesDAO () {
        super ();
        
        // set up the column list for server lists
        //
        listColumnsInfo.put ("id",              new ListColumnInfo (0, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("type",            new ListColumnInfo (1, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("resourcePath",    new ListColumnInfo (2, ListResultRowComparator.SORT_TYPE_STRING));
    }

    /*
     * Servlet methods
     */

    /**
     * <p>
     * Sorts and filters the list of {@link DataSource} objects and returns a {@link ListResult} object
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
     * @see                DataSourceModuleResource
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
        
        log.debug ("Getting list of data sources in " + path);
        DataSourceModule dsm = findByPath (path);

        if (dsm == null) {
            log.error ("parsed data source module is null.");
            return null;
        }
        
        // get current list of data source modules in display list form
        //
        resultList = getDataSourceList (dsm);
        
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
        
        log.debug ("numRows = " + numRows + "; pageNum = " + pageNum);
        
        return ListUtils.getListResult (
                   resultList, 
                   numRows, 
                   pageNum,
                   param
               );
    }
    
    /**
     * <p>
     * Searches for a data source module based on a URL encoded path and populates a {@link DataSourceModule} object.
     * </p>
     * 
     * @param  path  The URL encoded ID to look for.
     * @return       The requested data source module as a {@link DataSourceModule} object.
     * @see          DataSourceModuleResource
     */
    public DataSourceModule findByPath (String path) {
        DataSourceModule dsm = new DataSourceModule();
        File f;
        SAXBuilder builder = new SAXBuilder();
        Document dsmDoc;
        
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
        
        dsm.setPath (path);
        
        try {
            dsmDoc = builder.build (path);
            Element rootNode = dsmDoc.getRootElement();
//            Namespace ns = dsmDoc.getNamespacesInScope().get(0); // should only be one namespace
            List<DataSource> dataSources = new ArrayList<DataSource>();
            List<Element> dsmList = rootNode.getChildren();
            
            log.debug ("Located " + dsmList.size() + " child elements.");

            // iterate over all the "data source" elements
            //
            log.debug ("Iterating over child elements.");
            for (Element dsmChild : dsmList) {
                dataSources.add (new DataSource (dsmChild));
            }
            
            dsm.setDataSources (dataSources);

        } catch (IOException io) {
            log.error ("getById() unable to load " + path + ":" + io.getMessage());
        } catch (JDOMException jdomex) {
            log.error ("getById() unable to load " + path + ":" + jdomex.getMessage());
        }

        return dsm;
    }
    
    /**
     * <p>
     * Writes the data source module to disk as an XML file.
     * </p>
     * 
     * @param  dsm  The URL encoded ID to look for.
     */
    public void serialize (DataSourceModule dsm) throws Exception {
        if (dsm.getPath() == null)
            throw new IllegalArgumentException ("Data source module's path may not be null.");
        
        File f = new File (dsm.getPath());

        // if user preferences indicate to save backups of edited files, back up the original file.
        //
        if (PreferencesManager.getInstance().getBackupFiles().equals ("true")) {
            FileUtils.copyFile (f, new File (dsm.getPath() + ".bak"));
        }
        
        Document dsmDoc = new Document (new Element ("DatasourceModule", "ns1", DAOConstants.MODULES_NS));
        dsmDoc.getRootElement().addContent ("\n");
        
        String indent = StringUtils.getIndent (1);
        
        if (dsm.getDataSources() != null) {
            for (DataSource ds : dsm.getDataSources()) {
                Element dsNode = ds.toElement ("datasource", 2).addContent ("\n" + indent);

                dsmDoc.getRootElement().addContent (indent);
                dsmDoc.getRootElement().addContent (dsNode);
                dsmDoc.getRootElement().addContent ("\n");
            }
        }
        
        xmlo.output (dsmDoc, new FileOutputStream (dsm.getPath()));
    }
    
    /**
     * <p>
     * Contacts a CIS instance, generates requested data sources, and merges them with the specified data source module.
     * </p>
     * 
     * @param  path         The URL encoded module to look for.
     * @param  profilePath  The path to the deployment profile.
     * @param  serverId     The ID of the server to generate from.
     * @param  startPath    The starting path to look for data sources.
     * @return              A {@link ResultMessage} with the result of the operation.
     */
    public ResultMessage generate (
        String path,
        String profilePath,
        String serverId,
        String startPath
    ) {
        ResultMessage result;
        List<ResultMessage.MessageItem> validationItems = new ArrayList<ResultMessage.MessageItem>();
        DataSourceModule dsm;
        Server s;

        log.debug ("generate received path = \"" + path + "\", profilePath = \"" + profilePath + "\", serverId = \"" + serverId + "\", startPath = \"" + startPath + "\".");
        
        // validate inputs
        //
        log.debug ("validating inputs ...");

        if (path == null || path.length() == 0) {
            validationItems.add (new ResultMessage.MessageItem ("id", "Data source module path may not be null!"));
        } else {
            dsm = findByPath (path);
            if (dsm == null) {
                validationItems.add (new ResultMessage.MessageItem ("id", "Data source module, " + path + ", does not exist!"));
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
            validationItems.add (new ResultMessage.MessageItem ("startPath", "Starting path may not be null!"));
        }

        log.debug ("done.");

        log.debug ("validationItems.size() = " + validationItems.size());
        if (validationItems.size() > 0) {
            return new ResultMessage ("error", null, validationItems);
        }

        // write out temporary generate plan
        //
        String generatedXmlFileName = "tmpDataSources_" + System.currentTimeMillis() + ".xml";
        String planPath = writeGenerationPlan (serverId, startPath, generatedXmlFileName);
        if (planPath == null) {
            return new ResultMessage ("error", "Unable to write out temporary data source generation plan!", null);
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
            return new ResultMessage ("error", "Unable to launch PDTool command line to generate data sources!", null);
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
        
        // make sure the data source xml got created.
        //
        String generatedXmlPath = FilesDAO.getPdtHome() + "/resources/modules/" + generatedXmlFileName;
        if (! (new File (generatedXmlPath).exists())) {
            return new ResultMessage ("error", "Data sources did not get generated! Please see latest PDTool log for more information.", null);
        }
        
        // merge result module with input module
        //
        log.debug ("merging generated data sources with original data sources ...");
        DataSourceModule mergedDsm = mergeDataSourceModules (path, generatedXmlPath);
        
        // delete the generated data source module
        //
        log.debug ("deleting generated data sources file ...");
        (new File (generatedXmlPath)).delete();

        // write out the merged data sources module
        //
        log.debug ("writing out merged data sources ...");
        try {
            serialize (mergedDsm);
            result = new ResultMessage ("success", "Data sources from server " + serverId + ", startPath " + startPath + " successfully added.", null);
        } catch (Exception e) {
            result = new ResultMessage ("error", "Unable serialize merged original and generated data sources!", null);
        }
        
        log.debug ("done.");
        return result;
    }
    
    /**
     * <p>
     * Returns a {@link DataSource} object containing the list of default data source types or a specific data
     * source type's attributes for the current default server. These types and attributes are updated when the
     * user updates his/her default server on the preferences page.
     * </p>
     * 
     * @param dsTypeName   The data source type to look for. If null, then return the list of data source types.
     * @return             The data source types or attributes in a list contained in a {@link DataSource} object.
     */
    public DataSource getDsTypesOrAttrs (
        String dsTypeName
    ) {
        DataSource ds = null;
        DataSourceModule dsm = null;

        // user wants data source types list
        //
        if (dsTypeName == null || dsTypeName.length() == 0) {
            log.debug ("Getting list of data source types");

            dsm = findByPath (FilesDAO.getPdtHome() + "/" + DAOConstants.DS_TYPES_REL_PATH);

        // user wants data source's attributes list
        //
        } else {
            log.debug ("Getting list of data source attributes for " + dsTypeName);

            // get the data source types list
            //
            ds = getDsTypesOrAttrs (null);
            
            if (ds != null && ds.getDataSourceTypes() != null) {
                List<DataSource.DataSourceType> typesList = ds.getDataSourceTypes();
                String dsType = "";
                

                log.debug ("Locating data source type for " + dsTypeName);

                // find data source type from data source type name
                //
                for (DataSource.DataSourceType dst : typesList) {
                    if (dst.getName().equals (dsTypeName)) {
                        log.debug ("Found data source type for " + dsTypeName + ": " + dst.getType());

                        dsType = dst.getType();
                        break;
                    }
                }
                
                dsType = dsType.replaceAll ("[\\(\\)]", ""); // MsExcel(POI) is a type. >:(
    
                log.debug ("Data source type is " + dsType);

                // get the list of attributes for the data source type
                //
                dsm = findByPath (FilesDAO.getPdtHome() + "/" + DAOConstants.DS_ATTRIBUTES_REL_PATH.replaceAll ("<TYPE>", dsType));
            }
        }
        
        if (dsm == null || dsm.getDataSources() == null || dsm.getDataSources().size() == 0)
            return null;
        
        if (dsTypeName == null) {
            log.debug ("Located data source types");
        } else {
            log.debug ("Located attributes for data source type " + dsTypeName);
        }

        return dsm.getDataSources().get (0);
    }

    // assembles the list result row list
    //
    private List<ListResult.Row> getDataSourceList (DataSourceModule dsm) {
        List<ListResult.Row> dsl = new ArrayList<ListResult.Row>();
        
        if (dsm.getDataSources() != null && dsm.getDataSources().size() > 0) {
            for (DataSource ds : dsm.getDataSources()) {

                // don't display attribute defs or data source types. users don't need to see or edit them.
                //
                if (ds.getType() == DataSource.TYPE_ATTRIBUTE_DEFS || ds.getType() == DataSource.TYPE_DATA_SOURCE_TYPES)
                    continue;

                List<String> cell = new ArrayList<String>();
                
                cell.add (ds.getId());
                cell.add (ds.getResourcePath());
                cell.add (DataSource.TYPE_LABELS[ds.getType()]);
                
                dsl.add (new ListResult.Row (ds.getId(), cell));
            }
        }
                
        return dsl;
    }
    
    // writes out a temporary data source generation plan
    //
    private String writeGenerationPlan (
        String serverId,
        String startPath,
        String generatedXmlPath
    ) {
        return FilesDAO.writeTempGenerationPlan(
            "PASS	TRUE	ExecuteAction	generateDataSourcesXML	\"" + serverId + "\"	\"" + startPath + "\"	\"$MODULE_HOME/" + generatedXmlPath + "\"	\"$MODULE_HOME/servers.xml\""
        );
    }
    
    private DataSourceModule mergeDataSourceModules (
        String origPath,
        String toMergePath
    ) {
        DataSourceModule origDsm = findByPath (origPath);
        DataSourceModule toMergeDsm = findByPath (toMergePath);
        int i;
        
        for (DataSource ds : toMergeDsm.getDataSources()) {
            
            for (DataSource ods : origDsm.getDataSources()) {
                i = 1;
                
                while (ds.getId().equals (ods.getId())) {
                    ds.setId (ods.getId() + "-" + i++);
                }
            }
        }
        
        origDsm.getDataSources().addAll(toMergeDsm.getDataSources());
        
        return origDsm;        
    }
}

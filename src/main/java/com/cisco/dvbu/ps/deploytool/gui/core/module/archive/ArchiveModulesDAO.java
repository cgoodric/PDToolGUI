package com.cisco.dvbu.ps.deploytool.gui.core.module.archive;

import com.cisco.dvbu.ps.deploytool.gui.PDToolGUIConfiguration;
import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfile;
import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.Preferences;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.PreferencesManager;
import com.cisco.dvbu.ps.deploytool.gui.resources.ArchiveModuleResource;

import com.cisco.dvbu.ps.deploytool.gui.util.DAOConstants;
import com.cisco.dvbu.ps.deploytool.gui.util.ListColumnInfo;
import com.cisco.dvbu.ps.deploytool.gui.util.ListResult;
import com.cisco.dvbu.ps.deploytool.gui.util.ListResultRowComparator;
import com.cisco.dvbu.ps.deploytool.gui.util.ListUtils;

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
 * Data access object for archive modules. Used by {@link ArchiveModuleResource} servlet.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class ArchiveModulesDAO {
    
    private Map<String, ListColumnInfo> listColumnsInfo = new HashMap<String, ListColumnInfo>();

    private static final Logger log = LoggerFactory.getLogger (ArchiveModulesDAO.class);

    private PDToolGUIConfiguration conf = new PDToolGUIConfiguration();
    private String indent; //, indentX2, indentX3, indentX4;
    private XMLOutputter xmlo = new XMLOutputter();

    public ArchiveModulesDAO () {
        super ();
        
        // set up the column list for archive lists
        //
        listColumnsInfo.put ("id",              new ListColumnInfo (0, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("pkgName",         new ListColumnInfo (1, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("archiveFileName", new ListColumnInfo (2, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("description",     new ListColumnInfo (3, ListResultRowComparator.SORT_TYPE_STRING));
        
        indent = "";
        for (int i = 0; i < conf.getXmlIndentWidth(); i++)
            indent += " ";
        
//        indentX2 = indent + indent;
//        indentX3 = indent + indent + indent;
//        indentX4 = indent + indent + indent + indent;
    }

    /*
     * Servlet methods
     */

    /**
     * <p>
     * Sorts and filters the list of {@link Archive} objects and returns a {@link ListResult} object
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
     * @see                ArchiveModuleResource
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
        
        log.debug ("Getting list of archives in " + path);
        ArchiveModule am = findByPath (path);

        if (am == null) {
            log.error ("parsed archive module is null.");
            return null;
        }
        
        // get current list of archive modules in display list form
        //
        resultList = getArchiveList (am);
        
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
     * Searches for an archive module based on a URL encoded path and populates an {@link ArchiveModule} object.
     * </p>
     * 
     * @param  path  The URL encoded ID to look for.
     * @return       The requested archive module as an {@link ArchiveModule} object.
     * @see          ArchiveModuleResource
     */
    public ArchiveModule findByPath (String path) {
        ArchiveModule am = new ArchiveModule();
        File f;
        SAXBuilder builder = new SAXBuilder();
        Document amDoc;
        
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
        
        am.setPath (path);
        
        try {
            amDoc = builder.build (path);
            Element rootNode = amDoc.getRootElement();
//            Namespace ns = amDoc.getNamespacesInScope().get(0); // should only be one namespace
            List<Archive> archives = new ArrayList<Archive>();
            List<Element> amList = rootNode.getChildren();
            
            log.debug ("Located " + amList.size() + " child elements.");
            // iterate over all the "archive" elements
            //
            log.debug ("Iterating over child elements.");
            for (Element amChild : amList) {
                archives.add (new Archive (amChild));
            }
            
            am.setArchives (archives);

        } catch (IOException io) {
            log.error ("getById() unable to load " + path + ":" + io.getMessage());
        } catch (JDOMException jdomex) {
            log.error ("getById() unable to load " + path + ":" + jdomex.getMessage());
        }

        return am;
    }
    
    /**
     * <p>
     * Writes the archive module to disk as an XML file.
     * </p>
     * 
     * @param  am  The archive module to write out.
     */
    public void serialize (ArchiveModule am) throws Exception {
        if (am.getPath() == null)
            throw new IllegalArgumentException ("Archive module's path may not be null.");
        
        File f = new File (am.getPath());

        // if user preferences indicate to save backups of edited files, back up the original file.
        //
        if (PreferencesManager.getInstance().getBackupFiles().equals ("true")) {
            FileUtils.copyFile (f, new File (am.getPath() + ".bak"));
        }
        
        Document amDoc = new Document (new Element ("ArchiveModule", "ns1", DAOConstants.MODULES_NS));
        amDoc.getRootElement().addContent ("\n");
        
        if (am.getArchives() != null) {
            for (Archive a : am.getArchives()) {
                Element aNode = a.toElement ("archive", 2).addContent ("\n" + indent);

                amDoc.getRootElement().addContent (indent);
                amDoc.getRootElement().addContent (aNode);
                amDoc.getRootElement().addContent ("\n");
            }
        }
        
        xmlo.output (amDoc, new FileOutputStream (am.getPath()));
    }
    
    // assembles the list result row list
    //
    private List<ListResult.Row> getArchiveList (ArchiveModule am) {
        List<ListResult.Row> al = new ArrayList<ListResult.Row>();
        
        if (am.getArchives() != null && am.getArchives().size() > 0) {
            for (Archive a : am.getArchives()) {
                List<String> cell = new ArrayList<String>();
                
                cell.add (a.getId());
                cell.add (a.getPkgName());
                cell.add (a.getArchiveFileName());
                cell.add (a.getDescription());
                
                al.add (new ListResult.Row (a.getId(), cell));
            }
        }
        
        return al;
    }
}

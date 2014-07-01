package com.cisco.dvbu.ps.deploytool.gui.core.module.regression;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfile;
import com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_profile.DeploymentProfilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.Preferences;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.PreferencesManager;
import com.cisco.dvbu.ps.deploytool.gui.resources.RegressionModuleResource;

import com.cisco.dvbu.ps.deploytool.gui.util.DAOConstants;
import com.cisco.dvbu.ps.deploytool.gui.util.ListColumnInfo;
import com.cisco.dvbu.ps.deploytool.gui.util.ListResult;
import com.cisco.dvbu.ps.deploytool.gui.util.ListResultRowComparator;
import com.cisco.dvbu.ps.deploytool.gui.util.ListUtils;
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
 * Data access object for rebind modules. Used by {@link RegressionModuleResource} servlet.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class RegressionModulesDAO {

    private Map<String, ListColumnInfo> listColumnsInfo = new HashMap<String, ListColumnInfo>();

    private static final Logger log = LoggerFactory.getLogger (RegressionModulesDAO.class);

    private XMLOutputter xmlo = new XMLOutputter();

    public RegressionModulesDAO () {
        super ();
        
        // set up the column list for server lists
        //
        listColumnsInfo.put ("id",              new ListColumnInfo (0, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("type",            new ListColumnInfo (1, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("inputFilePath",   new ListColumnInfo (2, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("datasource",      new ListColumnInfo (3, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("query",           new ListColumnInfo (4, ListResultRowComparator.SORT_TYPE_STRING));
    }

    /*
     * Servlet methods
     */

    /**
     * <p>
     * Sorts and filters the list of {@link Regression} objects and returns a {@link ListResult} object
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
     * @see                RegressionModuleResource
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
        
        log.debug ("Getting list of reregressions in " + path);
        RegressionModule rm = findByPath (path);

        if (rm == null) {
            log.error ("parsed regression module is null.");
            return null;
        }
        
        // get current list of regression modules in display list form
        //
        resultList = getRegressionList (rm);
        
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
     * Searches for a regression module based on a URL encoded path and populates a {@link RegressionModule} object.
     * </p>
     * 
     * @param  path  The URL encoded ID to look for.
     * @return       The requested regression module as a {@link RegressionModule} object.
     * @see          RegressionModuleResource
     */
    public RegressionModule findByPath (String path) {
        RegressionModule rm = new RegressionModule();
        File f;
        SAXBuilder builder = new SAXBuilder();
        Document rmDoc;
        
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
        
        rm.setPath (path);
        
        try {
            rmDoc = builder.build (path);
            Element rootNode = rmDoc.getRootElement();
//            Namespace ns = rmDoc.getNamespacesInScope().get(0); // should only be one namespace
            List<Regression> regressions = new ArrayList<Regression>();
            List<Element> rmList = rootNode.getChildren();
            
            log.debug ("Located " + rmList.size() + " child elements.");

            // iterate over all the "regressionTest" and "regressionQuery" elements
            //
            log.debug ("Iterating over child elements.");
            for (Element rmChild : rmList) {
                
                // regression queries are grouped into a parent element, so iterate over all its
                // children to get the regression queries.
                //
                if (rmChild.getName().equals ("regressionQueries")) {
                    for (Element rqChild : rmChild.getChildren()) {
                        if (rqChild.getName().equals ("regressionQuery"))
                            regressions.add (new Regression (rqChild));
                    }
                    
                // regression security elements are grouped into a parent element, so iterate over
                // all the children and subchildren to get the regression security elements.
                //
                } else if (rmChild.getName().equals ("regressionSecurity")) {
                    for (Element rsNode : rmChild.getChildren()) {
                        if (rsNode.getName().equals ("regressionSecurityUsers")) {
                            for (Element rsuNode : rsNode.getChildren()) {
                                regressions.add (new Regression (rsuNode));
                            }
                        }
        
                        if (rsNode.getName().equals ("regressionSecurityQueries")) {
                            for (Element rsqNode : rsNode.getChildren()) {
                                regressions.add (new Regression (rsqNode));
                            }
                        }
        
                        if (rsNode.getName().equals ("regressionSecurityPlans")) {
                            for (Element rspNode : rsNode.getChildren()) {
                                regressions.add (new Regression (rspNode));
                            }
                        }
                    }
                } else {
                    regressions.add (new Regression (rmChild));
                }
            }
            
            rm.setRegressions (regressions);

        } catch (IOException io) {
            log.error ("getById() unable to load " + path + ":" + io.getMessage());
        } catch (JDOMException jdomex) {
            log.error ("getById() unable to load " + path + ":" + jdomex.getMessage());
        }

        return rm;
    }
    
    /**
     * <p>
     * Writes the regression module to disk as an XML file.
     * </p>
     * 
     * @param  rm  The {@link RegressionModule} to write out.
     */
    public void serialize (RegressionModule rm) throws Exception {
        if (rm.getPath() == null)
            throw new IllegalArgumentException ("Regression module's path may not be null.");
        
        File f = new File (rm.getPath());

        // if user preferences indicate to save backups of edited files, back up the original file.
        //
        if (PreferencesManager.getInstance().getBackupFiles().equals ("true")) {
            FileUtils.copyFile (f, new File (rm.getPath() + ".bak"));
        }
        
        Document rmDoc = new Document (new Element ("RegressionModule", "ns1", DAOConstants.MODULES_NS));
        rmDoc.getRootElement().addContent ("\n");
        
        String indentStr = StringUtils.getIndent (1);
        String indentStr2 = StringUtils.getIndent (2);
        String indentStr3 = StringUtils.getIndent (3);
        
        if (rm.getRegressions() != null) {
            boolean hasQueries = false;
            boolean hasSecurityUsers = false;
            boolean hasSecurityQueries = false;
            boolean hasSecurityPlans = false;
            
            // write out the regression tests first
            //
            for (Regression r : rm.getRegressions()) {
                switch (r.getType()) {
                    case Regression.TYPE_TEST:
                        Element rNode = r.toElement ("regressionTest", 2).addContent ("\n" + indentStr);
        
                        rmDoc.getRootElement().addContent (indentStr);
                        rmDoc.getRootElement().addContent (rNode);
                        rmDoc.getRootElement().addContent ("\n");
                        
                        break;

                    case Regression.TYPE_QUERY:
                        hasQueries = true;
                        break;

                    case Regression.TYPE_SECURITY_USER:
                        hasSecurityUsers = true;
                        break;

                    case Regression.TYPE_SECURITY_QUERY:
                        hasSecurityQueries = true;
                        break;

                    case Regression.TYPE_SECURITY_PLAN:
                        hasSecurityPlans = true;
                        break;
                }
            }
            
            // next write out the queries, if any
            //
            if (hasQueries) {
                Element rqNode = new Element ("regressionQueries");
                
                for (Regression r : rm.getRegressions()) {
                    if (r.getType() == Regression.TYPE_QUERY) {
                        Element qNode = r.toElement ("regressionQuery", 3).addContent ("\n" + indentStr2);
    
                        rqNode.addContent ("\n" + indentStr2);
                        rqNode.addContent (qNode);
                    }
                }

                rmDoc.getRootElement().addContent (indentStr);
                rmDoc.getRootElement().addContent (rqNode.addContent ("\n" + indentStr));
                rmDoc.getRootElement().addContent ("\n");
            }
            
            // finally, write out the security elements, if any
            //
            if (hasSecurityUsers || hasSecurityQueries || hasSecurityPlans) {
                Element rsNode = new Element ("regressionSecurity");

                if (hasSecurityUsers) {
                    Element rsuNode = new Element ("regressionSecurityUsers");

                    for (Regression r : rm.getRegressions()) {
                        if (r.getType() == Regression.TYPE_SECURITY_USER) {
                            Element uNode = r.toElement ("regressionSecurityUser", 4).addContent ("\n" + indentStr3);
        
                            rsuNode.addContent ("\n" + indentStr3);
                            rsuNode.addContent (uNode);
                        }
                    }

                    rsNode.addContent ("\n" + indentStr2);
                    rsNode.addContent (rsuNode.addContent ("\n" + indentStr2));
                }

                if (hasSecurityQueries) {
                    Element rsqNode = new Element ("regressionSecurityQueries");

                    for (Regression r : rm.getRegressions()) {
                        if (r.getType() == Regression.TYPE_SECURITY_QUERY) {
                            Element qNode = r.toElement ("regressionSecurityQuery", 4).addContent ("\n" + indentStr3);
        
                            rsqNode.addContent ("\n" + indentStr3);
                            rsqNode.addContent (qNode);
                        }
                    }

                    rsNode.addContent ("\n" + indentStr2);
                    rsNode.addContent (rsqNode.addContent ("\n" + indentStr2));
                }

                if (hasSecurityPlans) {
                    Element rspNode = new Element ("regressionSecurityPlans");

                    for (Regression r : rm.getRegressions()) {
                        if (r.getType() == Regression.TYPE_SECURITY_PLAN) {
                            Element pNode = r.toElement ("regressionSecurityPlan", 4).addContent ("\n" + indentStr3);
        
                            rspNode.addContent ("\n" + indentStr3);
                            rspNode.addContent (pNode);
                        }
                    }

                    rsNode.addContent ("\n" + indentStr2);
                    rsNode.addContent (rspNode.addContent ("\n" + indentStr2));
                }

                rmDoc.getRootElement().addContent (indentStr);
                rmDoc.getRootElement().addContent (rsNode.addContent ("\n" + indentStr));
                rmDoc.getRootElement().addContent ("\n");
            }
        }
        
        xmlo.output (rmDoc, new FileOutputStream (rm.getPath()));
    }
    
    // assembles the list result row list
    //
    private List<ListResult.Row> getRegressionList (RegressionModule rm) {
        List<ListResult.Row> rl = new ArrayList<ListResult.Row>();
        
        if (rm.getRegressions() != null && rm.getRegressions().size() > 0) {
            for (Regression r : rm.getRegressions()) {
                String ifp = null;
                String ds = null;
                String q = null;
                
                switch (r.getType()) {
                    case Regression.TYPE_TEST:
                        ifp = r.getInputFilePath();
                        ds = "";
                        q = "";
                        break;
                    
                    case Regression.TYPE_QUERY:
                        ifp = "";
                        ds = r.getDatasource();
                        q = r.getQuery();
                        break;
                    
                    case Regression.TYPE_SECURITY_USER:
                        ifp = r.getUserName() + "@" + r.getDomain();
                        ds = "";
                        q = "";
                        break;
                    
                    case Regression.TYPE_SECURITY_QUERY:
                        ifp = "";
                        ds = r.getDatasource();
                        q = Regression.QUERY_TYPE_LABELS[r.getQueryType()] + ": " + r.getQuery();
                        break;
                    
                    case Regression.TYPE_SECURITY_PLAN:
                        ifp = "";
                        ds = "";
                        q = "";
                        break;
                }

                List<String> cell = new ArrayList<String>();
                
                cell.add (r.getId());
                cell.add (Regression.TYPE_LABELS[r.getType()]);
                cell.add (ifp);
                cell.add (ds);
                cell.add (q);
                
                rl.add (new ListResult.Row (r.getId(), cell));
            }
        }
                
        return rl;
    }
}

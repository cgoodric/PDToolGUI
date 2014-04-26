package com.cisco.dvbu.ps.deploytool.gui.core.module.regression;

import com.cisco.dvbu.ps.deploytool.gui.util.DAOConstants;
import com.cisco.dvbu.ps.deploytool.gui.resources.RegressionResource;
import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;

import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage.MessageItem;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Data access object for rebinds. Used by {@link RegressionResource} servlet.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class RegressionsDAO {

    private static final Logger log = LoggerFactory.getLogger (RegressionsDAO.class);
    private RegressionModulesDAO rmDao = new RegressionModulesDAO();

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public RegressionsDAO () {
        super ();
    }
    
    /**
     * <p>
     * Searches for a single {@link Regression} object given a URL encoded ID string.
     * </p>
     * 
     * @param  path  The URL encoded path to look for.
     * @param  id    The ID to look for.
     * @return       The requested {@link Regression} as an object.
     */
    public Regression findById (
        String path,
        String id
    ) {
        RegressionModule rm = rmDao.findByPath (path);
        if (rm == null) {
            log.error ("Unable to locate regression module " + path);
        }
        
        log.debug ("Regression module located. Looking for regression ID " + id);
        return rm.findById (id);
    }
  
    /**
     * <p>
     * Adds a regression to a regression module.
     * </p>
     * 
     * @param  path  The path to the regression module.
     * @param  r     The {@link Regression} to add.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage add (
        String path,
        Regression r
    ) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;
        
        RegressionModule rm = rmDao.findByPath (path);
        if (rm == null) {
            return new ResultMessage ("error", "Regression module, " + path + ", does not exist!", null);
        }
        
        msgList = validate (r, rm, DAOConstants.OPERATION_ADD);
        
        if (msgList.size() == 0) {
        
            rm.getRegressions().add (r);
        
            try {
                rmDao.serialize (rm);
                result = new ResultMessage ("success", "Created regression " + r.getId(), null);
            } catch (Exception e) {
                log.error ("Error serializing regression module XML file, " + path + ": " + e.getMessage());
                return new ResultMessage ("error", "Unable to serialize regression module, " + path + ": " + e.getMessage(), null);
            }
        } else {
            return new ResultMessage ("error", null, msgList);
        }
        
        return result;
    }
  
    /**
     * <p>
     * Updates a regression in a regression module.
     * </p>
     * 
     * @param  path  The path to the regression module.
     * @param  r     The {@link Regression} to update.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage edit (
        String path,
        Regression r
    ) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;
        Regression origRegression = null;
        
        RegressionModule rm = rmDao.findByPath (path);
        if (rm == null) {
            return new ResultMessage ("error", "Regression module, " + path + ", does not exist!", null);
        }
        
        for (Regression tmp : rm.getRegressions()) {
            if (tmp.getId().equals (r.getOrigId())) {
                origRegression = tmp;
                break;
            }
        }
        
        msgList = validate (r, rm, DAOConstants.OPERATION_EDIT);
        
        if (msgList.size() == 0) {
        
            rm.getRegressions().remove (origRegression);
            rm.getRegressions().add (r);
        
            try {
                rmDao.serialize (rm);
                result = new ResultMessage ("success", "Updated regression " + r.getId(), null);
            } catch (Exception e) {
                log.error ("Error serializing regression module XML file, " + path + ": " + e.getMessage());
                return new ResultMessage ("error", "Unable to serialize regression module, " + path + ": " + e.getMessage(), null);
            }
        } else {
            return new ResultMessage ("error", null, msgList);
        }
        
        return result;
    }
    
    /**
     * <p>
     * Copies one or more regressions in a regression module.
     * </p>
     * 
     * @param  path  The path to the regression module.
     * @param  ids   The a comma separated list of regression IDs to copy.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage copy (
        String path,
        String ids
    ) {
        List<ResultMessage.MessageItem> msgList = new ArrayList<ResultMessage.MessageItem>();
        boolean atLeastOneItemCopied = false;
        ResultMessage result;
        
        RegressionModule rm = rmDao.findByPath (path);
        if (rm == null) {
            return new ResultMessage ("error", "Regression module, " + path + ", does not exist!", null);
        }
        
        // iterate over all the input ids
        //
        String[] idArray = ids.split (",");
        for (String id : idArray) {
            List<ResultMessage.MessageItem> tmpMsgList;
            Regression r = new Regression();

            r.setId (id);
            
            // make sure the source item exists.
            //
            tmpMsgList = validate (r, rm, DAOConstants.OPERATION_COPY);
            
            // if so, then make the copy
            //
            if (tmpMsgList.size() == 0) {

                Regression nr = new Regression (rm.findById (r.getId())); // find the original archive and make a copy of it.
                
                if (nr == null) {
                    tmpMsgList.add (new ResultMessage.MessageItem ("id", "Unable to locate archive id \"" + r.getId() + "\"."));
                    continue;
                }
                
                atLeastOneItemCopied = true;

                // query id's are dynamically generated (there's no id field in the XML for them.) adding
                // a space to the end of the query string should(!) generate a new ID that doesn't conflict
                // with the old one.
                //
                if (nr.getType() == Regression.TYPE_QUERY) {
                    nr.setQuery(nr.getQuery() + " ");
                } else {
                    // generate a new id for the copy. start with the original id + "_copy", then look for
                    // id + "_copy1", id + "_copy2", etc.
                    //
                    int j = 0;
                    do {
                        String newId = r.getId() + "_copy" + ((j == 0) ? "" : j);
                        
                        nr.setId (newId);
    
                        j++;
                    } while (validate (nr, rm, DAOConstants.OPERATION_COPY).size() == 0);
                }

                // add the copy of the element
                //
                rm.getRegressions().add (nr);
                
                tmpMsgList.add (new ResultMessage.MessageItem ("id", "Regression ID \"" + r.getId() + "\" copied to \"" + nr.getId() + "\"."));
            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneItemCopied) {
            try {
                rmDao.serialize (rm);
                result = new ResultMessage ("success", null, msgList);
            } catch (Exception e) {
                log.error ("Error serializing regression module XML file: " + e.getMessage());
                result = new ResultMessage ("error", "Error serializing regression module XML file: " + e.getMessage(), null);
            }
        } else {
            result = new ResultMessage ("error", null, msgList);
        }

        return result;
    }
    
    /**
     * <p>
     * Deletes one or more regressions in a regression module.
     * </p>
     * 
     * @param  path  The path to the regression module.
     * @param  ids   The a comma separated list of regression IDs to delete.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage delete (
        String path,
        String ids
    ) {
        List<ResultMessage.MessageItem> msgList = new ArrayList<ResultMessage.MessageItem>();
        boolean atLeastOneItemDeleted = false;
        ResultMessage result;
        
        RegressionModule rm = rmDao.findByPath (path);
        if (rm == null) {
            return new ResultMessage ("error", "Regression module, " + path + ", does not exist!", null);
        }
        
        // iterate over all the input ids
        //
        String[] idArray = ids.split (",");
        for (String id : idArray) {
            List<ResultMessage.MessageItem> tmpMsgList;
            Regression r = new Regression();

            r.setId (id);
            
            // make sure the item exists.
            //
            tmpMsgList = validate (r, rm, DAOConstants.OPERATION_DELETE);
            
            // if so, then make sure the item doesn't :)
            //
            if (tmpMsgList.size() == 0) {
                atLeastOneItemDeleted = true;
                
                // get rid of the server element
                //
                r = rm.findById (r.getId());
                rm.getRegressions().remove (r);
                
                tmpMsgList.add (new ResultMessage.MessageItem ("id", "Regression ID \"" + r.getId() + "\" deleted."));
            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneItemDeleted) {
            try {
                rmDao.serialize (rm);
                result = new ResultMessage ("success", null, msgList);
            } catch (Exception e) {
                log.error ("Error serializing regression module XML file: " + e.getMessage());
                result = new ResultMessage ("error", "Error serializing regression module XML file: " + e.getMessage(), null);
            }
        } else {
            result = new ResultMessage ("error", null, msgList);
        }

        return result;
    }

    // validates an incoming server payload based on the requested operation
    //
    private List<ResultMessage.MessageItem> validate (
        Regression r,
        RegressionModule rm,
        int operation
    ) {
        List<ResultMessage.MessageItem> result = new ArrayList<ResultMessage.MessageItem>();
        
        log.debug ("validating: operation = " + operation + ", rm = " + rm.getPath() + ", r = " + r.getId());
        
        if (r.getId() == null || r.getId().length() == 0) {
            result.add (new ResultMessage.MessageItem ("id", "Regression ID may not be empty."));
        } else if (operation == DAOConstants.OPERATION_ADD){
            if (rm.findById (r.id) != null) {
                result.add (new ResultMessage.MessageItem ("id", "Regression ID \"" + r.getId() + "\" already exists."));
            }
        } else if (operation == DAOConstants.OPERATION_EDIT || operation == DAOConstants.OPERATION_COPY || operation == DAOConstants.OPERATION_DELETE){
            if (rm.findById (((operation == DAOConstants.OPERATION_EDIT) ? r.getOrigId() : r.getId())) == null) {
                result.add (new ResultMessage.MessageItem ("id", "Regression ID \"" + ((operation == DAOConstants.OPERATION_EDIT) ? r.getOrigId() : r.getId()) + "\" does not exist."));
                return result; // no need to go any further if the item doesn't exist.
            }
        }
        
        // no need to continue with copy and delete operations (a copied item has presumably been 
        // previously validated and we don't need validate an item we're deleting.)
        //
        if (operation == DAOConstants.OPERATION_COPY || operation == DAOConstants.OPERATION_DELETE)
            return result;
        
        if (r.getType() == Regression.TYPE_TEST && (r.getInputFilePath() == null || r.getInputFilePath().length() == 0)) {
            result.add (new ResultMessage.MessageItem ("inputFilePath", "Input File Path may not be empty for regresion tests."));
        }
        
        if (r.getType() == Regression.TYPE_QUERY && (r.getDatasource() == null || r.getDatasource().length() == 0)) {
            result.add (new ResultMessage.MessageItem ("datasource", "Datasource may not be empty for regression queries."));
        }
        
        if (r.getType() == Regression.TYPE_QUERY && (r.getQuery() == null || r.getQuery().length() == 0)) {
            result.add (new ResultMessage.MessageItem ("query", "Query may not be empty for regression queries."));
        }
        
        return result;
    }
}

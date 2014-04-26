package com.cisco.dvbu.ps.deploytool.gui.core.module.data_source;

import com.cisco.dvbu.ps.deploytool.gui.util.DAOConstants;
import com.cisco.dvbu.ps.deploytool.gui.resources.DataSourceModuleResource;
import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;

import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage.MessageItem;

import java.util.ArrayList;
import java.util.List;

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
public class DataSourcesDAO {

    private static final Logger log = LoggerFactory.getLogger (DataSourcesDAO.class);
    private DataSourceModulesDAO dsmDao = new DataSourceModulesDAO ();

    public DataSourcesDAO () {
        super ();
    }
    
    public DataSource findById (
        String path,
        String id
    ) {
        DataSourceModule dsm = dsmDao.findByPath (path);
        if (dsm == null) {
            log.error ("Unable to locate data source module " + path);
        }
        
        log.debug ("Data source module located. Looking for data source ID " + id);
        return dsm.findById (id);
    }
  
    public ResultMessage add (
        String path, 
        DataSource ds
    ) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;

        DataSourceModule dsm = dsmDao.findByPath (path);
        if (dsm == null) {
            return new ResultMessage ("error", "Data source module, " + path + ", does not exist!", null);
        }
        
        msgList = validate (ds, dsm, DAOConstants.OPERATION_ADD);
        
        if (msgList.size() == 0) {
        
            dsm.getDataSources().add (ds);
        
            try {
                dsmDao.serialize (dsm);
                result = new ResultMessage ("success", "Created data source " + ds.getId(), null);
            } catch (Exception e) {
                log.error ("Error serializing archive module XML file, " + path + ": " + e.getMessage());
                return new ResultMessage ("error", "Unable to serialize archive module, " + path + ": " + e.getMessage(), null);
            }
        } else {
            return new ResultMessage ("error", null, msgList);
        }
        
        return result;
    }
  
    public ResultMessage edit (
        String path, 
        DataSource ds
    ) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;
        DataSource origDS = null;

        DataSourceModule dsm = dsmDao.findByPath (path);
        if (dsm == null) {
            return new ResultMessage ("error", "Data source, " + path + ", does not exist!", null);
        }
        
        for (DataSource tmp : dsm.getDataSources()) {
            if (tmp.getId().equals (ds.getOrigId())) {
                origDS = tmp;
                break;
            }
        }
        
        msgList = validate (ds, dsm, DAOConstants.OPERATION_EDIT);
        
        if (msgList.size() == 0) {
        
            dsm.getDataSources().remove(origDS);
            dsm.getDataSources().add (ds);
        
            try {
                dsmDao.serialize (dsm);
                result = new ResultMessage ("success", "Updated data source " + ds.getId(), null);
            } catch (Exception e) {
                log.error ("Error serializing data source module XML file, " + path + ": " + e.getMessage());
                return new ResultMessage ("error", "Unable to serialize data source module, " + path + ": " + e.getMessage(), null);
            }
        } else {
            return new ResultMessage ("error", null, msgList);
        }
        
        return result;
    }
    
    public ResultMessage copy (
        String path,
        String ids
    ) {
        List<ResultMessage.MessageItem> msgList = new ArrayList<ResultMessage.MessageItem>();
        boolean atLeastOneItemCopied = false;
        ResultMessage result;

        DataSourceModule dsm = dsmDao.findByPath (path);
        if (dsm == null) {
            return new ResultMessage ("error", "Data source, " + path + ", does not exist!", null);
        }
        
        // iterate over all the input ids
        //
        String[] idArray = ids.split (",");
        for (String id : idArray) {
            List<ResultMessage.MessageItem> tmpMsgList;
            DataSource ds = new DataSource ();

            ds.setId (id);
            
            // make sure the source item exists.
            //
            tmpMsgList = validate (ds, dsm, DAOConstants.OPERATION_COPY);
            
            // if so, then make the copy
            //
            if (tmpMsgList.size() == 0) {

                DataSource nds = new DataSource (dsm.findById (ds.getId())); // find the original archive and make a copy of it.
                
                if (nds == null) {
                    tmpMsgList.add (new ResultMessage.MessageItem ("id", "Unable to locate data source id \"" + ds.getId() + "\"."));
                    continue;
                }
                
                atLeastOneItemCopied = true;

                // generate a new id for the copy. start with the original id + "_copy", then look for
                // id + "_copy1", id + "_copy2", etc.
                //
                int j = 0;
                do {
                    String newId = ds.getId() + "_copy" + ((j == 0) ? "" : j);
                    
                    nds.setId (newId);

                    j++;
                } while (validate (nds, dsm, DAOConstants.OPERATION_COPY).size() == 0);
                
                // add the copy of the element
                //
                dsm.getDataSources().add (nds);
                
                tmpMsgList.add (new ResultMessage.MessageItem ("id", "Data source ID \"" + ds.getId() + "\" copied to \"" + nds.getId() + "\"."));
            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneItemCopied) {
            try {
                dsmDao.serialize (dsm);
                result = new ResultMessage ("success", null, msgList);
            } catch (Exception e) {
                log.error ("Error serializing archive module XML file: " + e.getMessage());
                result = new ResultMessage ("error", "Error serializing archive module XML file: " + e.getMessage(), null);
            }
        } else {
            result = new ResultMessage ("error", null, msgList);
        }

        return result;
    }
    
    public ResultMessage delete (
        String path,
        String ids
    ) {
        List<ResultMessage.MessageItem> msgList = new ArrayList<ResultMessage.MessageItem>();
        boolean atLeastOneItemDeleted = false;
        ResultMessage result;

        DataSourceModule dsm = dsmDao.findByPath (path);
        if (dsm == null) {
            return new ResultMessage ("error", "Data source, " + path + ", does not exist!", null);
        }
        
        // iterate over all the input ids
        //
        String[] idArray = ids.split (",");
        for (String id : idArray) {
            List<ResultMessage.MessageItem> tmpMsgList;
            DataSource ds = new DataSource();

            ds.setId (id);
            
            // make sure the item exists.
            //
            tmpMsgList = validate (ds, dsm, DAOConstants.OPERATION_DELETE);
            
            // if so, then make sure the item doesn't :)
            //
            if (tmpMsgList.size() == 0) {
                atLeastOneItemDeleted = true;
                
                // get rid of the server element
                //
                ds = dsm.findById (ds.getId());
                dsm.getDataSources().remove (ds);
                
                tmpMsgList.add (new ResultMessage.MessageItem ("id", "Data source ID \"" + ds.getId() + "\" deleted."));
            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneItemDeleted) {
            try {
                dsmDao.serialize (dsm);
                result = new ResultMessage ("success", null, msgList);
            } catch (Exception e) {
                log.error ("Error serializing data source module XML file: " + e.getMessage());
                result = new ResultMessage ("error", "Error serializing data source module XML file: " + e.getMessage(), null);
            }
        } else {
            result = new ResultMessage ("error", null, msgList);
        }

        return result;
    }

    // validates an incoming server payload based on the requested operation
    //
    private List<ResultMessage.MessageItem> validate (
        DataSource ds, 
        DataSourceModule dsm,
        int operation
    ) {
        List<ResultMessage.MessageItem> result = new ArrayList<ResultMessage.MessageItem>();
        
        log.debug ("validating: operation = " + operation + ", dsm = " + dsm.getPath() + ", ds = " + ds.getId());
        
        if (ds.getId() == null || ds.getId().length() == 0) {
            result.add (new ResultMessage.MessageItem ("id", "Data source ID may not be empty."));
        } else if (operation == DAOConstants.OPERATION_ADD){
            if (dsm.findById (ds.getId()) != null) {
                result.add (new ResultMessage.MessageItem ("id", "Data source ID \"" + ds.getId() + "\" already exists."));
            }
        } else if (operation == DAOConstants.OPERATION_EDIT || operation == DAOConstants.OPERATION_COPY || operation == DAOConstants.OPERATION_DELETE){
            if (dsm.findById (((operation == DAOConstants.OPERATION_EDIT) ? ds.getOrigId() : ds.getId())) == null) {
                result.add (new ResultMessage.MessageItem ("id", "Data source ID \"" + ((operation == DAOConstants.OPERATION_EDIT) ? ds.getOrigId() : ds.getId()) + "\" does not exist."));
                return result; // no need to go any further if the item doesn't exist.
            }
        }
        
        // no need to continue with copy and delete operations (a copied item has presumably been 
        // previously validated and we don't need validate an item we're deleting.)
        //
        if (operation == DAOConstants.OPERATION_COPY || operation == DAOConstants.OPERATION_DELETE)
            return result;
        
        if (ds.getResourcePath() == null || ds.getResourcePath().length() == 0) {
            result.add (new ResultMessage.MessageItem ("resourcePath", "Resource path may not be empty."));
        }
        
        return result;
    }
}

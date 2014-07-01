package com.cisco.dvbu.ps.deploytool.gui.core.module.resource;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.cisco.dvbu.ps.deploytool.gui.resources.ResourceResource;
import com.cisco.dvbu.ps.deploytool.gui.util.DAOConstants;
import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Data access object for resources. Used by {@link ResourceResource} servlet.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class ResourcesDAO {

    private static final Logger log = LoggerFactory.getLogger (ResourcesDAO.class);
    private ResourceModulesDAO rmDao = new ResourceModulesDAO();

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public ResourcesDAO () {
        super ();
    }
    
    /**
     * <p>
     * Searches for a single {@link Resource} object given a URL encoded ID string.
     * </p>
     * 
     * @param  path  The URL encoded path to look for.
     * @param  id    The ID to look for.
     * @return       The requested {@link Resource} as an object.
     */
    public Resource findById (
        String path,
        String id
    ) {
        ResourceModule rm = rmDao.findByPath (path);
        if (rm == null) {
            log.error ("Unable to locate resource module " + path);
        }
        
        log.debug ("Resource module located. Looking for resource ID " + id);
        return rm.findById (id);
    }
  
    /**
     * <p>
     * Adds a resource to a resource module.
     * </p>
     * 
     * @param  path  The path to the resource module.
     * @param  r     The {@link Resource} to add.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage add (
        String path,
        Resource r
    ) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;
        
        ResourceModule rm = rmDao.findByPath (path);
        if (rm == null) {
            return new ResultMessage ("error", "Resource module, " + path + ", does not exist!", null);
        }
        
        msgList = validate (r, rm, DAOConstants.OPERATION_ADD);
        
        if (msgList.size() == 0) {
        
            rm.getResources().add (r);
        
            try {
                rmDao.serialize (rm);
                result = new ResultMessage ("success", "Created resource " + r.getId(), null);
            } catch (Exception e) {
                log.error ("Error serializing resource module XML file, " + path + ": " + e.getMessage());
                return new ResultMessage ("error", "Unable to serialize resource module, " + path + ": " + e.getMessage(), null);
            }
        } else {
            return new ResultMessage ("error", null, msgList);
        }
        
        return result;
    }
  
    /**
     * <p>
     * Updates a resource in a resource module.
     * </p>
     * 
     * @param  path  The path to the resource module.
     * @param  r     The {@link Resource} to update.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage edit (
        String path,
        Resource r
    ) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;
        Resource origResource = null;
        
        ResourceModule rm = rmDao.findByPath (path);
        if (rm == null) {
            return new ResultMessage ("error", "Resource module, " + path + ", does not exist!", null);
        }
        
        for (Resource tmp : rm.getResources()) {
            if (tmp.getId().equals (r.getOrigId())) {
                origResource = tmp;
                break;
            }
        }
        
        msgList = validate (r, rm, DAOConstants.OPERATION_EDIT);
        
        if (msgList.size() == 0) {
        
            rm.getResources().remove(origResource);
            rm.getResources().add (r);
        
            try {
                rmDao.serialize (rm);
                result = new ResultMessage ("success", "Updated resource " + r.getId(), null);
            } catch (Exception e) {
                log.error ("Error serializing resource module XML file, " + path + ": " + e.getMessage());
                return new ResultMessage ("error", "Unable to serialize resource module, " + path + ": " + e.getMessage(), null);
            }
        } else {
            return new ResultMessage ("error", null, msgList);
        }
        
        return result;
    }
    
    /**
     * <p>
     * Copies one or more resources in a resource module.
     * </p>
     * 
     * @param  path  The path to the resource module.
     * @param  ids   The a comma separated list of resource IDs to copy.
     * @return       A message or messages with the result of the operation.
     */
    @SuppressWarnings("unused")
	public ResultMessage copy (
        String path,
        String ids
    ) {
        List<ResultMessage.MessageItem> msgList = new ArrayList<ResultMessage.MessageItem>();
        boolean atLeastOneItemCopied = false;
        ResultMessage result;
        
        ResourceModule rm = rmDao.findByPath (path);
        if (rm == null) {
            return new ResultMessage ("error", "Resource module, " + path + ", does not exist!", null);
        }
        
        // iterate over all the input ids
        //
        String[] idArray = ids.split (",");
        for (String id : idArray) {
            List<ResultMessage.MessageItem> tmpMsgList;
            Resource r = new Resource();

            r.setId (id);
            
            // make sure the source item exists.
            //
            tmpMsgList = validate (r, rm, DAOConstants.OPERATION_COPY);
            
            // if so, then make the copy
            //
            if (tmpMsgList.size() == 0) {

                Resource nr = new Resource (rm.findById (r.getId())); // find the original resource and make a copy of it.
                
                if (nr == null) {
                    tmpMsgList.add (new ResultMessage.MessageItem ("id", "Unable to locate resource id \"" + r.getId() + "\"."));
                    continue;
                }
                
                atLeastOneItemCopied = true;

                // generate a new id for the copy. start with the original id + "_copy", then look for
                // id + "_copy1", id + "_copy2", etc.
                //
                int j = 0;
                do {
                    String newId = r.getId() + "_copy" + ((j == 0) ? "" : j);
                    
                    nr.setId (newId);

                    j++;
                } while (validate (nr, rm, DAOConstants.OPERATION_COPY).size() == 0);
                
                // add the copy of the element
                //
                rm.getResources().add (nr);
                
                tmpMsgList.add (new ResultMessage.MessageItem ("id", "Resource ID \"" + r.getId() + "\" copied to \"" + nr.getId() + "\"."));
            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneItemCopied) {
            try {
                rmDao.serialize (rm);
                result = new ResultMessage ("success", null, msgList);
            } catch (Exception e) {
                log.error ("Error serializing resource module XML file: " + e.getMessage());
                result = new ResultMessage ("error", "Error serializing resource module XML file: " + e.getMessage(), null);
            }
        } else {
            result = new ResultMessage ("error", null, msgList);
        }

        return result;
    }
    
    /**
     * <p>
     * Deletes one or more resources in a resource module.
     * </p>
     * 
     * @param  path  The path to the resource module.
     * @param  ids   The a comma separated list of resource IDs to delete.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage delete (
        String path,
        String ids
    ) {
        List<ResultMessage.MessageItem> msgList = new ArrayList<ResultMessage.MessageItem>();
        boolean atLeastOneItemDeleted = false;
        ResultMessage result;
        
        ResourceModule rm = rmDao.findByPath (path);
        if (rm == null) {
            return new ResultMessage ("error", "Resource module, " + path + ", does not exist!", null);
        }
        
        // iterate over all the input ids
        //
        String[] idArray = ids.split (",");
        for (String id : idArray) {
            List<ResultMessage.MessageItem> tmpMsgList;
            Resource r = new Resource();

            r.setId (id);
            
            // make sure the item exists.
            //
            tmpMsgList = validate (r, rm, DAOConstants.OPERATION_DELETE);
            
            // if so, then make sure the item doesn't :)
            //
            if (tmpMsgList.size() == 0) {
                atLeastOneItemDeleted = true;
                
                // get rid of the resource element
                //
                r = rm.findById (r.getId());
                rm.getResources().remove (r);
                
                tmpMsgList.add (new ResultMessage.MessageItem ("id", "Resource ID \"" + r.getId() + "\" deleted."));
            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneItemDeleted) {
            try {
                rmDao.serialize (rm);
                result = new ResultMessage ("success", null, msgList);
            } catch (Exception e) {
                log.error ("Error serializing resource module XML file: " + e.getMessage());
                result = new ResultMessage ("error", "Error serializing resource module XML file: " + e.getMessage(), null);
            }
        } else {
            result = new ResultMessage ("error", null, msgList);
        }

        return result;
    }

    // validates an incoming server payload based on the requested operation
    //
    private List<ResultMessage.MessageItem> validate (
        Resource r,
        ResourceModule rm,
        int operation
    ) {
        List<ResultMessage.MessageItem> result = new ArrayList<ResultMessage.MessageItem>();
        
        log.debug ("validating: operation = " + operation + ", rm = " + rm.getPath() + ", r = " + r.getId());
        
        if (r.getId() == null || r.getId().length() == 0) {
            result.add (new ResultMessage.MessageItem ("id", "Resource ID may not be empty."));
        } else if (operation == DAOConstants.OPERATION_ADD){
            if (rm.findById (r.id) != null) {
                result.add (new ResultMessage.MessageItem ("id", "Resource ID \"" + r.getId() + "\" already exists."));
            }
        } else if (operation == DAOConstants.OPERATION_EDIT || operation == DAOConstants.OPERATION_COPY || operation == DAOConstants.OPERATION_DELETE){
            if (rm.findById (((operation == DAOConstants.OPERATION_EDIT) ? r.getOrigId() : r.getId())) == null) {
                result.add (new ResultMessage.MessageItem ("id", "Resource ID \"" + ((operation == DAOConstants.OPERATION_EDIT) ? r.getOrigId() : r.getId()) + "\" does not exist."));
                return result; // no need to go any further if the item doesn't exist.
            }
        }
        
        // no need to continue with copy and delete operations (a copied item has presumably been 
        // previously validated and we don't need validate an item we're deleting.)
        //
        if (operation == DAOConstants.OPERATION_COPY || operation == DAOConstants.OPERATION_DELETE)
            return result;
        
        if (r.getResourcePath() == null || r.getResourcePath().length() == 0) {
            result.add (new ResultMessage.MessageItem ("resourcePath", "Resource Path may not be empty."));
        }
        
        return result;
    }
}

package com.cisco.dvbu.ps.deploytool.gui.core.module.resource_cache;

import com.cisco.dvbu.ps.deploytool.gui.resources.ResourceCacheResource;
import com.cisco.dvbu.ps.deploytool.gui.util.DAOConstants;
import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;

import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage.MessageItem;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Data access object for resources. Used by {@link ResourceCacheResource} servlet.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class ResourceCachesDAO {

    private static final Logger log = LoggerFactory.getLogger (ResourceCachesDAO.class);
    private ResourceCacheModulesDAO rmDao = new ResourceCacheModulesDAO();

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public ResourceCachesDAO () {
        super ();
    }
    
    /**
     * <p>
     * Searches for a single {@link ResourceCache} object given a URL encoded ID string.
     * </p>
     * 
     * @param  path  The URL encoded path to look for.
     * @param  id    The ID to look for.
     * @return       The requested {@link ResourceCache} as an object.
     */
    public ResourceCache findById (
        String path,
        String id
    ) {
        ResourceCacheModule rm = rmDao.findByPath (path);
        if (rm == null) {
            log.error ("Unable to locate resource cache module " + path);
        }
        
        log.debug ("Resource cache module located. Looking for resource ID " + id);
        return rm.findById (id);
    }
  
    /**
     * <p>
     * Adds a resource cache to a resource cache module.
     * </p>
     * 
     * @param  path  The path to the resource cache module.
     * @param  r     The {@link ResourceCache} to add.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage add (
        String path,
        ResourceCache r
    ) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;
        
        ResourceCacheModule rm = rmDao.findByPath (path);
        if (rm == null) {
            return new ResultMessage ("error", "Resource Cache module, " + path + ", does not exist!", null);
        }
        
        msgList = validate (r, rm, DAOConstants.OPERATION_ADD);
        
        if (msgList.size() == 0) {
        
            rm.getResourceCaches().add (r);
        
            try {
                rmDao.serialize (rm);
                result = new ResultMessage ("success", "Created resource cache " + r.getId(), null);
            } catch (Exception e) {
                log.error ("Error serializing resource cache module XML file, " + path + ": " + e.getMessage());
                return new ResultMessage ("error", "Unable to serialize resource cache module, " + path + ": " + e.getMessage(), null);
            }
        } else {
            return new ResultMessage ("error", null, msgList);
        }
        
        return result;
    }
  
    /**
     * <p>
     * Updates a resource cache in a resource cache module.
     * </p>
     * 
     * @param  path  The path to the resource cache module.
     * @param  r     The {@link ResourceCache} to update.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage edit (
        String path,
        ResourceCache r
    ) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;
        ResourceCache origResourceCache = null;
        
        ResourceCacheModule rm = rmDao.findByPath (path);
        if (rm == null) {
            return new ResultMessage ("error", "Resource cache module, " + path + ", does not exist!", null);
        }
        
        for (ResourceCache tmp : rm.getResourceCaches()) {
            if (tmp.getId().equals (r.getOrigId())) {
                origResourceCache = tmp;
                break;
            }
        }
        
        msgList = validate (r, rm, DAOConstants.OPERATION_EDIT);
        
        if (msgList.size() == 0) {
        
            rm.getResourceCaches().remove(origResourceCache);
            rm.getResourceCaches().add (r);
        
            try {
                rmDao.serialize (rm);
                result = new ResultMessage ("success", "Updated resource cache " + r.getId(), null);
            } catch (Exception e) {
                log.error ("Error serializing resource cache module XML file, " + path + ": " + e.getMessage());
                return new ResultMessage ("error", "Unable to serialize resource cache module, " + path + ": " + e.getMessage(), null);
            }
        } else {
            return new ResultMessage ("error", null, msgList);
        }
        
        return result;
    }
    
    /**
     * <p>
     * Copies one or more resource caches in a resource cache module.
     * </p>
     * 
     * @param  path  The path to the resource cache module.
     * @param  ids   The a comma separated list of resource cache IDs to copy.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage copy (
        String path,
        String ids
    ) {
        List<ResultMessage.MessageItem> msgList = new ArrayList<ResultMessage.MessageItem>();
        boolean atLeastOneItemCopied = false;
        ResultMessage result;
        
        ResourceCacheModule rm = rmDao.findByPath (path);
        if (rm == null) {
            return new ResultMessage ("error", "Resource Cache module, " + path + ", does not exist!", null);
        }
        
        // iterate over all the input ids
        //
        String[] idArray = ids.split (",");
        for (String id : idArray) {
            List<ResultMessage.MessageItem> tmpMsgList;
            ResourceCache r = new ResourceCache();

            r.setId (id);
            
            // make sure the source item exists.
            //
            tmpMsgList = validate (r, rm, DAOConstants.OPERATION_COPY);
            
            // if so, then make the copy
            //
            if (tmpMsgList.size() == 0) {

                ResourceCache nr = new ResourceCache (rm.findById (r.getId())); // find the original resource cache and make a copy of it.
                
                if (nr == null) {
                    tmpMsgList.add (new ResultMessage.MessageItem ("id", "Unable to locate resource cache id \"" + r.getId() + "\"."));
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
                rm.getResourceCaches().add (nr);
                
                tmpMsgList.add (new ResultMessage.MessageItem ("id", "Resource Cache ID \"" + r.getId() + "\" copied to \"" + nr.getId() + "\"."));
            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneItemCopied) {
            try {
                rmDao.serialize (rm);
                result = new ResultMessage ("success", null, msgList);
            } catch (Exception e) {
                log.error ("Error serializing resource cache module XML file: " + e.getMessage());
                result = new ResultMessage ("error", "Error serializing resource cache module XML file: " + e.getMessage(), null);
            }
        } else {
            result = new ResultMessage ("error", null, msgList);
        }

        return result;
    }
    
    /**
     * <p>
     * Deletes one or more resource caches in a resource cache module.
     * </p>
     * 
     * @param  path  The path to the resource cache module.
     * @param  ids   The a comma separated list of resource cache IDs to delete.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage delete (
        String path,
        String ids
    ) {
        List<ResultMessage.MessageItem> msgList = new ArrayList<ResultMessage.MessageItem>();
        boolean atLeastOneItemDeleted = false;
        ResultMessage result;
        
        ResourceCacheModule rm = rmDao.findByPath (path);
        if (rm == null) {
            return new ResultMessage ("error", "Resource module, " + path + ", does not exist!", null);
        }
        
        // iterate over all the input ids
        //
        String[] idArray = ids.split (",");
        for (String id : idArray) {
            List<ResultMessage.MessageItem> tmpMsgList;
            ResourceCache r = new ResourceCache();

            r.setId (id);
            
            // make sure the item exists.
            //
            tmpMsgList = validate (r, rm, DAOConstants.OPERATION_DELETE);
            
            // if so, then make sure the item doesn't :)
            //
            if (tmpMsgList.size() == 0) {
                atLeastOneItemDeleted = true;
                
                // get rid of the resource cache element
                //
                r = rm.findById (r.getId());
                rm.getResourceCaches().remove (r);
                
                tmpMsgList.add (new ResultMessage.MessageItem ("id", "Resource Cache ID \"" + r.getId() + "\" deleted."));
            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneItemDeleted) {
            try {
                rmDao.serialize (rm);
                result = new ResultMessage ("success", null, msgList);
            } catch (Exception e) {
                log.error ("Error serializing resource cache module XML file: " + e.getMessage());
                result = new ResultMessage ("error", "Error serializing resource cache module XML file: " + e.getMessage(), null);
            }
        } else {
            result = new ResultMessage ("error", null, msgList);
        }

        return result;
    }

    // validates an incoming server payload based on the requested operation
    //
    private List<ResultMessage.MessageItem> validate (
        ResourceCache r,
        ResourceCacheModule rm,
        int operation
    ) {
        List<ResultMessage.MessageItem> result = new ArrayList<ResultMessage.MessageItem>();
        
        log.debug ("validating: operation = " + operation + ", rm = " + rm.getPath() + ", r = " + r.getId());
        
        if (r.getId() == null || r.getId().length() == 0) {
            result.add (new ResultMessage.MessageItem ("id", "Resource Cache ID may not be empty."));
        } else if (operation == DAOConstants.OPERATION_ADD){
            if (rm.findById (r.id) != null) {
                result.add (new ResultMessage.MessageItem ("id", "Resource Cache ID \"" + r.getId() + "\" already exists."));
            }
        } else if (operation == DAOConstants.OPERATION_EDIT || operation == DAOConstants.OPERATION_COPY || operation == DAOConstants.OPERATION_DELETE){
            if (rm.findById (((operation == DAOConstants.OPERATION_EDIT) ? r.getOrigId() : r.getId())) == null) {
                result.add (new ResultMessage.MessageItem ("id", "Resource Cache ID \"" + ((operation == DAOConstants.OPERATION_EDIT) ? r.getOrigId() : r.getId()) + "\" does not exist."));
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
        
        if (r.getResourceType() == null || r.getResourceType().length() == 0) {
            result.add (new ResultMessage.MessageItem ("resourceType", "Resource Type may not be empty."));
        }
        
        return result;
    }
}

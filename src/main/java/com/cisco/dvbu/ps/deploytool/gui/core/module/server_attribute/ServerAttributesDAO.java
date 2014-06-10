package com.cisco.dvbu.ps.deploytool.gui.core.module.server_attribute;

import com.cisco.dvbu.ps.deploytool.gui.util.DAOConstants;
import com.cisco.dvbu.ps.deploytool.gui.resources.ServerAttributeResource;
import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Data access object for server attributes. Used by {@link ServerAttributeResource} servlet.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class ServerAttributesDAO {

    private static final Logger log = LoggerFactory.getLogger (ServerAttributesDAO.class);
    private ServerAttributeModulesDAO samDao = new ServerAttributeModulesDAO();

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public ServerAttributesDAO () {
        super ();
    }
    
    /**
     * <p>
     * Searches for a single {@link ServerAttribute} object given a URL encoded ID string.
     * </p>
     * 
     * @param  path  The URL encoded path to look for.
     * @param  id    The ID to look for.
     * @return       The requested {@link ServerAttribute} as an object.
     */
    public ServerAttribute findById (
        String path,
        String id
    ) {
        ServerAttributeModule sam = samDao.findByPath (path);
        if (sam == null) {
            log.error ("Unable to locate server attribute module " + path);
        }
        
        log.debug ("Server attribute module located. Looking for server attribute ID " + id);
        return sam.findById (id);
    }
  
    /**
     * <p>
     * Adds a server attribute to a server attribute module.
     * </p>
     * 
     * @param  path  The path to the server attribute module.
     * @param  sa    The {@link ServerAttribute} to add.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage add (
        String path,
        ServerAttribute sa
    ) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;
        
        ServerAttributeModule sam = samDao.findByPath (path);
        if (sam == null) {
            return new ResultMessage ("error", "Server attribute module, " + path + ", does not exist!", null);
        }
        
        msgList = validate (sa, sam, DAOConstants.OPERATION_ADD);
        
        if (msgList.size() == 0) {
        
        sam.getServerAttributes().add (sa);
        
            try {
                samDao.serialize (sam);
                result = new ResultMessage ("success", "Created server attribute " + sa.getId(), null);
            } catch (Exception e) {
                log.error ("Error serializing server attribute module XML file, " + path + ": " + e.getMessage());
                return new ResultMessage ("error", "Unable to serialize server attribute module, " + path + ": " + e.getMessage(), null);
            }
        } else {
            return new ResultMessage ("error", null, msgList);
        }
        
        return result;
    }
  
    /**
     * <p>
     * Updates a server attribute in a server attribute module.
     * </p>
     * 
     * @param  path  The path to the server attribute module.
     * @param  sa    The {@link ServerAttribute} to update.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage edit (
        String path,
        ServerAttribute sa
    ) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;
        ServerAttribute origSa = null;
        
        ServerAttributeModule sam = samDao.findByPath (path);
        if (sam == null) {
            return new ResultMessage ("error", "Server attribute module, " + path + ", does not exist!", null);
        }
        
        for (ServerAttribute tmp : sam.getServerAttributes()) {
            if (tmp.getId().equals (sa.getOrigId())) {
                origSa = tmp;
                break;
            }
        }
        
        msgList = validate (sa, sam, DAOConstants.OPERATION_EDIT);
        
        if (msgList.size() == 0) {
        
            sam.getServerAttributes().remove (origSa);
            sam.getServerAttributes().add (sa);
        
            try {
                samDao.serialize (sam);
                result = new ResultMessage ("success", "Updated server attribute " + sa.getId(), null);
            } catch (Exception e) {
                log.error ("Error serializing server attribute module XML file, " + path + ": " + e.getMessage());
                return new ResultMessage ("error", "Unable to serialize server attribute module, " + path + ": " + e.getMessage(), null);
            }
        } else {
            return new ResultMessage ("error", null, msgList);
        }
        
        return result;
    }
    
    /**
     * <p>
     * Copies one or more server attributes in a server attribute module.
     * </p>
     * 
     * @param  path  The path to the server attribute module.
     * @param  ids   The a comma separated list of server attribute IDs to copy.
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
        
        ServerAttributeModule sam = samDao.findByPath (path);
        if (sam == null) {
            return new ResultMessage ("error", "Server attribute module, " + path + ", does not exist!", null);
        }
        
        // iterate over all the input ids
        //
        String[] idArray = ids.split (",");
        for (String id : idArray) {
            List<ResultMessage.MessageItem> tmpMsgList;
            ServerAttribute sa = new ServerAttribute();

            sa.setId (id);
            
            // make sure the source item exists.
            //
            tmpMsgList = validate (sa, sam, DAOConstants.OPERATION_COPY);
            
            // if so, then make the copy
            //
            if (tmpMsgList.size() == 0) {

                ServerAttribute nsa = new ServerAttribute (sam.findById (sa.getId())); // find the original server attribute and make a copy of it.
                
                if (nsa == null) {
                    tmpMsgList.add (new ResultMessage.MessageItem ("id", "Unable to locate server attribute id \"" + sa.getId() + "\"."));
                    continue;
                }
                
                atLeastOneItemCopied = true;

                // generate a new id for the copy. start with the original id + "_copy", then look for
                // id + "_copy1", id + "_copy2", etc.
                //
                int j = 0;
                do {
                    String newId = sa.getId() + "_copy" + ((j == 0) ? "" : j);
                    
                    nsa.setId (newId);

                    j++;
                } while (validate (nsa, sam, DAOConstants.OPERATION_COPY).size() == 0);
                
                // add the copy of the element
                //
                sam.getServerAttributes().add (nsa);
                
                tmpMsgList.add (new ResultMessage.MessageItem ("id", "Server attribute ID \"" + sa.getId() + "\" copied to \"" + nsa.getId() + "\"."));
            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneItemCopied) {
            try {
                samDao.serialize (sam);
                result = new ResultMessage ("success", null, msgList);
            } catch (Exception e) {
                log.error ("Error serializing server attribute module XML file: " + e.getMessage());
                result = new ResultMessage ("error", "Error serializing server attribute module XML file: " + e.getMessage(), null);
            }
        } else {
            result = new ResultMessage ("error", null, msgList);
        }

        return result;
    }
    
    /**
     * <p>
     * Deletes one or more server attributes in a server attributes module.
     * </p>
     * 
     * @param  path  The path to the server attribute module.
     * @param  ids   The a comma separated list of server attribute IDs to delete.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage delete (
        String path,
        String ids
    ) {
        List<ResultMessage.MessageItem> msgList = new ArrayList<ResultMessage.MessageItem>();
        boolean atLeastOneItemDeleted = false;
        ResultMessage result;
        
        ServerAttributeModule sam = samDao.findByPath (path);
        if (sam == null) {
            return new ResultMessage ("error", "Server attribute module, " + path + ", does not exist!", null);
        }
        
        // iterate over all the input ids
        //
        String[] idArray = ids.split (",");
        for (String id : idArray) {
            List<ResultMessage.MessageItem> tmpMsgList;
            ServerAttribute sa = new ServerAttribute();

            sa.setId (id);
            
            // make sure the item exists.
            //
            tmpMsgList = validate (sa, sam, DAOConstants.OPERATION_DELETE);
            
            // if so, then make sure the item doesn't :)
            //
            if (tmpMsgList.size() == 0) {
                atLeastOneItemDeleted = true;
                
                // get rid of the server element
                //
                sa = sam.findById (sa.getId());
                sam.getServerAttributes().remove (sa);
                
                tmpMsgList.add (new ResultMessage.MessageItem ("id", "Server attribute ID \"" + sa.getId() + "\" deleted."));
            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneItemDeleted) {
            try {
                samDao.serialize (sam);
                result = new ResultMessage ("success", null, msgList);
            } catch (Exception e) {
                log.error ("Error serializing group module XML file: " + e.getMessage());
                result = new ResultMessage ("error", "Error serializing group module XML file: " + e.getMessage(), null);
            }
        } else {
            result = new ResultMessage ("error", null, msgList);
        }

        return result;
    }

    // validates an incoming server payload based on the requested operation
    //
    private List<ResultMessage.MessageItem> validate (
        ServerAttribute sa,
        ServerAttributeModule sam,
        int operation
    ) {
        List<ResultMessage.MessageItem> result = new ArrayList<ResultMessage.MessageItem>();
        
        log.debug ("validating: operation = " + operation + ", sam = " + sam.getPath() + ", sa = " + sa.getId());
        
        if (sa.getId() == null || sa.getId().length() == 0) {
            result.add (new ResultMessage.MessageItem ("id", "Server attribute ID may not be empty."));
        } else if (operation == DAOConstants.OPERATION_ADD){
            if (sam.findById (sa.id) != null) {
                result.add (new ResultMessage.MessageItem ("id", "Server attribute ID \"" + sa.getId() + "\" already exists."));
            }
        } else if (operation == DAOConstants.OPERATION_EDIT || operation == DAOConstants.OPERATION_COPY || operation == DAOConstants.OPERATION_DELETE){
            if (sam.findById (((operation == DAOConstants.OPERATION_EDIT) ? sa.getOrigId() : sa.getId())) == null) {
                result.add (new ResultMessage.MessageItem ("id", "Server attribute ID \"" + ((operation == DAOConstants.OPERATION_EDIT) ? sa.getOrigId() : sa.getId()) + "\" does not exist."));
                return result; // no need to go any further if the item doesn't exist.
            }
        }
        
        // no need to continue with copy and delete operations (a copied item has presumably been 
        // previously validated and we don't need validate an item we're deleting.)
        //
        if (operation == DAOConstants.OPERATION_COPY || operation == DAOConstants.OPERATION_DELETE)
            return result;
        
        return result;
    }
}

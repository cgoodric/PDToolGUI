package com.cisco.dvbu.ps.deploytool.gui.core.module.user;

import com.cisco.dvbu.ps.deploytool.gui.util.DAOConstants;
import com.cisco.dvbu.ps.deploytool.gui.resources.UserResource;
import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;

import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage.MessageItem;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Data access object for users. Used by {@link UserResource} servlet.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class UsersDAO {

    private static final Logger log = LoggerFactory.getLogger (UsersDAO.class);
    private UserModulesDAO umDao = new UserModulesDAO();

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public UsersDAO () {
        super ();
    }
    
    /**
     * <p>
     * Searches for a single {@link User} object given a URL encoded ID string.
     * </p>
     * 
     * @param  path  The URL encoded path to look for.
     * @param  id    The ID to look for.
     * @return       The requested {@link User} as an object.
     */
    public User findById (
        String path,
        String id
    ) {
        UserModule um = umDao.findByPath (path);
        if (um == null) {
            log.error ("Unable to locate user module " + path);
        }
        
        log.debug ("User module located. Looking for user ID " + id);
        return um.findById (id);
    }
  
    /**
     * <p>
     * Adds a user to a user module.
     * </p>
     * 
     * @param  path  The path to the user module.
     * @param  u     The {@link User} to add.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage add (
        String path,
        User u
    ) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;
        
        UserModule um = umDao.findByPath (path);
        if (um == null) {
            return new ResultMessage ("error", "User module, " + path + ", does not exist!", null);
        }
        
        msgList = validate (u, um, DAOConstants.OPERATION_ADD);
        
        if (msgList.size() == 0) {
        
            um.getUsers().add (u);
        
            try {
                umDao.serialize (um);
                result = new ResultMessage ("success", "Created user " + u.getId(), null);
            } catch (Exception e) {
                log.error ("Error serializing user module XML file, " + path + ": " + e.getMessage());
                return new ResultMessage ("error", "Unable to serialize user module, " + path + ": " + e.getMessage(), null);
            }
        } else {
            return new ResultMessage ("error", null, msgList);
        }
        
        return result;
    }
  
    /**
     * <p>
     * Updates a user in a user module.
     * </p>
     * 
     * @param  path  The path to the user module.
     * @param  u     The {@link User} to update.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage edit (
        String path,
        User u
    ) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;
        User origUser = null;
        
        UserModule um = umDao.findByPath (path);
        if (um == null) {
            return new ResultMessage ("error", "User module, " + path + ", does not exist!", null);
        }
        
        for (User tmp : um.getUsers()) {
            if (tmp.getId().equals (u.getOrigId())) {
                origUser = tmp;
                break;
            }
        }
        
        msgList = validate (u, um, DAOConstants.OPERATION_EDIT);
        
        if (msgList.size() == 0) {
        
            um.getUsers().remove (origUser);
            um.getUsers().add (u);
        
            try {
                umDao.serialize (um);
                result = new ResultMessage ("success", "Updated user " + u.getId(), null);
            } catch (Exception e) {
                log.error ("Error serializing user module XML file, " + path + ": " + e.getMessage());
                return new ResultMessage ("error", "Unable to serialize user module, " + path + ": " + e.getMessage(), null);
            }
        } else {
            return new ResultMessage ("error", null, msgList);
        }
        
        return result;
    }
    
    /**
     * <p>
     * Copies one or more users in a user module.
     * </p>
     * 
     * @param  path  The path to the user module.
     * @param  ids   The a comma separated list of user IDs to copy.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage copy (
        String path,
        String ids
    ) {
        List<ResultMessage.MessageItem> msgList = new ArrayList<ResultMessage.MessageItem>();
        boolean atLeastOneItemCopied = false;
        ResultMessage result;
        
        UserModule um = umDao.findByPath (path);
        if (um == null) {
            return new ResultMessage ("error", "User module, " + path + ", does not exist!", null);
        }
        
        // iterate over all the input ids
        //
        String[] idArray = ids.split (",");
        for (String id : idArray) {
            List<ResultMessage.MessageItem> tmpMsgList;
            User u = new User();

            u.setId (id);
            
            // make sure the source item exists.
            //
            tmpMsgList = validate (u, um, DAOConstants.OPERATION_COPY);
            
            // if so, then make the copy
            //
            if (tmpMsgList.size() == 0) {

                User nu = new User (um.findById (u.getId())); // find the original archive and make a copy of it.
                
                if (nu == null) {
                    tmpMsgList.add (new ResultMessage.MessageItem ("id", "Unable to locate user id \"" + u.getId() + "\"."));
                    continue;
                }
                
                atLeastOneItemCopied = true;

                // generate a new id for the copy. start with the original id + "_copy", then look for
                // id + "_copy1", id + "_copy2", etc.
                //
                int j = 0;
                do {
                    String newId = u.getId() + "_copy" + ((j == 0) ? "" : j);
                    
                    nu.setId (newId);

                    j++;
                } while (validate (nu, um, DAOConstants.OPERATION_COPY).size() == 0);
                
                // add the copy of the element
                //
                um.getUsers().add (nu);
                
                tmpMsgList.add (new ResultMessage.MessageItem ("id", "User ID \"" + u.getId() + "\" copied to \"" + nu.getId() + "\"."));
            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneItemCopied) {
            try {
                umDao.serialize (um);
                result = new ResultMessage ("success", null, msgList);
            } catch (Exception e) {
                log.error ("Error serializing user module XML file: " + e.getMessage());
                result = new ResultMessage ("error", "Error serializing user module XML file: " + e.getMessage(), null);
            }
        } else {
            result = new ResultMessage ("error", null, msgList);
        }

        return result;
    }
    
    /**
     * <p>
     * Deletes one or more users in a user module.
     * </p>
     * 
     * @param  path  The path to the user module.
     * @param  ids   The a comma separated list of user IDs to delete.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage delete (
        String path,
        String ids
    ) {
        List<ResultMessage.MessageItem> msgList = new ArrayList<ResultMessage.MessageItem>();
        boolean atLeastOneItemDeleted = false;
        ResultMessage result;
        
        UserModule um = umDao.findByPath (path);
        if (um == null) {
            return new ResultMessage ("error", "User module, " + path + ", does not exist!", null);
        }
        
        // iterate over all the input ids
        //
        String[] idArray = ids.split (",");
        for (String id : idArray) {
            List<ResultMessage.MessageItem> tmpMsgList;
            User u = new User();

            u.setId (id);
            
            // make sure the item exists.
            //
            tmpMsgList = validate (u, um, DAOConstants.OPERATION_DELETE);
            
            // if so, then make sure the item doesn't :)
            //
            if (tmpMsgList.size() == 0) {
                atLeastOneItemDeleted = true;
                
                // get rid of the server element
                //
                u = um.findById (u.getId());
                um.getUsers().remove (u);
                
                tmpMsgList.add (new ResultMessage.MessageItem ("id", "User ID \"" + u.getId() + "\" deleted."));
            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneItemDeleted) {
            try {
                umDao.serialize (um);
                result = new ResultMessage ("success", null, msgList);
            } catch (Exception e) {
                log.error ("Error serializing user module XML file: " + e.getMessage());
                result = new ResultMessage ("error", "Error serializing user module XML file: " + e.getMessage(), null);
            }
        } else {
            result = new ResultMessage ("error", null, msgList);
        }

        return result;
    }

    // validates an incoming server payload based on the requested operation
    //
    private List<ResultMessage.MessageItem> validate (
        User u,
        UserModule um,
        int operation
    ) {
        List<ResultMessage.MessageItem> result = new ArrayList<ResultMessage.MessageItem>();
        
        log.debug ("validating: operation = " + operation + ", um = " + um.getPath() + ", u = " + u.getId());
        
        if (u.getId() == null || u.getId().length() == 0) {
            result.add (new ResultMessage.MessageItem ("id", "User ID may not be empty."));
        } else if (operation == DAOConstants.OPERATION_ADD){
            if (um.findById (u.id) != null) {
                result.add (new ResultMessage.MessageItem ("id", "User ID \"" + u.getId() + "\" already exists."));
            }
        } else if (operation == DAOConstants.OPERATION_EDIT || operation == DAOConstants.OPERATION_COPY || operation == DAOConstants.OPERATION_DELETE){
            if (um.findById (((operation == DAOConstants.OPERATION_EDIT) ? u.getOrigId() : u.getId())) == null) {
                result.add (new ResultMessage.MessageItem ("id", "User ID \"" + ((operation == DAOConstants.OPERATION_EDIT) ? u.getOrigId() : u.getId()) + "\" does not exist."));
                return result; // no need to go any further if the item doesn't exist.
            }
        }
        
        // no need to continue with copy and delete operations (a copied item has presumably been 
        // previously validated and we don't need validate an item we're deleting.)
        //
        if (operation == DAOConstants.OPERATION_COPY || operation == DAOConstants.OPERATION_DELETE)
            return result;
        
        if (u.getUserName() == null || u.getUserName().length() == 0) {
            result.add (new ResultMessage.MessageItem ("userName", "User Name may not be empty."));
        }
        
        if (u.getDomainName() == null || u.getDomainName().length() == 0) {
            result.add (new ResultMessage.MessageItem ("domainName", "User Domain may not be empty."));
        }
        
        return result;
    }
}

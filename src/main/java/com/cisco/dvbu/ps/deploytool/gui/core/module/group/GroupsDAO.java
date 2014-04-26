package com.cisco.dvbu.ps.deploytool.gui.core.module.group;

import com.cisco.dvbu.ps.deploytool.gui.util.DAOConstants;
import com.cisco.dvbu.ps.deploytool.gui.resources.GroupResource;
import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;

import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage.MessageItem;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Data access object for groups. Used by {@link GroupResource} servlet.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class GroupsDAO {

    private static final Logger log = LoggerFactory.getLogger (GroupsDAO.class);
    private GroupModulesDAO gmDao = new GroupModulesDAO();

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public GroupsDAO () {
        super ();
    }
    
    /**
     * <p>
     * Searches for a single {@link Group} object given a URL encoded ID string.
     * </p>
     * 
     * @param  path  The URL encoded path to look for.
     * @param  id    The ID to look for.
     * @return       The requested {@link Group} as an object.
     */
    public Group findById (
        String path,
        String id
    ) {
        GroupModule gm = gmDao.findByPath (path);
        if (gm == null) {
            log.error ("Unable to locate group module " + path);
        }
        
        log.debug ("Group module located. Looking for group ID " + id);
        return gm.findById (id);
    }
  
    /**
     * <p>
     * Adds a group to a group module.
     * </p>
     * 
     * @param  path  The path to the group module.
     * @param  g     The {@link Group} to add.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage add (
        String path,
        Group g
    ) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;
        
        GroupModule gm = gmDao.findByPath (path);
        if (gm == null) {
            return new ResultMessage ("error", "Group module, " + path + ", does not exist!", null);
        }
        
        msgList = validate (g, gm, DAOConstants.OPERATION_ADD);
        
        if (msgList.size() == 0) {
        
        gm.getGroups().add (g);
        
            try {
                gmDao.serialize (gm);
                result = new ResultMessage ("success", "Created group " + g.getId(), null);
            } catch (Exception e) {
                log.error ("Error serializing group module XML file, " + path + ": " + e.getMessage());
                return new ResultMessage ("error", "Unable to serialize group module, " + path + ": " + e.getMessage(), null);
            }
        } else {
            return new ResultMessage ("error", null, msgList);
        }
        
        return result;
    }
  
    /**
     * <p>
     * Updates a group in a group module.
     * </p>
     * 
     * @param  path  The path to the group module.
     * @param  g     The {@link Group} to update.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage edit (
        String path,
        Group g
    ) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;
        Group origGroup = null;
        
        GroupModule gm = gmDao.findByPath (path);
        if (gm == null) {
            return new ResultMessage ("error", "Group module, " + path + ", does not exist!", null);
        }
        
        for (Group tmp : gm.getGroups()) {
            if (tmp.getId().equals (g.getOrigId())) {
                origGroup = tmp;
                break;
            }
        }
        
        msgList = validate (g, gm, DAOConstants.OPERATION_EDIT);
        
        if (msgList.size() == 0) {
        
            gm.getGroups().remove (origGroup);
            gm.getGroups().add (g);
        
            try {
                gmDao.serialize (gm);
                result = new ResultMessage ("success", "Updated group " + g.getId(), null);
            } catch (Exception e) {
                log.error ("Error serializing group module XML file, " + path + ": " + e.getMessage());
                return new ResultMessage ("error", "Unable to serialize group module, " + path + ": " + e.getMessage(), null);
            }
        } else {
            return new ResultMessage ("error", null, msgList);
        }
        
        return result;
    }
    
    /**
     * <p>
     * Copies one or more groups in a group module.
     * </p>
     * 
     * @param  path  The path to the group module.
     * @param  ids   The a comma separated list of group IDs to copy.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage copy (
        String path,
        String ids
    ) {
        List<ResultMessage.MessageItem> msgList = new ArrayList<ResultMessage.MessageItem>();
        boolean atLeastOneItemCopied = false;
        ResultMessage result;
        
        GroupModule gm = gmDao.findByPath (path);
        if (gm == null) {
            return new ResultMessage ("error", "Group module, " + path + ", does not exist!", null);
        }
        
        // iterate over all the input ids
        //
        String[] idArray = ids.split (",");
        for (String id : idArray) {
            List<ResultMessage.MessageItem> tmpMsgList;
            Group g = new Group();

            g.setId (id);
            
            // make sure the source item exists.
            //
            tmpMsgList = validate (g, gm, DAOConstants.OPERATION_COPY);
            
            // if so, then make the copy
            //
            if (tmpMsgList.size() == 0) {

                Group ng = new Group (gm.findById (g.getId())); // find the original group and make a copy of it.
                
                if (ng == null) {
                    tmpMsgList.add (new ResultMessage.MessageItem ("id", "Unable to locate group id \"" + g.getId() + "\"."));
                    continue;
                }
                
                atLeastOneItemCopied = true;

                // generate a new id for the copy. start with the original id + "_copy", then look for
                // id + "_copy1", id + "_copy2", etc.
                //
                int j = 0;
                do {
                    String newId = g.getId() + "_copy" + ((j == 0) ? "" : j);
                    
                    ng.setId (newId);

                    j++;
                } while (validate (ng, gm, DAOConstants.OPERATION_COPY).size() == 0);
                
                // add the copy of the element
                //
                gm.getGroups().add (ng);
                
                tmpMsgList.add (new ResultMessage.MessageItem ("id", "Group ID \"" + g.getId() + "\" copied to \"" + ng.getId() + "\"."));
            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneItemCopied) {
            try {
                gmDao.serialize (gm);
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
    
    /**
     * <p>
     * Deletes one or more groups in a group module.
     * </p>
     * 
     * @param  path  The path to the group module.
     * @param  ids   The a comma separated list of group IDs to delete.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage delete (
        String path,
        String ids
    ) {
        List<ResultMessage.MessageItem> msgList = new ArrayList<ResultMessage.MessageItem>();
        boolean atLeastOneItemDeleted = false;
        ResultMessage result;
        
        GroupModule gm = gmDao.findByPath (path);
        if (gm == null) {
            return new ResultMessage ("error", "Group module, " + path + ", does not exist!", null);
        }
        
        // iterate over all the input ids
        //
        String[] idArray = ids.split (",");
        for (String id : idArray) {
            List<ResultMessage.MessageItem> tmpMsgList;
            Group g = new Group();

            g.setId (id);
            
            // make sure the item exists.
            //
            tmpMsgList = validate (g, gm, DAOConstants.OPERATION_DELETE);
            
            // if so, then make sure the item doesn't :)
            //
            if (tmpMsgList.size() == 0) {
                atLeastOneItemDeleted = true;
                
                // get rid of the group element
                //
                g = gm.findById (g.getId());
                gm.getGroups().remove (g);
                
                tmpMsgList.add (new ResultMessage.MessageItem ("id", "Group ID \"" + g.getId() + "\" deleted."));
            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneItemDeleted) {
            try {
                gmDao.serialize (gm);
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
        Group g,
        GroupModule gm,
        int operation
    ) {
        List<ResultMessage.MessageItem> result = new ArrayList<ResultMessage.MessageItem>();
        
        log.debug ("validating: operation = " + operation + ", gm = " + gm.getPath() + ", g = " + g.getId());
        
        if (g.getId() == null || g.getId().length() == 0) {
            result.add (new ResultMessage.MessageItem ("id", "Group ID may not be empty."));
        } else if (operation == DAOConstants.OPERATION_ADD){
            if (gm.findById (g.id) != null) {
                result.add (new ResultMessage.MessageItem ("id", "Group ID \"" + g.getId() + "\" already exists."));
            }
        } else if (operation == DAOConstants.OPERATION_EDIT || operation == DAOConstants.OPERATION_COPY || operation == DAOConstants.OPERATION_DELETE){
            if (gm.findById (((operation == DAOConstants.OPERATION_EDIT) ? g.getOrigId() : g.getId())) == null) {
                result.add (new ResultMessage.MessageItem ("id", "Group ID \"" + ((operation == DAOConstants.OPERATION_EDIT) ? g.getOrigId() : g.getId()) + "\" does not exist."));
                return result; // no need to go any further if the item doesn't exist.
            }
        }
        
        // no need to continue with copy and delete operations (a copied item has presumably been 
        // previously validated and we don't need validate an item we're deleting.)
        //
        if (operation == DAOConstants.OPERATION_COPY || operation == DAOConstants.OPERATION_DELETE)
            return result;
        
        if (g.getGroupName() == null || g.getGroupName().length() == 0) {
            result.add (new ResultMessage.MessageItem ("groupName", "Group Name may not be empty."));
        }
        
        if (g.getGroupDomain() == null || g.getGroupDomain().length() == 0) {
            result.add (new ResultMessage.MessageItem ("groupDomain", "Group Domain may not be empty."));
        }
        
        return result;
    }
}

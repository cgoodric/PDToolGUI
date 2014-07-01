package com.cisco.dvbu.ps.deploytool.gui.core.module.trigger;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.cisco.dvbu.ps.deploytool.gui.util.DAOConstants;
import com.cisco.dvbu.ps.deploytool.gui.resources.TriggerResource;
import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Data access object for groups. Used by {@link TriggerResource} servlet.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class TriggersDAO {

    private static final Logger log = LoggerFactory.getLogger (TriggersDAO.class);
    private TriggerModulesDAO tmDao = new TriggerModulesDAO();

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public TriggersDAO () {
        super ();
    }
    
    /**
     * <p>
     * Searches for a single {@link Trigger} object given a URL encoded ID string.
     * </p>
     * 
     * @param  path  The URL encoded path to look for.
     * @param  id    The ID to look for.
     * @return       The requested {@link Trigger} as an object.
     */
    public Trigger findById (
        String path,
        String id
    ) {
        TriggerModule tm = tmDao.findByPath (path);
        if (tm == null) {
            log.error ("Unable to locate trigger module " + path);
        }
        
        log.debug ("Trigger module located. Looking for trigger ID " + id);
        return tm.findById (id);
    }
  
    /**
     * <p>
     * Adds a trigger to a trigger module.
     * </p>
     * 
     * @param  path  The path to the trigger module.
     * @param  t     The {@link Trigger} to add.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage add (
        String path,
        Trigger t
    ) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;
        
        TriggerModule tm = tmDao.findByPath (path);
        if (tm == null) {
            return new ResultMessage ("error", "Trigger module, " + path + ", does not exist!", null);
        }
        
        msgList = validate (t, tm, DAOConstants.OPERATION_ADD);
        
        if (msgList.size() == 0) {
        
        tm.getTriggers().add (t);
        
            try {
                tmDao.serialize (tm);
                result = new ResultMessage ("success", "Created trigger " + t.getId(), null);
            } catch (Exception e) {
                log.error ("Error serializing trigger module XML file, " + path + ": " + e.getMessage());
                return new ResultMessage ("error", "Unable to serialize trigger module, " + path + ": " + e.getMessage(), null);
            }
        } else {
            return new ResultMessage ("error", null, msgList);
        }
        
        return result;
    }
  
    /**
     * <p>
     * Updates a trigger in a trigger module.
     * </p>
     * 
     * @param  path  The path to the trigger module.
     * @param  t     The {@link Trigger} to update.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage edit (
        String path,
        Trigger t
    ) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;
        Trigger origTrigger = null;
        
        TriggerModule tm = tmDao.findByPath (path);
        if (tm == null) {
            return new ResultMessage ("error", "Trigger module, " + path + ", does not exist!", null);
        }
        
        for (Trigger tmp : tm.getTriggers()) {
            if (tmp.getId().equals (t.getOrigId())) {
                origTrigger = tmp;
                break;
            }
        }
        
        msgList = validate (t, tm, DAOConstants.OPERATION_EDIT);
        
        if (msgList.size() == 0) {
        
            tm.getTriggers().remove (origTrigger);
            tm.getTriggers().add (t);
        
            try {
                tmDao.serialize (tm);
                result = new ResultMessage ("success", "Updated trigger " + t.getId(), null);
            } catch (Exception e) {
                log.error ("Error serializing trigger module XML file, " + path + ": " + e.getMessage());
                return new ResultMessage ("error", "Unable to serialize trigger module, " + path + ": " + e.getMessage(), null);
            }
        } else {
            return new ResultMessage ("error", null, msgList);
        }
        
        return result;
    }
    
    /**
     * <p>
     * Copies one or more trigger in a trigger module.
     * </p>
     * 
     * @param  path  The path to the trigger module.
     * @param  ids   The a comma separated list of trigger IDs to copy.
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
        
        TriggerModule tm = tmDao.findByPath (path);
        if (tm == null) {
            return new ResultMessage ("error", "Trigger module, " + path + ", does not exist!", null);
        }
        
        // iterate over all the input ids
        //
        String[] idArray = ids.split (",");
        for (String id : idArray) {
            List<ResultMessage.MessageItem> tmpMsgList;
            Trigger t = new Trigger();

            t.setId (id);
            
            // make sure the source item exists.
            //
            tmpMsgList = validate (t, tm, DAOConstants.OPERATION_COPY);
            
            // if so, then make the copy
            //
            if (tmpMsgList.size() == 0) {

                Trigger nt = new Trigger (tm.findById (t.getId())); // find the original trigger and make a copy of it.
                
                if (nt == null) {
                    tmpMsgList.add (new ResultMessage.MessageItem ("id", "Unable to locate trigger id \"" + t.getId() + "\"."));
                    continue;
                }
                
                atLeastOneItemCopied = true;

                // generate a new id for the copy. start with the original id + "_copy", then look for
                // id + "_copy1", id + "_copy2", etc.
                //
                int j = 0;
                do {
                    String newId = t.getId() + "_copy" + ((j == 0) ? "" : j);
                    
                    nt.setId (newId);

                    j++;
                } while (validate (nt, tm, DAOConstants.OPERATION_COPY).size() == 0);
                
                // add the copy of the element
                //
                tm.getTriggers().add (nt);
                
                tmpMsgList.add (new ResultMessage.MessageItem ("id", "Trigger ID \"" + t.getId() + "\" copied to \"" + nt.getId() + "\"."));
            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneItemCopied) {
            try {
                tmDao.serialize (tm);
                result = new ResultMessage ("success", null, msgList);
            } catch (Exception e) {
                log.error ("Error serializing trigger module XML file: " + e.getMessage());
                result = new ResultMessage ("error", "Error serializing trigger module XML file: " + e.getMessage(), null);
            }
        } else {
            result = new ResultMessage ("error", null, msgList);
        }

        return result;
    }
    
    /**
     * <p>
     * Deletes one or more triggers in a group module.
     * </p>
     * 
     * @param  path  The path to the trigger module.
     * @param  ids   The a comma separated list of trigger IDs to delete.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage delete (
        String path,
        String ids
    ) {
        List<ResultMessage.MessageItem> msgList = new ArrayList<ResultMessage.MessageItem>();
        boolean atLeastOneItemDeleted = false;
        ResultMessage result;
        
        TriggerModule tm = tmDao.findByPath (path);
        if (tm == null) {
            return new ResultMessage ("error", "Trigger module, " + path + ", does not exist!", null);
        }
        
        // iterate over all the input ids
        //
        String[] idArray = ids.split (",");
        for (String id : idArray) {
            List<ResultMessage.MessageItem> tmpMsgList;
            Trigger t = new Trigger();

            t.setId (id);
            
            // make sure the item exists.
            //
            tmpMsgList = validate (t, tm, DAOConstants.OPERATION_DELETE);
            
            // if so, then make sure the item doesn't :)
            //
            if (tmpMsgList.size() == 0) {
                atLeastOneItemDeleted = true;
                
                // get rid of the trigger element
                //
                t = tm.findById (t.getId());
                tm.getTriggers().remove (t);
                
                tmpMsgList.add (new ResultMessage.MessageItem ("id", "Trigger ID \"" + t.getId() + "\" deleted."));
            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneItemDeleted) {
            try {
                tmDao.serialize (tm);
                result = new ResultMessage ("success", null, msgList);
            } catch (Exception e) {
                log.error ("Error serializing trigger module XML file: " + e.getMessage());
                result = new ResultMessage ("error", "Error serializing trigger module XML file: " + e.getMessage(), null);
            }
        } else {
            result = new ResultMessage ("error", null, msgList);
        }

        return result;
    }

    // validates an incoming server payload based on the requested operation
    //
    private List<ResultMessage.MessageItem> validate (
        Trigger t,
        TriggerModule tm,
        int operation
    ) {
        List<ResultMessage.MessageItem> result = new ArrayList<ResultMessage.MessageItem>();
        
        log.debug ("validating: operation = " + operation + ", tm = " + tm.getPath() + ", t = " + t.getId());
        
        if (t.getId() == null || t.getId().length() == 0) {
            result.add (new ResultMessage.MessageItem ("id", "Trigger ID may not be empty."));
        } else if (operation == DAOConstants.OPERATION_ADD){
            if (tm.findById (t.id) != null) {
                result.add (new ResultMessage.MessageItem ("id", "Trigger ID \"" + t.getId() + "\" already exists."));
            }
        } else if (operation == DAOConstants.OPERATION_EDIT || operation == DAOConstants.OPERATION_COPY || operation == DAOConstants.OPERATION_DELETE){
            if (tm.findById (((operation == DAOConstants.OPERATION_EDIT) ? t.getOrigId() : t.getId())) == null) {
                result.add (new ResultMessage.MessageItem ("id", "Trigger ID \"" + ((operation == DAOConstants.OPERATION_EDIT) ? t.getOrigId() : t.getId()) + "\" does not exist."));
                return result; // no need to go any further if the item doesn't exist.
            }
        }
        
        // no need to continue with copy and delete operations (a copied item has presumably been 
        // previously validated and we don't need validate an item we're deleting.)
        //
        if (operation == DAOConstants.OPERATION_COPY || operation == DAOConstants.OPERATION_DELETE)
            return result;
/*        
        if (t.getResourcePath() == null || t.getResourcePath().length() == 0) {
            result.add (new ResultMessage.MessageItem ("groupName", "Trigger resource path may not be empty."));
        }
*/
        return result;
    }
}

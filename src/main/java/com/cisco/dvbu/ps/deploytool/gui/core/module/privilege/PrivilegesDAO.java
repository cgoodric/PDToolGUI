package com.cisco.dvbu.ps.deploytool.gui.core.module.privilege;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.cisco.dvbu.ps.deploytool.gui.util.DAOConstants;
import com.cisco.dvbu.ps.deploytool.gui.resources.PrivilegeResource;
import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Data access object for users. Used by {@link PrivilegeResource} servlet.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class PrivilegesDAO {

    private static final Logger log = LoggerFactory.getLogger (PrivilegesDAO.class);
    private PrivilegeModulesDAO pmDao = new PrivilegeModulesDAO();

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public PrivilegesDAO () {
        super ();
    }
    
    /**
     * <p>
     * Searches for a single {@link Privilege} object given a URL encoded ID string.
     * </p>
     * 
     * @param  path  The URL encoded path to look for.
     * @param  id    The ID to look for.
     * @return       The requested {@link Privilege} as an object.
     */
    public Privilege findById (
        String path,
        String id
    ) {
        PrivilegeModule pm = pmDao.findByPath (path);
        if (pm == null) {
            log.error ("Unable to locate privilege module " + path);
        }
        
        log.debug ("Privilege module located. Looking for privilege ID " + id);
        return pm.findById (id);
    }
  
    /**
     * <p>
     * Adds a privilege to a privilege module.
     * </p>
     * 
     * @param  path  The path to the privilege module.
     * @param  p     The {@link Privilege} to add.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage add (
        String path,
        Privilege p
    ) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;
        
        PrivilegeModule pm = pmDao.findByPath (path);
        if (pm == null) {
            return new ResultMessage ("error", "Privilege module, " + path + ", does not exist!", null);
        }
        
        msgList = validate (p, pm, DAOConstants.OPERATION_ADD);
        
        if (msgList.size() == 0) {
        
            pm.getPrivileges().add (p);
        
            try {
                pmDao.serialize (pm);
                result = new ResultMessage ("success", "Created privilege " + p.getId(), null);
            } catch (Exception e) {
                log.error ("Error serializing privilege module XML file, " + path + ": " + e.getMessage());
                return new ResultMessage ("error", "Unable to serialize privilege module, " + path + ": " + e.getMessage(), null);
            }
        } else {
            return new ResultMessage ("error", null, msgList);
        }
        
        return result;
    }
  
    /**
     * <p>
     * Updates a privilege in a privilege module.
     * </p>
     * 
     * @param  path  The path to the user module.
     * @param  p     The {@link Privilege} to update.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage edit (
        String path,
        Privilege p
    ) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;
        Privilege origPrivilege = null;
        
        PrivilegeModule pm = pmDao.findByPath (path);
        if (pm == null) {
            return new ResultMessage ("error", "Privilege module, " + path + ", does not exist!", null);
        }
        
        for (Privilege tmp : pm.getPrivileges()) {
            if (tmp.getId().equals (p.getOrigId())) {
                origPrivilege = tmp;
                break;
            }
        }
        
        msgList = validate (p, pm, DAOConstants.OPERATION_EDIT);
        
        if (msgList.size() == 0) {
        
            pm.getPrivileges().remove (origPrivilege);
            pm.getPrivileges().add (p);
        
            try {
                pmDao.serialize (pm);
                result = new ResultMessage ("success", "Updated privilege " + p.getId(), null);
            } catch (Exception e) {
                log.error ("Error serializing privilege module XML file, " + path + ": " + e.getMessage());
                return new ResultMessage ("error", "Unable to serialize privilege module, " + path + ": " + e.getMessage(), null);
            }
        } else {
            return new ResultMessage ("error", null, msgList);
        }
        
        return result;
    }
    
    /**
     * <p>
     * Copies one or more privilege in a privilege module.
     * </p>
     * 
     * @param  path  The path to the privilege module.
     * @param  ids   The a comma separated list of privilege IDs to copy.
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
        
        PrivilegeModule pm = pmDao.findByPath (path);
        if (pm == null) {
            return new ResultMessage ("error", "Privilege module, " + path + ", does not exist!", null);
        }
        
        // iterate over all the input ids
        //
        String[] idArray = ids.split (",");
        for (String id : idArray) {
            List<ResultMessage.MessageItem> tmpMsgList;
            Privilege p = new Privilege();

            p.setId (id);
            
            // make sure the source item exists.
            //
            tmpMsgList = validate (p, pm, DAOConstants.OPERATION_COPY);
            
            // if so, then make the copy
            //
            if (tmpMsgList.size() == 0) {

                Privilege np = new Privilege (pm.findById (p.getId())); // find the original privilege and make a copy of it.
                
                if (np == null) {
                    tmpMsgList.add (new ResultMessage.MessageItem ("id", "Unable to locate privilege id \"" + p.getId() + "\"."));
                    continue;
                }
                
                atLeastOneItemCopied = true;

                // generate a new id for the copy. start with the original id + "_copy", then look for
                // id + "_copy1", id + "_copy2", etc.
                //
                int j = 0;
                do {
                    String newId = p.getId() + "_copy" + ((j == 0) ? "" : j);
                    
                    np.setId (newId);

                    j++;
                } while (validate (np, pm, DAOConstants.OPERATION_COPY).size() == 0);
                
                // add the copy of the element
                //
                pm.getPrivileges().add (np);
                
                tmpMsgList.add (new ResultMessage.MessageItem ("id", "Privilege ID \"" + p.getId() + "\" copied to \"" + np.getId() + "\"."));
            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneItemCopied) {
            try {
                pmDao.serialize (pm);
                result = new ResultMessage ("success", null, msgList);
            } catch (Exception e) {
                log.error ("Error serializing privilege module XML file: " + e.getMessage());
                result = new ResultMessage ("error", "Error serializing privilege module XML file: " + e.getMessage(), null);
            }
        } else {
            result = new ResultMessage ("error", null, msgList);
        }

        return result;
    }
    
    /**
     * <p>
     * Deletes one or more privileges in a privilege module.
     * </p>
     * 
     * @param  path  The path to the privilege module.
     * @param  ids   The a comma separated list of privilege IDs to delete.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage delete (
        String path,
        String ids
    ) {
        List<ResultMessage.MessageItem> msgList = new ArrayList<ResultMessage.MessageItem>();
        boolean atLeastOneItemDeleted = false;
        ResultMessage result;
        
        PrivilegeModule pm = pmDao.findByPath (path);
        if (pm == null) {
            return new ResultMessage ("error", "Privilege module, " + path + ", does not exist!", null);
        }
        
        // iterate over all the input ids
        //
        String[] idArray = ids.split (",");
        for (String id : idArray) {
            List<ResultMessage.MessageItem> tmpMsgList;
            Privilege p = new Privilege();

            p.setId (id);
            
            // make sure the item exists.
            //
            tmpMsgList = validate (p, pm, DAOConstants.OPERATION_DELETE);
            
            // if so, then make sure the item doesn't :)
            //
            if (tmpMsgList.size() == 0) {
                atLeastOneItemDeleted = true;
                
                // get rid of the server element
                //
                p = pm.findById (p.getId());
                pm.getPrivileges().remove (p);
                
                tmpMsgList.add (new ResultMessage.MessageItem ("id", "Privilege ID \"" + p.getId() + "\" deleted."));
            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneItemDeleted) {
            try {
                pmDao.serialize (pm);
                result = new ResultMessage ("success", null, msgList);
            } catch (Exception e) {
                log.error ("Error serializing privilege module XML file: " + e.getMessage());
                result = new ResultMessage ("error", "Error serializing privilege module XML file: " + e.getMessage(), null);
            }
        } else {
            result = new ResultMessage ("error", null, msgList);
        }

        return result;
    }

    // validates an incoming server payload based on the requested operation
    //
    private List<ResultMessage.MessageItem> validate (
        Privilege p,
        PrivilegeModule pm,
        int operation
    ) {
        List<ResultMessage.MessageItem> result = new ArrayList<ResultMessage.MessageItem>();
        
        log.debug ("validating: operation = " + operation + ", pm = " + pm.getPath() + ", p = " + p.getId());
        
        if (p.getId() == null || p.getId().length() == 0) {
            result.add (new ResultMessage.MessageItem ("id", "Privilege ID may not be empty."));
        } else if (operation == DAOConstants.OPERATION_ADD){
            if (pm.findById (p.id) != null) {
                result.add (new ResultMessage.MessageItem ("id", "Privilege ID \"" + p.getId() + "\" already exists."));
            }
        } else if (operation == DAOConstants.OPERATION_EDIT || operation == DAOConstants.OPERATION_COPY || operation == DAOConstants.OPERATION_DELETE){
            if (pm.findById (((operation == DAOConstants.OPERATION_EDIT) ? p.getOrigId() : p.getId())) == null) {
                result.add (new ResultMessage.MessageItem ("id", "Privilege ID \"" + ((operation == DAOConstants.OPERATION_EDIT) ? p.getOrigId() : p.getId()) + "\" does not exist."));
                return result; // no need to go any further if the item doesn't exist.
            }
        }
        
        // no need to continue with copy and delete operations (a copied item has presumably been 
        // previously validated and we don't need validate an item we're deleting.)
        //
        if (operation == DAOConstants.OPERATION_COPY || operation == DAOConstants.OPERATION_DELETE)
            return result;
        
        if (p.getResourcePath() == null || p.getResourcePath().length() == 0) {
            result.add (new ResultMessage.MessageItem ("resourcePath", "Resource Path may not be empty."));
        }
        
        if (p.getPrivileges() == null || p.getPrivileges().size() == 0) {
            result.add (new ResultMessage.MessageItem ("privilege", "Must have at least one privilege defined."));
        } else {
            for (Privilege.PrivilegeItem pi : p.getPrivileges()) {
                if (pi.getName() == null || pi.getName().length() == 0) {
                    result.add (new ResultMessage.MessageItem ("priv_name", "Privilege user/group name may not be empty."));
                }
            }
        }
        
        return result;
    }
}

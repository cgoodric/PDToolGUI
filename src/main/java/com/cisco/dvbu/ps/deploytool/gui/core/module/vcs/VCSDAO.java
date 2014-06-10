package com.cisco.dvbu.ps.deploytool.gui.core.module.vcs;

import com.cisco.dvbu.ps.deploytool.gui.util.DAOConstants;
import com.cisco.dvbu.ps.deploytool.gui.resources.VCSResource;
import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Data access object for VCS records. Used by {@link VCSResource} servlet.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class VCSDAO {

    private static final Logger log = LoggerFactory.getLogger (VCSDAO.class);
    private VCSModulesDAO vmDao = new VCSModulesDAO();

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public VCSDAO () {
        super ();
    }
    
    /**
     * <p>
     * Searches for a single {@link VCS} object given a URL encoded ID string.
     * </p>
     * 
     * @param  path  The URL encoded path to look for.
     * @param  id    The ID to look for.
     * @return       The requested {@link VCS} as an object.
     */
    public VCS findById (
        String path,
        String id
    ) {
        VCSModule vm = vmDao.findByPath (path);
        if (vm == null) {
            log.error ("Unable to locate VCS module " + path);
        }
        
        log.debug ("VCS module located. Looking for VCS ID " + id);
        return vm.findById (id);
    }
  
    /**
     * <p>
     * Adds a VCS to a VCS module.
     * </p>
     * 
     * @param  path  The path to the VCS module.
     * @param  v     The {@link VCS} to add.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage add (
        String path,
        VCS v
    ) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;
        
        VCSModule vm = vmDao.findByPath (path);
        if (vm == null) {
            return new ResultMessage ("error", "VCS module, " + path + ", does not exist!", null);
        }
        
        msgList = validate (v, vm, DAOConstants.OPERATION_ADD);
        
        if (msgList.size() == 0) {
        
            vm.getVcsList().add (v);
            
            try {
                vmDao.serialize (vm);
                result = new ResultMessage ("success", "Created VCS record " + v.getId(), null);
            } catch (Exception e) {
                log.error ("Error serializing VCS module XML file, " + path + ": " + e.getMessage());
                return new ResultMessage ("error", "Unable to serialize VCS module, " + path + ": " + e.getMessage(), null);
            }
        } else {
            return new ResultMessage ("error", null, msgList);
        }
        
        return result;
    }
  
    /**
     * <p>
     * Updates a VCS in a VCS module.
     * </p>
     * 
     * @param  path  The path to the VCS module.
     * @param  v     The {@link VCS} to update.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage edit (
        String path,
        VCS v
    ) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;
        VCS origVCS = null;
        
        VCSModule vm = vmDao.findByPath (path);
        if (vm == null) {
            return new ResultMessage ("error", "VCS module, " + path + ", does not exist!", null);
        }
        
        for (VCS tmp : vm.getVcsList()) {
            if (tmp.getId().equals (v.getOrigId())) {
                origVCS = tmp;
                break;
            }
        }
        
        msgList = validate (v, vm, DAOConstants.OPERATION_EDIT);
        
        if (msgList.size() == 0) {
        
            vm.getVcsList().remove (origVCS);
            vm.getVcsList().add (v);
        
            try {
                vmDao.serialize (vm);
                result = new ResultMessage ("success", "Updated VCS " + v.getId(), null);
            } catch (Exception e) {
                log.error ("Error serializing VCS module XML file, " + path + ": " + e.getMessage());
                return new ResultMessage ("error", "Unable to serialize VCS module, " + path + ": " + e.getMessage(), null);
            }
        } else {
            return new ResultMessage ("error", null, msgList);
        }
        
        return result;
    }
    
    /**
     * <p>
     * Copies one or more VCS records in a VCS module.
     * </p>
     * 
     * @param  path  The path to the VCS module.
     * @param  ids   The a comma separated list of VCS IDs to copy.
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
        
        VCSModule vm = vmDao.findByPath (path);
        if (vm == null) {
            return new ResultMessage ("error", "VCS module, " + path + ", does not exist!", null);
        }
        
        // iterate over all the input ids
        //
        String[] idArray = ids.split (",");
        for (String id : idArray) {
            List<ResultMessage.MessageItem> tmpMsgList;
            VCS v = new VCS();

            v.setId (id);
            
            // make sure the source item exists.
            //
            tmpMsgList = validate (v, vm, DAOConstants.OPERATION_COPY);
            
            // if so, then make the copy
            //
            if (tmpMsgList.size() == 0) {

                VCS nv = new VCS (vm.findById (v.getId())); // find the original VCS record and make a copy of it.
                
                if (nv == null) {
                    tmpMsgList.add (new ResultMessage.MessageItem ("id", "Unable to locate VCS id \"" + v.getId() + "\"."));
                    continue;
                }
                
                atLeastOneItemCopied = true;

                // generate a new id for the copy. start with the original id + "_copy", then look for
                // id + "_copy1", id + "_copy2", etc.
                //
                int j = 0;
                do {
                    String newId = v.getId() + "_copy" + ((j == 0) ? "" : j);
                    
                    nv.setId (newId);

                    j++;
                } while (validate (nv, vm, DAOConstants.OPERATION_COPY).size() == 0);
                
                // add the copy of the element
                //
                vm.getVcsList().add (nv);
                
                tmpMsgList.add (new ResultMessage.MessageItem ("id", "VCS ID \"" + v.getId() + "\" copied to \"" + nv.getId() + "\"."));
            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneItemCopied) {
            try {
                vmDao.serialize (vm);
                result = new ResultMessage ("success", null, msgList);
            } catch (Exception e) {
                log.error ("Error serializing VCS module XML file: " + e.getMessage());
                result = new ResultMessage ("error", "Error serializing group module XML file: " + e.getMessage(), null);
            }
        } else {
            result = new ResultMessage ("error", null, msgList);
        }

        return result;
    }
    
    /**
     * <p>
     * Deletes one or more VCS records in a VCS module.
     * </p>
     * 
     * @param  path  The path to the VCS module.
     * @param  ids   The a comma separated list of VCS IDs to delete.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage delete (
        String path,
        String ids
    ) {
        List<ResultMessage.MessageItem> msgList = new ArrayList<ResultMessage.MessageItem>();
        boolean atLeastOneItemDeleted = false;
        ResultMessage result;
        
        VCSModule vm = vmDao.findByPath (path);
        if (vm == null) {
            return new ResultMessage ("error", "VCS module, " + path + ", does not exist!", null);
        }
        
        // iterate over all the input ids
        //
        String[] idArray = ids.split (",");
        for (String id : idArray) {
            List<ResultMessage.MessageItem> tmpMsgList;
            VCS v = new VCS();

            v.setId (id);
            
            // make sure the item exists.
            //
            tmpMsgList = validate (v, vm, DAOConstants.OPERATION_DELETE);
            
            // if so, then make sure the item doesn't :)
            //
            if (tmpMsgList.size() == 0) {
                atLeastOneItemDeleted = true;
                
                // get rid of the group element
                //
                v = vm.findById (v.getId());
                vm.getVcsList().remove (v);
                
                tmpMsgList.add (new ResultMessage.MessageItem ("id", "VCS ID \"" + v.getId() + "\" deleted."));
            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneItemDeleted) {
            try {
                vmDao.serialize (vm);
                result = new ResultMessage ("success", null, msgList);
            } catch (Exception e) {
                log.error ("Error serializing VCS module XML file: " + e.getMessage());
                result = new ResultMessage ("error", "Error serializing VCS module XML file: " + e.getMessage(), null);
            }
        } else {
            result = new ResultMessage ("error", null, msgList);
        }

        return result;
    }

    // validates an incoming server payload based on the requested operation
    //
    private List<ResultMessage.MessageItem> validate (
        VCS v,
        VCSModule vm,
        int operation
    ) {
        List<ResultMessage.MessageItem> result = new ArrayList<ResultMessage.MessageItem>();
        
        log.debug ("validating: operation = " + operation + ", vm = " + vm.getPath() + ", v = " + v.getId());
        
        if (v.getId() == null || v.getId().length() == 0) {
            result.add (new ResultMessage.MessageItem ("id", "VCS ID may not be empty."));
        } else if (operation == DAOConstants.OPERATION_ADD){
            if (vm.findById (v.id) != null) {
                result.add (new ResultMessage.MessageItem ("id", "VCS ID \"" + v.getId() + "\" already exists."));
            }
        } else if (operation == DAOConstants.OPERATION_EDIT || operation == DAOConstants.OPERATION_COPY || operation == DAOConstants.OPERATION_DELETE){
            if (vm.findById (((operation == DAOConstants.OPERATION_EDIT) ? v.getOrigId() : v.getId())) == null) {
                result.add (new ResultMessage.MessageItem ("id", "VCS ID \"" + ((operation == DAOConstants.OPERATION_EDIT) ? v.getOrigId() : v.getId()) + "\" does not exist."));
                return result; // no need to go any further if the item doesn't exist.
            }
        }
        
        // no need to continue with copy and delete operations (a copied item has presumably been 
        // previously validated and we don't need validate an item we're deleting.)
        //
        if (operation == DAOConstants.OPERATION_COPY || operation == DAOConstants.OPERATION_DELETE)
            return result;
/*        if (g.getGroupName() == null || g.getGroupName().length() == 0) {
            result.add (new ResultMessage.MessageItem ("groupName", "Group Name may not be empty."));
        }
        
        if (g.getGroupDomain() == null || g.getGroupDomain().length() == 0) {
            result.add (new ResultMessage.MessageItem ("groupDomain", "Group Domain may not be empty."));
        }
*/
        return result;
    }
}

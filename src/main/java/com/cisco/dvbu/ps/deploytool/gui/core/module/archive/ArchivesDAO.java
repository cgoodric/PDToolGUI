package com.cisco.dvbu.ps.deploytool.gui.core.module.archive;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.cisco.dvbu.ps.deploytool.gui.util.DAOConstants;
import com.cisco.dvbu.ps.deploytool.gui.resources.ArchiveResource;
import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Data access object for archives. Used by {@link ArchiveResource} servlet.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class ArchivesDAO {

    private static final Logger log = LoggerFactory.getLogger (ArchivesDAO.class);
    private ArchiveModulesDAO amDao = new ArchiveModulesDAO();

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public ArchivesDAO () {
        super ();
    }
    
    /**
     * <p>
     * Searches for a single {@link Archive} object given a URL encoded ID string.
     * </p>
     * 
     * @param  path  The URL encoded path to look for.
     * @param  id    The ID to look for.
     * @return       The requested {@link Archive} as an object.
     */
    public Archive findById (
        String path,
        String id
    ) {
        ArchiveModule am = amDao.findByPath (path);
        if (am == null) {
            log.error ("Unable to locate archive module " + path);
        }
        
        log.debug ("Archive module located. Looking for archive ID " + id);
        return am.findById (id);
    }
  
    /**
     * <p>
     * Adds an archive to an archive module.
     * </p>
     * 
     * @param  path  The path to the archive module.
     * @param  a     The {@link Archive} to add.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage add (
        String path,
        Archive a
    ) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;
        
        ArchiveModule am = amDao.findByPath (path);
        if (am == null) {
            return new ResultMessage ("error", "Archive module, " + path + ", does not exist!", null);
        }
        
        msgList = validate (a, am, DAOConstants.OPERATION_ADD);
        
        if (msgList.size() == 0) {
        
        am.getArchives().add (a);
        
            try {
                amDao.serialize (am);
                result = new ResultMessage ("success", "Created archive " + a.getId(), null);
            } catch (Exception e) {
                log.error ("Error serializing archive module XML file, " + path + ": " + e.getMessage());
                return new ResultMessage ("error", "Unable to serialize archive module, " + path + ": " + e.getMessage(), null);
            }
        } else {
            return new ResultMessage ("error", null, msgList);
        }
        
        return result;
    }
  
    /**
     * <p>
     * Updates an archive in an archive module.
     * </p>
     * 
     * @param  path  The path to the archive module.
     * @param  a     The {@link Archive} to update.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage edit (
        String path,
        Archive a
    ) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;
        Archive origArchive = null;
        
        ArchiveModule am = amDao.findByPath (path);
        if (am == null) {
            return new ResultMessage ("error", "Archive module, " + path + ", does not exist!", null);
        }
        
        for (Archive tmp : am.getArchives()) {
            if (tmp.getId().equals (a.getOrigId())) {
                origArchive = tmp;
                break;
            }
        }
        
        msgList = validate (a, am, DAOConstants.OPERATION_EDIT);
        
        if (msgList.size() == 0) {
        
            am.getArchives().remove(origArchive);
            am.getArchives().add (a);
        
            try {
                amDao.serialize (am);
                result = new ResultMessage ("success", "Updated archive " + a.getId(), null);
            } catch (Exception e) {
                log.error ("Error serializing archive module XML file, " + path + ": " + e.getMessage());
                return new ResultMessage ("error", "Unable to serialize archive module, " + path + ": " + e.getMessage(), null);
            }
        } else {
            return new ResultMessage ("error", null, msgList);
        }
        
        return result;
    }
    
    /**
     * <p>
     * Copies one or more archives in an archive module.
     * </p>
     * 
     * @param  path  The path to the archive module.
     * @param  ids   The a comma separated list of archive IDs to copy.
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
        
        ArchiveModule am = amDao.findByPath (path);
        if (am == null) {
            return new ResultMessage ("error", "Archive module, " + path + ", does not exist!", null);
        }
        
        // iterate over all the input ids
        //
        String[] idArray = ids.split (",");
        for (String id : idArray) {
            List<ResultMessage.MessageItem> tmpMsgList;
            Archive a = new Archive();

            a.setId (id);
            
            // make sure the source item exists.
            //
            tmpMsgList = validate (a, am, DAOConstants.OPERATION_COPY);
            
            // if so, then make the copy
            //
            if (tmpMsgList.size() == 0) {

                Archive na = new Archive (am.findById (a.getId())); // find the original archive and make a copy of it.
                
                if (na == null) {
                    tmpMsgList.add (new ResultMessage.MessageItem ("id", "Unable to locate archive id \"" + a.getId() + "\"."));
                    continue;
                }
                
                atLeastOneItemCopied = true;

                // generate a new id for the copy. start with the original id + "_copy", then look for
                // id + "_copy1", id + "_copy2", etc.
                //
                int j = 0;
                do {
                    String newId = a.getId() + "_copy" + ((j == 0) ? "" : j);
                    
                    na.setId (newId);

                    j++;
                } while (validate (na, am, DAOConstants.OPERATION_COPY).size() == 0);
                
                // add the copy of the element
                //
                am.getArchives().add (na);
                
                tmpMsgList.add (new ResultMessage.MessageItem ("id", "Archive ID \"" + a.getId() + "\" copied to \"" + na.getId() + "\"."));
            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneItemCopied) {
            try {
                amDao.serialize (am);
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
    
    /**
     * <p>
     * Deletes one or more archives in an archive module.
     * </p>
     * 
     * @param  path  The path to the archive module.
     * @param  ids   The a comma separated list of archive IDs to delete.
     * @return       A message or messages with the result of the operation.
     */
    public ResultMessage delete (
        String path,
        String ids
    ) {
        List<ResultMessage.MessageItem> msgList = new ArrayList<ResultMessage.MessageItem>();
        boolean atLeastOneItemDeleted = false;
        ResultMessage result;
        
        ArchiveModule am = amDao.findByPath (path);
        if (am == null) {
            return new ResultMessage ("error", "Archive module, " + path + ", does not exist!", null);
        }
        
        // iterate over all the input ids
        //
        String[] idArray = ids.split (",");
        for (String id : idArray) {
            List<ResultMessage.MessageItem> tmpMsgList;
            Archive a = new Archive();

            a.setId (id);
            
            // make sure the item exists.
            //
            tmpMsgList = validate (a, am, DAOConstants.OPERATION_DELETE);
            
            // if so, then make sure the item doesn't :)
            //
            if (tmpMsgList.size() == 0) {
                atLeastOneItemDeleted = true;
                
                // get rid of the server element
                //
                a = am.findById (a.getId());
                am.getArchives().remove (a);
                
                tmpMsgList.add (new ResultMessage.MessageItem ("id", "Archive ID \"" + a.getId() + "\" deleted."));
            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneItemDeleted) {
            try {
                amDao.serialize (am);
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

    // validates an incoming server payload based on the requested operation
    //
    private List<ResultMessage.MessageItem> validate (
        Archive a,
        ArchiveModule am,
        int operation
    ) {
        List<ResultMessage.MessageItem> result = new ArrayList<ResultMessage.MessageItem>();
        
        log.debug ("validating: operation = " + operation + ", am = " + am.getPath() + ", a = " + a.getId());
        
        if (a.getId() == null || a.getId().length() == 0) {
            result.add (new ResultMessage.MessageItem ("id", "Archive ID may not be empty."));
        } else if (operation == DAOConstants.OPERATION_ADD){
            if (am.findById (a.id) != null) {
                result.add (new ResultMessage.MessageItem ("id", "Archive ID \"" + a.getId() + "\" already exists."));
            }
        } else if (operation == DAOConstants.OPERATION_EDIT || operation == DAOConstants.OPERATION_COPY || operation == DAOConstants.OPERATION_DELETE){
            if (am.findById (((operation == DAOConstants.OPERATION_EDIT) ? a.getOrigId() : a.getId())) == null) {
                result.add (new ResultMessage.MessageItem ("id", "Archive ID \"" + ((operation == DAOConstants.OPERATION_EDIT) ? a.getOrigId() : a.getId()) + "\" does not exist."));
                return result; // no need to go any further if the item doesn't exist.
            }
        }
        
        // no need to continue with copy and delete operations (a copied item has presumably been 
        // previously validated and we don't need validate an item we're deleting.)
        //
        if (operation == DAOConstants.OPERATION_COPY || operation == DAOConstants.OPERATION_DELETE)
            return result;
        
        if (a.getArchiveFileName() == null || a.getArchiveFileName().length() == 0) {
            result.add (new ResultMessage.MessageItem ("archiveFileName", "Archive File Name may not be empty."));
        }
        
        return result;
    }
}

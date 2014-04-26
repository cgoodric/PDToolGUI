package com.cisco.dvbu.ps.deploytool.gui.core.config.server;

import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.PreferencesManager;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.file.FilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.util.DAOConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import org.apache.commons.io.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.dvbu.ps.deploytool.gui.resources.ServerResource;
import com.cisco.dvbu.ps.deploytool.gui.resources.ServerListResource;
import com.cisco.dvbu.ps.deploytool.gui.util.ListColumnInfo;
import com.cisco.dvbu.ps.deploytool.gui.util.ListResult;
import com.cisco.dvbu.ps.deploytool.gui.util.ListResultRowComparator;
import com.cisco.dvbu.ps.deploytool.gui.util.ListUtils;
import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;

/**
 * <p>
 * Data access object for servers. Used by {@link ServerResource} and {@link ServerListResource} servlets.
 * </p>
 *
 * @author Calvin Goodrich
 * @version 1.0
 */
public class ServersDAO {
    
    private static final Logger log = LoggerFactory.getLogger (ServersDAO.class);

    private Map<String, ListColumnInfo> listColumnsInfo = new HashMap<String, ListColumnInfo>();

    private Document serversDoc;
    private List<ListResult.Row> serversList = new ArrayList<ListResult.Row>();
    private Map<String, Server> servers = new HashMap<String, Server>();
    private long serversXmlLastUpdated = new Date(0).getTime();
    private XMLOutputter xmlo = new XMLOutputter();
    
    /**
     * <p>
     * Constructor.
     * </p>
     */
    public ServersDAO() {
        super ();
        
        // set up the column list for server lists
        //
        listColumnsInfo.put ("sid",               new ListColumnInfo (0, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("hostname",          new ListColumnInfo (1, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("port",              new ListColumnInfo (2, ListResultRowComparator.SORT_TYPE_NUMBER));
        listColumnsInfo.put ("usage",             new ListColumnInfo (3, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("user",              new ListColumnInfo (4, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("encryptedpassword", new ListColumnInfo (5, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("domain",            new ListColumnInfo (6, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("cishome",           new ListColumnInfo (7, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("clustername",       new ListColumnInfo (8, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("site",              new ListColumnInfo (9, ListResultRowComparator.SORT_TYPE_STRING));
        listColumnsInfo.put ("useHttps",          new ListColumnInfo (10, ListResultRowComparator.SORT_TYPE_STRING));
        
        // initialize servers list from servers.xml
        //
        getServers();
    }

    /*
     * Servlet methods
     */

    /**
     * <p>
     * Sorts and filters the list of {@link Server} objects and returns a {@link ListResult} object
     * containing the requested page of information.
     * </p>
     * 
     * @param isSearch     Indicates whether the request is a search request ("true" or "false".)
     * @param numRows      Number of rows to return.
     * @param pageNum      Page number to return (based on numRows.)
     * @param sortIndex    Data element to sort on (as represented by a column in the UI.)
     * @param sortOrder    Sort order ("asc" or "desc".)
     * @param filters      Multi-row filtering (not currently used.)
     * @param searchField  Search field to search on (isSearch = "true".)
     * @param searchString String to search for (isSearch = "true".)
     * @param searchOper   Search operation (equals or "eq", not equals or "ne", etc.)
     * @param param        Parameter name identifier to pass back when called to generate parameter value pick lists.
     * @return             The requested list of items.
     * @see                ServerListResource
     */
    public ListResult list(
        String isSearch,
        int    numRows,
        int    pageNum,
        String sortIndex,
        String sortOrder,
        String filters,
        String searchField,
        String searchString,
        String searchOper,
        String param
    ) {
        List<ListResult.Row> resultList;

        // refresh servers list from servers.xml file
        //
        getServers();
        
        // get current list of servers in display list form
        //
        resultList = this.serversList;
        
        // apply search rules, if any
        //
        if (isSearch != null && isSearch.equalsIgnoreCase ("true")) {
            resultList = ListUtils.applySearchRules (
                             resultList, 
                             listColumnsInfo, 
                             searchField, 
                             searchString, 
                             searchOper
                         );
        }
        
        // apply sorting to the servers list
        //
        if (sortIndex != null && sortOrder != null) {
            Collections.sort (resultList, new ListResultRowComparator (
                                                  listColumnsInfo.get(sortIndex).getIndex(), 
                                                  sortOrder, 
                                                  listColumnsInfo.get(sortIndex).getSortType()
                                              )
            );
        }
        
        return ListUtils.getListResult (
                   resultList, 
                   numRows, 
                   pageNum,
                   param
               );
    }
    
    /**
     * <p>
     * Searches for a single {@link Server} object given a URL encoded ID string.
     * </p>
     * 
     * @param  id  The URL encoded ID to look for.
     * @return     The requested {@link Server} as an object.
     * @see        ServerResource
     */
    public Server findById (String id) {
        getServers();

        return servers.get (id);
    }

    /**
     * <p>
     * Adds a {@link Server} object to the servers list and writes the new servers list to disk.
     * </p>
     * 
     * @param  server  The {@link Server} object to add.
     * @return         A {@link ResultMessage} object containing the results of the add request.
     * @see            ServerResource
     */
    public ResultMessage add (Server server) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;

        // make sure we have the latest version of the servers.xml file
        //
        getServers();
        
        // sanity check the inputs for an "add" operation
        //
        msgList = validate (server, DAOConstants.OPERATION_ADD);

        if (msgList.size() == 0) {
            serversDoc.getRootElement().addContent ("  ");
            serversDoc.getRootElement().addContent (createServerElement (server));
            serversDoc.getRootElement().addContent ("\n");
            
            try {
                serialize();
                result = new ResultMessage ("success", "Created server " + server.getId(), null);
            } catch (Exception e) {
                log.error ("Error serializing servers XML file: " + e.getMessage());
                result = new ResultMessage ("error", "Error serializing servers XML file: " + e.getMessage(), null);
            }
        } else {
            result = new ResultMessage ("error", null, msgList);
        }
        
        return result;
    }

    /**
     * <p>
     * Updates a {@link Server} object in the servers list and writes the updated servers list to disk.
     * </p>
     * 
     * @param  server  The {@link Server} object to update.
     * @return         A {@link ResultMessage} object containing the results of the edit request.
     * @see            ServerResource
     */
    public ResultMessage edit (Server server) {
        List<ResultMessage.MessageItem> msgList;
        ResultMessage result;

        // make sure we have the latest version of the servers.xml file
        //
        getServers();
        
        // sanity check the inputs for an "edit" operation
        //
        msgList = validate (server, DAOConstants.OPERATION_EDIT);

        if (msgList.size() == 0) {

            // get rid of the old server element
            //
            Element oldServer = getServerElement (server.getOrigid());
            serversDoc.getRootElement().removeContent (oldServer);
            
            // replace the new server element
            //
            serversDoc.getRootElement().addContent ("  ");
            serversDoc.getRootElement().addContent (createServerElement (server));
            serversDoc.getRootElement().addContent ("\n");
            
            try {
                serialize();
                result = new ResultMessage ("success", "Updated server " + server.getId(), null);
            } catch (Exception e) {
                log.error ("Error serializing servers XML file: " + e.getMessage());
                result = new ResultMessage ("error", "Error serializing servers XML file: " + e.getMessage(), null);
            }
        } else {
            result = new ResultMessage ("error", null, msgList);
        }
        
        return result;
    }

    /**
     * <p>
     * Copies a list of {@link Server} objects in the servers list and writes the updated servers list to disk.
     * </p>
     * 
     * @param  ids A comma separated list of server IDs to copy.
     * @return     A {@link ResultMessage} object containing the results of the copy request.
     * @see        ServerResource
     */
    public ResultMessage copy (String ids) {
        List<ResultMessage.MessageItem> msgList = new ArrayList<ResultMessage.MessageItem>();
        boolean atLeastOneServerCopied = false;
        ResultMessage result;
        
        // make sure we have the latest version of the servers.xml file
        //
        getServers();
        
        // iterate over all the input server ids
        //
        String[] idArray = ids.split (",");
        for (int i = 0; i < idArray.length; i++) {
            List<ResultMessage.MessageItem> tmpMsgList;
            Server s = new Server();
            s.setId (idArray[i]);
            
            // make sure the source server exists.
            //
            tmpMsgList = validate (s, DAOConstants.OPERATION_COPY);
            
            // if so, then make the copy
            //
            if (tmpMsgList.size() == 0) {

                atLeastOneServerCopied = true;
                Server ns = null;
                
                try {
                   ns = findById (s.getId());
                } catch (Exception e) {
                    log.error ("error encoding server id \"" + s.getId() + "\" for lookup of server to copy.");
                }
                
                if (ns == null) {
                    tmpMsgList.add (new ResultMessage.MessageItem ("id", "Unable to locate server id \"" + s.getId() + "\"."));
                    continue;
                }
                
                // generate a new server id for the copy. start with the original id + "_copy", then look for
                // id + "_copy1", id + "_copy2", etc.
                //
                int j = 0;
                do {
                    String newId = s.getId() + "_copy" + ((j == 0) ? "" : j);
                    
                    ns.setId (newId);

                    j++;
                } while (validate (ns, DAOConstants.OPERATION_COPY).size() == 0);
    
                // add the copy of the server element
                //
                serversDoc.getRootElement().addContent ("  ");
                serversDoc.getRootElement().addContent (createServerElement (ns));
                serversDoc.getRootElement().addContent ("\n");
                
                tmpMsgList.add (new ResultMessage.MessageItem ("id", "Server ID \"" + s.getId() + "\" copied to \"" + ns.getId() + "\"."));
            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneServerCopied) {
            try {
                serialize();
                result = new ResultMessage ("success", null, msgList);
            } catch (Exception e) {
                log.error ("Error serializing servers XML file: " + e.getMessage());
                result = new ResultMessage ("error", "Error serializing servers XML file: " + e.getMessage(), null);
            }
        } else {
            result = new ResultMessage ("error", null, msgList);
        }

        return result;
    }

    /**
     * <p>
     * Deletes a list of {@link Server} objects in the servers list and writes the updated servers list to disk.
     * </p>
     * 
     * @param  ids A comma separated list of server IDs to delete.
     * @return     A {@link ResultMessage} object containing the results of the delete request.
     * @see        ServerResource
     */
    public ResultMessage delete (String ids) {
        List<ResultMessage.MessageItem> msgList = new ArrayList<ResultMessage.MessageItem>();
        boolean atLeastOneServerDeleted = false;
        ResultMessage result;
        
        // make sure we have the latest version of the servers.xml file
        //
        getServers();
        
        // iterate over all the input server ids
        //
        String[] idArray = ids.split (",");
        for (int i = 0; i < idArray.length; i++) {
            List<ResultMessage.MessageItem> tmpMsgList;
            Server s = new Server();
            s.setId (idArray[i]);
            
            // make sure the server exists.
            //
            tmpMsgList = validate (s, DAOConstants.OPERATION_DELETE);
            
            // if so, then make sure the server doesn't :)
            //
            if (tmpMsgList.size() == 0) {
                atLeastOneServerDeleted = true;
                
                // get rid of the server element
                //
                Element serverElement = getServerElement (s.getId());
                serversDoc.getRootElement().removeContent (serverElement);
                
                tmpMsgList.add (new ResultMessage.MessageItem ("id", "Server ID \"" + s.getId() + "\" deleted."));
            }

            msgList.addAll (tmpMsgList);
        }

        if (atLeastOneServerDeleted) {
            try {
                serialize();
                result = new ResultMessage ("success", null, msgList);
            } catch (Exception e) {
                log.error ("Error serializing servers XML file: " + e.getMessage());
                result = new ResultMessage ("error", "Error serializing servers XML file: " + e.getMessage(), null);
            }
        } else {
            result = new ResultMessage ("error", null, msgList);
        }

        return result;
    }
    
    /*
     * Supplemental methods.
     */
    
    // get the server list from the servers XML file. extracts into DOM document and also a hash table
    // for lookups and a sortable/filterable list for server list requests.
    //
    private Map<String, Server> getServers() {
        SAXBuilder builder = new SAXBuilder();
        String pdtHome = System.getProperty ("apps.install.dir");
        List<ListResult.Row> tmpServersList = new ArrayList<ListResult.Row>();
        Map<String, Server> tmpServers = new HashMap<String, Server>();
        
        if (pdtHome == null || pdtHome.length() == 0) {
            log.error ("System property apps.install.dir not set!");
            return this.servers;
        }
        
        File f = new File(pdtHome + "/resources/modules/servers.xml");
        if (! f.exists()) {
            log.error ("Unable to find servers.xml at \"" + pdtHome + "/resources/modules/servers.xml\".");
            return this.servers;
        }
        if (! f.canRead()) {
            log.error ("Unable to read servers.xml at \"" + pdtHome + "/resources/modules/servers.xml\".");
            return this.servers;
        }
        if (f.lastModified() == serversXmlLastUpdated) { // file hasn't changed so keep using in-memory list
            return this.servers;
        }

        try {
            serversDoc = builder.build (pdtHome + "/resources/modules/servers.xml");
            Element rootNode = serversDoc.getRootElement();
            List<Element> list = rootNode.getChildren ("server");
            
            for (Element node : list) {
                List<String> tmpCell = new ArrayList<String>();
                Server tmpServer = new Server();
            
                tmpCell.add (node.getChildText ("id"));
                tmpServer.setId(node.getChildText ("id"));
                
                tmpCell.add (node.getChildText ("hostname"));
                tmpServer.setHostname(node.getChildText ("hostname"));
                
                tmpCell.add (node.getChildText ("port"));
                tmpServer.setPort(node.getChildText ("port"));
                
                tmpCell.add (node.getChildText ("usage"));
                tmpServer.setUsage(node.getChildText ("usage"));
                
                tmpCell.add (node.getChildText ("user"));
                tmpServer.setUser(node.getChildText ("user"));
                
                tmpCell.add (node.getChildText ("encryptedpassword"));
                tmpServer.setEncryptedpassword (node.getChildText ("encryptedpassword"));
                
                tmpCell.add (node.getChildText ("domain"));
                tmpServer.setDomain(node.getChildText ("domain"));
                
                tmpCell.add (node.getChildText ("cishome"));
                tmpServer.setCishome(node.getChildText ("cishome"));
                
                tmpCell.add (node.getChildText ("clustername"));
                tmpServer.setClustername(node.getChildText ("clustername"));
                
                tmpCell.add (node.getChildText ("site"));
                tmpServer.setSite(node.getChildText ("site"));

                tmpCell.add ("" + ((node.getChildText ("useHttps") != null ) ? node.getChildText ("useHttps").matches ("(?i)^(yes|true|on|1)$") : false));
                tmpServer.setUseHttps ((node.getChildText ("useHttps") != null ) ? node.getChildText ("useHttps").matches ("(?i)^(yes|true|on|1)$") : false);

                tmpServersList.add (new ListResult.Row(tmpServer.getId(), tmpCell));
                tmpServers.put (tmpServer.getId(), tmpServer);
            }
            
        } catch (IOException io) {
            log.error ("getServers() unable to load servers.xml:" + io.getMessage());
        } catch (JDOMException jdomex) {
            log.error ("getServers() unable to load servers.xml:" + jdomex.getMessage());
        }

        this.serversList = tmpServersList;
        this.servers = tmpServers;
        serversXmlLastUpdated = f.lastModified();

        return this.servers;
    }
    
    // validates an incoming server payload based on the requested operation
    //
    private List<ResultMessage.MessageItem> validate (
        Server server,
        int operation
    ) {
        List<ResultMessage.MessageItem> result = new ArrayList<ResultMessage.MessageItem>();
        
        if (server.getId() == null || server.getId().length() == 0) {
            result.add (new ResultMessage.MessageItem ("id", "Server ID may not be empty."));
        } else if (operation == DAOConstants.OPERATION_ADD){
            try {
                if (servers.get(server.getId()) != null) {
                    result.add (new ResultMessage.MessageItem ("id", "Server ID \"" + server.getId() + "\" already exists."));
                }
            } catch (Exception e) {
                log.error ("Unable to URL encode server " + server.getId() + " while sanity checking add operation.");
            }
        } else if (operation == DAOConstants.OPERATION_EDIT || operation == DAOConstants.OPERATION_COPY || operation == DAOConstants.OPERATION_DELETE){
            try {
                if (servers.get((operation == DAOConstants.OPERATION_EDIT) ? server.getOrigid() : server.getId()) == null) {
                    result.add (new ResultMessage.MessageItem ("id", "Server ID \"" + server.getId() + "\" does not exist."));
                    return result; // no need to go any further if the server doesn't exist.
                }
            } catch (Exception e) {
                log.error ("Unable to URL encode server " + server.getId() + " while sanity checking add operation.");
            }
        }
        
        // only need to verify the id with copy and delete operations
        //
        if (operation == DAOConstants.OPERATION_COPY || operation == DAOConstants.OPERATION_DELETE)
            return result;
        
        if (server.getHostname() == null || server.getHostname().length() == 0) {
            result.add (new ResultMessage.MessageItem ("hostname", "Host Name may not be empty."));
        }
        
        if (server.getPort() == null || server.getPort().length() == 0) {
            result.add (new ResultMessage.MessageItem ("port", "Port Number may not be empty."));
        } else {
            try {
                Integer.parseInt(server.getPort());
            } catch (Exception e) {
                result.add (new ResultMessage.MessageItem ("port", "Port Number is not an integer."));
            }
        }
        
        if (server.getUser() == null || server.getUser().length() == 0) {
            result.add (new ResultMessage.MessageItem ("user", "User Name may not be empty."));
        }
        
        if (server.getEncryptedpassword() == null || server.getEncryptedpassword().length() == 0) {
            result.add (new ResultMessage.MessageItem ("encryptedpassword", "Password may not be empty."));
        }
        
        if (server.getDomain() == null || server.getDomain().length() == 0) {
            result.add (new ResultMessage.MessageItem ("domain", "Domain may not be empty."));
        }
        
        return result;
    }
    
    // create a server DOM element to insert into the servers document.
    // includes whitespace so the serialized file doesn't end up too ugly.
    //
    private Element createServerElement (Server server) {
        Element s = new Element ("server");
        s.addContent ("\n    ");
        s.addContent (new Element("id").setText (server.getId()));
        s.addContent ("\n    ");
        s.addContent (new Element("hostname").setText (server.getHostname()));
        s.addContent ("\n    ");
        s.addContent (new Element("port").setText (server.getPort()));
        s.addContent ("\n    ");
        s.addContent (new Element("usage").setText (server.getUsage()));
        s.addContent ("\n    ");
        s.addContent (new Element("user").setText (server.getUser()));
        s.addContent ("\n    ");
        s.addContent (new Element("encryptedpassword").setText (server.getEncryptedpassword()));
        s.addContent ("\n    ");
        s.addContent (new Element("domain").setText (server.getDomain()));
        s.addContent ("\n    ");
        s.addContent (new Element("cishome").setText (server.getCishome()));
        s.addContent ("\n    ");
        s.addContent (new Element("clustername").setText (server.getClustername()));
        s.addContent ("\n    ");
        s.addContent (new Element("site").setText (server.getSite()));
        s.addContent ("\n    ");
        s.addContent (new Element("useHttps").setText ("" + server.isUseHttps()));
        s.addContent ("\n  ");
        
        return s;
    }
    
    // extracts the specified server DOM element from the servers document
    //
    private Element getServerElement (String id) {
        Element rootNode = serversDoc.getRootElement();
        List<Element> list = rootNode.getChildren ("server");
        Element tmp, result = null;
        
        for (int i = 0; i < list.size(); i++) {
            tmp = list.get (i);
            if (tmp.getChildText ("id").equals (id)) {
                result = tmp;
                return result;
            }
        }
        
        return result;
    }
    
    // writes the server list to disk
    //
    private void serialize() throws Exception {
        String pdtHome = FilesDAO.getPdtHome();
        File f = new File (pdtHome + "/resources/modules/servers.xml");

        // if user preferences indicate to save backups of edited files, back up the original file.
        //
        if (PreferencesManager.getInstance().getBackupFiles().equals ("true")) {
            File fb = new File (pdtHome + "/resources/modules/servers.xml.bak");
            
            log.debug ("Copying \"" + f.getAbsolutePath() + "\" to \"" + fb.getAbsolutePath() + "\".");
            
            // windows doesn't let you rename a file to an existing file's name
            //
            if (fb.exists() && fb.canWrite())
                FileUtils.deleteQuietly (fb);

            FileUtils.copyFile (f, fb);
        }

        xmlo.output (serversDoc, new FileOutputStream (f));
    }
}

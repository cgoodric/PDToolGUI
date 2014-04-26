package com.cisco.dvbu.ps.deploytool.gui.core.config.server;

import com.cisco.dvbu.ps.deploytool.gui.util.DAOConstants;

import com.cisco.dvbu.ps.deploytool.gui.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Bean object for servers. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class Server {
    private static final Logger log = LoggerFactory.getLogger (Server.class);

    private String operation;
    private String origid;
    private String id;
    private String hostname;
    private String port;
    private String usage;
    private String user;
    private String encryptedpassword;
    private String domain;
    private String cishome;
    private String clustername;
    private String site;
    private boolean useHttps = false;

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public Server () {}

    /**
     * <p>
     * Sets the <code>operation</code> field.
     * </p>
     * 
     * @param  operation  The operation that will be performed with this bean: "add", "edit", "copy", or "delete".
     */
    public void setOperation (String operation) {
        this.operation = operation;
    }

    /**
     * <p>
     * Returns the value of the <code>operation</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getOperation () {
        return operation;
    }

    /**
     * <p>
     * Sets the <code>origid</code> field. This is used in edit situations to preserve the original id of the server
     * in case the user changes the server id.
     * </p>
     * 
     * @param  origid  The original server id.
     */
    public void setOrigid (String origid) {
        this.origid = origid;
    }

    /**
     * <p>
     * Returns the value of the <code>origid</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getOrigid () {
        return origid;
    }

    /**
     * <p>
     * Sets the <code>id</code> field.
     * </p>
     * 
     * @param  id  The server's ID.
     */
    public void setId (String id) {
        this.id = id;
    }

    /**
     * <p>
     * Returns the value of the <code>id</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getId () {
        return id;
    }

    /**
     * <p>
     * Sets the <code>hostname</code> field.
     * </p>
     * 
     * @param  hostname  The host name of the server.
     */
    public void setHostname (String hostname) {
        this.hostname = hostname;
    }

    /**
     * <p>
     * Returns the value of the <code>hostname</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getHostname () {
        return hostname;
    }

    /**
     * <p>
     * Sets the <code>port</code> field.
     * </p>
     * 
     * @param  port  The port number of the server.
     */
    public void setPort (String port) {
        this.port = port;
    }

    /**
     * <p>
     * Returns the value of the <code>port</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getPort () {
        return port;
    }

    /**
     * <p>
     * Sets the <code>usage</code> field.
     * </p>
     * 
     * @param  usage  The intended usage of the server.
     */
    public void setUsage (String usage) {
        this.usage = usage;
    }

    /**
     * <p>
     * Returns the value of the <code>usage</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getUsage () {
        return usage;
    }

    /**
     * <p>
     * Sets the <code>user</code> field.
     * </p>
     * 
     * @param  user  The user id that will be used to access the server.
     */
    public void setUser (String user) {
        this.user = user;
    }

    /**
     * <p>
     * Returns the value of the <code>user</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getUser () {
        return user;
    }

    /**
     * <p>
     * Sets the <code>encryptedpassword</code> field.
     * </p>
     * 
     * @param  encryptedpassword  The password of the user accessing the server. If the value is not encrypted,
     *                            it is encrypted using CIS's password encryption methodology.
     */
    public void setEncryptedpassword (String encryptedpassword) {

        // automatically encrypt the password whenever this field is set
        //
        this.encryptedpassword = StringUtils.encryptPassword (encryptedpassword);
    }

    /**
     * <p>
     * Returns the value of the <code>encryptedpassword</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getEncryptedpassword () {
        return encryptedpassword;
    }

    /**
     * <p>
     * Sets the <code>domain</code> field.
     * </p>
     * 
     * @param  domain  The domain of the user accessing the server.
     */
    public void setDomain (String domain) {
        this.domain = domain;
    }

    /**
     * <p>
     * Returns the value of the <code>domain</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getDomain () {
        return domain;
    }

    /**
     * <p>
     * Sets the <code>cishome</code> field.
     * </p>
     * 
     * @param  cishome  The absolute path to the installation on the server.
     */
    public void setCishome (String cishome) {
        this.cishome = cishome;
    }

    /**
     * <p>
     * Returns the value of the <code>cishome</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getCishome () {
        return cishome;
    }

    /**
     * <p>
     * Sets the <code>clustername</code> field.
     * </p>
     * 
     * @param  clustername  The cluster name of the server.
     */
    public void setClustername (String clustername) {
        this.clustername = clustername;
    }

    /**
     * <p>
     * Returns the value of the <code>clustername</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getClustername () {
        return clustername;
    }

    /**
     * <p>
     * Sets the <code>site</code> field.
     * </p>
     * 
     * @param  site  The location of the server.
     */
    public void setSite (String site) {
        this.site = site;
    }

    /**
     * <p>
     * Returns the value of the <code>site</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getSite () {
        return site;
    }

    /**
     * <p>
     * Sets the <code>useHttps</code> field.
     * </p>
     * 
     * @param  useHttps  Indicates whether or not to use a secure HTTP connection when communicating with the server.
     */
    public void setUseHttps (boolean useHttps) {
        this.useHttps = useHttps;
    }

    /**
     * <p>
     * Returns the value of the <code>useHttps</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isUseHttps () {
        return useHttps;
    }
}

package com.cisco.dvbu.ps.deploytool.gui.core.module.user;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.cisco.dvbu.ps.deploytool.gui.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

/**
 * <p>
 * Bean object for user module user records. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 *
 * @author Calvin Goodrich
 * @version 1.0
 */
public class User {

    // attributes used by the UI
    //
    String operation;
    String origId;
    
    // module specific attributes
    //
    String id;
    String userName;
    String encryptedPassword;
    boolean forcePassword;
    String domainName = "composite";
    List<GroupMembership> groupMembershipList = new ArrayList<GroupMembership>();
    String privilege;
    String annotation;

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public User () {}

    /**
     * <p>
     * Copy constructor. Used for making/serializing copies, where only the ID is updated.
     * 
     * WARNING!!! - This makes "shallow" copies of the List objects (meaning that changing List elements
     * in the copy will update List elements in the original.)
     * </p>
     */
    public User (User u) {
        if (u != null) {
            this.operation = u.getOperation();
            this.origId = u.getOrigId();
            this.id = u.getId();
            this.userName = u.getUserName();
            this.encryptedPassword = u.getEncryptedPassword();
            this.forcePassword = u.isForcePassword();
            this.domainName = u.getDomainName();
            this.groupMembershipList = (u.getGroupMembershipList() != null) ? new ArrayList<GroupMembership> (u.getGroupMembershipList()) : null; // do not update!!
            this.privilege = u.getPrivilege();
            this.annotation = u.getAnnotation();
        }
    }

    /**
     * <p>
     * Constructor. Extracts attribute values from {@link Element} object.
     * </p>
     */
    public User (Element uNode) {
        for (Element c : uNode.getChildren()) {
            if (c.getName().equals ("id"))
                this.id = c.getText();

            if (c.getName().equals ("userName"))
                this.userName = c.getText();

            if (c.getName().equals ("encryptedPassword"))
                this.encryptedPassword = c.getText();

            if (c.getName().equals ("forcePassword"))
                this.forcePassword = (c.getText() == null) ? true : c.getText().matches ("(?i)^(yes|true|on|1)$");

            if (c.getName().equals ("domainName"))
                this.domainName = c.getText();

            if (c.getName().equals ("groupMembershipList")) {
                if (this.groupMembershipList == null)
                    this.groupMembershipList = new ArrayList<GroupMembership>();
                
                this.groupMembershipList.add (new GroupMembership (c));
            }

            if (c.getName().equals ("privilege"))
                this.privilege = c.getText();

            if (c.getName().equals ("annotation"))
                this.annotation = c.getText();
        }
    }

    /**
     * <p>
     * Returns the object as a JDom Element.
     * </p>
     * 
     * @param  name   The name of the element to use.
     * @param  indent The number of tabs (spaces) to indent the child elements.
     * @return        The value.
     */
    public Element toElement(
        String name,
        int indent
    ) {
        String indentStr = StringUtils.getIndent (indent);

        Element result = new Element (name);

        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("id").setText (this.id));

        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("userName").setText (this.userName));

        if (this.encryptedPassword != null && this.encryptedPassword.length() > 0) {
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("encryptedPassword").setText (this.encryptedPassword));
        }

        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("forcePassword").setText ("" + this.forcePassword));

        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("domainName").setText (this.domainName));

        if (this.groupMembershipList != null) {
            for (GroupMembership gm : groupMembershipList) {
                result.addContent ("\n" + indentStr);
                result.addContent (gm.toElement ("groupMembershipList", indent + 1).addContent ("\n" + indentStr));
            }
        }

        if (this.privilege != null && this.privilege.length() > 0) {
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("privilege").setText (this.privilege));
        }

        if (this.annotation != null && this.annotation.length() > 0) {
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("annotation").setText (this.annotation));
        }

        return result;
    }

    /**
     * <p>
     * Sets the <code>operation</code> field.
     * </p>
     * 
     * @param  operation  The operation to be performed on the archive.
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
     * Sets the <code>origId</code> field.
     * </p>
     * 
     * @param  origId  For edit/copy operations, this contains the original ID (for 
     *                 cases where the user wants to change the ID field.)
     */
    public void setOrigId (String origId) {
        this.origId = origId;
    }

    /**
     * <p>
     * Returns the value of the <code>origId</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getOrigId () {
        return origId;
    }

    /**
     * <p>
     * Sets the <code>id</code> field.
     * </p>
     * 
     * @param  id  The ID of the user record.
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
     * Sets the <code>userName</code> field.
     * </p>
     * 
     * @param  userName  The name of the user record.
     */
    public void setUserName (String userName) {
        this.userName = userName;
    }

    /**
     * <p>
     * Returns the value of the <code>userName</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getUserName () {
        return userName;
    }

    /**
     * <p>
     * Sets the <code>encryptedPassword</code> field.
     * </p>
     * 
     * @param  encryptedPassword  The password of the user record. If the value is not encrypted,
     *                            it is encrypted using CIS's password encryption methodology.
     */
    public void setEncryptedPassword (String encryptedPassword) {
        this.encryptedPassword = StringUtils.encryptPassword (encryptedPassword);
    }

    /**
     * <p>
     * Returns the value of the <code>encryptedPassword</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getEncryptedPassword () {
        return encryptedPassword;
    }

    /**
     * <p>
     * Sets the <code>forcePassword</code> field.
     * </p>
     * 
     * @param  forcePassword  A flag indicating that if a user already exists, that PDTool 
     *                        should reset the password using the value in encryptedPassword.
     */
    public void setForcePassword (boolean forcePassword) {
        this.forcePassword = forcePassword;
    }

    /**
     * <p>
     * Returns the value of the <code>forcePassword</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isForcePassword () {
        return forcePassword;
    }

    /**
     * <p>
     * Sets the <code>domainName</code> field.
     * </p>
     * 
     * @param  domainName  The ID of the user record.
     */
    public void setDomainName (String domainName) {
        this.domainName = (domainName == null || domainName.length() == 0) ? "composite" : domainName;
    }

    /**
     * <p>
     * Returns the value of the <code>domainName</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getDomainName () {
        return domainName;
    }

    /**
     * <p>
     * Sets the <code>groupMembershipList</code> field.
     * </p>
     * 
     * @param  groupMembershipList  The group membership list of the user record.
     */
    public void setGroupMembershipList (List<User.GroupMembership> groupMembershipList) {
        this.groupMembershipList = groupMembershipList;
    }

    /**
     * <p>
     * Returns the value of the <code>groupMembershipList</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<User.GroupMembership> getGroupMembershipList () {
        return groupMembershipList;
    }

    /**
     * <p>
     * Sets the <code>privilege</code> field.
     * </p>
     * 
     * @param  privilege  The global rights of the user record.
     */
    public void setPrivilege (String privilege) {
        this.privilege = privilege;
    }

    /**
     * <p>
     * Returns the value of the <code>privilege</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getPrivilege () {
        return privilege;
    }

    /**
     * <p>
     * Sets the <code>annotation</code> field.
     * </p>
     * 
     * @param  annotation  The annotation of the user record.
     */
    public void setAnnotation (String annotation) {
        this.annotation = annotation;
    }

    /**
     * <p>
     * Returns the value of the <code>annotation</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getAnnotation () {
        return annotation;
    }

    /**
     * <p>
     * Bean object for user module group membership lists in records. Intended to be serialized/deserialized by Jackson into/from JSON.
     * </p>
     *
     * @author Calvin Goodrich
     * @version 1.0
     */
    public static class GroupMembership {
        String groupName;
        String groupDomain = "composite";
        
        /**
         * <p>
         * Constructor.
         * </p>
         */
        public GroupMembership() {}

        /**
         * <p>
         * Constructor. Extracts attribute values from {@link Element} object.
         * </p>
         */
        public GroupMembership (Element gmNode) {
            for (Element gmChild : gmNode.getChildren()) {
                if (gmChild.getName().equals ("groupName"))
                    this.groupName = gmChild.getText();
    
                if (gmChild.getName().equals ("groupDomain"))
                    this.groupDomain = gmChild.getText();
    
            }
        }
    
        /**
         * <p>
         * Returns the object as a JDom Element.
         * </p>
         * 
         * @param  name   The name of the element to use.
         * @param  indent The number of tabs (spaces) to indent the child elements.
         * @return        The value.
         */
        public Element toElement (
            String name,
            int indent
        ) {
            Element result = new Element (name);
            String indentStr = StringUtils.getIndent (indent);
            
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("groupName").setText (this.groupName));
                
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("groupDomain").setText (this.groupDomain));
            
            return result;
        }

        /**
         * <p>
         * Sets the <code>groupName</code> field.
         * </p>
         * 
         * @param  groupName  The name of the group the user is a member of.
         */
        public void setGroupName (String groupName) {
            this.groupName = groupName;
        }

        /**
         * <p>
         * Returns the value of the <code>groupName</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getGroupName () {
            return groupName;
        }

        /**
         * <p>
         * Sets the <code>groupDomain</code> field.
         * </p>
         * 
         * @param  groupDomain  The domain of the group the user is a member of.
         */
        public void setGroupDomain (String groupDomain) {
            this.groupDomain = (groupDomain == null || groupDomain.length() == 0) ? "composite" : groupDomain;
        }

        /**
         * <p>
         * Returns the value of the <code>groupDomain</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getGroupDomain () {
            return groupDomain;
        }
    }

}

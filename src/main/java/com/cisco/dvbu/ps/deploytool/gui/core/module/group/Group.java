package com.cisco.dvbu.ps.deploytool.gui.core.module.group;

import com.cisco.dvbu.ps.deploytool.gui.util.StringUtils;

import org.jdom2.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Bean object for group module group records. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class Group {

    private static final Logger log = LoggerFactory.getLogger (Group.class);

    // attributes used by the UI
    //
    String operation;
    String origId;
    
    // module specific attributes
    //
    String id;
    String groupName;
    String groupDomain = "composite";
    String privilege;

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public Group () {}

    /**
     * <p>
     * Copy constructor. Used for making/serializing copies, where only the ID is updated.
     * </p>
     */
    public Group (Group g) {
        if (g != null) {
            this.operation = g.getOperation();
            this.origId = g.getOrigId();
            this.id = g.getId();
            this.groupName = g.getGroupName();
            this.groupDomain = g.getGroupDomain();
            this.privilege = g.getPrivilege();
        }
    }

    /**
     * <p>
     * Constructor. Extracts attribute values from {@link Element} object.
     * </p>
     */
    public Group (Element gNode) {
        int i = 0;
        for (Element gChild : gNode.getChildren()) {
            log.debug ("Located child #" + i++ + ": " + gChild.getName() + "{" + gChild.getNamespace().getURI() + "}");
            
            if (gChild.getName().equals ("id")) {
                log.debug ("Located group ID \"" + gChild.getText() + "\".");
                this.id = gChild.getText();
            }

            if (gChild.getName().equals ("groupName"))
                this.groupName = gChild.getText();

            if (gChild.getName().equals ("groupDomain"))
                this.groupDomain = gChild.getText();

            if (gChild.getName().equals ("privilege"))
                this.privilege = gChild.getText().toUpperCase();

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
        result.addContent (new Element ("id").setText (this.id));
            
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("groupName").setText (this.groupName));
        
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("groupDomain").setText (this.groupDomain));
        
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("privilege").setText (this.privilege));
        
        return result;
    }

    /**
     * <p>
     * Sets the <code>operation</code> field.
     * </p>
     * 
     * @param  operation  The operation to be performed on the group.
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
     * @param  id  The ID of the group record.
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
     * Sets the <code>groupName</code> field.
     * </p>
     * 
     * @param  groupName  The group's name.
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
     * @param  groupDomain  The group's domain.
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

    /**
     * <p>
     * Sets the <code>privilege</code> field.
     * </p>
     * 
     * @param  privilege  The group's privileges.
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
}

package com.cisco.dvbu.ps.deploytool.gui.core.module.privilege;

import com.cisco.dvbu.ps.deploytool.gui.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;


/**
 * <p>
 * Bean object for privilege module privilege records. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 *
 * @author Calvin Goodrich
 * @version 1.0
 */
public class Privilege {

    // attributes used by the UI
    //
    String operation;
    String origId;
    
    // module specific attributes
    //
    String id;
    String resourcePath;
    String resourceType;
    boolean recurse = false;
    boolean updateDependenciesRecursively = false;
    String mode;
    List<PrivilegeItem> privileges = new ArrayList<PrivilegeItem>();

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public Privilege () {
        super ();
    }

    /**
     * <p>
     * Copy constructor. Used for making/serializing copies, where only the ID is updated.
     * 
     * WARNING!!! - This makes "shallow" copies of the List objects (meaning that changing List elements
     * in the copy will update List elements in the original.)
     * </p>
     */
    public Privilege (Privilege p) {
        if (p != null) {
            this.operation = p.getOperation();
            this.origId = p.getOrigId();
            this.id = p.getId();
            this.resourcePath = p.getResourcePath();
            this.resourceType = p.getResourceType();
            this.recurse = p.isRecurse();
            this.updateDependenciesRecursively = p.isUpdateDependenciesRecursively();
            this.mode = p.getMode();
            this.privileges = (p.getPrivileges() != null) ? new ArrayList<PrivilegeItem> (p.getPrivileges()) : null; // do not update!!
        }
    }

    /**
     * <p>
     * Constructor. Extracts attribute values from {@link Element} object.
     * </p>
     */
    public Privilege (Element pNode) {
        for (Element c : pNode.getChildren()) {
            if (c.getName().equals ("id"))
                this.id = c.getText();

            if (c.getName().equals ("resourcePath"))
                this.resourcePath = c.getText();

            if (c.getName().equals ("resourceType"))
                this.resourceType = c.getText();

            if (c.getName().equals ("recurse"))
                this.recurse = (c.getText() == null) ? true : c.getText().matches ("(?i)^(yes|true|on|1)$");

            if (c.getName().equals ("updateDependenciesRecursively"))
                this.updateDependenciesRecursively = (c.getText() == null) ? true : c.getText().matches ("(?i)^(yes|true|on|1)$");

            if (c.getName().equals ("mode"))
                this.mode = c.getText();

            if (c.getName().equals ("privilege")) {
                if (this.privileges == null)
                    this.privileges = new ArrayList<PrivilegeItem>();
                
                this.privileges.add (new PrivilegeItem (c));
            }

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
        result.addContent (new Element ("resourcePath").setText (this.resourcePath));

        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("resourceType").setText (this.resourceType));

        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("recurse").setText ("" + this.recurse));

        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("updateDependenciesRecursively").setText ("" + this.updateDependenciesRecursively));

        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("mode").setText (this.mode));

        if (this.privileges != null) {
            for (PrivilegeItem pi : privileges) {
                result.addContent ("\n" + indentStr);
                result.addContent (pi.toElement ("privilege", indent + 1).addContent ("\n" + indentStr));
            }
        }

        return result;
    }

    /**
     * <p>
     * Sets the <code>operation</code> field.
     * </p>
     * 
     * @param  operation  The operation to be performed on the privilege.
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
     * @param  id  The ID of the privilege record.
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
     * Sets the <code>resourcePath</code> field.
     * </p>
     * 
     * @param  resourcePath  The resource path to apply the privilege to.
     */
    public void setResourcePath (String resourcePath) {
        this.resourcePath = resourcePath;
    }

    /**
     * <p>
     * Returns the value of the <code>resourcePath</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getResourcePath () {
        return resourcePath;
    }

    /**
     * <p>
     * Sets the <code>resourceType</code> field.
     * </p>
     * 
     * @param  resourceType  The type of the resource to apply the privilege to.
     */
    public void setResourceType (String resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * <p>
     * Returns the value of the <code>resourceType</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getResourceType () {
        return resourceType;
    }

    /**
     * <p>
     * Sets the <code>recurse</code> field.
     * </p>
     * 
     * @param  recurse  Indicates whether to recurse and apply privileges to all child resources.
     */
    public void setRecurse (boolean recurse) {
        this.recurse = recurse;
    }

    /**
     * <p>
     * Returns the value of the <code>recurse</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isRecurse () {
        return recurse;
    }

    /**
     * <p>
     * Sets the <code>updateDependenciesRecursively</code> field.
     * </p>
     * 
     * @param  updateDependenciesRecursively  Indicates whether to recurse and apply privileges to all child resources.
     */
    public void setUpdateDependenciesRecursively (boolean updateDependenciesRecursively) {
        this.updateDependenciesRecursively = updateDependenciesRecursively;
    }

    /**
     * <p>
     * Returns the value of the <code>updateDependenciesRecursively</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isUpdateDependenciesRecursively () {
        return updateDependenciesRecursively;
    }

    /**
     * <p>
     * Sets the <code>mode</code> field.
     * </p>
     * 
     * @param  mode  Indicates whether the privilege setting should replace existing settings
     *               or simply modify them. Valid values are "SET_EXACTLY" and "OVERWRITE_APPEND".
     */
    public void setMode (String mode) {
        this.mode = mode;
    }

    /**
     * <p>
     * Returns the value of the <code>mode</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getMode () {
        return mode;
    }

    /**
     * <p>
     * Sets the <code>privileges</code> field.
     * </p>
     * 
     * @param  privileges  The list of privilege settings for the resource by user/group.
     */
    public void setPrivileges (List<Privilege.PrivilegeItem> privileges) {
        this.privileges = privileges;
    }

    /**
     * <p>
     * Returns the value of the <code>privileges</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<Privilege.PrivilegeItem> getPrivileges () {
        return privileges;
    }

    /**
     * <p>
     * Bean object for privilege module record privilege item. Intended to be serialized/deserialized by Jackson into/from JSON.
     * </p>
     * 
     * @author Calvin Goodrich
     * @version 1.0
     */
    public static class PrivilegeItem {
        String name;
        String nameType;
        String domain = "composite";
        String privileges;
        String combinedPrivileges;
        String inheritedPrivileges;

        /**
         * <p>
         * Constructor.
         * </p>
         */
        public PrivilegeItem() {}
        
        /**
         * <p>
         * Constructor. Extracts attribute values from {@link Element} object.
         * </p>
         */
        public PrivilegeItem (Element piNode) {
            for (Element piChild : piNode.getChildren()) {
                if (piChild.getName().equals ("name"))
                    this.name = piChild.getText();
    
                if (piChild.getName().equals ("nameType"))
                    this.nameType = piChild.getText();
    
                if (piChild.getName().equals ("domain"))
                    this.domain = piChild.getText();
    
                if (piChild.getName().equals ("privileges"))
                    this.privileges = piChild.getText().toUpperCase();
    
                if (piChild.getName().equals ("combinedPrivileges"))
                    this.combinedPrivileges = piChild.getText().toUpperCase();
    
                if (piChild.getName().equals ("inheritedPrivileges"))
                    this.inheritedPrivileges = piChild.getText().toUpperCase();
    
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
            result.addContent (new Element ("name").setText (this.name));
                
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("nameType").setText (this.nameType));
            
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("domain").setText (this.domain));
            
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("privileges").setText (this.privileges));
            
            if (this.combinedPrivileges != null && this.combinedPrivileges.length() > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("combinedPrivileges").setText (this.combinedPrivileges));
            }
            
            if (this.inheritedPrivileges != null && this.inheritedPrivileges.length() > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("inheritedPrivileges").setText (this.inheritedPrivileges));
            }
            
            return result;
        }

        /**
         * <p>
         * Sets the <code>name</code> field.
         * </p>
         * 
         * @param  name  The name of the entity the privileges apply to (user or group.)
         */
        public void setName (String name) {
            this.name = name;
        }

        /**
         * <p>
         * Returns the value of the <code>name</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getName () {
            return name;
        }

        /**
         * <p>
         * Sets the <code>nameType</code> field.
         * </p>
         * 
         * @param  nameType  The entity type. Valid values are "USER" or "GROUP".
         */
        public void setNameType (String nameType) {
            this.nameType = nameType;
        }

        /**
         * <p>
         * Returns the value of the <code>nameType</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getNameType () {
            return nameType;
        }

        /**
         * <p>
         * Sets the <code>domain</code> field.
         * </p>
         * 
         * @param  domain  The domain of the entity.
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
         * Sets the <code>privileges</code> field.
         * </p>
         * 
         * @param  privileges  The space separated list of enabled privileges. Valid values are "NONE",
         *                     "READ", "WRITE", "SELECT", "EXECUTE", "INSERT", "UPDATE", "DELETE", and "GRANT".
         */
        public void setPrivileges (String privileges) {
            this.privileges = privileges;
        }

        /**
         * <p>
         * Returns the value of the <code>privileges</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getPrivileges () {
            return privileges;
        }

        /**
         * <p>
         * Sets the <code>combinedPrivileges</code> field.
         * </p>
         * 
         * @param  combinedPrivileges  The explicit privileges for the entity combined with the inherited 
         *                             privileges. This is generally set by the server and will not have
         *                             any effect when updated through PDTool.
         */
        public void setCombinedPrivileges (String combinedPrivileges) {
            this.combinedPrivileges = combinedPrivileges;
        }

        /**
         * <p>
         * Returns the value of the <code>combinedPrivileges</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getCombinedPrivileges () {
            return combinedPrivileges;
        }

        /**
         * <p>
         * Sets the <code>inheritedPrivileges</code> field.
         * </p>
         * 
         * @param  inheritedPrivileges  The privileges inherited through group membership or global right.
         *                              Again, this is generally set by the server and will not have any
         *                              effect when updated through PDTool.
         */
        public void setInheritedPrivileges (String inheritedPrivileges) {
            this.inheritedPrivileges = inheritedPrivileges;
        }

        /**
         * <p>
         * Returns the value of the <code>inheritedPrivileges</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getInheritedPrivileges () {
            return inheritedPrivileges;
        }
    }
}

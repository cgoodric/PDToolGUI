package com.cisco.dvbu.ps.deploytool.gui.core.module.rebind;

import com.cisco.dvbu.ps.deploytool.gui.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Bean object for rebind module rebind records. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 *
 * @author Calvin Goodrich
 * @version 1.0
 */
public class Rebind {

    // public constants
    //
    public static final int TYPE_RESOURCE = 0;
    public static final int TYPE_FOLDER = 1;

    private static final Logger log = LoggerFactory.getLogger (Rebind.class);

    // attributes used by the UI
    //
    String operation;
    String origId;
    
    // module specific attributes
    //
    String id;
    int type;
    
    // resource specific attributes
    //
    String resourcePath;
    String resourceType;
    List<RebindRule> rebindRules;
    
    // folder specific attributes
    //
    String startingFolderPath;
    String rebindFromFolder;
    String rebindToFolder;

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public Rebind () {
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
    public Rebind (Rebind r) {
        if (r != null) {
            this.operation = r.getOperation();
            this.origId = r.getOrigId();
            this.id = r.getId();
            this.type = r.getType();
            this.resourcePath = r.getResourcePath();
            this.resourceType = r.getResourceType();
            this.rebindRules = (r.getRebindRules() != null) ? new ArrayList<RebindRule> (r.getRebindRules()) : null; // do not update!!
        }
    }
    
    /**
     * <p>
     * Constructor. Extracts attribute values from {@link Element} object.
     * </p>
     */
    public Rebind (Element rNode) {
        for (Element rt : rNode.getChildren()) {
            if (rt.getName().equals ("rebindResource")) {
                log.debug ("    Located resource rebind.");
                this.type = TYPE_RESOURCE;
            } else {
                log.debug ("    Located folder rebind.");
                this.type = TYPE_FOLDER;
            }

            for (Element c : rt.getChildren()) {
                if (c.getName().equals ("id"))
                    this.id = c.getText();
                
                if (this.type == TYPE_RESOURCE) {
                    
                    if (c.getName().equals ("resourcePath"))
                        this.resourcePath = c.getText();
                    
                    if (c.getName().equals ("resourceType"))
                        this.resourceType = c.getText();
                    
                    if (c.getName().equals ("rebindRules")) {
                        if (rebindRules == null)
                            rebindRules = new ArrayList<RebindRule>();
                        
                        rebindRules.add (new RebindRule (c));
                    }
                    
                } else {

                    if (c.getName().equals ("startingFolderPath"))
                        this.startingFolderPath = c.getText();
                    
                    if (c.getName().equals ("rebindFromFolder"))
                        this.rebindFromFolder = c.getText();
                    
                    if (c.getName().equals ("rebindToFolder"))
                        this.rebindToFolder = c.getText();
                    
                }
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
        String indentStr2 = StringUtils.getIndent (indent + 1);

        Element result = new Element (name);
        
        if (this.type == TYPE_RESOURCE) {
            Element r = new Element ("rebindResource");

            r.addContent ("\n" + indentStr2);
            r.addContent (new Element ("id").setText (this.id));
    
            r.addContent ("\n" + indentStr2);
            r.addContent (new Element ("resourcePath").setText (this.resourcePath));
    
            if (this.resourceType != null && this.resourceType.length() > 0) {
                r.addContent ("\n" + indentStr2);
                r.addContent (new Element ("resourceType").setText (this.resourceType));
            }
            
            if (this.rebindRules != null && this.rebindRules.size() > 0) {
                for (RebindRule rr : rebindRules) {
                    r.addContent ("\n" + indentStr2);
                    r.addContent (rr.toElement ("rebindRules", indent + 2).addContent ("\n" + indentStr2));
                }
            }
            
            r.addContent ("\n" + indentStr);
    
            result.addContent ("\n" + indentStr);
            result.addContent (r);

        } else {
            Element f = new Element ("rebindFolder");

            f.addContent ("\n" + indentStr2);
            f.addContent (new Element ("id").setText (this.id));
    
            f.addContent ("\n" + indentStr2);
            f.addContent (new Element ("startingFolderPath").setText (this.startingFolderPath));
    
            f.addContent ("\n" + indentStr2);
            f.addContent (new Element ("rebindFromFolder").setText (this.rebindFromFolder));
    
            f.addContent ("\n" + indentStr2);
            f.addContent (new Element ("rebindToFolder").setText (this.rebindToFolder));
            
            f.addContent ("\n" + indentStr);
    
            result.addContent ("\n" + indentStr);
            result.addContent (f);

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
     * @param  id  The ID of the rebind record.
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
     * Sets the <code>type</code> field.
     * </p>
     * 
     * @param  type  Indicates whether the rebind entry is a resource or a folder.
     */
    public void setType (int type) {
        this.type = type;
    }

    /**
     * <p>
     * Returns the value of the <code>type</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getType () {
        return type;
    }

    /**
     * <p>
     * Sets the <code>resourcePath</code> field.
     * </p>
     * 
     * @param  resourcePath  The path of the resource to rebind.
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
     * @param  resourceType  The type of the resource to rebind.
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
     * Sets the <code>rebindRules</code> field.
     * </p>
     * 
     * @param  rebindRules  The list of rebind rules to apply to the resource.
     */
    public void setRebindRules (List<Rebind.RebindRule> rebindRules) {
        this.rebindRules = rebindRules;
    }

    /**
     * <p>
     * Returns the value of the <code>rebindRules</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<Rebind.RebindRule> getRebindRules () {
        return rebindRules;
    }

    /**
     * <p>
     * Sets the <code>startingFolderPath</code> field.
     * </p>
     * 
     * @param  startingFolderPath  The starting folder of the resources to search.
     */
    public void setStartingFolderPath (String startingFolderPath) {
        this.startingFolderPath = startingFolderPath;
    }

    /**
     * <p>
     * Returns the value of the <code>startingFolderPath</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getStartingFolderPath () {
        return startingFolderPath;
    }

    /**
     * <p>
     * Sets the <code>rebindFromFolder</code> field.
     * </p>
     * 
     * @param  rebindFromFolder  The folder to replace in the resource contained in "startingFolder".
     */
    public void setRebindFromFolder (String rebindFromFolder) {
        this.rebindFromFolder = rebindFromFolder;
    }

    /**
     * <p>
     * Returns the value of the <code>rebindFromFolder</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getRebindFromFolder () {
        return rebindFromFolder;
    }

    /**
     * <p>
     * Sets the <code>rebindToFolder</code> field.
     * </p>
     * 
     * @param  rebindToFolder  The replacement folder to use when rebinding resoruces contained in "startingFolder".
     */
    public void setRebindToFolder (String rebindToFolder) {
        this.rebindToFolder = rebindToFolder;
    }

    /**
     * <p>
     * Returns the value of the <code>rebindToFolder</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getRebindToFolder () {
        return rebindToFolder;
    }

    /**
     * <p>
     * Bean object for rebind module rebind rule definition. Intended to be serialized/deserialized by Jackson into/from JSON.
     * </p>
     *
     * @author Calvin Goodrich
     * @version 1.0
     */
    public static class RebindRule {
        String oldPath;
        String oldType;
        String newPath;
        String newType;
        
        /**
         * <p>
         * Constructor.
         * </p>
         */
        public RebindRule() {}

        /**
         * <p>
         * Constructor. Extracts attribute values from {@link Element} object.
         * </p>
         */
        public RebindRule (Element rNode) {
            for (Element c : rNode.getChildren()) {
                if (c.getName().equals ("oldPath"))
                    this.oldPath = c.getText();
        
                if (c.getName().equals ("oldType"))
                    this.oldType = c.getText();
    
                if (c.getName().equals ("newPath"))
                    this.newPath = c.getText();
        
                if (c.getName().equals ("newType"))
                    this.newType = c.getText();
    
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
            Element result = new Element (name);
            String indentStr = StringUtils.getIndent (indent);
    
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("oldPath").setText (this.oldPath));
            
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("oldType").setText (this.oldType));
            
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("newPath").setText (this.newPath));
            
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("newType").setText (this.newType));
            
            return result;
        }
    
        /**
         * <p>
         * Sets the <code>oldPath</code> field.
         * </p>
         * 
         * @param  oldPath  The resource path to look for in the rebound resource.
         */
        public void setOldPath (String oldPath) {
            this.oldPath = oldPath;
        }

        /**
         * <p>
         * Returns the value of the <code>oldPath</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getOldPath () {
            return oldPath;
        }

        /**
         * <p>
         * Sets the <code>oldType</code> field.
         * </p>
         * 
         * @param  oldType  The resource type of the resource to look for in the rebound resource.
         */
        public void setOldType (String oldType) {
            this.oldType = oldType;
        }

        /**
         * <p>
         * Returns the value of the <code>oldType</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getOldType () {
            return oldType;
        }

        /**
         * <p>
         * Sets the <code>newPath</code> field.
         * </p>
         * 
         * @param  newPath  The path of the replacement resource in the rebound resource.
         */
        public void setNewPath (String newPath) {
            this.newPath = newPath;
        }

        /**
         * <p>
         * Returns the value of the <code>newPath</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getNewPath () {
            return newPath;
        }

        /**
         * <p>
         * Sets the <code>newType</code> field.
         * </p>
         * 
         * @param  newType  The type of the replacement resource in the rebound resource.
         */
        public void setNewType (String newType) {
            this.newType = newType;
        }

        /**
         * <p>
         * Returns the value of the <code>newType</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getNewType () {
            return newType;
        }
    }
}

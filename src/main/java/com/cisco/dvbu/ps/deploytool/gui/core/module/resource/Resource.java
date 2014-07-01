package com.cisco.dvbu.ps.deploytool.gui.core.module.resource;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.cisco.dvbu.ps.deploytool.gui.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * <p>
 * Bean object for resource module resource records. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 *
 * @author Calvin Goodrich
 * @version 1.0
 */
public class Resource {
    
    // public constants
    //
    public static final int COPY_MODE_ALTER_NAME_IF_EXISTS = 0;
    public static final int COPY_MODE_FAIL_IF_EXISTS = 1;
    public static final int COPY_MODE_OVERWRITE_MERGE_IF_EXISTS = 2;
    public static final int COPY_MODE_OVERWRITE_REPLACE_IF_EXISTS = 3;
    public static final String[] COPY_MODE_LABELS = {
        "ALTER_NAME_IF_EXISTS",
        "FAIL_IF_EXISTS",
        "OVERWRITE_MERGE_IF_EXISTS",
        "OVERWRITE_REPLACE_IF_EXISTS"
    };

//    private static final Logger log = LoggerFactory.getLogger (Resource.class);

    // attributes used by the UI
    //
    String operation;
    String origId;
    
    // module specific attributes
    //
    String id;
    String resourcePath;
    String resourceType;
    boolean recursive = false;
    String targetContainerPath;
    String newName;
    int copyMode = COPY_MODE_FAIL_IF_EXISTS;
    String comment;
    String dataServiceName;
    List<String> arguments;

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public Resource () {
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
    public Resource (Resource r) {
        if (r != null) {
            this.operation = r.getOperation();
            this.origId = r.getOrigId();
            this.id = r.getId();
            this.resourcePath = r.getResourcePath();
            this.resourceType = r.getResourceType();
            this.recursive = r.isRecursive();
            this.targetContainerPath = r.getTargetContainerPath();
            this.newName = r.getNewName();
            this.copyMode = r.getCopyMode();
            this.comment = r.getComment();
            this.dataServiceName = r.getDataServiceName();
            this.arguments = (r.getArguments() != null) ? new ArrayList<String> (r.getArguments()) : null; // do not update!!
        }
    }

    /**
     * <p>
     * Constructor. Extracts attribute values from {@link Element} object.
     * </p>
     */
    public Resource (Element rNode) {
        for (Element rChild : rNode.getChildren()) {
            if (rChild.getName().equals ("id"))
                this.id = rChild.getText();

            if (rChild.getName().equals ("resourcePath"))
                this.resourcePath = rChild.getText();

            if (rChild.getName().equals ("resourceType"))
                this.resourceType = rChild.getText();

            if (rChild.getName().equals ("recursive"))
                this.recursive = (rChild.getText() == null) ? true : rChild.getText().matches ("(?i)^(yes|true|on|1)$");

            if (rChild.getName().equals ("targetContainerPath"))
                this.targetContainerPath = rChild.getText();

            if (rChild.getName().equals ("newName"))
                this.newName = rChild.getText();

            if (rChild.getName().equals ("copyMode")) {
                for (int i = 0; i < COPY_MODE_LABELS.length; i++) {
                    if (rChild.getText().toUpperCase().equals (COPY_MODE_LABELS[i])) {
                        this.copyMode = i;
                        break;
                    }
                }
            }

            if (rChild.getName().equals ("comment"))
                this.comment = rChild.getText();

            if (rChild.getName().equals ("dataServiceName"))
                this.dataServiceName = rChild.getText();

            if (rChild.getName().equals ("argument")) {
                if (this.arguments == null)
                    this.arguments = new ArrayList<String>();
                
                this.arguments.add (rChild.getText());
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
        
        if (this.resourceType != null && this.resourceType.length() > 0) {
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("resourceType").setText (this.resourceType));
        }

        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("recursive").setText ("" + this.recursive));

        if (this.targetContainerPath != null && this.targetContainerPath.length() > 0) {
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("targetContainerPath").setText (this.targetContainerPath));
        }

        if (this.newName != null && this.newName.length() > 0) {
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("newName").setText (this.newName));
        }

        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("copyMode").setText (COPY_MODE_LABELS[this.copyMode]));

        if (this.comment != null && this.comment.length() > 0) {
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("comment").setText (this.comment));
        }

        if (this.dataServiceName != null && this.dataServiceName.length() > 0) {
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("dataServiceName").setText (this.dataServiceName));
        }

        if (this.arguments != null && this.arguments.size() > 0) {
            for (String arg : arguments) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("argument").setText (arg));
            }
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
     * @param  id  The ID of the data source record.
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
     * @param  resourcePath  The path of the resource.
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
     * @param  resourceType  The type of the resource.
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
     * Sets the <code>recursive</code> field.
     * </p>
     * 
     * @param  recursive  Indicates whether to create non-existing parent folders when creating a folder.
     */
    public void setRecursive (boolean recursive) {
        this.recursive = recursive;
    }

    /**
     * <p>
     * Returns the value of the <code>recursive</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isRecursive () {
        return recursive;
    }

    /**
     * <p>
     * Sets the <code>targetContainerPath</code> field.
     * </p>
     * 
     * @param  targetContainerPath  The target for move/rename operations.
     */
    public void setTargetContainerPath (String targetContainerPath) {
        this.targetContainerPath = targetContainerPath;
    }

    /**
     * <p>
     * Returns the value of the <code>targetContainerPath</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getTargetContainerPath () {
        return targetContainerPath;
    }

    /**
     * <p>
     * Sets the <code>newName</code> field.
     * </p>
     * 
     * @param  newName  The new name for the resource.
     */
    public void setNewName (String newName) {
        this.newName = newName;
    }

    /**
     * <p>
     * Returns the value of the <code>newName</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getNewName () {
        return newName;
    }

    /**
     * <p>
     * Sets the <code>copyMode</code> field.
     * </p>
     * <ul>
     * <li>"ALTER_NAME_IF_EXISTS" - If a resource of the same name and type of the source
     *         resource already exists in the target container, then avoid conflicts by
     *         automatically generating a new name.  Names are generated by appending a number
     *         to the end of the provided name.</li>
     * <li>"FAIL_IF_EXISTS" - Fails if a resource of the same name and type already exists in the
     *         target container.  The resource will not be copied if this occurs.<li>
     * <li>"OVERWRITE_MERGE_IF_EXISTS" - If a resource of the same name and type of the source
     *         resource already exists in the target container, then overwrite the resource in the
     *         target container.  If the source resource is a container, then merge the contents
     *         of the source container with the corresponding resource in the target.  All
     *         resources in the source container will overwrite those resources with the same name
     *         in the target, but child resources in the target with different names will not be 
     *         overwritten and remain unaltered.</li>
     * <li>"OVERWRITE_REPLACE_IF_EXISTS" - If a resource of the same name and type of the source
     *         resource already exists in the target container, then overwrite the resource in the
     *         target container.  If the source resource is a container, then replace the container
     *         within the target container with the source container.  This is equivalent to
     *         deleting the container in the target before copying the source.</li>
     * </ul>
     * 
     * @param  copyMode  Indicates how copy operations should be performed.
     */
    public void setCopyMode (int copyMode) {
        this.copyMode = copyMode;
    }

    /**
     * <p>
     * Returns the value of the <code>copyMode</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getCopyMode () {
        return copyMode;
    }

    /**
     * <p>
     * Sets the <code>comment</code> field.
     * </p>
     * 
     * @param  comment  The comment to use when unlocking a resource.
     */
    public void setComment (String comment) {
        this.comment = comment;
    }

    /**
     * <p>
     * Returns the value of the <code>comment</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getComment () {
        return comment;
    }

    /**
     * <p>
     * Sets the <code>dataServiceName</code> field.
     * </p>
     * 
     * @param  dataServiceName  The data service to use when executing a published procedure.
     */
    public void setDataServiceName (String dataServiceName) {
        this.dataServiceName = dataServiceName;
    }

    /**
     * <p>
     * Returns the value of the <code>dataServiceName</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getDataServiceName () {
        return dataServiceName;
    }

    /**
     * <p>
     * Sets the <code>arguments</code> field.
     * </p>
     * 
     * @param  arguments  The list of arguments to use when executing a procedure.
     */
    public void setArguments (List<String> arguments) {
        this.arguments = arguments;
    }

    /**
     * <p>
     * Returns the value of the <code>arguments</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<String> getArguments () {
        return arguments;
    }
}

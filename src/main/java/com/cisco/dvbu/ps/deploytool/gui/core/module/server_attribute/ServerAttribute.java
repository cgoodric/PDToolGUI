package com.cisco.dvbu.ps.deploytool.gui.core.module.server_attribute;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.cisco.dvbu.ps.deploytool.gui.core.shared.GenericAttribute;
import com.cisco.dvbu.ps.deploytool.gui.util.StringUtils;

import org.jdom2.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Bean object for server attribute module server attribute records. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 *
 * @author Calvin Goodrich
 * @version 1.0
 */
public class ServerAttribute {

    public static final int TYPE_ATTRIBUTE = 0;
    public static final int TYPE_ATTRIBUTE_DEF = 1;

    private static final Logger log = LoggerFactory.getLogger (ServerAttribute.class);

    // attributes used by the UI
    //
    String operation;
    String origId;
    
    // module specific attributes
    //
    String id;
    int type;
    GenericAttribute attribute;
    ServerAttributeDef attributeDef;

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public ServerAttribute () {}

    /**
     * <p>
     * Copy constructor. Used for making/serializing copies, where only the ID is updated.
     * </p>
     */
    public ServerAttribute (ServerAttribute a) {
        if (a != null) {
            this.operation = a.getOperation();
            this.origId = a.getOrigId();
            this.id = a.getId();
            this.type = a.getType();
            this.attribute = a.getAttribute();
            this.attributeDef = a.getAttributeDef();
        }
    }

    /**
     * <p>
     * Constructor. Extracts attribute values from {@link Element} object.
     * </p>
     */
    public ServerAttribute (Element aNode) {
        log.debug ("Located child " + ": " + aNode.getName() + "{" + aNode.getNamespace().getURI() + "}");
        
        if (aNode.getName().equals ("serverAttribute")) {
            this.attribute = new GenericAttribute (aNode);
            this.type = TYPE_ATTRIBUTE;
            this.id = this.attribute.getId();
        }

        if (aNode.getName().equals ("serverAttributeDef")) {
            this.attributeDef = new ServerAttributeDef (aNode);
            this.type = TYPE_ATTRIBUTE_DEF;
            this.id = this.attributeDef.getId();
        }

        log.debug ("    type = " + this.type + "; id = " + this.id);
    }

    /**
     * <p>
     * Returns the object as a JDom Element.
     * </p>
     * 
     * @param  ignored  Normally, the name of the element to use, but ignored in this case.
     * @param  indent   The number of tabs (spaces) to indent the child elements.
     * @return          The value.
     */
    public Element toElement (
        String ignored,
        int indent
    ) {
        Element result = null;
        //String indentStr = StringUtils.getIndent (indent);
        
        if (this.type == TYPE_ATTRIBUTE) {
            result = attribute.toElement ("serverAttribute", indent);
        } else {
            result = attributeDef.toElement ("serverAttributeDef", indent);
        }
        
        //result.addContent ("\n" + indentStr);

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
     * Sets the <code>type</code> field.
     * </p>
     * 
     * @param  type  Indicates whether the data source entry is relational, generic, or introspection.
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
     * Sets the <code>attribute</code> field.
     * </p>
     * 
     * @param  attribute  The generic attribute.
     */
    public void setAttribute (GenericAttribute attribute) {
        this.attribute = attribute;
        this.type = TYPE_ATTRIBUTE;
        this.id = attribute.getId();
    }

    /**
     * <p>
     * Returns the value of the <code>attribute</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public GenericAttribute getAttribute () {
        return attribute;
    }

    /**
     * <p>
     * Sets the <code>attributeDef</code> field.
     * </p>
     * 
     * @param  attributeDef  The attribute definition.
     */
    public void setAttributeDef (ServerAttribute.ServerAttributeDef attributeDef) {
        this.attributeDef = attributeDef;
        this.type = TYPE_ATTRIBUTE_DEF;
        this.id = attributeDef.getId();
    }

    /**
     * <p>
     * Returns the value of the <code>attributeDef</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public ServerAttribute.ServerAttributeDef getAttributeDef () {
        return attributeDef;
    }

    public static class ServerAttributeDef {
        String id;
        String name;
        String type;
        String allowedValues;
        String annotation;
        String defaultValue;
        String displayName;
        String maxValue;
        String minValue;
        String pattern;
        String suggestedValues;
        String unitName;
        String updateRule;
        
        public ServerAttributeDef() {}

        /**
         * <p>
         * Constructor. Extracts attribute values from {@link Element} object.
         * </p>
         */
        public ServerAttributeDef (Element aNode) {
            for (Element c : aNode.getChildren()) {
                if (c.getName().equals ("id"))
                    this.id = c.getText();
    
                if (c.getName().equals ("name"))
                    this.name = c.getText();
    
                if (c.getName().equals ("type"))
                    this.type = c.getText();
    
                if (c.getName().equals ("allowedValues"))
                    this.allowedValues = c.getText();
    
                if (c.getName().equals ("annotation"))
                    this.annotation = c.getText();
    
                if (c.getName().equals ("defaultValue"))
                    this.defaultValue = c.getText();
    
                if (c.getName().equals ("displayName"))
                    this.displayName = c.getText();
    
                if (c.getName().equals ("maxValue"))
                    this.maxValue = c.getText();
    
                if (c.getName().equals ("minValue"))
                    this.minValue = c.getText();
    
                if (c.getName().equals ("pattern"))
                    this.pattern = c.getText();
    
                if (c.getName().equals ("suggestedValues"))
                    this.suggestedValues = c.getText();
    
                if (c.getName().equals ("unitName"))
                    this.unitName = c.getText();
    
                if (c.getName().equals ("updateRule"))
                    this.updateRule = c.getText();
    
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
            result.addContent (new Element ("name").setText (this.name));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("type").setText (this.type));

            if (this.allowedValues != null && this.allowedValues.length() > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("allowedValues").setText (this.allowedValues));
            }

            if (this.annotation != null && this.annotation.length() > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("annotation").setText (this.annotation));
            }

            if (this.defaultValue != null && this.defaultValue.length() > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("defaultValue").setText (this.defaultValue));
            }

            if (this.displayName != null && this.displayName.length() > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("displayName").setText (this.displayName));
            }

            if (this.maxValue != null && this.maxValue.length() > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("maxValue").setText (this.maxValue));
            }

            if (this.minValue != null && this.minValue.length() > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("minValue").setText (this.minValue));
            }

            if (this.pattern != null && this.pattern.length() > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("pattern").setText (this.pattern));
            }

            if (this.suggestedValues != null && this.suggestedValues.length() > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("suggestedValues").setText (this.suggestedValues));
            }

            if (this.unitName != null && this.unitName.length() > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("unitName").setText (this.unitName));
            }

            if (this.updateRule != null && this.allowedValues.length() > 0) {
                result.addContent ("\n" + updateRule);
                result.addContent (new Element ("updateRule").setText (this.updateRule));
            }

            return result;
        }

        /**
         * <p>
         * Sets the <code>id</code> field.
         * </p>
         * 
         * @param  id  The name of the attribute.
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
         * Sets the <code>name</code> field.
         * </p>
         * 
         * @param  name  The name of the attribute.
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
         * Sets the <code>type</code> field.
         * </p>
         * 
         * @param  type  The data type of the attribute's value.
         */
        public void setType (String type) {
            this.type = type;
        }

        /**
         * <p>
         * Returns the value of the <code>type</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getType () {
            return type;
        }

        /**
         * <p>
         * Sets the <code>allowedValues</code> field.
         * </p>
         * 
         * @param  allowedValues  The space separated list of allowed values.
         */
        public void setAllowedValues (String allowedValues) {
            this.allowedValues = allowedValues;
        }

        /**
         * <p>
         * Returns the value of the <code>allowedValues</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getAllowedValues () {
            return allowedValues;
        }

        /**
         * <p>
         * Sets the <code>annotation</code> field.
         * </p>
         * 
         * @param  annotation  The annotation about the attribute.
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
         * Sets the <code>defaultValue</code> field.
         * </p>
         * 
         * @param  defaultValue  The default value to use when a value is not supplied.
         */
        public void setDefaultValue (String defaultValue) {
            this.defaultValue = defaultValue;
        }

        /**
         * <p>
         * Returns the value of the <code>defaultValue</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getDefaultValue () {
            return defaultValue;
        }

        /**
         * <p>
         * Sets the <code>displayName</code> field.
         * </p>
         * 
         * @param  displayName  The attribute name to display in user interfaces.
         */
        public void setDisplayName (String displayName) {
            this.displayName = displayName;
        }

        /**
         * <p>
         * Returns the value of the <code>displayName</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getDisplayName () {
            return displayName;
        }

        /**
         * <p>
         * Sets the <code>maxValue</code> field.
         * </p>
         * 
         * @param  maxValue  The maximum value for the attribute.
         */
        public void setMaxValue (String maxValue) {
            this.maxValue = maxValue;
        }

        /**
         * <p>
         * Returns the value of the <code>maxValue</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getMaxValue () {
            return maxValue;
        }

        /**
         * <p>
         * Sets the <code>minValue</code> field.
         * </p>
         * 
         * @param  minValue  The minimum value for the attribute.
         */
        public void setMinValue (String minValue) {
            this.minValue = minValue;
        }

        /**
         * <p>
         * Returns the value of the <code>minValue</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getMinValue () {
            return minValue;
        }

        /**
         * <p>
         * Sets the <code>pattern</code> field.
         * </p>
         * 
         * @param  pattern  The pattern of the attribute.
         */
        public void setPattern (String pattern) {
            this.pattern = pattern;
        }

        /**
         * <p>
         * Returns the value of the <code>pattern</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getPattern () {
            return pattern;
        }

        /**
         * <p>
         * Sets the <code>suggestedValues</code> field.
         * </p>
         * 
         * @param  suggestedValues  The space separated list of suggested values.
         */
        public void setSuggestedValues (String suggestedValues) {
            this.suggestedValues = suggestedValues;
        }

        /**
         * <p>
         * Returns the value of the <code>suggestedValues</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getSuggestedValues () {
            return suggestedValues;
        }

        /**
         * <p>
         * Sets the <code>unitName</code> field.
         * </p>
         * 
         * @param  unitName  The unit of measure for the attribute.
         */
        public void setUnitName (String unitName) {
            this.unitName = unitName;
        }

        /**
         * <p>
         * Returns the value of the <code>unitName</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getUnitName () {
            return unitName;
        }

        /**
         * <p>
         * Sets the <code>updateRule</code> field.
         * </p>
         * 
         * @param  updateRule  The update rule of the attribute. Valid values: READ_ONLY and READ_WRITE.
         */
        public void setUpdateRule (String updateRule) {
            this.updateRule = updateRule;
        }

        /**
         * <p>
         * Returns the value of the <code>updateRule</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getUpdateRule () {
            return updateRule;
        }
    }
}

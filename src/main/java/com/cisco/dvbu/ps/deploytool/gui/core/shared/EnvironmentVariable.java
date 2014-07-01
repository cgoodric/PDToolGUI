package com.cisco.dvbu.ps.deploytool.gui.core.shared;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.cisco.dvbu.ps.deploytool.gui.util.StringUtils;

import org.jdom2.Element;

/**
 * <p>
 * Bean object for environment variables. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 *
 * @author Calvin Goodrich
 * @version 1.0
 */
public class EnvironmentVariable {
    private String variable;
    private String value;
    
    /**
     * <p>
     * Constructor.
     * </p>
     */
    public EnvironmentVariable() {};
    
    /**
     * <p>
     * Constructor.
     * </p>
     * 
     * @param variable The variable's name.
     * @param value    The message pertaining to the field.
     */
    public EnvironmentVariable(
        String variable,
        String value
    ) {
        this.variable = variable;
        this.value = value;
    }
    
    /**
     * <p>
     * Constructor. Extracts attribute values from {@link Element} object.
     * </p>
     */
    public EnvironmentVariable (Element eNode) {
        for (Element e : eNode.getChildren()) {
            if (e.getName().equals ("envName"))
                this.variable = e.getText();
            
            if (e.getName().equals ("envValue"))
                this.value = e.getText();
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
        result.addContent (new Element ("envName").setText (this.variable));
            
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("envValue").setText (this.value));
            
        return result;
    }

    /**
     * <p>
     * Sets the <code>variable</code> field.
     * </p>
     * 
     * @param  variable  The ID of the data constituting this row of the result list.
     */
    public void setVariable (String variable) {
        this.variable = variable;
    }

    /**
     * <p>
     * Returns the value of the <code>variable</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getVariable () {
        return variable;
    }

    /**
     * <p>
     * Sets the <code>value</code> field.
     * </p>
     * 
     * @param  value  The ID of the data constituting this row of the result list.
     */
    public void setValue (String value) {
        this.value = value;
    }

    /**
     * <p>
     * Returns the value of the <code>value</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getValue () {
        return value;
    }
}

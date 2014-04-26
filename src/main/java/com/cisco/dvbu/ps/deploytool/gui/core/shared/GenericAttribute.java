package com.cisco.dvbu.ps.deploytool.gui.core.shared;

import com.cisco.dvbu.ps.deploytool.gui.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Bean object for generic attributes. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 *
 * @author Calvin Goodrich
 * @version 1.0
 */
public class GenericAttribute {
    private static final Logger log = LoggerFactory.getLogger (GenericAttribute.class);

    String id;
    String name;
    String type;
    String value;
    List<String> valueArray;
    List<ValueItem> valueList;
    List<MapEntry> valueMap;

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public GenericAttribute() {}

    /**
     * <p>
     * Constructor. Extracts attribute values from {@link Element} object.
     * </p>
     */
    public GenericAttribute (Element gaNode) {
        for (Element c : gaNode.getChildren()) {
            if (c.getName().equals ("id")) {
                log.debug ("        located attribute id = '" + c.getName() + "'");
                this.id = c.getText();
            }
    
            if (c.getName().equals ("name"))
                this.name = c.getText();
    
            if (c.getName().equals ("type"))
                setType (c.getText().toUpperCase());
    
            if (c.getName().equals ("value"))
                setValue (c.getText());
            
            if (c.getName().equals ("valueArray")) {
                for (Element i : c.getChildren()) {
                    if (i.getName().equals ("item")) {
                       if (this.valueArray == null)
                           this.valueArray = new ArrayList<String>();
                       
                       this.valueArray.add (i.getText());
                    }
                }
            }
            
            if (c.getName().equals ("valueList")) {
                for (Element i : c.getChildren()) {
                    if (i.getName().equals ("item")) {
                        if (this.valueList == null)
                            this.valueList = new ArrayList<ValueItem>();
                        
                        this.valueList.add (new ValueItem(i));
                    }
                }
            }
            
            if (c.getName().equals ("valueMap")) {
                for (Element e : c.getChildren()) {
                    
                    if (e.getName().equals("entry")) {
                        MapEntry me = new MapEntry (e);

                        if (me.getKey() != null) {
                            if (this.valueMap == null)
                                this.valueMap = new ArrayList<MapEntry>();
                            
                            this.valueMap.add (me);
                        }
                    }
                }
            }
        }
    }

    /**
     * <p>
     * Returns the object as a JDom Element.
     * </p>
     * 
     * @return     The value.
     */
    public Element toElement (
        String name,
        int indent
    ) {
        Element result = new Element (name);
        String indentStr = StringUtils.getIndent (indent);
        String indentStr2 = StringUtils.getIndent (indent + 1);
        
        if (this.id != null) {
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("id").setText (this.id));
        }
        
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("name").setText (this.name));
            
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("type").setText (this.type));
        
        if (this.value != null) {
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("value").setText (this.value));
        }
        
        if (this.valueArray != null && this.valueArray.size() > 0) {
            Element vaNode = new Element ("valueArray");
            
            for (String item : this.valueArray) {
                vaNode.addContent ("\n" + indentStr2);
                vaNode.addContent (new Element ("item").setText (item));
            }
            
            vaNode.addContent ("\n" + indentStr);
            
            result.addContent ("\n" + indentStr);
            result.addContent (vaNode);
        }
        
        if (this.valueList != null && this.valueList.size() > 0) {
            Element vlNode = new Element ("valueList");
            
            for (ValueItem vi : this.valueList) {
                vlNode.addContent ("\n" + indentStr2);
                vlNode.addContent (vi.toElement ("item", indent + 2).addContent ("\n" + indentStr2));
            }
            
            vlNode.addContent ("\n" + indentStr);
            
            result.addContent ("\n" + indentStr);
            result.addContent (vlNode);
        }
        
        if (this.valueMap != null && this.valueMap.size() > 0) {
            Element vmNode = new Element ("valueMap");
            
            for (MapEntry entry : this.valueMap) {
                vmNode.addContent ("\n" + indentStr2);
                vmNode.addContent (entry.toElement ("entry", indent + 2).addContent ("\n" + indentStr2));
            }
            
            vmNode.addContent ("\n" + indentStr);
            
            result.addContent ("\n" + indentStr);
            result.addContent (vmNode);
        }

        return result;
    }
    
    /**
     * <p>
     * Sets the <code>id</code> field.
     * </p>
     * 
     * @param  id  The attribute's ID.
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
    public String getId() {
        return id;
    }

    /**
     * <p>
     * Sets the <code>name</code> field.
     * </p>
     * 
     * @param  name  The internal name of the attribute.
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
    public String getName() {
        return name;
    }

    /**
     * <p>
     * Sets the <code>type</code> field. If the type is "PASSWORD_STRING" then <code>value</code> is encrypted.
     * </p>
     * 
     * @param  type  The attribute type ("BOOLEAN", "DATE", "STRING", etc.)
     */
    public void setType (String type) {
        this.type = type.toUpperCase();
        if (type != null && type.equalsIgnoreCase ("PASSWORD_STRING") && this.value != null)
            this.value = StringUtils.encryptPassword (this.value);
    }

    /**
     * <p>
     * Returns the value of the <code>type</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getType() {
        return type;
    }

    /**
     * <p>
     * Sets the <code>value</code> field. If the <code>type</code> field is "PASSWORD_STRING" then the value is encrypted.
     * </p>
     * 
     * @param  value  The new value of the attribute.
     */
    public void setValue (String value) {
        if (this.type != null && this.type.equals ("PASSWORD_STRING"))
            this.value = StringUtils.encryptPassword (value);
        else
            this.value = value;
    }

    /**
     * <p>
     * Returns the value of the <code>value</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getValue() {
        return value;
    }

    /**
     * <p>
     * Sets the <code>valueArray</code> field.
     * </p>
     * 
     * @param  valueArray  The value list of list and array attributes.
     */
    public void setValueArray (List<String> valueArray) {
        this.valueArray = valueArray;
    }

    /**
     * <p>
     * Returns the value of the <code>valueArray</code> field.
     * </p>
     * 
     * @return     The value array.
     */
    public List<String> getValueArray() {
        return valueArray;
    }

    /**
     * <p>
     * Sets the <code>valueList</code> field.
     * </p>
     * 
     * @param  valueList  The value list of list and array attributes.
     */
    public void setValueList (List<ValueItem> valueList) {
        this.valueList = valueList;
    }

    /**
     * <p>
     * Returns the value of the <code>valueList</code> field.
     * </p>
     * 
     * @return     The value list.
     */
    public List<ValueItem> getValueList() {
        return valueList;
    }

    /**
     * <p>
     * Sets the <code>valueMap</code> field.
     * </p>
     * 
     * @param  valueMap  The value map of the attribute.
     */
    public void setValueMap (List<MapEntry> valueMap) {
        this.valueMap = valueMap;
    }

    /**
     * <p>
     * Returns the value of the <code>valueMap</code> field.
     * </p>
     * 
     * @return     The value map.
     */
    public List<MapEntry> getValueMap() {
        return valueMap;
    }
    
    /**
     * <p>
     * Bean object for value items for arrays, lists, and maps. Intended to be serialized/deserialized by Jackson into/from JSON.
     * </p>
     *
     * @author Calvin Goodrich
     * @version 1.0
     */
    public static class MapEntry {
        ValueItem key;
        ValueItem value;
        
        /**
         * <p>
         * Constructor.
         * </p>
         */
        public MapEntry() {}
    
        /**
         * <p>
         * Constructor. Extracts attribute values from {@link Element} object.
         * </p>
         */
        public MapEntry (Element eNode) {
            for (Element c: eNode.getChildren()) {
                if (c.getName().equals ("key"))
                    this.key = new ValueItem (c);
        
                if (c.getName().equals ("value"))
                    this.value = new ValueItem (c);
            }
        }
        
        /**
         * <p>
         * Returns the object as a JDom Element.
         * </p>
         * 
         * @return     The value.
         */
        public Element toElement(
            String name,
            int indent
        ) {
            Element result = new Element (name);
            String indentStr = StringUtils.getIndent (indent);

            result.addContent ("\n" + indentStr);
            result.addContent (this.key.toElement ("key", indent + 1).addContent ("\n" + indentStr));
            
            result.addContent ("\n" + indentStr);
            result.addContent (this.value.toElement ("value", indent + 1).addContent ("\n" + indentStr));
            
            return result;
        }

        /**
         * <p>
         * Sets the <code>key</code> field.
         * </p>
         * 
         * @param  key  The key {@link ValueItem} of the map entry
         */
        public void setKey (ValueItem key) {
            this.key = key;
        }
    
        /**
         * <p>
         * Returns the value of the <code>key</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public ValueItem getKey() {
            return key;
        }
    
        /**
         * <p>
         * Sets the <code>value</code> field.
         * </p>
         * 
         * @param  value  The new value {@link ValueItem} of the map entry.
         */
        public void setValue (ValueItem value) {
            this.value = value;
        }
    
        /**
         * <p>
         * Returns the value of the <code>value</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public ValueItem getValue() {
            return value;
        }
    }

    /**
     * <p>
     * Bean object for value items for arrays, lists, and maps. Intended to be serialized/deserialized by Jackson into/from JSON.
     * </p>
     *
     * @author Calvin Goodrich
     * @version 1.0
     */
    public static class ValueItem {
        String type;
        String value;
        
        /**
         * <p>
         * Constructor.
         * </p>
         */
        public ValueItem() {}
    
        /**
         * <p>
         * Constructor. Extracts attribute values from {@link Element} object.
         * </p>
         */
        public ValueItem (Element iNode) {
            for (Element c: iNode.getChildren()) {
                if (c.getName().equals ("type"))
                    this.type = c.getText().toUpperCase();
        
                if (c.getName().equals ("value"))
                    this.value = c.getText();
            }
        }
        
        /**
         * <p>
         * Returns the object as a JDom Element.
         * </p>
         * 
         * @return     The value.
         */
        public Element toElement(
            String name,
            int indent
        ) {
            Element result = new Element (name);
            String indentStr = StringUtils.getIndent (indent);

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("type").setText (this.type));
            
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("value").setText (this.value));
            
            return result;
        }

        /**
         * <p>
         * Sets the <code>type</code> field.
         * </p>
         * 
         * @param  type  The attribute type ("BOOLEAN", "DATE", "STRING", etc.)
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
        public String getType() {
            return type;
        }
    
        /**
         * <p>
         * Sets the <code>value</code> field.
         * </p>
         * 
         * @param  value  The new value of the attribute.
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
        public String getValue() {
            return value;
        }
    }
}

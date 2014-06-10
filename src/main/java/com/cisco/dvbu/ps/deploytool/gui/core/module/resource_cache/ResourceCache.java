package com.cisco.dvbu.ps.deploytool.gui.core.module.resource_cache;

import com.cisco.dvbu.ps.deploytool.gui.core.shared.CalendarPeriod;
import com.cisco.dvbu.ps.deploytool.gui.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * <p>
 * Bean object for resource cache module resource cache records. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 *
 * @author Calvin Goodrich
 * @version 1.0
 */
public class ResourceCache {
    
    public static final int STORAGE_MODE_NULL = 0;
    public static final int STORAGE_MODE_AUTOMATIC = 1;
    public static final int STORAGE_MODE_DATA_SOURCE = 2;
    public static final String[] STORAGE_MODE_LABELS = {
        "",
        "AUTOMATIC",
        "DATA_SOURCE"
    };
    
    public static final int REFRESH_MODE_NULL = 0;
    public static final int REFRESH_MODE_MANUAL = 1;
    public static final int REFRESH_MODE_SCHEDULED = 2;
    public static final String[] REFRESH_MODE_LABELS = {
        "",
        "MANUAL",
        "SCHEDULED"
    };
    
    public static final int CLEAR_RULE_NULL = 0;
    public static final int CLEAR_RULE_NONE = 1;
    public static final int CLEAR_RULE_ON_LOAD = 2;
    public static final int CLEAR_RULE_ON_FAILURE = 3;
    public static final String[] CLEAR_RULE_LABELS = {
        "",
        "NONE",
        "ON_LOAD",
        "ON_FAILURE"
    };

//    private static final Logger log = LoggerFactory.getLogger (ResourceCache.class);

    // attributes used by the UI
    //
    String operation;
    String origId;
    
    // module specific attributes
    //
    String id;
    String resourcePath;
    String resourceType;
    boolean configured = false;
    boolean enabled = false;
    int storageMode = 0;
    String storagePath;
    List<StorageTarget> storageTargets;
    int refreshMode = 0;
    String refreshStartTime;
    CalendarPeriod refreshPeriod;
    CalendarPeriod expirationPeriod;
    int clearRule = 0;

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public ResourceCache () {}

    /**
     * <p>
     * Copy constructor. Used for making/serializing copies, where only the ID is updated.
     * </p>
     * <p>
     * WARNING!!! - This makes "shallow" copies of the List objects (meaning that changing List elements
     * in the copy will update List elements in the original.)
     * </p>
     * 
     * @param   rc   A {@link ResourceCache} object from which to make a copy.
     */
    public ResourceCache (ResourceCache rc) {
        if (rc != null) {
            this.operation = rc.getOperation();
            this.origId = rc.getOrigId();
            this.id = rc.getId();
            this.resourcePath = rc.getResourcePath();
            this.resourceType = rc.getResourceType();
            this.configured = rc.isConfigured();
            this.enabled = rc.isEnabled();
            this.storageMode = rc.getStorageMode();
            this.storagePath = rc.getStoragePath();
            this.storageTargets = (rc.getStorageTargets() != null) ? new ArrayList<StorageTarget>(rc.getStorageTargets()) : null; // do not update!!
            this.refreshMode = rc.getRefreshMode();
            this.refreshStartTime = rc.getRefreshStartTime();
            this.refreshPeriod = rc.getRefreshPeriod();
            this.expirationPeriod = rc.getExpirationPeriod();
            this.clearRule = rc.getClearRule();
        }
    }

    /**
     * <p>
     * Constructor. Extracts attribute values from {@link Element} object.
     * </p>
     */
    public ResourceCache (Element rcNode) {
        for (Element c : rcNode.getChildren()) {
            if (c.getName().equals ("id"))
                this.id = c.getText();
            
            if (c.getName().equals ("resourcePath"))
                this.resourcePath = c.getText();
            
            if (c.getName().equals ("resourceType"))
                this.resourceType = c.getText();
            
            if (c.getName().equals ("cacheConfig")) {
                for (Element ccChild : c.getChildren()) {
                    if (ccChild.getName().equals ("configured"))
                        this.configured = (ccChild.getText() == null) ? true : ccChild.getText().matches ("(?i)^(yes|true|on|1)$");

                    if (ccChild.getName().equals ("enabled"))
                        this.enabled = (ccChild.getText() == null) ? true : ccChild.getText().matches ("(?i)^(yes|true|on|1)$");

                    if (ccChild.getName().equals ("storage")) {
                        for (Element sChild : ccChild.getChildren()) {

                            if (sChild.getName().equals ("mode")) {
                                for (int i = 0; i < STORAGE_MODE_LABELS.length; i++) {
                                    if (sChild.getText().toUpperCase().equals (STORAGE_MODE_LABELS[i])) {
                                        this.storageMode = i;
                                        break;
                                    }
                                }
                            }
            
                            if (sChild.getName().equals ("storageDataSourcePath"))
                                this.storagePath = sChild.getText();
            
                            if (sChild.getName().equals ("storageTargets")) {
                                if (this.storageTargets == null)
                                    this.storageTargets = new ArrayList<StorageTarget>();
                                
                                this.storageTargets.add (new StorageTarget (sChild));
                            }
                        }
                    }

                    if (ccChild.getName().equals ("refresh")) {
                        for (Element rChild : ccChild.getChildren()) {
                            
                            if (rChild.getName().equals ("mode")) {
                                for (int i = 0; i < REFRESH_MODE_LABELS.length; i++) {
                                    if (rChild.getText().toUpperCase().equals (REFRESH_MODE_LABELS[i])) {
                                        this.refreshMode = i;
                                        break;
                                    }
                                }
                            }
                            
                            if (rChild.getName().equals ("schedule")) {
                                for (Element sChild : rChild.getChildren()) {
                                    if (sChild.getName().equals ("startTime"))
                                        this.refreshStartTime = sChild.getText();

                                    if (sChild.getName().equals ("refreshPeriod"))
                                        this.refreshPeriod = new CalendarPeriod (sChild);
                                }
                            }
                        }
                    }
                    
                    if (ccChild.getName().equals ("expirationPeriod"))
                        this.expirationPeriod = new CalendarPeriod (ccChild);

                    if (ccChild.getName().equals ("clearRule")) {
                        for (int i = 0; i < CLEAR_RULE_LABELS.length; i++) {
                            if (ccChild.getText().toUpperCase().equals (CLEAR_RULE_LABELS[i])) {
                                this.clearRule = i;
                                break;
                            }
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
        String indentStr3 = StringUtils.getIndent (indent + 2);
        String indentStr4 = StringUtils.getIndent (indent + 3);
        
        Element result = new Element (name);
        
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("id").setText (this.id));
        
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("resourcePath").setText (this.resourcePath));
        
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("resourceType").setText (this.resourceType));
        
        Element cacheConfig = new Element ("cacheConfig");
        
            cacheConfig.addContent ("\n" + indentStr2);
            cacheConfig.addContent (new Element ("configured").setText ("" + this.configured));
        
            cacheConfig.addContent ("\n" + indentStr2);
            cacheConfig.addContent (new Element ("enabled").setText ("" + this.enabled));
            
            if (this.storageMode != STORAGE_MODE_NULL ||
                this.storagePath != null && this.storagePath.length() > 0 ||
                this.storageTargets != null && this.storageTargets.size() > 0
            ) {
                Element storage = new Element ("storage");
                
                    storage.addContent ("\n" + indentStr3);
                    storage.addContent (new Element ("mode").setText (STORAGE_MODE_LABELS[this.storageMode]));
                    
                    if (this.storagePath != null && this.storagePath.length() > 0) {
                        storage.addContent ("\n" + indentStr3);
                        storage.addContent (new Element ("storageDataSourcePath").setText (this.storagePath));
                    }
                    
                    if (this.storageTargets != null && this.storageTargets.size() > 0) {
                        for (StorageTarget st : this.storageTargets) {
                            storage.addContent ("\n" + indentStr3);
                            storage.addContent (st.toElement ("storageTargets", indent + 3));
                        }
                    }
                    
                    storage.addContent ("\n" + indentStr2);
                    
                cacheConfig.addContent ("\n" + indentStr2);
                cacheConfig.addContent (storage);
            }
        
            if (this.refreshMode != REFRESH_MODE_NULL ||
                this.refreshStartTime != null && this.refreshStartTime.length() > 0 ||
                this.refreshPeriod != null
            ) {
                Element refresh = new Element ("refresh");
                
                    refresh.addContent ("\n" + indentStr3);
                    refresh.addContent (new Element ("mode").setText (REFRESH_MODE_LABELS[this.refreshMode]));
                    
                    if (this.refreshStartTime != null && this.refreshStartTime.length() > 0 && 
                        this.refreshPeriod != null
                    ) {
                        Element schedule = new Element ("schedule");
                        
                        if (this.refreshStartTime != null && this.refreshStartTime.length() > 0) {
                            schedule.addContent ("\n" + indentStr4);
                            schedule.addContent (new Element ("startTime").setText (this.refreshStartTime));
                        }
                        
                        if (this.refreshPeriod != null) {
                            schedule.addContent ("\n" + indentStr4);
                            schedule.addContent (this.refreshPeriod.toElement ("refreshPeriod", indent + 4).addContent ("\n" + indentStr4));
                        }
                        
                        schedule.addContent ("\n" + indentStr3);
                        
                        refresh.addContent ("\n" + indentStr3);
                        refresh.addContent (schedule);
                    }
                    
                    refresh.addContent ("\n" + indentStr2);
                
                cacheConfig.addContent ("\n" + indentStr2);
                cacheConfig.addContent (refresh);
            }
        
            if (this.expirationPeriod != null) {
                cacheConfig.addContent ("\n" + indentStr2);
                cacheConfig.addContent (this.expirationPeriod.toElement ("expirationPeriod", indent + 2).addContent ("\n" + indentStr2));
            }
        
            if (this.clearRule != CLEAR_RULE_NULL) {
                cacheConfig.addContent ("\n" + indentStr2);
                cacheConfig.addContent (new Element ("clearRule").setText(CLEAR_RULE_LABELS[this.clearRule]));
            }
            
            cacheConfig.addContent ("\n" + indentStr);
        
        result.addContent ("\n" + indentStr);
        result.addContent (cacheConfig);
        
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
     * @param  id  The ID of the resource cache record.
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
     * @param  resourcePath  The path to the resource to be cached.
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
     * @param  resourceType  The type of resource being cached.
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
     * Sets the <code>configured</code> field.
     * </p>
     * 
     * @param  configured  Indicates whether caching for the resource is configured.
     */
    public void setConfigured (boolean configured) {
        this.configured = configured;
    }

    /**
     * <p>
     * Returns the value of the <code>configured</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isConfigured () {
        return configured;
    }

    /**
     * <p>
     * Sets the <code>enabled</code> field.
     * </p>
     * 
     * @param  enabled  Indicates whether caching for the resource is enabled.
     */
    public void setEnabled (boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * <p>
     * Returns the value of the <code>enabled</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isEnabled () {
        return enabled;
    }

    /**
     * <p>
     * Sets the <code>storageMode</code> field.
     * </p>
     * 
     * @param  storageMode  The storage mode. Valid values are 0 (AUTOMATIC) and 1 (DATA_SOURCE).
     */
    public void setStorageMode (int storageMode) {
        this.storageMode = storageMode;
    }

    /**
     * <p>
     * Returns the value of the <code>storageMode</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getStorageMode () {
        return storageMode;
    }

    /**
     * <p>
     * Sets the <code>storagePath</code> field.
     * </p>
     * 
     * @param  storagePath  The path to the caching data source.
     */
    public void setStoragePath (String storagePath) {
        this.storagePath = storagePath;
    }

    /**
     * <p>
     * Returns the value of the <code>storagePath</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getStoragePath () {
        return storagePath;
    }

    /**
     * <p>
     * Sets the <code>storageTargets</code> field.
     * </p>
     * 
     * @param  storageTargets  The list of cache storage targets (procedure caching may use multiple targets.)
     */
    public void setStorageTargets (List<ResourceCache.StorageTarget> storageTargets) {
        this.storageTargets = storageTargets;
    }

    /**
     * <p>
     * Returns the value of the <code>storageTargets</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<ResourceCache.StorageTarget> getStorageTargets () {
        return storageTargets;
    }

    /**
     * <p>
     * Sets the <code>refreshMode</code> field.
     * </p>
     * 
     * @param  refreshMode  The refresh mode. Valid values are 0 (MANUAL) and (SCHEDULED).
     */
    public void setRefreshMode (int refreshMode) {
        this.refreshMode = refreshMode;
    }

    /**
     * <p>
     * Returns the value of the <code>refreshMode</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getRefreshMode () {
        return refreshMode;
    }

    /**
     * <p>
     * Sets the <code>refreshStartTime</code> field.
     * </p>
     * 
     * @param  refreshStartTime  The date/time at which a cache refresh should occur. Scheduled 
     *                           periodic refreshes begin their periods from this date/time.
     */
    public void setRefreshStartTime (String refreshStartTime) {
        this.refreshStartTime = refreshStartTime;
    }

    /**
     * <p>
     * Returns the value of the <code>refreshStartTime</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getRefreshStartTime () {
        return refreshStartTime;
    }

    /**
     * <p>
     * Sets the <code>refreshPeriod</code> field.
     * </p>
     * 
     * @param  refreshPeriod  Describes the refresh period.
     */
    public void setRefreshPeriod (CalendarPeriod refreshPeriod) {
        this.refreshPeriod = refreshPeriod;
    }

    /**
     * <p>
     * Returns the value of the <code>refreshPeriod</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public CalendarPeriod getRefreshPeriod () {
        return refreshPeriod;
    }

    /**
     * <p>
     * Sets the <code>expirationPeriod</code> field.
     * </p>
     * 
     * @param  expirationPeriod  Describes the expiration period.
     */
    public void setExpirationPeriod (CalendarPeriod expirationPeriod) {
        this.expirationPeriod = expirationPeriod;
    }

    /**
     * <p>
     * Returns the value of the <code>id</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public CalendarPeriod getExpirationPeriod () {
        return expirationPeriod;
    }

    /**
     * <p>
     * Sets the <code>clearRule</code> field.
     * </p>
     * 
     * @param  clearRule  Describes when to clear data from the cache. Valid values are 0 (NONE),
     *                    1 (ON_LOAD), and 2 (ON_FAILURE).
     */
    public void setClearRule (int clearRule) {
        this.clearRule = clearRule;
    }

    /**
     * <p>
     * Returns the value of the <code>clearRule</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getClearRule () {
        return clearRule;
    }

    /**
     * <p>
     * Bean object for cache storage targets. Intended to be serialized/deserialized by Jackson into/from JSON.
     * </p>
     *
     * @author Calvin Goodrich
     * @version 1.0
     */
    public static class StorageTarget {
        String targetName;
        String path;
        String type = "TABLE"; // this is always "TABLE". no accessors required.

        /**
         * <p>
         * Constructor.
         * </p>
         */
        public StorageTarget() {}

        /**
         * <p>
         * Constructor. Extracts attribute values from {@link Element} object.
         * </p>
         */
        public StorageTarget (Element stNode) {
            for (Element c : stNode.getChildren()) {
                if (c.getName().equals ("targetName"))
                    this.targetName = c.getText();

                if (c.getName().equals ("path"))
                    this.path = c.getText();
                
                // not looking for the "type" element. it is hard coded.
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
            result.addContent (new Element ("targetName").setText (this.targetName));
    
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("path").setText (this.path));
    
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("type").setText ("TABLE"));
    
            return result;
        }
    
        /**
         * <p>
         * Sets the <code>targetName</code> field.
         * </p>
         * 
         * @param  targetName  The name of the caching target. For tables, this is "result". For procedures, this will either
         *                     be the name of a cursor parameter, or an empty string for the scalar parameter values.
         */
        public void setTargetName (String targetName) {
            this.targetName = targetName;
        }

        /**
         * <p>
         * Returns the value of the <code>targetName</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getTargetName () {
            return targetName;
        }

        /**
         * <p>
         * Sets the <code>path</code> field.
         * </p>
         * 
         * @param  path  The relative path to the target table in the data source.
         */
        public void setPath (String path) {
            this.path = path;
        }

        /**
         * <p>
         * Returns the value of the <code>path</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getPath () {
            return path;
        }
    }
}

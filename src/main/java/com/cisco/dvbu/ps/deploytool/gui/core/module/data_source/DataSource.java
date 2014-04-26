package com.cisco.dvbu.ps.deploytool.gui.core.module.data_source;

import com.cisco.dvbu.ps.deploytool.gui.core.shared.GenericAttribute;
import com.cisco.dvbu.ps.deploytool.gui.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Bean object for data source module data source records. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 *
 * @author Calvin Goodrich
 * @version 1.0
 */
public class DataSource {
    
    // public constants
    public static final int TYPE_GENERIC = 0;
    public static final int TYPE_RELATIONAL = 1;
    public static final int TYPE_INTROSPECT = 2;
    public static final int TYPE_ATTRIBUTE_DEFS = 3;
    public static final int TYPE_DATA_SOURCE_TYPES = 4;
    public static final String[] TYPE_LABELS = {
        "Generic",
        "Relational",
        "Introspect",
        "Attribute Definitions",
        "Data Source Types"
    };
    
    private static final Logger log = LoggerFactory.getLogger (DataSource.class);

    // attributes used by the UI
    //
    String operation;
    String origId;
    
    // module specific attributes
    //
    String id;
    int type;
    String resourcePath;
    String resourceType;
    String subtype;
    String dataSourceType;
    List<GenericAttribute> genericAttributes;
    
    // relational attributes
    //
    String hostname;
    int port;
    String databaseName;
    String login;
    String encryptedPassword;
    String valQuery;
    
    // introspect attributes
    //
    boolean runInBackgroundTransaction = false; // PDTool only supports foreground processing.
    String reportDetail;
    List<IntrospectionPlan> plans;
    
    // attribute defs attributes
    //
    int childCount;
    List<AttributeDef> attributeDefs;
    
    // data source type attributes
    //
    List<DataSourceType> dataSourceTypes;

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public DataSource () {}

    /**
     * <p>
     * Copy constructor. Used for making/serializing copies, where only the ID is updated.
     * </p>
     * <p>
     * WARNING!!! - This makes "shallow" copies of the List objects (meaning that changing List elements
     * in the copy will update List elements in the original.)
     * </p>
     * 
     * @param   ds   A {@link DataSource} object from which to make a copy.
     */
    public DataSource (DataSource ds) {
        if (ds != null) {
            this.operation = ds.getOperation();
            this.origId = ds.getOrigId();
            this.id = ds.getId();
            this.type = ds.getType();
            this.resourcePath = ds.getResourcePath();
            this.resourceType = ds.getResourceType();
            this.subtype = ds.getSubtype();
            this.dataSourceType = ds.getDataSourceType();
            this.genericAttributes = (ds.getGenericAttributes() != null) ? new ArrayList<GenericAttribute> (ds.getGenericAttributes()) : null; // do not update!!
            this.hostname = ds.getHostname();
            this.port = ds.getPort();
            this.databaseName = ds.getDatabaseName();
            this.login = ds.getLogin();
            this.encryptedPassword = ds.getEncryptedPassword();
            this.valQuery = ds.getValQuery();
            this.runInBackgroundTransaction = ds.isRunInBackgroundTransaction();
            this.reportDetail = ds.getReportDetail();
            this.plans = (ds.getPlans() != null) ? new ArrayList<IntrospectionPlan> (ds.getPlans()) : null; // do not update!
            this.childCount = ds.getChildCount();
            this.attributeDefs = (ds.getAttributeDefs() != null) ? new ArrayList<AttributeDef> (ds.getAttributeDefs()) : null; // do not update!
            this.dataSourceTypes = ds.getDataSourceTypes();
        }
    }

    /**
     * <p>
     * Constructor. Extracts attribute values from {@link Element} object.
     * </p>
     */
    public DataSource (Element dsNode) {
        for (Element dst : dsNode.getChildren()) {
            if (dst.getName().equals ("relationalDataSource")) {
                log.debug ("    Located relational data source.");
                this.type = TYPE_RELATIONAL;
            } else if (dst.getName().equals ("introspectDataSource")) {
                log.debug ("    Located introspect data source.");
                this.type = TYPE_INTROSPECT;
            } else if (dst.getName().equals ("attributeDefsDataSource")) {
                log.debug ("    Located attribute defs data source.");
                this.type = TYPE_ATTRIBUTE_DEFS;
            } else if (dst.getName().equals ("dataSourceTypesDataSource")) {
                log.debug ("    Located data sources types data source.");
                this.type = TYPE_DATA_SOURCE_TYPES;
            } else {
                log.debug ("    Located generic data source.");
                this.type = TYPE_GENERIC;
            }
            
            for (Element dstChild : dst.getChildren()) {
            
                // capture attributes common to all data source types (except the dataSourceTypesDataSource type) 
                //
                if (dstChild.getName().equals ("id")) {
                    log.debug ("      Located data source ID \"" + dstChild.getText() + "\".");

                    if (dstChild.getText() != null || dstChild.getText().length() > 0)
                        this.id = dstChild.getText();
                }

                if (dstChild.getName().equals ("resourcePath"))
                    this.resourcePath = dstChild.getText();

                if (dstChild.getName().equals ("resourceType"))
                    this.resourceType = dstChild.getText();

                if (dstChild.getName().equals ("subtype"))
                    this.subtype = dstChild.getText();

                if (dstChild.getName().equals ("dataSourceType") && this.type != TYPE_DATA_SOURCE_TYPES)
                    this.dataSourceType = dstChild.getText();

                if (dstChild.getName().equals ("genericAttribute")) {
                    if (this.genericAttributes == null)
                        this.genericAttributes = new ArrayList<GenericAttribute>();
                    
                    this.genericAttributes.add (new GenericAttribute (dstChild));
                }

                // capture data source specific attributes
                //
                switch (this.type) {
                    case TYPE_RELATIONAL:
                        if (dstChild.getName().equals ("hostname"))
                            this.hostname = dstChild.getText();
    
                        if (dstChild.getName().equals ("port"))
                            this.port = Integer.parseInt (dstChild.getText().trim());
    
                        if (dstChild.getName().equals ("databaseName"))
                            this.databaseName = dstChild.getText();
    
                        if (dstChild.getName().equals ("login"))
                            this.login = dstChild.getText();
    
                        if (dstChild.getName().equals ("encryptedPassword"))
                            this.encryptedPassword = dstChild.getText();
    
                        if (dstChild.getName().equals ("valQuery"))
                            this.valQuery = dstChild.getText();
                            
                        break;
                    
                    case TYPE_INTROSPECT:
                        
                        if (dstChild.getName().equals ("runInBackgroundTransaction"))
                            this.runInBackgroundTransaction = (dstChild.getText() == null) ? true : dstChild.getText().matches ("(?i)^(yes|true|on|1)$");

                        if (dstChild.getName().equals ("reportDetail"))
                            this.reportDetail = dstChild.getText();
                    
                        // i THINK there should only be one plan, but it's not clear from the XSD.
                        //
                        if (dstChild.getName().equals ("plan")) {
                            if (this.plans == null)
                                this.plans = new ArrayList<IntrospectionPlan>();
                            
                            this.plans.add (new IntrospectionPlan (dstChild));
                        }

                        break;
                    
                    case TYPE_ATTRIBUTE_DEFS:
                        
                        if (dstChild.getName().equals ("childCount"))
                            this.childCount = Integer.parseInt (dstChild.getText().trim());
                        
                        if (dstChild.getName().equals ("attributeDefs")) {
                            for (Element adChild : dstChild.getChildren()) {
                                if (adChild.getName().equals ("attributeDef")) {
                                    if (this.attributeDefs == null)
                                        this.attributeDefs = new ArrayList<AttributeDef>();
                                    
                                    this.attributeDefs.add (new AttributeDef (adChild));
                                }
                            }
                        }
                    
                        break;
                    
                    case TYPE_DATA_SOURCE_TYPES:
                    
                        if (dstChild.getName().equals ("dataSourceType")) {
                            if (this.dataSourceTypes == null)
                                this.dataSourceTypes = new ArrayList<DataSourceType>();
                            
                            this.dataSourceTypes.add (new DataSourceType (dstChild));
                        }
                        
                        break;

                } // switch (this.type)

            } // for (Element dstChild : dst.getChildren())

        } // for (Element dst : dsNode.getChildren())
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
        
        Element result = new Element (name);
        
        Element dstNode;
        
        switch (this.type) {
            case TYPE_RELATIONAL:
                dstNode = new Element ("relationalDataSource");
                break;
            
            case TYPE_INTROSPECT:
                dstNode = new Element ("introspectDataSource");
                break;
            
            case TYPE_ATTRIBUTE_DEFS:
                dstNode = new Element ("attributeDefsDataSource");
                break;
            
            case TYPE_DATA_SOURCE_TYPES:
                dstNode = new Element ("dataSourceTypesDataSource");
                break;
            
            default:
                dstNode = new Element ("genericDataSource");
                break;
        }

        if (this.id != null && this.id.length() > 0) {
            dstNode.addContent ("\n" + indentStr2);
            dstNode.addContent (new Element ("id").setText (this.id));
        }

        if (this.resourcePath != null && this.resourcePath.length() > 0) {
            dstNode.addContent ("\n" + indentStr2);
            dstNode.addContent (new Element ("resourcePath").setText (this.resourcePath));
        }

        if (this.type != TYPE_INTROSPECT && 
            this.type != TYPE_DATA_SOURCE_TYPES
        ) {
            if (this.resourceType != null && this.resourceType.length() > 0) {
                dstNode.addContent ("\n" + indentStr2);
                dstNode.addContent (new Element ("resourceType").setText (this.resourceType));
            }
            
            if (this.subtype != null && this.subtype.length() > 0) {
                dstNode.addContent ("\n" + indentStr2);
                dstNode.addContent (new Element ("subtype").setText (this.subtype));
            }
            
            if (this.dataSourceType != null && this.dataSourceType.length() > 0) {
                dstNode.addContent ("\n" + indentStr2);
                dstNode.addContent (new Element ("dataSourceType").setText (this.dataSourceType));
            }
        }
        
        switch (this.type) {
            case TYPE_RELATIONAL:
                if (this.hostname != null && this.hostname.length() > 0) {
                    dstNode.addContent ("\n" + indentStr2);
                    dstNode.addContent (new Element ("hostname").setText (this.hostname));
                }
        
                dstNode.addContent ("\n" + indentStr2);
                dstNode.addContent (new Element ("port").setText ("" + this.port));
        
                if (this.databaseName != null && this.databaseName.length() > 0) {
                    dstNode.addContent ("\n" + indentStr2);
                    dstNode.addContent (new Element ("databaseName").setText (this.databaseName));
                }
        
                if (this.login != null && this.login.length() > 0) {
                    dstNode.addContent ("\n" + indentStr2);
                    dstNode.addContent (new Element ("login").setText (this.login));
                }
        
                if (this.encryptedPassword != null && this.encryptedPassword.length() > 0) {
                    dstNode.addContent ("\n" + indentStr2);
                    dstNode.addContent (new Element ("encryptedPassword").setText (this.encryptedPassword));
                }
        
                if (this.valQuery != null && this.valQuery.length() > 0) {
                    dstNode.addContent ("\n" + indentStr2);
                    dstNode.addContent (new Element ("valQuery").setText (this.valQuery));
                }
        
                break;
            
            case TYPE_INTROSPECT:
                dstNode.addContent ("\n" + indentStr2);
                dstNode.addContent (new Element ("runInBackgroundTransaction").setText ("" + this.runInBackgroundTransaction));
        
                if (this.reportDetail != null && this.reportDetail.length() > 0) {
                    dstNode.addContent ("\n" + indentStr2);
                    dstNode.addContent (new Element ("reportDetail").setText (this.reportDetail));
                }
                
                if (this.plans != null) {
                    for (IntrospectionPlan ip : this.plans) {
                        dstNode.addContent ("\n" + indentStr2);
                        dstNode.addContent (ip.toElement ("plan", indent + 2).addContent ("\n" + indentStr2));
                    }
                }
        
                break;
            
            case TYPE_ATTRIBUTE_DEFS:
                dstNode.addContent ("\n" + indentStr2);
                dstNode.addContent (new Element ("childCount").setText ("" + this.childCount));
        
                if (this.attributeDefs != null) {
                    Element adsNode = new Element ("attributeDefs");
                    
                    for (AttributeDef ad : this.attributeDefs) {
                        adsNode.addContent ("\n" + indentStr3);
                        adsNode.addContent (ad.toElement ("attributeDef", indent + 3));
                    }

                    adsNode.addContent ("\n" + indentStr2);
                    
                    dstNode.addContent ("\n" + indentStr2);
                    dstNode.addContent (adsNode);
                }
        
                break;
            
            case TYPE_DATA_SOURCE_TYPES:
                if (this.dataSourceTypes != null) {
                    for (DataSourceType dst : this.dataSourceTypes) {
                        dstNode.addContent ("\n" + indentStr2);
                        dstNode.addContent (dst.toElement ("dataSourceType", indent + 2));
                    }
                }
      
                break;
        }
        
        if (this.genericAttributes != null) {
            for (GenericAttribute ga : this.genericAttributes) {
                dstNode.addContent ("\n" + indentStr2);
                dstNode.addContent (ga.toElement ("genericAttribute", indent + 2).addContent("\n" + indentStr2));
            }
        }

        dstNode.addContent ("\n" + indentStr);
        
        result.addContent ("\n" + indentStr);
        result.addContent (dstNode);
                    
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
     * Sets the <code>resourcePath</code> field.
     * </p>
     * 
     * @param  resourcePath  The resource path of the data source record.
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
     * Sets the <code>genericAttributes</code> field.
     * </p>
     * 
     * @param  genericAttributes  The generic attributes of the data source record.
     */
    public void setGenericAttributes (List<GenericAttribute> genericAttributes) {
        this.genericAttributes = genericAttributes;
    }

    /**
     * <p>
     * Returns the value of the <code>genericAttributes</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<GenericAttribute> getGenericAttributes () {
        return genericAttributes;
    }

    /**
     * <p>
     * Sets the <code>hostname</code> field for relational data sources.
     * </p>
     * 
     * @param  hostname  The hostname of the data source record.
     */
    public void setHostname (String hostname) {
        this.hostname = hostname;
    }

    /**
     * <p>
     * Returns the value of the <code>hostname</code> field for relational data sources.
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
     * @param  port  The port of the data source record for relational data sources.
     */
    public void setPort (int port) {
        this.port = port;
    }

    /**
     * <p>
     * Returns the value of the <code>port</code> field for relational data sources.
     * </p>
     * 
     * @return     The value.
     */
    public int getPort () {
        return port;
    }

    /**
     * <p>
     * Sets the <code>databaseName</code> field for relational data sources.
     * </p>
     * 
     * @param  databaseName  The database name of the data source record.
     */
    public void setDatabaseName (String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * <p>
     * Returns the value of the <code>databaseName</code> field for relational data sources.
     * </p>
     * 
     * @return     The value.
     */
    public String getDatabaseName () {
        return databaseName;
    }

    /**
     * <p>
     * Sets the <code>login</code> field.
     * </p>
     * 
     * @param  login  The login of the data source record for relational data sources.
     */
    public void setLogin (String login) {
        this.login = login;
    }

    /**
     * <p>
     * Returns the value of the <code>login</code> field for relational data sources.
     * </p>
     * 
     * @return     The value.
     */
    public String getLogin () {
        return login;
    }

    /**
     * <p>
     * Sets the <code>encryptedPassword</code> field for relational data sources.
     * </p>
     * 
     * @param  encryptedPassword  The password of the data source record. If the value is not encrypted,
     *                            it is encrypted using CIS's password encryption methodology.
     */
    public void setEncryptedPassword (String encryptedPassword) {
        this.encryptedPassword = StringUtils.encryptPassword (encryptedPassword);
    }

    /**
     * <p>
     * Returns the value of the <code>encryptedPassword</code> field for relational data sources.
     * </p>
     * 
     * @return     The value.
     */
    public String getEncryptedPassword () {
        return encryptedPassword;
    }

    /**
     * <p>
     * Sets the <code>valQuery</code> field.
     * </p>
     * 
     * @param  valQuery  The validation query of the data source record for relational data sources.
     */
    public void setValQuery (String valQuery) {
        this.valQuery = valQuery;
    }

    /**
     * <p>
     * Returns the value of the <code>valQuery</code> field for relational data sources.
     * </p>
     * 
     * @return     The value.
     */
    public String getValQuery () {
        return valQuery;
    }

    /**
     * <p>
     * Sets the <code>runInBackgroundTransaction</code> field for introspect data sources.
     * </p>
     * 
     * @param  runInBackgroundTransaction  Indicates whether the introspection should run as a background process.
     */
    public void setRunInBackgroundTransaction (boolean runInBackgroundTransaction) {
        //this.runInBackgroundTransaction = runInBackgroundTransaction;
        this.runInBackgroundTransaction = false; // PDTool only supports foreground processing. 
    }

    /**
     * <p>
     * Returns the value of the <code>runInBackgroundTransaction</code> field for introspect data sources.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isRunInBackgroundTransaction () {
        return runInBackgroundTransaction;
    }

    /**
     * <p>
     * Sets the <code>reportDetail</code> field.
     * </p>
     * 
     * @param  reportDetail  Indicates the level of reporting while the introspection is running. Valid values are "SUMMARY", "SIMPLE", 
     *                       "SIMPLE_COMPRESSED", and "FULL". For introspect data sources
     */
    public void setReportDetail (String reportDetail) {
        this.reportDetail = reportDetail;
    }

    /**
     * <p>
     * Returns the value of the <code>reportDetail</code> field for introspect data sources.
     * </p>
     * 
     * @return     The value.
     */
    public String getReportDetail () {
        return reportDetail;
    }

    /**
     * <p>
     * Sets the <code>plans</code> field for introspect data sources.
     * </p>
     * 
     * @param  plans  The introspection plans.
     */
    public void setPlans (List<DataSource.IntrospectionPlan> plans) {
        this.plans = plans;
    }

    /**
     * <p>
     * Returns the value of the <code>plans</code> field for introspect data sources.
     * </p>
     * 
     * @return     The value.
     */
    public List<DataSource.IntrospectionPlan> getPlans () {
        return plans;
    }

    /**
     * <p>
     * Sets the <code>resourceType</code> field for attribute defs data sources.
     * </p>
     * 
     * @param  resourceType  The resource type. Typeically "DATA_SOURCE" or "TREE".
     */
    public void setResourceType (String resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * <p>
     * Returns the value of the <code>resourceType</code> field for attribute defs data sources.
     * </p>
     * 
     * @return     The value.
     */
    public String getResourceType () {
        return resourceType;
    }

    /**
     * <p>
     * Sets the <code>subtype</code> field for attribute defs data sources.
     * </p>
     * 
     * @param  subtype  The subtype.
     */
    public void setSubtype (String subtype) {
        this.subtype = subtype;
    }

    /**
     * <p>
     * Returns the value of the <code>subtype</code> field for attribute defs data sources.
     * </p>
     * 
     * @return     The value.
     */
    public String getSubtype () {
        return subtype;
    }

    /**
     * <p>
     * Sets the <code>childCount</code> field for attribute defs data sources.
     * </p>
     * 
     * @param  childCount  The child count.
     */
    public void setChildCount (int childCount) {
        this.childCount = childCount;
    }

    /**
     * <p>
     * Returns the value of the <code>childCount</code> field for attribute defs data sources.
     * </p>
     * 
     * @return     The value.
     */
    public int getChildCount () {
        return childCount;
    }

    /**
     * <p>
     * Sets the <code>dataSourceType</code> field for attribute defs data sources.
     * </p>
     * 
     * @param  dataSourceType  The data source type name.
     */
    public void setDataSourceType (String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    /**
     * <p>
     * Returns the value of the <code>dataSourceType</code> field for attribute defs data sources.
     * </p>
     * 
     * @return     The value.
     */
    public String getDataSourceType () {
        return dataSourceType;
    }

    /**
     * <p>
     * Sets the <code>attributeDefs</code> field for attribute defs data sources.
     * </p>
     * 
     * @param  attributeDefs  The list of data source attributes.
     */
    public void setAttributeDefs (List<AttributeDef> attributeDefs) {
        this.attributeDefs = attributeDefs;
    }

    /**
     * <p>
     * Returns the value of the <code>attributeDefs</code> field for attribute defs data sources.
     * </p>
     * 
     * @return     The value.
     */
    public List<AttributeDef> getAttributeDefs () {
        return attributeDefs;
    }

    /**
     * <p>
     * Sets the <code>dataSourceTypes</code> field for data source attributes data sources.
     * </p>
     * 
     * @param  dataSourceTypes  The list of data source types.
     */
    public void setDataSourceTypes (List<DataSourceType> dataSourceTypes) {
        this.dataSourceTypes = dataSourceTypes;
    }

    /**
     * <p>
     * Returns the value of the <code>dataSourceTypes</code> field for data source type data sources.
     * </p>
     * 
     * @return     The value.
     */
    public List<DataSourceType> getDataSourceTypes () {
        return dataSourceTypes;
    }
    

    /**
     * <p>
     * Bean object for data source module record introspection plan lists. Intended to be serialized/deserialized by Jackson into/from JSON.
     * </p>
     *
     * @author Calvin Goodrich
     * @version 1.0
     */
    public static class IntrospectionPlan {
        boolean updateAllIntrospectedResources = false;
        boolean failFast = false;
        boolean commitOnFailure = false;
        boolean autoRollback = false;
        boolean scanForNewResourcesToAutoAdd = false;
        List<PlanEntry> planEntries;
        
        /**
         * <p>
         * Constructor.
         * </p>
         */
        public IntrospectionPlan () {}

        /**
         * <p>
         * Constructor. Extracts attribute values from {@link Element} object.
         * </p>
         */
        public IntrospectionPlan (Element ipNode) {
            for (Element c : ipNode.getChildren()) {
                if (c.getName().equals ("updateAllIntrospectedResources"))
                    this.updateAllIntrospectedResources = (c.getText() == null) ? true : c.getText().matches ("(?i)^(yes|true|on|1)$");
    
                if (c.getName().equals ("failFast"))
                    this.failFast = (c.getText() == null) ? true : c.getText().matches ("(?i)^(yes|true|on|1)$");
    
                if (c.getName().equals ("commitOnFailure"))
                    this.commitOnFailure = (c.getText() == null) ? true : c.getText().matches ("(?i)^(yes|true|on|1)$");
    
                if (c.getName().equals ("autoRollback"))
                    this.autoRollback = (c.getText() == null) ? true : c.getText().matches ("(?i)^(yes|true|on|1)$");
    
                if (c.getName().equals ("scanForNewResourcesToAutoAdd"))
                    this.scanForNewResourcesToAutoAdd = (c.getText() == null) ? true : c.getText().matches ("(?i)^(yes|true|on|1)$");
                
                if (c.getName().equals ("planEntry")) {
                    if (this.planEntries == null)
                        this.planEntries = new ArrayList<PlanEntry>();
                    
                    this.planEntries.add (new PlanEntry (c));
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
            result.addContent (new Element ("updateAllIntrospectedResources").setText ("" + this.updateAllIntrospectedResources));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("failFast").setText ("" + this.failFast));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("commitOnFailure").setText ("" + this.commitOnFailure));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("autoRollback").setText ("" + this.autoRollback));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("scanForNewResourcesToAutoAdd").setText ("" + this.scanForNewResourcesToAutoAdd));
            
            if (this.planEntries != null) {
                for (PlanEntry pe : planEntries) {
                    result.addContent ("\n" + indentStr);
                    result.addContent (pe.toElement ("planEntry", indent + 1).addContent ("\n" + indentStr));
                }
            }

            return result;
        }

        /**
         * <p>
         * Sets the <code>updateAllIntrospectedResources</code> field.
         * </p>
         * 
         * @param  updateAllIntrospectedResources  Indicates whether introspection should update all existing introspected items.
         */
        public void setUpdateAllIntrospectedResources (boolean updateAllIntrospectedResources) {
            this.updateAllIntrospectedResources = updateAllIntrospectedResources;
        }

        /**
         * <p>
         * Returns the value of the <code>updateAllIntrospectedResources</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isUpdateAllIntrospectedResources () {
            return updateAllIntrospectedResources;
        }

        /**
         * <p>
         * Sets the <code>failFast</code> field.
         * </p>
         * 
         * @param  failFast  Indicates whether the introspection process should bail on the first error.
         */
        public void setFailFast (boolean failFast) {
            this.failFast = failFast;
        }

        /**
         * <p>
         * Returns the value of the <code>failFast</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isFailFast () {
            return failFast;
        }

        /**
         * <p>
         * Sets the <code>commitOnFailure</code> field.
         * </p>
         * 
         * @param  commitOnFailure  Indicates whether the introspection process should commit the already successful introspected items when an
         *                          item fails to introspect.
         */
        public void setCommitOnFailure (boolean commitOnFailure) {
            this.commitOnFailure = commitOnFailure;
        }

        /**
         * <p>
         * Returns the value of the <code>commitOnFailure</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isCommitOnFailure () {
            return commitOnFailure;
        }

        /**
         * <p>
         * Sets the <code>autoRollback</code> field.
         * </p>
         * 
         * @param  autoRollback  Indicates if the introspection process should rollback all changes instead of committing. Used for
         *                       "dry run" introspections. Supercedes all other "commit" settings.
         */
        public void setAutoRollback (boolean autoRollback) {
            this.autoRollback = autoRollback;
        }

        /**
         * <p>
         * Returns the value of the <code>autoRollback</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isAutoRollback () {
            return autoRollback;
        }

        /**
         * <p>
         * Sets the <code>scanForNewResourcesToAutoAdd</code> field.
         * </p>
         * 
         * @param  scanForNewResourcesToAutoAdd  Indicates whether the introspection process should scan for new resources (instead of using
         *                                       the existing cache) when auto-adding items.
         */
        public void setScanForNewResourcesToAutoAdd (boolean scanForNewResourcesToAutoAdd) {
            this.scanForNewResourcesToAutoAdd = scanForNewResourcesToAutoAdd;
        }

        /**
         * <p>
         * Returns the value of the <code>scanForNewResourcesToAutoAdd</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isScanForNewResourcesToAutoAdd () {
            return scanForNewResourcesToAutoAdd;
        }

        /**
         * <p>
         * Sets the <code>planEntries</code> field.
         * </p>
         * 
         * @param  planEntries  The list of plan entries.
         */
        public void setPlanEntries (List<DataSource.PlanEntry> planEntries) {
            this.planEntries = planEntries;
        }

        /**
         * <p>
         * Returns the value of the <code>planEntries</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public List<DataSource.PlanEntry> getPlanEntries () {
            return planEntries;
        }
    }
    
    /**
     * <p>
     * Bean object for data source module record plan entry lists. Intended to be serialized/deserialized by Jackson into/from JSON.
     * </p>
     *
     * @author Calvin Goodrich
     * @version 1.0
     */
    public static class PlanEntry {
        String resourcePath;
        String resourceType;
        String subtype;
        String action;
        
        /**
         * <p>
         * Constructor.
         * </p>
         */
        public PlanEntry() {}

        /**
         * <p>
         * Constructor. Extracts attribute values from {@link Element} object.
         * </p>
         */
        public PlanEntry (Element peNode) {
            for (Element c : peNode.getChildren()) {
                if (c.getName().equals("resourceId")) {
                    for (Element ri : c.getChildren()) {
                        if (ri.getName().equals ("resourcePath"))
                            this.resourcePath = ri.getText();
                
                        if (ri.getName().equals ("resourceType"))
                            this.resourceType = ri.getText();
            
                        if (ri.getName().equals ("subtype"))
                            this.subtype = ri.getText();
                    }
                }
    
                if (c.getName().equals ("action"))
                    this.action = c.getText();
    
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
            
                Element resourceId = new Element ("resourceId");
    
                resourceId.addContent ("\n" + indentStr2);
                resourceId.addContent (new Element ("resourcePath").setText (this.resourcePath));
    
                resourceId.addContent ("\n" + indentStr2);
                resourceId.addContent (new Element ("resourceType").setText (this.resourceType));
    
                resourceId.addContent ("\n" + indentStr2);
                resourceId.addContent (new Element ("subtype").setText (this.subtype));
                
                resourceId.addContent ("\n" + indentStr);

            result.addContent ("\n" + indentStr);
            result.addContent (resourceId);

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("action").setText (this.action));

            return result;
        }

        /**
         * <p>
         * Sets the <code>resourcePath</code> field.
         * </p>
         * 
         * @param  resourcePath  The path to the resource to introspect.
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
         * @param  resourceType  The type of the resource to introspect.
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
         * Sets the <code>subtype</code> field.
         * </p>
         * 
         * @param  subtype  The subtype of the resource to introspect.
         */
        public void setSubtype (String subtype) {
            this.subtype = subtype;
        }

        /**
         * <p>
         * Returns the value of the <code>subtype</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getSubtype () {
            return subtype;
        }

        /**
         * <p>
         * Sets the <code>action</code> field.
         * </p>
         * 
         * @param  action  The action to take when introspecting the resource. Valid values are "ADD_OR_UPDATE",
         *                 "ADD_OR_UPDATE_RECURSIVELY", and "REMOVE"
         */
        public void setAction (String action) {
            this.action = action;
        }

        /**
         * <p>
         * Returns the value of the <code>action</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getAction () {
            return action;
        }
    }
    
    /**
     * <p>
     * Bean object for data source module record data source attribute lists. Intended to be serialized/deserialized by Jackson into/from JSON.
     * </p>
     *
     * @author Calvin Goodrich
     * @version 1.0
     */
    public static class DataSourceType {
        String name;
        String type;
        List<GenericAttribute> attributes;
        
        /**
         * <p>
         * Constructor.
         * </p>
         */
        public DataSourceType() {}

        /**
         * <p>
         * Constructor. Extracts attribute values from {@link Element} object.
         * </p>
         */
        public DataSourceType (Element dstNode) {
            for (Element c : dstNode.getChildren()) {
                if (c.getName().equals ("name"))
                    this.name = c.getText();
        
                if (c.getName().equals ("type"))
                    this.type = c.getText();
    
                if (c.getName().equals ("attribute")) {
                    if (this.attributes == null)
                        this.attributes = new ArrayList<GenericAttribute>();
                    
                    this.attributes.add (new GenericAttribute (c));
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
            Element result = new Element (name);
            String indentStr = StringUtils.getIndent (indent);

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("name").setText (this.name));

            if (this.type != null && this.type.length() > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("type").setText (this.type));
            }
            
            if (this.attributes != null) {
                for (GenericAttribute ga : this.attributes) {
                    result.addContent ("\n" + indentStr);
                    result.addContent (ga.toElement ("attribute", indent + 1));
                }
            }
                        
            return result;
        }

        /**
         * <p>
         * Sets the <code>name</code> field for data source type data sources.
         * </p>
         * 
         * @param  name  The data source type name.
         */
        public void setName (String name) {
            this.name = name;
        }
    
        /**
         * <p>
         * Returns the value of the <code>dsName</code> field for data source type data sources.
         * </p>
         * 
         * @return     The value.
         */
        public String getName () {
            return name;
        }
    
        /**
         * <p>
         * Sets the <code>type</code> field for data source type data sources.
         * </p>
         * 
         * @param  type  The data source type.
         */
        public void setType (String type) {
            this.type = type;
        }
    
        /**
         * <p>
         * Returns the value of the <code>type</code> field for data source type data sources.
         * </p>
         * 
         * @return     The value.
         */
        public String getType () {
            return type;
        }
    
        /**
         * <p>
         * Sets the <code>attributes</code> field for data source type data sources.
         * </p>
         * 
         * @param  attributes  The list of attributes.
         */
        public void setAttributes (List<GenericAttribute> attributes) {
            this.attributes = attributes;
        }
    
        /**
         * <p>
         * Returns the value of the <code>attributes</code> field for data source type data sources.
         * </p>
         * 
         * @return     The value.
         */
        public List<GenericAttribute> getAttributes() {
            return attributes;
        }
    }

    /**
     * <p>
     * Bean object for data source module record attribute definition lists. Intended to be serialized/deserialized by Jackson into/from JSON.
     * </p>
     *
     * @author Calvin Goodrich
     * @version 1.0
     */
    public class AttributeDef {
        String name;
        String type;
        String updateRule;
        boolean required = false;
        String displayName;
        boolean visible = true;
    
        /**
         * <p>
         * Constructor.
         * </p>
         */
        public AttributeDef() {}
    
        /**
         * <p>
         * Constructor. Extracts attribute values from {@link Element} object.
         * </p>
         */
        public AttributeDef (Element adNode) {
            for (Element c : adNode.getChildren()) {
                if (c.getName().equals ("name"))
                    this.name = c.getText();
        
                if (c.getName().equals ("type"))
                    this.type = c.getText();
    
                if (c.getName().equals ("updateRule"))
                    this.updateRule = c.getText();
    
                if (c.getName().equals ("required"))
                    this.required = (c.getText() == null) ? true : c.getText().matches ("(?i)^(yes|true|on|1)$");
    
                if (c.getName().equals ("displayName"))
                    this.displayName = c.getText();
    
                if (c.getName().equals ("visible"))
                    this.visible = (c.getText() == null) ? true : c.getText().matches ("(?i)^(yes|true|on|1)$");
    
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
    
            if (this.name != null && this.name.length() > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("name").setText (this.name));
            }
            
            if (this.type != null && this.type.length() > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("type").setText (this.type));
            }
            
            if (this.updateRule != null && this.updateRule.length() > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("updateRule").setText (this.updateRule));
            }
            
            // not required by XML, but Java booleans can't store null
            //
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("required").setText ("" + this.required));
    
            if (this.displayName != null && this.displayName.length() > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("displayName").setText (this.displayName));
            }
            
            // not required by XML, but Java booleans can't store null
            //
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("visible").setText ("" + this.visible));
    
            return result;
        }
    
        /**
         * <p>
         * Sets the <code>name</code> field.
         * </p>
         * 
         * @param  name  The attribute's name.
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
         * @param  type  The data type of the attribute.
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
         * Sets the <code>updateRule</code> field.
         * </p>
         * 
         * @param  updateRule  The update rule for the attribute. Valid values are "READ" or "READ_WRITE".
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
    
        /**
         * <p>
         * Sets the <code>required</code> field.
         * </p>
         * 
         * @param  required  Indicates whether the attribute is required or not.
         */
        public void setRequired (boolean required) {
            this.required = required;
        }
    
        /**
         * <p>
         * Returns the value of the <code>required</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isRequired () {
            return required;
        }
    
        /**
         * <p>
         * Sets the <code>displayName</code> field.
         * </p>
         * 
         * @param  displayName  The name of the attribute as displayed in a GUI.
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
         * Sets the <code>visible</code> field.
         * </p>
         * 
         * @param  visible  Indicates whether the attribute is displayed in a GUI.
         */
        public void setVisible (boolean visible) {
            this.visible = visible;
        }
    
        /**
         * <p>
         * Returns the value of the <code>visible</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isVisible () {
            return visible;
        }
    }
}

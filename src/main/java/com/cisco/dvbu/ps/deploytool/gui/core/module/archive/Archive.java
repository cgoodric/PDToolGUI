package com.cisco.dvbu.ps.deploytool.gui.core.module.archive;

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
 * Bean object for archive module archive records. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 *
 * @author Calvin Goodrich
 * @version 1.0
 */
public class Archive {
    
//    private static final Logger log = LoggerFactory.getLogger (Archive.class);

    public static final int RESOURCE_TYPE_EXPORT = 0;
    public static final int RESOURCE_TYPE_RELOCATE = 1;
    public static final int RESOURCE_TYPE_REBIND = 2;
    
    // attributes used by the UI
    //
    String operation;
    String origId;
    
    // module specific attributes
    //
    String id;
    String archiveMethod = "CAR";
    String archiveFileName;
    boolean includeDependencies = true;
    List<Resource> resources;
    boolean encrypt = false;
    String description;
    boolean includeaccess = true;
    boolean includeallusers = false;
    boolean includerequiredusers = false;
    boolean includecaching = true;
    boolean includejars = true;
    boolean includesourceinfo = true;
    boolean includestatistics = false;
    boolean messagesonly = false;
    boolean overridelocks = false;
    boolean overwrite = false;
    String pkgName;
    boolean printinfo = false;
    boolean printroots = false;
    boolean printusers = false;
    boolean printcontents = false;
    boolean printreferences = false;
    List<ResourceAttribute> setAttributes;
    List<User> users;

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public Archive () {}

    /**
     * <p>
     * Copy constructor. Used for making/serializing copies, where only the ID is updated.
     * 
     * WARNING!!! - This makes "shallow" copies of the List objects (meaning that changing List elements
     * in the copy will update List elements in the original.)
     * </p>
     */
    public Archive (Archive a) {
        if (a != null) {
            this.operation = a.getOperation();
            this.origId = a.getOrigId();
            this.id = a.getId();
            this.archiveFileName = a.getArchiveFileName();
            this.includeDependencies = a.isIncludeDependencies();
            this.resources = (a.getResources() != null) ? new ArrayList<Resource> (a.getResources()) : null; // do not update!!
            this.encrypt = a.isEncrypt();
            this.description = a.getDescription();
            this.includeaccess = a.isIncludeaccess();
            this.includeallusers = a.isIncludeallusers();
            this.includerequiredusers = a.isIncluderequiredusers();
            this.includecaching = a.isIncludecaching();
            this.includejars = a.isIncludejars();
            this.includesourceinfo = a.isIncludesourceinfo();
            this.includestatistics = a.isIncludestatistics();
            this.messagesonly = a.isMessagesonly();
            this.overridelocks = a.isOverridelocks();
            this.overwrite = a.isOverwrite();
            this.pkgName = a.getPkgName();
            this.printinfo = a.isPrintinfo();
            this.printroots = a.isPrintroots();
            this.printusers = a.isPrintusers();
            this.printcontents = a.isPrintcontents();
            this.printreferences = a.isPrintreferences();
            this.setAttributes = (a.getSetAttributes() != null) ? new ArrayList<ResourceAttribute> (a.getSetAttributes()) : null; // do not update!!
            this.users = (a.getUsers() != null) ? new ArrayList<User> (a.getUsers()) : null; // do not update!!
        }
    }

    /**
     * <p>
     * Constructor. Extracts attribute values from {@link Element} object.
     * </p>
     */
    public Archive (Element aNode) {
        for (Element aChild : aNode.getChildren()) {
            if (aChild.getName().equals ("id"))
                this.id = aChild.getText();

            if (aChild.getName().equals ("archiveFileName"))
                this.archiveFileName = aChild.getText();

            if (aChild.getName().equals ("archiveMethod"))
                //this.archiveMethod = aChild.getText();
                this.archiveMethod = "CAR"; // only CAR exports are supported at this time.

            if (aChild.getName().equals ("includeDependencies"))
                this.includeDependencies = (aChild.getText() == null) ? true : aChild.getText().matches ("(?i)^(yes|true|on|1)$");

            if (aChild.getName().equals ("resources")) {
                for (Element rChild : aChild.getChildren()) {
                    if (this.resources == null)
                        this.resources = new ArrayList<Resource>();
                    
                    this.resources.add (new Resource (rChild));
                }
            }

            if (aChild.getName().equals ("encrypt"))
                this.encrypt = (aChild.getText() == null) ? false : aChild.getText().matches ("(?i)^(yes|true|on|1)$");

            if (aChild.getName().equals ("description"))
                this.description = aChild.getText();

            if (aChild.getName().equals ("includeaccess"))
                this.includeaccess = (aChild.getText() == null) ? true : aChild.getText().matches ("(?i)^(yes|true|on|1)$");

            if (aChild.getName().equals ("includeallusers"))
                this.includeallusers = (aChild.getText() == null) ? false : aChild.getText().matches ("(?i)^(yes|true|on|1)$");

            if (aChild.getName().equals ("includerequiredusers"))
                this.includerequiredusers = (aChild.getText() == null) ? false : aChild.getText().matches ("(?i)^(yes|true|on|1)$");

            if (aChild.getName().equals ("includecaching"))
                this.includecaching = (aChild.getText() == null) ? true : aChild.getText().matches ("(?i)^(yes|true|on|1)$");

            if (aChild.getName().equals ("includejars"))
                this.includejars = (aChild.getText() == null) ? true : aChild.getText().matches ("(?i)^(yes|true|on|1)$");

            if (aChild.getName().equals ("includesourceinfo"))
                this.includesourceinfo = (aChild.getText() == null) ? true : aChild.getText().matches ("(?i)^(yes|true|on|1)$");

            if (aChild.getName().equals ("includestatistics"))
                this.includestatistics = (aChild.getText() == null) ? false : aChild.getText().matches ("(?i)^(yes|true|on|1)$");

            if (aChild.getName().equals ("messagesonly"))
                this.messagesonly = (aChild.getText() == null) ? false : aChild.getText().matches ("(?i)^(yes|true|on|1)$");

            if (aChild.getName().equals ("overridelocks"))
                this.overridelocks = (aChild.getText() == null) ? false : aChild.getText().matches ("(?i)^(yes|true|on|1)$");

            if (aChild.getName().equals ("overwrite"))
                this.overwrite = (aChild.getText() == null) ? false : aChild.getText().matches ("(?i)^(yes|true|on|1)$");

            if (aChild.getName().equals ("pkgName"))
                this.pkgName = aChild.getText();

            if (aChild.getName().equals ("printinfo"))
                this.printinfo = (aChild.getText() == null) ? false : aChild.getText().matches ("(?i)^(yes|true|on|1)$");

            if (aChild.getName().equals ("printroots"))
                this.printroots = (aChild.getText() == null) ? false : aChild.getText().matches ("(?i)^(yes|true|on|1)$");

            if (aChild.getName().equals ("printusers"))
                this.printusers = (aChild.getText() == null) ? false : aChild.getText().matches ("(?i)^(yes|true|on|1)$");

            if (aChild.getName().equals ("printcontents"))
                this.printcontents = (aChild.getText() == null) ? false : aChild.getText().matches ("(?i)^(yes|true|on|1)$");

            if (aChild.getName().equals ("printreferences"))
                this.printreferences = (aChild.getText() == null) ? false : aChild.getText().matches ("(?i)^(yes|true|on|1)$");

            if (aChild.getName().equals ("setAttributes")) {
                for (Element raChild : aChild.getChildren()) {
                    if (this.setAttributes == null)
                        this.setAttributes = new ArrayList<ResourceAttribute>();
                    
                    this.setAttributes.add (new ResourceAttribute (raChild));
                }
            }

            if (aChild.getName().equals ("users")) {
                for (Element uChild : aChild.getChildren()) {
                    if (this.users == null)
                        this.users = new ArrayList<User>();
                    
                    this.users.add (new User (uChild));
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
    public Element toElement (
        String name,
        int indent
    ) {
        String indentStr = StringUtils.getIndent (indent);
        String indentStr2 = StringUtils.getIndent (indent + 1);

        Element result = new Element (name);
        
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("id").setText (this.id));
            
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("archiveMethod").setText (this.archiveMethod));
            
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("archiveFileName").setText (this.archiveFileName));
            
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("includeDependencies").setText ("" + this.includeDependencies));
        
        if (this.resources != null && this.resources.size() > 0) {
            Element rNode = new Element ("resources");

            for (Resource r : this.resources) {
                Element rcNode = r.toElement ("ignored", indent + 2); // toElement() ignores name because name is dependent on resource type.

                // "export" elements are inline and don't need indenting of the closing tag.
                //
                if (r.getResourceType() != Archive.RESOURCE_TYPE_EXPORT)
                    rcNode.addContent ("\n" + indentStr2);

                rNode.addContent ("\n" + indentStr2);
                rNode.addContent (rcNode);
            }
            
            rNode.addContent ("\n" + indentStr);
            
            result.addContent ("\n" + indentStr);
            result.addContent (rNode);
        }
            
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("encrypt").setText ("" + this.encrypt));
        
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("description").setText (this.description));
            
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("includeaccess").setText ("" + this.includeaccess));
        
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("includeallusers").setText ("" + this.includeallusers));
        
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("includerequiredusers").setText ("" + this.includerequiredusers));
        
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("includecaching").setText ("" + this.includecaching));
        
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("includejars").setText ("" + this.includejars));
        
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("includesourceinfo").setText ("" + this.includesourceinfo));
        
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("includestatistics").setText ("" + this.includestatistics));
        
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("messagesonly").setText ("" + this.messagesonly));
        
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("overridelocks").setText ("" + this.overridelocks));
        
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("overwrite").setText ("" + this.overwrite));
        
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("pkgName").setText (this.pkgName));
            
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("printinfo").setText ("" + this.printinfo));
        
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("printroots").setText ("" + this.printroots));
        
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("printusers").setText ("" + this.printusers));
        
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("printcontents").setText ("" + this.printcontents));
        
        result.addContent ("\n" + indentStr);
        result.addContent (new Element ("printreferences").setText ("" + this.printreferences));
        
        if (this.setAttributes != null && this.setAttributes.size() > 0) {
            Element saNode = new Element ("setAttributes");

            for (ResourceAttribute ra : this.setAttributes) {
                saNode.addContent ("\n" + indentStr2);
                saNode.addContent (ra.toElement ("resourceAttribute", indent + 2).addContent ("\n" + indentStr2));
            }
            
            saNode.addContent ("\n" + indentStr);
            
            result.addContent ("\n" + indentStr);
            result.addContent (saNode);
        }
            
        if (this.users != null && this.users.size() > 0) {
            Element uNode = new Element ("users");

            for (User u : this.users) {
                Element ucNode = u.toElement ("ignored", indent + 2); // toElement() ignores name because name is dependent on user type.
                
                // "import" nodes are inline and don't need closing tag indenting
                //
                if (u.isExport())
                    ucNode.addContent ("\n" + indentStr2);

                uNode.addContent ("\n" + indentStr2);
                uNode.addContent (ucNode);
            }
            
            uNode.addContent ("\n" + indentStr);
            
            result.addContent ("\n" + indentStr);
            result.addContent (uNode);
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
     * @param  id  The ID of the archive record.
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
     * Sets the <code>archiveMethod</code> field.
     * </p>
     * 
     * @param  archiveMethod  The archive method (currently only "CAR" is supported.)
     */
    public void setArchiveMethod (String archiveMethod) {
        this.archiveMethod = archiveMethod;
    }

    /**
     * <p>
     * Returns the value of the <code>archiveMethod</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getArchiveMethod () {
        return archiveMethod;
    }

    /**
     * <p>
     * Sets the <code>archiveFileName</code> field.
     * </p>
     * 
     * @param  archiveFileName  The path to the archive file.
     */
    public void setArchiveFileName (String archiveFileName) {
        this.archiveFileName = archiveFileName;
    }

    /**
     * <p>
     * Returns the value of the <code>archiveFileName</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getArchiveFileName () {
        return archiveFileName;
    }

    /**
     * <p>
     * Sets the <code>includeDependencies</code> field.
     * </p>
     * 
     * @param  includeDependencies  For exports, indicates whether to include any dependent resources.
     */
    public void setIncludeDependencies (boolean includeDependencies) {
        this.includeDependencies = includeDependencies;
    }

    /**
     * <p>
     * Returns the value of the <code>includeDependencies</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isIncludeDependencies () {
        return includeDependencies;
    }

    /**
     * <p>
     * Sets the <code>resources</code> field.
     * </p>
     * 
     * @param  resources  For exports, the list of resources to export. For imports, the list of resources
     *                    to rename or rebind.
     */
    public void setResources (List<Archive.Resource> resources) {
        this.resources = resources;
    }

    /**
     * <p>
     * Returns the value of the <code>resources</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<Archive.Resource> getResources () {
        return resources;
    }

    /**
     * <p>
     * Sets the <code>encrypt</code> field.
     * </p>
     * 
     * @param  encrypt  For exports, indicates whether to encrypt the CAR file contents.
     */
    public void setEncrypt (boolean encrypt) {
        this.encrypt = encrypt;
    }

    /**
     * <p>
     * Returns the value of the <code>encrypt</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isEncrypt () {
        return encrypt;
    }

    /**
     * <p>
     * Sets the <code>description</code> field.
     * </p>
     * 
     * @param  description  For exports, the description to encode in the CAR file.
     */
    public void setDescription (String description) {
        this.description = description;
    }

    /**
     * <p>
     * Returns the value of the <code>description</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getDescription () {
        return description;
    }

    /**
     * <p>
     * Sets the <code>includeaccess</code> field.
     * </p>
     *
     * @param includeaccess For imports, indicates whether to include permissions.
     */
    public void setIncludeaccess (boolean includeaccess) {
        this.includeaccess = includeaccess;
    }

    /**
     * <p>
     * Returns the value of the <code>includeaccess</code> field.
     * </p>
     *
     * @return The value.
     */
    public boolean isIncludeaccess () {
        return includeaccess;
    }

    /**
     * <p>
     * Sets the <code>includeallusers</code> field.
     * </p>
     * 
     * @param  includeallusers  For imports, indicates whether to import all user home folders.
     */
    public void setIncludeallusers (boolean includeallusers) {
        this.includeallusers = includeallusers;
    }

    /**
     * <p>
     * Returns the value of the <code>includeallusers</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isIncludeallusers () {
        return includeallusers;
    }

    /**
     * <p>
     * Sets the <code>includerequiredusers</code> field.
     * </p>
     * 
     * @param  includerequiredusers  For exports, indicates whether to include any required users.
     */
    public void setIncluderequiredusers (boolean includerequiredusers) {
        this.includerequiredusers = includerequiredusers;
    }

    /**
     * <p>
     * Returns the value of the <code>includerequiredusers</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isIncluderequiredusers () {
        return includerequiredusers;
    }

    /**
     * <p>
     * Sets the <code>includecaching</code> field.
     * </p>
     * 
     * @param  includecaching  For exports, indicates whether to include caching information.
     */
    public void setIncludecaching (boolean includecaching) {
        this.includecaching = includecaching;
    }

    /**
     * <p>
     * Returns the value of the <code>includecaching</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isIncludecaching () {
        return includecaching;
    }

    /**
     * <p>
     * Sets the <code>includeJars</code> field.
     * </p>
     * 
     * @param  includejars  For exports, indicates whether to include jar files with CJP data sources.
     */
    public void setIncludejars (boolean includejars) {
        this.includejars = includejars;
    }

    /**
     * <p>
     * Returns the value of the <code>includejars</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isIncludejars () {
        return includejars;
    }

    /**
     * <p>
     * Sets the <code>includesourceinfo</code> field.
     * </p>
     * 
     * @param  includesourceinfo  For exports, indicates whether to include data source connection and credential information.
     */
    public void setIncludesourceinfo (boolean includesourceinfo) {
        this.includesourceinfo = includesourceinfo;
    }

    /**
     * <p>
     * Returns the value of the <code>includesourceinfo</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isIncludesourceinfo () {
        return includesourceinfo;
    }

    /**
     * <p>
     * Sets the <code>includestatistics</code> field.
     * </p>
     * 
     * @param  includestatistics  For exports, indicates whether to include gathered data source statistics.
     */
    public void setIncludestatistics (boolean includestatistics) {
        this.includestatistics = includestatistics;
    }

    /**
     * <p>
     * Returns the value of the <code>includestatistics</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isIncludestatistics () {
        return includestatistics;
    }

    /**
     * <p>
     * Sets the <code>messagesonly</code> field.
     * </p>
     * 
     * @param  messagesonly  
     */
    public void setMessagesonly (boolean messagesonly) {
        this.messagesonly = messagesonly;
    }

    /**
     * <p>
     * Returns the value of the <code>messagesonly</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isMessagesonly () {
        return messagesonly;
    }

    /**
     * <p>
     * Sets the <code>overridelocks</code> field.
     * </p>
     * 
     * @param  overridelocks  For imports, indicates whether to override any conflicting locks in the destination CIS instance.
     */
    public void setOverridelocks (boolean overridelocks) {
        this.overridelocks = overridelocks;
    }

    /**
     * <p>
     * Returns the value of the <code>overridelocks</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isOverridelocks () {
        return overridelocks;
    }

    /**
     * <p>
     * Sets the <code>overwrite</code> field.
     * </p>
     * 
     * @param  overwrite  For imports, indicates whether to overwrite or preserve any existing folder structures and content.
     */
    public void setOverwrite (boolean overwrite) {
        this.overwrite = overwrite;
    }

    /**
     * <p>
     * Returns the value of the <code>overwrite</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isOverwrite () {
        return overwrite;
    }

    /**
     * <p>
     * Sets the <code>pkgName</code> field.
     * </p>
     * 
     * @param  pkgName  For exports, the name of the exported package.
     */
    public void setPkgName (String pkgName) {
        this.pkgName = pkgName;
    }

    /**
     * <p>
     * Returns the value of the <code>pkgName</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getPkgName () {
        return pkgName;
    }

    /**
     * <p>
     * Sets the <code>printinfo</code> field.
     * </p>
     * 
     * @param  printinfo  
     */
    public void setPrintinfo (boolean printinfo) {
        this.printinfo = printinfo;
    }

    /**
     * <p>
     * Returns the value of the <code>printinfo</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isPrintinfo () {
        return printinfo;
    }

    /**
     * <p>
     * Sets the <code>printroots</code> field.
     * </p>
     * 
     * @param  printroots  
     */
    public void setPrintroots (boolean printroots) {
        this.printroots = printroots;
    }

    /**
     * <p>
     * Returns the value of the <code>printroots</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isPrintroots () {
        return printroots;
    }

    /**
     * <p>
     * Sets the <code>printusers</code> field.
     * </p>
     * 
     * @param  printusers  
     */
    public void setPrintusers (boolean printusers) {
        this.printusers = printusers;
    }

    /**
     * <p>
     * Returns the value of the <code>printusers</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isPrintusers () {
        return printusers;
    }

    /**
     * <p>
     * Sets the <code>printcontents</code> field.
     * </p>
     * 
     * @param  printcontents  
     */
    public void setPrintcontents (boolean printcontents) {
        this.printcontents = printcontents;
    }

    /**
     * <p>
     * Returns the value of the <code>printcontents</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isPrintcontents () {
        return printcontents;
    }

    /**
     * <p>
     * Sets the <code>printreferences</code> field.
     * </p>
     * 
     * @param  printreferences  
     */
    public void setPrintreferences (boolean printreferences) {
        this.printreferences = printreferences;
    }

    /**
     * <p>
     * Returns the value of the <code>printreferences</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isPrintreferences () {
        return printreferences;
    }

    /**
     * <p>
     * Sets the <code>setAttributes</code> field.
     * </p>
     * 
     * @param  setAttributes  For imports, the list of resource attributes to update.
     */
    public void setSetAttributes (List<Archive.ResourceAttribute> setAttributes) {
        this.setAttributes = setAttributes;
    }

    /**
     * <p>
     * Returns the value of the <code>setAttributes</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<Archive.ResourceAttribute> getSetAttributes () {
        return setAttributes;
    }

    /**
     * <p>
     * Sets the <code>users</code> field.
     * </p>
     * 
     * @param  users  For exports, the list of groups/users to export. For imports, an indicator of whether to merge or overwrite existing users.
     */
    public void setUsers (List<Archive.User> users) {
        this.users = users;
    }

    /**
     * <p>
     * Returns the value of the <code>users</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<Archive.User> getUsers () {
        return users;
    }

    /**
     * <p>
     * Bean object for archive module resource lists in records. Intended to be serialized/deserialized by Jackson into/from JSON.
     * </p>
     * 
     * @author Calvin Goodrich
     * @version 1.0
     */
    public static class Resource {
        int resourceType;
        String oldPath;
        String newPath;
    
        /**
         * <p>
         * Constructor.
         * </p>
         */
        public Resource() {}
    
        /**
         * <p>
         * Constructor. Extracts attribute values from {@link Element} object.
         * </p>
         */
        public Resource (Element rNode) {
            if (rNode.getName().equals ("export")) {
                this.resourceType = RESOURCE_TYPE_EXPORT;
                this.oldPath = rNode.getText();
            } else {
                if (rNode.getName().equals("relocate"))
                    this.resourceType = RESOURCE_TYPE_RELOCATE;
                else
                    this.resourceType = RESOURCE_TYPE_REBIND;
                
                for (Element c : rNode.getChildren()) {
                    if (c.getName().equals ("oldPath"))
                        this.oldPath = c.getText();
                
                    if (c.getName().equals ("newPath"))
                        this.newPath = c.getText();

                }                
            }
        }

        /**
         * <p>
         * Returns the object as a JDom Element.
         * </p>
         * 
         * @param  name   The name of the element to use. (Ignored in this case as the element name is determined by the "resourceType" flag.)
         * @param  indent The number of tabs (spaces) to indent the child elements.
         * @return        The value.
         */
        public Element toElement(
            String name, // the name will be determined by the "resourceType" flag. this field will be ignored.
            int indent
        ) {
            String indentStr = StringUtils.getIndent (indent);

            Element result;
            
            switch (this.resourceType) {
                case RESOURCE_TYPE_EXPORT:
                    result = new Element ("export");
                    break;
                
                case RESOURCE_TYPE_RELOCATE:
                    result = new Element ("relocate");
                    break;
                
                default:
                    result = new Element ("rebind");
                    break;
            }
            
            if (this.resourceType == RESOURCE_TYPE_EXPORT) {
                result.setText (this.oldPath);
            } else {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("oldPath").setText (this.oldPath));
                
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("newPath").setText (this.newPath));
            }

            return result;
        }

        /**
         * <p>
         * Sets the <code>resourceType</code> field.
         * </p>
         * 
         * @param  resourceType  The type of the resource. See the RESOURCE_TYPE_* constants of {@link Archive}.
         */
        public void setResourceType (int resourceType) {
            this.resourceType = resourceType;
        }
    
        /**
         * <p>
         * Returns the value of the <code>resourceType</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public int getResourceType () {
            return resourceType;
        }
    
        /**
         * <p>
         * Sets the <code>oldPath</code> field.
         * </p>
         * 
         * @param  oldPath  The original path in CIS.
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
         * Sets the <code>newPath</code> field.
         * </p>
         * 
         * @param  newPath  The new path in CIS.
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
    }
    
    /**
     * <p>
     * Bean object for archive module record set attribute lists. Intended to be serialized/deserialized by Jackson into/from JSON.
     * </p>
     * 
     * @author Calvin Goodrich
     * @version 1.0
     */
    public static class ResourceAttribute {
        String resourcePath;
        String resourceType;
        String attribute;
        String value;
    
        public ResourceAttribute() {}
    
        /**
         * <p>
         * Constructor. Extracts attribute values from {@link Element} object.
         * </p>
         */
        public ResourceAttribute (Element raNode) {
            for (Element c : raNode.getChildren()) {
                if (c.getName().equals ("resourcePath"))
                    this.resourcePath = c.getText();
        
                if (c.getName().equals ("resourceType"))
                    this.resourceType = c.getText();
    
                if (c.getName().equals ("attribute"))
                    this.attribute = c.getText();
    
                if (c.getName().equals ("value"))
                    this.value = c.getText();
    
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
            result.addContent (new Element ("resourcePath").setText (this.resourcePath));
            
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("resourceType").setText (this.resourceType));
            
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("attribute").setText (this.attribute));
            
            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("value").setText (this.value));
            
            return result;
        }

        /**
         * <p>
         * Sets the <code>resourcePath</code> field.
         * </p>
         * 
         * @param  resourcePath  The path to the resource in CIS.
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
         * @param  resourceType  The CIS resource type ("TABLE", "DATA_SOURCE", etc.) Currently only "DATA_SOURCE"
         *                       attributes may be updated by pkg_import.
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
         * Sets the <code>attribute</code> field.
         * </p>
         * 
         * @param  attribute  The attribute to update.
         */
        public void setAttribute (String attribute) {
            this.attribute = attribute;
        }
    
        /**
         * <p>
         * Returns the value of the <code>attribute</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getAttribute () {
            return attribute;
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
        public String getValue () {
            return value;
        }
    }
    
    /**
     * <p>
     * Bean object for archive module record user management. Intended to be serialized/deserialized by Jackson into/from JSON.
     * </p>
     * 
     * @author Calvin Goodrich
     * @version 1.0
     */
    public static class User {
        boolean export = false;
        String domain = "composite";
        String user;
        String group;
        boolean overwrite = false;
    
        public User() {}
    
        /**
         * <p>
         * Constructor. Extracts attribute values from {@link Element} object.
         * </p>
         */
        public User (Element uNode) {
            if (uNode.getName().equals ("export")) {
                this.export = true;

                for (Element c : uNode.getChildren()) {
                    if (c.getName().equals ("domain"))
                        this.domain = c.getText();
                
                    if (c.getName().equals ("user"))
                        this.user = c.getText();
                
                    if (c.getName().equals ("group"))
                        this.group = c.getText();
                                                    
                }
            }
    
            if (uNode.getName().equals ("import")) {
                this.export = false;
                this.overwrite = (uNode.getText() == null) ? false : uNode.getText().equals ("overwrite");
            }
        }

        /**
         * <p>
         * Returns the object as a JDom Element.
         * </p>
         * 
         * @param  name   The name of the element to use. (Ignored in this case as the element name is determined by the "export" flag.)
         * @param  indent The number of tabs (spaces) to indent the child elements.
         * @return        The value.
         */
        public Element toElement(
            String name, // the name will be determined by the "export" flag. this field will be ignored.
            int indent
        ) {
            String indentStr = StringUtils.getIndent (indent);

            Element result;
            
            if (this.export) {
                result = new Element ("export");
                
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("domain").setText (this.domain));
                
                if (this.group != null && this.group.length() > 0) {
                    result.addContent ("\n" + indentStr);
                    result.addContent (new Element ("group").setText (this.group));
                }
                
                if (this.user != null && this.user.length() > 0) {
                    result.addContent ("\n" + indentStr);
                    result.addContent (new Element ("user").setText (this.user));
                }
            } else {
                result = new Element ("import").setText ((this.overwrite) ? "overwrite" : "merge");
            }

            return result;
        }

        /**
         * <p>
         * Sets the <code>export</code> field.
         * </p>
         * 
         * @param  export  Indicates whether the record is for imports or exports.
         */
        public void setExport (boolean export) {
            this.export = export;
        }
    
        /**
         * <p>
         * Returns the value of the <code>export</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isExport () {
            return export;
        }
    
        /**
         * <p>
         * Sets the <code>domain</code> field.
         * </p>
         * 
         * @param  domain  For exports, indicates the domain of the user or group to export.
         */
        public void setDomain (String domain) {
            this.domain = (domain == null || domain.length() == 0) ? "composite" : domain;
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
         * Sets the <code>user</code> field.
         * </p>
         * 
         * @param  user  For exports, indicates the user to export.
         */
        public void setUser (String user) {
            this.user = user;
        }
    
        /**
         * <p>
         * Returns the value of the <code>user</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getUser () {
            return user;
        }
    
        /**
         * <p>
         * Sets the <code>group</code> field.
         * </p>
         * 
         * @param  group  For exports, indicates the group to export.
         */
        public void setGroup (String group) {
            this.group = group;
        }
    
        /**
         * <p>
         * Returns the value of the <code>group</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getGroup () {
            return group;
        }
    
        /**
         * <p>
         * Sets the <code>overwrite</code> field.
         * </p>
         * 
         * @param  overwrite  For imports, indicates whether to overwrite or merge users.
         */
        public void setOverwrite (boolean overwrite) {
            this.overwrite = overwrite;
        }
    
        /**
         * <p>
         * Returns the value of the <code>overwrite</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isOverwrite () {
            return overwrite;
        }
    }
}

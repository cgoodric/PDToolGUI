package com.cisco.dvbu.ps.deploytool.gui.util;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * <p>
 * Static class for constants and methods common to all DAO's.
 * </p>
 *
 * @author Calvin Goodrich
 * @version 1.0
 */
public class DAOConstants {

//    private static final Logger log = LoggerFactory.getLogger (DAOConstants.class);

    // so that the class is not instantiated
    //
    private DAOConstants () {}

    /**
     * XML namespace to use with all module XML files.
     */
    public static final String MODULES_NS = "http://www.dvbu.cisco.com/ps/deploytool/modules";

    /**
     * Add operation constant.
     */
    public static final int OPERATION_ADD = 1;

    /**
     * Edit/update operation constant.
     */
    public static final int OPERATION_EDIT = 2;

    /**
     * Copy operation constant.
     */
    public static final int OPERATION_COPY = 3;

    /**
     * Delete operation constant.
     */
    public static final int OPERATION_DELETE = 4;

    /**
     * Relative location from PDTool home for file templates and data source types and attibute modules.
     */
    public static final String TEMPLATES_REL_PATH = "gui/templates";
    
    /**
     * Relative location from PDTool home for data source types.
     */
    public static final String DS_TYPES_REL_PATH = TEMPLATES_REL_PATH + "/defaultDataSourceTypes.xml";

    /**
     * Relative location from PDTool home for data source attribute lists. Substitute &lt;TYPE&gt; with the basic data source type.
     */
    public static final String DS_ATTRIBUTES_REL_PATH = TEMPLATES_REL_PATH + "/defaultAttrs_<TYPE>.xml";

}

/**
 * Copyright (C) 2004 Composite Software, Inc. All Rights Reserved.
 */

package com.compositesw.common.repository;

import java.sql.DatabaseMetaData;

/**
 * @author kevin
 * @since kevlar
 */
public interface MetadataConstants {

    // Metadata type constants.
    short TYPE_NONE = 0;
    short TYPE_CONTAINER = 1;
    short TYPE_TABLE = 2;
    short TYPE_PROCEDURE = 3;
    short TYPE_TREE = 4;
    short TYPE_COLUMN = 5;
    short TYPE_INDEX = 6;
    short TYPE_PARAMETER = 7;
    short TYPE_DATA_SOURCE = 8;
    short TYPE_LINK = 9;
    short TYPE_DEFINITION_SET = 10;
    short TYPE_DEFINITION = 11;
    short TYPE_TRIGGER = 12;
    short TYPE_DOMAIN = 14;
    short TYPE_USER = 15;
    short TYPE_GROUP = 16;
    short TYPE_FOREIGN_KEY = 17;
    short TYPE_RELATIONSHIP = 18;
    short TYPE_MODEL = 19;
    short TYPE_POLICY = 20;
    // DEV NOTE: If you add any new types, also update the ResourceType enumeration & resource.xsd

    // Match type constants.
    short MATCH_TYPE_ANY = 32000;
    short MATCH_TYPE_CONTAINER_OR_DATA_SOURCE = 32001;
    short MATCH_TYPE_RESOURCE = 32002;
    short MATCH_TYPE_MEMBER = 32003;

    // Search subtype constants.
    short MATCH_SUBTYPE_ANY = 32100;
    short MATCH_SUBTYPE_TRANSFORM = 32101;


    // Search type constants.

    // Metadata subtype constants.
    //
    // Note: VISTA has reserved the range of subtypes from 1000 to 1999 inclusive.
    //
    // DEV NOTE: If you add any new subtypes, also update the ResourceSubType enumeration & resource.xsd
    short SUBTYPE_NONE = 0;
    short SUBTYPE_FOLDER = -10;
    short SUBTYPE_RELATIONAL = -100;
    short SUBTYPE_CATALOG = -101;
    short SUBTYPE_SCHEMA = -102;
    short SUBTYPE_DATABASE_PROCEDURE = -110;
    short SUBTYPE_EXTERNAL_SQL_PROCEDURE = -111;
    short SUBTYPE_SQL_PROCEDURE = -112;
    short SUBTYPE_CUSTOM_PROCEDURE = -114;
    short SUBTYPE_SQL_SCRIPT_PROCEDURE = -115;
    @Deprecated short SUBTYPE_BASIC_TRANSFORM_PROCEDURE = -116;
    @Deprecated short SUBTYPE_XSLT_TRANSFORM_PROCEDURE = -117;
    @Deprecated short SUBTYPE_STREAM_TRANSFORM_PROCEDURE = -118;
    short SUBTYPE_BUILT_IN_PROCEDURE = -119;
    short SUBTYPE_DATABASE_TABLE = -120;
    short SUBTYPE_SQL_TABLE = -121;
    short SUBTYPE_SQL_DEFINITION_SET = -122;
    short SUBTYPE_XS_DEFINITION_SET = -123;
    short SUBTYPE_WSDL_DEFINITION_SET = -124;
    short SUBTYPE_SYSTEM_TABLE = -125;
    short SUBTYPE_TRANSFORM_PROCEDURE = -126;
    @Deprecated short SUBTYPE_XQUERY_TRANSFORM_PROCEDURE = -127;
    short SUBTYPE_SCDL_DEFINITION_SET = -128;
    short SUBTYPE_ABSTRACT_WSDL_DEFINITION_SET = -129;
    short SUBTYPE_USER_DEFINED_FUNCTION = -128;
    short SUBTYPE_USER_DEFINED_AGGREGATE = -129;
    short SUBTYPE_NATIVE_FUNCTION = -130;
    short SUBTYPE_XQUERY_PROCEDURE = -131;
    // DEV NOTE: If you add any new subtypes, also update the ResourceSubType enumeration & resource.xsd

    // DEV NOTE: If you add any new subtypes, also update the ResourceSubType enumeration & resource.xsd
    short SUBTYPE_DATA_SOURCE_WSDL = -300;
    short SUBTYPE_SERVICE = -301;
    short SUBTYPE_PORT = -302;
    short SUBTYPE_OPERATION = -303;
    short SUBTYPE_IMPLEMENTATION = -304;
    short SUBTYPE_CONNECTOR = -305;
    short SUBTYPE_SECURITY_CONFIGURATION = -306;
    short SUBTYPE_DIRECTORY = -3901;
    short SUBTYPE_DELIMITED_FILE = -3903;
    short SUBTYPE_XML_FILE = -3904;
    short SUBTYPE_DATA_SOURCE_FILE = -3905;
    short SUBTYPE_DATA_SOURCE_XMLFILE = -3906;
    short SUBTYPE_TYPE_DEFINITION = -3907;
    short SUBTYPE_ELEMENT_DEFINITION = -3908;
    short SUBTYPE_CONSTANT_DEFINITION = -3909;
    short SUBTYPE_EXCEPTION_DEFINITION = -3910;
    short SUBTYPE_PORT_TYPE_DEFINITION = -3911;
    short SUBTYPE_BINDING_DEFINITION = -3912;
    short SUBTYPE_MESSAGE_DEFINITION = -3913;
    short SUBTYPE_SERVICE_DEFINITION = -3914;
    short SUBTYPE_DATA_SOURCE_XMLHTTP = -3915;
    short SUBTYPE_POI_EXCEL_FILE_TABLE = -3916;
    short SUBTYPE_DATA_SOURCE_REST = -3917;
    // DEV NOTE: If you add any new subtypes, also update the ResourceSubType enumeration & resource.xsd
    short SUBTYPE_COMPOSITE_WEB_SERVICE = -4000;

    short DOMAIN_CATEGORY_COMPOSITE = 1;
    short DOMAIN_CATEGORY_DYNAMIC = 2;
    short DOMAIN_CATEGORY_EXTERNAL = 3;

    // Indicates that the data source id is invalid.
    int NO_DATASOURCE_ID = 0;
    int NO_ID = 0;

    // Direction constants.
    byte DIRECTION_NONE = 0;
    byte DIRECTION_IN = 1;
    byte DIRECTION_INOUT = 2;
    byte DIRECTION_OUT = 4;
    byte DIRECTION_RETURN = 8;
    byte DIRECTION_ANY_IN = DIRECTION_IN | DIRECTION_INOUT;
    byte DIRECTION_ANY_OUT = DIRECTION_OUT | DIRECTION_INOUT | DIRECTION_RETURN;

    // Nullable constants.
    byte NULLABLE_UNKNOWN = 0;
    byte NULLABLE_TRUE = 1;
    byte NULLABLE_FALSE = 2;

    // Table type constants.
    byte TABLE_TYPE_UNKNOWN = 0;
    byte TABLE_TYPE_VIEW = 1;
    byte TABLE_TYPE_TABLE = 2;
    byte TABLE_TYPE_SYSTEM_TABLE = 3;
    byte TABLE_TYPE_GLOBAL_TEMPORARY = 4;
    byte TABLE_TYPE_LOCAL_TEMPORARY = 5;
    byte TABLE_TYPE_ALIAS = 6;
    byte TABLE_TYPE_SYNONYM = 7;

    // Limits
    int MAX_NAME_LENGTH = 255;
    int MAX_ANNOTATION_LENGTH = 65535;
    int MAX_PASSWORD_LENGTH = 64;
    int MAX_ENCRYPTED_PASSWORD_LENGTH = 255;

    // Attributes
    String ATTR_EXPLICITLY_DESIGNED = "explicitly.designed";
    //packaged view support. indicates that the SQL in the packaged query consists of a single SELECT statement
    String IS_SINGLE_SELECT = "isSingleSelect";
    
    // base table event support
    String ATTR_DELTA_BASE_TABLE_ID = "deltaBaseTableId";

    // Sort sequence constants.
    byte SORT_SEQUENCE_UNSUPPORTED = 0;
    byte SORT_SEQUENCE_ASCENDING = 1;
    byte SORT_SEQUENCE_DESCENDING = 2;

    // Attribute type constants.
    byte ATTR_TYPE_UNKNOWN = -1;
    byte ATTR_TYPE_NULL = 0;
    byte ATTR_TYPE_BOOLEAN = 1;
    byte ATTR_TYPE_BYTE = 2;
    byte ATTR_TYPE_DATE = 3;
    byte ATTR_TYPE_DOUBLE = 4;
    byte ATTR_TYPE_FLOAT = 5;
    byte ATTR_TYPE_INTEGER = 6;
    byte ATTR_TYPE_LONG = 7;
    byte ATTR_TYPE_SHORT = 8;
    byte ATTR_TYPE_STRING = 9;
    byte ATTR_TYPE_BOOLEAN_ARRAY = 21;
    byte ATTR_TYPE_BYTE_ARRAY = 22;
    byte ATTR_TYPE_DATE_ARRAY = 23;
    byte ATTR_TYPE_DOUBLE_ARRAY = 24;
    byte ATTR_TYPE_FLOAT_ARRAY = 25;
    byte ATTR_TYPE_INT_ARRAY = 26;
    byte ATTR_TYPE_LONG_ARRAY = 27;
    byte ATTR_TYPE_SHORT_ARRAY = 28;
    byte ATTR_TYPE_STRING_ARRAY = 29;
    byte ATTR_TYPE_OBJECT = 30;
    byte ATTR_TYPE_LIST = 31;
    byte ATTR_TYPE_MAP = 32;
    byte ATTR_TYPE_SET = 33;
    byte ATTR_TYPE_NAME_TYPE_PAIR = 34;
    byte ATTR_TYPE_FILE_PATH_STRING = 35;
    byte ATTR_TYPE_PATH_STRING = 36;
    byte ATTR_TYPE_PASSWORD_STRING = 37;
    byte ATTR_TYPE_FOLDER = 38;

    byte INDEX_TYPE_STATISTIC = (byte) DatabaseMetaData.tableIndexStatistic; // 0
    byte INDEX_TYPE_CLUSTERED = (byte) DatabaseMetaData.tableIndexClustered; // 1
    byte INDEX_TYPE_HASHED = (byte) DatabaseMetaData.tableIndexHashed; // 2
    byte INDEX_TYPE_OTHER = (byte) DatabaseMetaData.tableIndexOther; // 3
    byte INDEX_TYPE_UNKNOWN = 4;
    byte INDEX_TYPE_PRIMARY_KEY = 5;
    byte INDEX_TYPE_FOREIGN_KEY = 6;
    
    // lock update type
    boolean LOCK = false;
    boolean UNLOCK = true;
}

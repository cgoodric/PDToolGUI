//----------
// Constants
//----------

// interval in milliseconds for polling server for new log messages.
//
var INITIAL_LOG_POLLING_INTERVAL = 750;
var LOG_POLLING_INTERVAL = 200;

// file type constants
//
var FILE_TYPE_FOLDER = 0;
var FILE_TYPE_DEPLOY_CONFIG = 1;
var FILE_TYPE_SERVERS = 2;
var FILE_TYPE_DEPLOY_PLAN = 3;
var FILE_TYPE_MODULE_ARCHIVE = 4;
var FILE_TYPE_MODULE_DATA_SOURCE = 5;
var FILE_TYPE_MODULE_GROUP = 6;
var FILE_TYPE_MODULE_PRIVILEGE = 7;
var FILE_TYPE_MODULE_REBIND = 8;
var FILE_TYPE_MODULE_REGRESSION = 9;
var FILE_TYPE_MODULE_RESOURCE_CACHE = 10;
var FILE_TYPE_MODULE_RESOURCE = 11;
var FILE_TYPE_MODULE_SERVER_ATTRIBUTE = 12;
var FILE_TYPE_MODULE_SERVER_MANAGER = 13;
var FILE_TYPE_MODULE_TRIGGER = 14;
var FILE_TYPE_MODULE_USER = 15;
var FILE_TYPE_MODULE_VCS = 16;
var FILE_TYPE_LOGS = 17;

// file type location relative paths
//
var FILE_TYPE_PROPERTIES = new Array (
    {relativePath: "", suffix: ""},
    {relativePath: "resources/config", suffix: "properties"},
    {relativePath: "resources/modules", suffix: "xml"},
    {relativePath: "resources/plans", suffix: "dp"},
    {relativePath: "resources/modules", suffix: "xml"},
    {relativePath: "resources/modules", suffix: "xml"},
    {relativePath: "resources/modules", suffix: "xml"},
    {relativePath: "resources/modules", suffix: "xml"},
    {relativePath: "resources/modules", suffix: "xml"},
    {relativePath: "resources/modules", suffix: "xml"},
    {relativePath: "resources/modules", suffix: "xml"},
    {relativePath: "resources/modules", suffix: "xml"},
    {relativePath: "resources/modules", suffix: "xml"},
    {relativePath: "resources/modules", suffix: "xml"},
    {relativePath: "resources/modules", suffix: "xml"},
    {relativePath: "resources/modules", suffix: "xml"},
    {relativePath: "resources/modules", suffix: "xml"},
    {relativePath: "logs", suffix: ""}
);

// data source type constants
//
var DS_TYPE_GENERIC = 0;
var DS_TYPE_RELATIONAL = 1;
var DS_TYPE_INTROSPECT = 2;
var DS_TYPE_ATTRIBUTE_DEFS = 3;
var DS_TYPE_DATA_SOURCE_TYPES = 4;

// trigger condition types
//
var CONDITION_TYPE_SYSTEM_EVENT = 0;
var CONDITION_TYPE_TIMER = 1;
var CONDITION_TYPE_JMS = 2;
var CONDITION_TYPE_USER = 3;

// trigger action types
//
var ACTION_TYPE_PROCEDURE = 0;
var ACTION_TYPE_STATISTICS = 1;
var ACTION_TYPE_REINTROSPECT = 2;
var ACTION_TYPE_EMAIL = 3;

// global rights constants
//
var RIGHTS = new Array (
    {htmlid:"at", cisflag:"ACCESS_TOOLS", cislabel:"Access Tools"},
    {htmlid:"ur", cisflag:"UNLOCK_RESOURCE", cislabel:"Unlock Resource"},
    {htmlid:"rac", cisflag:"READ_ALL_CONFIG", cislabel:"Read All Config"},
    {htmlid:"mac", cisflag:"MODIFY_ALL_CONFIG", cislabel:"Modify All Config"},
    {htmlid:"rar", cisflag:"READ_ALL_RESOURCES", cislabel:"Read All Resources"},
    {htmlid:"mar", cisflag:"MODIFY_ALL_RESOURCES", cislabel:"Modify All Resources"},
    {htmlid:"ras", cisflag:"READ_ALL_STATUS", cislabel:"Read All Status"},
    {htmlid:"mas", cisflag:"MODIFY_ALL_STATUS", cislabel:"Modify All Status"},
    {htmlid:"rau", cisflag:"READ_ALL_USERS", cislabel:"Read All Users"},
    {htmlid:"mau", cisflag:"MODIFY_ALL_USERS", cislabel:"Modify All Users"}
);

// privileges constants
//
var PRIVILEGES = new Array (
    {htmlid:"r", chtmlid:"cr", ihtmlid:"nr", cisflag:"READ", cislabel:"Read"},
    {htmlid:"w", chtmlid:"cw", ihtmlid:"nw", cisflag:"WRITE", cislabel:"Write"},
    {htmlid:"s", chtmlid:"cs", ihtmlid:"ns", cisflag:"SELECT", cislabel:"Select"},
    {htmlid:"e", chtmlid:"ce", ihtmlid:"ne", cisflag:"EXECUTE", cislabel:"Execute"},
    {htmlid:"i", chtmlid:"ci", ihtmlid:"ni", cisflag:"INSERT", cislabel:"Insert"},
    {htmlid:"u", chtmlid:"cu", ihtmlid:"nu", cisflag:"UPDATE", cislabel:"Update"},
    {htmlid:"d", chtmlid:"cd", ihtmlid:"nd", cisflag:"DELETE", cislabel:"Delete"},
    {htmlid:"g", chtmlid:"cg", ihtmlid:"ng", cisflag:"GRANT", cislabel:"Grant"}
);

// resource type constants (as of 6.2.5.00.26)
//
var RESOURCE_TYPES = new Array (
    {type:"", cislabel:"(Select Type ...)", isDataSource: true},
    {type:"CONTAINER", cislabel:"Container", isDataSource: true},
    {type:"DATA_SOURCE", cislabel:"Data Source", isDataSource: true},
    {type:"DEFINITION_SET", cislabel:"Definition Set", isDataSource: true},
    {type:"MODEL", cislabel:"Model", isDataSource: false},
    {type:"POLICY", cislabel:"Policy", isDataSource: false},
    {type:"PROCEDURE", cislabel:"Procedure", isDataSource: true},
    {type:"LINK", cislabel:"Published Resource", isDataSource: true},
    {type:"RELATIONSHIP", cislabel:"Relationship", isDataSource: false},
    {type:"TABLE", cislabel:"Table", isDataSource: true},
    {type:"TREE", cislabel:"Tree", isDataSource: true},
    {type:"TRIGGER", cislabel:"Trigger", isDataSource: false}
);

// resource subtype constants (as of 6.2.5.00.26)
//
var RESOURCE_SUBTYPES = {
    "none": [
        {subtype:"", cislabel: "(Select Type ...)", isDataSource: true}
    ],
    "CONTAINER": [
        {subtype:"", cislabel: "(Select Sub-Type ...)", isDataSource: true},
        {subtype:"CATALOG_CONTAINER", cislabel:"Catalog", isDataSource: true},
        {subtype:"SCHEMA_CONTAINER", cislabel:"Schema", isDataSource: true},
        {subtype:"CONNECTOR_CONTAINER", cislabel:"JMS Connector Folder", isDataSource: false},
        {subtype:"DIRECTORY_CONTAINER", cislabel:"LDAP Folder", isDataSource: false},
        {subtype:"FOLDER_CONTAINER", cislabel:"CIS Folder", isDataSource: false},
        {subtype:"SERVICE_CONTAINER", cislabel:"Web Service Container", isDataSource: true},
        {subtype:"PORT_CONTAINER", cislabel:"Web Service Port Folder", isDataSource: true},
        {subtype:"OPERATIONS_CONTAINER", cislabel:"Web Service Operations Folder", isDataSource: true},
        {subtype:"SAP_AQ_AREA_CONTAINER", cislabel:"SAP AQ Area Folder", isDataSource: true},
        {subtype:"SAP_AQ_CONTAINER", cislabel:"SAP AQ Folder", isDataSource: true},
        {subtype:"SAP_BAPI_CONTAINER", cislabel:"SAP BAPI Folder", isDataSource: true},
        {subtype:"SAP_BAPI_APP_CONTAINER", cislabel:"SAP BAPI App Folder", isDataSource: true},
        {subtype:"SAP_IS_CONTAINER", cislabel:"SAP IS Folder", isDataSource: true},
        {subtype:"SAP_RFC_CONTAINER", cislabel:"SAP RFC Folder", isDataSource: true},
        {subtype:"SAP_RFC_DEVCLASS_CONTAINER", cislabel:"SAP RFC Dev Class Folder", isDataSource: true},
        {subtype:"SAP_RT_CONTAINER", cislabel:"SAP Table Folder", isDataSource: true},
        {subtype:"SAP_RT_0_CONTAINER", cislabel:"SAP RT 0 Folder", isDataSource: true},
        {subtype:"SAP_BW_INFOPROVIDER_FOLDER_CONTAINER", cislabel:"SAPBW InfoProvider Folder", isDataSource: true},
        {subtype:"SAP_BW_INFOPROVIDER_OBJECT_FOLDER_CONTAINER", cislabel:"SAPBW InfoProvider Object Folder", isDataSource: true},
        {subtype:"SAP_BW_HIERARCHIES_FOLDER_CONTAINER", cislabel:"SAPBW Hierarchies Folder", isDataSource: true},
        {subtype:"SIEBEL_BUSOBJ_CONTAINER", cislabel:"Siebel Business Objects Folder", isDataSource: true},
        {subtype:"SIEBEL_BUSSVC_CONTAINER", cislabel:"Siebel Business Service Folder", isDataSource: true}
    ],
    "DATA_SOURCE": [
        {subtype:"", cislabel: "None", isDataSource: true},
        {subtype:"COMPOSITE_WEB_SERVICE", cislabel: "Composite Web Service", isDataSource: true},
        {subtype:"FILE_DATA_SOURCE", cislabel: "File", isDataSource: true},
        {subtype:"RELATIONAL_DATA_SOURCE", cislabel: "Relational", isDataSource: true},
        {subtype:"REST_DATA_SOURCE", cislabel: "REST", isDataSource: true},
        {subtype:"WSDL_DATA_SOURCE", cislabel: "SOAP/WSDL", isDataSource: true},
        {subtype:"XML_FILE_DATA_SOURCE", cislabel: "XML File", isDataSource: true},
        {subtype:"XML_HTTP_DATA_SOURCE", cislabel: "XML/HTTP", isDataSource: true}
    ],
    "DEFINITION_SET": [
        {subtype:"", cislabel: "(Select Sub-Type ...)", isDataSource: false},
        {subtype:"ABSTRACT_WSDL_DEFINITION_SET", cislabel: "Abstract WSDL", isDataSource: false},
        {subtype:"SCDL_DEFINITION_SET", cislabel: "SCDL", isDataSource: false},
        {subtype:"SQL_DEFINITION_SET", cislabel: "SQL", isDataSource: false},
        {subtype:"WSDL_DEFINITION_SET", cislabel: "WSDL", isDataSource: false},
        {subtype:"XML_SCHEMA_DEFINITION_SET", cislabel: "XML Schema", isDataSource: false}
    ],
    "LINK": [
        {subtype:"", cislabel: "None", isDataSource: true}
    ],
    "MODEL": [
        {subtype:"", cislabel: "None", isDataSource: false}
    ],
    "POLICY": [
        {subtype:"", cislabel: "(Select Sub-Type ...)", isDataSource: false},
        {subtype:"CACHE_POLICY", cislabel: "Cache Policy", isDataSource: false},
        {subtype:"NONE", cislabel: "Custom Web Service Security Policy", isDataSource: false}
    ],
    "PROCEDURE": [
        {subtype:"", cislabel: "(Select Sub-Type ...)", isDataSource: true},
        {subtype:"DATABASE_PROCEDURE", cislabel: "Database Procedure", isDataSource: true},
        {subtype:"EXTERNAL_SQL_PROCEDURE", cislabel: "External SQL Procedure", isDataSource: true},
        {subtype:"JAVA_PROCEDURE", cislabel: "Custom Java Procedure", isDataSource: true},
        {subtype:"NATIVE_FUNCTION", cislabel: "Native Database Function", isDataSource: true},
        {subtype:"OPERATION_PROCEDURE", cislabel: "Web Service Operation", isDataSource: true},
        {subtype:"SQL_SCRIPT_PROCEDURE", cislabel: "SQL Script", isDataSource: false},
        {subtype:"XQUERY_PROCEDURE", cislabel: "XQuery Procedure", isDataSource: false},
        {subtype:"XSLT_PROCEDURE", cislabel: "XSLT Procedure", isDataSource: false},
        {subtype:"BASIC_TRANSFORM_PROCEDURE", cislabel: "Basic Transform", isDataSource: false},
        {subtype:"STREAM_TRANSFORM_PROCEDURE", cislabel: "Streaming Transform", isDataSource: false},
        {subtype:"TRANSFORM_PROCEDURE", cislabel: "Transformation Editor Transform", isDataSource: false},
        {subtype:"XQUERY_TRANSFORM_PROCEDURE", cislabel: "XQuery Transform", isDataSource: false},
        {subtype:"XSLT_TRANSFORM_PROCEDURE", cislabel: "XSLT Transform", isDataSource: false},
    ],
    "RELATIONSHIP": [
        {subtype:"", cislabel: "None", isDataSource: false}
    ],
    "TABLE": [
        {subtype:"", cislabel: "(Select Sub-Type ...)", isDataSource: true},
        {subtype:"SQL_TABLE", cislabel: "CIS View", isDataSource: false},
        {subtype:"SYSTEM_TABLE", cislabel: "CIS System Table", isDataSource: false},
        {subtype:"DATABASE_TABLE", cislabel: "Data Source Table", isDataSource: true},
        {subtype:"DELIMITED_FILE_TABLE", cislabel: "Delimited File", isDataSource: true},
        {subtype:"EXCEL_NON_ODBC_POI", cislabel: "Excel (Non-ODBC) Worksheet", isDataSource: true},
        {subtype:"SAP_AQ_QUERY_TABLE", cislabel: "SAP AQ Query", isDataSource: true},
        {subtype:"SAP_INFOSET_QUERY_TABLE", cislabel: "SAP Infoset Query", isDataSource: true},
        {subtype:"SAP_RFC_TABLE", cislabel: "SAP RFC", isDataSource: true},
        {subtype:"SAP_RT_TABLE", cislabel: "SAP Table", isDataSource: true},
        {subtype:"SIEBEL_BUSCOMP_TABLE", cislabel: "Siebel Business Component", isDataSource: true}
    ],
    "TREE": [
        {subtype:"", cislabel: "(Select Sub-Type ...)", isDataSource: true},
        {subtype:"XML_FILE_TREE", cislabel: "XML File", isDataSource: true}
    ],
    "TRIGGER": [
        {subtype:"", cislabel: "None", isDataSource: false}
    ]
};

// VCS resource type constants
//
var VCS_RESOURCE_TYPES = new Array (
    {type:"", cislabel:"(Select Type ...)"},
    {type:"CONTAINER", cislabel:"Container"},
    {type:"COMPOSITE DATABASE", cislabel:"Composite Database"},
    {type:"COMPOSITE WEB SERVICE", cislabel:"Composite Web Service"},
    {type:"LEGACY COMPOSITE WEB SERVICE", cislabel:"Composite Web Service (Legacy)"},
    {type:"DATA SOURCE", cislabel:"Data Source"},
    {type:"DEFINITIONS", cislabel:"Definitions"},
    {type:"FOLDER", cislabel:"Folder"},
    {type:"LINK", cislabel:"Link"},
    {type:"MODEL", cislabel:"Model"},
    {type:"POLICY", cislabel:"Policy"},
    {type:"PROCEDURE", cislabel:"Procedure"},
    {type:"RELATIONSHIP", cislabel:"Relationship"},
    {type:"TABLE", cislabel:"Table"},
    {type:"TREE", cislabel:"Tree"},
    {type:"TRIGGER", cislabel:"Trigger"}
);

// generic attribute types
//
var ATTRIBUTE_TYPES = [
    {type:"", cislabel:"(Select Type ...)"},
    {type:"BOOLEAN", cislabel:"Boolean"},
    {type:"BOOLEAN_ARRAY", cislabel:"Boolean Array"},
    {type:"BYTE", cislabel:"Byte"},
    {type:"BYTE_ARRAY", cislabel:"Byte Array"},
    {type:"DATE", cislabel:"Date"},
    {type:"DATE_ARRAY", cislabel:"Date Array"},
    {type:"DOUBLE", cislabel:"Double"},
    {type:"DOUBLE_ARRAY", cislabel:"Double Array"},
    {type:"FILE_PATH_STRING", cislabel:"File Path"},
    {type:"FLOAT", cislabel:"Float"},
    {type:"FLOAT_ARRAY", cislabel:"Float Array"},
    {type:"INT_ARRAY", cislabel:"Integer Array"},
    {type:"INTEGER", cislabel:"Integer"},
    {type:"LIST", cislabel:"List"},
    {type:"LONG", cislabel:"Long"},
    {type:"LONG_ARRAY", cislabel:"Long Array"},
    {type:"MAP", cislabel:"Map"},
    {type:"NULL", cislabel:"Null"},
    {type:"OBJECT", cislabel:"Object"},
    {type:"PASSWORD_STRING", cislabel:"Password String"},
    {type:"PATH_STRING", cislabel:"Path String"},
    {type:"SET", cislabel:"Set"},
    {type:"SHORT", cislabel:"Short"},
    {type:"SHORT_ARRAY", cislabel:"Short Array"},
    {type:"STRING", cislabel:"String"},
    {type:"STRING_ARRAY", cislabel:"String Array"},
    {type:"UNKNOWN", cislabel:"Unknown"}
];

// list of timezones, via wikipedia.org
//
var TIMEZONES = [
    {label: "(Select Timezone ...)", value: ""},
    {label: "UTC−12:00", value: "−12:00"},
    {label: "UTC−11:00", value: "−11:00"},
    {label: "UTC−10:00", value: "−10:00"},
    {label: "UTC−09:30", value: "−09:30"},
    {label: "UTC−09:00", value: "−09:00"},
    {label: "UTC−08:00", value: "−08:00"},
    {label: "UTC−07:00", value: "−07:00"},
    {label: "UTC−06:00", value: "−06:00"},
    {label: "UTC−05:00", value: "−05:00"},
    {label: "UTC−04:30", value: "−04:30"},
    {label: "UTC−04:00", value: "−04:00"},
    {label: "UTC−03:30", value: "−03:30"},
    {label: "UTC−03:00", value: "−03:00"},
    {label: "UTC−02:00", value: "−02:00"},
    {label: "UTC−01:00", value: "−01:00"},
    {label: "UTC", value: "Z"},
    {label: "UTC±00:00", value: "+00:00"},
    {label: "UTC+01:00", value: "+01:00"},
    {label: "UTC+02:00", value: "+02:00"},
    {label: "UTC+03:00", value: "+03:00"},
    {label: "UTC+03:30", value: "+03:30"},
    {label: "UTC+04:00", value: "+04:00"},
    {label: "UTC+04:30", value: "+04:30"},
    {label: "UTC+05:00", value: "+05:00"},
    {label: "UTC+05:30", value: "+05:30"},
    {label: "UTC+05:45", value: "+05:45"},
    {label: "UTC+06:00", value: "+06:00"},
    {label: "UTC+06:30", value: "+06:30"},
    {label: "UTC+07:00", value: "+07:00"},
    {label: "UTC+08:00", value: "+08:00"},
    {label: "UTC+08:45", value: "+08:45"},
    {label: "UTC+09:00", value: "+09:00"},
    {label: "UTC+09:30", value: "+09:30"},
    {label: "UTC+10:00", value: "+10:00"},
    {label: "UTC+10:30", value: "+10:30"},
    {label: "UTC+11:00", value: "+11:00"},
    {label: "UTC+11:30", value: "+11:30"},
    {label: "UTC+12:00", value: "+12:00"},
    {label: "UTC+12:45", value: "+12:45"},
    {label: "UTC+13:00", value: "+13:00"},
    {label: "UTC+14:00", value: "+14:00"}
];

// supported version control systems
//
VCS_TYPE_NULL = 0;
VCS_TYPE_SVN = 1;
VCS_TYPE_P4 = 2;
VCS_TYPE_CVS = 3;
VCS_TYPE_TFS2005 = 4;
VCS_TYPE_TFS2010 = 5;
VCS_TYPE_TFS2012 = 6;
VCS_TYPE_TFS2013 = 7;
var VCS_TYPES = [
    "(Choose VCS Type ...)",
    "svn", 
    "p4", 
    "cvs", 
    "tfs2005", 
    "tfs2010", 
    "tfs2012", 
    "tfs2013"
];


// random number generator for ajax calls (otherwise IE caches ajax call results.)
//
function myRand() {
    return Math.random() * 100000000000000000;
}

//----------------------
// Navigation management
//----------------------

// sets active top level navigation tab
//
function setActiveNavTab (tabName) {
    $("#" + tabName + " .nav_tab_left").removeClass("nav_tab_left").addClass("nav_tab_active_left");
    $("#" + tabName + " .nav_tab_center").removeClass("nav_tab_center").addClass("nav_tab_active_center");
    $("#" + tabName + " .nav_tab_right").removeClass("nav_tab_right").addClass("nav_tab_active_right");
}


//------------------
// Dialog management
//------------------

// set up the alert dialog
//
function createAlertDialog() {
    alertDialog = $("#alertDialog").dialog ({
        autoOpen: false,
        modal: true,
        width: 600,
        buttons: {
            OK: function() {
                $(this).dialog ("close");
            }
        }
    }); // #alertDialog
}

// set up the confirm dialog
//
function createConfirmDialog() {
    confirmDialog = $("#confirmDialog").dialog ({
        autoOpen: false,
        modal: true,
        width: 600
        // buttons created/updated by myConfirm function.
    });
}

// for interactions with grid and dialogs
//
var list, addEditDialog, dialogMsg, alertDialog, confirmDialog, genDialogMsg, generateDialog;

function myAlert (html, width) {
    $("#alertDialogMessage").html (html);

        if (width != null)
        alertDialog.dialog({width: width});
    else
        alertDialog.dialog({width: 600});

    alertDialog.dialog ("open");
}

function myConfirm (html, okCallbackFunction, width) {
    $("#confirmDialogMessage").html (html);

    confirmDialog.dialog (
        'option',
        'buttons',
        {
            OK: function() {
                $(this).dialog ("close");
                okCallbackFunction();
            },
            Cancel: function() {
                $(this).dialog ("close");
            }
        }
    );
    
    if (width != null)
        confirmDialog.dialog({width: width});
    else
        confirmDialog.dialog({width: 600});

    confirmDialog.dialog ("open");
}

// updates the add/edit dialog box's message <div>. can be any valid html fragment string.
//
function updateAEDialogMessage (h) {
    dialogMsg
        .html (h)
        .addClass ("ui-state-highlight");
    
    // set a timeout to animate clearing the highlight class over 1.5 seconds after half second timer expires.
    // (2 seconds total animation time.)
    //
    setTimeout (
        function() {
            dialogMsg.removeClass ("ui-state-highlight", 1500);
        }, 
        500 
    );
}

// clears all the contents of the add/edit dialog box <div>
//
function clearAEDialogMessage() {
    dialogMsg.empty();
}

// updates the generate dialog box's message <div>. can be any valid html fragment string.
//
function updateGenDialogMessage (h) {
    genDialogMsg.html (h);
}

// clears all the contents of the generate dialog box <div>
//
function clearGenDialogMessage() {
    genDialogMsg.empty();
}

// processes an add/edit AJAX call result.
//
// "useDialog" indicates whether to highlight add/edit dialog fields in error and display error messages in the add/edit dialog
// instead of using an alert dialog.
//
// opts is an object specifying particular options:
//
//     useDialog (true|false)        - processing uses the add/edit dialog
//     reloadGrid (true|false)       - whether to reload the grid when processing results (success or failure)
//     alertOnlyOnError (true|false) - Normally an alert window shows status only on failure. Setting this to false will show alerts on success also.
//     isDepPlanAdd (true|false)     - Indicates processing of a deployment add step (and needs to drop added row on failure.)
//
function processAjaxResult (data, opts) {
    var retVal = false;

    if (opts.reloadGrid)
        list.trigger("reloadGrid");

    // look to see what the result of the AJAX query was
    //
    if (data.status == "success") {
        
        // if we're using an add/edit dialog then close it.
        //
        if (opts.useDialog)
            addEditDialog.dialog ("close");

        var msg = data.message;
        if ($.type (data.messagelist) !== "undefined" && data.messagelist != null) {
            msg = "";
            for (var i = 0; i < data.messagelist.length; i++) {
                if (msg != "") msg += "<br/>\n";
                
                msg += data.messagelist[i].message;
            }
        }
        
        if ("alertOnlyOnError" in opts && ! opts.alertOnlyOnError)
            myAlert (msg);
        
        retVal = true;
    } else {
        var msg = data.message;
        
        // validation is handled on the server side, so just need to parse the
        // returned messages, set the corresponding form elements with error classes
        // and populate the dialog's message <div>
        //
        if ($.type (data.messagelist) !== "undefined" && data.messagelist != null) {
            msg = "";
            for (var i = 0; i < data.messagelist.length; i++) {
                if (msg != "") msg += "<br/>\n";
                
                $('#' + data.messagelist[i].field).addClass ("ui-state-error");
                msg += data.messagelist[i].message;
            }
        }
        
        if (opts.useDialog) {
            updateAEDialogMessage (msg);
            $("#aeOkButton").button ("enable");
        } else {
            myAlert (msg);
        }
            
        if (opts.isDepPlanAdd) {
            list.delRowData(list.getDataIDs()[list.getDataIDs().length - 1]); // delete the last row from the list
            $("#aeOkButton").button ("enable");
        }
    }
    
    return retVal;
}

// processes a generate AJAX call result.
//
// "useDialog" indicates whether to highlight add/edit dialog fields in error and display error messages in the add/edit dialog
// instead of using an alert dialog.
//
// opts is an object specifying particular options:
//
//     useDialog (true|false)        - processing uses the generate dialog
//     reloadGrid (true|false)       - whether to reload the grid when processing results (success or failure)
//     alertOnlyOnError (true|false) - Normally an alert window shows status on success or failure
//
function processGenerateResult (data, opts) {
    if (opts.reloadGrid)
        list.trigger("reloadGrid");

    // look to see what the result of the AJAX query was
    //
    if (data.status == "success") {
        
        // if we're using an add/edit dialog then close it.
        //
        if (opts.useDialog)
            generateDialog.dialog ("close");

        var msg = data.message;
        if ($.type (data.messagelist) !== "undefined" && data.messagelist != null) {
            msg = "";
            for (var i = 0; i < data.messagelist.length; i++) {
                if (msg != "") msg += "<br/>\n";
                
                msg += data.messagelist[i].message;
            }
        }
        
        if ("alertOnlyOnError" in opts && ! opts.alertOnlyOnError)
            myAlert (msg);
    } else {
        var msg = data.message;
        
        // validation is handled on the server side, so just need to parse the
        // returned messages, set the corresponding form elements with error classes
        // and populate the dialog's message <div>
        //
        if ($.type (data.messagelist) !== "undefined" && data.messagelist != null) {
            msg = "";
            for (var i = 0; i < data.messagelist.length; i++) {
                if (msg != "") msg += "<br/>\n";
                
                $('#' + data.messagelist[i].field).addClass ("ui-state-error");
                msg += data.messagelist[i].message;
            }
        }
        
        if (opts.useDialog)
            updateGenDialogMessage (msg);
        else
            if ("alertOnlyOnError" in opts && ! opts.alertOnlyOnError)
                myAlert (msg);
    }
}

// return the values of all options in a select list as an array.
//
function getSelectOptionValues (selector) {
    var options = $(selector + ' option');

    var values = $.map(options ,function (option) {
        return option.value;
    });
    
    return values;
}

// can't configure additional parameters for setSelectFromListData callback so using a global temporary variable
//
var tmpProfileSelect;
var tmpProfileDefault;
function setConfigProfileSelectData (select, defaultValue) {
    tmpProfileSelect = select;
    tmpProfileDefault = defaultValue;
    $.getJSON (
        "/file_list?fileType=" + FILE_TYPE_DEPLOY_CONFIG + "&_search=false&rows=1000&page=1&sidx=name&sord=asc&rnd=" + myRand(), // base URL
        null,                                                                                                    // argument
        setProfileSelectFromListData                                                                             // callback function to parse data
    );
}
                
function setProfileSelectFromListData (data) {
    $(tmpProfileSelect).children().remove();
    
    $(tmpProfileSelect).append ('<option value="">(Choose Profile ...)</option>');

    for (var i = 0; i < data.rows.length; i++) {
        var cell = data.rows[i].cell;
        var id = data.rows[i].id;

        $(tmpProfileSelect).append ('<option value="' + id + '">' + cell[0] + '</option>');
    }

    if (tmpProfileDefault != null)
        $(tmpProfileSelect).val (tmpProfileDefault);
    
    tmpProfileDefault = null;
}

var tmpServerSelect;
var tmpServerDefault;
function setServerSelectData (select, defaultValue) {
    tmpServerSelect = select;
    tmpServerDefault = defaultValue;
    $.getJSON (
        "/server_list?_search=false&rows=1000&page=1&sidx=sid&sord=asc&rnd=" + myRand(), // base URL
        null,                                                            // argument
        setServerSelectFromListData                                      // callback function to parse data
    );
}
                
function setServerSelectFromListData (data) {
    $(tmpServerSelect).children().remove();
    
    $(tmpServerSelect).append ('<option value="">(Choose Server ...)</option>');
    for (var i = 0; i < data.rows.length; i++) {
        var cell = data.rows[i].cell;
        var id = data.rows[i].id;
        
        $(tmpServerSelect).append ('<option value="' + id + '">' + cell[0] + '</option>');
    }
    
    if (tmpServerDefault != null)
        $(tmpServerSelect).val (tmpServerDefault);
        
    tmpServerDefault = null;
}

var tmpScheduleIdSelect;
var tmpScheduleIdDefault;
function setScheduleIdSelectData (select, path,  defaultValue) {
    tmpScheduleIdSelect = select;
    tmpScheduleIdDefault = defaultValue;
    $.getJSON (
        "/trigger_module/" + encodeURIComponent (path) + "?_search=false&rows=1000&page=1&sidx=id&sord=asc&rnd=" + myRand(), // base URL
        null,                                                               // argument
        setScheduleIdSelectFromListData                                     // callback function to parse data
    );
}
                
function setScheduleIdSelectFromListData (data) {
    $(tmpScheduleIdSelect).children().remove();
    $(tmpScheduleIdSelect).append ('<option value="">(Select Schedule ID ...)</option>');

    for (var i = 0; i < data.rows.length; i++) {
        var cell = data.rows[i].cell;
        var id = data.rows[i].id;
        
        if (cell[1] == "Schedule")
            $(tmpScheduleIdSelect).append ('<option value="' + id + '">' + cell[0] + '</option>');
    }
    
    if (tmpScheduleIdDefault != null)
        $(tmpScheduleIdSelect).val (tmpScheduleIdDefault);
        
    tmpScheduleIdDefault = null;
}

var tmpVCSIdSelect;
var tmpVCSIdDefault;
function setVCSIdSelectData (select, path,  defaultValue) {
    tmpVCSIdSelect = select;
    tmpVCSIdDefault = defaultValue;
    $.getJSON (
        "/vcs_module/" + encodeURIComponent (path) + "?_search=false&rows=1000&page=1&sidx=id&sord=asc&rnd=" + myRand(), // base URL
        null,                                                               // argument
        setVCSIdSelectFromListData                                          // callback function to parse data
    );
}
                
function setVCSIdSelectFromListData (data) {
    $(tmpVCSIdSelect).children().remove();
    $(tmpVCSIdSelect).append ('<option value="">(Select VCS Server ID ...)</option>');

    for (var i = 0; i < data.rows.length; i++) {
        var cell = data.rows[i].cell;
        var id = data.rows[i].id;
        
        if (cell[1] == "Connection")
            $(tmpVCSIdSelect).append ('<option value="' + id + '">' + cell[0] + '</option>');
    }
    
    if (tmpVCSIdDefault != null)
        $(tmpVCSIdSelect).val (tmpVCSIdDefault);
        
    tmpVCSIdDefault = null;
}

var tmpDstSelect;
var tmpDstDefault;
function setDataSourceTypeSelectData (select, defaultValue) {
    tmpDstSelect = select;
    tmpDstDefault = defaultValue;
    $.getJSON (
        "/data_source_module?rnd=" + myRand(), // base URL
        null,                                  // argument
        setDataSourceTypeSelectFromListData    // callback function to parse data
    );
}
                
function setDataSourceTypeSelectFromListData (data) {
    $(tmpDstSelect).children().remove();
    
    $(tmpDstSelect).append ('<option value="">(Choose Data Source Type ...)</option>');
    for (var i = 0; i < data.dataSourceTypes.length; i++) {
        var type = data.dataSourceTypes[i].name;
        
        $(tmpDstSelect).append ('<option value="' + type + '">' + type + '</option>');
    }
    
    if (tmpDstDefault != null)
        $(tmpDstSelect).val (tmpDstDefault);
        
    tmpDstDefault = null;
}

function setResourceTypeSelectData (
    select,
    isDataSource
) {
    $(select).children().remove();

    for (var i = 0; i < RESOURCE_TYPES.length; i++) {
        if (! isDataSource || RESOURCE_TYPES[i].isDataSource) {
            $(select).append ('<option value="' + RESOURCE_TYPES[i].type + '">' + RESOURCE_TYPES[i].cislabel + '</option>');
        }
    }
}

function setResourceSubTypeSelectData (
    select,
    resType,
    isDataSource
) {
    var subtypes = RESOURCE_SUBTYPES[resType];

    $(select).children().remove();

    if (subtypes != null) {
        for (var i = 0; i < subtypes.length; i++) {
            if (! isDataSource || subtypes[i].isDataSource) {
                $(select).append ('<option value="' + subtypes[i].subtype + '">' + subtypes[i].cislabel + '</option>');
            }
        }
    }
}

function setVcsResourceTypeSelectData (select) {
    for (var i = 0; i < VCS_RESOURCE_TYPES.length; i++) {
        $(select).append ('<option value="' + VCS_RESOURCE_TYPES[i].type + '">' + VCS_RESOURCE_TYPES[i].cislabel + '</option>');
    }
}

function setTimezoneSelectData (select) {
    for (var i = 0; i < TIMEZONES.length; i++) {
        var tz = TIMEZONES[i];
        $(select).append ('<option value="' + tz.value + '">' + tz.label + '</option>');
    }
}

function setAttributeTypeSelectData (select, isSimple) {
    for (var i = 0; i < ATTRIBUTE_TYPES.length; i++) {
        if (isSimple && (ATTRIBUTE_TYPES[i].type.match(/_ARRAY/) || ATTRIBUTE_TYPES[i].type == "LIST" || ATTRIBUTE_TYPES[i].type == "MAP"))
            continue;

        $(select).append ('<option value="' + ATTRIBUTE_TYPES[i].type + '">' + ATTRIBUTE_TYPES[i].cislabel + '</option>');
    }
}


var tmpServerInput;
function setServerAutoCompleteData (inputText) {
    tmpServerInput = inputText;
    $.getJSON (
        "/server_list?_search=false&rows=1000&page=1&sidx=sid&sord=asc&rnd=" + myRand(), // base URL
        null,                                                                            // argument
        setServerAutoCompleteFromListData                                                // callback function to parse data
    );
}
                
function setServerAutoCompleteFromListData (data) {
    var acSource = new Array();
    for (var i = 0; i < data.rows.length; i++) {
        acSource.push (data.rows[i].id);
    }
    $(tmpServerInput).autocomplete ("option", "source", acSource);
    
    tmpServerInput = null;
}

var tmpParamInput;
function setParamAutoCompleteData (inputText, fileType) {
    tmpParamInput = inputText;
    $.getJSON (
        "/file_list?fileType=" + fileType + "&_search=false&rows=10000&page=1&sidx=name&sord=asc&rnd=" + myRand(), // base URL
        null,                                                                               // argument
        setParamAutoCompleteFromListData                                                    // callback function to parse data
    );
}
                
function setParamAutoCompleteFromListData (data) {
    var acSource = new Array();
    for (var i = 0; i < data.rows.length; i++) {
        var path = data.rows[i].id.replace (/.*\/resources\/modules/, "$MODULE_HOME");
        acSource.push (path);
    }
    $(tmpParamInput).autocomplete ("option", "source", acSource);
    
    tmpParamInput = null;
}

function setSecurityUserAutoCompleteData (modulePath) {
    $.getJSON (
        "/regression_module/" + encodeURIComponent(modulePath) + "?_search=true&rows=10000&page=1&sidx=id&sord=asc&searchField=type&searchString=Security+User&searchOper=eq&filters=&rnd=" + myRand(), // base URL
        null,                                                                               // argument
        setSecurityUserAutoCompleteFromListData                                             // callback function to parse data
    );
}

var securityUserACSource = new Array();
function setSecurityUserAutoCompleteFromListData (data) {
    securityUserACSource = new Array();
    for (var i = 0; i < data.rows.length; i++) {
        securityUserACSource.push (data.rows[i].id);
    }
}

function setSecurityQueryAutoCompleteData (modulePath) {
    $.getJSON (
        "/regression_module/" + encodeURIComponent(modulePath) + "?_search=true&rows=10000&page=1&sidx=id&sord=asc&searchField=type&searchString=Security+Query&searchOper=eq&filters=&rnd=" + myRand(), // base URL
        null,                                                                               // argument
        setSecurityQueryAutoCompleteFromListData                                            // callback function to parse data
    );
}

var securityQueryACSource = new Array();
function setSecurityQueryAutoCompleteFromListData (data) {
    securityQueryACSource = new Array();
    for (var i = 0; i < data.rows.length; i++) {
        securityQueryACSource.push (data.rows[i].id);
    }
}


// function to add messages to message log as a single line of text. function will HTML encode special characters and
// highlight text messages in various colors.
//
function addMessageToLog (message, scrollToBottom) {

    // html encode content
    //
    message = message.replace ("&", "&amp;");
    message = message.replace ("<", "&lt;");
    message = message.replace (">", "&gt;");
    message = message.replace (/[ \t]+$/, "");

    // highlight certain text
    //
    message = message.replace ("ERROR", "<span class='syntax_highlight_error'>ERROR</span>");
    message = message.replace ("SUCCESSFUL SCRIPT COMPLETION", "<span class='syntax_highlight_success'>SUCCESSFUL SCRIPT COMPLETION</span>");
    // add additional text highlighting code here
    
    /* // the log dialog height CSS attribute doesn't match the scroll height + scroll top when scrolled to the bottom
    var height = $("#logDialog").css("height");
    var origScrollHeight = $("#logDialog")[0].scrollHeight;
    var origScrollTop = $("#logDialog").scrollTop();
    var origLCScrollTop = $("#logContent").scrollTop();
    var wasAtBottom = ($("#logDialog").prop ("scrollHeight") == $("#logDialog").scrollTop());
    alert ("height = " + height + ", osh = " + origScrollHeight + ", ost = " + origScrollTop + ", olcst = " + origLCScrollTop + ", wab = " + wasAtBottom);
    */
    
    //$("#logDialog").dialog ("open");
    //$("#logContent").append ('<div>' + message + '</div>');
    $("#logContent").append (message + "\n");
    
    // if the <div> was previously scrolled to the bottom, scroll to the new bottom
    //
    //if (wasAtBottom)
    if (scrollToBottom)
        $("#logDialog").scrollTop($("#logDialog").prop ("scrollHeight"));
}

// collapsible <div> panels. these display a few lines of text with a link that expands the div to the full content.
//
// The height of the content block when it's not expanded
//
var adjustheight = 19;
// The "more" link text
//
var moreText = "more";
// The "less" link text
//
var lessText = "less";

function setCollapsable() {
    if ($(".collapsable").length > 0) {
        // Sets the .colapsible-content div to the specified height and hides any content that overflows
        //
        $(".collapsable-content").css('height', adjustheight).css('overflow', 'hidden');

        // The section added to the bottom of the "colapsible" div
        //
        $(".collapsable").append('<div class="continued">&hellip;</div><a href="#" class="adjust">' + moreText + '</a>');

        $(".adjust")
            .data ('isOpen', 0)
            .click(
                function() {
                    if ($(this).data ('isOpen') == 0) {
                        $(this).parents ("div:first").find(".collapsable-content").css('height', 'auto').css('overflow', 'visible');
                        $(this).parents ("div:first").find("div.continued").css('display', 'none');
                        $(this).text (lessText);
                        $(this).data ('isOpen', 1);
                    } else {
                        $(this).parents("div:first").find(".collapsable-content").css('height', adjustheight).css('overflow', 'hidden');
                        $(this).parents("div:first").find("div.continued").css('display', 'block');
                        $(this).text(moreText);
                        $(this).data ('isOpen', 0);
                    }
                }
            );
    }
}

function setTooltips (tooltips) {
    if (tooltips !== undefined && tooltips != null) {
        for (var selector in tooltips) {
            $(selector).attr ("title", tooltips[selector]);
        }
    }
    
    $(document).tooltip({
        content: function () {
            return $(this).prop('title');
        }
    });
}

//---------------------
// Attribute management
//---------------------

// get the data source's list of attributes then populate the attribute value fields
//
var dsTypeAttrs = null;
function setDSAttributes (dsTypeName, gaList) {
    var ajaxOpts = {  
        type: "GET",
        contentType: 'application/json',
        url: "/data_source_module?dsTypeName=" + encodeURIComponent(dsTypeName) + "&rnd=" + myRand(),
        dataType: "json",
        success: function (data) {
            dsTypeAttrs = new Array();
            
            if (data != null && "attributeDefs" in data && data.attributeDefs != null) {
                var tmpAttrs = data.attributeDefs;
                for (var i = 0; i < tmpAttrs.length; i++) {
                    var tmpAttr = tmpAttrs[i];
                    
                    // there are a number of reasons we would not want to display an attribute in the list we're generating:
                    //
                    if (
                        ! ("name" in tmpAttr) || tmpAttr.name == null || tmpAttr.name.length == 0 ||                       // name is null
                        ! ("updateRule" in tmpAttr) || tmpAttr.updateRule == null || tmpAttr.updateRule != "READ_WRITE" //|| // update rule indicates not writeable
                        //! ("visible" in tmpAttr) || ! tmpAttr.visible == null || ! tmpAttr.visible                         // not displayed in Studio
                    ) continue;
                    
                    dsTypeAttrs.push (tmpAttr);
                }
            }

            // if the type is currently set to generic, update the dialog fields to reflect 
            // relational or generic data source types
            //
            if ($('#type input:radio:checked').val() == DS_TYPE_GENERIC ||
                $('#type input:radio:checked').val() == DS_TYPE_RELATIONAL) 
            {
                var newType = (isRelational()) ? DS_TYPE_RELATIONAL : DS_TYPE_GENERIC;
                $('#type_' + newType).prop ("checked", "checked");
                $("#type").buttonset("refresh");
                setDSFields();
            }

            setGenericAttributes (gaList);
        },
        // this should only happen when things go seriously wrong on the server
        //
        error: function (jqXHR, textStatus, errorThrown) {
            console.log ("Failed to get data source attributes for data source type '" + dsTypeName + "': textStatus = " + textStatus + "<br>errorThrown = " + errorThrown);
            dsTypeAttrs = null;
            
            if (gaList != null)
                setGenericAttributes (gaList);
        }
        //,async: false
    }

    $.ajax(ajaxOpts);
}

// searches the currently loaded set of data source attributes for a specific
// attribute object. if the input attribute name is null or a set of data source
// attributes has not been loaded, then null is returned.
//
function getDSAttribute (dsAttrName) {
    dsAttr = null;
    
    if (dsAttrName == null || dsTypeAttrs == null)
        return dsAttr;
    
    for (var i = 0; i < dsTypeAttrs.length; i++) {
        if (dsTypeAttrs[i].name != null && dsTypeAttrs[i].name == dsAttrName) {
            dsAttr = dsTypeAttrs[i];
            break;
        }
    }
    
    return dsAttr;
}

// determines if the currently loaded set of data source attributes represents
// a relational data source. if no data source attributes have been loaded,
// "false" is returned.
//
function isRelational() {
    if (getDSAttribute ("connValidateQuery") != null && getDSAttribute ("dsn") == null) {
        return true;
    } else {
        return false;
    }
}

// hides or shows the appropriate data source inputs based on the data source type radio button setting.
//
function setDSFields() {
    var selectedVal = $("#type input:radio:checked").val();
    $('.generic_input, .relational_input, .introspect_input').hide();
    
    // show appropriate data source type inputs, if needed.
    //
    if (selectedVal == DS_TYPE_GENERIC) {
        $('.generic_input').show();
    } else if (selectedVal == DS_TYPE_RELATIONAL) {
        $('.relational_input').show();
    } else if (selectedVal == DS_TYPE_INTROSPECT) {
        $('.introspect_input').show();
    }

/*
    // set the size of the dialog
    //
    if (selectedVal == DS_TYPE_INTROSPECT) {
        $('.introspect_input').show();
        $("#addEditDialog").dialog({width: 950});
    } else {
        $("#addEditDialog").dialog({width: $("#attributes_list").width() + 100});
    }
*/
}

// populate the generic attributes
//
function setGenericAttributes (gaList) {

    // ditch any existing children
    //
    $(".attributes_row").remove();
    
    if (gaList != null && gaList.length > 0) {
        for (var i = 0; i < gaList.length; i++) {
            var ga = gaList[i];

            if (ga.name != null && ga.name.length > 0) {
                var gaDef = getDSAttribute (ga.name);

                addAttributesRow();

                var attrRowId = attributesRowCount - 1;

                if (gaDef == null) {
                    $('#attr_name_' + attrRowId)
                        .val(ga.name)
                        .show();

                    $('#attr_name_sel_' + attrRowId).val("custom");
                    $('#attr_type_' + attrRowId)
                        .val(ga.type)
                        .show();
                    $('#attr_type_span_' + attrRowId).hide();
                } else {
                    $('#attr_name_' + attrRowId)
                        .hide()
                        .val(ga.name);
                    $('#attr_name_sel_' + attrRowId).val(ga.name);
                    $('#attr_type_' + attrRowId)
                        .hide()
                        .val(ga.type);
                    $('#attr_type_span_' + attrRowId)
                        .html(ga.type)
                        .show();
                }
                
                // deal with the different types of values
                //
                setValueFieldFromType ('attr_value_', attrRowId, ga.type);
                
                // set attribute value
                //
                if (ga.type.match (/_ARRAY$/)) {
                    attrArrays[attrRowId] = ga.valueArray;
                } else if (ga.type == "LIST") {
                    attrLists[attrRowId] = ga.valueList;
                } else if (ga.type == "MAP") {
                    attrMaps[attrRowId] = ga.valueMap;
                } else if (ga.type == "PASSWORD_STRING") {
                    $('#attr_value_' + attrRowId + '_pass').val (ga.value);
                } else if (ga.type == "BOOLEAN") {
                    $('#attr_value_' + attrRowId + '_bool_' + ga.value).prop ("checked", "checked");
                    $('#attr_value_' + attrRowId + '_bool').buttonset("refresh");
                } else {
                    $('#attr_value_' + attrRowId + '_text').val (ga.value);
                }
            }
        }
    } else {
        // add a blank row if there are no attributes elements.
        //
        addAttributesRow();
    }
}

// creates a row containing the form elements for an attribute entry
//
var attributesRowCount = 0;
function addAttributesRow() {
    // add the html code before the "add new attribute" button container row.
    //
    $('#attributes_add_row').before (function() {
         return '<tr id="attributes_row_' + attributesRowCount + '" class="attributes_row dynamic_row">\n' +
                '    <td>\n' +
                '        <select name="attr_name_sel_' + attributesRowCount + '" id="attr_name_sel_' + attributesRowCount + '" class="dynamic_row_value"></select>\n' +
                '        <input type="text" name="attr_name_' + attributesRowCount + '" id="attr_name_' + attributesRowCount + '" class="text ui-widget-content ui-corner-all dynamic_row_value" />\n' +
                '    </td>\n' +
                '    <td>\n' +
                '        <select name="attr_type_' + attributesRowCount + '" id="attr_type_' + attributesRowCount + '" class="dynamic_row_value"></select>\n' +
                '        <span name="attr_type_span_' + attributesRowCount + '" id="attr_type_span_' + attributesRowCount + '" class="dynamic_row_value">(Choose&nbsp;Attribute&nbsp;...)</span>\n' +
                '    </td>\n' +
                '    <td>\n' +
                '        <div id="attr_value_' + attributesRowCount + '_set" title="Set Value" class="dynamic_row_value" >Set Value</div>\n' +
                '        <input type="password" name="attr_value_' + attributesRowCount + '_pass" id="attr_value_' + attributesRowCount + '_pass" class="text ui-widget-content ui-corner-all dynamic_row_value" />\n' +
                '        <input type="text" name="attr_value_' + attributesRowCount + '_text" id="attr_value_' + attributesRowCount + '_text" class="text ui-widget-content ui-corner-all dynamic_row_value" />\n' +
                '        <div id="attr_value_' + attributesRowCount + '_bool">\n' +
                '            <input type="radio" name="attr_value_' + attributesRowCount + '_bool" id="attr_value_' + attributesRowCount + '_bool_true" value="true" /><label for="attr_value_' + attributesRowCount + '_bool_true">True</label>\n' +
                '            <input type="radio" name="attr_value_' + attributesRowCount + '_bool" id="attr_value_' + attributesRowCount + '_bool_false" value="false" /><label for="attr_value_' + attributesRowCount + '_bool_false">False</label>\n' +
                '        </div>\n' +
                '    </td>\n' +
                '    <td><div id="attributes_row_' + attributesRowCount + '_del" title="Delete Attribute" >Delete Attribute</div></td>\n' +
                '</tr>\n';
    });

    if (dsTypeAttrs == null) {
        $("#attr_name_sel_" + attributesRowCount).hide();
        $("#attr_type_span_" + attributesRowCount).hide();
    } else {
        setAttributeNameSelectData ('#attr_name_sel_' + attributesRowCount);
        $("#attr_name_" + attributesRowCount).hide();
        $("#attr_type_" + attributesRowCount).hide();
    }
    
    // set an event handler on the name select list
    //
    $("#attr_name_sel_" + attributesRowCount)
        .data ('rownum', attributesRowCount)
        .change (function() {
            var attrRowId = $(this).data ('rownum');
            var attrName = $(this).val();
            var attrDef = getDSAttribute (attrName);
            var attrType = (attrDef != null) 
                                ? attrDef.type 
                                : (attrName == "")
                                    ? "(Choose&nbsp;Attribute&nbsp;...)"
                                    : $('#attr_type_' + attrRowId).val();
            
            // set the attribute name text field to the select list's value
            //
            
            // if this is a custom field, show the text field. otherwise hide it.
            //
            if ($(this).val() == "custom") {
                $('#attr_name_' + attrRowId)
                    .val ("")
                    .show();

                $('#attr_type_' + attrRowId).show();
                $('#attr_type_span_' + attrRowId).hide();
            } else {
                $('#attr_name_' + attrRowId).val (attrName);
                
                $('#attr_name_' + attrRowId).hide();
                $('#attr_type_' + attrRowId)
                    .hide()
                    .val(attrType);
                $('#attr_type_span_' + attrRowId)
                    .show()
                    .html(attrType);
            }

            // show the appropriate value field for the type
            //
            setValueFieldFromType ('attr_value_', attrRowId, attrType);

            // finally adjust the width of the dialog so everything fits on the screen.
            //
            //$("#addEditDialog").dialog({width: $("#attributes_list").width() + 100});
        });
    
    // set the attribute type values for the attribute type select list
    //
    setAttributeTypeSelectData ("#attr_type_" + attributesRowCount, false);
    
    // set an event handler on the type select list so that complex value types are handled properly
    //
    $("#attr_type_" + attributesRowCount)
        .data ('rownum', attributesRowCount)
        .change (function() {
            var attrRowId = $(this).data ('rownum');
            
            $('#attr_type_span_' + attrRowId).html ($("#attr_type_" + attrRowId).val());
            
            // deal with the different types of values
            //
            setValueFieldFromType ('attr_value_', attrRowId, $(this).val());
        });

    // add a click event handler to the set value button to set the value clicked.
    //
    $("#attr_value_" + attributesRowCount + "_set")
        .button({
            icons: {
                primary: "ui-icon-pencil"
            },
            text: false
        })
        .data ('rownum', attributesRowCount) // attach the row number as a bit of data that the click handler can reference.
        .click(function (e) {
            e.preventDefault(); // do nothing and prevent the default click behavior.
        })
        .hide(); // hide the button initially
        
    // hide the password field initially.
    //
    $("#attr_value_" + attributesRowCount + "_pass").hide();


    // hide the boolean switch initially.
    //
    $("#attr_value_" + attributesRowCount + "_bool")
        .buttonset()
        .hide();


    // add a click event handler to the delete button to delete the attribute when clicked.
    //
    $("#attributes_row_" + attributesRowCount + "_del")
        .button({
            icons: {
                primary: "ui-icon-trash"
            },
            text: false
        })
        .data ('rownum', attributesRowCount) // attach the row number as a bit of data that the click handler can reference.
        .click(function (e) {
            e.preventDefault();
            $('#attributes_row_' + $(this).data ('rownum')).remove();
            if ($('.attributes_row').length == 0) {
                addAttributesRow();
            }
        });
        
    attrArrays[attributesRowCount] = null;
    attrLists[attributesRowCount] = null;
    attrMaps[attributesRowCount] = null;

    attributesRowCount++;
    
    return false;
} // function addAttributesRow()

function setAttributeNameSelectData (
    select,
    defaultAttribute
) {
    var options = new Array();
    
    for (var i = 0; i < dsTypeAttrs.length; i++) {
        var dsAttr = dsTypeAttrs[i];
        var displayName = (("displayName" in dsAttr) && dsAttr.displayName != null && dsAttr.displayName.length > 0) 
                              ? dsAttr.displayName 
                              : dsAttr.name;
        
        // don't display relational basic attributes (they're displayed in a different part of the add/edit dialog)
        //
        if (isRelational() && (
            dsAttr.name == "urlIP" ||
            dsAttr.name == "urlPort" ||
            dsAttr.name == "urlDatabaseName" ||
            dsAttr.name == "login" ||
            dsAttr.name == "password" ||
            dsAttr.name == "connValidateQuery"
        )) continue;
        
        options.push ({"displayName": displayName, "name": dsAttr.name});
    }
    
    options = sortByKey (options, 'displayName'); // string sort on "displayName", ascending
    
    // add a "custom" entry so that unknown or custom attributes can be handled. NB: if they don't match what's
    // available (and more importantly, writeable) in the target CIS instance, they'll be ignored.
    //
    $(select).append ('<option value="">(Choose Attribute ...)</option>');
    $(select).append ('<option value="custom">*Custom</option>');
    
    for (var i = 0; i < options.length; i++) {
        $(select).append ('<option value="' + options[i].name + '">' + options[i].displayName + '</option>');
    }
}

function setValueFieldFromType (
    fieldPrefix, 
    attrRowId, 
    type
) {
    if (type.match (/_ARRAY$/)) {
        $('#' + fieldPrefix + attrRowId + '_set')
            .show()
            .unbind("click")
            .click (function (e) {
                e.preventDefault();
                e.stopPropagation();
                setAttributeArrayDialogData ($(this).data ('rownum'));
                $('#attrArrayDialog').dialog ("open");
            });

        $('#' + fieldPrefix + attrRowId + '_pass').hide();
        $('#' + fieldPrefix + attrRowId + '_text').hide();
        $('#' + fieldPrefix + attrRowId + '_bool').hide();
        
    } else if (type == "LIST") {
        $('#' + fieldPrefix + attrRowId + '_set')
            .show()
            .unbind("click")
            .click (function (e) {
                e.preventDefault();
                e.stopPropagation();
                setAttributeListDialogData ($(this).data ('rownum'));
                $('#attrListDialog').dialog ("open");
            });

        $('#' + fieldPrefix + attrRowId + '_pass').hide();
        $('#' + fieldPrefix + attrRowId + '_text').hide();
        $('#' + fieldPrefix + attrRowId + '_bool').hide();

    } else if (type == "MAP") {
        $('#' + fieldPrefix + attrRowId + '_set')
            .show()
            .unbind("click")
            .click (function (e) {
                e.preventDefault();
                e.stopPropagation();
                setAttributeMapDialogData ($(this).data ('rownum'));
                $('#attrMapDialog').dialog ("open");
            });

        $('#' + fieldPrefix + attrRowId + '_pass').hide();
        $('#' + fieldPrefix + attrRowId + '_text').hide();
        $('#' + fieldPrefix + attrRowId + '_bool').hide();
        
    } else if (type == "PASSWORD_STRING") {
        $('#' + fieldPrefix + attrRowId + '_pass').show();

        $('#' + fieldPrefix + attrRowId + '_text').hide();
        $('#' + fieldPrefix + attrRowId + '_set').hide();
        $('#' + fieldPrefix + attrRowId + '_bool').hide();
        
    } else if (type == "BOOLEAN") {
        $('#' + fieldPrefix + attrRowId + '_bool').show();

        $('#' + fieldPrefix + attrRowId + '_text').hide();
        $('#' + fieldPrefix + attrRowId + '_pass').hide();
        $('#' + fieldPrefix + attrRowId + '_set').hide();
        
    } else { // some kind of text value
        $('#' + fieldPrefix + attrRowId + '_text').show();

        $('#' + fieldPrefix + attrRowId + '_pass').hide();
        $('#' + fieldPrefix + attrRowId + '_set').hide();
        $('#' + fieldPrefix + attrRowId + '_bool').hide();
    }
}

function setALMValueFieldFromType (
    fieldPrefix, 
    attrRowId, 
    type
) {
    if (type == "PASSWORD_STRING") {
        $('#' + fieldPrefix + attrRowId + '_pass').show();

        $('#' + fieldPrefix + attrRowId + '_text').hide();
        $('#' + fieldPrefix + attrRowId + '_bool').hide();
        
    } else if (type == "BOOLEAN") {
        $('#' + fieldPrefix + attrRowId + '_bool').show();

        $('#' + fieldPrefix + attrRowId + '_text').hide();
        $('#' + fieldPrefix + attrRowId + '_pass').hide();
    } else { // some kind of text value
        $('#' + fieldPrefix + attrRowId + '_text').show();

        $('#' + fieldPrefix + attrRowId + '_pass').hide();
        $('#' + fieldPrefix + attrRowId + '_bool').hide();
    }
    
    return false;
}

// Array dialog functions
//
var attrArrays = new Array();
function createAttributeArrayDialog (dialogId) {
    return $(dialogId).dialog ({
        autoOpen: false,
        //height: 300,
        width: 300,
        modal: true,
        buttons: [ // declaring buttons this way lets us assign ID's to the buttons so they can be interacted with later.
            {
                id: "aaOkButton",
                text: "OK",
                click: function() {
                    var arry = new Array();
                    var attrRowId = $("#aaAttrRowId").val();
                    var arrayType = $("#attrArrayType").val();
                    for (var i = 0; i < arraysRowCount; i++) {
                        
                        // make sure the row hasn't been deleted by the user
                        //
                        if ($("#arrays_row_" + i).length > 0) {
                            if (arrayType == "BOOLEAN_ARRAY") { // boolean values in return are causing the attribute's set button click handler to be cleared.
                                var boolValue = $('#array_value_' + i + '_bool input:radio:checked').val();
                                
                                if (boolValue !== undefined) {
                                    arry.push (boolValue + "");
                                }
                            } else {
                                if ($('#array_value_' + i + '_text').val().length > 0) {
                                    arry.push ($("#array_value_" + i + "_text").val());
                                }
                            }
                        }
                    }
                    
                    attrArrays[attrRowId] = arry;

                    $(this).dialog ("close");
                    
                    return false;
                }
            }, {
                id: "aaCancelButton",
                text: "Cancel",
                click: function() {
                    $(this).dialog ("close");              
                }
            }
        ]
    }); // #createAttributeArrayDialog
}

function setAttributeArrayDialogData (attrRowId) {
    var valueArray = attrArrays[attrRowId];
    var attrType = $('#attr_type_' + attrRowId).val();

    $("#aaAttrRowId").val(attrRowId);
    $("#attrArrayType").val(attrType);

    $('.arrays_row').remove();
    arraysRowCount = 0;
    
    if (valueArray != null) {
        if (valueArray.length == 0)
            addArraysRow();
            
        for (var a = 0; a < valueArray.length; a++) {
            var value = valueArray[a];
            
            addArraysRow();
            var rowid = arraysRowCount - 1;
            
            if (attrType == "BOOLEAN_ARRAY") {
                $('#array_value_' + rowid + '_bool_' + value.toLowerCase()).prop ("checked", "checked");
                $('#array_value_' + rowid + '_bool').buttonset("refresh");
            } else {
                $("#array_value_" + rowid + "_text").val (value);
            }
        }
    } else {
        addArraysRow();
    }
}

var arraysRowCount = 0;
function addArraysRow() {
    // add the html code before the "add new attribute" button container row.
    //
    $('#arrays_add_row').before (function() {
         return '<tr id="arrays_row_' + arraysRowCount + '" class="arrays_row dynamic_row">\n' +
                '    <td>\n' +
                '        <input type="text" name="array_value_' + arraysRowCount + '_text" id="array_value_' + arraysRowCount + '_text" class="text ui-widget-content ui-corner-all dynamic_row_value" />\n' +
                '        <div id="array_value_' + arraysRowCount + '_bool">\n' +
                '            <input type="radio" name="array_value_' + arraysRowCount + '_bool" id="array_value_' + arraysRowCount + '_bool_true" value="true" /><label for="array_value_' + arraysRowCount + '_bool_true">True</label>\n' +
                '            <input type="radio" name="array_value_' + arraysRowCount + '_bool" id="array_value_' + arraysRowCount + '_bool_false" value="false" /><label for="array_value_' + arraysRowCount + '_bool_false">False</label>\n' +
                '        </div>\n' +
                '    </td>\n' +
                '    <td><div id="arrays_row_' + arraysRowCount + '_del" title="Delete Value" >Delete Value</div></td>\n' +
                '</tr>\n';
    });
    
    $("#array_value_" + arraysRowCount + "_bool").buttonset();

    setALMValueFieldFromType ('array_value_', arraysRowCount, $("#attrArrayType").val().replace ("_ARRAY", ""));

    // add a click event handler to the delete button to delete the attribute when clicked.
    //
    $("#arrays_row_" + arraysRowCount + "_del")
        .button({
            icons: {
                primary: "ui-icon-trash"
            },
            text: false
        })
        .data ('rownum', arraysRowCount) // attach the row number as a bit of data that the click handler can reference.
        .click(function (e) {
            e.preventDefault();
            attrArrays[$(this).data ('rownum')] = null;
            $('#arrays_row_' + $(this).data ('rownum')).remove();
            if ($('.arrays_row').length == 0) {
                addArraysRow();
            }
        });

    arraysRowCount++;
    
    return false;
}

// List dialog functions
//
var attrLists = new Array();
function createAttributeListDialog (dialogId) {
    return $(dialogId).dialog ({
        autoOpen: false,
        //height: 300,
        width: 500,
        modal: true,
        buttons: [ // declaring buttons this way lets us assign ID's to the buttons so they can be interacted with later.
            {
                id: "alOkButton",
                text: "OK",
                click: function() {
                    var list = new Array();

                    for (var i = 0; i < listsRowCount; i++) {
                        
                        // make sure the row hasn't been deleted by the user
                        //
                        if ($("#lists_row_" + i).length > 0) {
                            var type = $("#list_type_" + i).val();
                            
                            // make sure the value type was chosen
                            //
                            if (type.length > 0) {
                                var listElement = { "type": type };
                                
                                if (type == "PASSWORD_STRING" && $('#list_value_' + i + "_pass").val().length > 0) {
                                    listElement["value"] = $("#list_value_" + i + "_pass").val();
                                } else if (type == "BOOLEAN" && $('#list_value_' + i + '_bool input:radio:checked').val() !== undefined) {
                                    listElement["value"] = $('#list_value_' + i + '_bool input:radio:checked').val();
                                } else if ($('#list_value_' + i + "_text").val().length > 0) {
                                    listElement["value"] = $("#list_value_" + i + "_text").val();
                                }

                                if (listElement.value !== undefined) {
                                    list.push (listElement);
                                }
                            }
                        }
                    }
                    
                    attrLists[$("#alAttrRowId").val()] = list;

                    $(this).dialog ("close");              

                }
            }, {
                id: "amCancelButton",
                text: "Cancel",
                click: function() {
                    $(this).dialog ("close");              
                }
            }
        ]
    }); // #createAttributeListDialog
}

function setAttributeListDialogData (attrRowId) {
    var valueList = attrLists[attrRowId];

    $("#alAttrRowId").val(attrRowId);

    $('.lists_row').remove();
    listsRowCount = 0;
    
    if (valueList != null) {
        if (valueList.length == 0)
            addListsRow();
            
        for (var i = 0; i < valueList.length; i++) {
            var item = valueList[i];
            
            addListsRow();
            var rowid = listsRowCount - 1;
            
            $("#list_type_" + rowid).val (item.type);

            setALMValueFieldFromType ('list_value_', rowid, item.type);
            if (item.type == "PASSWORD_STRING") {
                $("#list_value_" + rowid + "_pass").val (item.value);
            } else if (item.type == "BOOLEAN") {
                $('#list_value_' + rowid + '_bool_' + item.value.toLowerCase()).prop ("checked", "checked");
                $('#list_value_' + rowid + '_bool').buttonset("refresh");
            } else {
                $("#list_value_" + rowid + "_text").val (item.value);
            }
        }
    } else {
        addListsRow();
    }
}

var listsRowCount = 0;
function addListsRow() {
    // add the html code before the "add new attribute" button container row.
    //
    $('#lists_add_row').before (function() {
         return '<tr id="lists_row_' + listsRowCount + '" class="lists_row dynamic_row">\n' +
                '    <td><select name="list_type_' + listsRowCount + '" id="list_type_' + listsRowCount + '" class="dynamic_row_value"></select></td>\n' +
                '    <td>\n' +
                '        <input type="text" name="list_value_' + listsRowCount + '_text" id="list_value_' + listsRowCount + '_text" class="text ui-widget-content ui-corner-all dynamic_row_value" />\n' +
                '        <input type="password" name="list_value_' + listsRowCount + '_pass" id="list_value_' + listsRowCount + '_pass" class="text ui-widget-content ui-corner-all dynamic_row_value" />\n' +
                '        <div id="list_value_' + listsRowCount + '_bool">\n' +
                '            <input type="radio" name="list_value_' + listsRowCount + '_bool" id="list_value_' + listsRowCount + '_bool_true" value="true" /><label for="list_value_' + listsRowCount + '_bool_true">True</label>\n' +
                '            <input type="radio" name="list_value_' + listsRowCount + '_bool" id="list_value_' + listsRowCount + '_bool_false" value="false" /><label for="list_value_' + listsRowCount + '_bool_false">False</label>\n' +
                '        </div>\n' +
                '    </td>\n' +
                '    <td><div id="lists_row_' + listsRowCount + '_del" title="Delete Value" >Delete Value</div></td>\n' +
                '</tr>\n';
    });
    
    // set the attribute type values for the attribute type select list
    //
    setAttributeTypeSelectData ("#list_type_" + listsRowCount, true);
    
    // set an event handler on the type select list so that complex value types are handled properly
    //
    $("#list_type_" + listsRowCount)
        .data ('rownum', listsRowCount)
        .change (function() {
            var rowId = $(this).data ('rownum');
            
            // deal with the different types of values
            //
            setALMValueFieldFromType ('list_value_', rowId, $(this).val());
        });

    // hide password and boolean fields initially
    //
    $("#list_value_" + listsRowCount + "_pass").hide();
    $("#list_value_" + listsRowCount + "_bool")
        .buttonset()
        .hide();

    // add a click event handler to the delete button to delete the attribute when clicked.
    //
    $("#lists_row_" + listsRowCount + "_del")
        .button({
            icons: {
                primary: "ui-icon-trash"
            },
            text: false
        })
        .data ('rownum', listsRowCount) // attach the row number as a bit of data that the click handler can reference.
        .click(function (e) {
            e.preventDefault();
            $('#lists_row_' + $(this).data ('rownum')).remove();
            if ($('.lists_row').length == 0) {
                addListsRow();
            }
        });
    
    
    listsRowCount++;
    
    return false;
}

// Map dialog functions
//
var attrMaps = new Array();
function createAttributeMapDialog (dialogId) {
    return $(dialogId).dialog ({
        autoOpen: false,
        //height: 300,
        width: 750,
        modal: true,
        buttons: [ // declaring buttons this way lets us assign ID's to the buttons so they can be interacted with later.
            {
                id: "amOkButton",
                text: "OK",
                click: function() {
                    var map = new Array();

                    for (var i = 0; i < mapsRowCount; i++) {
                        
                        // make sure the row hasn't been deleted by the user and that the map entry key name is filled in
                        //
                        if ($("#maps_row_" + i).length > 0 && $('#map_key_' + i).val().length > 0) {
                            var valueType = $("#map_type_" + i).val();
                            
                            // make sure a value type was chosen
                            //
                            if (valueType.length > 0) {
                                var mapElement = { key:{type: "STRING", value: $("#map_key_" + i).val() } };

                                if (valueType == "PASSWORD_STRING" && $('#map_value_' + i + "_pass").val().length > 0) {
                                    mapElement["value"] = {
                                        type: valueType, 
                                        value: $("#map_value_" + i + "_pass").val()
                                    };
                                } else if (valueType == "BOOLEAN" && $('#map_value_' + i + '_bool input:radio:checked').val() !== undefined) {
                                    mapElement["value"] = {
                                        type: valueType, 
                                        value: $('#map_value_' + i + '_bool input:radio:checked').val()
                                    };
                                } else if ($('#map_value_' + i + "_text").val().length > 0) {
                                    mapElement["value"] = {
                                        type: valueType, 
                                        value: $("#map_value_" + i + "_text").val()
                                    };
                                }

                                if (mapElement.value !== undefined) {
                                    map.push (mapElement);
                                }
                            }
                        }
                    }
                    
                    attrMaps[$("#amAttrRowId").val()] = map;

                    $(this).dialog ("close");              
                }
            }, {
                id: "amCancelButton",
                text: "Cancel",
                click: function() {
                    $(this).dialog ("close");              
                }
            }
        ]
    }); // #createAttributeMapDialog
}

function setAttributeMapDialogData (attrRowId) {
    var valueMap = attrMaps[attrRowId];

        $("#amAttrRowId").val(attrRowId);

    $('.maps_row').remove();
    mapsRowCount = 0;
    
    if (valueMap != null) {
        if (valueMap.length == 0)
            addMapsRow();
            
        for (var e = 0; e < valueMap.length; e++) {
            var entry = valueMap[e];
            
            addMapsRow();
            var rowid = mapsRowCount - 1;
            
            $("#map_key_" + rowid).val (entry.key.value);
            $("#map_type_" + rowid).val (entry.value.type);

            setALMValueFieldFromType ('map_value_', rowid, entry.value.type);
            if (entry.value.type == "PASSWORD_STRING") {
                $("#map_value_" + rowid + "_pass").val (entry.value.value);
            } else if (entry.value.type == "BOOLEAN") {
                $('#map_value_' + rowid + '_bool_' + entry.value.value.toLowerCase()).prop ("checked", "checked");
                $('#map_value_' + rowid + '_bool').buttonset("refresh");
            } else {
                $("#map_value_" + rowid + "_text").val (entry.value.value);
            }
        }
    } else {
        addMapsRow();
    }
}

var mapsRowCount = 0;
function addMapsRow() {
    // add the html code before the "add new attribute" button container row.
    //
    $('#maps_add_row').before (function() {
         return '<tr id="maps_row_' + mapsRowCount + '" class="maps_row dynamic_row">\n' +
                '    <td><input type="text" name="map_key_' + mapsRowCount + '" id="map_key_' + mapsRowCount + '" class="text ui-widget-content ui-corner-all dynamic_row_value" /></td>\n' +
                '    <td><select name="map_type_' + mapsRowCount + '" id="map_type_' + mapsRowCount + '" class="dynamic_row_value"></select></td>\n' +
                '    <td>\n' +
                '        <input type="text" name="map_value_' + mapsRowCount + '_text" id="map_value_' + mapsRowCount + '_text" class="text ui-widget-content ui-corner-all dynamic_row_value" />\n' +
                '        <input type="password" name="map_value_' + mapsRowCount + '_pass" id="map_value_' + mapsRowCount + '_pass" class="text ui-widget-content ui-corner-all dynamic_row_value" />\n' +
                '        <div id="map_value_' + mapsRowCount + '_bool">\n' +
                '            <input type="radio" name="map_value_' + mapsRowCount + '_bool" id="map_value_' + mapsRowCount + '_bool_true" value="true" /><label for="map_value_' + mapsRowCount + '_bool_true">True</label>\n' +
                '            <input type="radio" name="map_value_' + mapsRowCount + '_bool" id="map_value_' + mapsRowCount + '_bool_false" value="false" /><label for="map_value_' + mapsRowCount + '_bool_false">False</label>\n' +
                '        </div>\n' +
                '    </td>\n' +
                '    <td><div id="maps_row_' + mapsRowCount + '_del" title="Delete Value" >Delete Value</div></td>\n' +
                '</tr>\n';
    });
    
    // set the attribute type values for the attribute type select list
    //
    setAttributeTypeSelectData ("#map_type_" + mapsRowCount, true);
    
    // set an event handler on the type select list so that complex value types are handled properly
    //
    $("#map_type_" + mapsRowCount)
        .data ('rownum', mapsRowCount)
        .change (function() {
            var rowId = $(this).data ('rownum');
            
            // deal with the different types of values
            //
            setValueFieldFromType ('map_value_', rowId, $(this).val());
        });

    // hide password and boolean fields initially
    //
    $("#map_value_" + mapsRowCount + "_pass").hide();
    $("#map_value_" + mapsRowCount + "_bool")
        .buttonset()
        .hide();

    // add a click event handler to the delete button to delete the attribute when clicked.
    //
    $("#maps_row_" + mapsRowCount + "_del")
        .button({
            icons: {
                primary: "ui-icon-trash"
            },
            text: false
        })
        .data ('rownum', mapsRowCount) // attach the row number as a bit of data that the click handler can reference.
        .click(function (e) {
            e.preventDefault();
            attrMaps[$(this).data ('rownum')] = null;
            $('#maps_row_' + $(this).data ('rownum')).remove();
            if ($('.maps_row').length == 0) {
                addMapsRow();
            }
        });

    mapsRowCount++;
    
    return false;
}


//----------------------------------------
// Generic functions
//----------------------------------------

/* srt - Sorting function to be used with Array.sort() - By: KooiInc, From: http://stackoverflow.com/questions/16648076/sort-array-on-key-value
    // usage examples
    'a,z,x,y,a,b,B,Z,a,i,j,y'.split(',').sort( srt({string:true;}) );
     //=> ,a,a,b,B,i,j,x,y,y,z,Z
    [100,7,8,2,2,0,5,1,6,5,-1].sort( srt() );
     //=> -1,0,1,2,2,5,5,6,7,8,100
    [100,7,8,2,2,0,5,1,6,5,-1].sort( srt({},true}) );
     //=> 100,8,7,6,5,5,2,2,1,0,-1
    var objarr = 
     [ {name:'bob', artist:'rudy'}
      ,{name:'Johhny', artist:'drusko'}
      ,{name:'Tiff', artist:'needell'}
      ,{name:'top', artist:'gear'}]
     .sort( srt({key:'name',string:true}, true) );
    for (var i=0;i<objarr.length;i+=1) {
      console.log(objarr[i].name);
    }
    //=> logs zeb, top, Tiff, Johnny consecutively
*/
function srt(on,descending) {
    on = (on && on.constructor === Object) ? on : {};

    return function (a,b) {

        if (on.string || on.key) {

            a = (on.key) ? a[on.key] : a;
            a = (on.string) ? String (a).toLowerCase() : a;

            b = (on.key) ? b[on.key] : b;
            b = (on.string) ? String (b).toLowerCase() : b;

            // if key is not present, move to the end 
            //
            if (on.key && (!b || !a)) {
                return (!a && !b) 
                            ? 1 
                            : (!a) ? 1 : -1;
            }
        }

        return (descending) 
                    ? ~~(on.string ? b.localeCompare(a) : a < b)
                    : ~~(on.string ? a.localeCompare(b) : a > b);
    };
}

function sortByKey(array, key) {
    return array.sort(function(a, b) {
        var x = a[key];
        var y = b[key];

        if (typeof x == "string") {
            x = x.toLowerCase(); 
            y = y.toLowerCase();
        }

        return ((x < y) ? -1 : ((x > y) ? 1 : 0));
    });
}

//----------------------------------------
// Custom widgets and associated functions
//----------------------------------------

// a custom spinner widget that helps a user choose an XML dateTime format-appropriate timezone
//
/*
$.widget ("ui.tzspinner", $.ui.spinner, {
    options: {
        // 1/2 hour
        step: 0.5,
        // hours
        page: 1,
        // set upper and lower bounds and roll over if bounds are hit
        spin: function (event, ui) {
            if (isNaN(ui.value * 1)) {
                $(this).tzspinner ("value", 0);
                return false;
            } else {
                if (ui.value > 12.0) {
                    $(this).tzspinner ("value", -12);
                    return false;
                } else if (ui.value < -12.0) {
                    $(this).tzspinner ("value", 12);
                    return false;
                }
            }
        }
    },
    _parse: function (value) {
        var newValue = value;
        
        if (isNaN(value * 1)) {
            var groups = parseTZ (value);
            
            if (groups == null || groups.length == 0) {
               return 0;
            }

            // translate to a floating point number where the integer represents the hour and the decimal the minutes
            // note: non-zero minute values get translated into 0.5 (30 minutes)
            //
            newValue = parseFloat(groups[1] + groups[2] + ((groups[3] % 60 == 0) ? ".0" : ".5"));
        }

        return newValue;
    },
    _format: function (value) {
        if (isNaN(value * 1) || value == 0) {
            return "Z";
        }

        var hour = Math.abs((value >= 0) ? Math.floor(value) : Math.ceil(value));
        var retVal = ((value >= 0) ? "+" : "-") + zpad(hour) + ":" + ((value * 10 % 10 == 0) ? "00" : "30");
        
        return retVal;
    }
}); // $.widget()
*/

// a custom spinner widget that left pads single digit numbers with a zero.
//
$.widget ("ui.zpadspinner", $.ui.spinner, {
    _parse: function (value) {
        if (isNaN(value * 1)) {
            return 0;
        }

        return value * 1;
    },
    _format: function (value) {
        return zpad (value);
    }
}); // $.widget()

// zero-pad's a single digit number to two digits, i.e.: 2 becomes "02" where as 10 stays "10".
//
function zpad (num) {
    if (Math.abs(num) < 10) {
        return ((num < 0) ? "-" : "") + "0" + Math.abs(num);
    } else {
        return "" + num;
    }
}

// parses a string looking for an XML dateTime timezone. returns null if not found.
//
function parseTZ (value) {
    var parserRegex = /([+-]?)(0?\d|1[0-2]):([0-5]\d)/; // looks for and extracts the sign, hour, and minute components of an XML dateTime timezone specification.
    return parserRegex.exec(value);
}

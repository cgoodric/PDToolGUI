// (c) 2014 Cisco and/or its affiliates. All rights reserved.

// constants
//
var ALL_RESOURCES_INDICATOR = "*";
var EXCLUDE_RESOURCES_INDICATOR = "-";

function getModuleId (methodName) {
    for (var moduleId in DEPLOYMENT_METHODS) {
        var module = DEPLOYMENT_METHODS[moduleId];
        
        if (methodName != null && methodName.length > 0 && module.methods[methodName] != null) {
            return moduleId;
        }
    }
    
    return null;
}

// sets the module values for a select list. method name is used to determine a selected module.
//
function setModuleSelectData (
    moduleSelectId,
    methodName
) {
    var selectedModuleId = null;

    $(moduleSelectId).children().remove();
    
    $(moduleSelectId).append ('<option value="">(Choose Module ...)</option>');
    for (var moduleId in DEPLOYMENT_METHODS) {
        var module = DEPLOYMENT_METHODS[moduleId];
        
        $(moduleSelectId).append ('<option value="' + moduleId + '">' + module.name + '</option>');
        
        // if a method name is specified, look to see if this is the module it refers to
        //
        if (methodName != null && methodName.length > 0 && module.methods[methodName] !== undefined) {
            selectedModuleId = moduleId;
        }
    }
    
    if (selectedModuleId != null) {
        $(moduleSelectId).val (selectedModuleId);
        return selectedModuleId;
    }
}

// figures out if we're using an overloaded method and returns the appropriate method name
//
function getOverloadedMethodName (
    methodName, 
    rowData
) {
    var moduleId = getModuleId (methodName);
    var result = methodName;
    
    if (moduleId != null && DEPLOYMENT_METHODS[moduleId].methods[methodName] !== undefined) {
        var module = DEPLOYMENT_METHODS[moduleId];
        var method = module.methods[methodName];
        
        if (method.overloadTest !== undefined) {
            result = method.overloadTest (rowData);
        } else if (method.overloadedMethodName !== undefined) {
            result = method.overloadedMethodName;
        }
    }
    
    return result;
}

// sets the method values for a select list. moduleId determines which set of methods to
// populate. selectedMethod determines a selected method.
//
function setMethodSelectData (
    methodSelectId,
    moduleId,
    selectedMethod
) {
    $(methodSelectId).children().remove();
    var moduleNum = null;
    
    var menuName = (moduleId != null && moduleId.length > 0) ? "Method" : "Module";
    $(methodSelectId).append ('<option value="">(Choose ' + menuName + ' ...)</option>');

    // make sure the module id is valid
    //
    if (DEPLOYMENT_METHODS[moduleId] == null)
        return;
    
    for (var methodName in DEPLOYMENT_METHODS[moduleId].methods) {
        $(methodSelectId).append ('<option value="' + methodName + '">' + methodName + '</option>');
    }
    
    $(".method_param").remove();

    if (selectedMethod != null && selectedMethod.length > 0)
        $(methodSelectId).val (selectedMethod);
}

function setMethodParameters (
    tableId,
    moduleId,
    methodName,
    rowData,
    prefs
) {
    if (DEPLOYMENT_METHODS[moduleId] !== undefined && DEPLOYMENT_METHODS[moduleId].methods[methodName] !== undefined) {
        var module = DEPLOYMENT_METHODS[moduleId];
        var method = module.methods[methodName];
        var paramList = method.params;
        
        // get the param values from the previous method so they can be put back into the same fields.
        //
        var oldParamValues = new Object();
        for (var p = 0; p <= 9; p++) {
            var paramName = $("#param" + p).parent().parent().children().first().first().contents().html(); // have to get this from the parameter row's label
            oldParamValues[paramName] = $("#param" + p).val();
        }
        
        // remove all the parameters from the previous method
        //
        $(".method_param").remove();

        // build up the new parameters
        //
        for (var p = 0; p < paramList.length; p++) {
            var param = paramList[p];
            var paramValue = (rowData["param" + p] !== undefined) 
                                ? rowData["param" + p]
                                : (oldParamValues[param.name] !== undefined)
                                    ? oldParamValues[param.name]
                                    : "";

            //if (param.type == "serverId") {
                //continue; // serverId is the first argument to all methods, and so is hard coded into the form
                
            // CSV and SCSV types
            //
            //} else 
            if (/^S?CSV/.test (param.type)) {
                var csvType, csvContent;
                var csvSplit = param.type.split (": ");

                // split the parameter type entry into CSV type and content
                //
                for (var i = 0; i < csvSplit.length; i++) {
                    switch (i) {
                        case 0:
                            csvType = csvSplit[0];
                            break;
                        case 1:
                            csvContent = csvSplit[1];
                            break;
                    }
                }
                
                switch (csvType) {
                    // simple comma separated values
                    // simple space or comma separated values
                    //
                    case "CSV":
                    case "SCSV":
                        switch (csvContent) {
                            
                            // provide a multi-select list of module content ids
                            //
                            // executeConfiguredProcedures in the resource module only accepts 
                            // procedure resource IDs. provide a multi-select list
                            //
                            case "id":
                            case "resourceProcedureId": // not sure if i can enforce this if users don't set resource type.
                            case "id_w_excludes":
                                var allowsExclude = (csvContent == "id_w_excludes") ? true : false;
                                var excludeHtml = "";

                                if (allowsExclude)
                                    excludeHtml = '        All excluding these ID\'s?&nbsp;<input type="checkbox" id="param' + p + '_ex" name="param' + p + '_ex" />';

                                $(tableId).append (
                                    '<tr class="method_param">' +
                                    '    <td><label for="param' + p + '" class="dialog_label">' + param.name + '</label></td>' + 
                                    '    <td>' +
                                    '        <select name="param' + p + '_sel" id="param' + p + '_sel" size="4" multiple="true" >' + 
                                    '            <option value="">(Enter valid value for path to module XML...)</option>' +
                                    '        </select>' + 
                                    '        <input type="hidden" name="param' + p + '" id="param' + p + '"/>' +
                                    excludeHtml +
                                    '    </td>' +
                                    '</tr>'
                                );
 
                                // set the parameter number and a change event on the select list 
                                // to update the hidden input value
                                //
                                $('#param' + p + '_sel')
                                    .data ('paramNum', p)
                                    .change (function (event, ui) {
                                        var pnum = $(this).data ('paramNum');
                                        
                                        // set the hidden input from the select list values. multi-select returns
                                        // an array of values so joining them together as a CSV. 
                                        //
                                        if ($('#param' + pnum + '_sel').val().length >= 1) {
                                            var starFound = false;
                                            
                                            $('#param' + pnum + '_sel option:selected').each (function() {
                                                if ($(this).val() == ALL_RESOURCES_INDICATOR)
                                                    starFound = true;
                                            });

                                            if (starFound) {
                                                $('#param' + pnum + '_sel').val(ALL_RESOURCES_INDICATOR);
                                                $('#param' + pnum).val (ALL_RESOURCES_INDICATOR);
                                                $('#param' + pnum + '_ex').prop ('checked', false);
                                            } else {
                                                    $('#param' + pnum).val (
                                                        (($('#param' + pnum + '_ex').prop ('checked')) ? EXCLUDE_RESOURCES_INDICATOR : "") +
                                                        $('#param' + pnum + '_sel').val().join()
                                                    );
                                            }
                                        } else {
                                            $('#param' + pnum).val ("");
                                        }
                                    });
                                
                                // set the value, the parameter number, and a change event on the hidden 
                                // input to update the selected items in the select list
                                //
                                $('#param' + p)
                                    .val (paramValue); /*
                                    .data ('paramNum', p)
                                    .change (function (event, ui) {
                                        var pnum = $(this).data ('paramNum');
                                        
                                        // set the select list selected values from the hidden input value.
                                        //
                                        $('#param' + pnum).val ($('#param' + pnum + '_sel').val().join());
                                    }); */
                                
                                // if this is a field that allows excludes, set the parameter number and a change 
                                // event on the exclude checkbox to update the hidden input value
                                //
                                if (allowsExclude) {
                                    $('#param' + p + '_ex')
                                        .data ('paramNum', p)
                                        .change (function (event, ui) {
                                            var pnum = $(this).data ('paramNum');
                                            var isChecked = $(this).prop ('checked');
                                            
                                            // set the hidden input from the select list values. multi-select returns
                                            // an array of values so joining them together as a CSV. 
                                            //
                                            if ($('#param' + pnum + '_sel').val().length >= 1) {
                                                var starFound = false;
                                                
                                                $('#param' + pnum + '_sel option:selected').each (function() {
                                                    if ($(this).val() == ALL_RESOURCES_INDICATOR)
                                                        starFound = true;
                                                });

                                                if (starFound) {
                                                    $('#param' + pnum + '_sel').val(ALL_RESOURCES_INDICATOR);
                                                    $('#param' + pnum).val (ALL_RESOURCES_INDICATOR);
                                                    $(this).prop ('checked', false);
                                                } else {
                                                    $('#param' + pnum).val (
                                                        ((isChecked) ? EXCLUDE_RESOURCES_INDICATOR : "") +
                                                        $('#param' + pnum + '_sel').val().join()
                                                    );
                                                }
                                            } else {
                                                $('#param' + pnum).val ("");
                                            }                                            
                                        });
                                }

                                // make ajax call to get the IDs and set callback to populate the select list
                                //
                                var modulePath = rowData["param" + param.moduleParamNum];
                                
                                if (modulePath != null && modulePath.length > 0) {
                                    var url = module.listUrlBase + encodeURIComponent (modulePath) + "?_search=false&rows=1000&page=1&sidx=id&sord=asc&param=" + p + "&rnd=" + myRand();

                                    var ajaxOpts = {  
                                        type: "GET",
                                        contentType: 'application/json',
                                        url: url,
                                        success: setIdSelectData,
                                        error: function (jqXHR, textStatus, errorThrown) {
                                            myAlert ("textStatus = " + textStatus + "<br>errorThrown = " + errorThrown);
                                            $("#aeOkButton").button ("enable");
                                        }
                                    }

                                    $.ajax(ajaxOpts);
                                }

                                break;
                            
                            // free-form strings. provide a button to bring up a dialog containing a table of text input fields.
                            //
                            case "string":
                                $(tableId).append (
                                    '<tr class="method_param">' +
                                    '    <td><label for="param' + p + '" class="dialog_label">' + param.name + '</label></td>' + 
                                    '    <td>' +
                                    '         <input type="text" name="param' + p + '" id="param' + p + '" size="60" disabled="true"/>' +
                                    '         <div id="param' + p + '_set" title="Set CSV Values" >Set CSV Values</div>' + 
                                    '    </td>' +
                                    '</tr>'
                                );
                                
                                $("#param" + p).val (paramValue);
                                
                                $("#param" + p + "_set")
                                    .button({
                                        icons: {
                                            primary: "ui-icon-pencil"
                                        },
                                        text: false
                                    })
                                    .data ('paramNum', p)
                                    .data ('isSCSV', (csvType == "SCSV"))
                                    .click (function() {
                                        setCSVDialogData (
                                            $(this).data ('paramNum'),
                                            $(this).data ('isSCSV')
                                        );
                                        
                                        $("#csvDialog").dialog ("open");
                                    });
                                
                                break;
                        }

                        break;

                    // an enumerated list of space or comma separated values
                    //
                    case "SCSV_enum":
                        $(tableId).append (
                            '<tr class="method_param">' +
                            '    <td><label for="param' + p + '" class="dialog_label">' + param.name + '</label></td>' + 
                            '    <td>' +
                            '         <select name="param' + p + '_sel" id="param' + p + '_sel" size="4" multiple="true" ></select>' + 
                            '         <input type="hidden" name="param' + p + '" id="param' + p + '"/>' +
                            '    </td>' +
                            '</tr>'
                        );
                        
                        // set the parameter number and a change event on the select list 
                        // to update the hidden input value
                        //
                        $('#param' + p + '_sel')
                            .data ('paramNum', p)
                            .change (function (event, ui) {
                                var pnum = $(this).data ('paramNum');
                                
                                // set the hidden input from the select list values. multi-select returns
                                // an array of values so joining them together as a CSV.
                                //
                                $('#param' + pnum).val ($('#param' + pnum + '_sel').val().join());
                            });
                        
                        // set the value, the parameter number, and a change event on the hidden 
                        // input to update the selected items in the select list
                        //
                        $('#param' + p)
                            .val (paramValue)
                            .data ('paramNum', p)
                            .change (function (event, ui) {
                                var pnum = $(this).data ('paramNum');
                                
                                // set the select list selected values from the hidden input value.
                                //
                                $('#param' + pnum).val ($('#param' + pnum + '_sel').val().join());
                            });
                        
                        // set the selection options for the select list
                        //
                        var enumList = csvContent.split (" ");
                        for (var e = 0; e < enumList.length; e++) {
                            var enumVal = enumList[e];
                            
                            $('#param' + p + '_sel').append ('<option value="' + enumVal.toUpperCase() + '">' + enumVal + '</option>');
                        }
 
                        // set the selected values from the hidden input field
                        //
                        $('#param' + p + '_sel').val ($('#param' + p).val().toUpperCase().split(/\s*[\s,]\s*/)); // regexp looks for either a space or comma surrounded by optional whitespace.

                        break;

                } // switch (csvType)
                
            // use a text area where parameter values are expected to be somewhat long
            //
            } else if (param.type == "long_string") {
                $(tableId).append (
                    '<tr class="method_param">' +
                    '    <td><label for="param' + p + '" class="dialog_label">' + param.name + '</label></td>' + 
                    '    <td><textarea name="param' + p + '" id="param' + p + '" cols="60" rows="4" class="text ui-widget-content ui-corner-all" ></textarea></td>' +
                    '</tr>'
                );
                
                // set the field's value
                //
                if (paramValue != null && paramValue.length > 0) {
                    $('#param' + p).val (paramValue);
                }
            
            // use a select dropdown for enumerated values where only a single value is needed.
            //
            } else if (/^enum:/.test (param.type)) {
                var enumType, enumContent;
                var enumSplit = param.type.split (": ");

                // split the parameter type entry into CSV type and content
                //
                for (var i = 0; i < enumSplit.length; i++) {
                    switch (i) {
                        case 0:
                            enumType = enumSplit[0];
                            break;
                        case 1:
                            enumContent = enumSplit[1];
                            break;
                    }
                }

                $(tableId).append (
                    '<tr class="method_param">' +
                    '    <td><label for="param' + p + '" class="dialog_label">' + param.name + '</label></td>' + 
                    '    <td><select name="param' + p + '" id="param' + p + '" ></select></td>' +
                    '</tr>'
                );                

                // set the selection options for the select list
                //
                var enumList = enumContent.split (" ");
                for (var e = 0; e < enumList.length; e++) {
                    var enumVal = enumList[e];
                    
                    $('#param' + p).append ('<option value="' + enumVal + '">' + enumVal + '</option>');
                }
                
                // finally set the value of the select list
                //
                if (paramValue != null && paramValue.length > 0) {
                    $('#param' + p).val (paramValue.toUpperCase());
                }


            // use a radio button for boolean fields
            //
            } else if (param.type == "boolean") {
            
                $(tableId).append (
                    '<tr class="method_param">' +
                    '    <td><label for="param' + p + '" class="dialog_label">' + param.name + '</label></td>' + 
                    '    <td>' +
                    '        <div id="param' + p + '_rdo">' +
                    '            <input type="radio" name="param' + p + '_rdo" id="param' + p + '_rdo_true" value="true" /><label for="param' + p + '_rdo_true">True</label>' +
                    '            <input type="radio" name="param' + p + '_rdo" id="param' + p + '_rdo_false" value="false" /><label for="param' + p + '_rdo_false">False</label>' +
                    '        </div>' +
                    '        <input type="hidden" name="param' + p + '" id="param' + p + '"/>' +
                    '    </td>' +
                    '</tr>'
                );                

                $("#param" + p + "_rdo").buttonset();
                if (paramValue != null && paramValue.length > 0) {
                    $('#param' + p + '_rdo_' + paramValue.toLowerCase()).prop ("checked", "checked");
                    $("#param" + p + "_rdo").buttonset("refresh");
                }

            
                // set the parameter number and a change event on the select list 
                // to update the hidden input value
                //
                $('#param' + p + '_rdo')
                    .data ('paramNum', p)
                    .change (function (event, ui) {
                        var pnum = $(this).data ('paramNum');
                        
                        // set the hidden input from the radio button 
                        //
                        $('#param' + pnum).val ($('#param' + pnum + '_rdo input:radio:checked').val());
                    });
                
                // set the value, the parameter number, and a change event on the hidden 
                // input to update the selected items in the select list
                //
                $('#param' + p)
                    .val (paramValue); /*
                    .data ('paramNum', p)
                    .change (function (event, ui) {
                        var pnum = $(this).data ('paramNum');
                        
                        // set the radio button selected value from the hidden input value.
                        //
                        $('#param' + pnum + '_rdo_' + $('#param' + pnum).val()).prop ("checked", "checked");
                        $("#param" + pnum + "_rdo").buttonset("refresh");
                    }); */
            
            // for vcs resource types, set up a static select list.
            //
            } else if (param.type == "vcs_type") {
                
                $(tableId).append (
                    '<tr class="method_param">' +
                    '    <td><label for="param' + p + '" class="dialog_label">' + param.name + '</label></td>' + 
                    '    <td><select name="param' + p + '" id="param' + p + '" ></select></td>' +
                    '</tr>'
                );
                
                setVcsResourceTypeSelectData ("#param" + p);
                if (paramValue != null && paramValue.length > 0) {
                    $('#param' + p).val (paramValue.toUpperCase());
                }

            // use a simple text field for anything that doesn't match a known type
            //
            } else {
                var linkHtml = '';
                if (/pathTo\w+XML/.test (param.name) || param.name == "vcsConnectionId")
                    linkHtml = '&nbsp;<span id="param' + p + '_link" class="clickable"><img src="images/edit.gif" border="0"/></span>';
                    
                $(tableId).append (
                    '<tr class="method_param">' +
                    '    <td><label for="param' + p + '" class="dialog_label">' + param.name + '</label></td>' + 
                    '    <td><input type="text" name="param' + p + '" id="param' + p + '" size="60" class="text ui-widget-content ui-corner-all" />' + linkHtml + '</td>' +
                    '</tr>'
                );
                
                // set the field's value
                //
                if (paramValue != null && paramValue.length > 0) {
                    $('#param' + p).val (paramValue);
                } else if (param.name == "server") {
                    $('#param' + p).val (prefs.defaultServer);
                }
                
                if (param.name == "server") {
                    // set up the server text input to be an auto complete field.
                    // set a dummy list for now. opening the dialog will set auto-complete
                    // values each time.
                    //
                    $('#param' + p).autocomplete ({
                                    source: [],
                                    appendTo: "#addEditDialog"
                                });

                                        
                    setServerAutoCompleteData ('#param' + p);                    
                }

                if (param.name == "pathToServersXML") {
                    // set the URL for the edit link
                    $('#param' + p + '_link').click (function() { location = "/pdtoolgui/config_servers.html?rnd=" + myRand(); });

                    // servers XML field always points to $MODULE_HOME/servers.xml. overwrite any existing values.
                    //
                    $('#param' + p).val ("$MODULE_HOME/servers.xml");
                    
                    // disable the field so the user can't update it.
                    //
                    $('#param' + p).prop ("disabled", true);
                }
                
                // set up auto-complete widget for VCS connection IDs.
                //
                if (param.name == "vcsConnectionId") {
                    $('#param' + p + '_link')
                        .data ('moduleId', moduleId)
                        .data ('methodName', methodName)
                        .data ('paramNum', p)
                        .click (editModuleCallback);

                    $('#param' + p).autocomplete ({source: []});
                }

                // set up auto-complete widgets for certain file paths.
                //
                if (/pathTo\w+(XML|Defs)/.test (param.name) && param.name != "pathToServersXML") {
                    // set up the edit link with some data to use when calling the editModuleCallback function
                    //
                    $('#param' + p + '_link')
                        .data ('moduleId', moduleId)
                        .data ('methodName', methodName)
                        .data ('paramNum', p)
                        .click (editModuleCallback);

                    // attach data to the input field and set up auto-complete on it.
                    //
                    $('#param' + p)
                        .autocomplete ({source: []})
                        .data ('moduleId', moduleId)
                        .data ('methodName', methodName)
                        .data ('paramNum', p);
                    
                    setParamAutoCompleteData ('#param' + p, module.fileType);

                    // set change event listener to update id fields and VCS connection auto-complete only if method is either not a generate method or is the "generateRegressionSecurityXML" method.)
                    //
                    if (/pathTo\w+XML/.test (param.name) && param.name != "pathToServersXML" && (rowData.method == "generateRegressionSecurityXML" || ! /^generate/.test (rowData.method))) {
                        $('#param' + p).blur (refreshIdsCallback);
                        if (module.name == "VCS") { 
                            $('#param' + p).blur (refreshVCSConnCallback);
                        }
                        
                        $('#param' + p).blur();
                    }
                } // is path to XML param
            } // is misc data type
            
            // set the title attribute so the tool tip works
            //
            $("label[for=param" + p + "]").attr ("title", param.title);
            
        } // for params in param list
    } else {
        console.log ("setMethodParameters: either moduleId or methodName was null!");

        $(".method_param").remove();
    }
}

function setIdSelectData (data) {
    if (data == null) {
        myAlert (
            "No data returned from call to module DAO to get module ID's. " +
            "Possible reasons: <ul>" + 
            "<li>The specified module XML file doesn't exist</li>" + 
            "<li>The default deployment properties file has not been set in the preferences page under the Configuration tab (needed to resolve variable references.)</li>" +
            "</ul>"
        );
        return false;
    }

    var p = data.param;

    if (data.rows != null && data.rows.length > 0) {
        $('#param' + p + '_sel').children().remove();
    }

    if ( data.rows !== undefined) {
        for (var i = 0; i < data.rows.length; i++) {
            var cell = data.rows[i].cell;

            var id = data.rows[i].cell[0];
            $('#param' + p + '_sel').append ('<option value="' + id + '">' + cell[0] + '</option>');
        }
        $('#param' + p + '_sel').append ('<option value="' + ALL_RESOURCES_INDICATOR + '">' + ALL_RESOURCES_INDICATOR + '</option>');
    }

    var valuesStr = $('#param' + p).val();
    
    // see if the user is using the value as an exclude list
    //
    if (valuesStr != null && valuesStr.substr (0,1) == EXCLUDE_RESOURCES_INDICATOR) {
        
        // if the field allows exclusions (meaning the exclude checkbox is present) then check the box
        //
        if ($("#param" + p + "_ex").length == 1) {
            $("#param" + p + "_ex").prop ("checked", true);
        }
        
        // remove the exclusion indicator from the value.
        //
        valuesStr = valuesStr.substr (1, valuesStr.length - 1);
    }
    
    // set the select list's selected values
    //
    if (valuesStr != null)
        $('#param' + p + '_sel').val (valuesStr.split(/\s*,\s*/));
}

function setVCSConnACData (data) {
    if (data == null || data.rows == null)
        return false;

    var p = data.param;

    var acValues = new Array();
    for (var i = 0; i < data.rows.length; i++) {
        var cell = data.rows[i].cell;

        acValues.push (cell[0]);
    }

    $("#param" + p).autocomplete ("option", "source", acValues);
}

function refreshIdsCallback() {
    var moduleId = $(this).data ('moduleId');
    var methodName = $(this).data ('methodName');
    var paramNum = $(this).data ('paramNum');
    var module = DEPLOYMENT_METHODS[moduleId];
    var method = module.methods[methodName];
    var param = method.params[paramNum];

    var moduleUrlBase = module.listUrlBase;
    var moduleXmlPath = $("#param" + method.moduleParamNum).val();
    var url = (module.name == "VCS")
                ? module.listUrlBase + encodeURIComponent (moduleXmlPath) + "?_search=true&rows=10&page=1&sidx=id&sord=asc&searchField=type&searchString=Resource&searchOper=eq&filters=&param=" + method.idParamNum + "&rnd=" + myRand()
                : module.listUrlBase + encodeURIComponent (moduleXmlPath) + "?_search=false&rows=1000&page=1&sidx=id&sord=asc&param=" + method.idParamNum + "&rnd=" + myRand();

    $.getJSON (
        url,
        null,
        setIdSelectData
    );
}

function refreshVCSConnCallback() {
    var moduleId = $(this).data ('moduleId');
    var methodName = $(this).data ('methodName');
    var paramNum = $(this).data ('paramNum');
    var module = DEPLOYMENT_METHODS[moduleId];
    var method = module.methods[methodName];
    var param = method.params[paramNum];

    var moduleUrlBase = module.listUrlBase;
    var moduleXmlPath = $("#param" + method.moduleParamNum).val();
    var url = module.listUrlBase + encodeURIComponent (moduleXmlPath) + "?_search=true&rows=10&page=1&sidx=id&sord=asc&searchField=type&searchString=Connection&searchOper=eq&filters=&param=" + method.connParamNum + "&rnd=" + myRand();

    $.getJSON (
        url,
        null,
        setVCSConnACData
    );
}

function editModuleCallback (event, ui) {
    var moduleId = $(this).data ('moduleId');
    var methodName = $(this).data ('methodName');
    var paramNum = $(this).data ('paramNum');
    var module = DEPLOYMENT_METHODS[moduleId];
    var method = module.methods[methodName];

    var paramVal = $("#param" + paramNum).val();
    var moduleXmlPath = $("#param" + method.moduleParamNum).val();

//    if (paramVal == null || paramVal.length == 0) {
        location = module.linkUrl + "?rnd=" + myRand();
//    } else {
//        location = module.editUrl + "?path=" + encodeURIComponent (moduleXmlPath) + "&rnd=" + myRand();
//    }
}

function createCSVDialog (dialogId) {
    return $(dialogId).dialog ({
        autoOpen: false,
        //height: 300,
        width: 300,
        modal: true,
        buttons: [ // declaring buttons this way lets us assign ID's to the buttons so they can be interacted with later.
            {
                id: "csvOkButton",
                text: "OK",
                click: function() {
                    var p = $("#csvParamNum").val();
                    var arry = new Array();

                    for (var i = 0; i < csvRowCount; i++) {
                        
                        // make sure the row hasn't been deleted by the user and that the array entry value name is filled in
                        //
                        if ($("#csv_row_" + i).length > 0 && $('#csv_value_' + i).val().length > 0) {
                            arry.push ($("#csv_value_" + i).val());
                        }
                    }
                    
                    $("#param" + p).val(arry.join());

                    $(this).dialog ("close");              
                }
            }, {
                id: "csvCancelButton",
                text: "Cancel",
                click: function() {
                    $(this).dialog ("close");              
                }
            }
        ]
    }); // #createAttributeArrayDialog
}

function setCSVDialogData (
    paramNum,
    isSCSV
) {
    var valueArray = new Array();
    
    if (isSCSV) {
        valueArray = $("#param" + paramNum).val().split(/\s*,\s*/);
        
        if (valueArray.length == 0)
            valueArray = $("#param" + paramNum).val().split(/\s+/);
    } else {
        valueArray = $("#param" + paramNum).val().split(/\s*,\s*/);
    }

    $("#csvParamNum").val(paramNum);

    $('.csv_row').remove();
    csvRowCount = 0;
    
    if (valueArray != null || valueArray.length > 0) {
        for (var c = 0; c < valueArray.length; c++) {
            var value = valueArray[c];
            
            addCSVRow();
            var rowid = csvRowCount - 1;
            
            $("#csv_value_" + rowid).val (value);
        }
    } else {
        addCSVRow();
    }
}

var csvRowCount = 0;
function addCSVRow() {
    // add the html code before the "add new attribute" button container row.
    //
    $('#csv_add_row').before (function() {
         return '<tr id="csv_row_' + csvRowCount + '" class="csv_row dynamic_row">\n' +
                '    <td><input type="text" name="csv_value_' + csvRowCount + '" id="csv_value_' + csvRowCount + '" class="text ui-widget-content ui-corner-all dynamic_row_value" /></td>\n' +
                '    <td><div id="csv_row_' + csvRowCount + '_del" title="Delete Value" >Delete Value</div></td>\n' +
                '</tr>\n';
    });
    
    // add a click event handler to the delete button to delete the attribute when clicked.
    //
    $("#csv_row_" + csvRowCount + "_del")
        .button({
            icons: {
                primary: "ui-icon-trash"
            },
            text: false
        })
        .data ('rownum', csvRowCount) // attach the row number as a bit of data that the click handler can reference.
        .click(function() {
            $('#csv_row_' + $(this).data ('rownum')).remove();
            if ($('.csv_row').length == 0) {
                addCSVRow();
            }
            return false;
        });

    csvRowCount++;
    
    return false;
}


var STANDARD_PARAM_TITLES = {
    serverId: "The CIS instance on which to perform this step", // dummy. uses the setting in pdtoolgui_docs_NN.js
    pathToServersXML: "The path to the servers.xml file (this will nearly always be <span class='code'>\"$MODULE_HOME/servers.xml\"</span>)"
};

// the currently supported modules, their respective methods, and the methods' parameters.
//
var DEPLOYMENT_METHODS = {
    "moduleArchive" : {
        name: "Archive", 
        listUrlBase: "/archive_module/",
        fileType: FILE_TYPE_MODULE_ARCHIVE,
        linkUrl: "/pdtoolgui/modules_archive.html",
        editUrl: "/pdtoolgui/module_archive.html",
        methods: {
            "pkg_import" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server", // dummy this doesn't get created as it's already in the dialog
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "archiveIds",
                        type: "CSV: id",
                        title: "The list of Archive Module ID's to import"
                    },
                    {
                        name: "pathToArchiveXML",
                        type: "file_path",
                        title: "The path to the Archive Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "pkg_export" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "archiveIds",
                        type: "CSV: id",
                        title: "The list of Archive Module ID's to export"
                    },
                    {
                        name: "pathToArchiveXML",
                        type: "file_path",
                        title: "The path to the Archive Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "backup_export" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "archiveIds",
                        type: "CSV: id",
                        title: "The list of Archive Module ID's to export"
                    },
                    {
                        name: "pathToArchiveXML",
                        type: "file_path",
                        title: "The path to the Archive Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "backup_import" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "archiveIds",
                        type: "CSV: id",
                        title: "The list of Archive Module ID's to import"
                    },
                    {
                        name: "pathToArchiveXML",
                        type: "file_path",
                        title: "The path to the Archive Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            }
        }
    },
    "moduleDataSource" : {
        name: "Data Source",
        listUrlBase: "/data_source_module/",
        fileType: FILE_TYPE_MODULE_DATA_SOURCE,
        linkUrl: "/pdtoolgui/modules_data_source.html",
        editUrl: "/pdtoolgui/module_data_source.html",
        methods: {
            "updateDataSources" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "dataSourceIds",
                        type: "CSV: id",
                        title: "The list of Data Source Module ID's to update"
                    },
                    {
                        name: "pathToDataSourceXML",
                        type: "file_path",
                        title: "The path to the Data Source Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "enableDataSources" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "dataSourceIds",
                        type: "CSV: id",
                        title: "The list of Data Source Module ID's to enable"
                    },
                    {
                        name: "pathToDataSourceXML",
                        type: "file_path",
                        title: "The path to the Data Source Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "reIntrospectDataSources" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "dataSourceIds",
                        type: "CSV: id",
                        title: "The list of Data Source Module ID's to re-introspect"
                    },
                    {
                        name: "pathToDataSourceXML",
                        type: "file_path",
                        title: "The path to the Data Source Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "introspectDataSources" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "dataSourceIds",
                        type: "CSV: id",
                        title: "The list of Data Source Module ID's to introspect"
                    },
                    {
                        name: "pathToDataSourceXML",
                        type: "file_path",
                        title: "The path to the Data Source Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "generateDataSourcesXML" : {
                idParamNum: null,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "startPath",
                        type: "cis_path",
                        title: "The path in the CIS instance in which to start looking for resources"
                    },
                    {
                        name: "pathToDataSourceXML",
                        type: "file_path",
                        title: "The path to the generated Data Source Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "generateDataSourceAttributeDefs" : {
                idParamNum: null,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "startPath",
                        type: "cis_path",
                        title: "The path in the CIS instance in which to start looking for resources"
                    },
                    {
                        name: "pathToDataSourceAttrDefs",
                        type: "file_path",
                        title: "The path to the generated Data Source Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "generateDataSourceAttributeDefsByDataSourceType" : {
                idParamNum: null,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "dataSourceType",
                        type: "dsType",
                        title: "The data source type (or adapter name) to use for generating attribute definitions"
                    },
                    {
                        name: "pathToDataSourceAttrDefs",
                        type: "file_path",
                        title: "The path to the generated Data Source Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "generateDataSourceTypes" : {
                idParamNum: null,
                moduleParamNum: 1,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "pathToDataSourceTypesXML",
                        type: "file_path",
                        title: "The path to the generated Data Source Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "generateDataSourcesResourceListXML" : {
                idParamNum: null,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "startPath",
                        type: "cis_path",
                        title: "The path in the CIS instance in which to start looking for resources"
                    },
                    {
                        name: "pathToDataSourceResourceListXML",
                        type: "file_path",
                        title: "The path to the generated Data Source Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            }
        }
    },
    "moduleGroup" :{
        name: "Group", 
        listUrlBase: "/group_module/",
        fileType: FILE_TYPE_MODULE_GROUP,
        linkUrl: "/pdtoolgui/modules_group.html",
        editUrl: "/pdtoolgui/module_group.html",
        methods: {
            "createOrUpdateGroups" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "groupIds",
                        type: "CSV: id",
                        title: "The list of Group Module ID's to create or update"
                    },
                    {
                        name: "pathToGroupsXML",
                        type: "file_path",
                        title: "The path to the Groups Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "deleteGroups" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "groupIds",
                        type: "CSV: id",
                        title: "The list of Group Module ID's to delete"
                    },
                    {
                        name: "pathToGroupsXML",
                        type: "file_path",
                        title: "The path to the Groups Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "addUsersToGroups" : {
                idParamNum: 1,
                moduleParamNum: 3,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "groupIds",
                        type: "CSV: id",
                        title: "The list of Group Module ID's to update"
                    },
                    {
                        name: "userNames",
                        type: "CSV: string",
                        title: "The list of users to add to each group ID"
                    },
                    {
                        name: "pathToGroupsXML",
                        type: "file_path",
                        title: "The path to the Groups Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "deleteUsersFromGroups" : {
                idParamNum: 1,
                moduleParamNum: 3,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "groupIds",
                        type: "CSV: id",
                        title: "The list of Group Module ID's to update"
                    },
                    {
                        name: "userNames",
                        type: "CSV: string",
                        title: "The list of users to delete from each group ID"
                    },
                    {
                        name: "pathToGroupsXML",
                        type: "file_path",
                        title: "The path to the Groups Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "generateGroupsXML" : {
                idParamNum: null,
                moduleParamNum: 3,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "domain",
                        type: "string",
                        title: "The CIS user domain to search for groups in"
                    },
                    {
                        name: "pathToGroupsXML",
                        type: "file_path",
                        title: "The path to the generated Groups Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            }
        }
    },
    "modulePrivilege" : {
        name: "Privilege", 
        listUrlBase: "/privilege_module/",
        fileType: FILE_TYPE_MODULE_PRIVILEGE,
        linkUrl: "/pdtoolgui/modules_privilege.html",
        editUrl: "/pdtoolgui/module_privilege.html",
        methods: {
            "updatePrivileges" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "privilegeIds",
                        type: "CSV: id",
                        title: "The list of Privilege Module ID's to update"
                    },
                    {
                        name: "pathToPrivilegeXML",
                        type: "file_path",
                        title: "The path to the Privileges Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "generatePrivilegesXML" : {
                idParamNum: null,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "startPath",
                        type: "cis_path",
                        title: "The path in the CIS instance in which to start looking for resources"
                    },
                    {
                        name: "pathToPrivilegeXML",
                        type: "file_path",
                        title: "The path to the generated Privileges Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "filter",
                        type: "SCSV_enum: ALL CONTAINER DATA_SOURCE DEFINITION_SET LINK PROCEDURE TABLE TREE TRIGGER",
                        title: "The list of resource types to generate privileges for"
                    },
                    {
                        name: "options",
                        type: "SCSV_enum: USER GROUP SYSTEM NONSYSTEM PARENT CHILD",
                        title: "The list of options that filters the types of privileges to generate. Defaults to <b>GROUP</b>, <b>NONSYSTEM</b>, and <b>PARENT</b>. " +
                               "<br><b>USER</b> - Generate privileges for users" + 
                               "<br><b>GROUP</b> - Generate privileges for groups" + 
                               "<br><b>SYSTEM</b> - Generate privileges for system users/groups" + 
                               "<br><b>NONSYSTEM</b> - Generate privileges for non-system users/groups" + 
                               "<br><b>PARENT</b> - Generate privileges for the found resource only" + 
                               "<br><b>CHILD</b> - Recursively generate privileges for folder and data source children"
                    },
                    {
                        name: "domainList",
                        type: "SCSV: string",
                        title: "The list of CIS user domains to generate privileges for"
                    }
                ]
            }
        }
    },
    "moduleRebind" : {
        name: "Rebind", 
        listUrlBase: "/rebind_module/",
        fileType: FILE_TYPE_MODULE_REBIND,
        linkUrl: "/pdtoolgui/modules_rebind.html",
        editUrl: "/pdtoolgui/module_rebind.html",
        methods: {
            "rebindResources" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "rebindIds",
                        type: "CSV: id",
                        title: "The list of Rebind Module resource ID's to rebind"
                    },
                    {
                        name: "pathToRebindXML",
                        type: "file_path",
                        title: "The path to the Rebind Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "rebindFolder" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "rebindIds",
                        type: "CSV: id",
                        title: "The list of Rebind Module folder ID's to rebind"
                    },
                    {
                        name: "pathToRebindXML",
                        type: "file_path",
                        title: "The path to the Rebind Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "generateRebindXML" : {
                idParamNum: null,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "startPath",
                        type: "cis_path",
                        title: "The path in the CIS instance in which to start looking for resources"
                    },
                    {
                        name: "pathToRebindXML",
                        type: "file_path",
                        title: "The path to the generated Rebind Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            }
        }
    },
    "moduleRegression" : {
        name: "Regression", 
        listUrlBase: "/regression_module/",
        fileType: FILE_TYPE_MODULE_REGRESSION,
        linkUrl: "/pdtoolgui/modules_regression.html",
        editUrl: "/pdtoolgui/module_regression.html",
        methods: {
            "createRegressionInputFile" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "regressionIds",
                        type: "CSV: id",
                        title: "The list of Regression Module ID's to create input files for"
                    },
                    {
                        name: "pathToRegressionXML",
                        type: "file_path",
                        title: "The path to the Regression Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "executeRegressionTest" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "regressionIds",
                        type: "CSV: id",
                        title: "The list of Regression Module ID's to test"
                    },
                    {
                        name: "pathToRegressionXML",
                        type: "file_path",
                        title: "The path to the Regression Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "compareRegressionFiles" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "regressionIds",
                        type: "CSV: id",
                        title: "The list of Regression Module ID's to compare"
                    },
                    {
                        name: "pathToRegressionXML",
                        type: "file_path",
                        title: "The path to the Regression Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "compareRegressionLogs" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "regressionIds",
                        type: "CSV: id",
                        title: "The list of Regression Module ID's to compare"
                    },
                    {
                        name: "pathToRegressionXML",
                        type: "file_path",
                        title: "The path to the Regression Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "executePerformanceTest" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "regressionIds",
                        type: "CSV: id",
                        title: "The list of Regression Module test ID's to test"
                    },
                    {
                        name: "pathToRegressionXML",
                        type: "file_path",
                        title: "The path to the Regression Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            }
,
            "executeSecurityTest" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "regressionIds",
                        type: "CSV: id",
                        title: "The list of Regression Module security test ID's to test"
                    },
                    {
                        name: "pathToRegressionXML",
                        type: "file_path",
                        title: "The path to the Regression Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "generateRegressionSecurityXML" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "regressionIds",
                        type: "CSV: id",
                        title: "The list of Regression Module test ID's to generate module entries for"
                    },
                    {
                        name: "pathToSourceRegressionXML",
                        type: "file_path",
                        title: "The path to the source Regression Module XML file (the generated module file path is specified in this file)"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            }        }
    },
    "moduleResource" : {
        name: "Resource", 
        listUrlBase: "/resource_module/",
        fileType: FILE_TYPE_MODULE_RESOURCE,
        linkUrl: "/pdtoolgui/modules_resource.html",
        editUrl: "/pdtoolgui/module_resource.html",
        methods: {
            "executeConfiguredProcedures" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "procedureIds",
                        type: "CSV: resourceProcedureId",
                        title: "The list of Resource Module procedure ID's to execute"
                    },
                    {
                        name: "pathToResourceXML",
                        type: "file_path",
                        title: "The path to the Resource Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "executeProcedure" : {
                idParamNum: null,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "procedureName",
                        type: "pubProcedureName",
                        title: "The published procedure to execute"
                    },
                    {
                        name: "dataServiceName",
                        type: "string",
                        title: "The virtual database (data service) the procedure resides in"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "arguments",
                        type: "CSV: string",
                        title: "The list of arguments to pass to the procedure. Character and date/time values must be single quoted."
                    }
                ]
            },
            "deleteResource" : {
                idParamNum: null,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "resourcePath",
                        type: "cis_path",
                        title: "The path in the CIS instance to the resource to delete"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "deleteResources" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "resourceIds",
                        type: "CSV: id",
                        title: "The list of Resource Module ID's to delete"
                    },
                    {
                        name: "pathToResourceXML",
                        type: "file_path",
                        title: "The path to the Resource Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "renameResource" : {
                idParamNum: null,
                moduleParamNum: null,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "resourcePath",
                        type: "cis_path",
                        title: "The path in the CIS instance to the resource to rename"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "newName",
                        type: "string",
                        title: "The new name for the resource"
                    }
                ]
            },
            "renameResources" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "resourceIds",
                        type: "CSV: id",
                        title: "The list of Resource Module ID's to rename"
                    },
                    {
                        name: "pathToResourceXML",
                        type: "file_path",
                        title: "The path to the Resource Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "copyResource" : {
                idParamNum: null,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "resourcePath",
                        type: "cis_path",
                        title: "The path in the CIS instance to the resource to copy"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "targetContainerPath",
                        type: "cis_path",
                        title: "The path in the CIS instance to the folder to copy the resource to"
                    },
                    {
                        name: "newName",
                        type: "string",
                        title: "The name for the copied resource"
                    },
                    {
                        name: "copyMode",
                        type: "enum: ALTER_NAME_IF_EXISTS FAIL_IF_EXISTS OVERWRITE_MERGE_IF_EXISTS OVERWRITE_REPLACE_IF_EXISTS",
                        title: "Indicates what to do if a resource already exists with the new name." +
                               "<br><b>ALTER_NAME_IF_EXISTS</b> - Uses a different name for the copy if the name is already in use." +
                               "<br><b>FAIL_IF_EXISTS</b> - Fail the plan step if the name is already in use." +
                               "<br><b>OVERWRITE_MERGE_IF_EXISTS</b> - Overwrite the existing resource and merge contents if the existing resource is a container." +
                               "<br><b>OVERWRITE_REPLACE_IF_EXISTS</b> - Overwrite the existing resource and replace contents if the existing resource is a container."
                    }
                ]
            },
            "copyResources" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "resourceIds",
                        type: "CSV: id",
                        title: "The list of Resource Module ID's to copy"
                    },
                    {
                        name: "pathToResourceXML",
                        type: "file_path",
                        title: "The path to the Resource Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "moveResource" : {
                idParamNum: null,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "resourcePath",
                        type: "cis_path",
                        title: "The path in the CIS instance to the resource to move"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "targetContainerPath",
                        type: "cis_path",
                        title: "The path in the CIS instance to the folder to move the resource to"
                    },
                    {
                        name: "newName",
                        type: "string",
                        title: "The new name for the resource."
                    }
                ]
            },
            "moveResources" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "resourceIds",
                        type: "CSV: id",
                        title: "The list of Resource Module ID's to move"
                    },
                    {
                        name: "pathToResourceXML",
                        type: "file_path",
                        title: "The path to the Resource Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "doResourcesExist" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "resourceIds",
                        type: "CSV: id",
                        title: "The list of Resource Module ID's to verify"
                    },
                    {
                        name: "pathToResourceXML",
                        type: "file_path",
                        title: "The path to the Resource Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "lockResource" : {
                idParamNum: null,
                moduleParamNum: null,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "resourcePath",
                        type: "cis_path",
                        title: "The path in the CIS instance to the resource to lock"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "lockResources" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "resourceIds",
                        type: "CSV: id",
                        title: "The list of Resource Module ID's to lock"
                    },
                    {
                        name: "pathToResourceXML",
                        type: "file_path",
                        title: "The path to the Resource Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "unlockResource" : {
                idParamNum: null,
                moduleParamNum: null,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "resourcePath",
                        type: "cis_path",
                        title: "The path in the CIS instance to the resource to unlock"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "comment",
                        type: "long_string",
                        title: "The comment to supply for unlocking the resource"
                    }
                ]
            },
            "unlockResources" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "resourceIds",
                        type: "CSV: id",
                        title: "The list of Resource Module ID's to unlock"
                    },
                    {
                        name: "pathToResourceXML",
                        type: "file_path",
                        title: "The path to the Resource Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "createFolder" : {
                idParamNum: null,
                moduleParamNum: null,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "resourcePath",
                        type: "cis_path",
                        title: "The path in the CIS instance to the folder to create"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "recursive",
                        type: "boolean",
                        title: "Indicates whether to create missing intervening folders or not"
                    }
                ]
            },
            "createFolders" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "resourceIds",
                        type: "CSV: id",
                        title: "The list of Resource Module folder ID's to create"
                    },
                    {
                        name: "pathToResourceXML",
                        type: "file_path",
                        title: "The path to the Resource Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            }
        }
    },
    "moduleResourceCache" : {
        name: "Resource Cache", 
        listUrlBase: "/resource_cache_module/",
        fileType: FILE_TYPE_MODULE_RESOURCE_CACHE,
        linkUrl: "/pdtoolgui/modules_resource_cache.html",
        editUrl: "/pdtoolgui/module_resource_cache.html",
        methods: {
            "updateResourceCache" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "resourceIds",
                        type: "CSV: id",
                        title: "The list of Resource Cache Module ID's to update"
                    },
                    {
                        name: "pathToResourceCacheXML",
                        type: "file_path",
                        title: "The path to the Resource Cache Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "updateResourceCacheEnabled" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "resourceIds",
                        type: "CSV: id",
                        title: "The list of Resource Cache Module ID's to enable/disable"
                    },
                    {
                        name: "pathToResourceCacheXML",
                        type: "file_path",
                        title: "The path to the Resource Cache Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "clearResourceCache" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "resourceIds",
                        type: "CSV: id",
                        title: "The list of Resource Cache Module ID's to clear"
                    },
                    {
                        name: "pathToResourceCacheXML",
                        type: "file_path",
                        title: "The path to the Resource Cache Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "refreshResourceCache" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "resourceIds",
                        type: "CSV: id",
                        title: "The list of Resource Cache Module ID's to refresh"
                    },
                    {
                        name: "pathToResourceCacheXML",
                        type: "file_path",
                        title: "The path to the Resource Cache Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "generateResourceCacheXML" : {
                idParamNum: null,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "startPath",
                        type: "cis_path",
                        title: "The path in the CIS instance in which to start looking for resources"
                    },
                    {
                        name: "pathToResourceCacheXML",
                        type: "file_path",
                        title: "The path to the generated Resource Cache Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "options",
                        type: "SCSV_enum: CONFIGURED NONCONFIGURED TABLE PROCEDURE",
                        title: "List of options that control what resources are generated." +
                               "<br><b>CONFIGURED</b> - Generate entries for resources configured for caching." +
                               "<br><b>NONCONFIGURED</b> - Generate entries for resources not configured for caching." +
                               "<br><b>TABLE</b> - Generate entries for table or view resources." +
                               "<br><b>PROCEDURE</b> - Generate entries for procedure resources."
                    }
                ]
            }
        }
    },
    "moduleServerAttribute" : {
        name: "Server Attribute", 
        listUrlBase: "/server_attribute_module/",
        fileType: FILE_TYPE_MODULE_SERVER_ATTRIBUTE,
        linkUrl: "/pdtoolgui/modules_server_attribute.html",
        editUrl: "/pdtoolgui/module_server_attribute.html",
        methods: {
            "updateServerAttributes" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "serverAttributeIds",
                        type: "CSV: id_w_excludes",
                        title: "The list of Server Attribute Module ID's to update"
                    },
                    {
                        name: "pathToServerAttributesXML",
                        type: "file_path",
                        title: "The path to the Server Attributes Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "generateServerAttributesXML" : {
                idParamNum: null,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "startPath",
                        type: "attr_path"
                    },
                    {
                        name: "pathToServerAttributesXML",
                        type: "file_path",
                        title: "The path to the generated Server Attributes Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "updateRule",
                        type: "enum: READ_ONLY READ_WRITE *",
                        title: "Filter the list of generated attributes based on whether the attribute is allowed to be updated. NOTE: Read only attributes are not updatable by PDTool."
                    }
                ]
            },
            "generateServerAttributeDefinitionsXML" : {
                idParamNum: null,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "startPath",
                        type: "attr_path",
                        title: "The starting path in the server attribute tree to look for attributes to generate"
                    },
                    {
                        name: "pathToServerAttributesXML",
                        type: "file_path",
                        title: "The path to the generated Server Attributes Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "updateRule",
                        type: "enum: READ_ONLY READ_WRITE *",
                        title: "Filter the list of generated attribute definitions based on whether the attribute is allowed to be updated"
                    }
                ]
            }
        }
    },
    "moduleServerManager" : {
        name: "Server Manager", 
        listUrlBase: "", // server manager doesn't have module xml files
        fileType: FILE_TYPE_MODULE_SERVER_MANAGER, // server manager doesn't have module files, but this is here for completeness.
        linkUrl: "/pdtoolgui/modules_server_manager.html",
        editUrl: "/pdtoolgui/module_server_manager.html",
        methods: {
            "startServer" : {
                idParamNum: null,
                moduleParamNum: null,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "stopServer" : {
                idParamNum: null,
                moduleParamNum: null,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "restartServer" : {
                idParamNum: null,
                moduleParamNum: null,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            }
        }
    },
    "moduleTrigger" : {
        name: "Trigger", 
        listUrlBase: "/trigger_module/",
        fileType: FILE_TYPE_MODULE_TRIGGER,
        linkUrl: "/pdtoolgui/modules_trigger.html",
        editUrl: "/pdtoolgui/module_trigger.html",
        methods: {
            "updateTriggers" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "triggerIds",
                        type: "CSV: id",
                        title: "The list of Trigger Module ID's to update"
                    },
                    {
                        name: "pathToTriggersXML",
                        type: "file_path",
                        title: "The path to the Trigger Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "enableTriggers" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "triggerIds",
                        type: "CSV: id",
                        title: "The list of Trigger Module ID's to enable"
                    },
                    {
                        name: "pathToTriggersXML",
                        type: "file_path",
                        title: "The path to the Trigger Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "generateTriggersXML" : {
                idParamNum: null,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "startPath",
                        type: "cis_path",
                        title: "The path in the CIS instance in which to start looking for resources"
                    },
                    {
                        name: "pathToTriggersXML",
                        type: "file_path",
                        title: "The path to the generated Trigger Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            }
        }
    },
    "moduleUser" : {
        name: "User", 
        listUrlBase: "/user_module/",
        fileType: FILE_TYPE_MODULE_USER,
        linkUrl: "/pdtoolgui/modules_user.html",
        editUrl: "/pdtoolgui/module_user.html",
        methods: {
            "createOrUpdateUsers" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "userIds",
                        type: "CSV: id_w_excludes",
                        title: "The list of User Module ID's to create or update"
                    },
                    {
                        name: "pathToUsersXML",
                        type: "file_path",
                        title: "The path to the User Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "deleteUsers" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "userIds",
                        type: "CSV: id",
                        title: "The list of User Module ID's to delete"
                    },
                    {
                        name: "pathToUsersXML",
                        type: "file_path",
                        title: "The path to the User Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "generateUsersXML" : {
                idParamNum: null,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "domainName",
                        type: "string",
                        title: "The CIS user domain to search for users in"
                    },
                    {
                        name: "pathToUsersXML",
                        type: "file_path",
                        title: "The path to the generated User Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            }
        }
    },
    "moduleVCS" : {
        name: "VCS", 
        listUrlBase: "/vcs_module/",
        fileType: FILE_TYPE_MODULE_VCS,
        linkUrl: "/pdtoolgui/modules_vcs.html",
        editUrl: "/pdtoolgui/module_vcs.html",
        methods: {
            "vcsInitWorkspace" : {
                idParamNum: null,
                moduleParamNum: null,
                params: [
                    {
                        name: "vcsUser",
                        type: "string",
                        title: "The user to log into the VCS server with"
                    },
                    {
                        name: "vcsPassword",
                        type: "password",
                        title: "The password to log into the VCS server with"
                    }
                ]
            },
            "vcsInitializeBaseFolderCheckin" : {
                idParamNum: null,
                moduleParamNum: null,
                params: [
                    {
                        name: "customPathList",
                        type: "CSV: string",
                        title: "A comma separated list of paths that are added to the base paths of <span class='code'>/shared</span> or <span class='code'>/services/databases</span> or <span class='code'>/services/webservices</span>"
                    },
                    {
                        name: "vcsUser",
                        type: "string",
                        title: "The user to log into the VCS server with"
                    },
                    {
                        name: "vcsPassword",
                        type: "password",
                        title: "The password to log into the VCS server with"
                    }
                ]
            },
            "vcsCheckout" : {
                idParamNum: null,
                moduleParamNum: null,
                overloadTest: function (rowData) {
                    if (rowData["param5"].match (/servers.xml$/) != null) {
                        return "vcsCheckout (VCSLabel)";
                    } else {
                        return "vcsCheckout";
                    }
                },
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "vcsResourcePath",
                        type: "cis_path",
                        title: "The path in the CIS instance to the resource to check out"
                    },
                    {
                        name: "vcsResourceType",
                        type: "vcs_type",
                        title: "The VCS resource type"
                    },
                    {
                        name: "vcsRevision",
                        type: "string",
                        title: "The VCS revision to check out"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serversXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "vcsUser",
                        type: "string",
                        title: "The user to log into the VCS server with"
                    },
                    {
                        name: "vcsPassword",
                        type: "password",
                        title: "The password to log into the VCS server with"
                    }
                ]
            },
            "vcsCheckout (VCSLabel)" : {
                idParamNum: null,
                moduleParamNum: null,
                overloadedMethodName: "vcsCheckout",
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "vcsResourcePath",
                        type: "cis_path",
                        title: "The path in the CIS instance to the resource to check out"
                    },
                    {
                        name: "vcsResourceType",
                        type: "vcs_type",
                        title: "The VCS resource type"
                    },
                    {
                        name: "vcsLabel",
                        type: "string",
                        title: "The VCS label to check out"
                    },
                    {
                        name: "vcsRevision",
                        type: "string",
                        title: "The VCS revision to check out"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serversXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "vcsUser",
                        type: "string",
                        title: "The user to log into the VCS server with"
                    },
                    {
                        name: "vcsPassword",
                        type: "password",
                        title: "The password to log into the VCS server with"
                    }
                ]
            },
            "vcsCheckouts" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "vcsIds",
                        type: "CSV: id",
                        title: "The list of VCS Module resource ID's to check out"
                    },
                    {
                        name: "pathToVcsXML",
                        type: "file_path",
                        title: "The path to the VCS Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serversXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "vcsUser",
                        type: "string",
                        title: "The user to log into the VCS server with"
                    },
                    {
                        name: "vcsPassword",
                        type: "password",
                        title: "The password to log into the VCS server with"
                    }
                ]
            },
            "vcsCheckin" : {
                idParamNum: null,
                moduleParamNum: null,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "vcsResourcePath",
                        type: "cis_path",
                        title: "The path in the CIS instance to the resource to check in"
                    },
                    {
                        name: "vcsResourceType",
                        type: "vcs_type",
                        title: "The VCS resource type"
                    },
                    {
                        name: "vcsMessage",
                        type: "string",
                        title: "The check in message"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serversXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "vcsCheckins" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "vcsIds",
                        type: "CSV: id",
                        title: "The list of VCS Module resource ID's to check in"
                    },
                    {
                        name: "pathToVcsXML",
                        type: "file_path",
                        title: "The path to the VCS Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serversXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "vcsForcedCheckin" : {
                idParamNum: null,
                moduleParamNum: null,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "vcsResourcePath",
                        type: "cis_path",
                        title: "The path in the CIS instance to the resource to force check in"
                    },
                    {
                        name: "vcsResourceType",
                        type: "vcs_type",
                        title: "The VCS resource type"
                    },
                    {
                        name: "vcsMessage",
                        type: "string",
                        title: "The check in message"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serversXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "vcsUser",
                        type: "string",
                        title: "The user to log into the VCS server with"
                    },
                    {
                        name: "vcsPassword",
                        type: "password",
                        title: "The password to log into the VCS server with"
                    }
                ]
            },
            "vcsForcedCheckins" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "vcsIds",
                        type: "CSV: id",
                        title: "The list of VCS Module resource ID's to force check in"
                    },
                    {
                        name: "pathToVcsXML",
                        type: "file_path",
                        title: "The path to the VCS Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serversXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "vcsUser",
                        type: "string",
                        title: "The user to log into the VCS server with"
                    },
                    {
                        name: "vcsPassword",
                        type: "password",
                        title: "The password to log into the VCS server with"
                    }
                ]
            },
            "vcsPrepareCheckin" : {
                idParamNum: null,
                moduleParamNum: null,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "vcsResourcePath",
                        type: "cis_path",
                        title: "The path in the CIS instance to the resource to prepare for check in"
                    },
                    {
                        name: "vcsResourceType",
                        type: "vcs_type",
                        title: "The VCS resource type"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serversXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "vcsUser",
                        type: "string",
                        title: "The user to log into the VCS server with"
                    },
                    {
                        name: "vcsPassword",
                        type: "password",
                        title: "The password to log into the VCS server with"
                    }
                ]
            },
            "vcsPrepareCheckins" : {
                idParamNum: 1,
                moduleParamNum: 2,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "vcsIds",
                        type: "CSV: id",
                        title: "The list of VCS Module resource ID's to prepare for check in"
                    },
                    {
                        name: "pathToVcsXML",
                        type: "file_path",
                        title: "The path to the VCS Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serversXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "vcsUser",
                        type: "string",
                        title: "The user to log into the VCS server with"
                    },
                    {
                        name: "vcsPassword",
                        type: "password",
                        title: "The password to log into the VCS server with"
                    }
                ]
            },
            "vcsScanPathLength" : {
                idParamNum: null,
                moduleParamNum: null,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "vcsMaxPathLength",
                        type: "integer",
                        title: "A non-negative integer length from which to compare path lengths found in vcsResourcePathList. When 0, use the default CommonConstants.maxWindowsPathLen=259."
                    },
                    {
                        name: "vcsResourcePathList",
                        type: "CSV: string",
                        title: "A comma separated list of CIS paths to scan"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "vcsUser",
                        type: "string",
                        title: "The user to log into the VCS server with"
                    },
                    {
                        name: "vcsPassword",
                        type: "password",
                        title: "The password to log into the VCS server with"
                    }
                ]
            },
            "generateVCSXML" : {
                idParamNum: null,
                moduleParamNum: null,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "startPath",
                        type: "cis_path",
                        title: "The path in the CIS instance in which to start looking for resources"
                    },
                    {
                        name: "pathToVcsXML",
                        type: "file_path",
                        title: "The path to the generated VCS Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            },
            "vcsInitWorkspace2" : {
                connParamNum: 0,
                idParamNum: null,
                moduleParamNum: 1,
                params: [
                    {
                        name: "vcsConnectionId",
                        type: "vcsConnectionId",
                        title: "The VCS Module connection ID to use to initialize the workspace"
                    },
                    {
                        name: "pathToVcsXML",
                        type: "file_path",
                        title: "The path to the VCS Module XML file"
                    },
                    {
                        name: "vcsUser",
                        type: "string",
                        title: "The user to log into the VCS server with"
                    },
                    {
                        name: "vcsPassword",
                        type: "password",
                        title: "The password to log into the VCS server with"
                    }
                ]
            },
            "vcsInitializeBaseFolderCheckin2" : {
                connParamNum: 0,
                idParamNum: null,
                moduleParamNum: 2,
                params: [
                    {
                        name: "vcsConnectionId",
                        type: "vcsConnectionId",
                        title: "The VCS Module connection ID to use to initialize the workspace"
                    },
                    {
                        name: "customPathList",
                        type: "CSV: string",
                        title: "A comma separated list of paths that are added to the base paths of <span class='code'>/shared</span> or <span class='code'>/services/databases</span> or <span class='code'>/services/webservices</span>"
                    },
                    {
                        name: "pathToVcsXML",
                        type: "file_path",
                        title: "The path to the VCS Module XML file"
                    },
                    {
                        name: "vcsUser",
                        type: "string",
                        title: "The user to log into the VCS server with"
                    },
                    {
                        name: "vcsPassword",
                        type: "password",
                        title: "The password to log into the VCS server with"
                    }
                ]
            },
            "vcsCheckout2" : {
                connParamNum: 1,
                idParamNum: null,
                moduleParamNum: 5,
                overloadTest: function (rowData) {
                    if (rowData["param7"].match (/servers.xml$/) != null) {
                        return "vcsCheckout2 (VCSLabel)";
                    } else {
                        return "vcsCheckout2";
                    }
                },
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "vcsConnectionId",
                        type: "vcsConnectionId",
                        title: "The VCS Module connection ID to use to perform the check out"
                    },
                    {
                        name: "vcsResourcePath",
                        type: "cis_path",
                        title: "The path in the CIS instance to the resource to check out"
                    },
                    {
                        name: "vcsResourceType",
                        type: "vcs_type",
                        title: "The VCS resource type"
                    },
                    {
                        name: "vcsRevision",
                        type: "string",
                        title: "The VCS revision to check out"
                    },
                    {
                        name: "pathToVcsXML",
                        type: "file_path",
                        title: "The path to the VCS Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serversXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "vcsUser",
                        type: "string",
                        title: "The user to log into the VCS server with"
                    },
                    {
                        name: "vcsPassword",
                        type: "password",
                        title: "The password to log into the VCS server with"
                    }
                ]
            },
            "vcsCheckout2 (VCSLabel)" : {
                connParamNum: 1,
                idParamNum: null,
                moduleParamNum: 6,
                overloadedMethodName: "vcsCheckout2",
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "vcsConnectionId",
                        type: "vcsConnectionId",
                        title: "The VCS Module connection ID to use to perform the check out"
                    },
                    {
                        name: "vcsResourcePath",
                        type: "cis_path",
                        title: "The path in the CIS instance to the resource to check out"
                    },
                    {
                        name: "vcsResourceType",
                        type: "vcs_type",
                        title: "The VCS resource type"
                    },
                    {
                        name: "vcsLabel",
                        type: "string",
                        title: "The VCS label to check out"
                    },
                    {
                        name: "vcsRevision",
                        type: "string",
                        title: "The VCS revision to check out"
                    },
                    {
                        name: "pathToVcsXML",
                        type: "file_path",
                        title: "The path to the VCS Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serversXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "vcsUser",
                        type: "string",
                        title: "The user to log into the VCS server with"
                    },
                    {
                        name: "vcsPassword",
                        type: "password",
                        title: "The password to log into the VCS server with"
                    }
                ]
            },
            "vcsCheckouts2" : {
                connParamNum: 1,
                idParamNum: 2,
                moduleParamNum: 3,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "vcsConnectionId",
                        type: "vcsConnectionId",
                        title: "The VCS Module connection ID to use to perform the check out"
                    },
                    {
                        name: "vcsIds",
                        type: "CSV: id",
                        title: "The list of VCS Module resource ID's to check out"
                    },
                    {
                        name: "pathToVcsXML",
                        type: "file_path",
                        title: "The path to the VCS Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serversXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "vcsUser",
                        type: "string",
                        title: "The user to log into the VCS server with"
                    },
                    {
                        name: "vcsPassword",
                        type: "password",
                        title: "The password to log into the VCS server with"
                    }
                ]
            },
            "vcsCheckin2" : {
                connParamNum: 1,
                idParamNum: null,
                moduleParamNum: 5,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "vcsConnectionId",
                        type: "vcsConnectionId",
                        title: "The VCS Module connection ID to use to perform the check in"
                    },
                    {
                        name: "vcsResourcePath",
                        type: "cis_path",
                        title: "The path in the CIS instance to the resource to check in"
                    },
                    {
                        name: "vcsResourceType",
                        type: "vcs_type",
                        title: "The VCS resource type"
                    },
                    {
                        name: "vcsMessage",
                        type: "string",
                        title: "The check in message"
                    },
                    {
                        name: "pathToVcsXML",
                        type: "file_path",
                        title: "The path to the VCS Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serversXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "vcsUser",
                        type: "string",
                        title: "The user to log into the VCS server with"
                    },
                    {
                        name: "vcsPassword",
                        type: "password",
                        title: "The password to log into the VCS server with"
                    }
                ]
            },
            "vcsCheckins2" : {
                connParamNum: 1,
                idParamNum: 2,
                moduleParamNum: 3,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "vcsConnectionId",
                        type: "vcsConnectionId",
                        title: "The VCS Module connection ID to use to perform the check in"
                    },
                    {
                        name: "vcsIds",
                        type: "CSV: id",
                        title: "The list of VCS Module resource ID's to check in"
                    },
                    {
                        name: "pathToVcsXML",
                        type: "file_path",
                        title: "The path to the VCS Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serversXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "vcsUser",
                        type: "string",
                        title: "The user to log into the VCS server with"
                    },
                    {
                        name: "vcsPassword",
                        type: "password",
                        title: "The password to log into the VCS server with"
                    }
                ]
            },
            "vcsForcedCheckin2" : {
                connParamNum: 1,
                idParamNum: null,
                moduleParamNum: 5,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "vcsConnectionId",
                        type: "vcsConnectionId",
                        title: "The VCS Module connection ID to use to perform the check in"
                    },
                    {
                        name: "vcsResourcePath",
                        type: "cis_path",
                        title: "The path in the CIS instance to the resource to force check in"
                    },
                    {
                        name: "vcsResourceType",
                        type: "vcs_type",
                        title: "The VCS resource type"
                    },
                    {
                        name: "vcsMessage",
                        type: "string",
                        title: "The check in message"
                    },
                    {
                        name: "pathToVcsXML",
                        type: "file_path",
                        title: "The path to the VCS Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serversXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "vcsUser",
                        type: "string",
                        title: "The user to log into the VCS server with"
                    },
                    {
                        name: "vcsPassword",
                        type: "password",
                        title: "The password to log into the VCS server with"
                    }
                ]
            },
            "vcsForcedCheckins2" : {
                connParamNum: 1,
                idParamNum: 2,
                moduleParamNum: 3,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "vcsConnectionId",
                        type: "vcsConnectionId",
                        title: "The VCS Module connection ID to use to perform the check in"
                    },
                    {
                        name: "vcsIds",
                        type: "CSV: id",
                        title: "The list of VCS Module resource ID's to force check in"
                    },
                    {
                        name: "pathToVcsXML",
                        type: "file_path",
                        title: "The path to the VCS Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serversXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "vcsUser",
                        type: "string",
                        title: "The user to log into the VCS server with"
                    },
                    {
                        name: "vcsPassword",
                        type: "password",
                        title: "The password to log into the VCS server with"
                    }
                ]
            },
            "vcsPrepareCheckin2" : {
                connParamNum: 1,
                idParamNum: null,
                moduleParamNum: 4,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "vcsConnectionId",
                        type: "vcsConnectionId",
                        title: "The VCS Module connection ID to use to perform the check in"
                    },
                    {
                        name: "vcsResourcePath",
                        type: "cis_path",
                        title: "The path in the CIS instance to the resource to prepare for check in"
                    },
                    {
                        name: "vcsResourceType",
                        type: "vcs_type",
                        title: "The VCS resource type"
                    },
                    {
                        name: "pathToVcsXML",
                        type: "file_path",
                        title: "The path to the VCS Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serversXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "vcsUser",
                        type: "string",
                        title: "The user to log into the VCS server with"
                    },
                    {
                        name: "vcsPassword",
                        type: "password",
                        title: "The password to log into the VCS server with"
                    }
                ]
            },
            "vcsPrepareCheckins2" : {
                connParamNum: 1,
                idParamNum: 2,
                moduleParamNum: 3,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "vcsConnectionId",
                        type: "vcsConnectionId",
                        title: "The VCS Module connection ID to use to perform the check in"
                    },
                    {
                        name: "vcsIds",
                        type: "CSV: id",
                        title: "The list of VCS Module resource ID's to prepare for check in"
                    },
                    {
                        name: "pathToVcsXML",
                        type: "file_path",
                        title: "The path to the VCS Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serversXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "vcsUser",
                        type: "string",
                        title: "The user to log into the VCS server with"
                    },
                    {
                        name: "vcsPassword",
                        type: "password",
                        title: "The password to log into the VCS server with"
                    }
                ]
            },
            "vcsScanPathLength2" : {
                connParamNum: 1,
                idParamNum: null,
                moduleParamNum: 4,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "vcsConnectionId",
                        type: "vcsConnectionId",
                        title: "The VCS Module connection ID to use to perform the generation"
                    },
                    {
                        name: "vcsMaxPathLength",
                        type: "integer",
                        title: "A non-negative integer length from which to compare path lengths found in vcsResourcePathList. When 0, use the default CommonConstants.maxWindowsPathLen=259."
                    },
                    {
                        name: "vcsResourcePathList",
                        type: "CSV: string",
                        title: "A comma separated list of CIS paths to scan"
                    },
                    {
                        name: "pathToVcsXML",
                        type: "file_path",
                        title: "The path to the generated VCS Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    },
                    {
                        name: "vcsUser",
                        type: "string",
                        title: "The user to log into the VCS server with"
                    },
                    {
                        name: "vcsPassword",
                        type: "password",
                        title: "The password to log into the VCS server with"
                    }
                ]
            },
            "generateVCSXML2" : {
                connParamNum: 1,
                idParamNum: null,
                moduleParamNum: 3,
                params: [
                    {
                        name: "server",
                        type: "serverId",
                        title: STANDARD_PARAM_TITLES["serverId"]
                    },
                    {
                        name: "vcsConnectionId",
                        type: "vcsConnectionId",
                        title: "The VCS Module connection ID to use to perform the generation"
                    },
                    {
                        name: "startPath",
                        type: "cis_path",
                        title: "The path in the CIS instance in which to start looking for resources"
                    },
                    {
                        name: "pathToVcsXML",
                        type: "file_path",
                        title: "The path to the generated VCS Module XML file"
                    },
                    {
                        name: "pathToServersXML",
                        type: "serverXML",
                        title: STANDARD_PARAM_TITLES["pathToServersXML"]
                    }
                ]
            }
        }
    }
};

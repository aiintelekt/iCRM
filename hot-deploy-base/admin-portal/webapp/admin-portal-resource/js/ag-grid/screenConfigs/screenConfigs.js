var screen = "";
var gridOptions = null;
var fields = [];

var selectMap = {"Y":"Yes", "N":"No"};
var dataTypesMap = {
                    "int":"Integer",
                    "text":"Text",
                    "select":"Dropdown",
                    "radio":"Radio",
                    "textArea": "Text Area"
                   };
var data = [
            {"fieldName":""},
            {"sequenceNum":""},
            {"fieldService":""},
            {"dataType":""},
            {"isMandatory":""},
            {"isCreate":""},
            {"isView":""},
            {"isEdit":""},
            {"isDisabled":""}
           ];
function addSpecifications(){
    var item = {
    "fieldName":"",
    "sequenceNum":"",
    "fieldService":"",
    "dataType":"",
    "isMandatory":"",
    "isCreate":"",
    "isView":"",
    "isEdit":"",
    "isDisabled":""
    };
    data.push(item);
    gridOptions.api.setRowData(data);
}

function extractValues(mappings){
    return Object.keys(mappings);
}
function lookupValue(mappings, key){
    return mappings[key];
}
function lookupKey(mappings, name){
    for(var key in mappings){
        if(mappings.hasOwnProperty(key)){
            if(name === mappings[key]){
            return key;
            }
        }
    }
}
function numberParser(params) {
    if(isNaN(Number(params.newValue))){
       alert("Please enter number")
       return Number(params.oldValue);
    }
    return Number(params.newValue);
}
function checkDuplicates(params){
    if(fields.includes(params.newValue) && (params.oldValue != params.newValue)){
        alert("Duplicate fields not allowed!");
        return params.oldValue;
    }
    fields.push(params.newValue);
    return params.newValue;
}

function getRowData() {
	var result;
	result = getAjaxResponse(function(agdata) {
		gridOptions.api.setRowData(agdata);
    });
}
function sizeToFit() {
    gridOptions.api.sizeColumnsToFit();
}
function getAjaxResponse(callback) {
    var clsId = $("#clsId").val();
        if (clsId != null && clsId != "" && clsId != undefined) {
            var errorMessage = null;
            var resultData = null;
            $.ajax({
                type: "POST",
                url: "getScreenConfigsThroughLS",
                async: false,
                data: {"clsId" : clsId},
                success: function(result) {
                    var result1 = data[0];
                    if(result[0] != null || result[0] != undefined){
                        errorMessage = result[0].errorMessage;
                        resultData = result[0].errorResult;
                    }
                    if(errorMessage != null || errorMessage != undefined) {
                        showAlert("error", errorMessage);
                        console.log("--errorMessage-----" + errorMessage);
                    }else{
                        data = result;
                        for(var rowCount=0; rowCount<data.length; ++rowCount){
                            fields.push(data[rowCount]["fieldName"]);
                        }
                        callback(result);
                    }
                },
                error: function() {
                    console.log('Error occured');
                    showAlert("error", "Error occured!");
                },
                complete: function() {
                }
            });
        }
        else{
            callback(data);
        }
}




function processRequest(formName, action){
    jQuery("#buttonCSC").attr("disabled","disabled");

    var formId = "#"+formName;
    var clsId = $("#clsId").val();
    var module = $("#module").val();
    var layout = $("#layout").val();
    var screen = $("#screen").val();
    var screenService = $("#screenService").val();
    var requestUri = $("#requestUri").val();

    module = module.replace(/ /g, "");
    if (module == null && module == "" && module == undefined) {
        alert("Module is not valid!");
        jQuery("#buttonCSC").removeAttr('disabled');
        return false;
    }

    layout = layout.replace(/ /g, "");

    if (layout == null && layout == "" && layout == undefined) {
        alert("Layout is not valid!");
        jQuery("#buttonCSC").removeAttr('disabled');
        return false;
    }

    screen = screen.replace(/ /g, "");
    if (screen == null && screen == "" && screen == undefined) {
        alert("Screen is not valid!");
        jQuery("#buttonCSC").removeAttr('disabled');
        return false;
    }
    if(clsId)
        jQuery(formId).append(clsId);

    jQuery(formId).append(module);
    jQuery(formId).append(layout);
    jQuery(formId).append(screen);
    jQuery(formId).append(screenService);
    jQuery(formId).append(requestUri);

    var index=0;
    for(var rowCount=0; rowCount<data.length; ++rowCount){
        var fieldName = data[rowCount]["fieldName"];
        var fieldService = data[rowCount]["fieldService"];
        var sequenceNum = data[rowCount]["sequenceNum"];
        var dataType = data[rowCount]["dataType"];
        var isMandatory = data[rowCount]["isMandatory"];
        var isCreate = data[rowCount]["isCreate"];
        var isView = data[rowCount]["isView"];
        var isEdit = data[rowCount]["isEdit"];
        var isDisabled = data[rowCount]["isDisabled"];

        if(fieldName != undefined && fieldName != ""){

            fieldName = jQuery("<input>").attr("name", "fieldName_o_"+index).val(fieldName);
            sequenceNum = jQuery("<input>").attr("name", "sequenceNum_o_"+index).val(sequenceNum);
            fieldService = jQuery("<input>").attr("name", "fieldService_o_"+index).val(fieldService);
            dataType = jQuery("<input>").attr("name", "dataType_o_"+index).val(dataType);
            isMandatory = jQuery("<input>").attr("name", "isMandatory_o_"+index).val(isMandatory);
            isCreate = jQuery("<input>").attr("name", "isCreate_o_"+index).val(isCreate);
            isView = jQuery("<input>").attr("name", "isView_o_"+index).val(isView);
            isEdit = jQuery("<input>").attr("name", "isEdit_o_"+index).val(isEdit);
            isDisabled = jQuery("<input>").attr("name", "isDisabled_o_"+index).val(isDisabled);

            jQuery(formId).append(DOMPurify.sanitize(fieldName));
            jQuery(formId).append(DOMPurify.sanitize(sequenceNum));
            jQuery(formId).append(DOMPurify.sanitize(fieldService));
            jQuery(formId).append(DOMPurify.sanitize(dataType));
            jQuery(formId).append(DOMPurify.sanitize(isMandatory));
            jQuery(formId).append(DOMPurify.sanitize(isCreate));
            jQuery(formId).append(DOMPurify.sanitize(isView));
            jQuery(formId).append(DOMPurify.sanitize(isEdit));
            jQuery(formId).append(DOMPurify.sanitize(isDisabled));
        }
        index++;
    }
        jQuery(formId).attr("action",action);
        jQuery(formId).submit();


}

var columnDefs = [
	    {
            "headerName":"Field Name",
            "field":"fieldName",
            "cellEditor":"agTextCellEditor",
            "valueParser": checkDuplicates
        },
        {
            "headerName":"Sequence",
            "field":"sequenceNum",
            "cellEditor":"agTextCellEditor",
            "valueParser": numberParser
        },
        {
            "headerName":"Field Service",
            "field":"fieldService",
            "cellEditor":"agTextCellEditor",
        },
        {
            "headerName":"Data Type",
            "field":"dataType",
            "cellEditor":"agSelectCellEditor",
            "cellEditorParams":{values:extractValues(dataTypesMap)},
            "valueFormatter": function(params){
                                return lookupValue(dataTypesMap, params.value);
                              },
            "valueParser": function(params){
                                return lookupKey(dataTypesMap, params.newValue);
                            }
        },
        {
            "headerName":"Is Mandatory",
            "field":"isMandatory",
            "cellEditor":"agSelectCellEditor",
            "cellEditorParams":{values:extractValues(selectMap)},
            "valueFormatter": function(params){
                                return lookupValue(selectMap, params.value);
                              },
            "valueParser": function(params){
                                return lookupKey(selectMap, params.newValue);
                            }
        },
        {
            "headerName":"Is Create",
            "field":"isCreate",
            "cellEditor":"agSelectCellEditor",
            "cellEditorParams":{values:extractValues(selectMap)},
            "valueFormatter": function(params){
                                return lookupValue(selectMap, params.value);
                              },
            "valueParser": function(params){
                                return lookupKey(selectMap, params.newValue);
                            }
        },
        {
            "headerName":"Is View",
            "field":"isView",
            "cellEditor":"agSelectCellEditor",
            "cellEditorParams":{values:extractValues(selectMap)},
            "valueFormatter": function(params){
                                return lookupValue(selectMap, params.value);
                              },
            "valueParser": function(params){
                                return lookupKey(selectMap, params.newValue);
                            }
        },
        {
            "headerName":"Is Edit",
            "field":"isEdit",
            "cellEditor":"agSelectCellEditor",
            "cellEditorParams":{values:extractValues(selectMap)},
            "valueFormatter": function(params){
                                return lookupValue(selectMap, params.value);
                              },
            "valueParser": function(params){
                                return lookupKey(selectMap, params.newValue);
                            }

        },
        {
            "headerName":"Is Disabled",
            "field":"isDisabled",
            "cellEditor":"agSelectCellEditor",
            "cellEditorParams":{values:extractValues(selectMap)},
            "valueFormatter": function(params){
                                return lookupValue(selectMap, params.value);
                              },
            "valueParser": function(params){
                                return lookupKey(selectMap, params.newValue);
                            }
        }
    ];

$(document).ready(function() {
    $("#screen").blur(function() {
        screen = $("#screen").val();
        if(screen != undefined && screen != ""){
            var module = $("#module").val();
            $("#requestUri").val("/"+module+"/control/"+screen);
        }
    });

    $("#myGrid").empty();
    gridOptions = {
        defaultColDef: {
            filter: true,
            sortable: true,
            resizable: true
        },
        columnDefs: columnDefs,
        defaultColDef: {filter:true, width:124, editable:true},
        //rowData: data,
        floatingFilter: false,
        //rowSelection: "multiple",
        editType: "fullRow",
        paginationPageSize: 10,
        domLayout:"autoHeight",
        pagination: true,
        onGridReady: function() {
            sizeToFit();
            getRowData();
        }
    }
    gridOptions.singleClickEdit = true;
    //lookup the container we want the Grid to use
    var eGridDiv = document.querySelector("#myGrid");
    // create the grid passing in the div to use together with the columns & data we want to use
    new agGrid.Grid(eGridDiv, gridOptions);



});




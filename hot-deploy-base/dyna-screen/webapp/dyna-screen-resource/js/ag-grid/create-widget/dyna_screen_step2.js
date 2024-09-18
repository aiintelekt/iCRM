var screen = "";
var gridOptions = null;
var screenConfig="bb";

var yesNoOptions = {
	"Y" : "Yes",
	"N" : "No"
};
var noYesOptions = {
	"N" : "No",
	"Y" : "Yes"
};
var fieldTypes = {
	"TEXT" : "Text",
	"PASSWORD" : "Password",
	"DATE" : "Date",
	"DROPDOWN" : "Dropdown",
	"PICKER" : "Picker",
	"NUMBER" : "Number",
	//"DATE_TIME" : "Date Time",
	"RADIO" : "Radio",
	"TEXT_AREA" : "Text Area",
	"CHECK_BOX" : "Check Box",
	"CURRENCY" : "Currency",
	"HIDDEN" : "Hidden",
	"DISPLAY" : "Display",
	"DATE_RANGE" : "Date Range",
	"TIME" : "Time",
	"DATE_TIME" : "Date Time",
	"CUSTOM_DATE" : "Custom Date",
	"FULL_RICH_TEXT" : "Full Rich Text",
	"LITE_RICH_TEXT" : "Lite Rich Text"
};
var lookupTypes = {
	"" : "Please Select",
	"STATIC_DATA" : "Static Data",
	"DYNAMIC_DATA" : "Dynamic Data",
	"CUSTOM" : "Custom Data",
	//"PICKER_DATA" : "Picker Data"
};

var roleTypeList = {
		"" : "Please Select"
	};

initiate();
function initiate(){
	$.ajax({
		type : "POST",
		url : "getDynamicData",
		async : false,
		data : {
			"filterData" : { lookupFieldFilter: "{ 	\"entity_name\": \"RoleType\", 	\"name_field\": \"description\", 	\"value_field\": \"roleTypeId\" }" }
		},
		success : function(data) {
			if (data.code == 200) {
				//roleTypeList = result.fieldDataList;
				for (var key in data.fieldDataList) {
					if (key) {
						roleTypeList[key] = data.fieldDataList[key];
					}
				}
			} else {
				showAlert ("error", data.message);
			}
		},
		error : function() {
			console.log('Error occured');
			showAlert("error", "Error occured!");
		},
		complete : function() {
		}
	});
}

// populating the rows
var fieldList = [];

function extractValues(mappings) {
	return Object.keys(mappings);
}
function lookupValue(mappings, key) {
	return mappings[key];
}
function lookupKey(mappings, name) {
	for ( var key in mappings) {
		if (mappings.hasOwnProperty(key)) {
			if (name === mappings[key]) {
				return key;
			}
		}
	}
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
	
	var dynaConfigId = $("#dynaConfigId").val();
	var serviceName = $("#serviceName").val();
	
	if (dynaConfigId) {
		$.ajax({
			type : "POST",
			url : "getDynaScreenRenderDetail",
			async : false,
			data : {
				"dynaConfigId" : dynaConfigId
			},
			success : function(data) {
				if (data.code == 200) {
					fieldList = data.screenConfigFieldList;
					screenConfig = data.screenConfig;
					
					$("#componentMountPoint").val(screenConfig.componentMountPoint);
                	$("#screenDisplayName").val(screenConfig.screenDisplayName);
                	$("#layoutType").val(screenConfig.layoutType);
                	$("#securityGroupId").val(screenConfig.securityGroupId);
                	$("#defaultMessage").val(screenConfig.defaultMessage);
                	//$("#isPrimary").val(screenConfig.isPrimary);
                	$("#isDisabledDyna").val(screenConfig.isDisabled);
                	
                	$('.dropdown').dropdown();
                	
					$('.date').datetimepicker({
						useCurrent : false,
						format : 'DD/MM/YYYY'
					});
                	
                	callback(fieldList);
					
				} else {
					showAlert ("error", data.message);
				}
			},
			error : function() {
				console.log('Error occured');
				showAlert("error", "Error occured!");
			},
			complete : function() {
			}
		});
	} else if (serviceName) {
		$.ajax({
			type : "POST",
			url : "getDynaScreenFieldList",
			async : false,
			data : {
				"serviceName" : serviceName
			},
			success : function(result) {
				if (result.code == 200) {
					fieldList = result.screenConfigFieldList;
                	callback(fieldList);
				} else {
					showAlert ("error", data.message);
				}
			},
			error : function() {
				console.log('Error occured');
				showAlert("error", "Error occured!");
			},
			complete : function() {
			}
		});
	} else {
		callback([]);
	}
	
}

var columnDefs = [ {
	"headerName" : "Field ID",
	"field" : "dynaFieldId",
	"cellEditor" : "agTextCellEditor",
}, {
	"headerName" : "Field Name (UILabel)",
	"field" : "fieldName",
	"cellEditor" : "agTextCellEditor",
}, {
	"headerName" : "Label picker",
	//"field" : "isEdit",
	"sortable" : false,
	"filter" : false,
	"editable" : false,
	"cellRenderer" : function(params) {
		var data = "";
		data = data + '<span onClick="openLabelPicker(\''+params.data.fieldName+'\')" class="btn btn-xs btn-primary tooltips m-1 label-picker-btn" title="Edit"> <i class="glyphicon glyphicon-list-alt" aria-hidden="true"></i> </span>';
		return data;
	}

}, {
	"headerName" : "Field Type",
	"field" : "fieldType",
	"cellEditor" : "agSelectCellEditor",
	"cellEditorParams" : {
		values : extractValues(fieldTypes)
	},
	"valueFormatter" : function(params) {
		return lookupValue(fieldTypes, params.value);
	},
	"valueParser" : function(params) {
		return lookupKey(fieldTypes, params.newValue);
	}
}, {
	"headerName" : "Is Required",
	"field" : "isRequired",
	"cellEditor" : "agSelectCellEditor",
	"cellEditorParams" : {
		values : extractValues(noYesOptions)
	},
	"valueFormatter" : function(params) {
		return lookupValue(noYesOptions, params.value);
	},
	"valueParser" : function(params) {
		return lookupKey(noYesOptions, params.newValue);
	}
}, {
	"headerName" : "Is Create",
	"field" : "isCreate",
	"cellEditor" : "agSelectCellEditor",
	"cellEditorParams" : {
		values : extractValues(yesNoOptions)
	},
	"valueFormatter" : function(params) {
		return lookupValue(yesNoOptions, params.value);
	},
	"valueParser" : function(params) {
		return lookupKey(yesNoOptions, params.newValue);
	}
}, {
	"headerName" : "Is View",
	"field" : "isView",
	"cellEditor" : "agSelectCellEditor",
	"cellEditorParams" : {
		values : extractValues(yesNoOptions)
	},
	"valueFormatter" : function(params) {
		return lookupValue(yesNoOptions, params.value);
	},
	"valueParser" : function(params) {
		return lookupKey(yesNoOptions, params.newValue);
	}
}, {
	"headerName" : "Is Edit",
	"field" : "isEdit",
	"cellEditor" : "agSelectCellEditor",
	"cellEditorParams" : {
		values : extractValues(yesNoOptions)
	},
	"valueFormatter" : function(params) {
		return lookupValue(yesNoOptions, params.value);
	},
	"valueParser" : function(params) {
		return lookupKey(yesNoOptions, params.newValue);
	}

}, {
	"headerName" : "Is Disabled",
	"field" : "isDisabled",
	"cellEditor" : "agSelectCellEditor",
	"cellEditorParams" : {
		values : extractValues(noYesOptions)
	},
	"valueFormatter" : function(params) {
		return lookupValue(noYesOptions, params.value);
	},
	"valueParser" : function(params) {
		return lookupKey(noYesOptions, params.newValue);
	}
}, {
	"headerName" : "Sequence Num",
	"field" : "sequenceNum",
	"cellEditor" : "agTextCellEditor",
}, {
	"headerName" : "Default Value",
	"field" : "defaultValue",
	"cellEditor" : "agTextCellEditor",
}, {
	"headerName" : "Field Data Pattern",
	"field" : "fieldDataPattern",
	"cellEditor" : "agTextCellEditor",
}, {
	"headerName" : "Max Length",
	"field" : "maxLength",
	"cellEditor" : "agTextCellEditor",
}, {
	"headerName" : "Lookup Type",
	"field" : "lookupTypeId",
	"cellEditor" : "agSelectCellEditor",
	"cellEditorParams" : {
		values : extractValues(lookupTypes)
	},
	"valueFormatter" : function(params) {
		return lookupValue(lookupTypes, params.value);
	},
	"valueParser" : function(params) {
		return lookupKey(lookupTypes, params.newValue);
	}
}, {
	"headerName" : "Lookup Field Service",
	"field" : "lookupFieldService",
	"cellEditor" : "agTextCellEditor",
}, {
	"headerName" : "Lookup Field Filter",
	"field" : "lookupFieldFilter",
	"cellEditor" : "agTextCellEditor",
}/*, {
	"headerName" : "Lookup Field Service Url",
	"field" : "lookupFieldServiceUrl",
	"cellEditor" : "agTextCellEditor",
}*/, {
	"headerName" : "Field Service",
	"field" : "fieldService",
	"cellEditor" : "agTextCellEditor",
}, {
	"headerName" : "Picker Window Id",
	"field" : "pickerWindowId",
	"cellEditor" : "agTextCellEditor",
}, {
	"headerName" : "Role Type",
	"field" : "roleTypeId",
	"cellEditor" : "agSelectCellEditor",
	"cellEditorParams" : {
		values : extractValues(roleTypeList)
	},
	"valueFormatter" : function(params) {
		return lookupValue(roleTypeList, params.value);
	},
	"valueParser" : function(params) {
		return lookupKey(roleTypeList, params.newValue);
	}
}, 

];

function openLabelPicker(fieldName) {
	//alert(fieldName);
	$('#uiLabelPicker').modal('show');
}

$(document).ready(function() {
	
	var actionMethod;
	$('#create-dyna-screen').on('click', function() {
		actionMethod = "dynaScreenStep2CreateAction";
		$('#mainFrom').submit();
		
	});
	$('#update-dyna-screen').on('click', function() {
		actionMethod = "dynaScreenUpdateAction";
		$('#mainFrom').submit();
		
	});
	
	$('.actions li:last-child a').click(function(){
		actionMethod = "dynaScreenStep2CreateAction";
		$('#mainFrom').submit();
    });
	
	$('#mainFrom').validator().on('submit', function (e) {
		
		if (e.isDefaultPrevented()) {
	    	// handle the invalid form...
			
	  	} else {
	  		e.preventDefault();
	  		
	  		var errorMessage = prepareScreenFields();
	  		if (errorMessage) {
	  			showAlert ("error", errorMessage);
	  		} else {
	  			$.post(actionMethod, $('#mainFrom').serialize(), function(data) {
					
					if (data.code == 200) {
						showAlert ("success", data.message);
						window.location = "updateDynaScreen?dynaConfigId="+data.dynaConfigId;
					} else {
						showAlert ("error", data.message);
					}
						
				});
	  		}
	  		
	  	}
		
	});
	
	$('#add-screen-field-btn').on('click', function() {
		onAddFieldRow();
	});
	
	$('#remove-screen-field-btn').on('click', function() {
		onRemoveFieldSelected();
	});
	
	$('#serviceName').change(function() {
		$("#dynaConfigId").val("");
		loadScreenFieldGrid();
	});
	
});

function loadScreenFieldGrid() {
	
	$("#dyna-field-grid").empty();
	gridOptions = {
		defaultColDef : {
			filter : true,
			sortable : true,
			resizable : true,
			editable : true,
			width : 124,
		},
		columnDefs : columnDefs,
		// rowData: data,
		floatingFilter : true,
		rowSelection: "multiple",
		//editType : "fullRow",
		paginationPageSize : 15,
		domLayout : "autoHeight",
		pagination : true,
		onGridReady : function() {
			//sizeToFit();
			getRowData();
		},
		stopEditingWhenGridLosesFocus: true,
	}

	// lookup the container we want the Grid to use
	var eGridDiv = document.querySelector("#dyna-field-grid");
	// create the grid passing in the div to use together with the columns &
	// data we want to use
	new agGrid.Grid(eGridDiv, gridOptions);
	
}

function prepareScreenFields() {
	$('#dyna-screen-fields').empty();
	
	var isValid = true;
	var errorMessage = "";
	
	gridOptions.api.forEachNode( function(rowNode, index) {
	    console.log('node ' + rowNode.data.fieldName + ' is in the grid');
	    
		var dynaFieldId = prepareData(rowNode.data.dynaFieldId);
		var fieldName = prepareData(rowNode.data.fieldName);
		var fieldType = prepareData(rowNode.data.fieldType);
		var isRequired = prepareData(rowNode.data.isRequired);
		var isCreate = prepareData(rowNode.data.isCreate);
		var isView = prepareData(rowNode.data.isView);
		var isEdit = prepareData(rowNode.data.isEdit);
		var isDisabled = prepareData(rowNode.data.isDisabled);
		var sequenceNum = prepareData(rowNode.data.sequenceNum);
		var defaultValue = prepareData(rowNode.data.defaultValue);
		var fieldDataPattern = prepareData(rowNode.data.fieldDataPattern);
		var maxLength = prepareData(rowNode.data.maxLength);
		var lookupTypeId = prepareData(rowNode.data.lookupTypeId);
		var lookupFieldService = prepareData(rowNode.data.lookupFieldService);
		var lookupFieldFilter = prepareData(rowNode.data.lookupFieldFilter);
		//var lookupFieldServiceUrl = prepareData(rowNode.data.lookupFieldServiceUrl);
		var fieldService = prepareData(rowNode.data.fieldService);
		var pickerWindowId = prepareData(rowNode.data.pickerWindowId);
		var roleTypeId = prepareData(rowNode.data.roleTypeId);
		
		// field validation [start]
		
		if ((fieldType=='DROPDOWN' || fieldType=='RADIO') && lookupTypeId == '') {
			isValid = false;
			errorMessage += 'Lookup type empty# '+fieldName+"</br>";
		}
		if (fieldType=='DROPDOWN' && lookupTypeId == 'DYNAMIC_DATA' && (lookupFieldService=='' || lookupFieldFilter=='')) {
			isValid = false;
			errorMessage += 'Dropdown as dynamic data, Lookup field service & lookup field filter cant empty# '+fieldName;
		}
		if (fieldType=='PICKER' && pickerWindowId == '') {
			isValid = false;
			errorMessage += 'Picker window ID empty# '+fieldName;
		}
		
		// field validation [end]
		
		// initiate data [start]
		
		if (lookupTypeId == 'STATIC_DATA') {
			lookupFieldService = ''; lookupFieldFilter = ''; pickerWindowId = '';
		} else if (lookupTypeId == 'DYNAMIC_DATA') {
			pickerWindowId = '';
		} 
		
		if (fieldType=='PICKER') {
			lookupTypeId = ''; lookupFieldService = ''; lookupFieldFilter = '';
		} else if (fieldType=='TEXT') {
			lookupTypeId = ''; lookupFieldService = ''; lookupFieldFilter = ''; pickerWindowId = '';
		} 
		
		// initiate data [end]
		
		if (dynaFieldId && isValid) {
			dynaFieldId = "<input type='hidden' name='dynaFieldId' value='"+dynaFieldId+"'>";
			fieldName = "<input type='hidden' name='fieldName' value='"+fieldName+"'>";
			fieldType = "<input type='hidden' name='fieldType' value='"+fieldType+"'>";
			isRequired = "<input type='hidden' name='isRequired' value='"+isRequired+"'>";
			isCreate = "<input type='hidden' name='isCreate' value='"+isCreate+"'>";
			isView = "<input type='hidden' name='isView' value='"+isView+"'>";
			isEdit = "<input type='hidden' name='isEdit' value='"+isEdit+"'>";
			isDisabled = "<input type='hidden' name='isDisabled' value='"+isDisabled+"'>";
			sequenceNum = "<input type='hidden' name='sequenceNum' value='"+sequenceNum+"'>";
			defaultValue = "<input type='hidden' name='defaultValue' value='"+defaultValue+"'>";
			fieldDataPattern = "<input type='hidden' name='fieldDataPattern' value='"+fieldDataPattern+"'>";
			maxLength = "<input type='hidden' name='maxLength' value='"+maxLength+"'>";
			lookupTypeId = "<input type='hidden' name='lookupTypeId' value='"+lookupTypeId+"'>";
			lookupFieldService = "<input type='hidden' name='lookupFieldService' value='"+lookupFieldService+"'>";
			lookupFieldFilter = "<input type='hidden' name='lookupFieldFilter' value='"+lookupFieldFilter+"'>";
			//lookupFieldServiceUrl = "<input type='hidden' name='lookupFieldServiceUrl' value='"+lookupFieldServiceUrl+"'>";
			fieldService = "<input type='hidden' name='fieldService' value='"+fieldService+"'>";
			pickerWindowId = "<input type='hidden' name='pickerWindowId' value='"+pickerWindowId+"'>";
			roleTypeId = "<input type='hidden' name='roleTypeId' value='"+roleTypeId+"'>";
			
			$('#dyna-screen-fields').append(dynaFieldId);
			$('#dyna-screen-fields').append(fieldName);
			$('#dyna-screen-fields').append(fieldType);
			$('#dyna-screen-fields').append(isRequired);
			$('#dyna-screen-fields').append(isCreate);
			$('#dyna-screen-fields').append(isView);
			$('#dyna-screen-fields').append(isEdit);
			$('#dyna-screen-fields').append(isDisabled);
			$('#dyna-screen-fields').append(sequenceNum);
			$('#dyna-screen-fields').append(defaultValue);
			$('#dyna-screen-fields').append(fieldDataPattern);
			$('#dyna-screen-fields').append(maxLength);
			$('#dyna-screen-fields').append(lookupTypeId);
			$('#dyna-screen-fields').append(lookupFieldService);
			$('#dyna-screen-fields').append(lookupFieldFilter);
			//$('#dyna-screen-fields').append(lookupFieldServiceUrl);
			$('#dyna-screen-fields').append(fieldService);
			$('#dyna-screen-fields').append(pickerWindowId);
			$('#dyna-screen-fields').append(roleTypeId);
			
		}
		
	});
	
	return errorMessage;
}

function onAddFieldRow() {
	
	var selectedNode = null;
	var selectedIndexPosition = 0;
	var selectedNodes = gridOptions.api.getSelectedNodes();
	if (selectedNodes && selectedNodes.length > 0) {
		selectedNode = selectedNodes[0];
	}
	
	if (selectedNode) {
		selectedIndexPosition = selectedNode.rowIndex;
	}
	
    var newItem = {};
    var res = gridOptions.api.updateRowData({
        add: [newItem],
        addIndex: selectedIndexPosition+1
    });
    //printResult(res);
}

function onRemoveFieldSelected() {
    var selectedData = gridOptions.api.getSelectedRows();
    console.log(selectedData.length);
    for (i = 0; i <= selectedData.length; i++) {
    }
    var res = gridOptions.api.updateRowData({
        remove: selectedData
    });

    //printResult(res);
}

function printResult(res) {
    console.log('---------------------------------------')
    if (res.add) {
        res.add.forEach(function(rowNode) {
            console.log('Added Row Node', rowNode);
        });
    }
    if (res.remove) {
        res.remove.forEach(function(rowNode) {
            console.log('Removed Row Node', rowNode);
        });
    }
}

function prepareData(data) {
	if (data) {
		return data;
	}
	return "";
}

function setUiLabelPickerWindowValue(selectedVal) {
	
	var selectedNodes = gridOptions.api.getSelectedNodes();
	
	selectedNodes[0].setDataValue("fieldName", selectedVal);
	
	$('#uiLabelPicker').modal('hide');
}

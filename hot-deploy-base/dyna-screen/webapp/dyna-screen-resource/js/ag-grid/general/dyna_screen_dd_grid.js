var gridFieldDataOptions = null;

// populating the rows
var fieldDataList = [];

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

function getRowFieldData() {
	var result;
	result = getAjaxResponseFieldData(function(agdata) {
		gridFieldDataOptions.api.setRowData(agdata);
	});
}
function sizeToFitFieldData() {
	gridFieldDataOptions.api.sizeColumnsToFit();
}
function getAjaxResponseFieldData(callback) {
	
	var dynaConfigId = $("#dynaConfigId").val();
	var selectedDynaFieldId = $("#selectedDynaFieldId").val();
	
	if (dynaConfigId && selectedDynaFieldId) {
		$.ajax({
			type : "POST",
			url : "getDynaFieldRenderDetail",
			async : false,
			data : {
				"dynaConfigId" : dynaConfigId,
				"dynaFieldId" : selectedDynaFieldId
			},
			success : function(data) {
				if (data.code == 200) {
					fieldDataList = data.fieldDatas;
					callback(fieldDataList);
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
	
}

var columnDefsFieldData = [ {
	"headerName" : "Data Name",
	"field" : "dataName",
	"cellEditor" : "agTextCellEditor",
}, {
	"headerName" : "Data Value",
	"field" : "dataValue",
	"cellEditor" : "agTextCellEditor",
}, {
	"headerName" : "Sequence Num",
	"field" : "sequenceNum",
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
}

];

$(document).ready(function() {
	
	$('#update-field-data-btn').on('click', function() {
		
		prepareScreenFieldDatas();
		
		$.post('fieldDataUpdateAction', $('#field-data-form').serialize(), function(data) {
			
			if (data.code == 200) {
				showAlert ("success", "Successfully updated field data");
				//window.location = "dynaScreenFind";
				$('#data-modal-view').modal("hide");
			} else {
				showAlert ("error", data.message);
			}
				
		});
		
	});
	
	$('#add-field-data-btn').on('click', function() {
		onAddFieldDataRow();
	});
	
	$('#remove-field-data-btn').on('click', function() {
		onRemoveFieldDataSelected();
	});

});

function loadFieldDataGrid() {
	
	$("#dyna-field-data-grid").empty();
	gridFieldDataOptions = {
		defaultColDef : {
			filter : true,
			sortable : true,
			resizable : true,
			width : 124,
			editable : true
		},
		columnDefs : columnDefsFieldData,
		// rowData: data,
		floatingFilter : true,
		rowSelection: "multiple",
		//editType : "fullRow",
		paginationPageSize : 10,
		domLayout : "autoHeight",
		pagination : false,
		onGridReady : function() {
			sizeToFitFieldData();
			getRowFieldData();
		},
		stopEditingWhenGridLosesFocus: true,
	}

	// lookup the container we want the Grid to use
	var eGridDiv = document.querySelector("#dyna-field-data-grid");
	// create the grid passing in the div to use together with the columns &
	// data we want to use
	new agGrid.Grid(eGridDiv, gridFieldDataOptions);
}

function prepareScreenFieldDatas() {
	$('#dyna-screen-field-datas').empty();
	
	gridFieldDataOptions.api.forEachNode( function(rowNode, index) {
	    console.log('node ' + rowNode.data.fieldName + ' is in the grid');
		
		var dataName = prepareData(rowNode.data.dataName);
		var dataValue = prepareData(rowNode.data.dataValue);
		var sequenceNum = prepareData(rowNode.data.sequenceNum);
		var roleTypeId = prepareData(rowNode.data.roleTypeId);
		
		if (dataName && dataValue) {
			dataName = "<input type='hidden' name='dataName' value='"+dataName+"'>";
			dataValue = "<input type='hidden' name='dataValue' value='"+dataValue+"'>";
			sequenceNum = "<input type='hidden' name='sequenceNum' value='"+sequenceNum+"'>";
			roleTypeId = "<input type='hidden' name='roleTypeId' value='"+roleTypeId+"'>";
			
			$('#dyna-screen-field-datas').append(dataName);
			$('#dyna-screen-field-datas').append(dataValue);
			$('#dyna-screen-field-datas').append(sequenceNum);
			$('#dyna-screen-field-datas').append(roleTypeId);
			
		}
		
	});
}

function onAddFieldDataRow() {
	
	var selectedNode = null;
	var selectedIndexPosition = 0;
	var selectedNodes = gridFieldDataOptions.api.getSelectedNodes();
	if (selectedNodes && selectedNodes.length > 0) {
		selectedNode = selectedNodes[0];
	}
	
	if (selectedNode) {
		selectedIndexPosition = selectedNode.rowIndex;
	}
	
    var newItem = {};
    var res = gridFieldDataOptions.api.updateRowData({
        add: [newItem],
        addIndex: selectedIndexPosition+1
    });
	
}

function onRemoveFieldDataSelected() {
    var selectedData = gridFieldDataOptions.api.getSelectedRows();
    console.log(selectedData.length);
    for (i = 0; i <= selectedData.length; i++) {
    }
    var res = gridFieldDataOptions.api.updateRowData({
        remove: selectedData
    });
}

function prepareData(data) {
	if (data) {
		return data;
	}
	return "";
}

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
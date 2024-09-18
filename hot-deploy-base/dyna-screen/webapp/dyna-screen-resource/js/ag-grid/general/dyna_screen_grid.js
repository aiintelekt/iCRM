$("#find-dyna-screen-form").on("keypress", function(event) {
	var keyPressed = event.keyCode || event.which;
	if (keyPressed === 13) {
		loadScreenGrid();
		event.preventDefault();
		return false;
	}
});
$(function() {
	loadScreenGrid();
});

var columnDynaScreenDefs = [
{
	"headerName" : "Config Id",
	"field" : "dynaConfigId",
	"sortable" : true,
	"filter" : true,
	"lockPosition": true,
	"checkboxSelection": true,
	"headerCheckboxSelection": true,
}, {
	"headerName" : "Dyna Name",
	"field" : "screenDisplayName",
	"sortable" : true,
	"filter" : true,
}, {
	"headerName" : "Primary",
	"field" : "isPrimary",
	"sortable" : true,
	"filter" : true,
}, {
	"headerName" : "From Date",
	"field" : "fromDate",
	"sortable" : true,
	"filter" : true,
}, {
	"headerName" : "Thru Date",
	"field" : "thruDate",
	"sortable" : true,
	"filter" : true,
}, {
	"headerName" : "Layout Type",
	"field" : "layoutType",
	"sortable" : true,
	"filter" : true,
},  {
	"headerName" : "Component Id",
	"field" : "componentMountPoint",
	"sortable" : true,
	"filter" : true,
}, {
	"headerName" : "Action",
	//"field" : "isEdit",
	"sortable" : false,
	"filter" : false,
	cellRenderer : function(params) {
		var data = "";
		data = data + '<a href="/dyna-screen/control/updateDynaScreen?dynaConfigId='+params.data.dynaConfigId+'" class="btn btn-xs btn-primary tooltips m-1" title="Edit" target="_blank"> <i class="fa fa-edit" aria-hidden="true"></i> </a>';
		data = data + '<a href="/dyna-screen/control/previewDynaScreen?dynaConfigId='+params.data.dynaConfigId+'" class="btn btn-xs btn-primary tooltips m-1" title="Preview" target="_blank"> <i class="fa fa-eye" aria-hidden="true"></i> </a>';
		
		return data;
	}

}

];

$("#find-dyna-screen-form").on("keypress", function (event) {
    var keyPressed = event.keyCode || event.which; 
    if (keyPressed === 13) { 
    	event.preventDefault(); 
    	loadScreenGrid(); 
        return false; 
    } 
});
$(document).ready(function() {
	$('#remove-screen-btn').on('click', function() {

		var selectedData = gridOptionsDynaScreen.api.getSelectedRows();
		if (selectedData.length > 0) {
			console.log(selectedData);
		    var selectedDynaConfigIds = "";
		    for (i = 0; i < selectedData.length; i++) {
		    	var data = selectedData[i];
		    	selectedDynaConfigIds += data.dynaConfigId+",";
		    }
		    selectedDynaConfigIds = selectedDynaConfigIds.substring(0, selectedDynaConfigIds.length - 1);
		    //alert(selectedDynaConfigIds);
		    
		    var inputData = {"dynaConfigIds": selectedDynaConfigIds};
		    
		    $.ajax({
				type : "POST",
				url : "removeDynaConfiguration",
				async : false,
				data : inputData,
				success : function(data) {
					if (data.code == 200) {
						showAlert ("success", "Successfully removed dyna configuration# "+selectedDynaConfigIds);
						getDynaScreenRowData();
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
			showAlert("error", "Please select atleast one row to be removed!");
		}
		
	});
	
});

var gridOptionsDynaScreen = null;
function loadScreenGrid() {
	$("#dyna-screen-grid").empty();
	gridOptionsDynaScreen = {
		defaultColDef : {
			filter : true,
			sortable : true,
			resizable : true
		},
		columnDefs : columnDynaScreenDefs,
		floatingFilter : true,
		rowSelection : "multiple",
		editType : "fullRow",
		paginationPageSize : 10,
		domLayout : "autoHeight",
		pagination : true,
		"custom": {
			"dataUniqueIdField": "dynaConfigId",
			"rowSelection": "multiple"
		},
		onGridReady : function() {
			sizeToFitDynaScreen();
			getDynaScreenRowData();
		}
	}

	// lookup the container we want the Grid to use
	var eGridDiv = document.querySelector("#dyna-screen-grid");
	// create the grid passing in the div to use together with the columns &
	// data we want to use
	new agGrid.Grid(eGridDiv, gridOptionsDynaScreen);

}

function getDynaScreenAjaxResponse(callback) {
	var result = [];
	var resultRes = null;
	var params = {}
	
	var inputData = {};
	var componentMountPoint = $("#componentMountPoint").val(); 
	var layoutType = $("#layoutType").val();
	var isPrimary = $("#isPrimary").val();
	
	//inputData = {"componentMountPoint": componentMountPoint, "layoutType": layoutType, "isPrimary": isPrimary};
	
	var errorMessage = null;
	var resultData = null;
	$.ajax({
		type : "POST",
		url : "searchDynaScreenConfigurations",
		async : false,
		data: JSON.parse(JSON.stringify($("#find-dyna-screen-form").serialize())),
		success : function(data) {
			if (data.code != 200) {
				showAlert("error", data.message);
			}
			callback(data.data);
		},
		error : function() {
			console.log('Error occured');
			showAlert("error", "Error occured!");
			callback(result);
		},
		complete : function() {
		}
	});

}

function sizeToFitDynaScreen() {
	gridOptionsDynaScreen.api.sizeColumnsToFit();
}

function getDynaScreenRowData() {
	var result;
	result = getDynaScreenAjaxResponse(function(agdata) {
		gridOptionsDynaScreen.api.setRowData(agdata);
	});
}

function onRemoveConfigSelected() {
    var selectedData = gridOptionsDynaScreen.api.getSelectedRows();
    console.log(selectedData);
    for (i = 0; i <= selectedData.length; i++) {
    }
    var res = gridOptionsDynaScreen.api.updateRowData({
        remove: selectedData
    });

    //printResult(res);
}

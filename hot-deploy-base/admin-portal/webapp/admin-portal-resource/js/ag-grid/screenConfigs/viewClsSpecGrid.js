

var columnClsSpecDefs = [{ 
	"headerName":"Field Id",
	"field":"fieldId",
	"sortable":true,
	"filter":true,
},
{ 
	"headerName":"Field Name",
	"field":"fieldName",
	"sortable":true,
	"filter":true,
},
{ 
	"headerName":"Sequence Num",
	"field":"sequenceNum",
	"sortable":true,
	"filter":true,
},
{ 
	"headerName":"Is Disabled",
	"field":"isDisabled",
	"sortable":true,
	"filter":true,
},
{ 
	"headerName":"Is Mandatory",
	"field":"isMandatory",
	"sortable":true,
	"filter":true,
},
{ 
	"headerName":"Data Type",
	"field":"dataType",
	"sortable":true,
	"filter":true,
},
{ 
	"headerName":"Is Create",
	"field":"isCreate",
	"sortable":true,
	"filter":true,
},
{ 
	"headerName":"Is View",
	"field":"isView",
	"sortable":true,
	"filter":true,
},
{ 
	"headerName":"Is Edit",
	"field":"isEdit",
	"sortable":true,
	"filter":true,
},
{ 
	"headerName":"Field Service",
	"field":"fieldService",
	"sortable":true,
	"filter":true,
}

];

var gridOptionsClsSpec = null;
function loadClsSpecGrid(){
	$("#viewClsSpecGrid").empty();
	gridOptionsClsSpec = {
			defaultColDef: {
				filter: true,
				sortable: true,
				resizable: true
			},
			columnDefs: columnClsSpecDefs,
			floatingFilter: true,
			rowSelection: "multiple",
			editType: "fullRow",
			paginationPageSize: 10,
			domLayout:"autoHeight",
			pagination: true,
			onGridReady: function() {
				sizeToFitClsSpec();
				getClsSpecRowData();
			}
	}

	//lookup the container we want the Grid to use
	var eGridDiv = document.querySelector("#viewClsSpecGrid");
	// create the grid passing in the div to use together with the columns & data we want to use
	new agGrid.Grid(eGridDiv, gridOptionsClsSpec);

}


function getClsSpecAjaxResponse(callback) {
	var result = [];
	var resultRes = null;
	var params = {}
	var inputData = {};
	var clsId = $("#clsId").val();
	dataSet = {"clsId" : clsId};

	var errorMessage = null;
	var resultData = null;
	$.ajax({
		type: "POST",
		url: "getClsSpecificationDetails",
		async: false,
		data: dataSet,
		success: function(data) {
			var result1 = data[0];
			if(data[0] != null || data[0] != undefined){
				errorMessage = data[0].errorMessage;
				resultData = data[0].errorResult;
			}
			if(errorMessage != null || errorMessage != undefined) {
				showAlert("error", errorMessage);
				console.log("--errorMessage-----" + errorMessage);
				callback(resultData);
			}else{
				callback(data.data);
			}

		},
		error: function() {
			console.log('Error occured');
			showAlert("error", "Error occured!");
			callback(result);
		},
		complete: function() {
		}
	});

}

function getClsSpecRowData() {
	var result;
	result = getClsSpecAjaxResponse(function(agdata) {
		gridOptionsClsSpec.api.setRowData(agdata);
	});
}

function sizeToFitClsSpec() {
	gridOptionsClsSpec.api.sizeColumnsToFit();
}







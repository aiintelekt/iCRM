$(function() {
	loadAlertGrid();
});

var columnAlertGridDefs = [{ 
	"headerName":"Opportunity Number",
	"field":"salesOpportunityId",
	"sortable":true,
	"filter":true,
	"filter":"agTextColumnFilter",
	 cellRenderer : function(params){
		return `<a href="viewOpportunity?salesOpportunityId=${params.data.salesOpportunityId}">${params.value}</a>` 
	 }
},
{ 
	"headerName":"Call Outcome",
	"field":"callOutCome",
	"sortable":true,
	"filter":true,
	"filter":"agTextColumnFilter"
},
{ 
	"headerName":"Response Type",
	"field":"responseType",
	"sortable":true,
	"filter":true,
	"filter":"agTextColumnFilter"
},
{ 
	"headerName":"Status",
	"field":"opportunityStatusId",
	"sortable":true,
	"filter":true,
	"filter":"agTextColumnFilter"
},
{ 
	"headerName":"Status Reason",
	"field":"responseReasonId",
	"sortable":true,
	"filter":true,
	"filter":"agTextColumnFilter"
},
{ 
	"headerName":"Remark",
	"field":"remarks",
	"sortable":true,
	"filter":true,
	"filter":"agTextColumnFilter"
},
{ 
	"headerName":"Owner",
	"field":"assignedUserLoginId",
	"sortable":true,
	"filter":true,
	"filter":"agTextColumnFilter"
},
{ 
	"headerName":"Business Unit",
	"field":"businessUnitName",
	"sortable":true,
	"filter":true,
	"filter":"agTextColumnFilter"
},
{ 
	"headerName":"Created On",
	"field":"createdOn",
	"sortable":true,
	"filter":true,
	"filter":"agTextColumnFilter"
},
{ 
	"headerName":"Created By",
	"field":"createdByUserLogin",
	"sortable":true,
	"filter":true,
	"filter":"agTextColumnFilter"
}

];

var gridAlertOptions = null;
function loadAlertGrid(){
	$("#alertGrid").empty();
	gridAlertOptions = {
			defaultColDef: {
				filter: true,
				sortable: true,
				resizable: true
			},
			columnDefs: columnAlertGridDefs,
			floatingFilter: true,
			rowSelection: "multiple",
			editType: "fullRow",
			paginationPageSize: 10,
			domLayout:"autoHeight",
			pagination: true,
			onGridReady: function() {
				sizeToFitAlertGrid();
				getAlertRowData();
			}
	}

	//lookup the container we want the Grid to use
	var eGridDiv = document.querySelector("#alertGrid");
	// create the grid passing in the div to use together with the columns & data we want to use
	new agGrid.Grid(eGridDiv, gridAlertOptions);

}


function getAjaxResponseAlertGrid(callback) {
	var data1;
	var result = [];
	var resultRes = null;
	var params = {}
	var paramStr = {};
	var errorMessage = null;
	var resultData = null;

	var salesOpportunityId = jQuery('#alertEntityReferenceId').val();

	paramStr = {"salesOpportunityId" : salesOpportunityId};
	if(salesOpportunityId != null && salesOpportunityId != undefined && salesOpportunityId != ""){
		$.ajax({
			type: "POST",
			url: "getOpportunity",
			async: false,
			data: paramStr,
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
}

function getAlertRowData() {
	var result;
	result = getAjaxResponseAlertGrid(function(agdata) {
		gridAlertOptions.api.setRowData(agdata);
	});
}

function sizeToFitAlertGrid() {
	gridAlertOptions.api.sizeColumnsToFit();
}


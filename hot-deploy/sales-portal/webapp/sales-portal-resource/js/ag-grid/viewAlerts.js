$(function() {
	loadAgGridViewAlerts();
});

var columnDefsViewAlerts = [
	{ 
		"headerName":"Alert ID",
		"field":"alertTrackingId",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter",
		cellRenderer: function(params) {
			return  '<a target="_blank" href="/sales-portal/control/viewCustomerAlerts?alertTrackingId='+params.data.alertTrackingId+'">'+params.data.alertTrackingId+'</a>';
		}
	},
	{ 
		"headerName":"Category",
		"field":"alertCategoryDesc",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Priority",
		"field":"priorityDesc",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Message",
		"field":"alertInfo",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	}

	];

var gridAlertViewOptions = null;
function loadAgGridViewAlerts(){
	$("#viewAlertsGrid").empty();
	gridAlertViewOptions = {
			defaultColDef: {
				filter: true,
				sortable: true,
				resizable: true
			},
			columnDefs: columnDefsViewAlerts,
			floatingFilter: true,
			rowSelection: "multiple",
			editType: "fullRow",
			paginationPageSize: 10,
			domLayout:"autoHeight",
			pagination: true,
			onGridReady: function() {
				sizeToFitAlertView();
				getRowDataViewAlerts();
			}
	}

	//lookup the container we want the Grid to use
	var eGridDiv = document.querySelector("#viewAlertsGrid");
	// create the grid passing in the div to use together with the columns & data we want to use
	new agGrid.Grid(eGridDiv, gridAlertViewOptions);

}


function getAjaxResponseViewAlerts(callback) {
	var data1;
	var result = [];
	var resultRes = null;
	var params = {}
	var customerCin = jQuery('#customerCin').val();
	params = {"customerCin": customerCin, "alertPriority" : "HIGH"};

	var errorMessage = null;
	var resultData = null;
	
	if(customerCin == null || customerCin == '' || customerCin == 'undefined'){
		callback(result);
	}
	else if(customerCin != "" && customerCin != null && customerCin != undefined){
		$.ajax({
		type: "POST",
		url: "getCustomerAlertDetails",
		async: true,
		data: params,
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
				callback(data);
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

function getRowDataViewAlerts() {
	var result;
	result = getAjaxResponseViewAlerts(function(agdata) {
		gridAlertViewOptions.api.setRowData(agdata);
	});
}

function sizeToFitAlertView() {
	gridAlertViewOptions.api.sizeColumnsToFit();
}



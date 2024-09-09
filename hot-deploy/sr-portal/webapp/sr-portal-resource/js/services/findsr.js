$(function() {
loadsr();
});

var columnDefs234 = [
	{
		"headerName":"Activity Number",
		"field":"workEffortId",
		checkboxSelection:true,
		cellRenderer: params => `<a href="viewActivity?workEffortId=${params.data.workEffortId}">${params.value}</a>`
		},
		{
		"headerName":"ParentActivity",
		"field":"ParentActivity"
		},
		{
		"headerName" : "Activity Type",
		"field" : "workEffortServiceTypeDescription"
		},  
		{
		"headerName" : "Activity Sub Type",
		"field" : "workEffortSubServiceTypeDescription"
		},
		{
		"headerName" : "CIF ID",
		"field" : "CIFID"
		},

		{
		"headerName" : "Customer Name",
		"field" : "CustomerName"
		},
		{
		"headerName" : "Customer Type",
		"field" : "CustomerType"
		},

		{
		"headerName" : "Subject",
		"field" : "Subject"
		},
		{
		"headerName" : "Account#",
		"field" : "accountNumber"
		},
		{
		"headerName" : "Business Unit",
		"field" : "businessUnitId"
		},
		{
		"headerName":"Planned Start",
		"field":"estimatedStartDate",
		"filter": true,
		cellStyle: function() {
			return{textAlign:"right"};
	    }
		},
		{
		"headerName":"Campaign Code",
		"field":"campaignCode"
		},

		{
		"headerName":"Product Name",
		"field":"productName"
		},
		{
		"headerName":"Type",
		"field":"Type"
		},
		{
		"headerName":"Sub Type",
		"field":"SubType"
		},
		{
		"headerName":"Source",
		"field":"source"
		},
		{
		"headerName":"Instruction/Comments",
		"field":"Comments"
		},
		{
		"headerName":"Planned Duration",
		"field":"duration"
		},
		{
		"headerName":"Planned Due",
		"field":"estimatedCompletionDate"
		},
		{
		"headerName":"Actual Completion",
		"field":"actualCompletionDate"
		},
		{
		"headerName":"OnceandDone",
		"field":"wfOnceDone"
		},        
		{
		"headerName":"Activity Status",
		"field":"currentStatusId"
		},
		{
		"headerName":"Owner",
		"field":"primOwnerId"
		        
		},
		{
		"headerName":"Business Unit Name",
		"field":"businessUnitName"
		},
		{
		"headerName":"Created by From iServe",
		"field":"CreatedbyFromiserve"
		},
		{
		"headerName":"LastUpdated By From iServe",
		"field":"LatestUpdatedbyFromiserve"
		},
		{
		"headerName":"Overdue",
		"field":"overDue"
		},
		{
		"headerName":"Resolution",
		"field":"resolution"
		},
		{
		"headerName":"Actual Start",
		"field":"actualStartDate"
		},
		{
		"headerName":"Actual Duration(Days)",
		"field":"actualDuration"
		},
		{
		"headerName":"Created On",
		"field":"createdDate"
		},
		{
		"headerName":"Created By",
		"field":"createdByUserLogin"
		},
		{
		"headerName":"Modified On",
		"field":"lastModifiedDate",
		"filter": true,
		cellStyle: function() {
			return{textAlign:"right"};
	    }
		},

		{
		"headerName":"Modified By",
		"field":"lastModifiedByUserLogin"
		}



		];



var gridOption= null;
function  loadsr(){

$("#sractivity").empty();
gridOptions = {
defaultColDef: {
filter: true,
sortable: true,
resizable: true
},
columnDefs: columnDefs234,
// rowData: getRowData(),
floatingFilter: true,
rowSelection: "multiple",
editType: "fullRow",
paginationPageSize: 10,
domLayout:"autoHeight",
pagination: true,
onGridReady: function() {
getRowData();
}
}

// lookup the container we want the Grid to use
var eGridDiv = document.querySelector("#sractivity");
// create the grid passing in the div to use together with the columns &
// data we want to use
new agGrid.Grid(eGridDiv, gridOptions);

}


function getAjaxResponse(callback) {
var data1;
var result = [];
var resultRes = null;
// var params = {}
 var paramStr = $("#findactivity").serialize();
 var formData = JSON.stringify(paramStr);
 var errorMessage = null;
 var resultData = null;
	$.ajax({
		type: "POST",
		url: "getActivity",
		async: false,
		data:paramStr,
		success: function(data) {
		var result1 = data[0];
		if(data[0] != null || data[0] != undefined){
		errorMessage = data[0].errorMessage;
		resultData = data[0].errorResult;
		}
		if(errorMessage != null || errorMessage != undefined) {
		showAlert("error", errorMessage);
		callback(resultData);
		}else{
		callback(data);
		
		}
		},
		error: function() {
		showAlert("error", "Error occured!");
		callback(result);
		},
		complete: function() {
		}
	});
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

function getSelectedRows() {
    var selectedRows;
    selectedRows = this.gridOptions.api.getSelectedRows();
    selectedRows.map((row)=>{
    });
    return selectedRows
}
function onBtExport() {
	var params = {
	skipHeader: false,
	allColumns: true,
	fileName: "Findactivity",
	exportMode: 'csv'
	};


	gridOptions.api.exportDataAsCsv(params);
	}
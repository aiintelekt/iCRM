$(function() {
loadgrid();
});

var columnDefs= [
	{
		"headerName":"CSAT ID",
		"field":"csatid",
		checkboxSelection:true,
		
		},
		{
		"headerName":"SR Number",
		"field":"externalId"
		},
		{
		"headerName" : "Customer/Prospect Name",
		"field" : "workEffortServiceType"
		},  
		{
		"headerName" : "SR Type",
		"field" : "workEffortSubServiceType"
		},
		{
		"headerName" : "SR Category",
		"field" : "srCategoryId"
		},

		{
		"headerName" : "Was the Service Agent/RM Courteous?",
		"field" : "Customer Name"
		},
		{
		"headerName" : "Was the Service  Provided Timely?",
		"field" : "Customer Type"
		},

		{
		"headerName" : "Was the Service Agent/RM Knowledgeable?",
		"field" : "Subject"
		},
		{
		"headerName" : "Was the Problem solved to your satisfaction?",
		"field" : "accountNumber"
		},
		{
		"headerName" : "Overall Satisfaction on DBS handling your problem?",
		"field" : "businessUnitId"
		},
		{
		"headerName":"CSAT Overdue Date",
		"field":"estimatedStartDate"
		},
		{
		"headerName":"Status Reason",
		"field":"Campaigncode"
		},

		{
		"headerName":"Ref Number",
		"field":"Productname"
		},
		{
		"headerName":"Owner",
		"field":"Type"
		},
		
		{
		"headerName":"Duration",
		"field":"Source"
		},
		{
		"headerName":"Due Date",
		"field":"Duedate"
		},
		{
		"headerName":"Owner BU",
		"field":"duration"
		},
		
		{
		"headerName":"Dateclosed",
		"field":"OnceandDone"
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
		"field":"lastModifiedDate"
		},
		
		{
		"headerName":"Modified By",
		"field":"lastModifiedByUserLogin"
		}
		];



var gridOption= null;
function  loadgrid(){

$("#grid").empty();
gridOptions = {
defaultColDef: {
filter: true,
sortable: true,
resizable: true
},
columnDefs: columnDefs,
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
var eGridDiv = document.querySelector("#grid");
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



 
 var externalId = jQuery('#externalId').val();
 var businessUnitName = jQuery('#businessUnitName').val();
 var actualStartDate = jQuery('#actualStartDate').val();
 var actualEndDate = jQuery('#actualEndDate').val();
 
 var workEffortServiceType = jQuery('#workEffortServiceType').val();
 var workEffortSubServiceType = jQuery('#workEffortSubServiceType').val();
 var currentStatusId = jQuery('#currentStatusId').val();
	
 dataSet = {
  "externalId" : externalId,
  "businessUnitName": businessUnitName,
  "actualStartDate": actualStartDate,
  "actualEndDate": actualEndDate,
  "workEffortServiceType": workEffortServiceType,
  "workEffortSubServiceType":workEffortSubServiceType,
  "currentStatusId":currentStatusId
 };
	
	
	
 var formData = JSON.stringify(paramStr);
 var errorMessage = null;
 var resultData = null;
	$.ajax({
		type: "POST",
		url: "",
		async: false,
		//data: {"start": 0, "length":1000},
		//data: $("#findactivity").serialize(),
		data:dataSet,
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
		// $('#loader').hide();
		}
	});
// }

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

function onBtExport() {
	var params = {
	skipHeader: false,
	allColumns: true,
	fileName: "Findcustsatisfaction",
	exportMode: 'csv'
	};


	gridOptions.api.exportDataAsCsv(params);
	}
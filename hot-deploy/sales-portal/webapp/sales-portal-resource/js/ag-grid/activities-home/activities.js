$(function() {
	getActivityCounts();
	loadActivitiesGridCell();
});
var MyOpenActivities="";
var MyDelegatedActivities="";
var MyBUActivities="";
var AllActivities="";

$(document).ready(function() {
	$("#MyOpenActivities").click(function(event) {
		 event.preventDefault(); 
			 MyOpenActivities=$("#MyOpenActivities").text();
			MyDelegatedActivities="";
			 MyBUActivities="";
			getActivityCounts();
			loadActivitiesGridCell();
		
	});
	
	$("#AllActivities").click(function(event) {
		 event.preventDefault(); 
		 AllActivities=$("#AllActivities").text();
			MyDelegatedActivities="";
			 MyBUActivities="";
			 MyOpenActivities="";
			getActivityCounts();
			loadActivitiesGridCell();
		
	});
	
	
	
	$("#MyDelegatedActivities").click(function(event) {
		 event.preventDefault(); 
		 MyDelegatedActivities=$("#MyDelegatedActivities").text();
			
			MyOpenActivities="";
			MyBUActivities="";
			
			getActivityCounts();
			loadActivitiesGridCell();
		
	});
	
	
	$("#MyBUActivities").click(function(event) {
		 event.preventDefault(); 
		 MyBUActivities=$("#MyBUActivities").text();
			MyOpenActivities="";
			MyDelegatedActivities="";
			getActivityCounts();
			loadActivitiesGridCell();
	});
	
});
	
	
var columnDefs = [
	{
		"headerName":"Activity Number",
		"field":"workEffortId",
		checkboxSelection:true,
		cellRenderer: params => `<a href="viewActivity?workEffortId=${params.data.workEffortId}">${params.value}</a>`
	},
	{
		"headerName":"Parent Activity",
		"field":"ParentActivity"
	},
	{
		"headerName" : "Activity Type",
		"field" : "workEffortServiceType"
	},  
	{
		"headerName" : "Activity Sub Type",
		"field" : "workEffortSubServiceType"
	},
	{
		"headerName" : "CIF ID",
		"field" : "cinNumber"
	},

	{
		"headerName" : "Customer Name",
		"field" : "customerName"
	},
	{
		"headerName" : "Customer Type",
		"field" : "customerType"
	},

	{
		"headerName" : "Subject",
		"field" : "description"
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
		"field":"estimatedStartDate"
	},
	{
		"headerName":"Campaigncode",
		"field":"campaignCode"
	},

	{
		"headerName":"Product Name",
		"field":"productName"
	},

	{
		"headerName":"Source",
		"field":"createdSourceBy"
	},
	{
		"headerName":"Instruction/Comments",
		"field":"comments"
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
		"field":"actualCompletionDate	"
	},
	{
		"headerName":"OnceandDone",
		"field":"wfOnceDone	"
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
		"headerName":"Team",
		"field":"emplTeamId"
	},
	{
		"headerName":"Created by From iServe",
		"field":"createdSourceBy"
	},
	{
		"headerName":"LastUpdated By From iServe",
		"field":"lastUpdatedSource"
	},
	{
		"headerName":"Overdue",
		"field":"overdue"
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
		"field":"lastModifiedDate"
	},

	{
		"headerName":"Modified By",
		"field":"lastModifiedByUserLogin"
	}

	];


var gridOptions = null;
function  loadActivitiesGridCell(){
	$("#activitiesGrid").empty();
	gridOptions = {
			defaultColDef: {
				filter: true,
				sortable: true,
				resizable: true
			},
			columnDefs: columnDefs,
			rowData: getRowData(),
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
//	lookup the container we want the Grid to use
	var eGridDiv = document.querySelector("#activitiesGrid");
//	create the grid passing in the div to use together with the columns &
//	data we want to use
	new agGrid.Grid(eGridDiv, gridOptions);
}
function getAjaxResponse(callback) {
	var data1;
	var result = [];
	var resultRes = null;
	var errorMessage = null;
	var resultData = null;
	$.ajax({
		type: "POST",
		url: "getactivityHome",
		async: false,
		data: {"start": 0, "length":2000,"MyOpenActivities":MyOpenActivities,"MyDelegatedActivities":MyDelegatedActivities,
			"buPartyId":MyBUActivities},
		
		success: function(data) {
			var result1 = data[0];
			if(data[0] != null || data[0] != undefined){
				errorMessage = data[0].errorMessage;
				resultData = data[0].errorResult;
			}
			if(errorMessage != null || errorMessage != undefined) {

				callback(resultData);
			}else{

				callback(data);
			}

		},
		error: function() {

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
function onBtExport() {
	var params = {
			skipHeader: false,
			allColumns: true,
			fileName: "Login_History",
			exportMode: 'csv'
	};

	gridOptions.api.exportDataAsCsv(params);
}
function getSelectedRows() {
	var selectedRows;
	selectedRows = this.gridOptions.api.getSelectedRows();
	selectedRows.map((row)=>{
	});
	return selectedRows
}
function getActivityCounts(){
    var errorMessage = null;
    var resultData = null;
    $.ajax({
        type: "POST",
        url: "getActivityCounts",
        async: true,
        data: "data",
        success: function(data) {
            var result1 = data[0];
            if(data[0] != null || data[0] != undefined){
            errorMessage = data[0].errorMessage;
            resultData = data[0].errorResult;
            }
            if(errorMessage != null || errorMessage != undefined) {
                alert(errorMessage);
            }else{
                 $("#myActivities").html(DOMPurify.sanitize(data["myActivities"]));
                 $("#myTeamActivities").html(DOMPurify.sanitize(data["myTeamActivities"]));
                 $("#completedActivities").html(DOMPurify.sanitize(data["completedActivities"]));
                 $("#overDueActivities").html(DOMPurify.sanitize(data["overDueActivities"]));
            }
        },
        error: function() {
         alert("No counts found!");
        },
        complete: function() {
        // $('#loader').hide();
        }
    });

}
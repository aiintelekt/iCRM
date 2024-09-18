$(function() {
	loadActivitiesHomegrid();
	$('#reassignModal #close').click(function(){
		$('#reassignModal input[type=reset]').click();
	});
	$('#reassignModal #btnclose').click(function(){
		$('#reassignModal input[type=reset]').click();
	});
});

$("#loggedInUserActivities").click(function(){
	$('#systemViewFilterLabel').html(DOMPurify.sanitize($(this).html()));
	event.preventDefault(); 
	item = {}
	$('#Activities').remove('#lastMonthsDate');
	jsonString = JSON.stringify(item);
    $("#Activities").val(jsonString);
    $("#systemViewFilter").remove();
    $("#ownerUserLoginId").remove();
    $("#open").val('');
    var systemViewFilterParamVal = "loggedInUserActivities";
    var ownerUserLoginIdParamVal = document.getElementById("searchParam1").value;
    var sixMonthsDateStr=$("#sixMonthsDate").val();
    $("<input id='systemViewFilter' />").attr("type", "hidden").attr("name", "systemViewFilter").attr("value", systemViewFilterParamVal).appendTo("#Activities");
    $("<input id='ownerUserLoginId' />").attr("type", "hidden").attr("name", "ownerUserLoginId").attr("value", ownerUserLoginIdParamVal).appendTo("#Activities");
     if($('#Activities').has('#lastMonthsDate').length == 0)
    	$("<input id='lastMonthsDate' />").attr("type", "hidden").attr("name", "lastMonthsDate").attr("value", sixMonthsDateStr).appendTo("#Activities");
    loadActivitiesHomegrid();
}); 

$("#loggedInUserOpenActivities").click(function(event) {
	$('#systemViewFilterLabel').html(DOMPurify.sanitize($(this).html()));
    event.preventDefault(); 
	item = {}
    jsonString = JSON.stringify(item);
    $("#systemViewFilter").remove();
    $("#ownerUserLoginId").remove();
    $("#lastMonthsDate").remove();
    $("#open").val('');
    var systemViewFilterParamVal = "loggedInUserOpenActivities";
    var ownerUserLoginIdParamVal = document.getElementById("searchParam1").value;
    $("<input id='systemViewFilter' />").attr("type", "hidden").attr("name", "systemViewFilter").attr("value", systemViewFilterParamVal).appendTo("#Activities");
    $("<input id='ownerUserLoginId' />").attr("type", "hidden").attr("name", "ownerUserLoginId").attr("value", ownerUserLoginIdParamVal).appendTo("#Activities");
    loadActivitiesHomegrid();
});

$("#loggedInUserClosedActivities").click(function(event) {
	$('#systemViewFilterLabel').html(DOMPurify.sanitize($(this).html()));
    event.preventDefault(); 
	item = {}
    jsonString = JSON.stringify(item);
    $("#Activities").val(jsonString);
    $("#systemViewFilter").remove();
    $("#ownerUserLoginId").remove();
    $("#lastMonthsDate").remove();
    $("#open").val('');
    var systemViewFilterParamVal = "loggedInUserClosedActivities";
    var ownerUserLoginIdParamVal = document.getElementById("searchParam1").value;
    $("<input id='systemViewFilter' />").attr("type", "hidden").attr("name", "systemViewFilter").attr("value", systemViewFilterParamVal).appendTo("#Activities");
    $("<input id='ownerUserLoginId' />").attr("type", "hidden").attr("name", "ownerUserLoginId").attr("value", ownerUserLoginIdParamVal).appendTo("#Activities");
    loadActivitiesHomegrid();
});

$("#loggedInUserTeamActivities").click(function(event) {
	$('#systemViewFilterLabel').html(DOMPurify.sanitize($(this).html()));
    event.preventDefault(); 
	item = {}
    jsonString = JSON.stringify(item);
    $("#Activities").val(jsonString);
    $("#systemViewFilter").remove();
    $("#ownerUserLoginId").remove();
    $("#lastMonthsDate").remove();
    $("#open").val('');
    var systemViewFilterParamVal = "loggedInUserTeamActivities";
    var ownerUserLoginIdParamVal = document.getElementById("searchParam1").value;
    $("<input id='systemViewFilter' />").attr("type", "hidden").attr("name", "systemViewFilter").attr("value", systemViewFilterParamVal).appendTo("#Activities");
    $("<input id='ownerUserLoginId' />").attr("type", "hidden").attr("name", "ownerUserLoginId").attr("value", ownerUserLoginIdParamVal).appendTo("#Activities");
    loadActivitiesHomegrid();
});

$("#loggedInUserTeamOpenActivities").click(function(event) {
	$('#systemViewFilterLabel').html(DOMPurify.sanitize($(this).html()));
    event.preventDefault(); 
	item = {}
	$('#Activities').remove('#lastMonthsDate');
    jsonString = JSON.stringify(item);
    $("#Activities").val(jsonString);
    $("#systemViewFilter").remove();
    $("#ownerUserLoginId").remove();
    $("#lastMonthsDate").remove();
    $("#open").val('');
    var systemViewFilterParamVal = "loggedInUserTeamOpenActivities";
    var ownerUserLoginIdParamVal = document.getElementById("searchParam1").value;
    var sixMonthsDateStr=$("#sixMonthsDate").val();
    $("<input id='systemViewFilter' />").attr("type", "hidden").attr("name", "systemViewFilter").attr("value", systemViewFilterParamVal).appendTo("#Activities");
    $("<input id='ownerUserLoginId' />").attr("type", "hidden").attr("name", "ownerUserLoginId").attr("value", ownerUserLoginIdParamVal).appendTo("#Activities");
    if($('#Activities').has('#lastMonthsDate').length == 0)
    	$("<input id='lastMonthsDate' />").attr("type", "hidden").attr("name", "lastMonthsDate").attr("value", sixMonthsDateStr).appendTo("#Activities");
    loadActivitiesHomegrid();
});

$("#loggedInUserTeamClosedActivities").click(function(event) {
	$('#systemViewFilterLabel').html(DOMPurify.sanitize($(this).html()));
    event.preventDefault(); 
	item = {}
	$('#Activities').remove('#lastMonthsDate');
    jsonString = JSON.stringify(item);
    $("#Activities").val(jsonString);
    $("#systemViewFilter").remove();
    $("#ownerUserLoginId").remove();
    $("#lastMonthsDate").remove();
    $("#open").val('');
    var systemViewFilterParamVal = "loggedInUserTeamClosedActivities";
    var ownerUserLoginIdParamVal = document.getElementById("searchParam1").value;
    var sixMonthsDateStr=$("#sixMonthsDate").val();
    $("<input id='systemViewFilter' />").attr("type", "hidden").attr("name", "systemViewFilter").attr("value", systemViewFilterParamVal).appendTo("#Activities");
    $("<input id='ownerUserLoginId' />").attr("type", "hidden").attr("name", "ownerUserLoginId").attr("value", ownerUserLoginIdParamVal).appendTo("#Activities");
    if($('#Activities').has('#lastMonthsDate').length == 0)
    	$("<input id='lastMonthsDate' />").attr("type", "hidden").attr("name", "lastMonthsDate").attr("value", sixMonthsDateStr).appendTo("#Activities");
    loadActivitiesHomegrid();
});

var columnDefs234 = [
{
	"headerName":"Activity Number",
	"field":"workEffortId",
	checkboxSelection:true,
	width: 240,
    suppressAutoSize:true,
	cellRenderer: params => `<a href="viewActivity?workEffortId=${params.data.workEffortId}">${params.value}</a>`

},
{
	"colId": "emplTeamId",
	"headerName" : "Empl Team Id",
	"field" : "emplTeamId"
},
{
	"colId": "businessUnitId",
	"headerName" : "BU ID",
	"field" : "businessUnitId"
},
{
	"headerName" : "Name",
	"field" : "customerName"
},
{
	"headerName" : "Customer Type",
	"field" : "customerType"
},
{
	"headerName" : "CIF ID",
	"field" : "CIFID"
},
{
	"headerName" : "Prospect ID",
	"field" : "prospectId"
},
{
	"headerName" : "Non CRM",
	"field" : "isNonCrm"
},
{
	"headerName" : "V+ ID",
	"field" : "wfVplusId"
},
{
	"headerName" : "National ID",
	"field" : "wfNationalId"
},
{
	"headerName": "Empl Team",
	"field" : "emplTeamName"
},
{
	"headerName":"ParentActivity",
	"field":"parentActivity"
},
{
	"headerName" : "Activity Type",
	"field" : "activityType"
},  
{
	"headerName" : "Activity Sub Type",
	"field" : "activitySubType"
},
{
	"headerName" : "Subject",
	"field" : "subject"
},
{
	"headerName" : "Account#",
	"field" : "accountNumber"
},
{
	"headerName" : "Business Unit",
	"field" : "businessUnitName"
},
{
	"headerName":"Planned Start",
	"field":"plannedStartDate",
	"minWidth": 300,
	"sortable": true,
	"filter": true,
	cellStyle: function() {
		return{textAlign:"right"};
	}

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
	"headerName":"Type",
	"field":"type"
},
{
	"headerName":"Sub Type",
	"field":""
},
{
	"headerName":"Source",
	"field":"source"
},
{
	"headerName":"Instruction/Comments",
	"field":"comments"
},
{
	"headerName":"Planned Duration",
	"field":"plannedDuration",
	cellStyle: function() {
		return{textAlign:"right"};
	}
},
{
	"headerName":"Planned Due",
	"field":"plannedDueDate",
	"minWidth": 300,
	"filter": true,
	cellStyle: function() {
		return{textAlign:"right"};
	}
},
{
	"headerName":"Actual Completion",
	"field":"actualCompletionDate",
	"minWidth": 300,
	"filter": true,
	cellStyle: function() {
		return{textAlign:"right"};
	}
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
	"field":"primOwnerName"
},	
{
	"headerName":"Created by From iServe",
	"field":""
},
{
	"headerName":"LastUpdated By From iServe",
	"field":""
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
	"field":"actualStartDate",
	"minWidth": 300,
	"filter": true,
	cellStyle: function() {
		return{textAlign:"right"};
	}
},
{
	"headerName":"Actual Duration(Days)",
	"field":"actualDuration",
	cellStyle: function() {
		return{textAlign:"right"};
	}
},
{
	"headerName":"Created On",
	"field":"createdDate",
	"minWidth": 300,
	"filter": true,
	cellStyle: function() {
		return{textAlign:"right"};
	}
},
{
	"headerName":"Created By",
	"field":"createdByUserLogin"
},
{
	"headerName":"Modified On",
	"field":"lastModifiedDate",
	"minWidth": 300,
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

var gridOptionsActivity= null;
function  loadActivitiesHomegrid(){
	$("#homegrid").empty();
	gridOptionsActivity = {
			defaultColDef: {
				filter: true,
				sortable: true,
				resizable: true
			},
			columnDefs: columnDefs234,
			rowData:getGridData(),
			floatingFilter: true,
			rowSelection: "single",
			editType: "fullRow",
			paginationPageSize: 10,
			domLayout:"autoHeight",
			pagination: true,
			onGridReady: function() {
				columnVisible();
			}
	}
	var eGridDiv = document.querySelector("#homegrid");
	new agGrid.Grid(eGridDiv, gridOptionsActivity);
}

function columnVisible(){	
		gridOptionsActivity.columnApi.setColumnsVisible(["emplTeamId","businessUnitId"], false);
}
function getGridData() {
    var result = [];
    var resultRes = null;
    var params = {}
    var paramStr = $("#Activities").serialize();
    var fromData = JSON.stringify(paramStr);
    
    $.ajax({
        type: "POST",
        url: "getactivityHome",
        async: false,
        data: JSON.parse(fromData),
        success: function(data) {
            resultRes = data;
            result.push(data);
        },error: function(data) {
			showAlert("error", "Error occured!");
			resultRes=data;
		},
		complete: function() {
			$('#loader').hide();
		}
    });
    return resultRes;
}

function getRowData() {
	var result;
	result = getAjaxResponse(function(agdata) {
		gridOptionsActivity.api.setRowData(agdata);
	});
}

function sizeToFit() {
	gridOptionsActivity.api.sizeColumnsToFit();
} 

function onBtExport() {
	var params = {
			skipHeader: false,
			allColumns: true,
			fileName: "Findactivity",
			exportMode: 'csv'
	};
	gridOptionsActivity.api.exportDataAsCsv(params);
}

function getSelectedRows() {
	var selectedRows;
	selectedRows = this.gridOptionsActivity.api.getSelectedRows();
	selectedRows.map((row)=>{
	});
	return selectedRows
}

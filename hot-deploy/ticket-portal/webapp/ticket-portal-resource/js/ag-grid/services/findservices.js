$("#loader").show();
$(function() {
	loadFindSrvsAgGrid();
	$('#reassignModal #close').click(function(){
		$('#reassignModal input[type=reset]').click();
	});
	$('#reassignModal #btnclose').click(function(){
		$('#reassignModal input[type=reset]').click();
	});
});

$("#loggedInUserServiceRequests").click(function(event) {
	$('#systemViewFilterLabel').html(DOMPurify.sanitize($(this).html()));
    event.preventDefault(); 
	item = {}
	jsonString = JSON.stringify(item);
    $("#findServiceRequestForm").val(jsonString);
    $("#systemViewFilter").remove();
    $("#ownerUserLoginId").remove();
    $("#open").val('');
    var systemViewFilterParamVal = "loggedInUserServiceRequests";
    var ownerUserLoginIdParamVal = document.getElementById("searchParam1").value;
    var sixMonthsDateStr=$("#sixMonthsDate").val();
    $("<input id='systemViewFilter' />").attr("type", "hidden").attr("name", "systemViewFilter").attr("value", systemViewFilterParamVal).appendTo("#findServiceRequestForm");
    $("<input id='ownerUserLoginId' />").attr("type", "hidden").attr("name", "ownerUserLoginId").attr("value", ownerUserLoginIdParamVal).appendTo("#findServiceRequestForm");
    if($('#findServiceRequestForm').has('#lastMonthsDate').length == 0)
    	$("<input id='lastMonthsDate' />").attr("type", "hidden").attr("name", "lastMonthsDate").attr("value", sixMonthsDateStr).appendTo("#findServiceRequestForm");
	loadFindSrvsAgGrid();
});

$("#loggedInUserOpenServiceRequests").click(function(event) {
	$('#systemViewFilterLabel').html(DOMPurify.sanitize($(this).html()));
    event.preventDefault();
	item = {}
    jsonString = JSON.stringify(item);
    $("#systemViewFilter").remove();
    $("#ownerUserLoginId").remove();
    $("#lastMonthsDate").remove();
    $("#open").val('');
    var systemViewFilterParamVal = "loggedInUserOpenServiceRequests";
    var ownerUserLoginIdParamVal = document.getElementById("searchParam1").value;
    $("<input id='systemViewFilter' />").attr("type", "hidden").attr("name", "systemViewFilter").attr("value", systemViewFilterParamVal).appendTo("#findServiceRequestForm");
    $("<input id='ownerUserLoginId' />").attr("type", "hidden").attr("name", "ownerUserLoginId").attr("value", ownerUserLoginIdParamVal).appendTo("#findServiceRequestForm");
	loadFindSrvsAgGrid();
});

$("#loggedInUserClosedServiceRequests").click(function(event) {
	$('#systemViewFilterLabel').html(DOMPurify.sanitize($(this).html()));
    event.preventDefault(); 
	item = {}
    jsonString = JSON.stringify(item);
    $("#findServiceRequestForm").val(jsonString);
    $("#systemViewFilter").remove();
    $("#ownerUserLoginId").remove();
    $("#lastMonthsDate").remove();
    $("#open").val('');
    var systemViewFilterParamVal = "loggedInUserClosedServiceRequests";
    var ownerUserLoginIdParamVal = document.getElementById("searchParam1").value;
    $("<input id='systemViewFilter' />").attr("type", "hidden").attr("name", "systemViewFilter").attr("value", systemViewFilterParamVal).appendTo("#findServiceRequestForm");
    $("<input id='ownerUserLoginId' />").attr("type", "hidden").attr("name", "ownerUserLoginId").attr("value", ownerUserLoginIdParamVal).appendTo("#findServiceRequestForm");
	loadFindSrvsAgGrid();
});

$("#loggedInUserDelegatedServiceRequests").click(function(event) {
	$('#systemViewFilterLabel').html(DOMPurify.sanitize($(this).html()));
    event.preventDefault(); 
	item = {}
	var ownerUserLoginIdStr=$("#searchParam1").val();
    jsonString = JSON.stringify(item);
    $("#findServiceRequestForm").val(jsonString);
    $("#systemViewFilter").remove();
    $("#ownerUserLoginId").remove();
    $("#lastMonthsDate").remove();
    $("#open").val('');
    var systemViewFilterParamVal = "loggedInUserDelegatedServiceRequests";
    var ownerUserLoginIdParamVal = document.getElementById("searchParam1").value;
    $("<input id='systemViewFilter' />").attr("type", "hidden").attr("name", "systemViewFilter").attr("value", systemViewFilterParamVal).appendTo("#findServiceRequestForm");
    $("<input id='ownerUserLoginId' />").attr("type", "hidden").attr("name", "ownerUserLoginId").attr("value", ownerUserLoginIdParamVal).appendTo("#findServiceRequestForm");
    loadFindSrvsAgGrid();
});

$("#loggedInUserTeamServiceRequests").click(function(event) {
	$('#systemViewFilterLabel').html(DOMPurify.sanitize($(this).html()));
    event.preventDefault(); 
	item = {}
    jsonString = JSON.stringify(item);
    $("#findServiceRequestForm").val(jsonString);
    $("#systemViewFilter").remove();
    $("#ownerUserLoginId").remove();
    $("#lastMonthsDate").remove();
    $("#open").val('');
    var systemViewFilterParamVal = "loggedInUserTeamServiceRequests";
    var ownerUserLoginIdParamVal = document.getElementById("searchParam1").value;
    $("<input id='systemViewFilter' />").attr("type", "hidden").attr("name", "systemViewFilter").attr("value", systemViewFilterParamVal).appendTo("#findServiceRequestForm");
    $("<input id='ownerUserLoginId' />").attr("type", "hidden").attr("name", "ownerUserLoginId").attr("value", ownerUserLoginIdParamVal).appendTo("#findServiceRequestForm");
    loadFindSrvsAgGrid();
});

$("#loggedInUserTeamOpenServiceRequests").click(function(event) {
	$('#systemViewFilterLabel').html(DOMPurify.sanitize($(this).html()));
    event.preventDefault(); 
	item = {}
    jsonString = JSON.stringify(item);
    $("#findServiceRequestForm").val(jsonString);
    $("#systemViewFilter").remove();
    $("#ownerUserLoginId").remove();
    $("#lastMonthsDate").remove();
    $("#open").val('');
    var systemViewFilterParamVal = "loggedInUserTeamOpenServiceRequests";
    var ownerUserLoginIdParamVal = document.getElementById("searchParam1").value;
    var sixMonthsDateStr=$("#sixMonthsDate").val();
    $("<input id='systemViewFilter' />").attr("type", "hidden").attr("name", "systemViewFilter").attr("value", systemViewFilterParamVal).appendTo("#findServiceRequestForm");
    $("<input id='ownerUserLoginId' />").attr("type", "hidden").attr("name", "ownerUserLoginId").attr("value", ownerUserLoginIdParamVal).appendTo("#findServiceRequestForm");
    $("<input id='lastMonthsDate' />").attr("type", "hidden").attr("name", "lastMonthsDate").attr("value", sixMonthsDateStr).appendTo("#findServiceRequestForm");
    loadFindSrvsAgGrid();
});

$("#loggedInUserTeamClosedServiceRequests").click(function(event) {
	$('#systemViewFilterLabel').html(DOMPurify.sanitize($(this).html()));
    event.preventDefault(); 
	item = {}
    jsonString = JSON.stringify(item);
    $("#findServiceRequestForm").val(jsonString);
    $("#systemViewFilter").remove();
    $("#ownerUserLoginId").remove();
    $("#lastMonthsDate").remove();
    $("#open").val('');
    var systemViewFilterParamVal = "loggedInUserTeamClosedServiceRequests";
    var ownerUserLoginIdParamVal = document.getElementById("searchParam1").value;
    var sixMonthsDateStr=$("#sixMonthsDate").val();
    $("<input id='systemViewFilter' />").attr("type", "hidden").attr("name", "systemViewFilter").attr("value", systemViewFilterParamVal).appendTo("#findServiceRequestForm");
    $("<input id='ownerUserLoginId' />").attr("type", "hidden").attr("name", "ownerUserLoginId").attr("value", ownerUserLoginIdParamVal).appendTo("#findServiceRequestForm");
    $("<input id='lastMonthsDate' />").attr("type", "hidden").attr("name", "lastMonthsDate").attr("value", sixMonthsDateStr).appendTo("#findServiceRequestForm");
    loadFindSrvsAgGrid();
});

$("#loggedInUserBUOpenServiceRequests").click(function(event) {
	$('#systemViewFilterLabel').html(DOMPurify.sanitize($(this).html()));
    event.preventDefault(); 
	item = {}
    jsonString = JSON.stringify(item);
    $("#findServiceRequestForm").val(jsonString);
    $("#systemViewFilter").remove();
    $("#ownerUserLoginId").remove();
    $("#lastMonthsDate").remove();
    $("#open").val('');
    var systemViewFilterParamVal = "loggedInUserBUOpenServiceRequests";
    var ownerUserLoginIdParamVal = document.getElementById("searchParam1").value;
    var sixMonthsDateStr=$("#sixMonthsDate").val();
    $("<input id='systemViewFilter' />").attr("type", "hidden").attr("name", "systemViewFilter").attr("value", systemViewFilterParamVal).appendTo("#findServiceRequestForm");
    $("<input id='ownerUserLoginId' />").attr("type", "hidden").attr("name", "ownerUserLoginId").attr("value", ownerUserLoginIdParamVal).appendTo("#findServiceRequestForm");
    $("<input id='lastMonthsDate' />").attr("type", "hidden").attr("name", "lastMonthsDate").attr("value", sixMonthsDateStr).appendTo("#findServiceRequestForm");
    loadFindSrvsAgGrid();
});

$("#loggedInUserBUClosedServiceRequests").click(function(event) {
	$('#systemViewFilterLabel').html(DOMPurify.sanitize($(this).html()));
    event.preventDefault(); 
	item = {}
    jsonString = JSON.stringify(item);
    $("#findServiceRequestForm").val(jsonString);
    $("#systemViewFilter").remove();
    $("#ownerUserLoginId").remove();
    $("#lastMonthsDate").remove();
    $("#open").val('');
    var systemViewFilterParamVal = "loggedInUserBUClosedServiceRequests";
    var ownerUserLoginIdParamVal = document.getElementById("searchParam1").value;
    var sixMonthsDateStr=$("#sixMonthsDate").val();
    $("<input id='systemViewFilter' />").attr("type", "hidden").attr("name", "systemViewFilter").attr("value", systemViewFilterParamVal).appendTo("#findServiceRequestForm");
    $("<input id='ownerUserLoginId' />").attr("type", "hidden").attr("name", "ownerUserLoginId").attr("value", ownerUserLoginIdParamVal).appendTo("#findServiceRequestForm");
    $("<input id='lastMonthsDate' />").attr("type", "hidden").attr("name", "lastMonthsDate").attr("value", sixMonthsDateStr).appendTo("#findServiceRequestForm");
    loadFindSrvsAgGrid();
});

$("#loggedInUserBUOverdueServiceRequests").click(function(event) {
	$('#systemViewFilterLabel').html(DOMPurify.sanitize($(this).html()));
    event.preventDefault(); 
	item = {}
    jsonString = JSON.stringify(item);
    $("#findServiceRequestForm").val(jsonString);
    $("#systemViewFilter").remove();
    $("#ownerUserLoginId").remove();
    $("#lastMonthsDate").remove();
    $("#open").val('');
    var systemViewFilterParamVal = "loggedInUserBUOverdueServiceRequests";
    var ownerUserLoginIdParamVal = document.getElementById("searchParam1").value;
    var sixMonthsDateStr=$("#sixMonthsDate").val();
    $("<input id='systemViewFilter' />").attr("type", "hidden").attr("name", "systemViewFilter").attr("value", systemViewFilterParamVal).appendTo("#findServiceRequestForm");
    $("<input id='ownerUserLoginId' />").attr("type", "hidden").attr("name", "ownerUserLoginId").attr("value", ownerUserLoginIdParamVal).appendTo("#findServiceRequestForm");
    if($('#findServiceRequestForm').has('#lastMonthsDate').length == 0)
    	$("<input id='lastMonthsDate' />").attr("type", "hidden").attr("name", "lastMonthsDate").attr("value", sixMonthsDateStr).appendTo("#findServiceRequestForm");
    loadFindSrvsAgGrid();
});

var columnDefsMkt=[
	{
        "headerName": "SR Number",
        "filter": "agTextColumnFilter",
        "checkboxSelection":true,
        "field": "externalId",
        width: 240,
        suppressAutoSize:true,
        cellRenderer: function(params) {
        	return '<a href="viewServiceRequest?srNumber=' + params.data.externalId + ' &seqId=' + params.data.externalId + ' &partyId=' + params.data.partyId + '&entity=CustRequestSrSummary" target="">' + params.data.externalId + '</a>'
        }
    },{
        "headerName": "Name",
        "filter": "agTextColumnFilter",
        "field": "customerName"
    },{
        "headerName": "Customer Type",
        "filter": "agTextColumnFilter",
        "field": "customerType"
    },{
        "headerName": "CIF ID/CIN",
        "field": "cinNumber"
    },{
        "headerName": "Prospect ID",
        "filter": "agTextColumnFilter",
        "field": "prospectId"
    },{
        "headerName": "V +ID",
        "filter": "agTextColumnFilter",
        "field": "vPlusId"
    },{
        "headerName": "National ID",
        "filter": "agTextColumnFilter",
        "field": "nationalId"
    },{
        "headerName": "SR Type",
        "filter": "agTextColumnFilter",
        "field": "srTypeName"
    },{
        "headerName": "SR Category",
        "filter": "agTextColumnFilter",
        "field": "srCategoryName"
    },{
        "headerName": "SR Sub Category",
        "filter": "agTextColumnFilter",
        "field": "srSubCategoryName"
    },{
        "headerName": "Other SR Sub Category",
        "filter": "agTextColumnFilter",
        "field": "otherSrSubCategory"
    },{
        "headerName": "Priority",
        "filter": "agTextColumnFilter",
        "field": "priority"
    },{
		"headerName": "SR Status",
		"field": "srStatus",
		"minWidth": 200,
		"sortable": true,
		"filter": true
	},{
		"headerName": "SR Sub Status",
		"field": "srSubStatus",
		"minWidth": 200,
		"sortable": true,
		"filter": true
	},{
		"headerName": "Description",
		"field": "description",
		"minWidth": 300,
		"sortable": true,
		"filter": true
	},{
		"headerName": "Resolution",
		"field": "resolution",
		"minWidth": 300,
		"sortable": true,
		"filter": true
	},{
		"headerName": "Open Date",
		"field": "openDate",
		"minWidth": 300,
		"sortable": true,
		"filter": true,
		cellStyle: function() {
			return{textAlign:"right"};
	    }
	},{
		"headerName": "Duration",
		"field": "durationDays",
		"minWidth": 300,
		"sortable": true,
		"filter": true,
		cellStyle: function() {
			return{textAlign:"right"};
	    }
	},{
		"headerName": "Due Date",
		"field": "dueDate",
		"minWidth": 300,
		"sortable": true,
		"filter": true,
		cellStyle: function() {
			return{textAlign:"right"};
	    }
	},{
		"headerName": "SLA At Risk",
		"field": "slaRisk",
		"minWidth": 300,
		"sortable": true,
		"filter": true
	},{
		"headerName": "Overdue",
		"field": "overDueFlag",
		"minWidth": 300,
		"sortable": true,
		"filter": true
	},{
        "headerName": "Owner",
        "filter": "agTextColumnFilter",
        "field": "ownerUserLoginDescription"
    },{
      "headerName": "Team",
        "filter": "agTextColumnFilter",
        "field": "teamDescription"
    },{
    	"headerName":"Owner BU",
    	"filter": "agTextColumnFilter",
    	"field":"ownerBuName"
    },{
    	"headerName":"Linked From",
    	"filter": "agTextColumnFilter",
    	"field":"linkedFrom"
    },{
    	"headerName":"Linked To",
    	"filter": "agTextColumnFilter",
    	"field":"linkedTo"
    },{
    	"headerName":"Opp",
    	"filter": false,
    	"field":"salesOpportunityId",
    	 cellRenderer: params =>'<a href="newOpportunity?srNumber=' + params.data.externalId + '" class=" btn btn-xs btn-primary glyphicon glyphicon-plus">'
    },{
    	"headerName":"ACT",
    	"filter": false,
    	"field":"workEffortId",
    	cellRenderer: params =>'<a href="srAddTask?linkedFrom=' + params.data.externalId + '" class=" btn btn-xs btn-primary glyphicon glyphicon-plus">'
    },{
    	"headerName":"Account Type",
    	"filter": "agTextColumnFilter",
    	"field":"accountTypeDescription"
    },{
    	"headerName":"Account #",
    	"filter": "agTextColumnFilter",
    	"field":"accountNumber"
    },{
    	"headerName":"Once & Done",
    	"filter": "agTextColumnFilter",
    	"field":"onceDone"
    },{
        "headerName": "Created On",
        "filter": "agTextColumnFilter",
        "field": "createdOn",
        cellStyle: function() {
			return{textAlign:"right"};
	    }
    }, {
        "headerName": "Created By",
        "filter": "agTextColumnFilter",
        "field": "createdByUserLoginId"
    }, {
        "headerName": "Modified On",
        "filter": "agTextColumnFilter",
        "field": "modifiedOn",
        cellStyle: function() {
			return{textAlign:"right"};
	    }
    }, {
        "headerName": "Modified By",
        "field": "modifiedByUserLoginId"
    }, {
        "headerName": "Closed On",
        "filter": "agTextColumnFilter",
        "field": "closedOn",
        cellStyle: function() {
			return{textAlign:"right"};
	    }
    },{
    	"headerName":"Closed By",
    	"field":"closedBy"
    },{
    	"colId": "emplTeamId",
    	"headerName": "Empl Team Id",
        "field": "empTeamId"
    },,{
    	"colId": "businessUnitId",
    	"headerName": "BU ID",
        "field": "ownerBuId"
    },{
    	"colId": "srStatusId",
    	"headerName": "SR Status Id",
        "field": "srStatusId"
    },{
    	"colId": "srSubStatusId",
    	"headerName": "SR Sub Status Id",
        "field": "srSubStatusId"
    }
];
var gridOptionsMkt = null;
function loadFindSrvsAgGrid(){
	$("#findservicerequestgrid").empty();
	gridOptionsMkt = {
		defaultColDef: {
			filter: true,
			sortable: true,
			resizable: true,
		},
		columnDefs: columnDefsMkt,
		rowData: getGridData(),
		floatingFilter: true,
		rowSelection: "single",
		editType: "fullRow",
		paginationPageSize: 15,
		domLayout:"autoHeight",
		pagination: true,
		getRowStyle: getUserRowStyle,
		onGridReady: function() {
			columnVisible();
		}
	}
	var eGridDiv = document.querySelector("#findservicerequestgrid");
	new agGrid.Grid(eGridDiv, gridOptionsMkt);
}

function getUserRowStyle(params){
	if(params.data.overDueFlag == "Yes"){
		return{
			'background-color':'#FFBF00'
		}
	}
}

function columnVisible(){	
	gridOptionsMkt.columnApi.setColumnsVisible(["emplTeamId","businessUnitId","srStatusId","srSubStatusId"], false);
}

function getGridData() {
    var result = [];
    var resultRes = null;
    var params = {}
    var paramStr = $("#findServiceRequestForm").serialize();
    var fromData = JSON.stringify(paramStr);
    $.ajax({
        type: "POST",
        url: "findServiceRequest",
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

function getRowDataMkt() {
	var result;
	result = getAjaxResponse(function(agdata) {
		gridOptionsMkt.api.setRowData(agdata);
		
	});
}
function sizeToFitDrip() {
	gridOptionsMkt.api.sizeColumnsToFit();
}
$("#loader").hide();
function clearData() {
	gridOptionsMkt.api.setRowData([]);
}

function getSelectedRows() {
	var selectedRows;
	selectedRows = this.gridOptionsMkt.api.getSelectedRows();
	selectedRows.map((row)=>{
	});
	return selectedRows
}

function onBtExport() {
    var params = {
        skipHeader: false,
        allColumns: true,
        fileName: "Services",
        exportMode: 'csv'
    };
    gridOptionsMkt.api.exportDataAsCsv(params);
}

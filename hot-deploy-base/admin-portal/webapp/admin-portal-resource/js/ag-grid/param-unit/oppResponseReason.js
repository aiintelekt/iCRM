
fagReady("OPPTN_RES_REASON", function(el, api, colApi, gridApi){
    $("#refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#clear-filter-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    	gridApi.refreshUserPreferences();
    });
	$("#sub-filter-clear-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    });
    $("#export-btn").click(function () {
    	gridApi.csvExport();
    });

    $("#update-btn").click(function () {
    	gridApi.saveUpdates();
        setTimeout(() => {  loadMainGrid(gridApi); }, 1000);
    })

    $("#main-search-btn").click(function () {
    	loadMainGrid(gridApi);
    });
    
    loadMainGrid(gridApi);
});

function loadMainGrid(gridApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	$.ajax({
	  async: false,
	  url:'/admin-portal/control/getOpportunityConfigData',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#searchForm").serialize())),
	  success: function(data){
		  gridApi.setRowData(data);
	  }
	});
}

/*
$("#loader").show();
$(function() {
	loadRsReasonAgGrid();
});

var columnRsReasonDefs = [
	{	"headerName":"Call Outcome ",
		"field":"callOutcomeDescription",
		"sortable":true,
		width:20,
		"filter":"agTextColumnFilter"
	},
	{	"headerName":"Opportunity Response Type",
				"field":"responseTypeDescription",
				"sortable":true,
				width:20,
				"filter":true},
	{
		"headerName": "Opportunity Response Reason ",
		"field": "description",
		"sortable": true,
		"filter": true,
		width:20,
		
	"cellRenderer":params => `<a href="updateOppResponseReason?parentEnumId=${params.data.outComeId}&responseTypeId=${params.data.parentEnumId}&enumId=${params.data.enumId}">${params.value}</a>`
		
		
	},
	{
		"headerName": "Created On",
		"field": "createdOn",
		"sortable": true,
		"filter": true,
		width:15
	},
	{ 
		"headerName":"Modified On",
		"field":"modifiedOn",
		"sortable":true,
		"filter":true,
		width:15
	},

	];
var gridRsReasonOptions = null;
function loadRsReasonAgGrid(){
	$("#rspReasonGrid").empty();
	gridRsReasonOptions = {
			defaultColDef: {				
				sortable: true,
				resizable: true,

				// allow every column to be aggregated
				//enableValue: true,
				// allow every column to be grouped
				//enableRowGroup: true,
				// allow every column to be pivoted
				//enablePivot: true,
			},
			columnDefs: columnRsReasonDefs,
			rowData: getRsReasonGridData(),
			floatingFilter: true,
			rowSelection: "multiple",
			editType: "fullRow",
			paginationPageSize: 10,
			domLayout:"autoHeight",
			pagination: true,
			onFirstDataRendered: onFirstDataRendered
	};

	//lookup the container we want the Grid to use
	var eGridDiv = document.querySelector("#rspReasonGrid");

	// create the grid passing in the div to use together with the columns & data we want to use
	new agGrid.Grid(eGridDiv, gridRsReasonOptions);

}
function getRsReasonGridData() {
	var result = [];
	var resultRes = null;

	var paramStr = $("#searchRsReasonForm").serialize();
	var fromData = JSON.stringify(paramStr);

	$.ajax({
		type: "POST",
		url: "getOpportunityConfigData", 
		async: false,
		data: JSON.parse(fromData),
		success: function(data) {
			resultRes = data;
			result.push(data);			
		}
	});
	return resultRes;
}
$("#loader").hide();
//data binding while loading the page
function onFirstDataRendered(params) {
	params.api.sizeColumnsToFit();
}

function sizeToFit() {
	gridRsReasonOptions.api.sizeColumnsToFit();
}

function onBtRsReasonExport() {
	var params = {
			skipHeader: false,
			allColumns: true,
			fileName: "Response_Reason",
			exportMode: 'csv'
	};


	gridRsReasonOptions.api.exportDataAsCsv(params);
}
*/
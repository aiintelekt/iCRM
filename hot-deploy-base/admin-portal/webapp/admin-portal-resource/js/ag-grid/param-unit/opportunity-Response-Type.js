
fagReady("OPPTN_RES_TYPE", function(el, api, colApi, gridApi){
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
	  url:'/admin-portal/control/getOpportunityConfigDataResponseType',
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
	loadAgGridResponseType();
});

var columnResponseType = [
	{
		"headerName": "Call Outcome",
		"field": "callOutcomeDescription",
		"sortable": true,
		"filter": true,
		width: 15
	},
	{
		"headerName": "Opportunity Response Type ",
		"field": "description",
		"sortable": true,
		"filter": true,
		width: 20,                                                                                                           
	"cellRenderer":params => `<a href="updateOpportunityResponseType?enumId=${params.data.enumId}&parentEnumId=${params.data.parentEnumId}">${params.value}</a>`
	},
	{
		"headerName": "Created On",
		"field": "createdOn",
		"sortable": true,
		"filter": true,
		width: 15
	},
	{
		"headerName": "Modified On",
		"field": "modifiedOn",
		"sortable": true,
		"filter": true,
		width: 15
	}
	];
var gridResponseType = null;
function loadAgGridResponseType(){
	$("#gridResponseType").empty();
	gridResponseType = {
			columnDefs: columnResponseType,
			rowData: getResponseTypeGridData(),
			floatingFilter: true,
			rowSelection: "multiple",
			editType: "fullRow",
			paginationPageSize: 10,
			domLayout:"autoHeight",
			pagination: true,
			onFirstDataRendered: onFirstDataRendered
	};

	//lookup the container we want the Grid to use
	var eGridDiv = document.querySelector("#gridResponseType");

	// create the grid passing in the div to use together with the columns & data we want to use
	new agGrid.Grid(eGridDiv, gridResponseType);

}
function getResponseTypeGridData() {
	var result = [];
	var resultRes = null;

	var paramStr = $("#responseTypeSearchForm").serialize();
	var fromData = JSON.stringify(paramStr);
	$.ajax({
		type: "POST",
		url: "getOpportunityConfigDataResponseType", 
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
	gridResponseType.api.sizeColumnsToFit();
}

function onBtresponseTypeExport() {
	var params = {
			skipHeader: false,
			allColumns: true,
			fileName: "Response_Type",
			exportMode: 'csv'
	};
	gridResponseType.api.exportDataAsCsv(params);
}
*/
$("#loader").show();
$(function() {
	loadRsReasonAgGrid();
});

var columnRsReasonDefs = [
	{	"headerName":"Component",
		"field":"componentName",
		"sortable":true,
		width:20,
		"filter":"agTextColumnFilter"
	},
	{	"headerName":"Party ",
				"field":"partyIdDescription",
				"sortable":true,
				width:20,
				"filter":true},
	{	"headerName":"Message Type",
				"field":"roleTypeIdDescription",
				"sortable":true,
				width:20,
				"filter":true},
	{	"headerName":"Enabled",
				"field":"isEnabled",
				"sortable":true,
				width:20,
				"filter":true},
				
	{	"headerName":"From Date",
		"field":"fromDate",
		"sortable":true,
		width:20,
		"filter":true},	
		
		{	"headerName":"Thru Date",
			"field":"thruDate",
			"sortable":true,
			width:20,
			"filter":true},	
			
	{
		"headerName": "description",
		"field": "description",
		"sortable": true,
		"filter": true,
		width:20,
		
	"cellRenderer":params => `<a href="globalMessageUpdateDetails?componentId=${params.data.componentId}&roleTypeId=${params.data.roleTypeId}&fromDate=${params.data.fromDate}&description=${params.data.description}">${params.value}</a>`
		
		
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

	var paramStr = $("#searchGlobalMsgConfigForm").serialize();
	var fromData = JSON.stringify(paramStr);

	$.ajax({
		type: "POST",
		url: "getGlobalMessageConfigData", 
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
			fileName: "Scroll_msg",
			exportMode: 'csv'
	};


	gridRsReasonOptions.api.exportDataAsCsv(params);
}
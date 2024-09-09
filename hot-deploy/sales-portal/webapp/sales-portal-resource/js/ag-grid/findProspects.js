
$(function() {
	loadAgGridMyCell();
});

var columnDefs = [
	{
		"headerName":"Prospect Id",
		"field":"prospectExtId",checkboxSelection:true,headerCheckboxSelection:true,

		cellRenderer : function(params){
			return '<a href = "/sales-portal/control/viewProspect?prospectExtId='+params.data.prospectExtId+'">'+params.value+'</a>'
		},
		headerCheckboxSelection: function(params) {
			var displayedColumns = params.columnApi.getAllDisplayedColumns();
			return displayedColumns[0] === params.column;
		},
	},
	{
		"headerName":"First Name",
		"field":"firstName"
	},
	{
		"headerName" : "Last Name",
		"field" : "lastName"
	},  
	{
		"headerName" : "Date Of Birth",
		"field" : "dateOfBirth"
	},
	{
		"headerName" : "Nationality",
		"field" : "nationality"
	},
	{
		"headerName" : "Segment",
		"field" : "segment"
	},
	{
		"headerName" : "Product Line Interest",
		"field" : "prodLineInterest"
	},
	{
		"headerName" : "Occupation",
		"field" : "occupation"
	},
	{
		"headerName" : "Status",
		"field" : "status"
	}
	];

var gridOptions = null;
function loadAgGridMyCell(){

	$("#ProspectAgGrid").empty();
	gridOptions = {
			defaultColDef: {
				filter: true,
				sortable: true,
				resizable: true
			},
			columnDefs: columnDefs,
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
	var eGridDiv = document.querySelector("#ProspectAgGrid");
	// create the grid passing in the div to use together with the columns &
//	data we want to use
	new agGrid.Grid(eGridDiv, gridOptions);

}

function getAjaxResponse(callback) {
	var data1;
	var result = [];
	var resultRes = null;
	var params = {}
	var paramStr = $("#searchProspect").serialize();
	var parameters =  $("#searchProspect :input")
	.filter(function(index, element) {
		return $(element).val() != '';
	}).serialize();
	var fromData = JSON.stringify(paramStr);
	var errorMessage = null;
	var resultData = null;
	$.ajax({
		type: "POST",
		url: "getProspect",
		async: false,
		data: JSON.parse(fromData),
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
			fileName: "Find_Prospects",
			exportMode: 'csv'
	};
	gridOptions.api.exportDataAsCsv(params);
}

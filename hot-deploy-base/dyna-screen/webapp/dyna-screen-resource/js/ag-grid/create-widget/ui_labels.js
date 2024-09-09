$("#loader").show();
$(function() {
	loadUILabelAgGrid();
});

var columnDefsUiLabels = [

{
	"headerName": "UI Label",
	"field": "labelKey",
	"filter": "agTextColumnFilter",
	cellRenderer: params =>
		`<a href="#" onclick="setUiLabelPickerWindowValue('${params.data.labelKey}');">${params.value}</a>`
},
{
	"headerName" : "Label Name",
	"field" : "labelValue",
},
];

var gridOptionsUiLabel = null;
function loadUILabelAgGrid(){
	$("#ui_label_grid").empty();
	
	gridOptionsUiLabel = {
		defaultColDef : {
			filter : true,
			sortable : true,
			resizable : true
		},
		columnDefs : columnDefsUiLabels,
		floatingFilter : true,
		rowSelection : "single",
		paginationPageSize : 20,
		domLayout : "autoHeight",
		pagination : true,
		//onFirstDataRendered: onFirstDataRendered,
		onGridReady : function() {
			getUiLabelRowData();
		}
	}

	//lookup the container we want the Grid to use
	var eGridDiv = document.querySelector("#ui_label_grid");

	// create the grid passing in the div to use together with the columns & data we want to use
	new agGrid.Grid(eGridDiv, gridOptionsUiLabel);

}

function getUiLabelGridData(callback) {
	var result = [];
	var resultRes = null;
	var params = {}
	var inputData = {};
	
	//var paramStr = $("#findUiLabels").serialize();
	var labelComponentName = $("#labelComponentName").val(); 
	
	inputData = {"labelComponentName": labelComponentName};
	
	var errorMessage = null;
	var resultData = null;
	$.ajax({
		type : "POST",
		url : "searchUiLables",
		async : false,
		data : inputData,
		success : function(data) {
			if (data.code != 200) {
				showAlert("error", data.message);
			}
			callback(data.data);
		},
		error : function() {
			console.log('Error occured');
			showAlert("error", "Error occured!");
			callback(data.data);
		},
		complete : function() {
		}
	});

}

$("#loader").hide();

function sizeToFitUiLabelGrid() {
	gridOptionsUiLabel.api.sizeColumnsToFit();
}

function getUiLabelRowData() {
	var result;
	result = getUiLabelGridData(function(agdata) {
		gridOptionsUiLabel.api.setRowData(agdata);
	});
	sizeToFitUiLabelGrid();
}

//data binding while loading the page
function onFirstDataRendered(params) {
	gridOptionsUiLabel.api.sizeColumnsToFit();
}

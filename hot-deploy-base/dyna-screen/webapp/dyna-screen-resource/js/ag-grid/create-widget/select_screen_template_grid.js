$(function() {
	loadScreenGrid();
});

var columnSelectScreenTemplateDefs = [
		{
			"headerName" : "Config Id",
			"field" : "dynaConfigId",
			"sortable" : true,
			"filter" : true/*,
			cellRenderer : function(params) {
				return '<a href="/dyna-screen/control/dynaScreenStep2Create?dynaConfigId='
						+ params.data.dynaConfigId
						+ ' ">'
						+ params.data.dynaConfigId
						+ '</a>';
			}*/
		}, {
			"headerName" : "Display Name",
			"field" : "screenDisplayName",
			"sortable" : true,
			"filter" : true,
		}, {
			"headerName" : "Layout Type",
			"field" : "layoutType",
			"sortable" : true,
			"filter" : true,
		}, {
			"headerName" : "From Date",
			"field" : "fromDate",
			"sortable" : true,
			"filter" : true,
		}, {
			"headerName" : "Thru Date",
			"field" : "thruDate",
			"sortable" : true,
			"filter" : true,
		}, {
			"headerName" : "Action",
			//"field" : "isEdit",
			"sortable" : false,
			"filter" : false,
			cellRenderer : function(params) {
				var data = "";
				data = data + '<a href="/dyna-screen/control/previewDynaScreen?dynaConfigId='+params.data.dynaConfigId+'" class="btn btn-xs btn-primary tooltips m-1" title="Preview" target="_blank"> <i class="fa fa-eye" aria-hidden="true"></i> </a>';
				
				return data;
			}
	
		}

];

var gridOptionsSelectScreenTemplate = null;
function loadScreenGrid() {
	$("#select-screen-template").empty();
	gridOptionsSelectScreenTemplate = {
		defaultColDef : {
			filter : true,
			sortable : true,
			resizable : true
		},
		columnDefs : columnSelectScreenTemplateDefs,
		floatingFilter : true,
		rowSelection : "single",
		editType : "fullRow",
		paginationPageSize : 10,
		domLayout : "autoHeight",
		pagination : true,
		onGridReady : function() {
			sizeToFitSelectScreenTemplate();
			getSelectScreenTemplateRowData();
		}
	}

	// lookup the container we want the Grid to use
	var eGridDiv = document.querySelector("#select-screen-template");
	// create the grid passing in the div to use together with the columns &
	// data we want to use
	new agGrid.Grid(eGridDiv, gridOptionsSelectScreenTemplate);

}

function getSelectScreenTemplateAjaxResponse(callback) {
	var result = [];
	var resultRes = null;
	var params = {}
	var inputData = {};

	inputData = {"isPrimary" : "Y"};
	
	var errorMessage = null;
	var resultData = null;
	$.ajax({
		type : "POST",
		url : "searchDynaScreenConfigurations",
		async : false,
		data : inputData,
		success : function(data) {
			if (data.code != 200) {
				showAlert("error", data.message);
			}
			callback(data.data);
		},
		error : function(data) {
			console.log('Error occured');
			showAlert("error", "Error occured!");
			callback(data.data);
		},
		complete : function() {
		}
	});

}

function sizeToFitSelectScreenTemplate() {
	gridOptionsSelectScreenTemplate.api.sizeColumnsToFit();
}

function getSelectScreenTemplateRowData() {
	var result;
	result = getSelectScreenTemplateAjaxResponse(function(agdata) {
		gridOptionsSelectScreenTemplate.api.setRowData(agdata);
	});
}

$("#loader1").show();
$(function() {
	loadoppActivityGrid();
});

var columnDefsActivity = [{ 
	"headerName":"Activity Number",
	"field":"activity",
	"sortable":false,
	"filter":false,
	"filter":"agTextColumnFilter",
	cellRenderer:params => `<a href="viewActivity?workEffortId=${params.data.workEffortId};">${params.value}</a>`
},


{ 
	"headerName":"Activity Type",
	"field":"activityType",
	"sortable":false,
	"filter":false,
	"filter":"agTextColumnFilter"
},
{ 
	"headerName":"Activity Sub Type",
	"field":"activitySubType",
	"sortable":false,
	"filter":false,
	"filter":"agTextColumnFilter"

},
{ 
	"headerName":"Regarding Id",
	"field":"regardingId",
	"sortable":false,
	"filter":false,
	"filter":"agTextColumnFilter"

},

{  //rowData: getRowDataActivity(),
	"headerName":"Customer Name",
	"field":"customerName",
	"sortable":false,
	"filter":false,
	"filter":"agTextColumnFilter"

},

{ 
	"headerName":"Customer CIN",
	"field":"customerCIN",
	"sortable":false,
	"filter":false,
	"filter":"agTextColumnFilter"

},
{ 
	"headerName":"Owner",
	"field":"owner",
	"sortable":false,
	"filter":false,
	"filter":"agTextColumnFilter"

},


{ 
	"headerName":"Activity Status",
	"field":"status",
	"sortable":false,
	"filter":false,
	"filter":"agTextColumnFilter"

},
{ 
	"headerName":"Created date",
	"field":"createdDate",
	"sortable":false,
	"filter":false,
	"filter":"agTextColumnFilter"

},

{ 
	"headerName":"Planned date",
	"field":"plannedDate",
	"sortable":false,
	"filter":false,
	"filter":"agTextColumnFilter"

}
];

var gridOptionsOppActivity = null;
function loadoppActivityGrid(){
	$("#openActivityGrid").empty();
	gridOptionsOppActivity = {
			defaultColDef: {
				filter: false,
				sortable: false,
				resizable: false
			},
			columnDefs: columnDefsActivity,
			floatingFilter: false,
			paginationPageSize: 10,
			domLayout:"autoHeight",
			pagination: true,
			onGridReady: function() {
				getRowDatOppActivity();
			}
	}

	//lookup the container we want the Grid to use
	var eGridDiv = document.querySelector("#openActivityGrid");
	// create the grid passing in the div to use together with the columns & data we want to use
	new agGrid.Grid(eGridDiv, gridOptionsOppActivity);

}


function getAjaxResponseActivity(callback) {
	var data1;
	var result = [];
	var resultRes = null;
	const queryString = window.location.search;
	const urlParams = new URLSearchParams(queryString);
	var srNumber = urlParams.get('srNumber')
	var errorMessage = null;
	var resultData = null;
	$.ajax({
		type: "POST",
		url: "getActivityData",
		async: false,
		data: { "srNumber" : srNumber },
		success: function(data) {
			var result1 = data[0];
			if(data[0] != null || data[0] != undefined){
				errorMessage = data[0].errorMessage;
				resultData = data[0].errorResult;
			}
			if(errorMessage != null || errorMessage != undefined) {
				showAlert("error", errorMessage);
				callback(resultData);
			}else{
				callback(data);
			}

		},
		error: function() {
			console.log('Error occured');
			showAlert("error", "Error occured!");
		},
		complete: function() {
			//$('#loader').hide();
		}
	});
}

function getRowDatOppActivity() {
	var result;
	console.log("oppp--"+$("#openActivities").val());
	result = getAjaxResponseActivity(function(agdata) {
		gridOptionsOppActivity.api.setRowData(agdata);
	});
}

//data binding while loading the page
function onFirstDataRendered(params) {
	params.api.sizeColumnsToFit();
}
function clearData() {
	gridOptionsOppActivity.api.setRowData([]);
}



function onBtExport() {
	var params = {
			skipHeader: false,
			allColumns: true,
			fileName: "Find activity",
			exportMode: 'csv'
	};


	gridOptionsOppActivity.api.exportDataAsCsv(params);
}
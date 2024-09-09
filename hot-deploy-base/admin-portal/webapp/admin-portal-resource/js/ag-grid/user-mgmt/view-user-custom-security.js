/* 
$(function() {
	loadCustomSecurityAgGrid();
});

var columnCustomSecurityDefs = [{ 
	   "headerName":"Group Id",
	   "field":"groupId",
	   "sortable":true,
	   "filter":true
	},
	{ 
	   "headerName":"Security Type",
	   "field":"securityType",
	   "sortable":true,
	   "filter":true
	},
	{ 
	   "headerName":"Description",
	   "field":"description",
	   "sortable":true,
	   "filter":true
	}];

var gridCustomSecurityOptions = null;

function loadCustomSecurityAgGrid(){
	$("#gridCustomSecurity").empty();
	gridCustomSecurityOptions = {
		    defaultColDef: {
		        filter: true,
		        sortable: true,
		        resizable: true,
		        // allow every column to be aggregated
		        //enableValue: true,
		        // allow every column to be grouped
		        //enableRowGroup: true,
		        // allow every column to be pivoted
		        //enablePivot: true,
		    },
		    columnDefs: columnCustomSecurityDefs,
		    rowData: getCustomSecurityGridData(),
		    floatingFilter: true,
		    rowSelection: "multiple",
		    editType: "fullRow",
		    paginationPageSize: 10,
		    domLayout:"autoHeight",
		    pagination: true,
		    onFirstDataRendered: onCustomtSecurityDataRendered
		};

		//lookup the container we want the Grid to use
		var eGridDiv = document.querySelector("#gridCustomSecurity");

		// create the grid passing in the div to use together with the columns & data we want to use
		new agGrid.Grid(eGridDiv, gridCustomSecurityOptions);

}
function getCustomSecurityGridData() {
    var resultRes = null;
    var params = {}
    $.ajax({
        type: "POST",
        url: "getSecurityGroups",
        async: false,
        data:  { "partyId": $("#partyId").val() },
        success: function(data) {
            resultRes = data;
        }
    });
    return resultRes;
}
//data binding while loading the page
function onCustomtSecurityDataRendered(params) {
    params.api.sizeColumnsToFit();
}


function sizeToFit() {
    gridCustomSecurityOptions.api.sizeColumnsToFit();
}

*/

//fagReady("CUSTOM_SECURITY", function(el, api, colApi, gridApi){
//    $("#custom-security-ref-pref").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#custom-security-save-pref").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#custom-security-clear-filter").click(function () {
//    	gridApi.clearAllColumnFilters();
//    });
//	$("#sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#custom-security-export").click(function () {
//    	gridApi.csvExport();
//    });
//    /*
//    $("#update-btn").click(function () {
//    	gridApi.saveUpdates();
//        setTimeout(() => {  loadCustomSecurity(gridApi, api); }, 1000);
//    })
//
//    $("#main-search-btn").click(function () {
//    	loadCustomSecurity(gridApi, api);
//    });
//    $("#insert-btn").click(function () {
//    	gridApi.insertNewRow()
//    })
//    $("#remove-btn").click(function () {
//        //removeMainGrid(fag1, api);
//    	gridApi.removeSelected();
//        setTimeout(() => {  loadCustomSecurity(gridApi, api); }, 1000);
//        
//    }); */
//    loadCustomSecurity(gridApi, api);
//});
//
//var customSecurityUrl = "";
//function loadCustomSecurity(gridApi, api) {
//	if(customSecurityUrl == ""){
//		resetGridStatusBar();
//		customSecurityUrl = getGridDataFetchUrl("CUSTOM_SECURITY");
//	}
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(customSecurityUrl != null && customSecurityUrl != "" && customSecurityUrl !="undefined"){
//		api.showLoadingOverlay();
//		var formInput = $('#searchForm, #limitForm').serialize();
//		$.ajax({
//		  async: false,
//		  url:customSecurityUrl,
//		  type:"POST",
//		  data: { "userLoginId": $("#userLoginId").val()},
//		  success: function(data){
//			  gridApi.setRowData(data);
//			  //data.list=[];
//			  //paginateHandler(data);
//		  }
//		});
//	}
//}

$(function() {
	let listCustomSecurityInstanceId= "CUSTOM_SECURITY";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = listCustomSecurityInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#custom-security-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, listCustomSecurityInstanceId, userId);
	});
	$("#custom-security-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#custom-security-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, listCustomSecurityInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getListCustomSecurityListGridData();
		}
	});
	$('#custom-security-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#main-search-btn").click(function () {
		getListCustomSecurityListGridData();
    });
	function getListCustomSecurityListGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("CUSTOM_SECURITY");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#customSecurityAndDerivedForm";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getListCustomSecurityListGridData();
	}
});
/*
let recentTransactionsInstanceId= $('#recentTransactionsInstanceId').val();
fagReady("recentTransactionsInstanceId", function(el, api, colApi, gridApi) {
    $("#transactions-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#transactions-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#transactions-clear-filter-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    	gridApi.refreshUserPreferences();
    });
	$("#transactions-sub-filter-clear-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    });
    $("#transactions-export-btn").click(function () {
    	gridApi.csvExport();
    });
    $("#transactions-shipped-btn").click(function () {
    	loadRecentTransactions(gridApi, api, colApi);
    });
    $(".filter-transactions").click(function(event) {
    	event.preventDefault(); 

    $("#transactions-grid-header-title").html($(this).attr("data-searchTypeLabel"));
    loadRecentTransactions(gridApi, api, colApi);
    });
    loadRecentTransactions(gridApi, api, colApi);
});

function loadRecentTransactions(gridApi, api, colApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	api.showLoadingOverlay();
	$.ajax({
	  async: true,
	  url:'/customer-portal/control/searchOrders',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#recent-transactions-form").serialize())),
	  success: function(result){
		  gridApi.setRowData(result.data);
		  setTimeout(() => colApi.autoSizeAllColumns(), 1000);
	}
	});
}

*/


$(function() {
	let recentTransInstanceId= $('#recentTransactionsInstanceId').val();
	let gridInstance  = "";
	//loadCustBoughtProduct();
	//let recentTransInstanceId = $("#recent-transactions_instanceId").val();
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = recentTransInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	//formDataObject.dataFetchCall = getRecentTransRowData;
	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	function getRecentTransGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/customer-portal/control/searchOrders";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#recent-transactions-form";
		callCtx.ajaxResponseKey = "data";
		
		setGridData(gridInstance, callCtx);
	}
	
	
	if(gridInstance){
		getRecentTransGridData();
	}
	/*
	function getRecentTransRowData() {
		var result = getRecentTransRowDataResponse(function(agdata) {
			gridInstance.setGridOption('rowData', agdata);
	    });
	} */
	
	$('#recent-trans-save-pref').click(function(){
		saveGridPreference(gridInstance, recentTransInstanceId, userId);
	});
	$("#transactions-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#recent-trans-clear-pref').click(function(){
		clearGridPreference(gridInstance, recentTransInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getRecentTransGridData();
		}
	});
	$('#recent-trans-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});
});



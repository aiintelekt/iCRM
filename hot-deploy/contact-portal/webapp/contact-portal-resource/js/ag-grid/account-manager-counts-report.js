//fagReady("ACCOUNT_MANAGER_COUNTS_REPORT", function(el, api, colApi, gridApi){
//	$("#account-manager-counts-report-export-btn").click(function() {
//		gridApi.csvExport();
//	});
//	loadAccountManagerCountsReport(api, gridApi);
//	postLoadGridData(api, gridApi, "accountManagerCountsReport", loadAccountManagerCountsReport);
//});
//
//function loadAccountManagerCountsReport(api, gridApi) {
//	var rowData = [];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	var formInput = $('#accountManagerCountsReportForm, #limitForm').serialize();
//	$.ajax({
//		async: false,
//		url: 'accountManagerCountsEvent',
//		type: "POST",
//		data: JSON.parse(JSON.stringify($('#accountManagerCountsReportForm, #limitForm').serialize())),
//		success: function(data) {
//			gridApi.setRowData(data.data);
//			data.data = [];
//			paginateHandler(data);
//		}
//	});
//}

$(function() {
	let accountManagerInstanceId= "ACCOUNT_MANAGER_COUNTS_REPORT";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = accountManagerInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#acountManager-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, accountManagerInstanceId, userId);
	});
	$('#acountManager-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance,accountManagerInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getAccountManagerGridData();
		}
	});
    $('#acountManager-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
    $("#account-manager-counts-report-export-btn").click(function() {
    	gridInstance.exportDataAsCsv();
	});
	function getAccountManagerGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "accountManagerCountsEvent";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#accountManagerCountsReportForm, #limitForm_ACCOUNT_MANAGER_COUNTS_REPORT";
		callCtx.ajaxResponseKey = "data";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getAccountManagerGridData();
	}
});



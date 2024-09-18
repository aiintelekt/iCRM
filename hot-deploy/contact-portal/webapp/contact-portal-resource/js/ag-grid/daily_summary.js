//fagReady("DAILY_SUMMARY_REPORT", function(el, api, colApi, gridApi){
//	$("#summary-refresh-pref-btn").click(function() {
//		gridApi.refreshUserPreferences();
//	});
//	$("#summary-save-pref-btn").click(function() {
//		gridApi.saveUserPreferences();
//	});
//	$("#summary-clear-filter-btn").click(function() {
//		try {
//			gridApi.clearAllColumnFilters();
//		} catch (e) {}
//		gridApi.refreshUserPreferences();
//	});
//	$("#summary-export-btn").click(function() {
//		gridApi.csvExport();
//	});
//	$("#summary-sub-filter-clear-btn").click(function () {
//		try{
//			gridApi.clearAllColumnFilters();
//		}catch(e){
//		}
//	});
//	$( "#main-search-btn" ).click(function() {
//		var createdDate = $("#createdDate").val();
//		if(createdDate != null && createdDate != "") {
//			$("#createdDate_error").html('');
//			loadMainGrid( api,gridApi) ;
//		} else {
//			$("#createdDate_error").html("Please Select Date");
//		}
//	});
//	$("#dailySummaryReport").on("keypress", function (event) {
//		var keyPressed = event.keyCode || event.which; 
//		if (keyPressed === 13) { 
//			loadMainGrid(api,gridApi) ;
//			event.preventDefault(); 
//			return false; 
//		} 
//	});
//});
//function loadMainGrid(api,gridApi) {
//	var rowData = [];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	$.ajax({
//		async: true,
//		url: 'getDailySummaryReport',
//		type: "POST",
//		data: JSON.parse(JSON.stringify($("#dailySummaryReport").serialize())),
//		success: function(data) {
//			gridApi.setRowData(data.data);
//			data.data = [];
//		}
//	});
//}

$(function() {
	let dailySummaryInstanceId= "PERSON_RESPOSIBLE_FOR_REPORT";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = dailySummaryInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#dailySummary-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, dailySummaryInstanceId, userId);
	});
	$('#dailySummary-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance,dailySummaryInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getDailySummaryGridData();
		}
	});
	$( "#main-search-btn" ).click(function() {
		var createdDate = $("#createdDate").val();
		if(createdDate != null && createdDate != "") {
			$("#createdDate_error").html('');
			getDailySummaryGridData();
		} else {
			$("#createdDate_error").html("Please Select Date");
		}
	});
	$("#dailySummaryReport").on("keypress", function (event) {
		var keyPressed = event.keyCode || event.which; 
		if (keyPressed === 13) { 
			getDailySummaryGridData();
			event.preventDefault(); 
			return false; 
		} 
	});
    $('#dailySummary-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
    $("#summary-export-btn").click(function() {
    	gridInstance.exportDataAsCsv();
	});
	function getDailySummaryGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "getDailySummaryReport";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#dailySummaryReport";
		callCtx.ajaxResponseKey = "data";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getDailySummaryGridData();
	}
});
//fagReady("VIEW_EMAIL_TRACKING", function(el, api, colApi, gridApi){
//	$("#email-track-refresh-pref-btn").click(function() {
//		gridApi.refreshUserPreferences();
//	});
//	$("#email-track-save-pref-btn").click(function() {
//		gridApi.saveUserPreferences();
//	});
//	$("#email-track-clear-filter-btn").click(function() {
//		try {
//			gridApi.clearAllColumnFilters();
//		} catch (e) {}
//		gridApi.refreshUserPreferences();
//	});
//	$("#email-track-export-btn").click(function() {
//		gridApi.csvExport();
//	});
//	$("#email-track-main-search-btn").click(function() {
//		loadEmailTrackGrid(gridApi, api, colApi);
//	});
//	$("#email-track-sub-filter-clear-btn").click(function () {
//		try{
//			gridApi.clearAllColumnFilters();
//		}catch(e){
//		}
//	});
//	$("#emailTracking").on("keypress", function (event) {
//		var keyPressed = event.keyCode || event.which; 
//		if (keyPressed === 13) { 
//			loadEmailTrackGrid(gridApi, api, colApi);
//			event.preventDefault(); 
//			return false; 
//		} 
//	}); 
//	loadEmailTrackGrid(gridApi, api, colApi);
//});
//
//function loadEmailTrackGrid(gridApi,api,colApi) {
//	var rowData = [];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	var formInput = $('#emailTracking').serialize();
//	$.ajax({
//		async: true,
//		url: '/admin-portal/control/viewEmailTracking',
//		type: "POST",
//		data: JSON.parse(JSON.stringify(formInput)),
//		success: function(data) {
//			gridApi.setRowData(data.data);
//			console.log("data----------------"+data.data);
//			data.data = [];
//			paginateHandler(data);
//		}
//	});
//}

$(function() {
	let emailTrackingInstanceId= "VIEW_EMAIL_TRACKING";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = emailTrackingInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#email-track-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, emailTrackingInstanceId, userId);
	});

	$('#email-track-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, emailTrackingInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getEmailTrackingGridData();
		}
	});
	$('#email-track-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#email-track-main-search-btn").click(function() {
		getEmailTrackingGridData();
	});
	$("#email-track-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$("#emailTracking").on("keypress", function (event) {
		var keyPressed = event.keyCode || event.which; 
		if (keyPressed === 13) { 
			getEmailTrackingGridData();
			event.preventDefault(); 
			return false; 
		} 
	}); 
	function getEmailTrackingGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/admin-portal/control/viewEmailTracking";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#emailTracking";
		callCtx.ajaxResponseKey = "data";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getEmailTrackingGridData();
	}
});

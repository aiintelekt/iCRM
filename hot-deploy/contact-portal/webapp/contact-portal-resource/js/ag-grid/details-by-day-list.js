//fagReady("DETAILS_BY_DAY", function(el, api, colApi, gridApi){
//	$( "#main-search-btn" ).click(function() {
//		var createdDate = $("#createdDate").val();
//		if(createdDate != null && createdDate != "") {
//			$("#createdDate_error").html('');
//			loadDetailsByDayList(api, gridApi);
//		} else {
//			$("#createdDate_error").html("Please Select Date");
//		}
//	});
//	$("#details-by-day-export-btn").click(function() {
//		gridApi.csvExport();
//	});
//	$("#searchDetailsByDayForm").on("keypress", function (event) {
//		var keyPressed = event.keyCode || event.which; 
//		if (keyPressed === 13) { 
//			loadDetailsByDayList( api, gridApi);
//			event.preventDefault(); 
//			return false; 
//		} 
//	});
//	loadDetailsByDayList(api, gridApi);
//});
//
//function loadDetailsByDayList(api, gridApi) {
//	var rowData = [];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	$.ajax({
//		async: true,
//		url: 'detailsByDayEvent',
//		type: "POST",
//		data: JSON.parse(JSON.stringify($('#searchDetailsByDayForm, #limitForm').serialize())),
//		success: function(data) {
//			gridApi.setRowData(data.data);
//			data.data = [];
//			paginateHandler(data);
//		}
//	});
//}



$(function() {
	let detailsbyDayInstanceId= "DETAILS_BY_DAY";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = detailsbyDayInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#detail-save-filter-btn').click(function(){
		saveGridPreference(gridInstance, detailsbyDayInstanceId, userId);
	});
	$('#detail-clear-pref-btn').click(function(){
		clearGridPreference(gridInstance, detailsbyDayInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getDetaisGridData();
		}
	});
    $('#detail-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
    $("#details-by-day-export-btn").click(function() {
    	gridInstance.exportDataAsCsv();
	});
    $( "#main-search-btn" ).click(function() {
		var createdDate = $("#createdDate").val();
		if(createdDate != null && createdDate != "") {
			$("#createdDate_error").html('');
			getDetaisGridData();
		} else {
			$("#createdDate_error").html("Please Select Date");
		}
	});
	$("#searchDetailsByDayForm").on("keypress", function (event) {
		var keyPressed = event.keyCode || event.which; 
		if (keyPressed === 13) { 
			getDetaisGridData();
			event.preventDefault(); 
			return false; 
		} 
	});
	function getDetaisGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "detailsByDayEvent";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchDetailsByDayForm, #limitForm_DETAILS_BY_DAY";
		callCtx.ajaxResponseKey = "data";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getDetaisGridData();
	}
});
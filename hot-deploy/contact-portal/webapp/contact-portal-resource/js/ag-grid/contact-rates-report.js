//fagReady("CONTACT_RATES_REPORT", function(el, api, colApi, gridApi){
//	$("#contact-rates-report-export-btn").click(function() {
//		gridApi.csvExport();
//	});
//	$("#contactRatesReportForm").on("keypress", function (event) {
//		var keyPressed = event.keyCode || event.which; 
//		if (keyPressed === 13) { 
//			loadContactRatesReport(api, gridApi);
//			event.preventDefault(); 
//			return false; 
//		} 
//	});
//	$( "#find-contact-rates-btn" ).click(function() {
//		var fromDate = $("#fromDate").val();
//		var thruDate = $("#thruDate").val();
//		$("#fromDate_error").html("");
//		$("#thruDate_error").html("");
//		if(fromDate != null && fromDate != "" && thruDate != null && thruDate != "") {
//			loadContactRatesReport(api, gridApi);
//		} else {
//			if(fromDate == null || fromDate == "") {
//				$("#fromDate_error").html("Please Select From Date");
//			}
//			if(thruDate == null || thruDate == "") {
//				$("#thruDate_error").html("Please Select Thru Date");
//			}
//		}
//	});
//	loadContactRatesReport(api, gridApi);
//	postLoadGridData(api, gridApi, "contactRatesReport", loadContactRatesReport);
//});
//
//function loadContactRatesReport(api, gridApi) {
//	var rowData = [];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	$.ajax({
//		async: true,
//		url: 'getContactRatesReport',
//		type: "POST",
//		data: JSON.parse(JSON.stringify($('#contactRatesReportForm, #limitForm').serialize())),
//		success: function(data) {
//			gridApi.setRowData(data.data);
//			data.data = [];
//			paginateHandler(data);
//		}
//	});
//}


$(function() {
	let contactRateInstanceId= "CONTACT_RATES_REPORT";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = contactRateInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#contact-rates-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, dailySummaryInstanceId, userId);
	});
	$('#contact-rates-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance,dailySummaryInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getContactratesGridData();
		}
	});
	$("#contactRatesReportForm").on("keypress", function (event) {
		var keyPressed = event.keyCode || event.which; 
		if (keyPressed === 13) { 
			getContactratesGridData();
			event.preventDefault(); 
			return false; 
		} 
	});
	$( "#find-contact-rates-btn" ).click(function() {
		var fromDate = $("#fromDate").val();
		var thruDate = $("#thruDate").val();
		$("#fromDate_error").html("");
		$("#thruDate_error").html("");
		if(fromDate != null && fromDate != "" && thruDate != null && thruDate != "") {
			getContactratesGridData();
		} else {
			if(fromDate == null || fromDate == "") {
				$("#fromDate_error").html("Please Select From Date");
			}
			if(thruDate == null || thruDate == "") {
				$("#thruDate_error").html("Please Select Thru Date");
			}
		}
	});
    $('#contact-rates-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
    $("#contact-rates-report-export-btn").click(function() {
    	gridInstance.exportDataAsCsv();
	});
	function getContactratesGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "getContactRatesReport";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#contactRatesReportForm, #limitForm_CONTACT_RATES_REPORT";
		callCtx.ajaxResponseKey = "data";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getContactratesGridData();
	}
});
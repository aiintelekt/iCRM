//fagReady("PERSON_RESPOSIBLE_FOR_REPORT", function(el, api, colApi, gridApi){
//	$("#person-resposible-for-report-export-btn").click(function() {
//		gridApi.csvExport();
//	});
//	loadPersonResponsibleForReport(api, gridApi);
//	postLoadGridData(api, gridApi, "personResponsibleForReport", loadPersonResponsibleForReport);
//});
//
//function loadPersonResponsibleForReport(api, gridApi) {
//	var rowData = [];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	var formInput = $('#personResponsibleForReportForm, #limitForm').serialize();
//	$.ajax({
//		async: false,
//		url: 'personResponsibleForReportEvent',
//		type: "POST",
//		data: JSON.parse(JSON.stringify($('#personResponsibleForReportForm, #limitForm').serialize())),
//		success: function(data) {
//			gridApi.setRowData(data.data);
//			data.data = [];
//			paginateHandler(data);
//		}
//	});
//}

$(function() {
	let personResponsibleInstanceId= "PERSON_RESPOSIBLE_FOR_REPORT";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = personResponsibleInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#personResponsible-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, personResponsibleInstanceId, userId);
	});
	$('#personResponsible-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance,personResponsibleInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getPersonResponsibleGridData();
		}
	});
    $('#personResponsible-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
    $("#person-resposible-for-report-export-btn").click(function() {
    	gridInstance.exportDataAsCsv();
	});
	function getPersonResponsibleGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "personResponsibleForReportEvent";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#personResponsibleForReportForm, #limitForm_PERSON_RESPOSIBLE_FOR_REPORT";
		callCtx.ajaxResponseKey = "data";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getPersonResponsibleGridData();
	}
});
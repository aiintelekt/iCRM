//added for Template picker
//fagReady("TEMPLATE_PICKER_LIST", function(el, api, colApi, gridApi){
//	$("#template-refresh-pref-btn").click(function () {
//		gridApi.refreshUserPreferences();
//	});
//	$("#template-save-pref-btn").click(function () {
//		gridApi.saveUserPreferences();
//	});
//	$("#template-clear-filter-btn").click(function () {
//		try{
//			gridApi.clearAllColumnFilters();
//		}catch(e){
//		}
//		gridApi.refreshUserPreferences();
//	});
//	$("#sub-filter-clear-btn").click(function () {
//		try{
//			gridApi.clearAllColumnFilters();
//		}catch(e){
//		}
//	});
//	$("#template-export-btn").click(function () {
//		gridApi.csvExport();
//	});
//
//	$("#find-temp-search-btn").click(function () {
//		temp_picker_instance = $('#temp_picker_instance').val();
//		loadTemplatePickerGrid(gridApi, api, colApi);
//	});
//	
//	$("#find_temp_trigger").click(function(event) {
//        event.preventDefault();
//		temp_picker_instance = $('#temp_picker_instance').val();
//        console.log("dashboard-filter trigger");
//        loadTemplatePickerGrid(gridApi, api, colApi);
//		colApi.autoSizeAllColumns();
//    });
//	
//});
//
//function loadTemplatePickerGrid(gridApi, api, colApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	$.ajax({
//		async: true,
//		url: "/common-portal/control/findTemplatesAjax",
//		type:"POST",
//		data: JSON.parse(JSON.stringify($("#"+temp_picker_instance+"_Form").serialize())),
//		success: function(data){
//			gridApi.setRowData(data.data);
//			setTimeout(() => colApi.autoSizeAllColumns(), 1000);
//		}
//	});
//}

var temp_picker_instance_id = "";

$(function() {
	let templatelistInstanceId= "TEMPLATE_PICKER_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = templatelistInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#template-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, templatelistInstanceId, userId);
	});
	$("#find-temp-search-btn").click(function () {
		//temp_picker_instance_id = $('#temp_picker_instance').val();
		getTemplateGridData();
	});
	
	$("#find_temp_trigger").click(function(event) {
        event.preventDefault();
//      temp_picker_instance_id = $('#temp_picker_instance').val();
//		console.log("temp_picker_instance_id---"+temp_picker_instance_id);
        console.log("dashboard-filter trigger");
		getTemplateGridData();
    });
	
	$("#template-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#template-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, templatelistInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getTemplateGridData();
		}
	});
	$('#template-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	
	function getTemplateGridData(){
		gridInstance.showLoadingOverlay();
		temp_picker_instance_id = $('#temp_picker_instance').val();
		console.log("temp_picker_instance_id=="+temp_picker_instance_id);
		
		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/findTemplatesAjax";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#"+temp_picker_instance_id+"_Form";
		callCtx.ajaxResponseKey = "data";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getTemplateGridData();
	}
});

//fagReady("PROCESS_FLOW_LIST", function(el, api, colApi, gridApi){
//	$("#process-flow-refresh-pref-btn").click(function () {
//		gridApi.refreshUserPreferences();
//	});
//	$("#save-pref-btn").click(function () {
//		gridApi.saveUserPreferences();
//	});
//	$("#clear-filter-btn").click(function () {
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
//	$("#export-btn").click(function () {
//		gridApi.csvExport();
//	});
//	$("#processFlow-refresh-btn").click(function () {
//		loadProcessFlow(api, gridApi,colApi);
//		api.sizeColumnsToFit();
//	});
//	$("#find-process-flow").on("keypress", function (event) {
//		var keyPressed = event.keyCode || event.which; 
//		if (keyPressed === 13) { 
//			loadProcessFlow(api, gridApi, colApi);
//			event.preventDefault(); 
//			return false; 
//		} 
//	});
//	
//	//loadProcessFlow(api, gridApi, colApi);
//});

//var listProcessFlow= "";
//function loadProcessFlow(api, gridApi, colApi) {
//	if(listProcessFlow == ""){
//		resetGridStatusBar();
//		listProcessFlow = getGridDataFetchUrl("PROCESS_FLOW_LIST");
//	}
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	if(listProcessFlow != null && listProcessFlow != "" && listProcessFlow !="undefined"){
//		api.showLoadingOverlay();
//		var formInput = $('#find-process-flow').serialize();
//		$.ajax({
//			async: true,
//			url:listProcessFlow,
//			type:"POST",
//			data: JSON.parse(JSON.stringify(formInput)),
//			success: function(result){
//				if(result){
//					gridApi.setRowData(result.data);
//					setTimeout(() => colApi.autoSizeAllColumns(), 1000);
//					result.data=[];
//					paginateHandler(result);
//				}
//			}
//		});
//	}
//	setTimeout(() => colApi.autoSizeAllColumns(), 1000);
//}


$(function() {
	let processFlowInstanceId= "PROCESS_FLOW_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = processFlowInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#process-flow-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, processFlowInstanceId, userId);
	});
	$('#process-flow-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, processFlowInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			processFlowGridData();
		}
	});
	$('#process-flow-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#process-flow-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$("#processFlow-refresh-btn").click(function () {
		processFlowGridData();
		gridInstance.sizeColumnsToFit();
	});
	$("#find-process-flow").on("keypress", function (event) {
		var keyPressed = event.keyCode || event.which; 
		if (keyPressed === 13) { 
			processFlowGridData();
			event.preventDefault(); 
			return false; 
		} 
	});
	function processFlowGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("PROCESS_FLOW_LIST");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#find-process-flow";
		callCtx.ajaxResponseKey = "data";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		processFlowGridData();
	}
});

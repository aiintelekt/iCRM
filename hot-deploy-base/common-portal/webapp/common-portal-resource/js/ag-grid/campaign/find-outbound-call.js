//fagReady("COMM_OUT_BOUND_CALL_LIST", function(el, api, colApi, gridApi){
//    $("#outboundcall-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//		loadOutboundCallGrid(api, gridApi);
//    });
//    $("#outboundcall-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#outboundcall-clear-filter-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    	gridApi.refreshUserPreferences();
//    });
//	$("#outboundcall-sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#outboundcall-export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//
//    $("#refresh-outboundcall-btn").click(function () {
//    	loadOutboundCallGrid(api, gridApi);
//    });
//    
//    postLoadGridData(api, gridApi, "callList", loadOutboundCallGrid);
//    
//    //loadOutboundCallGrid(gridApi);
//});
//
//function loadOutboundCallGrid(api, gridApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	$.ajax({
//	  async: false,
//	  url:'/common-portal/control/searchOutBoundCalls',
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify($("#outboundcall-search-form").serialize())),
//	  success: function(data){
//		  gridApi.setRowData(data.list);
//	  }
//	});
//}

$(function() {
	let outBoundCallListGridInstanceId= "COMM_OUT_BOUND_CALL_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = outBoundCallListGridInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#outboundcall-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, outBoundCallListGridInstanceId, userId);
	});
	$('#outboundcall-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, outBoundCallListGridInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getOutBoundCallListGridData();
		}
	});
	$('#outboundcall-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	  $("#refresh-outboundcall-btn").click(function () {
		  getOutBoundCallListGridData();
	});
	$("#outboundcall-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	function getOutBoundCallListGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/searchOutBoundCalls";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#outboundcall-search-form";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getOutBoundCallListGridData();
	}
});

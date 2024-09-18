//fagReady("LEAD_HISTORY_LIST", function(el, api, colApi, gridApi){
//    $("#lead-history-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#lead-history-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#lead-history-clear-filter-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    	gridApi.refreshUserPreferences();
//    });
//	$("#sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#lead-history-export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//    
//    postLoadGridData(null, gridApi, "lead-history", loadRebateHistoryGrid);
//    
//    //loadRebateHistoryGrid(gridApi);
//   
//});
//
//var findLeadHistoryUrl= "";
//function loadRebateHistoryGrid(gridApi) {
//	
//	if(findLeadHistoryUrl == ""){
//		resetGridStatusBar();
//		findLeadHistoryUrl = getGridDataFetchUrl("LEAD_HISTORY_LIST");
//	}
//	
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(findLeadHistoryUrl != null && findLeadHistoryUrl != "" && findLeadHistoryUrl !="undefined"){
//		var formInput = $('#lead-history-search-form, #limitForm').serialize();
//		$.ajax({
//		  async: true,
//		  url:findLeadHistoryUrl,
//		  type:"POST",
//		  data: JSON.parse(JSON.stringify(formInput)),
//		  success: function(data){
//			  gridApi.setRowData(data.list);
//			  //setTimeout(() => colApi.autoSizeAllColumns(), 1000);
//		  }
//		});
//	}
//}


$(function() {
	let leadHistoryInstanceId= "LEAD_HISTORY_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = leadHistoryInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#lead-history-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, leadHistoryInstanceId, userId);
	});
	$("#lead-history-refresh-pref-btn").click(function () {
		gridInstance.refreshUserPreferences();
    });
	$('#lead-history-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, leadHistoryInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getLeadHistoryGridData();
		}
	});
	 $("#lead-history-export-btn").click(function () {
	    gridInstance.exportDataAsCsv();
	 });
	function getLeadHistoryGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("LEAD_HISTORY_LIST");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#lead-history-search-form, #limitForm_LEAD_HISTORY_LIST";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getLeadHistoryGridData();
	}
});
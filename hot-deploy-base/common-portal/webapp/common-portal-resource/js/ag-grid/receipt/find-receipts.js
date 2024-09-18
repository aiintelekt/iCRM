//fagReady("RECEIPT_LIST", function(el, api, colApi, gridApi){
//	$("#refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#clear-filter-btn").click(function () {
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
//    $("#export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//    $("#main-search-btn").click(function () {
//    	 loadReceipts(gridApi, api);
//    });
//    
//    postLoadGrid(api, gridApi, null, "c-receipt", loadReceipts);
//    postLoadGrid(api, gridApi, null, "c-receipts", loadReceipts);
//    
//    //loadReceipts(gridApi, api);
//	
//});
//
//var listReceiptsUrl = "partyReceiptData";
//function loadReceipts(gridApi, api) {
//	if(listReceiptsUrl == ""){
//		resetGridStatusBar();
//		listReceiptsUrl = getGridDataFetchUrl("RECEIPT_LIST");
//	}
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(listReceiptsUrl != null && listReceiptsUrl != "" && listReceiptsUrl !="undefined"){
//		api.showLoadingOverlay();
//		$.ajax({
//			async: false,
//			url:listReceiptsUrl,
//			type:"POST",
//			data: JSON.parse(JSON.stringify($("#findReceipts").serialize())),
//			success: function(data){
//				gridApi.setRowData(data.list);
//			}
//		});
//	}
//}

$(function() {
	let receiptsListInstanceId= "RECEIPT_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = receiptsListInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#receipts-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, receiptsListInstanceId, userId);
	});
	$("#receipts-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#receipts-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, receiptsListInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getReceiptListGridData();
		}
	});
	$('#receipts-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	  $("#main-search-btn").click(function () {
			getReceiptListGridData();
	});
	function getReceiptListGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "partyReceiptData";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#findReceipts";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getReceiptListGridData();
	}
});
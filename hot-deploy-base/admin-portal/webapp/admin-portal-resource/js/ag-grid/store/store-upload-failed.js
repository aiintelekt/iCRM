//fagReady("STORE_UPLOAD_FAILED", function(el, api, colApi, gridApi) {
//    $("#refresh-pref-btn").click(function() {
//        gridApi.refreshUserPreferences();
//    });
//    $("#save-pref-btn").click(function() {
//        gridApi.saveUserPreferences();
//    });
//    $("#clear-filter-btn").click(function() {
//        try {
//            gridApi.clearAllColumnFilters();
//        } catch (e) {}
//        gridApi.refreshUserPreferences();
//    });
//    $("#export-btn").click(function() {
//        gridApi.csvExport();
//    });
//    $("#search-btn").click(function() {
//        loadFailedListMainGrid(gridApi, api, colApi)
//    });
//    $("#insert-btn").click(function() {
//        gridApi.insertNewRow()
//    });
//    
//    $("#storeFailed-refresh").click(function() {
//    	loadFailedListMainGrid(gridApi, api, colApi);	
//    });
//    loadFailedListMainGrid(gridApi, api, colApi)
//});
//function loadFailedListMainGrid(gridApi,api,colApi) {
//    var rowData = [];
//    gridApi.setRowData(rowData);
//    api.showLoadingOverlay();
//    var formInput = {};
//    $.ajax({
//        async: false,
//        url: '/admin-portal/control/getStoreUploadFailedList',
//        type: "POST",
//        data: JSON.parse(JSON.stringify(formInput)),
//        success: function(data) {
//            gridApi.setRowData(data.data);
//            data.data = [];
//            paginateHandler(data);
//        }
//    });
//}


$(function() {
	let storeUploadFailedInstanceId= "STORE_UPLOAD_FAILED";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = storeUploadFailedInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#storeUploadFailed-save-filter-btn').click(function(){
		saveGridPreference(gridInstance, storeUploadFailedInstanceId, userId);
	});
	$('#storeUploadFailed-clear-pref-btn').click(function(){
		clearGridPreference(gridInstance,storeUploadFailedInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getStoreUploadFailedGridData();
		}
	});
	
    $('#storeUploadFailed-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
    $("#storeUploadFailed-export-btn").click(function() {
    	gridInstance.exportDataAsCsv();
	});
    $("#search-btn").click(function() {
		getStoreUploadFailedGridData();
    });
    $("#storeFailed-refresh").click(function() {
		getStoreUploadFailedGridData();
    });
	function getStoreUploadFailedGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/admin-portal/control/getStoreUploadFailedList";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm";
		callCtx.ajaxResponseKey = "data";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getStoreUploadFailedGridData();
	}
});

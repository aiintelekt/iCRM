//fagReady("STORE_UPLOAD_SUCCESS", function(el, api, colApi, gridApi) {
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
//        loadMainGrid(gridApi, api, colApi)
//    });
//    $("#insert-btn").click(function() {
//        gridApi.insertNewRow()
//    });
//    
//    $("#storeSuccess-refresh").click(function() {
//    	loadMainGrid(gridApi, api, colApi);	
//    });
//    loadMainGrid(gridApi, api, colApi)
//});
//function loadMainGrid(gridApi,api,colApi) {
//    var rowData = [];
//    gridApi.setRowData(rowData);
//    api.showLoadingOverlay();
//    var formInput = {};
//    $.ajax({
//        async: false,
//        url: '/admin-portal/control/getStoreUploadSuccessList',
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
	let storeUploadSuccessInstanceId= "STORE_UPLOAD_SUCCESS";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = storeUploadSuccessInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#storeSuccess-save-filter-btn').click(function(){
		saveGridPreference(gridInstance, storeUploadSuccessInstanceId, userId);
	});
	$('#storeSuccess-clear-pref-btn').click(function(){
		clearGridPreference(gridInstance,storeUploadSuccessInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getStoreUploadSuccessGridData();
		}
	});
	
    $('#storeSuccess-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
    $("#storeSuccess-export-btn").click(function() {
    	gridInstance.exportDataAsCsv();
	});
    $("#search-btn").click(function() {
		getStoreUploadSuccessGridData();
    });
    $("#storeSuccess-refresh").click(function() {
		getStoreUploadSuccessGridData();
    });
	function getStoreUploadSuccessGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/admin-portal/control/getStoreUploadSuccessList";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#storeSuccessForm";
		callCtx.ajaxResponseKey = "data";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getStoreUploadSuccessGridData();
	}
});

//fagReady("STORE_RECEIPTS_LIST", function(el, api, colApi, gridApi) {
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
//    $("#fetch-previous").click(function () {
//    	fetchPrevious();
//    	loadMainGrid(gridApi, api, colApi);
//    });
//    $("#fetch-next").click(function () {
//    	fetchNext();
//    	loadMainGrid(gridApi, api, colApi);
//    });
//    $("#fetch-first").click(function () {
//    	fetchFirst();
//    	loadMainGrid(gridApi, api, colApi);
//    });
//    $("#fetch-last").click(function () {
//    	fetchLast();
//    	loadMainGrid(gridApi, api, colApi);
//    });
//    $("#storeReceipt-search-btn").click(function() {
//    	loadMainGrid(gridApi, api, colApi);	
//    });
//    $("#delete_store_receipt").click(function(){
//    	
//    	var selectedData = api.getSelectedRows();
//    	if (selectedData.length > 0) {
//    		var productStoreId = "";
//    	    for (i = 0; i < selectedData.length; i++) {
//    	    	var data = selectedData[i];
//    	    	productStoreId += data.productStoreId+",";
//    	    }
//    	    productStoreId = productStoreId.substring(0, productStoreId.length - 1);
//    	    deleteProductStoreReceipt(productStoreId);
//    	}else{
//    		showAlert("error", "Please select store  to be delete!");
//    		return false;
//    	}
//    });
//    loadMainGrid(gridApi, api, colApi)
//});
//function loadMainGrid(gridApi,api,colApi) {
//    var rowData = [];
//    gridApi.setRowData(rowData);
//    api.showLoadingOverlay();
//    var formInput = $('#storeReceipt-search-form').serialize();
//    $.ajax({
//        async: false,
//        url: '/admin-portal/control/getStoreReceiptList',
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
	let storeReceiptInstanceId= "STORE_RECEIPTS_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = storeReceiptInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#storeReceipt-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, storeReceiptInstanceId, userId);
	});
	$('#storeReceipt-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, storeReceiptInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getStoreReceiptGridData();
		}
	});
    $('#storeReceipt-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
    $("#export-btn").click(function() {
        gridInstance.exportDataAsCsv();
    });
    $("#search-btn").click(function() {
		getStoreReceiptGridData();
    });
    $("#fetch-previous").click(function () {
    	fetchPrevious();
		getStoreReceiptGridData();
    });
    $("#fetch-next").click(function () {
    	fetchNext();
		getStoreReceiptGridData();
    });
    $("#fetch-first").click(function () {
    	fetchFirst();
		getStoreReceiptGridData();
    });
    $("#fetch-last").click(function () {
    	fetchLast();
		getStoreReceiptGridData();
    });
    $("#storeReceipt-search-btn").click(function() {
		getStoreReceiptGridData();
    });
    $("#delete_store_receipt").click(function(){
    	
    	var selectedData = gridInstance.getSelectedRows();
    	if (selectedData.length > 0) {
    		var productStoreId = "";
    	    for (i = 0; i < selectedData.length; i++) {
    	    	var data = selectedData[i];
    	    	productStoreId += data.productStoreId+",";
    	    }
    	    productStoreId = productStoreId.substring(0, productStoreId.length - 1);
    	    deleteProductStoreReceipt(productStoreId);
    	}else{
    		showAlert("error", "Please select store  to be delete!");
    		return false;
    	}
    });

	function getStoreReceiptGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/admin-portal/control/getStoreReceiptList";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#storeReceipt-search-form";
		callCtx.ajaxResponseKey = "data";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getStoreReceiptGridData();
	}
});
function deleteProductStoreReceipt(productStoreId){
	let input ={};
	input={"productStoreId":productStoreId};
	$.ajax({
        async: false,
        data: input,
        url: '/admin-portal/control/deleteStoreReceipt',
        type: "POST",
        success: function(data) {
        	if (data){
        		console.log(JSON.stringify(data));
        		var status = data["status"];
        		var message = data["message"];
        		if (status && status ==="SUCCESS"){
        			showAlert("success",message);
        			$("#storeReceipt-search-btn").trigger("click");
        		}else{
        			showAlert("error",message);
        		}
        	}
        	
        }
    });
}
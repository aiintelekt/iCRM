//fagReady("STORE_RECEIPTS_LIST", function (el, api, colApi, gridApi) {
//    $("#storereceipt-refresh-pref-btn").click(function () {
//        gridApi.refreshUserPreferences();
//    });
//    $("#storereceipt-save-pref-btn").click(function () {
//        gridApi.saveUserPreferences();
//    });
//    $("#storereceipt-clear-filter-btn").click(function () {
//        try {
//            gridApi.clearAllColumnFilters();
//        } catch (e) {}
//        gridApi.refreshUserPreferences();
//    });
//	$("#storereceipt-export-btn").click(function () {
//        gridApi.csvExport();
//    });
//
//    $("#refresh-storereceipt-btn").click(function () {
//        loadStoreReciptGrid(api, gridApi);
//    });
//    
//    $("#remove-storereceipt-btn").click(function () {
//    	var selectedData = api.getSelectedRows();
//    	if (selectedData.length > 0) {
//    		var domainEntityType = $("#findStoreReceipt input[name=domainEntityType]").val();
//    		var domainEntityId = $("#findStoreReceipt input[name=domainEntityId]").val();
//    		
//    		var selectedProductStoreIds = "";
//		    for (i = 0; i < selectedData.length; i++) {
//		    	var data = selectedData[i];
//		    	selectedProductStoreIds += data.productStoreId+",";
//		    }
//		    selectedProductStoreIds = selectedProductStoreIds.substring(0, selectedProductStoreIds.length - 1);
//        	
//        	var inputData = {"domainEntityType": domainEntityType, "domainEntityId": domainEntityId, "selectedProductStoreIds": selectedProductStoreIds};
//		    
//		    $.ajax({
//				type : "POST",
//				url : "/common-portal/control/removeStoreReceipt",
//				async : true,
//				data : inputData,
//				success : function(data) {
//					if (data.code == 200) {
//						showAlert ("success", data.message);
//						$("#refresh-storereceipt-btn").trigger('click');
//					} else {
//						showAlert ("error", data.message);
//					}
//				},
//				error : function() {
//					console.log('Error occured');
//					showAlert("error", "Error occured!");
//				},
//				complete : function() {
//				}
//			});
//    	} else {
//    		showAlert("error", "Please select atleast one record!");
//    	}
//    });
//
//    postLoadGridData(api, gridApi, "store-receipts", loadStoreReciptGrid);
//    
//    //loadStoreReciptGrid(api, gridApi);
//});
//
//function loadStoreReciptGrid(api, gridApi) {
//    var rowData =[];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//    
//    $.ajax({
//        async: true,
//        url: '/common-portal/control/searchProductStores',
//        type: "POST",
//        data: JSON.parse(JSON.stringify($("#findStoreReceipt").serialize())),
//        success: function (data) {
//            gridApi.setRowData(data.list);
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
    $("#storeReceipt-export-btn").click(function() {
        gridInstance.exportDataAsCsv();
    });
    $("#refresh-storereceipt-btn").click(function () {
		getStoreReceiptGridData();
    });
    
    $("#remove-storereceipt-btn").click(function () {
    	var selectedData = gridInstance.getSelectedRows();
    	if (selectedData.length > 0) {
    		var domainEntityType = $("#findStoreReceipt input[name=domainEntityType]").val();
    		var domainEntityId = $("#findStoreReceipt input[name=domainEntityId]").val();
    		
    		var selectedProductStoreIds = "";
		    for (i = 0; i < selectedData.length; i++) {
		    	var data = selectedData[i];
		    	selectedProductStoreIds += data.productStoreId+",";
		    }
		    selectedProductStoreIds = selectedProductStoreIds.substring(0, selectedProductStoreIds.length - 1);
        	
        	var inputData = {"domainEntityType": domainEntityType, "domainEntityId": domainEntityId, "selectedProductStoreIds": selectedProductStoreIds};
		    
		    $.ajax({
				type : "POST",
				url : "/common-portal/control/removeStoreReceipt",
				async : true,
				data : inputData,
				success : function(data) {
					if (data.code == 200) {
						showAlert ("success", data.message);
						$("#refresh-storereceipt-btn").trigger('click');
					} else {
						showAlert ("error", data.message);
					}
				},
				error : function() {
					console.log('Error occured');
					showAlert("error", "Error occured!");
				},
				complete : function() {
				}
			});
    	} else {
    		showAlert("error", "Please select atleast one record!");
    	}
    });
	function getStoreReceiptGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/searchProductStores";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#findStoreReceipt";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getStoreReceiptGridData();
	}
});
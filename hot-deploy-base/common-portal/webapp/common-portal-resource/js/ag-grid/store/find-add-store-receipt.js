//fagReady("STORE_RECEIPTS_LIST", function (el, api, colApi, gridApi) {
//    $("#add-storereceipt-refresh-pref-btn").click(function () {
//        gridApi.refreshUserPreferences();
//    });
//    $("#add-storereceipt-save-pref-btn").click(function () {
//        gridApi.saveUserPreferences();
//    });
//    $("#add-storereceipt-clear-filter-btn").click(function () {
//        try {
//            gridApi.clearAllColumnFilters();
//        } catch (e) {}
//        gridApi.refreshUserPreferences();
//    });
//	$("#add-storereceipt-export-btn").click(function () {
//        gridApi.csvExport();
//    });
//	
//	$('#add-store-receipt-modal').on('shown.bs.modal', function (e) {
//    	api.sizeColumnsToFit();
//	});
//	
//	$("#refresh-add-storereceipt-btn").click(function () {
//		loadAddStoreReciptGrid(api, gridApi);
//    })
//	
//    $("#add-storereceipt-btn").click(function () {
//    	var selectedData = api.getSelectedRows();
//    	if (selectedData.length > 0) {
//    		var domainEntityType = $("#findAddStoreReceipt input[name=domainEntityType]").val();
//    		var domainEntityId = $("#findAddStoreReceipt input[name=domainEntityId]").val();
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
//				url : "/common-portal/control/addStoreReceipt",
//				async : true,
//				data : inputData,
//				success : function(data) {
//					if (data.code == 200) {
//						showAlert ("success", data.message);
//						$('#add-store-receipt-modal').modal('hide');
//						$("#refresh-storereceipt-btn").trigger('click');
//						$("#refresh-add-storereceipt-btn").trigger('click');
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
//    postLoadGridData(api, gridApi, "store-receipts", loadAddStoreReciptGrid);
//    
//    //loadAddStoreReciptGrid(api, gridApi);
//});
//
//function loadAddStoreReciptGrid(api, gridApi) {
//    var rowData =[];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//    
//    $.ajax({
//        async: true,
//        url: '/common-portal/control/searchProductStores',
//        type: "POST",
//        data: JSON.parse(JSON.stringify($("#findAddStoreReceipt").serialize())),
//        success: function (data) {
//            gridApi.setRowData(data.list);
//        }
//    });
//}



$(function() {
	let findStoreReceiptInstanceId= "ADD_STORE_RECEIPTS_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = findStoreReceiptInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#add-storeReceipt-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, findStoreReceiptInstanceId, userId);
	});
	$('#add-storeReceipt-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, findStoreReceiptInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getFindStoreReceiptGridData();
		}
	});
    $('#add-storeReceipt-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
    $("#add-storeReceipt-export-btn").click(function() {
        gridInstance.exportDataAsCsv();
    });
	$('#add-store-receipt-modal').on('shown.bs.modal', function (e) {
		//gridInstance.sizeColumnsToFit();
	});
	
	$("#refresh-btn-add-storereceipt").click(function () {
		getFindStoreReceiptGridData();
	})
	
	$("#add-btn-storereceipt").click(function () {
		var selectedData = gridInstance.getSelectedRows();
		if (selectedData.length > 0) {
			var domainEntityType = $("#findAddStoreReceipt input[name=domainEntityType]").val();
			var domainEntityId = $("#findAddStoreReceipt input[name=domainEntityId]").val();
	    	
			var selectedProductStoreIds = "";
		    for (i = 0; i < selectedData.length; i++) {
		    	var data = selectedData[i];
		    	selectedProductStoreIds += data.productStoreId+",";
		    }
		    selectedProductStoreIds = selectedProductStoreIds.substring(0, selectedProductStoreIds.length - 1);
	    	
	    	var inputData = {"domainEntityType": domainEntityType, "domainEntityId": domainEntityId, "selectedProductStoreIds": selectedProductStoreIds};
		    
		    $.ajax({
				type : "POST",
				url : "/common-portal/control/addStoreReceipt",
				async : true,
				data : inputData,
				success : function(data) {
					if (data.code == 200) {
						showAlert ("success", data.message);
						$('#add-store-receipt-modal').modal('hide');
						$("#refresh-storereceipt-btn").trigger('click');
						$("#refresh-add-storereceipt-btn").trigger('click');
						$("#refresh-btn-add-storereceipt").trigger('click');
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
	function getFindStoreReceiptGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/searchProductStores";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#findAddStoreReceipt";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getFindStoreReceiptGridData();
	}
});
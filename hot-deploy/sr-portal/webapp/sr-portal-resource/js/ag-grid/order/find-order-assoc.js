//fagReady("LIST_SR_ORDERS_ASSOC", function(el, api, colApi, gridApi){
//    $("#order-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#order-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#order-clear-filter-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    	gridApi.refreshUserPreferences();
//    });
//	$("#order-sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#order-export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//
//    $("#refresh-order-btn").click(function () {
//    	loadorderGrid(gridApi);
//    });
//    
//    $("#order-remove-btn").click(function () {
//    	var selectedData = api.getSelectedRows();
//		if (selectedData.length > 0) {
//			console.log(selectedData);
//			
//		    var selectedEntryIds = "";
//		    for (i = 0; i < selectedData.length; i++) {
//		    	var data = selectedData[i];
//		    	selectedEntryIds += data.lineItemIdentifier+",";
//		    }
//		    selectedEntryIds = selectedEntryIds.substring(0, selectedEntryIds.length - 1);
//		    
//		    var inputData = {"selectedEntryIds": selectedEntryIds, "srNumber": $('#order-search-form input[name=srNumber]').val()};
//		    $.ajax({
//				type : "POST",
//				url : "/sr-portal/control/removeOrderLineAssocData",
//				async : true,
//				data : inputData,
//				success : function(result) {
//					if (result.code == 200) {
//						showAlert ("success", "SR and Order association removed!");
//						loadorderGrid(gridApi);
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
//			
//		} else {
//			showAlert("error", "Please select atleast one row to be removed!");
//		}
//    });
//    
//    postLoadGridData(null, gridApi, "sr-orders", loadorderGrid);
//    if ($('#isSrOrderAssoc').val() && $('#isSrOrderAssoc').val()==='Y') {
//    	loadorderGrid(gridApi);
//    }
//    
//    //loadorderGrid(gridApi);
//    //api.sizeColumnsToFit();
//});
//
//function loadorderGrid(gridApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	$.ajax({
//	  async: false,
//	  url:'/sr-portal/control/searchOrders',
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify($("#order-search-form").serialize())),
//	  success: function(result){
//		  gridApi.setRowData(result.data);
//	  }
//	});
//}

$(function() {
	let orderAssocListInstanceId= "LIST_SR_ORDERS_ASSOC";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject1 = {};
	formDataObject1.gridInstanceId = orderAssocListInstanceId;
	formDataObject1.externalLoginKey = externalLoginKey;
	formDataObject1.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject1);
	
	$('#order-assoc-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, orderAssocListInstanceId, userId);
	});
	$('#order-assoc-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, orderAssocListInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject1);
		if(gridInstance){
			getOrderAssocGridData();
		}
	});
	$('#order-assoc-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	  $("#refresh-order-btn").click(function () {
		getOrderAssocGridData();
	});
	$("#order-assoc-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$("#order-remove-btn").click(function () {
		var selectedData = gridInstance.getSelectedRows();
		if (selectedData.length > 0) {
			console.log(selectedData);
			
		    var selectedEntryIds = "";
		    for (i = 0; i < selectedData.length; i++) {
		    	var data = selectedData[i];
		    	selectedEntryIds += data.lineItemIdentifier+",";
		    }
		    selectedEntryIds = selectedEntryIds.substring(0, selectedEntryIds.length - 1);
		    
		    var inputData = {"selectedEntryIds": selectedEntryIds, "srNumber": $('#order-search-form input[name=srNumber]').val()};
		    $.ajax({
				type : "POST",
				url : "/sr-portal/control/removeOrderLineAssocData",
				async : true,
				data : inputData,
				success : function(result) {
					if (result.code == 200) {
						showAlert ("success", "SR and Order association removed!");
						getOrderAssocGridData();
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
			showAlert("error", "Please select atleast one row to be removed!");
		}
	});
	if ($('#isSrOrderAssoc').val() && $('#isSrOrderAssoc').val()==='Y') {
		getOrderAssocGridData();
    }
	function getOrderAssocGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/sr-portal/control/searchOrders";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#order-search-form";
		callCtx.ajaxResponseKey = "data";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getOrderAssocGridData();
	}
});
function viewSkuDescription(description){
	$('#show-des-modal_des_title').html("Sku Info");
	$('#show-des-modal_des_value').html(DOMPurify.sanitize(base64.decode(description)));
	$('#show-des-modal').modal("show");
}

function orderIdParams(params){
	return`<a target="_blank" href="/sr-portal/control/updateSrOrderAssoc?orderId=${params.data.orderId}&externalId=${params.data.externalId}&srNumber=${params.data.domainEntityId}&externalLoginKey=${params.data.externalLoginKey}" >${params.data.externalId}</a>`;
} 
function skuDescriptionParams(params) { 
	let skuDescription = params.data.skuDescription;
	let value = skuDescription; 
	if (skuDescription && skuDescription.length > 20) {
		value = skuDescription.substring(0, 20) + '<span onclick="viewSkuDescription(\'' + base64.encode(skuDescription) + '\')" class="btn btn-xs btn-primary m5 tooltips">...</span>'; 
	} 
	return value; 
}

function details(params){
	return `<a target="_blank" href="/common-portal/control/viewOrder?orderId=${params.data.orderId}&externalLoginKey=${params.data.externalLoginKey}" >view</a>`;
}

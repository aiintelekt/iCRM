//added for customer picker
//fagReady("ORDER_PICKER_LIST", function(el, api, colApi, gridApi){
//    $("#orderPicker-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#orderPicker-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#orderPicker-clear-filter-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    	gridApi.refreshUserPreferences();
//    });
//	$("#orderPicker-sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#orderPicker-export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//
//    $("#orderPicker-search-btn").click(function () {
//    	loadOrderPickerGrid(api, gridApi, colApi);
//    });
//    
//    $('#orderPicker').on('shown.bs.modal', function (e) {
//    	api.sizeColumnsToFit();
//	});
//    //To submit the form to while click the enter button
//    $("#find-order-form").on("keypress", function (event) {
//        var keyPressed = event.keyCode || event.which; 
//        if (keyPressed === 13) { 
//        	loadOrderPickerGrid(api,gridApi, colApi);
//            event.preventDefault();
//            return false; 
//        } 
//    });
//    //loadOrderPickerGrid(api, gridApi, colApi);
//});
//
//function loadOrderPickerGrid(api, gridApi, colApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	$.ajax({
//	  async: true,
//	  url:'/common-portal/control/searchOrders',
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify($("#find-order-form").serialize())),
//	  success: function(result){
//		  gridApi.setRowData(result.data);
//		  setTimeout(() => colApi.autoSizeAllColumns(), 1000);
//	  }
//	});
//}


$(function() {
	let orderPickerInstanceId= "ORDER_PICKER_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = orderPickerInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#orderPicker-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, orderPickerInstanceId, userId);
	});
	$('#orderPicker-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, orderPickerInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getOrderPickerGridData();
		}
	});
	
	$('#order-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});
	
	  $("#orderPicker-search-btn").click(function () {
			getOrderPickerGridData();
	});
	
	$('#orderPicker').on('shown.bs.modal', function (e) {
		gridInstance.sizeColumnsToFit();
	});
	//To submit the form to while click the enter button
	$("#find-order-form").on("keypress", function (event) {
	    var keyPressed = event.keyCode || event.which; 
	    if (keyPressed === 13) { 
			getOrderPickerGridData();
	        event.preventDefault();
	        return false; 
	    } 
	});
	function getOrderPickerGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/searchOrders";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#find-order-form";
		callCtx.ajaxResponseKey = "data";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getOrderPickerGridData();
	}
});
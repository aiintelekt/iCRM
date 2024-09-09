//added for customer picker
//fagReady("CUSTOMER_PICKER_LIST", function(el, api, colApi, gridApi){
//    $("#customerPicker-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#customerPicker-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#customerPicker-clear-filter-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    	gridApi.refreshUserPreferences();
//    });
//	$("#customerPicker-sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#customerPicker-export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//
//    $("#customerPicker-search-btn").click(function () {
//    	loadCustomerPickerGrid(gridApi, api, colApi);
//    });
//    
//    $('#customerPicker').on('shown.bs.modal', function (e) {
//    	loadCustomerPickerGrid(gridApi, api, colApi);
//    	api.sizeColumnsToFit();
//	});
//  //To submit the form to while click the enter button
//    $("#findCustForm").on("keypress", function (event) {
//        var keyPressed = event.keyCode || event.which; 
//        if (keyPressed === 13) { 
//        	loadCustomerPickerGrid(gridApi, api, colApi);
//            event.preventDefault();
//            return false; 
//        } 
//    });
//});
//
//function loadCustomerPickerGrid(gridApi, api, colApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	$.ajax({
//	  async: true,
//	  url:'/common-portal/control/searchCustomers',
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify($("#findCustForm").serialize())),
//	  success: function(data){
//		  gridApi.setRowData(data.list);
//	  }
//	});
//}


$(function() {
	let customerPickerInstanceId= "CUSTOMER_PICKER_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = customerPickerInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#customerPicker-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, customerPickerInstanceId, userId);
	});
	$('#customerPicker-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, customerPickerInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getCustomerPickerGridData();
		}
	});
	
	$('#customerPicker-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	  $("#customerPicker-search-btn").click(function () {
			getCustomerPickerGridData();
	});
	
	$('#customerPicker').on('shown.bs.modal', function (e) {
		getCustomerPickerGridData();
		gridInstance.sizeColumnsToFit();
	});
	//To submit the form to while click the enter button
	$("#findCustForm").on("keypress", function (event) {
	    var keyPressed = event.keyCode || event.which; 
	    if (keyPressed === 13) { 
			getCustomerPickerGridData();
	        event.preventDefault();
	        return false; 
	    } 
	});
	function getCustomerPickerGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/searchCustomers";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#findCustForm";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getCustomerPickerGridData();
	}
});

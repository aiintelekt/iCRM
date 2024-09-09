//added for customer picker
//fagReady("CONTRACTOR_PICKER_LIST", function(el, api, colApi, gridApi){
//    $("#contractorPicker-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#contractorPicker-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#contractorPicker-clear-filter-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    	gridApi.refreshUserPreferences();
//    });
//	$("#contractorPicker-sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#contractorPicker-export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//
//    $("#contractorPicker-search-btn").click(function () {
//    	loadContractorPickerGrid(gridApi, api, colApi);
//    });
//    
//    $('#contractorPicker').on('shown.bs.modal', function (e) {
//    	loadContractorPickerGrid(gridApi, api, colApi);
//    	//colApi.sizeColumnsToFit();
//	});
//  //To submit the form to while click the enter button
//    $("#findContractorForm").on("keypress", function (event) {
//        var keyPressed = event.keyCode || event.which; 
//        if (keyPressed === 13) { 
//        	loadContractorPickerGrid(gridApi, api, colApi);
//            event.preventDefault();
//            return false; 
//        } 
//    });
//});
//
//function loadContractorPickerGrid(gridApi, api, colApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	$.ajax({
//	  async: true,
//	  url:'/common-portal/control/searchCustomers',
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify($("#findContractorForm").serialize())),
//	  success: function(data){
//		  gridApi.setRowData(data.list);
//		  setTimeout(() => colApi.autoSizeAllColumns(), 1000);
//	  }
//	});
//}

$(function() {
	let contractorPickerInstanceId= "CONTRACTOR_PICKER_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = contractorPickerInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#contractorPicker-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, contractorPickerInstanceId, userId);
	});
	$('#contractorPicker-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, contractorPickerInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getContractorPickerGridData();
		}
	});
	  $("#contractorPicker-search-btn").click(function () {
			getContractorPickerGridData();
	});
	  $('#contractorPicker-sub-filter-clear-btn').click(function(){
			gridInstance.setFilterModel(null);
		});
	$('#contractorPicker').on('shown.bs.modal', function (e) {
		getContractorPickerGridData();
		//colApi.sizeColumnsToFit();
	});
	//To submit the form to while click the enter button
	$("#findContractorForm").on("keypress", function (event) {
	    var keyPressed = event.keyCode || event.which; 
	    if (keyPressed === 13) { 
			getContractorPickerGridData();
	        event.preventDefault();
	        return false; 
	    } 
	});
	function getContractorPickerGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/searchCustomers";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#findContractorForm";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getContractorPickerGridData();
	}
});
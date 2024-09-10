
//fagReady("PICKER_PARTY_LIST", function(el, api, colApi, gridApi){
//    $("#dealer-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#dealer-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#dealer-clear-filter-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    	gridApi.refreshUserPreferences();
//    });
//	$("#dealer-sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//
//    $("#dealerPicker-search-btn").click(function () {
//    	loadDealerPickerGrid(gridApi);
//    });
//    
//    $('#dealerPicker').on('shown.bs.modal', function (e) {
//    	loadDealerPickerGrid(gridApi);
//    	api.sizeColumnsToFit();
//	});
//    //To submit the form to while click the enter button
//    $("#findDealerForm").on("keypress", function (event) {
//        var keyPressed = event.keyCode || event.which; 
//        if (keyPressed === 13) { 
//        	loadDealerPickerGrid(gridApi);
//            event.preventDefault();
//            return false; 
//        } 
//    });
//});
//
//function loadDealerPickerGrid(gridApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	$.ajax({
//	  async: true,
//	  url:'/common-portal/control/searchPartys',
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify($("#findDealerForm").serialize())),
//	  success: function(data){
//		  gridApi.setRowData(data);
//	  }
//	});
//}


$(function() {
	let orderdealerPickerInstanceId= "DEALER_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = orderdealerPickerInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#findOrderDealerPicker-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, orderdealerPickerInstanceId, userId);
	});
	$('#findOrderDealerPicker-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, orderdealerPickerInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getDealersPickerGridData();
		}
	});
	  $("#findOrderDealerPicker-search-btn").click(function () {
		  getDealersPickerGridData();
	});
	  
	  $('#findOrderDealerPicker-sub-filter-clear-btn').click(function(){
			gridInstance.setFilterModel(null);
		});
	$('#findOrderDealerPicker').on('shown.bs.modal', function (e) {
		getDealersPickerGridData();
	});
	$("#findOrderDealerPicker-export-btn").click(function () {
		gridInstance.exportDataAsCsv();
	});
	//To submit the form to while click the enter button
	$("#findOrderDealerForm").on("keypress", function (event) {
	    var keyPressed = event.keyCode || event.which; 
	    if (keyPressed === 13) { 
	    	getDealersPickerGridData();
	        event.preventDefault();
	        return false; 
	    } 
	});
	function getDealersPickerGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/searchPartys";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#findOrderDealerForm";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getDealersPickerGridData();
	}
});
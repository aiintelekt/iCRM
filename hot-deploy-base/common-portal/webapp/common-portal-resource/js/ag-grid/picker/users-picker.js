//fagReady("USER_PICKER_LIST", function(el, api, colApi, gridApi){
//    $("#user-picker-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#user-picker-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#user-picker-clear-filter-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    	gridApi.refreshUserPreferences();
//    });
//	$("#sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#user-picker-export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//    $("#user-picker-search-btn").click(function () {
//    	loadUserPickerGrid(gridApi, api, colApi);
//    });
//
//    //loadUserPickerGrid(gridApi, api);
//});
//
//function loadUserPickerGrid(gridApi, api) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	var formInput = $('#findUserForm').serialize();
//	api.showLoadingOverlay();
//	$.ajax({
//	  async: true,
//	  url:'/common-portal/control/getUsersList?isIncludeLoggedInUser=Y&isIncludeInactiveUser=N',
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify(formInput)),
//	  success: function(data){
//		  gridApi.setRowData(data);
//	  }
//	});
//}



$(function() {
	let userPickerInstanceId= "USER_PICKER_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = userPickerInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#userPicker-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, userPickerInstanceId, userId);
	});
	$('#userPicker-clear-pref-btn').click(function(){
		clearGridPreference(gridInstance, userPickerInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getUserPickerGridData();
		}
	});
	$("#user-picker-search-btn").click(function () {
		getUserPickerGridData();
    });
    
    $('#userPicker-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});

	function getUserPickerGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/getUsersList?isIncludeLoggedInUser=Y&isIncludeInactiveUser=N";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#findUserForm";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getUserPickerGridData();
	}
});



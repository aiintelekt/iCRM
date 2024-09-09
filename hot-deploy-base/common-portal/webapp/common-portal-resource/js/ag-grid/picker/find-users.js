/*
fagReady("PICKER_USERS_LIST", function(el, api, colApi, gridApi){
    $("#reassignPicker-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#reassignPicker-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#reassignPicker-clear-filter-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    	gridApi.refreshUserPreferences();
    });
	$("#sub-filter-clear-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    });
    $("#reassignPicker-export-btn").click(function () {
    	gridApi.csvExport();
    });
    $("#sr-reassign-search-btn").click(function () {
    	loadUserPickerGrid(gridApi, api);
    });

    //loadUserPickerGrid(gridApi, api);
});

function loadUserPickerGrid(gridApi, api) {
	var rowData =[];
	gridApi.setRowData(rowData);
	var formInput = $('#searchReassignForm').serialize();
	api.showLoadingOverlay();
	$.ajax({
	  async: true,
	  url:'/common-portal/control/getUsersList?isIncludeLoggedInUser=Y&isIncludeInactiveUser=N',
	  type:"POST",
	  data: JSON.parse(JSON.stringify(formInput)),
	  success: function(data){
		  gridApi.setRowData(data);
	  }
	});
}
*/


$(function() {
	let pickerUserInstanceId= "PICKER_USERS_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = pickerUserInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	function getUsersGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/getUsersList?isIncludeLoggedInUser=Y&isIncludeInactiveUser=N";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchReassignForm";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getUsersGridData();
	}
	$('#reassignPicker-save-pref').click(function(){
		saveGridPreference(gridInstance, pickerUserInstanceId, userId);
	});
	
	$('#reassignPicker-clear-pref').click(function(){
		clearGridPreference(gridInstance, pickerUserInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getUsersGridData();
		}
	});
	$('#reassignPicker-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});
	$('#sr-reassign-search-btn').click(function(){
		getUsersGridData();
	});
});


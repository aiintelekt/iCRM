//
//fagReady("DESIGNATION_LIST", function(el, api, colApi, gridApi){
//	$("#designation-refresh-pref-btn").click(function () {
//		gridApi.refreshUserPreferences();
//	});
//	$("#designation-save-pref-btn").click(function () {
//		gridApi.saveUserPreferences();
//	});
//	$("#designation-clear-filter-btn").click(function () {
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
//	$(document).on("click", ".designationModal", function () {
//	     var formName = $(this).data('value');
//	     loadPartyDesignationGrid(gridApi, api, colApi, formName);
//	});
//    
//});
//
//function loadPartyDesignationGrid(gridApi, api, colApi, formName) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	var formInput = $('#'+formName).serialize();
//	$.ajax({
//	  async: false,
//	  url: "/common-portal/control/getContactsDetails",
//	  type: "POST",
//	  data: JSON.parse(JSON.stringify(formInput)),
//	  success: function(event){
//		  gridApi.setRowData(event.data);
//		  setTimeout(() => colApi.autoSizeAllColumns(), 500);
//	  }
//	});
//}




$(function() {
	let listOfDesignationInstanceId= "DESIGNATION_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	var formNameDesignationList = "";

	const formDataObject = {};
	formDataObject.gridInstanceId = listOfDesignationInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	

	gridInstance = prepareGridInstance(formDataObject);

	$('#designation-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, listOfDesignationInstanceId, userId);
	});
	$('.designationModal').on("click", function () {
		formNameDesignationList = $(this).data('value');
		getListOfDesignationGridData(formNameDesignationList);
	});
	$('#designation-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, listOfDesignationInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getListOfDesignationGridData(formNameDesignationList);
		}
	});
	$('#designation-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});

	function getListOfDesignationGridData(formNameDesignationList){
		gridInstance.showLoadingOverlay();

		let name3= '#'+formNameDesignationList;
		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/getContactsDetails";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = name3;
		callCtx.ajaxResponseKey = "data";

		setGridData(gridInstance, callCtx);
	}
	if(gridInstance && formNameDesignationList !=""){
		getListOfDesignationGridData(formNameDesignationList);
	}
});
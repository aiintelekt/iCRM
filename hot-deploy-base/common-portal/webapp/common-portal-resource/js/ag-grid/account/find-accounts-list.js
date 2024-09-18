//fagReady("ACCOUNT_PICKER", function(el, api, colApi, gridApi) {
//    $("#acc-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#acc-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//	$("#acc-sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#acc-clear-filter-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    	gridApi.refreshUserPreferences();
//    });
//    $("#acc-export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//
//    $("#acc-refresh-btn").click(function () {
//    	loadAccountPickerGrid(gridApi, api, colApi);
//    })
//
//    $("#acc-search-btn").click(function () {
//    	loadAccountPickerGrid(gridApi, api, colApi);
//    });
//   
//    //loadAccountPickerGrid(gridApi, api, colApi);
//    
//});
//
//function loadAccountPickerGrid(gridApi, api, colApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	var formInput = $('#acc-searchForm').serialize();
//	$.ajax({
//	  async: true,
//	  url:'/common-portal/control/searchAccounts',
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify(formInput)),
//	  success: function(data){
//		  gridApi.setRowData(data.data);
//		  setTimeout(() => colApi.autoSizeAllColumns(), 1000);
//		  data.data=[];
//		  paginateHandler(data);
//	  }
//	});
//}

$(function() {
	const accountPickerListInstanceId= "ACCOUNT_PICKER";
	const externalLoginKey = $("#externalLoginKey").val();
	const formDataObject = {};
	const userId = $("#userId").val();
	let gridInstance  = "";

	formDataObject.gridInstanceId = accountPickerListInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;

	gridInstance = prepareGridInstance(formDataObject);

	if(gridInstance){
		accountPickerListData();
	}

	function accountPickerListData() {
		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/searchAccounts";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#acc-searchForm";
		callCtx.ajaxResponseKey = "data";

		setGridData(gridInstance, callCtx);
	}

	$('#acc-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, accountPickerListInstanceId, userId);
	});

	$('#acc-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, accountPickerListInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			accountPickerListData();
		}
	});

	$('#acc-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#acc-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$("#acc-search-btn").click(function() {
		accountPickerListData();
	});
});
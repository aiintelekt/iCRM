//fagReady("PARTY_EMAIL_LIST", function(el, api, colApi, gridApi){
//    $("#email-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#email-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#email-clear-filter-btn").click(function () {
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
//    
//    $(document).on("click", ".emailModal", function () {
//	     var formName = $(this).data('value');
//	     loadPartyEmailGrid(gridApi, api, colApi, formName);
//	});
//});
//
//function loadPartyEmailGrid(gridApi, api, colApi, formName) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	var formInput = $('#'+formName).serialize();
//	$.ajax({
//	  async: false,
//	  url: "/common-portal/control/getContactsDetails",
//      type: "POST",
//      data: JSON.parse(JSON.stringify(formInput)),
//      success: function(event){
//		  gridApi.setRowData(event.data);
//		  setTimeout(() => colApi.autoSizeAllColumns(), 500);
//	  }
//	});
//}


$(function() {
	let listOfEmailInstanceId= "PARTY_EMAIL_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
    var formName = "";

	const formDataObject = {};
	formDataObject.gridInstanceId = listOfEmailInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#email-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, listOfEmailInstanceId, userId);
	});
	$('.emailModal').on("click", function () {
		formName = $(this).data('value');
		getListOfEmailGridData(formName);
	});
	$('#email-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, listOfEmailInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getListOfEmailGridData(formName);
		}
	});
	$('#email-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	
	function getListOfEmailGridData(formName){
		gridInstance.showLoadingOverlay();

		let name2= '#'+formName;
		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/getContactsDetails";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = name2;
		callCtx.ajaxResponseKey = "data";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance && formName){
		getListOfEmailGridData(formName);
	}
});

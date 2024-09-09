//fagReady("PARTY_PHONE_LIST", function(el, api, colApi, gridApi){
//    $("#phone-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#phone-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#phone-clear-filter-btn").click(function () {
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
//    $(document).on("click", ".phoneModal", function () {
//	     var formName = $(this).data('value');
//	     loadPartyPhoneGrid(gridApi, api, colApi, formName);
//	});
//});
//
//function loadPartyPhoneGrid(gridApi, api, colApi, formName) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	var formInput = $('#'+formName).serialize();
//	$.ajax({
//	  async: false,
//	  url: "/common-portal/control/getContactsDetails",
//      type: "POST",
//      data: JSON.parse(JSON.stringify(formInput)),
//	  success: function(event){
//		  gridApi.setRowData(event.data);
//		  setTimeout(() => colApi.autoSizeAllColumns(), 500);
//	  }
//	});
//}

$(function() {
	let partyPhoneInstanceId= "PARTY_PHONE_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	var formName = "";

	const formDataObject = {};
	formDataObject.gridInstanceId = partyPhoneInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	

	gridInstance = prepareGridInstance(formDataObject);

	$('#phone-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, partyPhoneInstanceId, userId);
	});
	$('.phoneModal').on("click", function () {
		formName = $(this).data('value');
		getPartyPhoneGridData(formName);
	});

	$('#phone-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, partyPhoneInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getPartyPhoneGridData(formName);
		}
	});
	$('#phone-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});

	function getPartyPhoneGridData(formName){
		gridInstance.showLoadingOverlay();

		let name1= '#'+formName;
		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/getContactsDetails";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = name1;
		callCtx.ajaxResponseKey = "data";

		setGridData(gridInstance, callCtx);
	}
	if(gridInstance && formName){
		getPartyPhoneGridData(formName);
	}
});

function partyContactNumber(params) { 
	return '<a href="#" onclick="addPhone(\'' + params.data.rowId+ '\',\'' + params.data.contactPartyId + '\',\'' + params.data.partyId + '\',\'' + params.data.contactMechId + '\',\'' + params.data.partyRelAssocId + '\',\''+params.data.targetRoleTypeId+'\')">'+params.data.contactNumber+'</a>'; 
}
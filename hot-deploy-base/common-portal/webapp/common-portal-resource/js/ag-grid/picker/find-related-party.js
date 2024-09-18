//fagReady("ADD_RELATED_PARTY_LIST", function(el, api, colApi, gridApi){
//    $("#refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#clear-filter-btn").click(function () {
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
//    $("#export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//
//    $("#relatedParty-search-btn").click(function () {
//    	loadRelatedPartyPickerGrid(gridApi, api, colApi);
//    });
//    
//    $('#add-related-party').on('shown.bs.modal', function (e) {
//    	api.sizeColumnsToFit();
//	});
//    
//    $("#relatedParty-refresh-btn").click(function () {
//    	loadRelatedPartyPickerGrid(gridApi, api, colApi);
//    })
//    
//    //loadRelatedPartyPickerGrid(gridApi, api, colApi);
//    
//});
//
//function loadRelatedPartyPickerGrid(gridApi, api, colApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	$.ajax({
//	  async: true,
//	  url:'/common-portal/control/searchPartys',
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify($("#findPartyForm").serialize())),
//	  success: function(data){
//		  gridApi.setRowData(data);
//	  }
//	});
//}

$(function() {
	let relatedPartyListInstanceId= "ADD_RELATED_PARTY_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = relatedPartyListInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#related-party-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, relatedPartyListInstanceId, userId);
	});
	$('#related-party-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, relatedPartyListInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getRelatedPartyListGridData();
		}
	});
	$('#related-party-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#relatedParty-search-btn").click(function () {
		getRelatedPartyListGridData();
	});
	
	$('#add-related-party').on('shown.bs.modal', function (e) {
		gridInstance.sizeColumnsToFit();
	});
	
	$("#relatedParty-refresh-btn").click(function () {
		getRelatedPartyListGridData();
	});
	function getRelatedPartyListGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/searchPartys";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#findPartyForm";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getRelatedPartyListGridData();
	}
});
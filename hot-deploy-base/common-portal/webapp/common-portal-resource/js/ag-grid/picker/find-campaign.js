//fagReady("PICKER_CAMPAIGN_LIST", function(el, api, colApi, gridApi){
//    $("#campaignPicker-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#campaignPicker-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#campaignPicker-clear-filter-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    	gridApi.refreshUserPreferences();
//    });
//	$("#sub-filter-clear-btn").click(function () {
//	    	try{
//	    		gridApi.clearAllColumnFilters();
//	    	}catch(e){
//	    	}
//	    });
//    $("#campaignPicker-export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//
//    $("#campaignPicker-search-btn").click(function () {
//    	loadCampaignPickerGrid(gridApi, api, colApi);
//    });
//    
//    $("#campaignPicker-refresh-btn").click(function () {
//    	loadCampaignPickerGrid(gridApi, api, colApi);
//    });
//    
//    $('#campaignPicker').on('shown.bs.modal', function (e) {
//    	api.sizeColumnsToFit();
//	});
//    //To submit the form to while click the enter button
//    $("#findCampaignsForm").on("keypress", function (event) {
//        var keyPressed = event.keyCode || event.which; 
//        if (keyPressed === 13) { 
//        	loadCampaignPickerGrid(gridApi, api, colApi);
//            event.preventDefault();
//            return false; 
//        } 
//    });
//    
//    //postLoadGrid(api, gridApi, colApi, "contact-campaigns", loadInvoices);
//    //postLoadGrid(api, gridApi, colApi, "lead-campaigns", loadInvoices);
//    //postLoadGrid(api, gridApi, colApi, "c-campaigns", loadInvoices);
//    //postLoadGrid(api, gridApi, colApi, "a-campaigns", loadInvoices);
//    
//    //loadCampaignPickerGrid(api, gridApi, colApi);
//    
//});
//
//function loadCampaignPickerGrid(gridApi, api, colApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	
//	$.ajax({
//	  async: true,
//	  url:'/common-portal/control/searchCampaigns',
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify($("#findCampaignsForm").serialize())),
//	  success: function(data){
//		  gridApi.setRowData(data.list);
//	  }
//	});
//}

$(function() {
	let campaignPickerInstanceId= "PICKER_CAMPAIGN_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = campaignPickerInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#campaignPicker-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, campaignPickerInstanceId, userId);
	});
	$('#campaignPicker-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, campaignPickerInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			CampaignGridData();
		}
	});
	$('#campaignPicker-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#campaignPicker-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	 $("#campaignPicker-search-btn").click(function () {
			CampaignGridData();
	});
	
	$("#campaignPicker-refresh-btn").click(function () {
		CampaignGridData();
	});
	
	$('#campaignPicker').on('shown.bs.modal', function (e) {
		gridInstance.sizeColumnsToFit();
	});
	//To submit the form to while click the enter button
	$("#findCampaignsForm").on("keypress", function (event) {
	    var keyPressed = event.keyCode || event.which; 
	    if (keyPressed === 13) { 
			CampaignGridData();
	        event.preventDefault();
	        return false; 
	    } 
	});
	function CampaignGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/searchCampaigns";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#findCampaignsForm";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		CampaignGridData();
	}
});

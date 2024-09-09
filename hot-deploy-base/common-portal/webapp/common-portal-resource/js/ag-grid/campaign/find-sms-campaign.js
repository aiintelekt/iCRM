//fagReady("CUST_SMS_CAMPAIGN_GRID", function (el, api, colApi, gridApi) {
//	$("#sms-campaign-refresh-pref-btn").click(function () {
//		gridApi.refreshUserPreferences();
//	});
//	$("#sms-campaign-save-pref-btn").click(function () {
//		gridApi.saveUserPreferences();
//	});
//	$("#sms-campaign-clear-filter-btn").click(function () {
//		try {
//			gridApi.clearAllColumnFilters();
//		} catch (e) {}
//		gridApi.refreshUserPreferences();
//	});
//	$("#sub-filter-clear-btn").click(function () {
//		try{
//			gridApi.clearAllColumnFilters();
//		}catch(e){
//		}
//	});
//	$("#export-btn").click(function () {
//		gridApi.csvExport();
//	});
//
//	$("#update-btn").click(function () {
//		gridApi.saveUpdates();
//		setTimeout(() => {
//			loadSmsCampaignGrid(gridApi, api, colApi);
//		}, 1000);
//	})
//
//	$("#main-search-btn").click(function () {
//		if (!$("#campaignType").val()) {
//			api.sizeColumnsToFit();
//			loadSmsCampaignGrid(gridApi, api, colApi);
//		}
//	});
//	$("#insert-btn").click(function () {
//		gridApi.insertNewRow()
//	})
//	$("#remove-btn").click(function () {
//		// removeMainGrid(fag1, api);
//		gridApi.removeSelected();
//		setTimeout(() => {
//			loadSmsCampaignGrid(gridApi, api, colApi);
//		}, 1000);
//	});
//
//	$("#refresh-sms-campaign-btn").click(function () {
//		loadSmsCampaignGrid(gridApi, api, colApi);
//	})
//
//	postLoadGrid(api, gridApi, colApi, "cc-sms", loadSmsCampaignGrid);	
//
//	//loadSmsCampaignGrid(api, gridApi);
//});
//
//function loadSmsCampaignGrid(gridApi, api, colApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//
//	$.ajax({
//		async: true,
//		url: '/common-portal/control/searchCampaigns',
//		type: "POST",
//		data: JSON.parse(JSON.stringify($("#findSmsMarketingCampaigns").serialize())),
//		success: function (data) {
//			gridApi.setRowData(data.list);
//		}
//	});
//}

$(function() {
	let smsCampaignInstanceId= "CUST_SMS_CAMPAIGN_GRID";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = smsCampaignInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#sms-campaign-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, smsCampaignInstanceId, userId);
	});
	$('#sms-campaign-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, smsCampaignInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getEmailCampaignsGridData();
		}
	});
	$("#main-search-btn").click(function () {
		if (!$("#campaignType").val()) {
			getSmsCampaignsGridData();
		}
	});
	$('#sms-campaign-sub-filter-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#refresh-sms-campaign-btn").click(function () {
		getSmsCampaignsGridData();
	});
	$("#sms-campaign-export-btn").click(function () {
	    gridInstance.exportDataAsCsv();
	});
	function getSmsCampaignsGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/searchCampaigns";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#findSmsMarketingCampaigns";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getSmsCampaignsGridData();
	}
});
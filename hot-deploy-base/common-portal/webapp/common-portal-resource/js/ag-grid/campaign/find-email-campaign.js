//fagReady("CUST_EMAIL_CAMPAIGN_GRID", function (el, api, colApi, gridApi) {
//	$("#email-campaign-refresh-pref-btn").click(function () {
//		gridApi.refreshUserPreferences();
//	});
//	$("#email-campaign-save-pref-btn").click(function () {
//		gridApi.saveUserPreferences();
//	});
//	$("#email-campaign-clear-filter-btn").click(function () {
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
//			loadEmailCampaignGrid(gridApi, api, colApi);
//		}, 1000);
//	})
//
//	$("#main-search-btn").click(function () {
//		api.sizeColumnsToFit();
//		loadEmailCampaignGrid(gridApi, api, colApi);
//	});
//	$("#insert-btn").click(function () {
//		gridApi.insertNewRow()
//	})
//	$("#remove-btn").click(function () {
//		// removeMainGrid(fag1, api);
//		gridApi.removeSelected();
//		setTimeout(() => {
//			loadEmailCampaignGrid(gridApi, api, colApi);
//		}, 1000);
//	});
//
//	$("#refresh-email-campaign-btn").click(function () {
//		loadEmailCampaignGrid(gridApi, api, colApi);
//	})
//
//	postLoadGrid(api, gridApi, colApi, "cc-email", loadEmailCampaignGrid);	
//
//	//loadEmailCampaignGrid(api, gridApi, colApi);
//});
//
//function loadEmailCampaignGrid(gridApi, api, colApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//
//	$.ajax({
//		async: true,
//		url: '/common-portal/control/searchCampaigns',
//		type: "POST",
//		data: JSON.parse(JSON.stringify($("#findEmailMarketingCampaigns").serialize())),
//		success: function (data) {
//			gridApi.setRowData(data.list);
//		}
//	});
//}
$(function() {
	let emailCampaignInstanceId= "CUST_EMAIL_CAMPAIGN_GRID";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = emailCampaignInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	

	gridInstance = prepareGridInstance(formDataObject);

	$('#email-campaign-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, emailCampaignInstanceId, userId);
	});
	$('#email-campaign-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, emailCampaignInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getEmailCampaignsGridData();
		}
	});
	$("#main-search-btn").click(function () {
		getEmailCampaignsGridData();
	});

	$("#refresh-email-campaign-btn").click(function () {
		getEmailCampaignsGridData();
	});
	$('#email-campaign-sub-filter-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#email-campaign-export-btn").click(function () {
	    gridInstance.exportDataAsCsv();
	});
	function getEmailCampaignsGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/searchCampaigns";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#findEmailMarketingCampaigns";
		callCtx.ajaxResponseKey = "list";

		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getEmailCampaignsGridData();
	}
});

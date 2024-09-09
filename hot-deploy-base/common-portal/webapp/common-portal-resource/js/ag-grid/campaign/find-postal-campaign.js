//fagReady("CUST_POSTAL_CAMPAIGN_GRID", function (el, api, colApi, gridApi) {
//	$("#postal-campaign-refresh-pref-btn").click(function () {
//		gridApi.refreshUserPreferences();
//	});
//	$("#postal-campaign-save-pref-btn").click(function () {
//		gridApi.saveUserPreferences();
//	});
//	$("#postal-campaign-clear-filter-btn").click(function () {
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
//			loadPostalCampaignGrid(gridApi, api, colApi);
//		}, 1000);
//	})
//
//	$("#main-search-btn").click(function () {
//		if (!$("#campaignType").val()) {
//			api.sizeColumnsToFit();
//			loadPostalCampaignGrid(gridApi, api, colApi);
//		}
//	});
//	$("#insert-btn").click(function () {
//		gridApi.insertNewRow()
//	})
//	$("#remove-btn").click(function () {
//		// removeMainGrid(fag1, api);
//		gridApi.removeSelected();
//		setTimeout(() => {
//			loadPostalCampaignGrid(gridApi, api, colApi);
//		}, 1000);
//	});
//
//	$("#refresh-postal-campaign-btn").click(function () {
//		loadPostalCampaignGrid(gridApi, api, colApi);
//	})
//
//	postLoadGrid(api, gridApi, colApi, "cc-postal", loadPostalCampaignGrid);	
//
//	//loadPostalCampaignGrid(api, gridApi);
//});
//
//function loadPostalCampaignGrid(gridApi, api, colApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//
//	$.ajax({
//		async: true,
//		url: '/common-portal/control/searchCampaigns',
//		type: "POST",
//		data: JSON.parse(JSON.stringify($("#findPostalMarketingCampaigns").serialize())),
//		success: function (data) {
//			gridApi.setRowData(data.list);
//		}
//	});
//}


$(function() {
	let postalCampaignInstanceId= "CUST_POSTAL_CAMPAIGN_GRID";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = postalCampaignInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#postal-campaign-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, postalCampaignInstanceId, userId);
	});
	$('#postal-campaign-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, postalCampaignInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getPostalCampaignsGridData();
		}
	});
	$("#main-search-btn").click(function () {
		if (!$("#campaignType").val()) {
			getPostalCampaignsGridData();
		}
	});
	$('#postal-campaign-sub-filter-btn').click(function(){
		gridInstance.setFilterModel(null);
	});

	$("#refresh-postal-campaign-btn").click(function () {
		getPostalCampaignsGridData();
	});
	$("#postal-campaign-export-btn").click(function () {
	    gridInstance.exportDataAsCsv();
	});
	function getPostalCampaignsGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/searchCampaigns";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#findPostalMarketingCampaigns";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getPostalCampaignsGridData();
	}
});
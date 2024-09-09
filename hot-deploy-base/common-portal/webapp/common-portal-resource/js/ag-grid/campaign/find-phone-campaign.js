//fagReady("CUST_PHONE_CAMPAIGN_GRID", function (el, api, colApi, gridApi) {
//    $("#phone-campaign-refresh-pref-btn").click(function () {
//        gridApi.refreshUserPreferences();
//    });
//    $("#phone-campaign-save-pref-btn").click(function () {
//        gridApi.saveUserPreferences();
//    });
//    $("#phone-campaign-clear-filter-btn").click(function () {
//        try {
//            gridApi.clearAllColumnFilters();
//        } catch (e) {}
//        gridApi.refreshUserPreferences();
//    });
//	$("#sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#phone-campaign-export-btn").click(function () {
//        gridApi.csvExport();
//    });
//
//    $("#update-btn").click(function () {
//        gridApi.saveUpdates();
//        setTimeout(() => {
//            loadPhoneCampaignGrid(gridApi, api, colApi);
//        }, 1000);
//    })
//
//    $("#main-search-btn").click(function () {
//        if (!$("#campaignType").val()) {
//            api.sizeColumnsToFit();
//            loadPhoneCampaignGrid(gridApi, api, colApi);
//        }
//    });
//    $("#insert-btn").click(function () {
//        gridApi.insertNewRow()
//    })
//    $("#remove-btn").click(function () {
//        // removeMainGrid(fag1, api);
//        gridApi.removeSelected();
//        setTimeout(() => {
//            loadPhoneCampaignGrid(gridApi, api, colApi);
//        }, 1000);
//    });
//
//    $("#refresh-phone-campaign-btn").click(function () {
//        loadPhoneCampaignGrid(gridApi, api, colApi);
//    })
//
//    postLoadGrid(api, gridApi, colApi, "cc-phone", loadPhoneCampaignGrid);	
//    postLoadGrid(api, gridApi, colApi, "c-campaigns", loadPhoneCampaignGrid);	
//    
//    //loadPhoneCampaignGrid(gridApi, api, colApi);
//});
//
//function loadPhoneCampaignGrid(gridApi, api, colApi) {
//    var rowData =[];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//    
//    $.ajax({
//        async: true,
//        url: '/common-portal/control/searchCampaigns',
//        type: "POST",
//        data: JSON.parse(JSON.stringify($("#findPhoneMarketingCampaigns").serialize())),
//        success: function (data) {
//            gridApi.setRowData(data.list);
//        }
//    });
//}


$(function() {
	let campaignInstanceId= "CUST_PHONE_CAMPAIGN_GRID";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = campaignInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#phone-campaign-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, campaignInstanceId, userId);
	});
	$('#phone-campaign-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, campaignInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getPhoneMarketingCampaignsGridData();
		}
	});
    $("#main-search-btn").click(function () {
        if (!$("#campaignType").val()) {
        	getPhoneMarketingCampaignsGridData();
        }
    });
	$('#phone-campaign-sub-filter-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
    $("#refresh-phone-campaign-btn").click(function () {
    	getPhoneMarketingCampaignsGridData();
    })
    $("#phone-campaign-export-btn").click(function () {
	    gridInstance.exportDataAsCsv();
	});
	function getPhoneMarketingCampaignsGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/searchCampaigns";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#findPhoneMarketingCampaigns";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getPhoneMarketingCampaignsGridData();
	}
});

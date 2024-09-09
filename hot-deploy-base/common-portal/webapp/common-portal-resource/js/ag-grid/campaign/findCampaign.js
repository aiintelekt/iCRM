fagReady("PROF_EMAIL_CAMPAIGN_GRID", function (el, api, colApi, gridApi) {
    $("#campaign-refresh-pref-btn").click(function () {
        gridApi.refreshUserPreferences();
    });
    $("#save-pref-btn").click(function () {
        gridApi.saveUserPreferences();
    });
    $("#clear-filter-btn").click(function () {
        try {
            gridApi.clearAllColumnFilters();
        } catch (e) {}
        gridApi.refreshUserPreferences();
    });
	$("#sub-filter-clear-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    });
    $("#export-btn").click(function () {
        gridApi.csvExport();
    });

    $("#update-btn").click(function () {
        gridApi.saveUpdates();
        setTimeout(() => {
            loadCampaignGrid(api, gridApi);
        }, 1000);
    })

    $("#main-search-btn").click(function () {
        if (!$("#campaignType").val()) {
            api.sizeColumnsToFit();
            loadCampaignGrid(api, gridApi);
        }
    });
    $("#insert-btn").click(function () {
        gridApi.insertNewRow()
    })
    $("#remove-btn").click(function () {
        // removeMainGrid(fag1, api);
        gridApi.removeSelected();
        setTimeout(() => {
            loadCampaignGrid(api, gridApi);
        }, 1000);
    });

    $("#refresh-campaign-btn").click(function () {
        loadCampaignGrid(api, gridApi);
    })

    postLoadGridData(api, gridApi, "c-campaigns", loadCampaignGrid);
    postLoadGridData(api, gridApi, "lead-campaigns", loadCampaignGrid);
    postLoadGridData(api, gridApi, "contact-campaigns", loadCampaignGrid);
    postLoadGridData(api, gridApi, "a-campaigns", loadCampaignGrid);

    //loadCampaignGrid(api, gridApi);
});

function loadCampaignGrid(api, gridApi) {
    var rowData =[];
	gridApi.setRowData(rowData);
	api.showLoadingOverlay();
    
    $.ajax({
        async: true,
        url: '/common-portal/control/searchCampaigns',
        type: "POST",
        data: JSON.parse(JSON.stringify($("#findMarketingCampaigns").serialize())),
        success: function (data) {
            gridApi.setRowData(data.list);
        }
    });
}
fagReady("REBATE_PROGRAM_PICKER", function(el, api, colApi, gridApi){
    $("#progtpl-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#progtpl-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#progtpl-clear-filter-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    	gridApi.refreshUserPreferences();
    });
	$("#sub-filter-clear-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    });
    $("#progtpl-export-btn").click(function () {
    	gridApi.csvExport();
    });
    
    $("#main-search-btn").click(function () {
    	loadProgTemplatePickerGrid(gridApi, api, colApi);
    });
   
    loadProgTemplatePickerGrid(gridApi, api, colApi);
});

function loadProgTemplatePickerGrid(gridApi, api, colApi) {
	console.log('calling find method');

	var rowData = [];
	gridApi.setRowData(rowData);
	api.showLoadingOverlay();

	$.ajax({
		async : true,
		url : '/rebate-portal/control/searchRebatePrograms',
		type : "POST",
		data : JSON
				.parse(JSON.stringify($("#rebate-find-progams").serialize())),
		success : function(data) {
			gridApi.setRowData(data.list);
			data.list = [];
			paginateHandler(data);
		}
	});
}
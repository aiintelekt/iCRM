fagReady("AGR_PROG_PICKER", function(el, api, colApi, gridApi){
    $("#agr-prog-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#agr-prog-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#agr-prog-clear-filter-btn").click(function () {
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
    $("#agr-prog-export-btn").click(function () {
    	gridApi.csvExport();
    });

    $("#agr-prog-search-btn").click(function () {
    	loadAgrProgPickerGrid(gridApi);
    });
    
    $('#programTemplatePicker').on('shown.bs.modal', function (e) {
    	api.sizeColumnsToFit();
	});
    
    loadAgrProgPickerGrid(gridApi);
});

function loadAgrProgPickerGrid(gridApi) {
	var rowData = [];
	gridApi.setRowData(rowData);
	$.ajax({
		async : true,
		url : '/rebate-portal/control/searchRebatePrograms',
		type : "POST",
		data : JSON.parse(JSON.stringify($("#agr-find-progams").serialize())),
		success : function(result) {
			gridApi.setRowData(result.list);
		}
	});
}

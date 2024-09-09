fagReady("APV_TPL_PICKER", function(el, api, colApi, gridApi){
    $("#apvtpl-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#apvtpl-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#apvtpl-clear-filter-btn").click(function () {
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
    $("#apvtpl-export-btn").click(function () {
    	gridApi.csvExport();
    });

    $("#approvalTemplatePicker-search-btn").click(function () {
    	loadApvTplPickerGrid(gridApi);
    });
    
    $("#find-apvtpl-form").on("keypress", function (event) {
        var keyPressed = event.keyCode || event.which; 
        if (keyPressed === 13) { 
        	loadApvTplPickerGrid(gridApi);
            event.preventDefault(); 
            return false; 
        } 
    });
    $('#approvalTemplatePicker-reset-btn').trigger('click');
});

function loadApvTplPickerGrid(gridApi) {
	var rowData = [];
	gridApi.setRowData(rowData);
	$.ajax({
		async : true,
		url : '/approval-portal/control/searchTemplates',
		type : "POST",
		data : JSON.parse(JSON.stringify($("#find-apvtpl-form").serialize())),
		success : function(data) {
			gridApi.setRowData(data.list);
			data.list = [];
			paginateHandler(data);
		}
	});
}

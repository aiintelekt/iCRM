//added for customer picker
fagReady("EXPORT_FILE_LIST", function(el, api, colApi, gridApi){
    $("#exp-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#exp-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#exp-clear-filter-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    	gridApi.refreshUserPreferences();
    });
	$("#exp-sub-filter-clear-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    });
    $("#exp-export-btn").click(function () {
    	gridApi.csvExport();
    });

    $("#exp-search-btn").click(function () {
    	loadExportFileGrid(api, gridApi, colApi);
    });
    
    $("#exp-refresh-btn").click(function () {
    	loadExportFileGrid(api, gridApi, colApi);
    });
    
    $('#orderPicker').on('shown.bs.modal', function (e) {
    	api.sizeColumnsToFit();
	});
    //To submit the form to while click the enter button
    $("#exp-search-form").on("keypress", function (event) {
        var keyPressed = event.keyCode || event.which; 
        if (keyPressed === 13) { 
        	loadExportFileGrid(api,gridApi, colApi);
            event.preventDefault();
            return false; 
        } 
    });
    //loadExportFileGrid(api, gridApi, colApi);
});

function loadExportFileGrid(api, gridApi, colApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	api.showLoadingOverlay();
	$.ajax({
	  async: true,
	  url:'/common-portal/control/searchExportFiles',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#exp-search-form").serialize())),
	  success: function(result){
		  gridApi.setRowData(result.list);
		  setTimeout(() => colApi.autoSizeAllColumns(), 1000);
	  }
	});
}


fagReady("LIST_PRODUCT_STORES", function(el, api, colApi, gridApi){
	
	$("#refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#clear-filter-btn").click(function () {
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
    $("#export-btn").click(function () {
    	gridApi.csvExport();
    });
    $("#remove-btn").click(function () {
    	var selectedRows=api.getSelectedRows();
    	if(selectedRows!= undefined && selectedRows != null&& selectedRows.length>0){
    		gridApi.removeSelected();
    		setTimeout(() => { loadTechnicianRates(gridApi, api, colApi); }, 1000);
    	} else {
            showAlert("error","Please select atleast one record in the list");
        }
    });
    //To submit the form to while click the enter button
    $("#searchForm").on("keypress", function (event) {
        var keyPressed = event.keyCode || event.which; 
        if (keyPressed === 13) { 
        	loadTechnicianRates(gridApi, api);
            event.preventDefault();
            return false; 
        } 
    });
    loadTechnicianRates(gridApi, api);
	
});

var techniciansRatesUrl = "";
function loadTechnicianRates(gridApi, api) {
	if(techniciansRatesUrl == ""){
		resetGridStatusBar();
		techniciansRatesUrl = getGridDataFetchUrl("LIST_PRODUCT_STORES");
	}
	var rowData =[];
	gridApi.setRowData(rowData);
	if(techniciansRatesUrl != null && techniciansRatesUrl != "" && techniciansRatesUrl !="undefined"){
		api.showLoadingOverlay();
		$.ajax({
		  async: false,
		  url:techniciansRatesUrl,
		  type:"POST",
		  data: JSON.parse(JSON.stringify($("#searchForm").serialize())),
		  success: function(data){
			  gridApi.setRowData(data);
		  }
		});
	}
}
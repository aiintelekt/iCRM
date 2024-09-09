fagReady("CSP_INVOICE_GRID", function(el, api, colApi, gridApi){
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
    
    $("#main-search-btn").click(function () {
    	 loadInvoices(gridApi, api);
    });
    loadInvoices(gridApi, api);
});

var listInvoicesUrl = "";
function loadInvoices(gridApi, api) {
	if(listInvoicesUrl == ""){
		resetGridStatusBar();
		listInvoicesUrl = getGridDataFetchUrl("CSP_INVOICE_GRID");
	}
	var rowData =[];
	gridApi.setRowData(rowData);
	if(listInvoicesUrl != null && listInvoicesUrl != "" && listInvoicesUrl !="undefined"){
		api.showLoadingOverlay();
		$.ajax({
		  async: false,
		  url:listInvoicesUrl,
		  type:"POST",
		  data: JSON.parse(JSON.stringify($("#findInvoices").serialize())),
		  success: function(data){
			  gridApi.setRowData(data);
		  }
		});
	}
}
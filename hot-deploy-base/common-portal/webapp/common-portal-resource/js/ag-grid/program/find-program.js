fagReady("SR_BASE_LIST", function(el, api, colApi, gridApi){
    $("#program-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#program-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#program-clear-filter-btn").click(function () {
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
    $("#program-export-btn").click(function () {
    	gridApi.csvExport();
    });
    
    $("#refresh-btn").click(function () {
    	loadProgramGrid(gridApi, api, colApi);
    });
    
    $("#program-search-btn").click(function () {
    	loadProgramGrid(gridApi, api, colApi);
    });
    
    postLoadGrid(api, gridApi, colApi, "programs", loadProgramGrid);
});

function loadProgramGrid(gridApi, api, colApi) {
	console.log('calling program find method');
	
	var rowData =[];
	gridApi.setRowData(rowData);
	api.showLoadingOverlay();
	
	$.ajax({
	  async: true,
	  url:'/service-portal/control/searchSRs',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#program-search-form").serialize())),
	  success: function(data){
		  gridApi.setRowData(data.list);
		  data.list=[];
		  paginateHandler(data);
		  setTimeout(() => colApi.autoSizeAllColumns(), 1000);
	  }
	});
}
fagReady("AGREEMENT_PICKER_LIST", function(el, api, colApi, gridApi){
    $("#agretpl-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#agretpl-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#agretpl-clear-filter-btn").click(function () {
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
    $("#agretpl-export-btn").click(function () {
    	gridApi.csvExport();
    });
    
    $("#main-search-btn").click(function () {
    	loadAgreTemplatePickerGrid(gridApi, api, colApi);
    });
   
    loadAgreTemplatePickerGrid(gridApi, api, colApi);
});

function loadAgreTemplatePickerGrid(gridApi, api, colApi) {
	console.log('calling find method');
	
	var rowData =[];
	gridApi.setRowData(rowData);
	api.showLoadingOverlay();
	
	$.ajax({
	  async: true,
	  url:'/rebate-portal/control/searchAgmtTemplates',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#rebate-find-AgmtTemplates").serialize())),
	  success: function(data){
		  gridApi.setRowData(data.list);
		  data.list=[];
		  paginateHandler(data);
	  }
	});
}
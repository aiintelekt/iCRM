fagReady("ASSIGNED_COMM_HISTORY", function(el, api, colApi, gridApi){
    $("#assigned-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#assigned-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#assigned-clear-filter-btn").click(function () {
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
    $("#assigned-export-btn").click(function () {
    	gridApi.csvExport();
    }); 
   
    loadAssignedComHistory(gridApi,api, colApi);
    
});


function loadAssignedComHistory(gridApi, api, colApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	api.showLoadingOverlay();
	$.ajax({
	  async: false,
	  url:'/common-portal/control/getEmailActivities',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#assignedForm").serialize())),
	  success: function(data){
		  gridApi.setRowData(data.list);
		  setTimeout(() => colApi.autoSizeAllColumns(), 1000);
	  }
	});
}
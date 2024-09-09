
fagReady("LIST_ACTIVITY_WORK_TYPE", function(el, api, colApi, gridApi){
	
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
    		var valid = true;
    		for (i = 0; i < selectedRows.length; i++) {
		    	var data = selectedRows[i];
		    	var workEffortPurposeTypeId = data.workEffortPurposeTypeId;
		    	
		    }
    		
			gridApi.removeSelected();
			setTimeout(() => { loadActivityworkType(gridApi, api, colApi); }, 1000);
    		
    	} else {
            showAlert("error","Please select atleast one record in the list");
        }
    });
    loadActivityworkType(gridApi, api);
	
});

var activityWorkTypeUrl = "";
function loadActivityworkType(gridApi, api) {
	if(activityWorkTypeUrl == ""){
		resetGridStatusBar();
		activityWorkTypeUrl = getGridDataFetchUrl("LIST_ACTIVITY_WORK_TYPE");
	}
	var rowData =[];
	gridApi.setRowData(rowData);
	if(activityWorkTypeUrl != null && activityWorkTypeUrl != "" && activityWorkTypeUrl !="undefined"){
		api.showLoadingOverlay();
		$.ajax({
		  async: false,
		  url:activityWorkTypeUrl,
		  type:"POST",
		  data: JSON.parse(JSON.stringify($("#searchForm").serialize())),
		  success: function(data){
			  gridApi.setRowData(data);
		  }
		});
	}
}
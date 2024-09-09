fagReady("ROLE_SECURITY_ASSOC", function(el, api, colApi, gridApi){
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
    	loadSecurityGrps(gridApi, api, colApi);
    });
    $("#remove-btn").click(function () {
    	var selectedRows=api.getSelectedRows();
    	if(selectedRows!= undefined && selectedRows != null&& selectedRows.length>0){
    		gridApi.removeSelected();
            setTimeout(() => { loadSecurityGrps(gridApi, api, colApi); }, 1000);
    	} else {
            showAlert("error","Please select atleast one record in the list");
        }
    });
    
    loadSecurityGrps(gridApi, api, colApi);
});



var getGroupRoleUrl = "";

function loadSecurityGrps(gridApi, api, colApi) {
	if(getGroupRoleUrl == ""){
		//resetGridStatusBar();
		getGroupRoleUrl = getGridDataFetchUrl("ROLE_SECURITY_ASSOC");
	}
	var rowData =[];
	gridApi.setRowData(rowData);
	if(getGroupRoleUrl != null && getGroupRoleUrl != "" && getGroupRoleUrl !="undefined"){
		api.showLoadingOverlay();
		var formInput = $('#searchForm').serialize();

		$.ajax({
		  async: false,
		  url:getGroupRoleUrl,
		  type:"POST",
		  data: JSON.parse(JSON.stringify(formInput)),
		  success: function(data){

			  gridApi.setRowData(data.list);
			  //data.list=[];
			  //paginateHandler(data);
			  setTimeout(() => colApi.autoSizeAllColumns(), 1000);
		  }
		});
	}
}
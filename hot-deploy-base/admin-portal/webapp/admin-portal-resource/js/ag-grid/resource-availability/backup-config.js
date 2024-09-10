
fagReady("BACKUP_CONFIGURATION", function(el, api, colApi, gridApi){
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

    $("#update-btn").click(function () {
    	gridApi.saveUpdates();
        setTimeout(() => {  loadMainGrid(gridApi, api, colApi); }, 1000);
    })

    $("#main-search-btn").click(function () {
    	loadMainGrid(gridApi, api, colApi);
    });
    
    $("#backup-config-remove-btn").click(function () {
    	var selectedRows=api.getSelectedRows();
    	if(selectedRows!= undefined && selectedRows != null&& selectedRows.length>0){
    		gridApi.removeSelected();
            setTimeout(() => { loadMainGrid(gridApi, api, colApi);loadCoordinator(); }, 1000);
    	} else {
            showAlert("error","Please select atleast one record in the list");
        }
		
    });

    $("#create-click").click(function () {
    	setTimeout(() => { loadMainGrid(gridApi, api, colApi); }, 1000);
    });
    loadMainGrid(gridApi, api, colApi);
});

function loadMainGrid(gridApi, api, colApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	api.showLoadingOverlay();
	$.ajax({
	  async: false,
	  url:'/admin-portal/control/getBackConfigurationList',
	  type:"POST",
	  data: {},//JSON.parse(JSON.stringify($("#backup-search-form").serialize())),
	  success: function(data){
		  gridApi.setRowData(data.list);
	  }
	});
}

fagReady("ADD_SECURITY_PERMISSION", function(el, api, colApi, gridApi){
    $("#add-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#add-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#add-clear-filter-btn").click(function () {
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
    $("#add-export-btn").click(function () {
    	gridApi.csvExport();
    });

    $("#add-main-search-btn").click(function () {
    	loadSecurityPermissionsGrid(gridApi, api, colApi);
    });
    $("#add-security-perm-btn").click(function () {
    	var selectedRows = api.getSelectedRows();
    	 if(selectedRows){
    		 var rows = JSON.stringify(selectedRows);
    		 $("#selectedRows").val(rows);
    		 $("#addPermissionForm").submit();
          } else {
             showAlert("error","Please select atleast one record in the list")
          }
    });
    loadSecurityPermissionsGrid(gridApi, api, colApi);
});



var securityPermissionUrl = "";
function loadSecurityPermissionsGrid(gridApi, api, colApi) {
	if(securityPermissionUrl == ""){
		//resetGridStatusBar();
		securityPermissionUrl = getGridDataFetchUrl("ADD_SECURITY_PERMISSION");

	}
	var rowData =[];
	gridApi.setRowData(rowData);
	if(securityPermissionUrl != null && securityPermissionUrl != "" && securityPermissionUrl !="undefined"){
		api.showLoadingOverlay();
		var formInput = $('#searchForm, #limitForm').serialize();

		$.ajax({
		  async: false,
		  url:securityPermissionUrl,
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


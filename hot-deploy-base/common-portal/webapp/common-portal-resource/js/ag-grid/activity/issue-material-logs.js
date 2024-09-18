
//fagReady("ISSUE_MATERIAL_LOGS", function(el, api, colApi, gridApi){
//    $("#issue-material-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#issue-material-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#issue-material-clear-filter-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    	gridApi.refreshUserPreferences();
//    });
//	$("#sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#issue-material-export-btn").click(function () {
//    	gridApi.csvExport();
//    }); 
//   
//    postLoadGrid(api, gridApi, colApi, "issueMaterial", loadIssueMaterialGrid);
//    //loadIssueMaterialGrid(gridApi,api, colApi);
//    
//});
//
//function loadIssueMaterialGrid(gridApi, api, colApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	$.ajax({
//	  async: false,
//	  url:'/common-portal/control/getIssueMaterials',
//	  type:"POST",
//	  data: {"workEffortId": $("#workEffortId").val()},
//	  success: function(data){
//		  gridApi.setRowData(data.list);
//		  setTimeout(() => colApi.autoSizeAllColumns(), 1000);
//	  }
//	});
//}

$(function() {
	let issueMaterialLogsListInstanceId= "ISSUE_MATERIAL_LOGS";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject1 = {};
	formDataObject1.gridInstanceId = issueMaterialLogsListInstanceId;
	formDataObject1.externalLoginKey = externalLoginKey;
	formDataObject1.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject1);
	
	$('#issue-material-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, issueMaterialLogsListInstanceId, userId);
	});
	$('#issue-material-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, issueMaterialLogsListInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject1);
		if(gridInstance){
			getIssueMaterialGridData();
		}
	});
	$('#issue-material-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});

	function getIssueMaterialGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/getIssueMaterials";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#issue-material-form";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getIssueMaterialGridData();
	}
});

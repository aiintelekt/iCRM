//fagReady("SR_ACT_ISU_MATERIAL", function(el, api, colApi, gridApi){
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
//	$("#issue-material-sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#issue-material-export-btn").click(function () {
//    	gridApi.csvExport();
//    }); 
//    
//    postLoadGridData(null, gridApi, "sr-issue-material", loadActivityIssueMaterial);
//    
//    //loadActivityIssueMaterial(gridApi, api, colApi);
//    
//});
//
//function loadActivityIssueMaterial(gridApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	$.ajax({
//	  async: false,
//	  url:'/sr-portal/control/getSrActivityIssueMaterials',
//	  type:"POST",
//	  data: {"srNumber": $("#custRequestId").val()},
//	  success: function(data){
//		  gridApi.setRowData(data.list);
//		  //setTimeout(() => colApi.autoSizeAllColumns(), 1000);
//	  }
//	});
//}

$(function() {
	let issueMaterialListInstanceId= "SR_ACT_ISU_MATERIAL";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = issueMaterialListInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#time-entry-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, issueMaterialListInstanceId, userId);
	});
	$('#time-entry-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, issueMaterialListInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getIssueMaterialGridData();
		}
	});
	$('#time-entry-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#issue-material-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	function getIssueMaterialGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/sr-portal/control/getSrActivityIssueMaterials";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#issueMaterialsForm";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getIssueMaterialGridData();
	}
});

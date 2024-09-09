//fagReady("REBATE_LIST", function(el, api, colApi, gridApi){
//    $("#rebate-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#rebate-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#rebate-clear-filter-btn").click(function () {
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
//    $("#rebate-export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//    
//    $("#rebate-search-btn").click(function () {
//    	loadRebateGrid(gridApi, api, colApi);
//    });
//    
//    postLoadGrid(api, gridApi, colApi, "rebate", loadRebateGrid);
//    postLoadGrid(api, gridApi, colApi, "c-rebate", loadRebateGrid);
//    postLoadGrid(api, gridApi, colApi, "sr-rebate", loadRebateGrid);
//    
//    //postLoadGrid(api, gridApi, colApi, "rebate", loadRebateGrid);
//});
//
//function loadRebateGrid(gridApi, api, colApi) {
//	console.log('calling rebate find method');
//	
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	
//	$.ajax({
//	  async: true,
//	  url:'/rebate-portal/control/searchRebates',
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify($("#rebate-search-form").serialize())),
//	  success: function(data){
//		  gridApi.setRowData(data.list);
//		  data.list=[];
//		  paginateHandler(data);
//	  }
//	});
//}



$(function() {
	let rebateInstanceId= "REBATE_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = rebateInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#rebate-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, rebateInstanceId, userId);
	});
	$('#rebate-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, rebateInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getRebateGridData();
		}
	});
	$('#rebate-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
    $("#rebate-export-btn").click(function () {
		gridInstance.exportDataAsCsv();
    });
    $("#rebate-search-btn").click(function () {
		getRebateGridData();
    });
	function getRebateGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/rebate-portal/control/searchRebates";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#rebate-search-form";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getRebateGridData();
	}
});
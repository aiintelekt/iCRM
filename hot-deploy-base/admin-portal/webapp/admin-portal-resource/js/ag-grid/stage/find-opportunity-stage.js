
//fagReady("OPPO_STAGE_LIST", function(el, api, colApi, gridApi){
//    $("#refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#clear-filter-btn").click(function () {
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
//    $("#export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//
//    $("#update-btn").click(function () {
//    	gridApi.saveUpdates();
//        setTimeout(() => {  loadOpportunityStaging(gridApi, api); }, 1000);
//    })
//
//    $("#main-search-btn").click(function () {
//    	loadOpportunityStaging(gridApi, api);
//    });
//    $("#insert-btn").click(function () {
//    	gridApi.insertNewRow()
//    })
//    $("#remove-btn").click(function () {
//        //removeMainGrid(fag1, api);
//    	gridApi.removeSelected();
//        setTimeout(() => {  loadOpportunityStaging(gridApi, api); }, 1000);
//        
//    });
//    
//    loadOpportunityStaging(gridApi, api);
//});


//var opportunityStagingUrl = "";
//function loadOpportunityStaging(gridApi, api) {
//	if(opportunityStagingUrl == ""){
//		opportunityStagingUrl = getGridDataFetchUrl("OPPO_STAGE_LIST");
//	}
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(opportunityStagingUrl != null && opportunityStagingUrl != "" && opportunityStagingUrl !="undefined"){
//		api.showLoadingOverlay();
//		var formInput = $('#searchForm, #limitForm').serialize();
//		$.ajax({
//		  async: false,
//		  url:opportunityStagingUrl,
//		  type:"POST",
//		  data: JSON.parse(JSON.stringify(formInput)),
//		  success: function(data){
//			  gridApi.setRowData(data);
//		  }
//		});
//	}
//}



$(function() {
	let oppoStagListInstanceId= "OPPO_STAGE_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = oppoStagListInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#oppo-stage-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, oppoStagListInstanceId, userId);
	});
	$('#oppo-stage-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, oppoStagListInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getOpportunityStagingData();
		}
	});
	$('#oppo-stage-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#oppo-stage-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
    $("#main-search-btn").click(function () {
    	getOpportunityStagingData();
    });
	function getOpportunityStagingData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("OPPO_STAGE_LIST");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm, #limitForm_OPPO_STAGE_LIST";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getOpportunityStagingData();
	}
});
function opportunityStageId(params){
	return `<a href="/admin-portal/control/viewOppoStage?opportunityStageId=${params.data.opportunityStageId}">${params.data.opportunityStageId}</a>`;
}
$(function() {
	let userTeamInstanceId= "USER_TEAM";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = userTeamInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#user-team-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, userTeamInstanceId, userId);
	});
	$("#user-team-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#user-team-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, userTeamInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getUserTeamListGridData();
		}
	});
	$('#user-team-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	
	function getUserTeamListGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("USER_TEAM");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#userTeamForm";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getUserTeamListGridData();
	}
});
//fagReady("USER_TEAM", function(el, api, colApi, gridApi){
//    $("#user-team-ref-pref").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#user-team-save-pref").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#user-team-clear-filter").click(function () {
//    	gridApi.clearAllColumnFilters();
//    });
//	$("#sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#user-team-export").click(function () {
//    	gridApi.csvExport();
//    });
//    $("#user-team-remove-btn").click(function () {
//    	gridApi.removeSelected();
//        setTimeout(() => {  loadUserTeams(gridApi, api); }, 1000);
//        
//    });
//    loadUserTeams(gridApi, api, colApi);
//});
//
//var userTeamUrl = "";
//function loadUserTeams(gridApi, api, colApi) {
//	
//	if(userTeamUrl == ""){
//		//resetGridStatusBar();
//		userTeamUrl = getGridDataFetchUrl("USER_TEAM");
//	}
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(userTeamUrl != null && userTeamUrl != "" && userTeamUrl !="undefined"){
//		api.showLoadingOverlay();
//		var formInput = $('#searchForm, #limitForm').serialize();
//		$.ajax({
//		  async: false,
//		  url:userTeamUrl,
//		  type:"POST",
//		  data: { "partyId": $("#partyId").val()},
//		  success: function(data){
//			  gridApi.setRowData(data.list);
//			  setTimeout(() => colApi.autoSizeAllColumns(), 1000);
//			  //data.list=[];
//			  //paginateHandler(data);
//		  }
//		});
//	}
//}

function viewTeam(params) {
	return `<a href="viewTeam?emplTeamId=${params.data.emplTeamId}">${params.value}</a>`;
}
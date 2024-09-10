//fagReady("PICKER_TM_LIST", function(el, api, colApi, gridApi){
//    $("#resPickerAccount-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#resPickerAccount-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#resPickerAccount-clear-filter-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    	gridApi.refreshUserPreferences();
//    });
//	$("#resPickerSub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#resPickerAccount-export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//    $("#team-mem-search-btn").click(function () {
//    	loadTeamMemPickerGrid(gridApi, api);
//    });
//    
//    $("#resPickerAccount-refresh-btn").click(function () {
//    	loadTeamMemPickerGrid(gridApi, api);
//    });
//    
//  //To submit the form to while click the enter button
//    $("#searchTeamMembersForm").on("keypress", function (event) {
//        var keyPressed = event.keyCode || event.which; 
//        if (keyPressed === 13) { 
//        	loadTeamMemPickerGrid(gridApi, api);
//            event.preventDefault();
//            return false; 
//        } 
//    });
//    
//    //loadTeamMemPickerGrid(gridApi, api);
//    
//});
//
//function loadTeamMemPickerGrid(gridApi, api) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	var formInput = $('#searchTeamMembersForm').serialize();
//	api.showLoadingOverlay();
//	$.ajax({
//	  async: true,
//	  url:'/common-portal/control/getTeamMembersList',
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify(formInput)),
//	  success: function(data){
//		  gridApi.setRowData(data);
//	  }
//	});
//}


$(function() {
	let teamMemberInstanceId= "PICKER_TM_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = teamMemberInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#resPickerAccount-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, teamMemberInstanceId, userId);
	});
	$('#resPickerAccount-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, teamMemberInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getTeamMemberGridData();
		}
	});
	$('#resPickerSub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	 $("#main-search-btn").click(function () {
		 getTeamMemberGridData();
    });
	    $("#team-mem-search-btn").click(function () {
	    	getTeamMemberGridData();
	    });
	    
	    $("#resPickerAccount-refresh-btn").click(function () {
	    	getTeamMemberGridData();
	    });
	 $("#searchTeamMembersForm").on("keypress", function (event) {
	        var keyPressed = event.keyCode || event.which; 
	        if (keyPressed === 13) { 
	        	getTeamMemberGridData();
	            event.preventDefault();
	            return false; 
	        } 
	    });
   
	function getTeamMemberGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/getTeamMembersList";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchTeamMembersForm";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getTeamMemberGridData();
	}
});

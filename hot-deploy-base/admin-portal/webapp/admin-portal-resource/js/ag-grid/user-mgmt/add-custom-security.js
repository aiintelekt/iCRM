//fagReady("ADD_CUSTOM_SECURITY", function(el, api, colApi, gridApi){
//    $("#add-custom-security-ref-pref").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#add-custom-security-save-pref").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#add-custom-security-clear-filter").click(function () {
//    	gridApi.clearAllColumnFilters();
//    });
//	$("#sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#add-custom-security-export").click(function () {
//    	gridApi.csvExport();
//    });
//    $("#add-custom-security-btn").click(function () {
//    	var selectedRows = api.getSelectedRows();
//    	 if(selectedRows){
//    		 var rows = JSON.stringify(selectedRows);
//    		 $("#selectedGroupIds").val(rows);
//    		 $("#addSecurityGroupForm").submit();
//          } else {
//             showAlert("error","Please select atleast one record in the list")
//          }
//    });
//    
//    addCustomSecurity(gridApi, api, colApi);
//});
//
//var listCustomSecurityUrl = "";
//function addCustomSecurity(gridApi, api, colApi) {
//	if(listCustomSecurityUrl == ""){
//		resetGridStatusBar();
//		listCustomSecurityUrl = getGridDataFetchUrl("ADD_CUSTOM_SECURITY");
//	}
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(listCustomSecurityUrl != null && listCustomSecurityUrl != "" && listCustomSecurityUrl !="undefined"){
//		api.showLoadingOverlay();
//		var formInput = $('#searchForm, #limitForm').serialize();
//		$.ajax({
//		  async: false,
//		  url:listCustomSecurityUrl,
//		  type:"POST",
//		  data: { "partyId": $("#partyId").val()},
//		  success: function(data){
//			  gridApi.setRowData(data.list);
//			  //data.list=[];
//			  //paginateHandler(data);
//			  setTimeout(() => colApi.autoSizeAllColumns(), 1000);
//		  }
//		});
//	}
//}
$(function() {
	let customSecurityInstanceId= "ADD_CUSTOM_SECURITY";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = customSecurityInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#user-roles-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, customSecurityInstanceId, userId);
	});
	$("#add-custom-security-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#user-roles-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, customSecurityInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getCustomSecurityListGridData();
		}
	});
	$('#user-roles-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	  $("#add-custom-security-btn").click(function () {
	    	var selectedRows = gridInstance.getSelectedRows();
	    	 if(selectedRows){
	    		 var rows = JSON.stringify(selectedRows);
	    		 $("#selectedGroupIds").val(rows);
	    		 $("#addSecurityGroupForm").submit();
	          } else {
	             showAlert("error","Please select atleast one record in the list")
	          }
	    });
	function getCustomSecurityListGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("ADD_CUSTOM_SECURITY");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#addSecurityGroupForm";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getCustomSecurityListGridData();
	}
});
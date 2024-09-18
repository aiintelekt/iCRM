//fagReady("ADD_ROLES_TO_USER", function(el, api, colApi, gridApi){
//    $("#add-roles-to-user-ref-pref").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#add-roles-to-user-save-pref").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#add-roles-to-user-clear-filter").click(function () {
//    	gridApi.clearAllColumnFilters();
//    });
//	$("#sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#add-roles-to-user-export").click(function () {
//    	gridApi.csvExport();
//    });
//    $("#add-roles-to-user-btn").click(function () {
//    	var selectedRows = api.getSelectedRows();
//    	 if(selectedRows){
//    		 var rows = JSON.stringify(selectedRows);
//    		 $("#selecteddRows").val(rows);
//    		 $("#addRolesToUserForm").submit();
//          } else {
//             showAlert("error","Please select atleast one record in the list")
//          }
//    });
//    loadAddRolesToUser(gridApi, api);
//});
//
//var addRolesToUserUrl = "";
//function loadAddRolesToUser(gridApi, api) {
//	
//	var partyId = $("#partyId").val();
//	
//	if(addRolesToUserUrl == ""){
//		resetGridStatusBar();
//		addRolesToUserUrl = getGridDataFetchUrl("ADD_ROLES_TO_USER");
//	}
//	
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(addRolesToUserUrl != null && addRolesToUserUrl != "" && addRolesToUserUrl !="undefined"){
//		api.showLoadingOverlay();
//		var formInput = $('#searchForm, #limitForm').serialize();
//		$.ajax({
//		  async: false,
//		  url:addRolesToUserUrl,
//		  type:"POST",
//		  data:  { "partyId": partyId },
//		  success: function(data){
//			  gridApi.setRowData(data);
//			  //data.list=[];   
//			  //paginateHandler(data);
//		  }
//		});
//	}
//}

$(function() {
	let addUserRoleInstanceId= "ADD_ROLES_TO_USER";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = addUserRoleInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#add-user-role-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, addUserRoleInstanceId, userId);
	});
	$("#add-user-role-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#add-user-role-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, addUserRoleInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getAddUserRoleListGridData();
		}
	});
	$('#add-user-role-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#add-roles-to-user-btn").click(function () {
		var selectedRows = gridInstance.getSelectedRows();
		 if(selectedRows){
			 var rows = JSON.stringify(selectedRows);
			 $("#selecteddRows").val(rows);
			 $("#addRolesToUserForm").submit();
	      } else {
	         showAlert("error","Please select atleast one record in the list")
	      }
	});
	function getAddUserRoleListGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("ADD_ROLES_TO_USER");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#addRoleToUserForm";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getAddUserRoleListGridData();
	}
});

//fagReady("USER_ROLES_LIST", function(el, api, colApi, gridApi){
//    $("#user-roles-list-ref-pref").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#user-roles-list-save-pref").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#user-roles-list-clear-filter").click(function () {
//    	gridApi.clearAllColumnFilters();
//    });
//	$("#sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#user-roles-list-export").click(function () {
//    	gridApi.csvExport();
//    });
//    $("#user-roles-list-remove-btn").click(function () {
//    	gridApi.removeSelected();
//        setTimeout(() => {  loadUserRolesList(gridApi, api); }, 1000);
//    });
//    
//    loadUserRolesList(gridApi, api);
//});
//
//var userRolesListUrl = "";
//function loadUserRolesList(gridApi, api) {
//	if(userRolesListUrl == ""){
//		resetGridStatusBar();
//		userRolesListUrl = getGridDataFetchUrl("USER_ROLES_LIST");
//	}
//	
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(userRolesListUrl != null && userRolesListUrl != "" && userRolesListUrl !="undefined"){
//		api.showLoadingOverlay();
//		var formInput = $('#searchForm, #limitForm').serialize();
//		$.ajax({
//		  async: false,
//		  url:userRolesListUrl,
//		  type:"POST",
//		  data: { "partyId": $("#partyId").val()},
//		  success: function(data){
//			  gridApi.setRowData(data);
//			  //data.list=[];
//			  //paginateHandler(data);
//		  }
//		});
//	}
//}

$(function() {
	let userRolesInstanceId= "USER_ROLES_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = userRolesInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#user-roles-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, userRolesInstanceId, userId);
	});
	$("#user-roles-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#user-roles-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, userRolesInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getUserRolesListGridData();
		}
	});
	$('#user-roles-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$('#user-roles-list-remove-btn').on('click', function(e) {
        e.preventDefault();
        var selectedData = gridInstance.getSelectedRows();
        var selectedRowData = [];
        if (selectedData && selectedData.length > 0) {
       	 	var partyIdVal = "";
       	 	var roleTypeIdVal = "";
 		    for (i = 0; i < selectedData.length; i++) {
 		    	var data = selectedData[i];
 		    	partyIdVal += data.partyId;
 		    	roleTypeIdVal += data.roleTypeId;
 		    }
 	        var inputData = {
 	               "partyId": partyIdVal,"roleTypeId": roleTypeIdVal
 	           };
            $.ajax({
                type: "POST",
                url: "/admin-portal/control/removeUserRole",
                async: true,
                data: inputData,
                success: function(result) {
                    if (result.responseMessage === "success") {
                        showAlert("success", "Role removed successfully");
                        getUserRolesListGridData();
                    } else {
                        showAlert("error", result.message);
                        e.preventDefault();
                    }
                },
                error: function() {
                    console.log('Error occurred');
                    showAlert("error", "Error occurred!");
                },
                complete: function() {
                }
            });
        } else {
            showAlert("error", "Please select at least one record in the list");
        }
    });

	function getUserRolesListGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("USER_ROLES_LIST");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#securityRoleForm";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getUserRolesListGridData();
	}
});
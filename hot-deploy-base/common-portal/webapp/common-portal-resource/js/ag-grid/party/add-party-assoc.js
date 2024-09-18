//fagReady("ALL_PARTIES", function(el, api, colApi, gridApi){
//    $("#partyassoc-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#partyassoc-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#partyassoc-clear-filter-btn").click(function () {
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
//    $("#partyassoc-export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//
//    $("#add-partyassoc-search-btn").click(function () {
//    	addPartyPickerGrid(gridApi);
//    });
//    
//    $("#add-partyassoc-btn").click(function () {
//    	console.log('call add-partyassoc-btn');
//    	var flag = true;
//    	if(flag){
//    		var selectedData = api.getSelectedRows();
//    		if (selectedData.length > 0) {
//    			console.log(selectedData);
//    			
//    		    var selectedPartyIds = "";
//    		    var selectedRoleTypeIds = "";
//    		    for (i = 0; i < selectedData.length; i++) {
//    		    	var data = selectedData[i];
//    		    	selectedPartyIds += data.partyId+",";
//    		    	selectedRoleTypeIds += data.roleTypeId+",";
//    		    }
//    		    selectedPartyIds = selectedPartyIds.substring(0, selectedPartyIds.length - 1);
//    		    console.log(selectedPartyIds);
//    		    selectedRoleTypeIds = selectedRoleTypeIds.substring(0, selectedRoleTypeIds.length - 1);
//    		    console.log(selectedRoleTypeIds);
//    		    
//    		    var inputData = {"selectedPartyIds": selectedPartyIds, "selectedRoleTypeIds": selectedRoleTypeIds, "domainEntityType": $("#partyassoc-domainEntityType").val(), "domainEntityId": $("#partyassoc-domainEntityId").val()};
//    		    
//    		    $.ajax({
//    				type : "POST",
//    				url : "/common-portal/control/addAssocParties",
//    				async : true,
//    				data : inputData,
//    				success : function(result) {
//    					if (result.code == 200) {
//    						showAlert ("success", "Successfully added parties..");
//    						//loadPartyAssocGrid(gridApi);
//    						$('#create-partyassoc-modal').modal('hide');
//    						$("#refresh-partyassoc-btn").trigger('click');
//    					} else {
//    						showAlert ("error", data.message);
//    					}
//    				},
//    				error : function() {
//    					console.log('Error occured');
//    					showAlert("error", "Error occured!");
//    				},
//    				complete : function() {
//    				}
//    			});
//    		} else {
//    			showAlert("error", "Please select atleast one row to be added!");
//    		}
//    	}
//    });
//    
//    postLoadGridData(null, gridApi, "associated-parties", addPartyPickerGrid);
//});
//
//function addPartyPickerGrid(gridApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	$.ajax({
//	  async: true,
//	  url:'/common-portal/control/getAllParties',
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify($("#add-partyassoc-form").serialize())),
//	  success: function(data){
//		  gridApi.setRowData(data.list);
//	  }
//	});
//}


$(function() {
	let allPartiesAssocInstanceId= "ALL_PARTIES";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = allPartiesAssocInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#partyassoc-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, allPartiesAssocInstanceId, userId);
	});
	$('#partyassoc-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, allPartiesAssocInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getAllPartiesAssocGridData();
		}
	});
	$('#partyassoc-sub-clear-filter-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#partyassoc-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#partyassoc-main-search-btn').click(function(){
		getAllPartiesAssocGridData();
	});
	$("#add-partyassoc-btn").click(function () {
    	console.log('call add-partyassoc-btn');
    	var flag = true;
    	if(flag){
    		var selectedData = gridInstance.getSelectedRows();
    		if (selectedData.length > 0) {
    			console.log(selectedData);
    			
    		    var selectedPartyIds = "";
    		    var selectedRoleTypeIds = "";
    		    for (i = 0; i < selectedData.length; i++) {
    		    	var data = selectedData[i];
    		    	selectedPartyIds += data.partyId+",";
    		    	selectedRoleTypeIds += data.roleTypeId+",";
    		    }
    		    selectedPartyIds = selectedPartyIds.substring(0, selectedPartyIds.length - 1);
    		    console.log(selectedPartyIds);
    		    selectedRoleTypeIds = selectedRoleTypeIds.substring(0, selectedRoleTypeIds.length - 1);
    		    console.log(selectedRoleTypeIds);
    		    
    		    var inputData = {"selectedPartyIds": selectedPartyIds, "selectedRoleTypeIds": selectedRoleTypeIds, "domainEntityType": $("#partyassoc-domainEntityType").val(), "domainEntityId": $("#partyassoc-domainEntityId").val()};
    		    
    		    $.ajax({
    				type : "POST",
    				url : "/common-portal/control/addAssocParties",
    				async : true,
    				data : inputData,
    				success : function(result) {
    					if (result.code == 200) {
    						showAlert ("success", "Successfully added parties..");
    						$('#create-partyassoc-modal').modal('hide');
    						$("#refresh-partyassoc-btn").trigger('click');
    					} else {
    						showAlert ("error", data.message);
    					}
    				},
    				error : function() {
    					console.log('Error occured');
    					showAlert("error", "Error occured!");
    				},
    				complete : function() {
    				}
    			});
    		} else {
    			showAlert("error", "Please select atleast one row to be added!");
    		}
    	}
    });
	function getAllPartiesAssocGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/getAllParties";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#add-partyassoc-form";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getAllPartiesAssocGridData();
	}
});

fagReady("ASSOC_PARTY_LIST", function(el, api, colApi, gridApi){
    $("#partyassoc-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#partyassoc-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#partyassoc-clear-filter-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    	gridApi.refreshUserPreferences();
    });
	$("#sub-filter-clear-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    });
    $("#partyassoc-export-btn").click(function () {
    	gridApi.csvExport();
    });

    $("#refresh-partyassoc-btn").click(function () {
    	loadPartyAssocGrid(gridApi);
    });
    
    $("#partyassoc-search-btn").click(function () {
    	loadPartyAssocGrid(gridApi);
    });
    
    $("#partyassoc-remove-btn").click(function () {
    	console.log('call partyassoc-remove-btn');
    	var flag = true;
    	if(flag){
    		var selectedData = api.getSelectedRows();
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
    				url : "/common-portal/control/removeAssocParties",
    				async : true,
    				data : inputData,
    				success : function(result) {
    					if (result.code == 200) {
    						showAlert ("success", "Successfully removed parties..");
    						//loadPartyAssocGrid(gridApi);
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
    			showAlert("error", "Please select atleast one row to be removed!");
    		}
    	}
    });
    
    postLoadGridData(null, gridApi, "associated-parties", loadPartyAssocGrid);
});

function loadPartyAssocGrid(gridApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	$.ajax({
	  async: false,
	  url:'/common-portal/control/getAssocParties',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#partyassoc-search-form").serialize())),
	  success: function(data){
		  gridApi.setRowData(data.list);
	  }
	});
}

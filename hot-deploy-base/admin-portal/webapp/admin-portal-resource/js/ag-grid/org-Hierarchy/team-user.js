//fagReady("TEAM_MEMBER", function(el, api, colApi, gridApi){
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
//    $("#remove-btn").click(function () {
//        var selectedRows=api.getSelectedRows();
//    	if(selectedRows!= undefined && selectedRows != null&& selectedRows.length>0){
//    		gridApi.removeSelected();
//            setTimeout(() => { loadTeamMember(gridApi, api, colApi); }, 1000);
//    	} else {
//            showAlert("error","Please select atleast one record in the list");
//        }
//        
//    });
//    
//    loadTeamMember(gridApi, api);
//});
//
//var getTeamMemberUrl = "";
//function loadTeamMember(gridApi, api) {
//	if(getTeamMemberUrl == ""){
//		//resetGridStatusBar();
//		getTeamMemberUrl = getGridDataFetchUrl("TEAM_MEMBER");
//	}
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(getTeamMemberUrl != null && getTeamMemberUrl != "" && getTeamMemberUrl !="undefined"){
//		api.showLoadingOverlay();
//		var formInput = $('#searchForm, #limitForm').serialize();
//		$.ajax({
//		  async: false,
//		  url:getTeamMemberUrl,
//		  type:"POST",
//		  data: JSON.parse(JSON.stringify(formInput)),
//		  success: function(data){
//			  gridApi.setRowData(data.list);
//		  }
//		});
//	}
//}

$(function() {
	let viewteamMemberListInstanceId = "TEAM_MEMBER";
	let gridInstance = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = viewteamMemberListInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;

	gridInstance = prepareGridInstance(formDataObject);

	$('#team-member-save-pref-btn').click(function() {
		saveGridPreference(gridInstance, viewteamMemberListInstanceId, userId);
	});
	$('#team-member-clear-filter-btn').click(function() {
		clearGridPreference(gridInstance, viewteamMemberListInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if (gridInstance) {
			getViewTeamMemberListGridData();
		}
	});
	$('#team-member-sub-filter-clear-btn').click(function() {
		gridInstance.setFilterModel(null);
	});
	//	  $("#remove-btn").click(function () {
	//	  var selectedRows=gridInstance.getSelectedRows();
	//		if(selectedRows!= undefined && selectedRows != null&& selectedRows.length>0){
	//			//gridApi.removeSelected();
	//			
	//	      setTimeout(() => { getViewTeamMemberListGridData(); }, 1000);
	//		} else {
	//	      showAlert("error","Please select atleast one record in the list");
	//	  }
	//	  
	//	});
	$("#teams-member-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#remove-btn').on('click', function(e) {
		event.preventDefault();
		var selectedData = gridInstance.getSelectedRows();
		var selectedRowData = [];
		if (selectedData && selectedData.length > 0) {
			var partyIds = "";
			var emplTeamId = "";
			for (i = 0; i < selectedData.length; i++) {
				var data = selectedData[i];
				partyIds += data.partyId+",";
				if(!emplTeamId)
					emplTeamId = data.emplTeamId;
				//selectedRowData.push({"partyId": data.partyId, "emplTeamId": data.emplTeamId});
			}
			partyIds = partyIds.substring(0, partyIds.length - 1);
			//emplTeamIds = emplTeamIds.substring(0, emplTeamIds.length - 1);
			var inputData = {
				"partyId": partyIds, "emplTeamId": emplTeamId
			};
			$.ajax({
				type: "POST",
				url: "/admin-portal/control/removeUserTeams",
				async: true,
				data: inputData,
				success: function(result) {
					if (result.responseMessage === "success") {
						showAlert("success", "Successfully removed");
						getViewTeamMemberListGridData();
						event.preventDefault();
					} else {
						showAlert("error", result.message);
						event.preventDefault();
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
	function getViewTeamMemberListGridData() {
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("TEAM_MEMBER");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm, #limitForm_TEAM_MEMBER";
		callCtx.ajaxResponseKey = "list";

		setGridData(gridInstance, callCtx);
	}
	if (gridInstance) {
		getViewTeamMemberListGridData();
	}
});

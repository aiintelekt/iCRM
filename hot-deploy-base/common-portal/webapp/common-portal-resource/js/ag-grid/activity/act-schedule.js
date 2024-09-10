//fagReady("ACT_RES_AVAIL_LIST", function(el, api, colApi, gridApi){
//    $("#act-schedule-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#act-schedule-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#act-schedule-clear-filter-btn").click(function () {
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
//    $("#act-schedule-export-btn").click(function () {
//    	gridApi.csvExport();
//    }); 
//    
//    postLoadGrid(api, gridApi, null, "act-schedule", loadActScheduleGrid);
    //loadActScheduleGrid(gridApi, api, colApi);
    
//    $('#act-schedule-form').on('submit', function (e) {
//    	if (e.isDefaultPrevented()) {
//        	// handle the invalid form...
//      	} else {
//      		e.preventDefault();
//      		
//      		var action = "updateResAvailData";
//      		
//      		$.post("/admin-portal/control/"+action, $('#act-schedule-form').serialize(), function(data) {
//    			if (data.code == 200) {
//    				showAlert ("success", data.message);
//    				$("#act-schedule-modal").modal('hide');
//    				loadActScheduleGrid(gridApi, api, colApi);
//    			} else {
//    				showAlert ("error", data.message);
//    			}
//    		});
//      	}
//    });
    
//    $("#resavail-remove-btn").click(function () {
//    	var selectedData = api.getSelectedRows();
//		if (selectedData.length > 0) {
//			
//			console.log(selectedData);
//			
//		    var selectedEntryIds = "";
//		    for (i = 0; i < selectedData.length; i++) {
//		    	var data = selectedData[i];
//		    	selectedEntryIds += data.entryId+",";
//		    }
//		    selectedEntryIds = selectedEntryIds.substring(0, selectedEntryIds.length - 1);
//		    
//		    var inputData = {"selectedEntryIds": selectedEntryIds, "isRemoveAssociation": "Y"};
//		    $.ajax({
//				type : "POST",
//				url : "/admin-portal/control/removeResAvailData",
//				async : true,
//				data : inputData,
//				success : function(result) {
//					if (result.code == 200) {
//						showAlert ("success", "Successfully removed resource downtime# "+selectedEntryIds);
//						loadActScheduleGrid(gridApi, api, colApi);
//					} else {
//						showAlert ("error", data.message);
//					}
//				},
//				error : function() {
//					console.log('Error occured');
//					showAlert("error", "Error occured!");
//				},
//				complete : function() {
//				}
//			});
//			
//		} else {
//			showAlert("error", "Please select atleast one row to be removed!");
//		}
//    });
    
//});

//function loadActScheduleGrid(gridApi, api, colApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	$.ajax({
//	  async: false,
//	  url:'/admin-portal/control/searchResAvails',
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify($("#act-schedule-search-form").serialize())),
//	  success: function(result){
//		  gridApi.setRowData(result.data);
//		  //setTimeout(() => colApi.autoSizeAllColumns(), 1000);
//	  }
//	});
//}

$(function() {
	let actScheduleListInstanceId= "ACT_RES_AVAIL_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject1 = {};
	formDataObject1.gridInstanceId = actScheduleListInstanceId;
	formDataObject1.externalLoginKey = externalLoginKey;
	formDataObject1.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject1);
	
	$('#act-schedule-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, actScheduleListInstanceId, userId);
	});
	$('#act-schedule-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, actScheduleListInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject1);
		if(gridInstance){
			getScheduleGridData();
		}
	});
	$('#act-schedule-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#act-schedule-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
    $('#act-schedule-form').on('submit', function (e) {
    	if (e.isDefaultPrevented()) {
        	// handle the invalid form...
      	} else {
      		e.preventDefault();
      		
      		var action = "updateResAvailData";
      		
      		$.post("/admin-portal/control/"+action, $('#act-schedule-form').serialize(), function(data) {
    			if (data.code == 200) {
    				showAlert ("success", data.message);
    				$("#act-schedule-modal").modal('hide');
    				getScheduleGridData();
    			} else {
    				showAlert ("error", data.message);
    			}
    		});
      	}
    });
    $("#resavail-remove-btn").click(function () {
    	var selectedData = gridInstance.getSelectedRows();
		if (selectedData.length > 0) {
			
			console.log(selectedData);
			
		    var selectedEntryIds = "";
		    for (i = 0; i < selectedData.length; i++) {
		    	var data = selectedData[i];
		    	selectedEntryIds += data.entryId+",";
		    }
		    selectedEntryIds = selectedEntryIds.substring(0, selectedEntryIds.length - 1);
		    
		    var inputData = {"selectedEntryIds": selectedEntryIds, "isRemoveAssociation": "Y"};
		    $.ajax({
				type : "POST",
				url : "/admin-portal/control/removeResAvailData",
				async : true,
				data : inputData,
				success : function(result) {
					if (result.code == 200) {
						showAlert ("success", "Successfully removed resource downtime# "+selectedEntryIds);
						getScheduleGridData();
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
    });
	function getScheduleGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/admin-portal/control/searchResAvails";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#act-schedule-search-form";
		callCtx.ajaxResponseKey = "data";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getScheduleGridData();
	}
});

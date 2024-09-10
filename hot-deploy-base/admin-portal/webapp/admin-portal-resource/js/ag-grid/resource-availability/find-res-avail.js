
//fagReady("RES_AVAIL_LIST", function(el, api, colApi, gridApi){
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
//        setTimeout(() => {  loadMainGrid(gridApi); }, 1000);
//    })
//
//    $("#main-search-btn").click(function () {
//    	loadMainGrid(gridApi);
//    });
//    
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
//						showAlert ("success", "Resource is removed from the Scheduling");
//						loadMainGrid(gridApi);
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
//    
//    loadMainGrid(gridApi);
//});
//
//function loadMainGrid(gridApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	$.ajax({
//	  async: false,
//	  url:'/admin-portal/control/searchResAvails',
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify($("#availablity-search-form").serialize())),
//	  success: function(result){
//		  gridApi.setRowData(result.data);
//	  }
//	});
//}


$(function() {
	let resAvailsInstanceId= "RES_AVAIL_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = resAvailsInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#resavail-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, resAvailsInstanceId, userId);
	});
	$("#resavail-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#resavail-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, resAvailsInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getResAvailsGridData();
		}
	});
	$('#resavail-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#main-search-btn").click(function () {
		getResAvailsGridData();
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
						showAlert ("success", "Resource is removed from the Scheduling");
						getResAvailsGridData();
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
	function getResAvailsGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/admin-portal/control/searchResAvails";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#availablity-search-form";
		callCtx.ajaxResponseKey = "data";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getResAvailsGridData();
	}
});
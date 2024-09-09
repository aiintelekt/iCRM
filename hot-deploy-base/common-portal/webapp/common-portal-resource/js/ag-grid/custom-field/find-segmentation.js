//fagReady("SEGMENTATIONS", function(el, api, colApi, gridApi){
//    $("#segmentation-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#segmentation-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#segmentation-clear-filter-btn").click(function () {
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
//    $("#segmentation-export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//
//    $("#refresh-segmentation-btn").click(function () {
//    	loadSegmentationGrid(gridApi);
//    });
//    
//    $("#find-segmentation").click(function(event) {
//    	loadSegmentationGrid(gridApi);
//    });
//    $("#filter_segment_filterGroupCode").change(function() {
//    	if(this.checked){
//    		$("#filterGroupCode").val("N");
//    	}else{
//    		$("#filterGroupCode").val("Y");
//    	}
//    	loadSegmentationGrid(gridApi);
//    });
//    $("#segmentation-remove-btn").click(function () {
//    	
//		var selectedData = api.getSelectedRows();
//		console.log(selectedData);
//		if (selectedData.length > 0) {
//		    var groupIds = "";
//		    var customFieldIds = "";
//		    var partyIds = "";
//		    
//		    var domainEntityId = "";
//		    var domainEntityType = "";
//		    
//		    for (i = 0; i < selectedData.length; i++) {
//		    	var data = selectedData[i];
//		    	groupIds += data.groupId+",";
//		    	customFieldIds += data.customFieldId+",";
//		    	partyIds += data.partyId+",";
//		    	
//		    	domainEntityId = data.domainEntityId;
//		    	domainEntityType = data.domainEntityType;
//		    }
//		    groupIds = groupIds.substring(0, groupIds.length - 1);
//		    customFieldIds = customFieldIds.substring(0, customFieldIds.length - 1);
//		    partyIds = partyIds.substring(0, partyIds.length - 1);
//		    
//		    var inputData = {"groupIds": groupIds, "customFieldIds": customFieldIds, "partyIds": partyIds, "domainEntityId": domainEntityId, "domainEntityType": domainEntityType};
//		    
//		    $.ajax({
//				type : "POST",
//				url : "/common-portal/control/removeSegmentation",
//				async : true,
//				data : inputData,
//				success : function(result) {
//					if (result.code == 200) {
//						showAlert ("success", "Successfully removed selected segmentation");
//						loadSegmentationGrid(gridApi);
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
//		} else {
//			showAlert("error", "Please select atleast one row to be removed!");
//		}
//		
//    });
//    
//    $("#add-segmentation").click(function(event) {
//    	var inputData = {};
//    	var paramStr = $("#segmentation-add-form").serialize();
//    	inputData = JSON.parse(JSON.stringify(paramStr));
//
//    	$.ajax({
//    		type: "POST",
//         	url: "/common-portal/control/addSegmentation",
//            data:  inputData,
//            async: false,
//            success: function (data) {   
//                if (data.code == 200) {
//                	showAlert("success", "Successfully added");
//                	loadSegmentationGrid(gridApi);
//                } else {
//                	showAlert("error", data.message);
//                }
//                
//            }
//            
//    	}); 
//    });
//    
//    postLoadGridData(null, gridApi, "segmentation", loadSegmentationGrid);
//    
//    //loadSegmentationGrid(gridApi);
//});
//
//function loadSegmentationGrid(gridApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	$.ajax({
//	  async: false,
//	  url:'/common-portal/control/searchSegmentations',
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify($("#segmentation-search-form").serialize())),
//	  success: function(result){
//		  gridApi.setRowData(result.data);
//	  }
//	});
//}

$(function() {
	let segInstanceId= "SEGMENTATIONS";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = segInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	

	gridInstance = prepareGridInstance(formDataObject);
	$("#refresh-segmentation-btn").click(function () {
		getSegGridData();
	});

	$("#find-segmentation").click(function(event) {
		getSegGridData();
	});
	$("#filter_segment_filterGroupCode").change(function() {
		if(this.checked){
			$("#filterGroupCode").val("N");
		}else{
			$("#filterGroupCode").val("Y");
		}
		getSegGridData();
	});
	$("#segmentation-remove-btn").click(function () {

		var selectedData = gridInstance.getSelectedRows();
		console.log(selectedData);
		if (selectedData.length > 0) {
			var groupIds = "";
			var customFieldIds = "";
			var partyIds = "";

			var domainEntityId = "";
			var domainEntityType = "";

			for (i = 0; i < selectedData.length; i++) {
				var data = selectedData[i];
				groupIds += data.groupId+",";
				customFieldIds += data.customFieldId+",";
				partyIds += data.partyId+",";

				domainEntityId = data.domainEntityId;
				domainEntityType = data.domainEntityType;
			}
			groupIds = groupIds.substring(0, groupIds.length - 1);
			customFieldIds = customFieldIds.substring(0, customFieldIds.length - 1);
			partyIds = partyIds.substring(0, partyIds.length - 1);

			var inputData = {"groupIds": groupIds, "customFieldIds": customFieldIds, "partyIds": partyIds, "domainEntityId": domainEntityId, "domainEntityType": domainEntityType};

			$.ajax({
				type : "POST",
				url : "/common-portal/control/removeSegmentation",
				async : true,
				data : inputData,
				success : function(result) {
					if (result.code == 200) {
						showAlert ("success", "Successfully removed selected segmentation");
						getSegGridData();
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

	$("#add-segmentation").click(function(event) {
		var inputData = {};
		var paramStr = $("#segmentation-add-form").serialize();
		inputData = JSON.parse(JSON.stringify(paramStr));

		$.ajax({
			type: "POST",
			url: "/common-portal/control/addSegmentation",
			data:  inputData,
			async: false,
			success: function (data) {   
				if (data.code == 200) {
					showAlert("success", "Successfully added");
					getSegGridData();
				} else {
					showAlert("error", data.message);
				}

			}

		}); 
	});
	$('#segmentation-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, segInstanceId, userId);
	});
	$('#segmentation-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, segInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getSegGridData();
		}
	});
	$('#segmentation-sub-filter-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#segmentation-export-btn").click(function () {
		gridInstance.exportDataAsCsv();
	});

	function getSegGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/searchSegmentations";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#segmentation-search-form";
		callCtx.ajaxResponseKey = "data";

		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getSegGridData();
	}
});

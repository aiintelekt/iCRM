var inspectStatusList = JSON.parse($('#inspectStatusList').val());

var gridInstanceId = $("#gridInstanceId").val();
//fagReady(gridInstanceId, function(el, api, colApi, gridApi){
//    $("#order-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#order-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#order-clear-filter-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    	gridApi.refreshUserPreferences();
//    });
//	$("#order-sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#order-export-btn").click(function () {
//    	gridApi.csvExport();
//    });

//    $("#refresh-order-btn").click(function () {
//    	loadorderGrid(gridApi);
//    });
//    
//    $("#order-remove-btn").click(function () {
//    	var selectedData = api.getSelectedRows();
//		if (selectedData.length > 0) {
//			
//			console.log(selectedData);
//			
//		    var selectedEntryIds = "";
//		    for (i = 0; i < selectedData.length; i++) {
//		    	var data = selectedData[i];
//		    	selectedEntryIds += data.lineItemIdentifier+",";
//		    }
//		    selectedEntryIds = selectedEntryIds.substring(0, selectedEntryIds.length - 1);
//		    
//		    var inputData = {"selectedEntryIds": selectedEntryIds, "srNumber": $('#order-search-form input[name=srNumber]').val()};
//		    $.ajax({
//				type : "POST",
//				url : "/sr-portal/control/removeOrderLineAssocData",
//				async : true,
//				data : inputData,
//				success : function(result) {
//					if (result.code == 200) {
//						showAlert ("success", result.message);
//						loadorderGrid(gridApi);
//					} else {
//						showAlert ("error", result.message);
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
//    $("#approve-im-btn").click(function () {
//    	var selectedRows = api.getSelectedRows();
//    	 if(selectedRows){
//    		 var rows = JSON.stringify(selectedRows);
//    		 //$("#selectedRows").val(rows);
//    		 //$("#addTeamToUserForm").submit();
//    		 var inputData = {"selectedRows": rows};
//    		 $.ajax({
// 				type : "POST",
// 				url : "approveIssueMaterial",
// 				async : true,
// 				data : inputData,
// 				success : function(result) {
// 					if (result.code == 200) {
// 						showAlert ("success", "Order line item's approved successfully!");
// 						loadorderGrid(gridApi);
// 					} else {
// 						showAlert ("error", data.message);
// 					}
// 				}
// 			});
//          } else {
//             showAlert("error","Please select atleast one record in the list")
//          }
//    });
//    
//    $("#proof-btn").click(function () {
//    	var selectedData = api.getSelectedRows();
//    	console.log('selectedData.length: '+selectedData.length);
//    	if (selectedData.length != 2) {
//    		showAlert ("error", "Please select 2 rows only");
//    	} else {
//			
//			console.log(selectedData);
//			
//			var selectedEntryIds = "";
//		    for (i = 0; i < selectedData.length; i++) {
//		    	var data = selectedData[i];
//		    	selectedEntryIds += data.lineItemIdentifier+",";
//		    }
//		    selectedEntryIds = selectedEntryIds.substring(0, selectedEntryIds.length - 1);
//		    
//		    var inputData = {"selectedEntryIds": selectedEntryIds, "srNumber": $('#order-search-form input[name=srNumber]').val()};
//		    $.ajax({
//				type : "POST",
//				url : "/sr-portal/control/getProofData",
//				async : true,
//				data : inputData,
//				success : function(result) {
//					if (result.code == 200) {
//						//showAlert ("success", "SR and Order association removed!");
//						var proofItem1 = result.proofData.externalId_1+'_'+result.proofData.itemNumber_1;
//						var proofItem2 = result.proofData.externalId_2+'_'+result.proofData.itemNumber_2;
//						
//						$("#proof-title").html('Proofing '+proofItem1+' & '+proofItem2);
//						$("#part-changes").html(result.proofData.partChanges);
//						
//						$("#proof-data-title-1").html(proofItem1);
//						$("#proof-data-title-2").html(proofItem2);
//						
//						$("#proof-data-desc-1").html(result.proofData.itemDesc_1);
//						$("#proof-data-desc-2").html(result.proofData.itemDesc_2);
//						
//						highlightText($("#proof-data-desc-1"), $("#proof-data-desc-2"), 'highlight');
//						highlightText($("#proof-data-desc-2"), $("#proof-data-desc-1"), 'highlight2');
//					} else {
//						showAlert ("error", data.message);
//					}
//				}
//			});
//			
//			$("#proof-modal").modal('show');
//			//$('#show-des-modal').modal("show");
//		}
//    });

//	$("#update-btn").click(function () {
//    	gridApi.saveUpdates();
//        setTimeout(() => { 
//							loadorderGrid(gridApi);
//							gridApi.refreshUserPreferences(); 
//						}, 1000);
//    });
    
//    $("#proof-action-btn").click(function () {
//    	var selectedData = api.getSelectedRows();
//    	console.log('selectedData.length: '+selectedData.length);
//    	if (selectedData.length != 2) {
//    		showAlert ("error", "Please select 2 rows only");
//    	} else {
//			
//			console.log(selectedData);
//			
//			var selectedEntryIds = "";
//		    for (i = 0; i < selectedData.length; i++) {
//		    	var data = selectedData[i];
//		    	selectedEntryIds += data.lineItemIdentifier+",";
//		    }
//		    selectedEntryIds = selectedEntryIds.substring(0, selectedEntryIds.length - 1);
//		    
//		    var inputData = {"selectedEntryIds": selectedEntryIds, "srNumber": $('#order-search-form input[name=srNumber]').val()};
//		    $.ajax({
//				type : "POST",
//				url : "/sr-portal/control/performProof",
//				async : true,
//				data : inputData,
//				success : function(result) {
//					if (result.code == 200) {
//						$("#proof-modal").modal('hide');
//						showAlert ("success", "Successfully Proofed");
//					} else {
//						showAlert ("error", data.message);
//					}
//				}
//			});
//		}
//    });
    
//    postLoadGridData(null, gridApi, "sr-orders", loadorderGrid);
//    if ($('#isSrOrderAssoc').val() && $('#isSrOrderAssoc').val()==='Y') {
//    	loadorderGrid(gridApi);
//    }
    
    //loadorderGrid(gridApi);
    //api.sizeColumnsToFit();
//});
// ag grid custom dropdown implementation start
function extractValues(mappings) {
	return Object.keys(mappings);
}
function lookupValue(mappings, key) {
	return mappings[key];
}
function lookupKey(mappings, name) {
	for ( var key in mappings) {
		if (mappings.hasOwnProperty(key)) {
			if (name === mappings[key]) {
				return key;
			}
		}
	}
}
//function loadorderGrid(gridApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	$.ajax({
//	  async: false,
//	  url:'/sr-portal/control/searchOrders',
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify($("#order-search-form").serialize())),
//	  success: function(result){
//		  gridApi.setRowData(result.data);
//	  }
//	});
//}
$(function() {
	let orderListInstanceId= gridInstanceId;
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = orderListInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#teams-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, orderListInstanceId, userId);
	});
	$('#teams-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, orderListInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getLoadOrderGridData();
		}
	});
	$('#teams-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	if ($('#isSrOrderAssoc').val() && $('#isSrOrderAssoc').val()==='Y') {
		getLoadOrderGridData();
    }
    $("#refresh-order-btn").click(function () {
		getLoadOrderGridData();
    });

	$("#update-btn").click(function () {
//    	gridApi.saveUpdates();
        setTimeout(() => { getLoadOrderGridData(); }, 1000);
    });
	$("#order-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
    $("#order-remove-btn").click(function () {
    	var selectedData = gridInstance.getSelectedRows();
		if (selectedData.length > 0) {
			
			console.log(selectedData);
			
		    var selectedEntryIds = "";
		    for (i = 0; i < selectedData.length; i++) {
		    	var data = selectedData[i];
		    	selectedEntryIds += data.lineItemIdentifier+",";
		    }
		    selectedEntryIds = selectedEntryIds.substring(0, selectedEntryIds.length - 1);
		    
		    var inputData = {"selectedEntryIds": selectedEntryIds, "srNumber": $('#order-search-form input[name=srNumber]').val()};
		    $.ajax({
				type : "POST",
				url : "/sr-portal/control/removeOrderLineAssocData",
				async : true,
				data : inputData,
				success : function(result) {
					if (result.code == 200) {
						showAlert ("success", result.message);
						getLoadOrderGridData();
					} else {
						showAlert ("error", result.message);
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
    
    $("#approve-im-btn").click(function () {
    	var selectedRows = gridInstance.getSelectedRows();
    	 if(selectedRows){
    		 var rows = JSON.stringify(selectedRows);
    		 var inputData = {"selectedRows": rows};
    		 $.ajax({
 				type : "POST",
 				url : "approveIssueMaterial",
 				async : true,
 				data : inputData,
 				success : function(result) {
 					if (result.code == 200) {
 						showAlert ("success", "Order line item's approved successfully!");
 						getLoadOrderGridData();
 					} else {
 						showAlert ("error", data.message);
 					}
 				}
 			});
          } else {
             showAlert("error","Please select atleast one record in the list")
          }
    });
    
    $("#proof-btn").click(function () {
    	var selectedData = gridInstance.getSelectedRows();
    	console.log('selectedData.length: '+selectedData.length);
    	if (selectedData.length != 2) {
    		showAlert ("error", "Please select 2 rows only");
    	} else {
			
			console.log(selectedData);
			
			var selectedEntryIds = "";
		    for (i = 0; i < selectedData.length; i++) {
		    	var data = selectedData[i];
		    	selectedEntryIds += data.lineItemIdentifier+",";
		    }
		    selectedEntryIds = selectedEntryIds.substring(0, selectedEntryIds.length - 1);
		    
		    var inputData = {"selectedEntryIds": selectedEntryIds, "srNumber": $('#order-search-form input[name=srNumber]').val()};
		    $.ajax({
				type : "POST",
				url : "/sr-portal/control/getProofData",
				async : true,
				data : inputData,
				success : function(result) {
					if (result.code == 200) {
						var proofItem1 = result.proofData.externalId_1+'_'+result.proofData.itemNumber_1;
						var proofItem2 = result.proofData.externalId_2+'_'+result.proofData.itemNumber_2;
						
						$("#proof-title").html(DOMPurify.sanitize('Proofing '+proofItem1+' & '+proofItem2));
						$("#part-changes").html(DOMPurify.sanitize(result.proofData.partChanges));
						
						$("#proof-data-title-1").html(DOMPurify.sanitize(proofItem1));
						$("#proof-data-title-2").html(DOMPurify.sanitize(proofItem2));
						
						$("#proof-data-desc-1").html(DOMPurify.sanitize(result.proofData.itemDesc_1));
						$("#proof-data-desc-2").html(DOMPurify.sanitize(result.proofData.itemDesc_2));
						
						highlightText($("#proof-data-desc-1"), $("#proof-data-desc-2"), 'highlight');
						highlightText($("#proof-data-desc-2"), $("#proof-data-desc-1"), 'highlight2');
					} else {
						showAlert ("error", data.message);
					}
				}
			});
			
			$("#proof-modal").modal('show');
		}
    });
    $("#proof-action-btn").click(function () {
    	var selectedData = gridInstance.getSelectedRows();
    	console.log('selectedData.length: '+selectedData.length);
    	if (selectedData.length != 2) {
    		showAlert ("error", "Please select 2 rows only");
    	} else {
			
			console.log(selectedData);
			
			var selectedEntryIds = "";
		    for (i = 0; i < selectedData.length; i++) {
		    	var data = selectedData[i];
		    	selectedEntryIds += data.lineItemIdentifier+",";
		    }
		    selectedEntryIds = selectedEntryIds.substring(0, selectedEntryIds.length - 1);
		    
		    var inputData = {"selectedEntryIds": selectedEntryIds, "srNumber": $('#order-search-form input[name=srNumber]').val()};
		    $.ajax({
				type : "POST",
				url : "/sr-portal/control/performProof",
				async : true,
				data : inputData,
				success : function(result) {
					if (result.code == 200) {
						$("#proof-modal").modal('hide');
						showAlert ("success", "Successfully Proofed");
					} else {
						showAlert ("error", data.message);
					}
				}
			});
		}
    });
	function getLoadOrderGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/sr-portal/control/searchOrders";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#order-search-form";
		callCtx.ajaxResponseKey = "data";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getLoadOrderGridData();
	}
});

function viewSkuDescription(description){
	$('#show-des-modal_des_title').html("Lot Detail");
	$('#show-des-modal_des_value').html(DOMPurify.sanitize(base64.decode(description)));
	$('#show-des-modal').modal("show");
}
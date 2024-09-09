
//fagReady("SR_ACT_TIME_ENTRY", function(el, api, colApi, gridApi){
//    $("#time-entry-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#time-entry-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//
//		var selectedRows = api.getSelectedRows();
//		alert("selectedRows----->"+selectedRows);
//    });
//    $("#time-entry-clear-filter-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    	gridApi.refreshUserPreferences();
//    });
//	$("#time-entry-sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#time-entry-export-btn").click(function () {
//    	gridApi.csvExport();
//    }); 
//	
	/*
	$('#updateTimeEntry').click(function(){
		var selectedRows = api.getSelectedRows();
		if(selectedRows!= undefined && selectedRows != null && selectedRows.length>0){
    		 var rows = JSON.stringify(selectedRows);
    		 $("#updateTimeEntryFrom #selectedRows").val(rows);
    		 //$("#updateTimeEntryFrom").submit();
			
			$.ajax({
			  async: false,
			  url:'updateTimeEntry',
			  type:"POST",
			  data: JSON.parse(JSON.stringify($("#updateTimeEntryFrom").serialize())),
			  success: function(data){
				if (data.code == 200) {
					postLoadGridData(null, gridApi, "sr-time-entry", loadActivityTimeEntryGrid);
					showAlert("success", "Time entry successfully updated");
				}
			  }
			});	
		}
    });
	*/

//	$('#update-time-entry-btn').click(function(){
//		$('#updateTimeEntryFrom #hourminute_error').html("");
//		$('#updateTimeEntryFrom #hour_error_row').hide();
//		var valid = true;
//		var partyId = $('#updateTimeEntryFrom #partyId').val();
//		var rateTypeId = $('#updateTimeEntryFrom #rateTypeId').val();
//		if(!rateTypeId){
//			$("#updateTimeEntryFrom  #rateTypeId_error").html("Please select purpose");
//			valid=false;
//		} 
//		if("TOLL_CHARGE"== rateTypeId || "ANCILLARY_COST" == rateTypeId){
//			var cost = $('#updateTimeEntryFrom #cost').val();
//			if(!cost){
//				valid =false;
//				$("#updateTimeEntryFrom #cost_error").html("Please enter cost");
//			}
//		} else {
//			var hours = $('#updateTimeEntryFrom #hours').val();
//			var minutes = $('#updateTimeEntryFrom #minutes').val();
//			
//			if((!hours || hours == "0") && (!minutes ||minutes == "0")){
//				$('#updateTimeEntryFrom #hour_error_row').show();
//				$('#updateTimeEntryFrom #hourminute_error').html("Hours and Minutes should not be zero");
//				valid =false;
//			}
//		}
//		
//		var timeEntryDate = $('#updateTimeEntryFrom #timeEntryDate').val();
//		if(!timeEntryDate){
//			$("#updateTimeEntryFrom  #timeEntryDate_error").html("Please select date of service");
//			valid=false;
//		} else{
//			$("#updateTimeEntryFrom  #timeEntryDate_error").html("");
//		}
//		
//		$.ajax({
//			async: false,
//		  	url:'/common-portal/control/validateRateConfig',
//		  	type:"POST",
//			data: {"partyId":partyId,"rateTypeId":rateTypeId},
//			success: function(data){
//				if (data.code == 200) {
//					var responseStatus = data.responseStatus;
//					var confirmMsg = "";
//					if(responseStatus && "STANDARD_TECH" === responseStatus){
//						confirmMsg = "Standard rates and Technician rates is not configured, Do you want to proceed?";
//					} else if(responseStatus && "TECH" === responseStatus){
//						confirmMsg = "Technician rates is not configured, Do you want to proceed?";		
//					}
//					if(responseStatus){
//						valid = false;
//						
//						bootbox.confirm(confirmMsg, function(result) {
//						   if (!result) {
//								valid =false;
//						   } else{
//								setTimeout( function() {
//									updateTimeEntryVal();
//								}, 100 );
//							 	
//					       }
//					    });
//					} else{
//						valid = true;
//					}
//					
//				} else {
//					showAlert ("error", data.message);
//					valid =false;
//				}
//			}		
//		});
//		
//		if(valid){	
//			updateTimeEntryVal();
//		}
//    });
//	
//	$('#create-time-entry-btn').click(function(){
//		$("#createTimeEntryFrom #partyId_error").html("");
//		$("#createTimeEntryFrom  #rateTypeId_error").html("");
//		$("#createTimeEntryFrom #hours_error").html("");
//		$("#createTimeEntryFrom #minutes_error").html("");
//		$("#createTimeEntryFrom #hours_cost").html("");
//		
//		var partyId=$("#createTimeEntryFrom #partyId").val();
//		var rateTypeId=$("#createTimeEntryFrom #rateTypeId").val();
//		var hours=$("#createTimeEntryFrom #hours").val();
//		var minutes=$("#createTimeEntryFrom #minutes").val();
//		var cost=$("#createTimeEntryFrom #cost").val();
//		var valid = true;
//		if(!partyId) $("#createTimeEntryFrom #partyId_error").html("Please select technician");
//		if(!rateTypeId) $("#createTimeEntryFrom  #rateTypeId_error").html("Please select purpose");
//		if("TOLL_CHARGE"== rateTypeId || "ANCILLARY_COST" == rateTypeId){
//			if(!cost) {
//				$("#createTimeEntryFrom #cost_error").html("Please enter cost");
//				valid = false;
//			}
//		} else if(rateTypeId){
//			if((!hours || hours == "0") && (!minutes ||minutes == "0")){
//				$("#createTimeEntryFrom #hours_error").html("Please enter hours");
//				valid =false;
//			}
//		}
//		var workEffortId=$("#createTimeEntryFrom #workEffortId").val();
//		if(!workEffortId){
//			$("#createTimeEntryFrom #workEffortId_error").html("Please select activity");
//			valid =false;
//		}
//		var techId=$("#createTimeEntryFrom #partyId").val();
//		if(!techId){
//			$("#createTimeEntryFrom #partyId_error").html("Please select technician");
//			valid =false;
//		}
//		var purpose=$("#createTimeEntryFrom #rateTypeId").val();
//		if(!purpose){
//			$("#createTimeEntryFrom #rateTypeId_error").html("Please select purpose");
//			valid =false;
//		}
//		var timeEntryDate = $('#createTimeEntryFrom #timeEntryDate').val();
//		if(!timeEntryDate){
//			$("#createTimeEntryFrom  #timeEntryDate_error").html("Please select date of service");
//			valid=false;
//		} else{
//			$("#createTimeEntryFrom  #timeEntryDate_error").html("");
//		}
//		
//		$.ajax({
//			async: false,
//		  	url:'/common-portal/control/validateRateConfig',
//		  	type:"POST",
//			data: {"partyId":partyId,"rateTypeId":rateTypeId},
//			success: function(data){
//				if (data.code == 200) {
//					var responseStatus = data.responseStatus;
//					var confirmMsg = "";
//					if(responseStatus && "STANDARD_TECH" === responseStatus){
//						confirmMsg = "Standard rates and Technician rates is not configured, Do you want to proceed?";
//					} else if(responseStatus && "TECH" === responseStatus){
//						confirmMsg = "Technician rates is not configured, Do you want to proceed?";		
//					}
//					if(responseStatus){
//						valid = false;
//						
//						bootbox.confirm(confirmMsg, function(result) {
//						   if (!result) {
//								valid =false;
//						   } else{
//								setTimeout( function() {
//									createTimeEntry();
//								}, 100 );
//							 	
//					       }
//					    });
//					} else{
//						valid = true;
//					}
//					
//				} else {
//					showAlert ("error", data.message);
//					valid =false;
//				}
//			}		
//		});
//		
//		if(valid) {
//			createTimeEntry();
//		}
//		
//		
//	});
//	
//	$("#sync3rdPartyInvoice").click(function () {
//    	let custRequestId = $("#custRequestId").val();
//    	$.ajax({
//		  async: false,
//		  url:'/common-portal/control/sync3rdPartyInvoice',
//		  type:"POST",
//		  data: {"custRequestId":custRequestId},
//		  success: function(data){
//		  	if (data.code == 200) {
//				postLoadGridData(null, gridApi, "sr-time-entry", loadActivityTimeEntryGrid);
//			} else{
//				showAlert("error",data.message);
//			}
//		  }
//		});
//    });
//
//    postLoadGridData(null, gridApi, "sr-time-entry", loadActivityTimeEntryGrid);
//    
//    //loadActivityTimeEntryGrid(gridApi, api, colApi);
//    
//});

//function createTimeEntry(){
//	$.ajax({
//		async: false,
//	  	url:'createSrTimeEntryService',
//	  	type:"POST",
//		data: JSON.parse(JSON.stringify($("#createTimeEntryFrom").serialize())),
//		success: function(data){
//			if (data.code == 200) {
//				$('#create-time-entry').modal('hide');
//				//postLoadGridData(null, gridApi, "sr-time-entry", loadActivityTimeEntryGrid);
//				//window.location.reload();
//				setTimeout(location.reload.bind(location),1000);
//				showAlert("success", "Time entry successfully created");	
//			} else{
//				$('#create-time-entry').modal('hide');
//				showAlert("error",data.message);
//			}
//		}
//	});
//}
//
//function updateTimeEntryVal(){
//		$.ajax({
//			async: false,
//		  	url:'updateTimeEntry',
//		  	type:"POST",
//			data: JSON.parse(JSON.stringify($("#updateTimeEntryFrom").serialize())),
//			success: function(data){
//				if (data.code == 200) {
//					$('#update-time-entry').modal('hide');
//					//postLoadGridData(null, gridApi, "sr-time-entry", loadActivityTimeEntryGrid);
//					//window.location.reload();
//					setTimeout(location.reload.bind(location),1000);
//					showAlert("success", "Time entry successfully updated");	
//				}
//			}
//		});	
//	}

//function loadActivityTimeEntryGrid(gridApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	$.ajax({
//	  async: false,
//	  url:'/sr-portal/control/getSrActivityTimeEntries',
//	  type:"POST",
//	  data: {"srNumber": $("#custRequestId").val()},
//	  success: function(data){
//		  gridApi.setRowData(data.list);
//		  //setTimeout(() => colApi.autoSizeAllColumns(), 1000);
//	  }
//	});
//}

$(function() {
	let timeEntriesListInstanceId= "SR_ACT_TIME_ENTRY";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = timeEntriesListInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#time-entry-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, timeEntriesListInstanceId, userId);
	});
	$('#time-entry-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, timeEntriesListInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getTimeEntriesGridData();
		}
	});
	$('#time-entry-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#time-entry-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#update-time-entry-btn').click(function(){
		$('#updateTimeEntryFrom #hourminute_error').html("");
		$('#updateTimeEntryFrom #hour_error_row').hide();
		var valid = true;
		var partyId = $('#updateTimeEntryFrom #partyId').val();
		var rateTypeId = $('#updateTimeEntryFrom #rateTypeId').val();
		if(!rateTypeId){
			$("#updateTimeEntryFrom  #rateTypeId_error").html("Please select purpose");
			valid=false;
		} 
		if("TOLL_CHARGE"== rateTypeId || "ANCILLARY_COST" == rateTypeId){
			var cost = $('#updateTimeEntryFrom #cost').val();
			if(!cost){
				valid =false;
				$("#updateTimeEntryFrom #cost_error").html("Please enter cost");
			}
		} else {
			var hours = $('#updateTimeEntryFrom #hours').val();
			var minutes = $('#updateTimeEntryFrom #minutes').val();
			
			if((!hours || hours == "0") && (!minutes ||minutes == "0")){
				$('#updateTimeEntryFrom #hour_error_row').show();
				$('#updateTimeEntryFrom #hourminute_error').html("Hours and Minutes should not be zero");
				valid =false;
			}
		}
		
		var timeEntryDate = $('#updateTimeEntryFrom #timeEntryDate').val();
		if(!timeEntryDate){
			$("#updateTimeEntryFrom  #timeEntryDate_error").html("Please select date of service");
			valid=false;
		} else{
			$("#updateTimeEntryFrom  #timeEntryDate_error").html("");
		}
		
		$.ajax({
			async: false,
		  	url:'/common-portal/control/validateRateConfig',
		  	type:"POST",
			data: {"partyId":partyId,"rateTypeId":rateTypeId},
			success: function(data){
				if (data.code == 200) {
					var responseStatus = data.responseStatus;
					var confirmMsg = "";
					if(responseStatus && "STANDARD_TECH" === responseStatus){
						confirmMsg = "Standard rates and Technician rates is not configured, Do you want to proceed?";
					} else if(responseStatus && "TECH" === responseStatus){
						confirmMsg = "Technician rates is not configured, Do you want to proceed?";		
					}
					if(responseStatus){
						valid = false;
						
						bootbox.confirm(confirmMsg, function(result) {
						   if (!result) {
								valid =false;
						   } else{
								setTimeout( function() {
									updateTimeEntryVal();
								}, 100 );
							 	
					       }
					    });
					} else{
						valid = true;
					}
					
				} else {
					showAlert ("error", data.message);
					valid =false;
				}
			}		
		});
		
		if(valid){	
			updateTimeEntryVal();
		}
    });
	
	$('#create-time-entry-btn').click(function(){
		$("#createTimeEntryFrom #partyId_error").html("");
		$("#createTimeEntryFrom  #rateTypeId_error").html("");
		$("#createTimeEntryFrom #hours_error").html("");
		$("#createTimeEntryFrom #minutes_error").html("");
		$("#createTimeEntryFrom #hours_cost").html("");
		
		var partyId=$("#createTimeEntryFrom #partyId").val();
		var rateTypeId=$("#createTimeEntryFrom #rateTypeId").val();
		var hours=$("#createTimeEntryFrom #hours").val();
		var minutes=$("#createTimeEntryFrom #minutes").val();
		var cost=$("#createTimeEntryFrom #cost").val();
		var valid = true;
		if(!partyId) $("#createTimeEntryFrom #partyId_error").html("Please select technician");
		if(!rateTypeId) $("#createTimeEntryFrom  #rateTypeId_error").html("Please select purpose");
		if("TOLL_CHARGE"== rateTypeId || "ANCILLARY_COST" == rateTypeId){
			if(!cost) {
				$("#createTimeEntryFrom #cost_error").html("Please enter cost");
				valid = false;
			}
		} else if(rateTypeId){
			if((!hours || hours == "0") && (!minutes ||minutes == "0")){
				$("#createTimeEntryFrom #hours_error").html("Please enter hours");
				valid =false;
			}
		}
		var workEffortId=$("#createTimeEntryFrom #workEffortId").val();
		if(!workEffortId){
			$("#createTimeEntryFrom #workEffortId_error").html("Please select activity");
			valid =false;
		}
		var techId=$("#createTimeEntryFrom #partyId").val();
		if(!techId){
			$("#createTimeEntryFrom #partyId_error").html("Please select technician");
			valid =false;
		}
		var purpose=$("#createTimeEntryFrom #rateTypeId").val();
		if(!purpose){
			$("#createTimeEntryFrom #rateTypeId_error").html("Please select purpose");
			valid =false;
		}
		var timeEntryDate = $('#createTimeEntryFrom #timeEntryDate').val();
		if(!timeEntryDate){
			$("#createTimeEntryFrom  #timeEntryDate_error").html("Please select date of service");
			valid=false;
		} else{
			$("#createTimeEntryFrom  #timeEntryDate_error").html("");
		}
		
		$.ajax({
			async: false,
		  	url:'/common-portal/control/validateRateConfig',
		  	type:"POST",
			data: {"partyId":partyId,"rateTypeId":rateTypeId},
			success: function(data){
				if (data.code == 200) {
					var responseStatus = data.responseStatus;
					var confirmMsg = "";
					if(responseStatus && "STANDARD_TECH" === responseStatus){
						confirmMsg = "Standard rates and Technician rates is not configured, Do you want to proceed?";
					} else if(responseStatus && "TECH" === responseStatus){
						confirmMsg = "Technician rates is not configured, Do you want to proceed?";		
					}
					if(responseStatus){
						valid = false;
						
						bootbox.confirm(confirmMsg, function(result) {
						   if (!result) {
								valid =false;
						   } else{
								setTimeout( function() {
									createTimeEntry();
								}, 100 );
							 	
					       }
					    });
					} else{
						valid = true;
					}
					
				} else {
					showAlert ("error", data.message);
					valid =false;
				}
			}		
		});
		
		if(valid) {
			createTimeEntry();
		}
		
		
	});
	
	$("#sync3rdPartyInvoice").click(function () {
    	let custRequestId = $("#custRequestId").val();
    	$.ajax({
		  async: false,
		  url:'/common-portal/control/sync3rdPartyInvoice',
		  type:"POST",
		  data: {"custRequestId":custRequestId},
		  success: function(data){
		  	if (data.code == 200) {
				getTimeEntriesGridData();
			} else{
				showAlert("error",data.message);
			}
		  }
		});
    });
	function getTimeEntriesGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/sr-portal/control/getSrActivityTimeEntries";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#timeEntriesForm";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getTimeEntriesGridData();
	}
});
function createTimeEntry(){
	$.ajax({
		async: false,
	  	url:'createSrTimeEntryService',
	  	type:"POST",
		data: JSON.parse(JSON.stringify($("#createTimeEntryFrom").serialize())),
		success: function(data){
			if (data.code == 200) {
				$('#create-time-entry').modal('hide');
				//postLoadGridData(null, gridApi, "sr-time-entry", loadActivityTimeEntryGrid);
				//window.location.reload();
				setTimeout(location.reload.bind(location),1000);
				showAlert("success", "Time entry successfully created");	
			} else{
				$('#create-time-entry').modal('hide');
				showAlert("error",data.message);
			}
		}
	});
}

function updateTimeEntryVal(){
		$.ajax({
			async: false,
		  	url:'updateTimeEntry',
		  	type:"POST",
			data: JSON.parse(JSON.stringify($("#updateTimeEntryFrom").serialize())),
			success: function(data){
				if (data.code == 200) {
					$('#update-time-entry').modal('hide');
					//postLoadGridData(null, gridApi, "sr-time-entry", loadActivityTimeEntryGrid);
					//window.location.reload();
					setTimeout(location.reload.bind(location),1000);
					showAlert("success", "Time entry successfully updated");	
				}
			}
		});	
	}
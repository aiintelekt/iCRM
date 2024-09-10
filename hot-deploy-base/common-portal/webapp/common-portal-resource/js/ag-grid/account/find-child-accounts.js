//fagReady("CHILD_ACCOUNTS", function(el, api, colApi, gridApi){
//    $("#childAcc-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#childAcc-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//	$("#childAcc-sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#childAcc-clear-filter-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    	gridApi.refreshUserPreferences();
//    });
//    $("#childAcc-export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//
//    $("#childAcc-refresh-btn").click(function () {
//    	loadChildAccMainGrid(gridApi, api, colApi);
//    })
//    
//    //loadChildAccMainGrid(gridApi, api, colApi);
//    
//});

function addChildAccount(value){
	let partyId = value;
	let parentAccountId = $("#parentAccountId").val();
	let input ={};
	console.log("parentAccountId--"+parentAccountId+"--partyId---"+partyId);
	$("#childAccountId_val").val('');
	$("#childAccountId_desc").val('');
	if (parentAccountId && partyId){
		input={"partyId":partyId,"parentAccountId":parentAccountId};
		$.ajax({
			  async: true,
			  url:'/common-portal/control/addChildAccount',
			  type:"POST",
			  data: input,
			  success: function(data){
				  console.log(JSON.stringify(data));
				  let result = data ["responseMessage"];
				  let resultMsg ="";
				  if (result && result === "error") {
					  resultMsg = data ["errorMessage"];
					  showAlert("error",resultMsg);
				  }else if (result && result === "success") {
					  resultMsg = data ["successMessage"];
					  showAlert("success",resultMsg);
					  $("#childAcc-refresh-btn").trigger('click');
				  }else{
					  showAlert("success","Child account added successfully");
					  $("#childAcc-refresh-btn").trigger('click');
				  }
			  }
			});
	}else{
		let message = "Account details missing";
		if (parentAccountId){
			message = "Parent account details missing";
		}
		showAlert("error",message);
		return false;
	}
	
}
//function loadChildAccMainGrid(gridApi, api, colApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	var formInput = $('#child-accounts-form').serialize();
//	$.ajax({
//	  async: true,
//	  url:'/common-portal/control/searchChildAccounts',
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify(formInput)),
//	  success: function(data){
//		  gridApi.setRowData(data.data);
//	  }
//	});
//}


$(function() {
	const childAccountsListInstanceId= "CHILD_ACCOUNTS";
	const externalLoginKey = $("#externalLoginKey").val();
	const formDataObject = {};
	const userId = $("#userId").val();
	let gridInstance  = "";

	formDataObject.gridInstanceId = childAccountsListInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;

	gridInstance = prepareGridInstance(formDataObject);

	if(gridInstance){
		childAccountsListData();
	}

	function childAccountsListData() {
		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/searchChildAccounts";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#child-accounts-form";
		callCtx.ajaxResponseKey = "data";

		setGridData(gridInstance, callCtx);
	}

	$('#childAcc-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, childAccountsListInstanceId, userId);
	});

	$('#childAcc-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, childAccountsListInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			childAccountsListData();
		}
	});

	$('#childAcc-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
    $("#childAcc-refresh-btn").click(function () {
    	childAccountsListData();
    })
	$("#childAcc-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
});
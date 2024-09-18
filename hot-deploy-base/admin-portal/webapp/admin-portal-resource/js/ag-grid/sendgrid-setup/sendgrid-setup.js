//let apiGrid="";
//fagReady("SENDGRIDSETUP_LIST", function(el, api, colApi, gridApi){
//	$("#sendgrid-refresh-pref-btn").click(function () {
//		gridApi.refreshUserPreferences();
//	});
//	$("#sendgrid-save-pref-btn").click(function () {
//		gridApi.saveUserPreferences();
//	});
//	$("#sendgrid-clear-filter-btn").click(function () {
//		try{
//			gridApi.clearAllColumnFilters();
//		}catch(e){
//		}
//		gridApi.refreshUserPreferences();
//	});
//	$("#sendgrid-sub-filter-clear-btn").click(function () {
//		try{
//			gridApi.clearAllColumnFilters();
//		}catch(e){
//		}
//	});
//	$("#sendgrid-export-btn").click(function () {
//		gridApi.csvExport();
//	});
//	$("#sendgrid-search-btn").click(function () {
//		loadSendGridList(api, gridApi,colApi);
//	});
//	$("#updateSendgrid").click(function() {
//		event.preventDefault();
//		errorMessage("updateSendGridConfig","isDefaultsendgrid","isDefaultsendgrid_error");
//		var configName = $('#configNamesendgrid').val();
//		var configId = $('#updateSendGridConfig #configId').val();
//		var apiKey = $('#apiKeysendgrid').val();
//		var senderMail = $('#senderMailsendgrid').val();
//		var senderName = $('#senderNamesendgrid').val();
//		var isDefault = $('#isDefaultsendgrid').val();
//		var check = $('#check').val();
//		var skipBlacklistCheckValue = $('#sendGridSetup #skipBlacklistCheck').val();
//		if ((configName && apiKey && senderMail && senderName && isDefault && configId && skipBlacklistCheckValue) !=""){
//			$.ajax({
//				type:"POST",
//				url:"editSendGridConfig",
//				async:false,
//				data:{"configName": configName,"apiKey": apiKey,"senderMail": senderMail,"senderName": senderName,"isDefault": isDefault,"configId":configId,"check":check,"skipBlacklistCheck":skipBlacklistCheckValue},
//				success: function(result) {
//					if(result.responseMessage=="success"){
//						$('#editSendGridConfig').modal('hide');
//						loadSendGridList(gridApi, api, colApi);
//						showAlert("success", "Successfully updated ");
//					}else if(result.responseMessage=="error"){
//						$('#editSendGridConfig').modal('hide');
//						loadSendGridList(gridApi, api, colApi);
//						showAlert("error", "error in update process");
//					}
//				},
//				error:function(){
//					console.log('Error occured');
//					showAlert("error", "Error occured!");
//				},
//				complete: function(){}
//			});
//		} else {
//			errorMessage("updateSendGridConfig","isDefaultsendgrid","isDefaultsendgrid_error");
//		}
//	});
//	loadSendGridList(gridApi, api, colApi);
//});
//function loadSendGridList(gridApi,apiGrid,colApi) {
//	var rowData = [];
//	gridApi.setRowData(rowData);
//	apiGrid.showLoadingOverlay();
//	var formInput = $('#sendGridListForm').serialize();
//	$.ajax({
//		async: true,
//		url: '/admin-portal/control/getSendGridConfig',
//		type: "POST",
//		data: JSON.parse(JSON.stringify(formInput)),
//		success: function(data) {
//			gridApi.setRowData(data.data);
//			data.data = [];
//			paginateHandler(data);
//		}
//	});
//}


$(function() {
	let sendGridSetupInstanceId= "SENDGRIDSETUP_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = sendGridSetupInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#sendgrid-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, sendGridSetupInstanceId, userId);
	});

	$('#sendgrid-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, sendGridSetupInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getSendGridListGridData();
		}
	});
	$('#sendgrid-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#sendgrid-search-btn").click(function () {
		getSendGridListGridData();
	});
	$("#sendgrid-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$("#updateSendgrid").click(function() {
		event.preventDefault();
		errorMessage("updateSendGridConfig","isDefaultsendgrid","isDefaultsendgrid_error");
		var configName = $('#configNamesendgrid').val();
		var configId = $('#updateSendGridConfig #configId').val();
		var apiKey = $('#apiKeysendgrid').val();
		var senderMail = $('#senderMailsendgrid').val();
		var senderName = $('#senderNamesendgrid').val();
		var isDefault = $('#isDefaultsendgrid').val();
		var check = $('#check').val();
		var skipBlacklistCheckValue = $('#sendGridSetup #skipBlacklistCheck').val();
		if ((configName && apiKey && senderMail && senderName && isDefault && configId && skipBlacklistCheckValue) !=""){
			$.ajax({
				type:"POST",
				url:"editSendGridConfig",
				async:false,
				data:{"configName": configName,"apiKey": apiKey,"senderMail": senderMail,"senderName": senderName,"isDefault": isDefault,"configId":configId,"check":check,"skipBlacklistCheck":skipBlacklistCheckValue},
				success: function(result) {
					if(result.responseMessage=="success"){
						$('#editSendGridConfig').modal('hide');
						getSendGridListGridData();
						showAlert("success", "Successfully updated ");
					}else if(result.responseMessage=="error"){
						$('#editSendGridConfig').modal('hide');
						getSendGridListGridData();
						showAlert("error", "error in update process");
					}
				},
				error:function(){
					console.log('Error occured');
					showAlert("error", "Error occured!");
				},
				complete: function(){}
			});
		} else {
			errorMessage("updateSendGridConfig","isDefaultsendgrid","isDefaultsendgrid_error");
		}
	});
	function getSendGridListGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/admin-portal/control/getSendGridConfig";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#sendGridListForm";
		callCtx.ajaxResponseKey = "data";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getSendGridListGridData();
	}
});

function editSendGrid(configId,configName,apikey,senderMail,senderName,isDefault,createdDate,lastUpdatedDate) {
	$("#updateSendGridConfig #configNamesendgrid").val(configName);
	$("#updateSendGridConfig #apiKeysendgrid").val(apikey);
	$("#updateSendGridConfig #senderMailsendgrid").val(senderMail);
	$("#updateSendGridConfig #configId").val(configId);
	$("#updateSendGridConfig #senderNamesendgrid").val(senderName);
	$("#updateSendGridConfig #check").val("Y");
	if(isDefault !="" && isDefault !=undefined){
		$('#updateSendGridConfig #isDefaultsendgrid').val(isDefault).trigger('change');
	}
	$('#editSendGridConfig').modal('show');
}
$(document).ready(function() {
	$("#sendGridSetup #isDefault").change(function() {
		if ($("#sendGridSetup #isDefault").val() != "") {
			$('#sendGridSetup #isDefault_error').hide();
		} else {
			$('#sendGridSetup #isDefault_error').show();
		}
	});
	$("#updateSendGridConfig #isDefaultsendgrid").change(function() {
		if ($("#updateSendGridConfig #isDefaultsendgrid").val() != "") {
			$('#updateSendGridConfig #isDefaultsendgrid_error').hide();
		} else {
			$('#updateSendGridConfig #isDefaultsendgrid_error').show();
		}
	});
	$("#reset-sendgrid").click(function() {
		$('#sendGridSetup #isDefault').val("");
	});
	$("#createSendGrid").click(function() {
		errorMessage("sendGridSetup","isDefault","isDefault_error");
	});
});
function errorMessage(formId, inputId, errorText) {
	var type = document.getElementById(inputId);
	var selectedText = type.options[type.selectedIndex].innerHTML;
	var selectedValue = type.value;
	if (selectedValue == "") {
		$('#'+formId+' #'+errorText).html("Please select any one option in the list.");
		$('#'+formId+' #'+errorText).show();
		console.log("errorText" + errorText);
		console.log("inputId" + inputId);
	} else {
		$('#'+formId+' #'+errorText).hide();
	}
}
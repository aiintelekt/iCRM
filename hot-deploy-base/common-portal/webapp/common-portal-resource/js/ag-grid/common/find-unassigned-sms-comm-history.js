$(function() {
	let unassignedsmsCommHistoryInstanceId= "UNASSIGNED_SMS_LIST";
	let gridInstanceSms  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	const formDataObject = {};
	formDataObject.gridInstanceId = "UNASSIGNED_SMS_LIST";
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	

	gridInstanceSms = prepareGridInstance(formDataObject);
	$("#unassigned-comm-history-grid").hide();
	let barId = $("#unassignedForm input[name=filterBy]").val();
	if(barId == "unassigned_sms"){
		$("#unassigned-comm-history-grid").hide();
		$("#unassigned-sms-grid").show();
		getUnassignedSmsComHistoryGridData();
	}
	$("#unassigned-sms-tag").click(function() {
		$("#unassigned-comm-history-grid").hide();
		$("#unassigned-sms-grid").show();
		getUnassignedSmsComHistoryGridData();
	});
	$("#unassigned_sms").click(function() {
		$("#unassigned-comm-history-grid").hide();
		$("#unassigned-sms-grid").show();
		getUnassignedSmsComHistoryGridData();
	});
	$("#fetch-previous").click(function () {
		fetchPrevious();
		getUnassignedSmsComHistoryGridData();  
	});
	$("#fetch-next").click(function () {
		fetchNext();
		getUnassignedSmsComHistoryGridData();  
	});
	$("#fetch-first").click(function () {
		fetchFirst();
		getUnassignedSmsComHistoryGridData();  
	});
	$("#fetch-last").click(function () {
		fetchLast();
		getUnassignedSmsComHistoryGridData();  
	});

	$('.goto-btn').keypress(function(event){
		var keycode = (event.keyCode ? event.keyCode : event.which);
		if(keycode == '13'){
			if(goto())    getUnassignedSmsComHistoryGridData();   
		}
	});
	$('#unassigned-sms-save-pref-btn').click(function(){
		saveGridPreference(gridInstanceSms, unassignedsmsCommHistoryInstanceId, userId);
	});
	$('#unassigned-sms-clear-filter-btn').click(function(){
		clearGridPreference(gridInstanceSms, unassignedsmsCommHistoryInstanceId, userId);
		if (gridInstanceSms) {
			gridInstanceSms.destroy();
		}
		gridInstanceSms = prepareGridInstance(formDataObject);
		if(gridInstanceSms){
			getUnassignedSmsComHistoryGridData();
		}
	});
	$('#sms-sub-filter-clear-btn').click(function(){
		gridInstanceSms.setFilterModel(null);
	});
	$("#sms-commHistory-list-export-btn").click(function() {
		gridInstanceSms.exportDataAsCsv();
	});
	function getUnassignedSmsComHistoryGridData(){
		gridInstanceSms.showLoadingOverlay();
		console.log("externalLoginKey"+externalLoginKey);
		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/getEmailActivities";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#unassignedForm, #limitForm_UNASSIGNED_SMS_LIST";
		callCtx.ajaxResponseKey = "list";

		setGridData(gridInstanceSms, callCtx);
	}
	if(gridInstanceSms){
		getUnassignedSmsComHistoryGridData();
	}
});


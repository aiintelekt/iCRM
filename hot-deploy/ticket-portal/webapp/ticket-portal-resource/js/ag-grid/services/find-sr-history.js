function dateComparator(date1, date2) {
	var date1Number = monthToComparableNumber(date1);
	var date2Number = monthToComparableNumber(date2);

	if (date1Number === null && date2Number === null) {
		return 0;
	}
	if (date1Number === null) {
		return -1;
	}
	if (date2Number === null) {
		return 1;
	}

	return date1Number - date2Number;
}

function monthToComparableNumber(date) {
	if (date === undefined || date === null || date.length <= 10) {
		return null;
	}

	var yearNumber = date.substring(6, 10);
	var monthNumber = date.substring(3, 5);
	var dayNumber = date.substring(0, 2);

	var hourNumber = date.substring(11, 13);
	var minuteNumber = date.substring(14, 16);
	var timePeriod = date.substring(17, 19);
	if("PM" === timePeriod){
		hourNumber = Number(12)+Number(4);
		if(Number(hourNumber) == Number(24)){
			hourNumber =00;
		}
	}

	var result = yearNumber * 10000 + monthNumber * 100 + dayNumber + hourNumber + minuteNumber;
	return result;
}

//fagReady("SR_HISTORY_LIST", function(el, api, colApi, gridApi){
//    $("#srhistory-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#srhistory-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#srhistory-clear-filter-btn").click(function () {
//    	gridApi.clearAllColumnFilters();
//    });
//    $("#srhistory-export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//    
//    loadSrHistoryGrid(gridApi, api, colApi);
//   
//});
//
//var findSrHistoryUrl= "";
//function loadSrHistoryGrid(gridApi, api, colApi) {
//	
//	if(findSrHistoryUrl == ""){
//		resetGridStatusBar();
//		findSrHistoryUrl = getGridDataFetchUrl("SR_HISTORY_LIST");
//	}
//	
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(findSrHistoryUrl != null && findSrHistoryUrl != "" && findSrHistoryUrl !="undefined"){
//		var formInput = $('#sr-history-search-form, #limitForm').serialize();
//		$.ajax({
//		  async: true,
//		  url:findSrHistoryUrl,
//		  type:"POST",
//		  data: JSON.parse(JSON.stringify(formInput)),
//		  success: function(data){
//			  gridApi.setRowData(data);
//			  setTimeout(() => colApi.autoSizeAllColumns(), 1000);
//		  }
//		});
//	}
//}
$(function() {
	let srHistoryInstanceId= "SR_HISTORY_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = srHistoryInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#srhistory-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, srHistoryInstanceId, userId);
	});
	$('#srhistory-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, srHistoryInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getSrHistoryGridData();
		}
	});
	$('#subFltrClearId-account').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#srhistory-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	
	function getSrHistoryGridData(){
		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("SR_HISTORY_LIST");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#sr-history-search-form, #limitForm_SR_HISTORY_LIST";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getSrHistoryGridData();
	}
});

//fagReady("LIST_TECHNICIAN_RATES", function(el, api, colApi, gridApi){

//$("#refresh-pref-btn").click(function () {
//gridApi.refreshUserPreferences();
//});
//$("#save-pref-btn").click(function () {
//gridApi.saveUserPreferences();
//});
//$("#clear-filter-btn").click(function () {
//try{
//gridApi.clearAllColumnFilters();
//}catch(e){
//}
//gridApi.refreshUserPreferences();
//});
//$("#sub-filter-clear-btn").click(function () {
//try{
//gridApi.clearAllColumnFilters();
//}catch(e){
//}
//});
//$("#export-btn").click(function () {
//gridApi.csvExport();
//});
//$("#remove-btn").click(function () {
//var selectedRows=api.getSelectedRows();
//if(selectedRows!= undefined && selectedRows != null&& selectedRows.length>0){
//var valid = true;
//for (i = 0; i < selectedRows.length; i++) {
//var data = selectedRows[i];
//var partyId = data.partyId;
//if("company" === partyId){
//valid = false;
//break;
//}
//}
//if(!valid){
//showAlert("error","Please don't Select Standard Rates to remove");
//}else{
//gridApi.removeSelected();
//setTimeout(() => { loadTechnicianRates(gridApi, api, colApi); }, 1000);
//}
//} else {
//showAlert("error","Please select atleast one record in the list");
//}
//});

//$("#main-search-btn").click(function () {
//loadTechnicianRates(gridApi, api);
//});
//loadTechnicianRates(gridApi, api);

//});

//var techniciansRatesUrl = "";
//function loadTechnicianRates(gridApi, api) {
//if(techniciansRatesUrl == ""){
//resetGridStatusBar();
//techniciansRatesUrl = getGridDataFetchUrl("LIST_TECHNICIAN_RATES");
//}
//var rowData =[];
//gridApi.setRowData(rowData);
//if(techniciansRatesUrl != null && techniciansRatesUrl != "" && techniciansRatesUrl !="undefined"){
//api.showLoadingOverlay();
//$.ajax({
//async: false,
//url:techniciansRatesUrl,
//type:"POST",
//data: JSON.parse(JSON.stringify($("#searchForm").serialize())),
//success: function(data){
//gridApi.setRowData(data);
//}
//});
//}
//}

$(function() {
	let listOfTechnicianRateInstanceId= "LIST_TECHNICIAN_RATES";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = listOfTechnicianRateInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	

	gridInstance = prepareGridInstance(formDataObject);

	$('#technician-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, listOfTechnicianRateInstanceId, userId);
	});

	$('#technician-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, listOfTechnicianRateInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getlistOfTechnicianRateGridData();
		}
	});
	$('#technician-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#main-search-btn").click(function () {
		getlistOfTechnicianRateGridData();
	});
	$("#technician-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$("#remove-btn").click(function () {
		var selectedRows = gridInstance.getSelectedRows();
		if (selectedRows !== undefined && selectedRows !== null && selectedRows.length > 0) {
			var valid = true;
			for (var i = 0; i < selectedRows.length; i++) {
				var data = selectedRows[i];
				var partyId = data.partyId;
				if ("company" === partyId) {
					valid = false;
					break;
				}
			}
			if (!valid) {
				showAlert("error", "Please don't Select Standard Rates to remove");
			} else {
				var partyIdVal = "";
				var fromDateVal = "";
				var currencyUomIdVal = "";
				var rateTypeIdVal = "";

				for (var i = 0; i < selectedRows.length; i++) {
					var data = selectedRows[i];
					partyIdVal += data.partyId;
					fromDateVal += data.fromDate;
					currencyUomIdVal += data.currencyUomId;
					rateTypeIdVal += data.rateTypeId;
				}
				var inputData = {"partyId": partyIdVal,"fromDate": fromDateVal,"currencyUomId": currencyUomIdVal,"rateTypeId": rateTypeIdVal};
				$.ajax({
					type: "POST",
					async: true,
					url: "/common-portal/control/removeTechnicianRates",
					data: inputData,
					success: function (data) {
						if (data) {
							getlistOfTechnicianRateGridData();
							showAlert("success", "Technician Rate removed successfully");
						}
					},
					error: function () {
						showAlert("error", "Error occurred in remove process");
					},
					complete: function () {
					}
				});
			}
		} else {
			showAlert("error", "Please select at least one record in the list");
		}
	});

	function getlistOfTechnicianRateGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("LIST_TECHNICIAN_RATES");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm";
		callCtx.ajaxResponseKey = "";

		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getlistOfTechnicianRateGridData();
	}
});
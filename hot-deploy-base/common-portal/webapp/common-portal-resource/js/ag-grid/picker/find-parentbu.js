fagReady("PARENTBU_LIST", function(el, api, colApi, gridApi){
	$("#parentBUPicker-refresh-pref-btn").click(function () {
		gridApi.refreshUserPreferences();
	});
	$("#parentBUPicker-save-pref-btn").click(function () {
		gridApi.saveUserPreferences();
	});
	$("#parentBUPicker-clear-filter-btn").click(function () {
		try{
			gridApi.clearAllColumnFilters();
		}catch(e){
		}
		gridApi.refreshUserPreferences();
	});
	$("#sub-filter-clear-btn").click(function () {
		try{
			gridApi.clearAllColumnFilters();
		}catch(e){
		}
	});
	$("#parentBUPicker-export-btn").click(function () {
		gridApi.csvExport();
	});

	$("#parentBUPicker-search-btn").click(function () {
		loadParentBUPickerGrid(api, gridApi);
	});

	$('#campaignPicker').on('shown.bs.modal', function (e) {
		api.sizeColumnsToFit();
	});
	//To submit the form to while click the enter button
	$("#searchForm").on("keypress", function (event) {
		var keyPressed = event.keyCode || event.which; 
		if (keyPressed === 13) { 
			loadParentBUPickerGrid(api,gridApi);
			event.preventDefault();
			return false; 
		} 
	});
	//loadParentBUPickerGrid(api, gridApi);
});

function loadParentBUPickerGrid(api, gridApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	api.showLoadingOverlay();
	var externalLoginKey=$("#searchForm #externalLoginKey").val();
	$.ajax({
		async: true,
		url:'/admin-portal/control/getParentBusinessUnitInUpdate',
		type:"POST",
		data: JSON.parse(JSON.stringify($("#searchForm").serialize())),
		success: function(data){
			gridApi.setRowData(data);
		}
	});
}

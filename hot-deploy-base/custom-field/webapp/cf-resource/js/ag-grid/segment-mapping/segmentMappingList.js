fagReady("SEGMENT_MAPPING", function(el, api, colApi, gridApi) {
	$("#segmentMapping-refresh-pref-btn").click(function () {
		gridApi.refreshUserPreferences();
	});
	$("#segmentMapping-save-pref-btn").click(function () {
		gridApi.saveUserPreferences();
	});
	$("#segmentMapping-clear-filter-btn").click(function () {
		try{
			gridApi.clearAllColumnFilters();
		}catch(e){
		}
		gridApi.refreshUserPreferences();
	});
	$("#segmentMapping-sub-filter-clear-btn").click(function () {
		try{
			gridApi.clearAllColumnFilters();
		}catch(e){
		}
	});
	$("#segmentMapping-export-btn").click(function () {
		gridApi.csvExport();
	});
	$("#segmentMapping-shipped-btn").click(function () {
		loadTierLevels(gridApi, api, colApi);
	});
	$(".filter-segmentMapping").click(function(event) {
		event.preventDefault(); 
	loadSegmentMapping(gridApi, api, colApi);
	});
	loadSegmentMapping(gridApi, api, colApi);
});

function loadSegmentMapping(gridApi, api, colApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	api.showLoadingOverlay();
	var formInput = $('#createSegmentMap').serialize();
	$.ajax({
		async: false,
		url:'/custom-field/control/segmentMappingList',
		type:"POST",
		data: JSON.parse(JSON.stringify(formInput)),
		success: function(result){
			gridApi.setRowData(result.data);
			setTimeout(() => colApi.autoSizeAllColumns(), 1000);
		}
	});
}
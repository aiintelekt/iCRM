fagReady("QUOTE_LIST", function(el, api, colApi, gridApi){
	$("#quote-refresh-pref-btn").click(function () {
		gridApi.refreshUserPreferences();
	});
	$("#quote-save-pref-btn").click(function () {
		gridApi.saveUserPreferences();
	});
	$("#quote-clear-filter-btn").click(function () {
		try{
			gridApi.clearAllColumnFilters();
		}catch(e){
		}
		gridApi.refreshUserPreferences();
	});
	$("#quote-sub-filter-clear-btn").click(function () {
		try{
			gridApi.clearAllColumnFilters();
		}catch(e){
		}
	});
	$("#quote-export-btn").click(function () {
		gridApi.csvExport();
	});

	$("#refresh-quote-btn").click(function () {
		loadQuoteGrid(gridApi, api, colApi);
	});

	$(".filter-quote").click(function(event) {
		event.preventDefault();
		$("#quote-grid-header-title").html(DOMPurify.sanitize($(this).attr("data-searchTypeLabel")));
		$("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));
		loadQuoteGrid(gridApi, api, colApi);
	});

	postLoadGrid(api, gridApi, colApi, "quotes", loadQuoteGrid);
});

function loadQuoteGrid(gridApi, api, colApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	api.showLoadingOverlay();
	$.ajax({
		async: false,
		url:'/common-portal/control/searchQuotes',
		type:"POST",
		data: JSON.parse(JSON.stringify($("#quote-search-form").serialize())),
		success: function(result){
			gridApi.setRowData(result.list);
			setTimeout(() => colApi.autoSizeAllColumns(), 1000);
		}
	});
}
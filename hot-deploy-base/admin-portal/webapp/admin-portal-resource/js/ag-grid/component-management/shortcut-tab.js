fagReady("TAB_SHORTCUTS", function(el, api, colApi, gridApi){
    $("#refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#clear-filter-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    	gridApi.refreshUserPreferences();
    });
    $("#export-btn").click(function () {
    	gridApi.csvExport();
    });
    $("#update-btn").click(function () {
    	gridApi.saveUpdates();
        setTimeout(() => {  loadShortcuts(gridApi, api); }, 1000);
    });
    $("#main-search-btn").click(function () {
    	loadShortcuts(gridApi, api);
    });
    loadShortcuts(gridApi, api);
});

var shortcutsUrl = "";

function loadShortcuts(gridApi, api) {
	if(shortcutsUrl == ""){
		shortcutsUrl = getGridDataFetchUrl("TAB_SHORTCUTS");
	}
		var rowData =[];
		gridApi.setRowData(rowData);
	if(shortcutsUrl !=null  && shortcutsUrl !="" && shortcutsUrl !="undefined"){
		api.showLoadingOverlay();
		var formInput = $('#searchForm, #limitForm').serialize();
		$.ajax({
		  async: false,
		  url:shortcutsUrl,
		  type:"POST",
		  data: JSON.parse(JSON.stringify(formInput)),
		  success: function(data){
			gridApi.setRowData(data);
			}
		});
	}
}

fagReady("COMPONENTS", function(el, api, colApi, gridApi){
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
        setTimeout(() => {  loadComponents(gridApi, api); }, 1000);
    })
    $("#main-search-btn").click(function () {
    	loadComponents(gridApi, api);
    });
    loadComponents(gridApi, api);
});

var componentsUrl = "";

function loadComponents(gridApi, api) {
	if(componentsUrl == ""){
		componentsUrl = getGridDataFetchUrl("COMPONENTS");
	}
		var rowData =[];
		gridApi.setRowData(rowData);
	if(componentsUrl !=null  && componentsUrl !="" && componentsUrl !="undefined"){
		api.showLoadingOverlay();
		var formInput = $('#searchForm, #limitForm').serialize();
		$.ajax({
		  async: false,
		  url:componentsUrl,
		  type:"POST",
		  data: JSON.parse(JSON.stringify(formInput)),
		  success: function(data){
		  gridApi.setRowData(data);
		  }
		});
	}
}

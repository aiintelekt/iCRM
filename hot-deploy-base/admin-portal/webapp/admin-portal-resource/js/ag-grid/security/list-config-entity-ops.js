fagReady("ENTITY_OPERATION", function(el, api, colApi, gridApi){
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
	$("#sub-filter-clear-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    });
    $("#export-btn").click(function () {
    	gridApi.csvExport();
    });

    $("#update-btn").click(function () {
    	gridApi.saveUpdates();
        setTimeout(() => {  loadEntityOps(gridApi, api, colApi); }, 1000);
    })

    $("#search-btn").click(function () {
    	loadEntityOps(gridApi, api, colApi);
    	//gridApi.refreshUserPreferences();
    });
    
    
    //To submit the form to while click the enter button
    $("#searchForm").on("keypress", function (event) {
        var keyPressed = event.keyCode || event.which; 
        if (keyPressed === 13) { 
        	loadEntityOps(gridApi, api, colApi); 
            event.preventDefault(); 
            return false; 
        } 
    }); 
    
    loadEntityOps(gridApi, api, colApi);
});

var findEntityOpsUrl= "";
function loadEntityOps(gridApi, api, colApi) {
	if(findEntityOpsUrl == ""){
		resetGridStatusBar();
		findEntityOpsUrl = getGridDataFetchUrl("ENTITY_OPERATION");
	}
	var rowData =[];
	gridApi.setRowData(rowData);
	if(findEntityOpsUrl != null && findEntityOpsUrl != "" && findEntityOpsUrl !="undefined"){
		api.showLoadingOverlay();
		var formInput = $('#searchForm, #limitForm').serialize();
		$.ajax({
		  async: true,
		  url:findEntityOpsUrl,
		  type:"POST",
		  data: JSON.parse(JSON.stringify(formInput)),
		  success: function(data){
			  gridApi.setRowData(data);
			  //data.list=[];
			  //paginateHandler(data);
			  setTimeout(() => colApi.autoSizeAllColumns(), 1000);
		  }
		});
	}
}

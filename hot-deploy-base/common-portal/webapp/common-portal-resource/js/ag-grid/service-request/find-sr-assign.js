fagReady("SR_ASSIGN", function(el, api, colApi, gridApi){
    $("#sr-assign-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#sr-assign-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#sr-assign-clear-filter-btn").click(function () {
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
    $("#sr-export-export-btn").click(function () {
    	gridApi.csvExport();
    });
    
    $("#main-search-btn").click(function () {
    	loadSrAssignGrid(gridApi, api, colApi);
    });
    
	$("#fetch-previous").click(function () {
    	fetchPrevious();
    	loadSrAssignGrid(gridApi, api, colApi);
    });
    $("#fetch-next").click(function () {
    	fetchNext();
    	loadSrAssignGrid(gridApi, api, colApi);
    });
    $("#fetch-first").click(function () {
    	fetchFirst();
    	loadSrAssignGrid(gridApi, api, colApi);
    });
    $("#fetch-last").click(function () {
    	fetchLast();
    	loadSrAssignGrid(gridApi, api, colApi);
    });
    
    $('#goto-page').keypress(function(event){
        var keycode = (event.keyCode ? event.keyCode : event.which);
        if(keycode == '13'){
        	if(goto())
        		loadSrAssignGrid(gridApi, api, colApi); 
        }
    });
    
    //To submit the form to while click the enter button
    $("#searchForm").on("keypress", function (event) {
        var keyPressed = event.keyCode || event.which; 
        if (keyPressed === 13) { 
        	loadSrAssignGrid(gridApi, api, colApi);
            event.preventDefault();
            return false; 
        } 
    });
    
    loadSrAssignGrid(gridApi, api, colApi);
    
});

function loadSrAssignGrid(gridApi, api, colApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	api.showLoadingOverlay();
	var fromPhoneNumber = $('#fromPhoneNumber').val();
	var externalLoginKey = $('#externalLoginKey').val();
	$.ajax({
	  async: true,
	  url:'/common-portal/control/searchSrDetails',
	  type:"POST",
	  data: {
			"fromPhoneNumber": fromPhoneNumber,"externalLoginKey": externalLoginKey
		},
	  success: function(data){
		  gridApi.setRowData(data.list);
		  data.list=[];
		  paginateHandler(data);
	  }
	});
}
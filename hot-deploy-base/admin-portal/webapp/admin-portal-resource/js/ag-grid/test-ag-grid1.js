
fagReady("GRID_TEST_1", function(el, api, colApi, gridApi){
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
        setTimeout(() => {  loadMainGrid1(gridApi); }, 1000);
    })

    $("#main-search-btn").click(function () {
        loadMainGrid1(gridApi);
    });
    $("#insert-btn").click(function () {
    	gridApi.insertNewRow()
        //gridOptions.api.applyTransaction({ add: newItems });
    	//fag1.logGridWrapperInfo();
    	//var apiWrapper = api.gridOptionsWrapper;
    	//var gridOptions = apiWrapper.gridOptions;
    	//console.log("gridOptions---------->"+gridOptions);
        //var newItems = [createNewRowData()];
        //gridOptions.api.applyTransaction({ add: newItems });
    })
    $("#remove-btn").click(function () {
        //removeMainGrid(fag1, api);
    	gridApi.removeSelected();
        setTimeout(() => {  loadMainGrid1(gridApi); }, 1000);
        
    });
    
	loadMainGrid1(gridApi);
});



function loadMainGrid1(grid1) {
	var rowData =[];
	grid1.setRowData(rowData);
	$.ajax({
	  async: false,
	  url:'getPerson',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#searchForm").serialize())),
	  success: function(data){
			// console.log("data: ", data)
		  grid1.setRowData(data);
	  }
	});
}

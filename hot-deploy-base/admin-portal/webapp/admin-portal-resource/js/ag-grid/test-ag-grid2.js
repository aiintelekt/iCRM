
fagReady("GRID_TEST_2", function(el, api, colApi, gridApi2) {
	//gridApi2 = fag2;
	$("#refresh-pref-btn1").click(function () {
		gridApi2.refreshUserPreferences();
    });
    $("#save-pref-btn1").click(function () {
    	gridApi2.saveUserPreferences();
    });
    $("#clear-filter-btn1").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    	gridApi.refreshUserPreferences();
    });
    $("#export-btn1").click(function () {
    	gridApi2.csvExport();
    });

    $("#update-btn1").click(function () {
    	gridApi2.saveUpdates();
        setTimeout(() => {  loadMainGrid2(gridApi2); }, 1000);
    })

    $("#main-search-btn1").click(function () {
        loadMainGrid2(gridApi2);
    });
    $("#insert-btn1").click(function () {
    	gridApi2.insertNewRow();
    })
    $("#remove-btn1").click(function () {
    	gridApi2.removeSelected();
        setTimeout(() => {  loadMainGrid2(gridApi2); }, 1000);
        
    });
    
    loadMainGrid2(gridApi2);

});
function loadMainGrid2(grid2) {
	var rowData =[];
	grid2.setRowData(rowData);
	$.ajax({
	  url:'/admin-portal/control/getParty',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#searchForm2").serialize())),
	  success: function(data){
		  grid2.setRowData(data)
	  }
	})
}
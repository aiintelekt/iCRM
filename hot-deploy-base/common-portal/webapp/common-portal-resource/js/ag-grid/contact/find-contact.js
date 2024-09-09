fagReady("CONTACTS", function(el, api, colApi, gridApi){
    $("#contact-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#contact-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#contact-clear-filter-btn").click(function () {
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
    $("#contact-export-btn").click(function () {
    	gridApi.csvExport();
    });

    $("#contact-update-btn").click(function () {
    	gridApi.saveUpdates();
        setTimeout(() => {  loadContactsGrid(gridApi); }, 1000);
    })

    $("#contacts-search-btn").click(function () {
    	loadContactsGrid(gridApi);
    });
    $("#contact-insert-btn").click(function () {
    	gridApi.insertNewRow()
    })
    $("#contact-remove-btn").click(function () {
    	
        //removeMainGrid(fag1, api);
    	gridApi.removeSelected();
        setTimeout(() => {  loadContactsGrid(gridApi); }, 1000);
        
    });
    
    $("a[href='#contacts']").on('show.bs.tab', function(e) {
    	api.sizeColumnsToFit();
    });
    
    loadContactsGrid(gridApi);
});

function loadContactsGrid(gridApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	$.ajax({
	  async: true,
	  url:'/common-portal/control/searchContacts',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#searchContactsForm").serialize())),
	  success: function(data){
		  gridApi.setRowData(data);
	  }
	});
}

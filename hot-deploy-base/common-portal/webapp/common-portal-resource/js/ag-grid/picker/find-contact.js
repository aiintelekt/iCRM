//added for contact picker
fagReady("CONTACT_PICKER_LIST", function(el, api, colApi, gridApi){
    $("#contactPicker-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#contactPicker-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#contactPicker-clear-filter-btn").click(function () {
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
    $("#contactPicker-export-btn").click(function () {
    	gridApi.csvExport();
    });

    $("#partyPicker-search-btn").click(function () {
    	loadPartyPickerGrid(gridApi);
    });
    
    $('#partyPicker').on('shown.bs.modal', function (e) {
    	api.sizeColumnsToFit();
	});
    
    //loadPartyPickerGrid(gridApi);
});

function loadPartyPickerGrid(gridApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	$.ajax({
	  async: true,
	  url:'/contact-portal/control/searchContacts',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#findPartyForm").serialize())),
	  success: function(data){
		  gridApi.setRowData(data);
	  }
	});
}

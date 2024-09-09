
fagReady("PICKER_PAYMENT_LIST", function(el, api, colApi, gridApi){
	
    $("#paymentPicker-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#paymentPicker-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#paymentPicker-clear-filter-btn").click(function () {
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
    $("#paymentPicker-export-btn").click(function () {
    	gridApi.csvExport();
    });

    $("#paymentPicker-search-btn").click(function () {
    	loadPartyPickerGrid(gridApi);
    });
    
    $('#paymentPicker').on('shown.bs.modal', function (e) {
    	loadPartyPickerGrid(gridApi);
    	api.sizeColumnsToFit();
	});
});

function loadPartyPickerGrid(gridApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	$.ajax({
	  async: true,
	  url:'/common-portal/control/searchPayments',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#findPaymentForm").serialize())),
	  success: function(data){
		  gridApi.setRowData(data);
	  }
	});
}

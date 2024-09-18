fagReady("INVOICE_ITEM_LIST_SR", function(el, api, colApi, gridApi){
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
    
    $("#remove-btn").click(function () {
    		var statusId = $("#statusId").val();
		    if (statusId == "INVOICE_CANCELLED") {
		    	showAlert("error", "Can not update Invoice Item when status is not in In Progress");
		    	return false;
		    }
		    else{
		    	var selectedData = api.getSelectedRows();
    		if (selectedData.length > 0) {
    			
    		    var invoiceItemSeqId = "";
    		    var invoiceId = "";
    		    for (i = 0; i < selectedData.length; i++) {
    		    	var data = selectedData[i];
    		    	invoiceItemSeqId = data.invoiceItemSeqId;
    		    	invoiceId = data.invoiceId;
    		    }
    		    
    		    var inputData = {"invoiceItemSeqId": invoiceItemSeqId,"invoiceId": invoiceId};
    		    $.ajax({
    				type : "POST",
    				url : "/accounting-portal/control/removeInvoiceItemAction",
    				async : true,
    				data : inputData,
    				success : function(result) {
    						showAlert ("success", "Successfully Removed InvoiceItem ");
    						loadInvoiceItems(gridApi, api)
    				},
    				error : function() {
    					console.log('Error occured');
    					showAlert("error", "Error occured!");
    				},
    				complete : function() {
    				}
    			});
    			
    		} else {
    			showAlert("error", "Please select atleast one row to be removed!");
    		}
		  }
    });
    
    $("#main-search-btn").click(function () {
    	loadInvoiceItems(gridApi, api);
    });
    loadInvoiceItems(gridApi, api);
});

var listInvoicesUrl = "";
function loadInvoiceItems(gridApi, api) {
	if(listInvoicesUrl == ""){
		resetGridStatusBar();
		listInvoicesUrl = getGridDataFetchUrl("INVOICE_ITEM_LIST_SR");
	}
	var rowData =[];
	gridApi.setRowData(rowData);
	if(listInvoicesUrl != null && listInvoicesUrl != "" && listInvoicesUrl !="undefined"){
		api.showLoadingOverlay();
		$.ajax({
		  async: false,
		  url : "/common-portal/control/findInvoicesItemList",
		  type:"POST",
		 data: JSON.parse(JSON.stringify($("#searchForm").serialize())),
		  success: function(data){
			  gridApi.setRowData(data);
		  }
		});
	}
}
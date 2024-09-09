
$(function() {
	let invoiceListInstanceId= "INVOICE_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = invoiceListInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	//formDataObject.dataFetchCall = getRecentTransRowData;
	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	function getInvoiceGridData(){
		var listInvoicesUrl = "/common-portal/control/searchRmsInvoices";
		if(listInvoicesUrl == ""){
			resetGridStatusBar();
			listInvoicesUrl = getGridDataFetchUrl("INVOICE_LIST");
		}
		const callCtx = {};
		callCtx.ajaxUrl = listInvoicesUrl;
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#findInvoices";
		callCtx.ajaxResponseKey = "data";
		
		setGridData(gridInstance, callCtx);
	}
	
	
	if(gridInstance){
		getInvoiceGridData();
	}
	/*
	function getRecentTransRowData() {
		var result = getRecentTransRowDataResponse(function(agdata) {
			gridInstance.setGridOption('rowData', agdata);
	    });
	} */
	
	$('#invoice-save-pref').click(function(){
		saveGridPreference(gridInstance, invoiceListInstanceId, userId);
	});
	
	$('#invoice-clear-pref').click(function(){
		clearGridPreference(gridInstance, invoiceListInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getInvoiceGridData();
		}
	});
	$('#invoice-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#invoice-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	 $("#main-search-btn").click(function () {
    	 getInvoiceGridData();
    });
    
    $("#refresh-invoice-btn").click(function () {
    	getInvoiceGridData();
    });
    
    $("#send-ereceipt-btn").click(function () {
    	var selectedData = gridInstance.getSelectedRows();
		if (selectedData.length > 0) {
			
			console.log(selectedData);
			
		    var selectedItemIds = "";
		    for (i = 0; i < selectedData.length; i++) {
		    	var data = selectedData[i];
		    	selectedItemIds += data.transactionNumber+",";
		    }
		    selectedItemIds = selectedItemIds.substring(0, selectedItemIds.length - 1);
		    $('#send-ereceipt-form input[name=transactionNumber]').val(selectedItemIds);
			
			$.ajax({
				async : false,
				url : '/common-portal/control/sendEreceipt',
				type : "POST",
				data : JSON.parse(JSON.stringify($("#send-ereceipt-form").serialize())),
				success : function(data) {
					if (data.code == 200) {
						showAlert ("success", "Successfully send ereceipt..");
						getInvoiceGridData();
					} else {
						showAlert ("error", data.message);
					}
				}
			});
			
		} else {
			showAlert("error", "Please select at least one order to send Ereceipt!");
		}
    });

});

/*
fagReady("INVOICE_LIST", function(el, api, colApi, gridApi){
	$("#invoice-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#invoice-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#invoice-clear-filter-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    	gridApi.refreshUserPreferences();
    });
	$("#invoice-sub-filter-clear-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    });
    $("#invoice-export-btn").click(function () {
    	gridApi.csvExport();
    });
    
    $("#main-search-btn").click(function () {
    	 loadInvoices(gridApi, api, colApi);
    });
    
    $("#refresh-invoice-btn").click(function () {
    	loadInvoices(gridApi, api, colApi);
    });
    
    $("#send-ereceipt-btn").click(function () {
    	var selectedData = api.getSelectedRows();
		if (selectedData.length > 0) {
			
			console.log(selectedData);
			
		    var selectedItemIds = "";
		    for (i = 0; i < selectedData.length; i++) {
		    	var data = selectedData[i];
		    	selectedItemIds += data.transactionNumber+",";
		    }
		    selectedItemIds = selectedItemIds.substring(0, selectedItemIds.length - 1);
		    $('#send-ereceipt-form input[name=transactionNumber]').val(selectedItemIds);
			
			$.ajax({
				async : false,
				url : '/common-portal/control/sendEreceipt',
				type : "POST",
				data : JSON.parse(JSON.stringify($("#send-ereceipt-form").serialize())),
				success : function(data) {
					if (data.code == 200) {
						showAlert ("success", "Successfully send ereceipt..");
						loadInvoices(gridApi, api, colApi);
					} else {
						showAlert ("error", data.message);
					}
				}
			});
			
		} else {
			showAlert("error", "Please select at least one order to send Ereceipt!");
		}
    });
    
    postLoadGrid(api, gridApi, colApi, "a-invoice", loadInvoices);
    postLoadGrid(api, gridApi, colApi, "c-invoice", loadInvoices);
});

var listInvoicesUrl = "/common-portal/control/searchRmsInvoices";
function loadInvoices(gridApi, api, colApi) {
	if(listInvoicesUrl == ""){
		resetGridStatusBar();
		listInvoicesUrl = getGridDataFetchUrl("INVOICE_LIST");
	}
	var rowData =[];
	gridApi.setRowData(rowData);
	if(listInvoicesUrl != null && listInvoicesUrl != "" && listInvoicesUrl !="undefined"){
		api.showLoadingOverlay();
		$.ajax({
		  async: false,
		  url:listInvoicesUrl,
		  type:"POST",
		  data: JSON.parse(JSON.stringify($("#findInvoices").serialize())),
		  success: function(data){
			  gridApi.setRowData(data.data);
		  }
		});
	}
}
*/
//popup to view invoice receipt
function enableInvoiceReceiptPopup(transactionNumber){
	if(transactionNumber != null && transactionNumber != "" && transactionNumber !="undefined"){
		$.ajax({
			async: false,
			url:'/common-portal/control/getReceiptHtmlData',
			type:"POST",
			data: {'orderId':transactionNumber},
			success: function(data){
				if(data.data)
					$("#invoiceReceiptHtmlContent").html(DOMPurify.sanitize(data.data));
				else
					$("#invoiceReceiptHtmlContent").html("No content found");
				$('#invoice-receipt-popup').modal("show");
			}
		});
	}
}
function viewSkuDescription(description){
	$('#show-inv-des-modal_des_title').html("Sku Info");
	$('#show-inv-des-modal_des_value').html(DOMPurify.sanitize(base64.decode(description)));
	$('#show-inv-des-modal').modal("show");
}

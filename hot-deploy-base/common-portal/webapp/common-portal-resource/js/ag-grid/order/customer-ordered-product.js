$(function() {
	let customerOrderedInstanceId= $('#customerOrderedInstanceId').val();
	let gridInstance  = "";

	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = customerOrderedInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	//formDataObject.dataFetchCall = getCustBoughtRowData;
	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	function getCustOrderedGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/customer-portal/control/searchOrders";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#ordered-product-form";
		callCtx.ajaxResponseKey = "data";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getCustOrderedGridData();
	}
	$('#cust-ordered-save-pref').click(function(){
		saveGridPreference(gridInstance, customerOrderedInstanceId, userId);
	});
	$("#ordered-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#cust-ordered-clear-pref').click(function(){
		clearGridPreference(gridInstance, customerOrderedInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getCustOrderedGridData();
		}
	});
	$('#cust-ordered-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});
	
});



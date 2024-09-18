$(function() {
	let posOrderInstanceId= "FIND_POS_ORDER_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	console.log("userId"+userId);
	console.log("externalLoginKey"+externalLoginKey);
	const formDataObject = {};
	formDataObject.gridInstanceId = posOrderInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#pos-order-save-filter-btn').click(function(){
		saveGridPreference(gridInstance, posOrderInstanceId, userId);
	});
	$('#pos-order-clear-pref-btn').click(function(){
		clearGridPreference(gridInstance, posOrderInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getPosOrderGridData();
		}
	});
    $('#pos-order-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
    $("#pos-order-by-day-export-btn").click(function() {
    	gridInstance.exportDataAsCsv();
	});
    $( "#main-search-btn" ).click(function() {
		getPosOrderGridData();
	});
	$("#searchPosOrderForm").on("keypress", function (event) {
		var keyPressed = event.keyCode || event.which; 
		if (keyPressed === 13) { 
			getPosOrderGridData();
			event.preventDefault(); 
			return false; 
		} 
	});
	function getPosOrderGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "searchPosOrder";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchPosOrderForm, #limitForm_FIND_POS_ORDER_LIST";
		callCtx.ajaxResponseKey = "data";
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getPosOrderGridData();
	}
});
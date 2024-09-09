//fagReady("PC_PROD", function(el, api, colApi, gridApi){
//    $("#refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#clear-filter-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    	gridApi.refreshUserPreferences();
//    });
//	$("#sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//
//    $("#update-btn").click(function () {
//    	gridApi.saveUpdates();
//        setTimeout(() => {  loadProduct(gridApi, api); }, 1000);
//    })
//
//    $("#main-search-btn").click(function () {
//    	loadProduct(gridApi, api);
//    });
//    $("#searchForm").on("keypress", function (event) {
//        var keyPressed = event.keyCode || event.which; 
//        if (keyPressed === 13) { 
//        	event.preventDefault(); 
//        	loadProduct(gridApi, api); 
//            return false; 
//        } 
//    });
//    loadProduct(gridApi, api);
//});
//
//var productUrl = "";
//function loadProduct(gridApi, api) {
//	if(productUrl == ""){
//		productUrl = getGridDataFetchUrl("PC_PROD");
//	}
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(productUrl != null && productUrl != "" && productUrl !="undefined"){
//		api.showLoadingOverlay();
//		var formInput = $('#searchForm, #limitForm').serialize();
//		$.ajax({
//		  async: false,
//		  url:productUrl,
//		  type:"POST",
//		  data: JSON.parse(JSON.stringify(formInput)),
//		  success: function(data){
//			  gridApi.setRowData(data);
//			  //data.list=[];
//			  //paginateHandler(data);
//		  }
//		});
//	}
//}

$(function() {
	let pcInstanceId= "PC_PROD";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = pcInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#pc-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, pcInstanceId, userId);
	});
	$("#pc-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#pc-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, pcInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getProductGridData();
		}
	});
	$('#pc-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#main-search-btn").click(function () {
		getProductGridData();
	});
	$("#searchForm").on("keypress", function (event) {
	    var keyPressed = event.keyCode || event.which; 
	    if (keyPressed === 13) { 
	    	event.preventDefault(); 
	    	getProductGridData();
	        return false; 
	    } 
	});
	function getProductGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("PC_PROD");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm, #limitForm_PC_PROD";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getProductGridData();
	}
});



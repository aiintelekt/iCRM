//fagReady("PC_PRO_CATALOG", function(el, api, colApi, gridApi){
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
//        setTimeout(() => {  loadProductCatalog(gridApi, api); }, 1000);
//    })
//
//    $("#main-search-btn").click(function () {
//    	loadProductCatalog(gridApi, api);
//    });
//    $("#searchForm").on("keypress", function (event) {
//        var keyPressed = event.keyCode || event.which; 
//        if (keyPressed === 13) { 
//        	event.preventDefault(); 
//        	loadProductCatalog(gridApi, api);
//            return false; 
//        } 
//    });
//    loadProductCatalog(gridApi, api);
//});
//
//var prodCatalogUrl = "";
//function loadProductCatalog(gridApi, api) {
//	if(prodCatalogUrl == ""){
//		prodCatalogUrl = getGridDataFetchUrl("PC_PRO_CATALOG");
//	}
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(prodCatalogUrl != null && prodCatalogUrl != "" && prodCatalogUrl !="undefined"){
//		api.showLoadingOverlay();
//		var formInput = $('#searchForm, #limitForm').serialize();
//		$.ajax({
//		  async: false,
//		  url:prodCatalogUrl,
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
	let productCatalogInstanceId= "PC_PRO_CATALOG";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = productCatalogInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#product-catalogs-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, productCatalogInstanceId, userId);
	});
	$("#product-catalogs-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#product-catalogs-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, productCatalogInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getProductCatalogGridData();
		}
	});
	$('#product-catalogs-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#main-search-btn").click(function () {
		getProductCatalogGridData();
	});
	$("#searchForm").on("keypress", function (event) {
	    var keyPressed = event.keyCode || event.which; 
	    if (keyPressed === 13) { 
	    	event.preventDefault(); 
			getProductCatalogGridData();
	        return false; 
	    } 
	});
	function getProductCatalogGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("PC_PRO_CATALOG");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm, #limitForm_PC_PRO_CATALOG";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getProductCatalogGridData();
	}
});

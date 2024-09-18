//fagReady("PC_PRO_CATEGORY", function(el, api, colApi, gridApi){
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
//        setTimeout(() => {  loadProductCategory(gridApi, api); }, 1000);
//    })
//
//    $("#main-search-btn").click(function () {
//    	loadProductCategory(gridApi, api);
//    });
//    $("#searchForm").on("keypress", function (event) {
//        var keyPressed = event.keyCode || event.which; 
//        if (keyPressed === 13) { 
//        	event.preventDefault(); 
//        	loadProductCategory(gridApi, api); 
//            return false; 
//        } 
//    });
//    loadProductCategory(gridApi, api);
//});
//
//var prodCategoryUrl = "";
//function loadProductCategory(gridApi, api) {
//	if(prodCategoryUrl == ""){
//		prodCategoryUrl = getGridDataFetchUrl("PC_PRO_CATEGORY");
//	}
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(prodCategoryUrl != null && prodCategoryUrl != "" && prodCategoryUrl !="undefined"){
//		api.showLoadingOverlay();
//		var formInput = $('#searchForm, #limitForm').serialize();
//		$.ajax({
//		  async: false,
//		  url:prodCategoryUrl,
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
	let productCategoryInstanceId= "PC_PRO_CATEGORY";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = productCategoryInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#product-category-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, productCategoryInstanceId, userId);
	});
	$("#product-category-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#product-category-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, productCategoryInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getProductCategoryGridData();
		}
	});
	$('#product-category-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#main-search-btn").click(function () {
		getProductCategoryGridData();
	});
	$("#searchForm").on("keypress", function (event) {
	    var keyPressed = event.keyCode || event.which; 
	    if (keyPressed === 13) { 
	    	event.preventDefault(); 
	    	getProductCategoryGridData();
	        return false; 
	    } 
	});
	function getProductCategoryGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("PC_PRO_CATEGORY");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm, #limitForm_PC_PRO_CATEGORY";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getProductCategoryGridData();
	}
});





//fagReady("PC_PRO_SUB_CATEGORY", function(el, api, colApi, gridApi){
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
//    	loadProductSubCategory(gridApi, api);
//    });
//    $("#searchForm").on("keypress", function (event) {
//        var keyPressed = event.keyCode || event.which; 
//        if (keyPressed === 13) { 
//        	event.preventDefault(); 
//        	loadProductSubCategory(gridApi, api); 
//            return false; 
//        } 
//    });
//    loadProductSubCategory(gridApi, api);
//});
//
//var prodSubCategoryUrl = "";
//function loadProductSubCategory(gridApi, api) {
//	if(prodSubCategoryUrl == ""){
//		prodSubCategoryUrl = getGridDataFetchUrl("PC_PRO_SUB_CATEGORY");
//	}
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(prodSubCategoryUrl != null && prodSubCategoryUrl != "" && prodSubCategoryUrl !="undefined"){
//		api.showLoadingOverlay();
//		var formInput = $('#searchForm, #limitForm').serialize();
//		$.ajax({
//		  async: false,
//		  url:prodSubCategoryUrl,
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
	let productSubCategoryInstanceId= "PC_PRO_SUB_CATEGORY";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = productSubCategoryInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#product-sub-category-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, productSubCategoryInstanceId, userId);
	});
	$("#product-sub-category-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#product-sub-category-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, productSubCategoryInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getProductSubCategoryGridData();
		}
	});
	$('#product-sub-category-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#main-search-btn").click(function () {
		getProductSubCategoryGridData();
	});
	$("#searchForm").on("keypress", function (event) {
	    var keyPressed = event.keyCode || event.which; 
	    if (keyPressed === 13) { 
	    	event.preventDefault(); 
	    	getProductSubCategoryGridData();
	        return false; 
	    } 
	});
	function getProductSubCategoryGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("PC_PRO_SUB_CATEGORY");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm, #limitForm_PC_PRO_SUB_CATEGORY";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getProductSubCategoryGridData();
	}
});


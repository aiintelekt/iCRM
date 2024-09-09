
//fagReady("LIST_PRODUCT_STORES", function(el, api, colApi, gridApi){
//	
//	$("#refresh-pref-btn").click(function () {
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
//
//	$("#sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//    $("#remove-btn").click(function () {
//    	var selectedRows=api.getSelectedRows();
//    	if(selectedRows!= undefined && selectedRows != null&& selectedRows.length>0){
//    		gridApi.removeSelected();
//    		setTimeout(() => { loadTechnicianRates(gridApi, api, colApi); }, 1000);
//    	} else {
//            showAlert("error","Please select atleast one record in the list");
//        }
//    });
//    
//    $("#main-search-btn").click(function () {
//    	loadTechnicianRates(gridApi, api);
//    });
//    //To submit the form to while click the enter button
//    $("#searchForm").on("keypress", function (event) {
//        var keyPressed = event.keyCode || event.which; 
//        if (keyPressed === 13) { 
//        	loadTechnicianRates(gridApi, api);
//            event.preventDefault();
//            return false; 
//        } 
//    });
//    loadTechnicianRates(gridApi, api);
//	
//});
//
//var techniciansRatesUrl = "";
//function loadTechnicianRates(gridApi, api) {
//	if(techniciansRatesUrl == ""){
//		resetGridStatusBar();
//		techniciansRatesUrl = getGridDataFetchUrl("LIST_PRODUCT_STORES");
//	}
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(techniciansRatesUrl != null && techniciansRatesUrl != "" && techniciansRatesUrl !="undefined"){
//		api.showLoadingOverlay();
//		$.ajax({
//		  async: false,
//		  url:techniciansRatesUrl,
//		  type:"POST",
//		  data: JSON.parse(JSON.stringify($("#searchForm").serialize())),
//		  success: function(data){
//			  gridApi.setRowData(data);
//		  }
//		});
//	}
//}


$(function() {
	let technicianInstanceId= "LIST_PRODUCT_STORES";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = technicianInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#save-pref-btn').click(function(){
		saveGridPreference(gridInstance, technicianInstanceId, userId);
	});
	$('#clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, technicianInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getTechnicianGridData();
		}
	});
//	  $("#remove-btn").click(function () {
//		var selectedRows=gridInstance.getSelectedRows();
//		if(selectedRows!= undefined && selectedRows != null&& selectedRows.length>0){
//			gridInstance.removeSelected();
//			setTimeout(() => { 		getTechnicianGridData(); }, 1000);
//		} else {
//	        showAlert("error","Please select atleast one record in the list");
//	    }
//	});
	
	$("#main-search-btn").click(function () {
		getTechnicianGridData();
	});
	$("#technician-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	//To submit the form to while click the enter button
	$("#searchForm").on("keypress", function (event) {
	    var keyPressed = event.keyCode || event.which; 
	    if (keyPressed === 13) { 
			getTechnicianGridData();
	        event.preventDefault();
	        return false; 
	    } 
	});
	function getTechnicianGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("LIST_PRODUCT_STORES");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getTechnicianGridData();
	}
});
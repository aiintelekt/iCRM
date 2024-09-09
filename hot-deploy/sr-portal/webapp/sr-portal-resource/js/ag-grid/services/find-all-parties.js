//fagReady("ALL_PARTIES", function(el, api, colApi, gridApi){
//	
//    $("#sr-party-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#sr-party-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#sr-party-clear-filter-btn").click(function () {
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
//    $("#sr-party-export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//
//    $("#sr-party-main-search-btn").click(function () {
//    	 loadAllParties(gridApi, api, colApi);
//    });
//  
//    $("#add-party-to-sr-btn").click(function () {
//    	var selectedRows=api.getSelectedRows();
//    	if(selectedRows!= undefined && selectedRows != null&& selectedRows.length>0){
//    		 var rows = JSON.stringify(selectedRows);
//    		 $("#selecteddRows").val(rows);
//    		 $("#createSrPartyAssocForm").submit();
//    	} else {
//    		$('div[data-notify="container"]').css('z-index', '1000000');
//    		setTimeout(showAlert("error","Please select atleast one record in the list"), 10000);
//        }
//    });
//    
//    loadAllParties(gridApi, api, colApi);
//});
//
//var getAllPartiesUrl = "";
//
//function loadAllParties(gridApi, api, colApi) {
//	
//	if(getAllPartiesUrl == ""){
//		//resetGridStatusBar();
//		getAllPartiesUrl = getGridDataFetchUrl("ALL_PARTIES");
//	}
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(getAllPartiesUrl != null && getAllPartiesUrl != "" && getAllPartiesUrl !="undefined"){
//		api.showLoadingOverlay();
//		var formInput = $('#searchAssocForm').serialize();
//		$.ajax({
//		  async: true,
//		  url:getAllPartiesUrl,
//		  type:"POST",
//		  data: JSON.parse(JSON.stringify(formInput)),
//		  success: function(data){
//			  gridApi.setRowData(data.list);
//			  data.list=[];
//		  }
//		});
//	 setTimeout(() => colApi.autoSizeAllColumns(), 1000);
//	}
//}


$(function() {
	let allPartiesInstanceId= "ALL_PARTIES";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = allPartiesInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#sr-party-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, allPartiesInstanceId, userId);
	});
	$('#sr-party-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, allPartiesInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getAllPartiesGridData();
		}
	});
	$('#sr-party-sub-clear-filter-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#sr-party-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#sr-party-main-search-btn').click(function(){
		getAllPartiesGridData();
	});
    $("#add-party-to-sr-btn").click(function () {
    	var selectedRows=gridInstance.getSelectedRows();
    	if(selectedRows!= undefined && selectedRows != null&& selectedRows.length>0){
    		 var rows = JSON.stringify(selectedRows);
    		 $("#selecteddRows").val(rows);
    		 $("#createSrPartyAssocForm").submit();
    	} else {
    		$('div[data-notify="container"]').css('z-index', '1000000');
    		setTimeout(showAlert("error","Please select atleast one record in the list"), 10000);
        }
    });
	function getAllPartiesGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("ALL_PARTIES");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchAssocForm";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getAllPartiesGridData();
	}
});
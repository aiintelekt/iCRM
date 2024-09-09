//fagReady("CONTACTS_GRID", function(el, api, colApi, gridApi){
//	
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
//  /*  $("#contact-update-btn").click(function () {
//    	gridApi.saveUpdates();
//        setTimeout(() => {  loadContactsGrid(gridApi); }, 1000);
//    })*/
//
//    $("#main-search-btn").click(function () {
//    	 loadContactsGrid(gridApi, api, colApi);
//    });
//   $("#contact-insert-btn").click(function () {
//    	gridApi.insertNewRow();
//    })
//    $("#contact-remove-btn").click(function () {
//        //removeMainGrid(fag1, api);
//    	gridApi.removeSelected();
//        setTimeout(() => {  loadContactsGrid(gridApi); }, 1000);
//        
//    });
//    
//      $(".add-contact-party").on('shown.bs.modal', function(e) {
//    	loadContactsGrid(gridApi, api, colApi);
//    	//setTimeout(() => colApi.autoSizeAllColumns(), 1000);
//    });
//  //To submit the form to while click the enter button
//    $("#searchContactsForm").on("keypress", function (event) {
//        var keyPressed = event.keyCode || event.which; 
//        if (keyPressed === 13) { 
//        	loadContactsGrid(gridApi, api, colApi);
//            event.preventDefault();
//            return false; 
//        } 
//    });
//});
//
//var getDataTableContacts = "";
//
//function loadContactsGrid(gridApi, api, colApi) {
//	
//	if(getDataTableContacts == ""){
//		//resetGridStatusBar();
//		getDataTableContacts = getGridDataFetchUrl("CONTACTS_GRID");
//	}
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(getDataTableContacts != null && getDataTableContacts != "" && getDataTableContacts !="undefined"){
//		api.showLoadingOverlay();
//		$.ajax({
//		  async: true,
//		  //url:'/common-portal/control/getDataTableContacts',
//		  url: getDataTableContacts,
//		  type:"POST",
//		  data: JSON.parse(JSON.stringify($("#searchContactsForm").serialize())),
//		  success: function(data){
//				//alert("data======"+JSON.stringify(data));
//	
//			  gridApi.setRowData(data.data);
//			
//		  }
//		});
//	 setTimeout(() => colApi.autoSizeAllColumns(), 1000);
//	}
//}
//    
    
$(function() {
	let contactInstanceId= "CONTACTS_GRID";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = contactInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	

	gridInstance = prepareGridInstance(formDataObject);

	$('#contact-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, contactInstanceId, userId);
	});
	$('#contact-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, contactInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getContactsGridData();
		}
	});
	$("#main-search-btn").click(function () {
		getContactsGridData();
	});
	$('#contact-sub-filter-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#searchContactsForm").on("keypress", function (event) {
		var keyPressed = event.keyCode || event.which; 
		if (keyPressed === 13) { 
			getContactsGridData();
			event.preventDefault();
			return false; 
		} 
	});

	function getContactsGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("CONTACTS_GRID");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchContactsForm";
		callCtx.ajaxResponseKey = "data";

		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getContactsGridData();
	}
});

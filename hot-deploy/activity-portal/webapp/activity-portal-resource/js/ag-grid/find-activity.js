//fagReady("ACTIVITY_LIST", function(el, api, colApi, gridApi){
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
//    $("#fetch-previous").click(function () {
//    	fetchPrevious();
//    	loadMainGrid(gridApi, api, colApi);
//    });
//    $("#fetch-next").click(function () {
//    	fetchNext();
//    	loadMainGrid(gridApi, api, colApi);
//    });
//    $("#fetch-first").click(function () {
//    	fetchFirst();
//    	loadMainGrid(gridApi, api, colApi);
//    });
//    $("#fetch-last").click(function () {
//    	fetchLast();
//    	loadMainGrid(gridApi, api, colApi);
//    });
//    $('#goto-page').keypress(function(event){
//        var keycode = (event.keyCode ? event.keyCode : event.which);
//        if(keycode == '13'){
//        	if(goto()) {
//        		loadMainGrid(gridApi, api, colApi);
//        	}
//        }
//    });
//    $("#activity-search-form").on("keypress", function (event) {
//        var keyPressed = event.keyCode || event.which; 
//        if (keyPressed === 13) { 
//        	event.preventDefault(); 
//        	loadSRGrid(gridApi, api, colApi); 
//            return false; 
//        } 
//    }); 
//
//    $("#main-search-btn").click(function () {
//    	loadMainGrid(gridApi, api, colApi);
//    });
//    
//    $(".filter-activity").click(function(event) {
//        event.preventDefault(); 
//        
//        $("#activity-grid-header-title").html($(this).attr("data-searchTypeLabel"));
//        $("#activity-search-form input[name=searchType]").val($(this).attr("data-searchType"));
//        
//        loadMainGrid(gridApi, api, colApi);
//    });
//    
//    loadMainGrid(gridApi, api, colApi);
//});
//
//function loadMainGrid(gridApi, api, colApi) {
//	console.log('calling find method');
//	
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	
//	$.ajax({
//	  async: true,
//	  url:'/activity-portal/control/searchActivitys',
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify($("#activity-search-form, #limitForm").serialize())),
//	  success: function(data){
//		  gridApi.setRowData(data.list);
//		  data.list=[];
//		  paginateHandler(data);
//	  }
//	});
//}


$(function() {
	let activityInstanceId= "ACTIVITY_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = activityInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#activity-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, activityInstanceId, userId);
	});
	$('#activity-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, activityInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getActivityGridData();
		}
	});
	 $('#activity-sub-filter-clear-btn').click(function(){
			gridInstance.setFilterModel(null);
		});
	  $("#fetch-previous").click(function () {
		fetchPrevious();
		getActivityGridData();
	});
	$("#fetch-next").click(function () {
		fetchNext();
		getActivityGridData();
	});
	$("#fetch-first").click(function () {
		fetchFirst();
		getActivityGridData();
	});
	$("#fetch-last").click(function () {
		fetchLast();
		getActivityGridData();
	});
	$('#goto-page').keypress(function(event){
	    var keycode = (event.keyCode ? event.keyCode : event.which);
	    if(keycode == '13'){
	    	if(goto()) {
	    		getActivityGridData();
	    	}
	    }
	});
	$("#activity-search-form").on("keypress", function (event) {
	    var keyPressed = event.keyCode || event.which; 
	    if (keyPressed === 13) { 
	    	event.preventDefault(); 
			getActivityGridData();
	        return false; 
	    } 
	}); 
	
	$("#main-search-btn").click(function () {
		getActivityGridData();
	});
	$("#activity-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$(".filter-activity").click(function(event) {
	    event.preventDefault(); 
	    
	    $("#activity-grid-header-title").html(DOMPurify.sanitize($(this).attr("data-searchTypeLabel")));
	    $("#activity-search-form input[name=searchType]").val($(this).attr("data-searchType"));
	    
		getActivityGridData();
	});
	function getActivityGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/activity-portal/control/searchActivitys";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#activity-search-form, #limitForm_ACTIVITY_LIST";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getActivityGridData();
	}
});
//fagReady("ACTIVITY_GEN_LIST", function(el, api, colApi, gridApi){
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
//    	gridApi.refreshUserPreferences();s
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
//        	loadMainGrid(gridApi, api, colApi); 
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
//    $("input[name=activityCat]").change(function(event) {
//    	console.log("activityCat  "+this.value);
//    	let activityCat = this.value;
//    	if (activityCat=='PROG') {
//    		$("#activity-search-form input[name=isChecklistActivity]").val('Y');
//    	} else if (activityCat=='OTHER') {
//    		$("#activity-search-form input[name=isChecklistActivity]").val("N");
//    	}
//    	loadMainGrid(gridApi, api, colApi);
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
	let accountGenListInstanceId= "ACTIVITY_GEN_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = accountGenListInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#save-pref-btn').click(function(){
		saveGridPreference(gridInstance, accountGenListInstanceId, userId);
	});
	$('#clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, accountGenListInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
	    	getAccountGenListGridData();
		}
	});
    $("#fetch-previous").click(function () {
    	fetchPrevious();
    	getAccountGenListGridData();
    });
    $("#fetch-next").click(function () {
    	fetchNext();
    	getAccountGenListGridData();
    });
    $("#fetch-first").click(function () {
    	fetchFirst();
    	getAccountGenListGridData();
    });
    $("#fetch-last").click(function () {
    	fetchLast();
    	getAccountGenListGridData();
    });
    $('#goto-page').keypress(function(event){
        var keycode = (event.keyCode ? event.keyCode : event.which);
        if(keycode == '13'){
        	if(goto()) {
            	getAccountGenListGridData();
        	}
        }
    });
    $("#activity-search-form").on("keypress", function (event) {
        var keyPressed = event.keyCode || event.which; 
        if (keyPressed === 13) { 
        	event.preventDefault(); 
        	getAccountGenListGridData();
            return false; 
        } 
    }); 
    $("#activity-gen-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
    $("#main-search-btn").click(function () {
    	getAccountGenListGridData();
    });
    
    $(".filter-activity").click(function(event) {
        event.preventDefault(); 
        
        $("#activity-grid-header-title").html(DOMPurify.sanitize($(this).attr("data-searchTypeLabel")));
        $("#activity-search-form input[name=searchType]").val($(this).attr("data-searchType"));
        
    	getAccountGenListGridData();
    });
    
    $("input[name=activityCat]").change(function(event) {
    	console.log("activityCat  "+this.value);
    	let activityCat = this.value;
    	if (activityCat=='PROG') {
    		$("#activity-search-form input[name=isChecklistActivity]").val('Y');
    	} else if (activityCat=='OTHER') {
    		$("#activity-search-form input[name=isChecklistActivity]").val("N");
    	}
    	getAccountGenListGridData();
    });
    $('#activity-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	function getAccountGenListGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/activity-portal/control/searchActivitys";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#activity-search-form, #limitForm_ACTIVITY_GEN_LIST";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getAccountGenListGridData();
	}
});
function subjectParams(params) {
	if (params.data.salesOpportunityId != null && params.data.salesOpportunityId != "") {
		return '<a target="_blank" href="viewActivity?workEffortId=' + params.data.iaNumber + '&seqId=' + params.data.iaNumber + '&partyId=' + params.data.partyId + '&salesOpportunityId=' + params.data.salesOpportunityId + '&entity=WorkEffortCallSummary" target="">' + params.data.subject + '</a>'; 
	} else if (params.data.workEffortTypeId == 'TASK' && params.data.domainEntityType == "REBATE" && params.data.approvalCategoryId == "APVL_CAT_REBATE") {
		return '<a target="_blank" href="/rebate-portal/control/viewRebate?agreementId=' + params.data.domainEntityId + '&externalLoginKey=' + params.data.externalLoginKey + '">' + params.data.subject + '</a>'; 
	} else {
		return '<a target="_blank" href="viewActivity?workEffortId=' + params.data.iaNumber + '&seqId=' + params.data.iaNumber + '&partyId=' + params.data.partyId + '&entity=WorkEffortCallSummary" target="">' + params.data.subject + '</a>';
	}
} 
function iaNumberParams(params) { 
	return '<a target="_blank" href="/activity-portal/control/viewActivity?workEffortId=' + params.data.iaNumber + '&seqId=' + params.data.iaNumber + '?yId=' + params.data.partyId + '&entity=WorkEffortCallSummary&externalLoginKey=' + params.data.externalLoginKey + '">' + params.data.iaNumber + '</a>'; 
}
function sourceIdParams(params) { 
	return '<a target="_blank" href="' + params.data.sourceIdLink + '">' + params.data.domainEntityId + '</a>'; 
}

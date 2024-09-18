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
//        setTimeout(() => {  loadMainGrid(gridApi, api, colApi); }, 1000);
//    })
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
// 	$("#dashboard-filter").click(function(event) {
//        event.preventDefault(); 
//        //alert("fdsafasdf--->"+$("#searchForm input[name=filterBy]").val());
//        loadMainGrid(gridApi, api, colApi);
//    });
//
//	$('input[type=radio][name="filterType"]').on('change', function(event) {
//		event.preventDefault(); 
//	    $("#searchForm input[type=hidden][name='filterType']").val(this.value);
//	    $("#filterBy").val("");
//    	$(".box-animate").not(this).removeClass("selected-element-b");
//		$("#overdue").addClass( "selected-element-b");
//		loadActivityDashboardCount();
//		load_dynamic_data('overdue');
//    	
//	    //loadMainGrid(gridApi, api, colApi);
//	});
//	
//	$('#location').on('change', function() {
//	    $("#searchForm #location").val(this.value);
//	    $("#filterBy").val("");
//    	$(".box-animate").not(this).removeClass("selected-element-b");
//    	$("#overdue").addClass( "selected-element-b");
//    	loadActivityDashboardCount();
//		load_dynamic_data('overdue');
//	    //loadSRDashboardData(gridApi, api, colApi);
//    	//$("#searchForm").submit();
//	});
//	
//	$("#fetch-previous").click(function () {
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
//    
//    $('#goto-page').keypress(function(event){
//        var keycode = (event.keyCode ? event.keyCode : event.which);
//        if(keycode == '13'){
//        	if(goto())
//        		loadMainGrid(gridApi, api, colApi); 
//        }
//    });
//    
//    loadMainGrid(gridApi, api, colApi);
//});


//function loadMainGrid(gridApi, api, colApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//
//	api.showLoadingOverlay();
//	var formInput = $('#searchForm, #limitForm').serialize();
//	$.ajax({
//	  async: true,
//	  url:"getActivityDashboardData",
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify(formInput)),
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
	$("#main-search-btn").click(function () {
		getAccountGenListGridData(); 
    });
	$("#activity-gen-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
    $(".filter-activity").click(function(event) {
        event.preventDefault(); 
        
        $("#activity-grid-header-title").html(DOMPurify.sanitize($(this).attr("data-searchTypeLabel")));
        $("#activity-search-form input[name=searchType]").val($(this).attr("data-searchType"));
        
		getAccountGenListGridData(); 
    });
 	$("#dashboard-filter").click(function(event) {
        event.preventDefault(); 
		getAccountGenListGridData(); 
    });

	$('input[type=radio][name="filterType"]').on('change', function(event) {
		event.preventDefault(); 
	    $("#searchForm input[type=hidden][name='filterType']").val(this.value);
	    $("#filterBy").val("");
    	$(".box-animate").not(this).removeClass("selected-element-b");
		$("#overdue").addClass( "selected-element-b");
		loadActivityDashboardCount();
		load_dynamic_data('overdue');
	});
	
	$('#location').on('change', function() {
	    $("#searchForm #location").val(this.value);
	    $("#filterBy").val("");
    	$(".box-animate").not(this).removeClass("selected-element-b");
    	$("#overdue").addClass( "selected-element-b");
    	loadActivityDashboardCount();
		load_dynamic_data('overdue');
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
        	if(goto())
        		getAccountGenListGridData(); 
        }
    });
    $('#activity-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	function getAccountGenListGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "getActivityDashboardData";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm, #limitForm_ACTIVITY_GEN_LIST";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getAccountGenListGridData();
	}
});
function loadActivityDashboardCount() {

	var formInput = $('#searchForm').serialize();
	$.ajax({
	  async: false,
	  url:"getActivityDashboardCountList",
	  type:"POST",
	  data: JSON.parse(JSON.stringify(formInput)),
	  success: function(data){
		  var dataList = data.list;
		  if(dataList != null && dataList != "" && dataList != "undefined"){
			  for (i = 0; i < dataList.length; i++) {
		    	var data = dataList[i];
		    	console.log(data.barId+","+data.count);
			    if("overdue"== data.barId){
			    	$("#overdue_Id").html(sanitize(data.count));
			    } else if("due_today" == data.barId){
			    	$("#due-today_Id").html(sanitize(data.count));
			    } else if("due_tomorrow" == data.barId){
			    	$("#due-tomorrow_Id").html(sanitize(data.count));
			    } else if("pending_approvals" == data.barId){
			    	$("#pending-approvals_Id").html(sanitize(data.count));
			    } else if("pending_reviews" == data.barId){
			    	$("#pending-reviews_Id").html(sanitize(data.count));
			    }
			  }
		  }
	  }
	});
	
}

function load_dynamic_data(id){
	$("#filterBy").val(id);
	$("#dashboard-filter").click();
}

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

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
//	$("#mark-complete-btn").click(function(event) {
//		var selectedData = api.getSelectedRows();
//		if (selectedData.length > 0) {
//			console.log(selectedData);
//			
//		    let selectedIds = "";
//		    let workEffortTypeId = "";
//		    let workEffortId = "";
//		    let isSchedulingRequired = "";
//		    let currentStatusId = "";
//		    let isScheduleTask = "";
//		    let workEffortPurposeTypeId = "";
//		    for (i = 0; i < selectedData.length; i++) {
//		    	var data = selectedData[i];
//		    	selectedIds += data.iaNumber+",";
//		    	workEffortTypeId = data.workEffortTypeId;
//		    	workEffortId = data.iaNumber;
//		    	isSchedulingRequired = data.isSchedulingRequired;
//		    	currentStatusId = data.currentStatusId;
//		    	isScheduleTask = data.isScheduleTask;
//		    	workEffortPurposeTypeId = data.workEffortPurposeTypeId;
//		    }
//		    selectedIds = selectedIds.substring(0, selectedIds.length - 1);
//		    console.log('workEffortId: '+workEffortId+', workEffortTypeId: '+workEffortTypeId+', currentStatusId: '+currentStatusId+', isSchedulingRequired: '+isSchedulingRequired+', workEffortPurposeTypeId: '+workEffortPurposeTypeId);
//		    
//		    if (workEffortPurposeTypeId!='10020') { // 10020 = Coordinator Task
//		    	showAlert("error", "Please select only Coordinator task for 'Mark Complete'");
//		    } else {
//		    	if (isSchedulingRequired=='N' && (currentStatusId=="IA_OPEN" || currentStatusId=="IA_MSCHEDULED" || currentStatusId=="IA_MIN_PROGRESS") && (isScheduleTask=="N")) {
//			    	var valid = true; 
//				    if (workEffortTypeId == "TASK" && isSchedulingRequired == "Y") {
//				    	$.ajax({
//				            type: "POST",
//				            url: "/common-portal/control/getActivityTimeEntryCount",
//				            data: {
//				                "workEffortId": workEffortId,
//				                "externalLoginKey": $("#externalLoginKey").val()
//				            },
//				            async: false,
//				            success: function (data) {
//				                if (data.code == 200) {
//				                    if (data.timeEntryCount === 0) {
//				                        if (!confirm('Time Entry Missing. Proceed with Ending Activity?')) {
//				                            valid = false;
//				                        }
//				                    }
//				                }
//				            }
//				        }); 
//				    }
//				       
//				    if (valid) {
//				    	console.log("valid to mark complete");	
//				    	showAlert("info", "Mark complete in-progress... Please wait..");
//				    	$.ajax({
//				            url: 'closedServiceActivityDetails',
//				            data: {
//				                "workEffortId": workEffortId,
//				                "currentStatusId": currentStatusId
//				            },
//				            type: "post",
//				            success: function (data) {
//				                showAlert("success", " Activity Closed Successfully");
//				                setTimeout(() => loadMainGrid(gridApi, api, colApi), 1000);
//				            },
//				            error: function (data) {
//				                console.log("dataerror====", data);
//				                return data;
//				            }
//				        });
//				    }
//			    } else {
//			    	showAlert("error", "Not eligible for 'Mark Complete'");
//			    }
//		    }
//		} else {
//			showAlert("error", "Please select one row for 'Mark Complete'");
//		}
//    });
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
//    $("#searchForm").on("keypress", function (event) {
//        var keyPressed = event.keyCode || event.which; 
//        if (keyPressed === 13) { 
//        	loadMainGrid(gridApi, api, colApi);
//            event.preventDefault();
//            return false; 
//        } 
//    });
//    loadMainGrid(gridApi, api, colApi);
//});
//
//function loadActivityDashboardCount() {
//
//	var formInput = $('#searchForm').serialize();
//	$.ajax({
//	  async: false,
//	  url:"getActivityDashboardCountList",
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify(formInput)),
//	  success: function(data){
//		  var dataList = data.list;
//		  if(dataList != null && dataList != "" && dataList != "undefined"){
//			  for (i = 0; i < dataList.length; i++) {
//		    	var data = dataList[i];
//		    	console.log(data.barId+","+data.count);
//			    if("overdue"== data.barId){
//			    	$("#overdue_Id").html(data.count);
//			    } else if("due_today" == data.barId){
//			    	$("#due-today_Id").html(data.count);
//			    } else if("due_tomorrow" == data.barId){
//			    	$("#due-tomorrow_Id").html(data.count);
//			    }
//			  }
//		  }
//	  }
//	});
//	
//}
//
//function load_dynamic_data(id){
//	$("#filterBy").val(id);
//	$("#dashboard-filter").click();
//}

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
	$("#activity-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
    $("#main-search-btn").click(function () {
		getActivityGridData();
    });
    $('#activity-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
    $(".filter-activity").click(function(event) {
        event.preventDefault(); 
        
        $("#activity-grid-header-title").html(DOMPurify.sanitize($(this).attr("data-searchTypeLabel")));
        $("#activity-search-form input[name=searchType]").val(DOMPurify.sanitize($(this).attr("data-searchType")));
        
		getActivityGridData();
    });
 	$("#dashboard-filter").click(function(event) {
        event.preventDefault(); 
        //alert("fdsafasdf--->"+$("#searchForm input[name=filterBy]").val());
		getActivityGridData();
    });

	$('input[type=radio][name="filterType"]').on('change', function(event) {
		event.preventDefault(); 
	    $("#searchForm input[type=hidden][name='filterType']").val(this.value);
	    $("#filterBy").val("");
    	$(".box-animate").not(this).removeClass("selected-element-b");
		$("#overdue").addClass( "selected-element-b");
		loadActivityDashboardCount();
		load_dynamic_data('overdue');
    	
	    //loadMainGrid(gridApi, api, colApi);
	});
	
	$('#location').on('change', function() {
	    $("#searchForm #location").val(this.value);
	    $("#filterBy").val("");
    	$(".box-animate").not(this).removeClass("selected-element-b");
    	$("#overdue").addClass( "selected-element-b");
    	loadActivityDashboardCount();
		load_dynamic_data('overdue');
	    //loadSRDashboardData(gridApi, api, colApi);
    	//$("#searchForm").submit();
	});
	
	$("#mark-complete-btn").click(function(event) {
		var selectedData = gridInstance.getSelectedRows();
		if (selectedData.length > 0) {
			console.log(selectedData);
			
		    let selectedIds = "";
		    let workEffortTypeId = "";
		    let workEffortId = "";
		    let isSchedulingRequired = "";
		    let currentStatusId = "";
		    let isScheduleTask = "";
		    let workEffortPurposeTypeId = "";
		    for (i = 0; i < selectedData.length; i++) {
		    	var data = selectedData[i];
		    	selectedIds += data.iaNumber+",";
		    	workEffortTypeId = data.workEffortTypeId;
		    	workEffortId = data.iaNumber;
		    	isSchedulingRequired = data.isSchedulingRequired;
		    	currentStatusId = data.currentStatusId;
		    	isScheduleTask = data.isScheduleTask;
		    	workEffortPurposeTypeId = data.workEffortPurposeTypeId;
		    }
		    selectedIds = selectedIds.substring(0, selectedIds.length - 1);
		    console.log('workEffortId: '+workEffortId+', workEffortTypeId: '+workEffortTypeId+', currentStatusId: '+currentStatusId+', isSchedulingRequired: '+isSchedulingRequired+', workEffortPurposeTypeId: '+workEffortPurposeTypeId);
		    
		    if (workEffortPurposeTypeId!='10020') { // 10020 = Coordinator Task
		    	showAlert("error", "Please select only Coordinator task for 'Mark Complete'");
		    } else {
		    	if (isSchedulingRequired=='N' && (currentStatusId=="IA_OPEN" || currentStatusId=="IA_MSCHEDULED" || currentStatusId=="IA_MIN_PROGRESS") && (isScheduleTask=="N")) {
			    	var valid = true; 
				    if (workEffortTypeId == "TASK" && isSchedulingRequired == "Y") {
				    	$.ajax({
				            type: "POST",
				            url: "/common-portal/control/getActivityTimeEntryCount",
				            data: {
				                "workEffortId": workEffortId,
				                "externalLoginKey": $("#externalLoginKey").val()
				            },
				            async: false,
				            success: function (data) {
				                if (data.code == 200) {
				                    if (data.timeEntryCount === 0) {
				                        if (!confirm('Time Entry Missing. Proceed with Ending Activity?')) {
				                            valid = false;
				                        }
				                    }
				                }
				            }
				        }); 
				    }
				       
				    if (valid) {
				    	console.log("valid to mark complete");	
				    	showAlert("info", "Mark complete in-progress... Please wait..");
				    	$.ajax({
				            url: 'closedServiceActivityDetails',
				            data: {
				                "workEffortId": workEffortId,
				                "currentStatusId": currentStatusId
				            },
				            type: "post",
				            success: function (data) {
				                showAlert("success", " Activity Closed Successfully");
				                setTimeout(() => 			getActivityGridData(), 1000);
				            },
				            error: function (data) {
				                console.log("dataerror====", data);
				                return data;
				            }
				        });
				    }
			    } else {
			    	showAlert("error", "Not eligible for 'Mark Complete'");
			    }
		    }
		} else {
			showAlert("error", "Please select one row for 'Mark Complete'");
		}
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
        	if(goto())
    			getActivityGridData();
        }
    });

    $("#searchForm").on("keypress", function (event) {
        var keyPressed = event.keyCode || event.which; 
        if (keyPressed === 13) { 
			getActivityGridData();
            event.preventDefault();
            return false; 
        } 
    });
	function getActivityGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "getActivityDashboardData";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm, #limitForm_ACTIVITY_LIST";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getActivityGridData();
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
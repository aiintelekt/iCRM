function dateComparator(date1, date2) {
	var date1Number = monthToComparableNumber(date1);
	var date2Number = monthToComparableNumber(date2);

	if (date1Number === null && date2Number === null) {
		return 0;
	}
	if (date1Number === null) {
		return -1;
	}
	if (date2Number === null) {
		return 1;
	}

	return date1Number - date2Number;
}

function monthToComparableNumber(date) {
	if (date === undefined || date === null || date.length <= 10) {
		return null;
	}

	var yearNumber = date.substring(6, 10);
	var monthNumber = date.substring(3, 5);
	var dayNumber = date.substring(0, 2);

	var hourNumber = date.substring(11, 13);
	var minuteNumber = date.substring(14, 16);
	var timePeriod = date.substring(17, 19);
	if("PM" === timePeriod){
		hourNumber = Number(12)+Number(4);
		if(Number(hourNumber) == Number(24)){
			hourNumber =00;
		}
	}
	//console.log("date--->"+date);
	//console.log("hourNumber--->"+hourNumber+"---minuteNumber--->"+minuteNumber);

	var result = yearNumber * 10000 + monthNumber * 100 + dayNumber + hourNumber + minuteNumber;
	return result;
}

//fagReady("SR_BASE_LIST_GEN", function(el, api, colApi, gridApi){
//    $("#sr-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#sr-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#sr-clear-filter-btn").click(function () {
//    	//loadSRGrid(gridApi, api, colApi);
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    	$("#filterBy").val("");
//    	$(".box-animate").not(this).removeClass("selected-element-b");
//    	loadSRDashboardData(gridApi, api, colApi);
//    	gridApi.refreshUserPreferences();
//    });
//	$("#sr-sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#sr-export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//
//    $("#sr-update-btn").click(function () {
//    	gridApi.saveUpdates();
//        setTimeout(() => {  loadSRDashboardData(gridApi, api, colApi); }, 1000);
//    })

//    $("#sr-search-btn").click(function () {
//    	//loadSRGrid(gridApi, api, colApi);
//    	loadSRDashboardData(gridApi, api, colApi);
//    	//gridApi.refreshUserPreferences();
//    });
//    
//    $("#fetch-previous").click(function () {
//    	fetchPrevious();
//    	//loadSRGrid(gridApi, api);
//    	loadSRDashboardData(gridApi, api, colApi);
//    });
//    $("#fetch-next").click(function () {
//    	fetchNext();
//    	//loadSRGrid(gridApi, api);
//    	loadSRDashboardData(gridApi, api, colApi);
//    });
//    $("#fetch-first").click(function () {
//    	fetchFirst();
//    	//loadSRGrid(gridApi, api);
//    	loadSRDashboardData(gridApi, api, colApi);
//    });
//    $("#fetch-last").click(function () {
//    	fetchLast();
//    	//loadSRGrid(gridApi, api);
//    	loadSRDashboardData(gridApi, api, colApi);
//    });
//    
//    $('#goto-page').keypress(function(event){
//        var keycode = (event.keyCode ? event.keyCode : event.which);
//        if(keycode == '13'){
//        	if(goto()) loadSRDashboardData(gridApi, api, colApi);
//        		//loadSRGrid(gridApi, api); 
//        }
//    });
//    
//    $(".filter-sr").click(function(event) {
//        event.preventDefault(); 
//        //$("#sr-grid-header-title").html($(this).attr("data-searchTypeLabel"));
//        //alert($(this).attr("data-searchTypeLabel"));
//        //$("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));
//        
//        //loadSRGrid(gridApi, api, colApi);
//        loadSRDashboardData(gridApi, api, colApi);
//    });
//    $("#dashboard-filter").click(function(event) {
//        event.preventDefault(); 
//        //alert("fdsafasdf--->"+$("#searchForm input[name=filterBy]").val());
//        loadSRDashboardData(gridApi, api, colApi);
//    });
//    
//    //To submit the form to while click the enter button
//    $("#searchForm").on("keypress", function (event) {
//        var keyPressed = event.keyCode || event.which; 
//        if (keyPressed === 13) { 
//        	//loadSRGrid(gridApi, api, colApi);
//        	loadSRDashboardData(gridApi, api, colApi);
//            event.preventDefault(); 
//            return false; 
//        } 
//    }); 
//    
//    $("#sr-mapit-btn").click(function () {
//    	var selectedData = api.getSelectedRows();
//		if (selectedData.length > 0) {
//			console.log(selectedData);
//			
//		    var selectedSrIds = "";
//		    for (i = 0; i < selectedData.length; i++) {
//		    	var data = selectedData[i];
//		    	selectedSrIds += data.custRequestId+",";
//		    }
//		    selectedSrIds = selectedSrIds.substring(0, selectedSrIds.length - 1);
//		    var url = "/uiadv-portal/control/findSrMap?selectedSrIds="+base64.encode(selectedSrIds)+"&externalLoginKey="+ $("#searchForm input[name=externalLoginKey]").val();
//		    window.open(url, '_blank');
//		} else {
//			showAlert("error", "Please select atleast one row for 'Map It'");
//		}
//    });
//    
//    $('input[type=radio][name="filterType"]').on('change', function() {
//	    $("#searchForm input[type=hidden][name='filterType']").val(this.value);
//	    $("#filterBy").val("");
//    	$(".box-animate").not(this).removeClass("selected-element-b");
//		$("#sr-open").addClass( "selected-element-b");
//		loadSRDashboardCount();
//		load_dynamic_data('sr-open');
//	    //loadSRDashboardData(gridApi, api, colApi);
//    	//$("#searchForm").submit();
//	});
//	
//    //loadSRGrid(gridApi, api, colApi);
//    loadSRDashboardData(gridApi, api, colApi);
//});

function loadSRDashboardCount() {

	var formInput = $('#searchForm').serialize();
	$.ajax({
	  async: true,
	  url:"getSrDashboardCountList",
	  type:"POST",
	  data: JSON.parse(JSON.stringify(formInput)),
	  success: function(data){
		  var dataList = data.list;
		  if(dataList != null && dataList != "" && dataList != "undefined"){
			  for (i = 0; i < dataList.length; i++) {
			    	var data = dataList[i];
			    	console.log(data.barId+","+data.count);
			    if("Open"== data.barId){
			    	$("#sr-open_Id").html(sanitize(data.count));
			    } else if("Open_not_scheduled" == data.barId){
			    	$("#sr-inprogress_Id").html(sanitize(data.count));
			    } else if("completed_last_week" == data.barId){
			    	$("#sr-over-due_Id").html(sanitize(data.count));
			    } else if("idle" == data.barId){
			    	$("#sr-at-risk_Id").html(sanitize(data.count));
			    } else if("New" == data.barId){
			    	$("#sr-pending_Id").html(sanitize(data.count));
			    } else if("completed_last_month" == data.barId){
			    	$("#sr-cancelled_Id").html(sanitize(data.count));
			    } else if("high_priority" == data.barId){
			    	$("#sr-high-priority_Id").html(sanitize(data.count));
			    } else if("pending_approvals" == data.barId){
			    	$("#pending-approvals_Id").html(sanitize(data.count));
			    } else if("pending_reviews" == data.barId){
			    	$("#pending-reviews_Id").html(sanitize(data.count));
			    } else if("Scheduled" == data.barId){
			    	$("#sr-scheduled_Id").html(sanitize(data.count));
			    } else if("Completed" == data.barId){
			    	$("#sr-closed_Id").html(sanitize(data.count));
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

//function loadSRDashboardData(gridApi, api, colApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//
//	api.showLoadingOverlay();
//	var formInput = $('#searchForm, #limitForm').serialize();
//	$.ajax({
//	  async: true,
//	  url:"getSrDashboardList",
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
	let srBaseListInstanceId= "SR_BASE_LIST_GEN";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = srBaseListInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#sr-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, srBaseListInstanceId, userId);
	});
	$('#sr-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, srBaseListInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			$("#filterBy").val("");
	    	$(".box-animate").not(this).removeClass("selected-element-b");
	    	getSRListGridData();
		}
	});
	$('#sr-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	
    $("#sr-search-btn").click(function () {
    	getSRListGridData();
    });
	$("#sr-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
    $("#fetch-previous").click(function () {
    	fetchPrevious();
    	getSRListGridData();
    });
    $("#fetch-next").click(function () {
    	fetchNext();
    	getSRListGridData();
    });
    $("#fetch-first").click(function () {
    	fetchFirst();
    	getSRListGridData();
    });
    $("#fetch-last").click(function () {
    	fetchLast();
    	getSRListGridData();
    });
    
    $('#goto-page').keypress(function(event){
        var keycode = (event.keyCode ? event.keyCode : event.which);
        if(keycode == '13'){
        	if(goto())  getSRListGridData();
        }
    });
    
    $(".filter-sr").click(function(event) {
        event.preventDefault(); 
        getSRListGridData();
    });
    $("#dashboard-filter").click(function(event) {
        event.preventDefault(); 
        getSRListGridData();
    });
    
    //To submit the form to while click the enter button
    $("#searchForm").on("keypress", function (event) {
        var keyPressed = event.keyCode || event.which; 
        if (keyPressed === 13) { 
        	getSRListGridData();
            event.preventDefault(); 
            return false; 
        } 
    }); 
    
    $("#sr-mapit-btn").click(function () {
    	var selectedData = gridInstance.getSelectedRows();
		if (selectedData.length > 0) {
			console.log(selectedData);
			
		    var selectedSrIds = "";
		    for (i = 0; i < selectedData.length; i++) {
		    	var data = selectedData[i];
		    	selectedSrIds += data.custRequestId+",";
		    }
		    selectedSrIds = selectedSrIds.substring(0, selectedSrIds.length - 1);
		    var url = "/uiadv-portal/control/findSrMap?selectedSrIds="+base64.encode(selectedSrIds)+"&externalLoginKey="+ $("#searchForm input[name=externalLoginKey]").val();
		    window.open(url, '_blank');
		} else {
			showAlert("error", "Please select atleast one row for 'Map It'");
		}
    });
    
    $('input[type=radio][name="filterType"]').on('change', function() {
	    $("#searchForm input[type=hidden][name='filterType']").val(this.value);
	    $("#filterBy").val("");
    	$(".box-animate").not(this).removeClass("selected-element-b");
		$("#sr-open").addClass( "selected-element-b");
		loadSRDashboardCount();
		load_dynamic_data('sr-open');
	});
	
	function getSRListGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "getSrDashboardList";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm, #limitForm_SR_BASE_LIST_GEN";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getSRListGridData();
	}
});

function srNumber(params){ 
	return `<a target="_black" href="viewServiceRequest?srNumber=${params.data.srNumber}"?yId=${params.data.partyId}">${params.data.srNumber}</a>`;
	}

	function srName(params){ 
		return `<a target="_black" href="viewServiceRequest?srNumber=${params.data.srNumber}"?yId=${params.data.partyId}">${params.data.srName}</a>`;
	}

	function formatDate(data) {
	    var value = data.value;
	    if (value instanceof Date && !isNaN(value)) {
	        return value ? new Date(value).toLocaleDateString() : '';
	    } else {
	        return value ? value : '';
	    }
	}

	function openDate(data) {
	    return formatDate(data);
	}

	function dueDate(data) {
	    return formatDate(data);
	}

	function createdOn(data) {
	    return formatDate(data);
	}

	function modifiedOn(data) {
	    return formatDate(data);
	}

	function dateClosed(data) {
	    return formatDate(data);
	}


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

//fagReady("SERVICE_REQUEST_LIST", function(el, api, colApi, gridApi){
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
//
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
//    $('#srLocation').on('change', function() {
//	    $("#searchForm #srLocation").val(this.value);
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

//function loadSRDashboardCount() {
//
//	var formInput = $('#searchForm').serialize();
//	$.ajax({
//	  async: true,
//	  url:"getSrDashboardCountList",
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify(formInput)),
//	  success: function(data){
//		  var dataList = data.list;
//		  if(dataList != null && dataList != "" && dataList != "undefined"){
//			  for (i = 0; i < dataList.length; i++) {
//			    	var data = dataList[i];
//			    	console.log(data.barId+","+data.count);
//			    if("Open"== data.barId){
//			    	$("#sr-open_Id").html(data.count);
//			    } else if("Open_not_scheduled" == data.barId){
//			    	$("#sr-inprogress_Id").html(data.count);
//			    } else if("completed_last_week" == data.barId){
//			    	$("#sr-over-due_Id").html(data.count);
//			    } else if("idle" == data.barId){
//			    	$("#sr-at-risk_Id").html(data.count);
//			    } else if("New" == data.barId){
//			    	$("#sr-pending_Id").html(data.count);
//			    } else if("completed_last_month" == data.barId){
//			    	$("#sr-cancelled_Id").html(data.count);
//			    } else if("high_priority" == data.barId){
//			    	$("#sr-high-priority_Id").html(data.count);
//			    } else if("pending_approvals" == data.barId){
//			    	$("#pending-approvals_Id").html(data.count);
//			    } else if("pending_reviews" == data.barId){
//			    	$("#pending-reviews_Id").html(data.count);
//			    } else if("Scheduled" == data.barId){
//			    	$("#sr-scheduled_Id").html(data.count);
//			    } else if("Completed" == data.barId){
//			    	$("#sr-closed_Id").html(data.count);
//			    } else if("on_hold" == data.barId){
//			    	$("#sr-on-hold_Id").html(data.count);
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
	let srInstanceId= "SERVICE_REQUEST_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = srInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#sr-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, srInstanceId, userId);
	});
	$('#sr-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, srInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			$("#filterBy").val("");
	    	$(".box-animate").not(this).removeClass("selected-element-b");
			getSRGridData();
		}
	});
	$('#sr-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#sr-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
    $("#sr-search-btn").click(function () {
		getSRGridData();
    });
    
    $("#fetch-previous").click(function () {
    	fetchPrevious();		
    	getSRGridData();
    });
    $("#fetch-next").click(function () {
    	fetchNext();
		getSRGridData();
    });
    $("#fetch-first").click(function () {
    	fetchFirst();
		getSRGridData();
    });
    $("#fetch-last").click(function () {
    	fetchLast();
		getSRGridData();
    });
    
    $('#goto-page').keypress(function(event){
        var keycode = (event.keyCode ? event.keyCode : event.which);
        if(keycode == '13'){
        	if(goto()) 		getSRGridData();
        }
    });
    
    $(".filter-sr").click(function(event) {
        event.preventDefault(); 
		getSRGridData();
    });
    $("#dashboard-filter").click(function(event) {
        event.preventDefault(); 
		getSRGridData();
    });
    
    //To submit the form to while click the enter button
    $("#searchForm").on("keypress", function (event) {
        var keyPressed = event.keyCode || event.which; 
        if (keyPressed === 13) { 
    		getSRGridData();
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
	
    $('#srLocation').on('change', function() {
	    $("#searchForm #srLocation").val(this.value);
	    $("#filterBy").val("");
    	$(".box-animate").not(this).removeClass("selected-element-b");
		$("#sr-open").addClass( "selected-element-b");
		loadSRDashboardCount();
		load_dynamic_data('sr-open');
	});
    
    $(".location-filter").click(function(event) {
        event.preventDefault();
        let location = $(this).attr('data-storeId');
        console.log(location);
        $("#searchForm input[name=srLocation]").val(location);
        loadSRDashboardCount();
		load_dynamic_data('sr-open');
    });	
    
    $('#location-dd')
	    .dropdown({
	      transition: 'drop',
	      clearable: true,
	      
	      onChange: function(value, label, ele, selected){
	      	//console.log('click clear 66 > '+value);
	      	if (!value) {
	      		$('#location-dd .title').html('Select Location');
	      		$('#location-dd .title').removeClass('default');
	      		
	      		$("#searchForm input[name=srLocation]").val('');
	            loadSRDashboardCount();
	    		load_dynamic_data('sr-open');
	      	}
	      }
	    })
	  ;
    
	function getSRGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "getSrDashboardList";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm, #limitForm_SERVICE_REQUEST_LIST";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getSRGridData();
	}
});
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
			    } else if("on_hold" == data.barId){
			    	$("#sr-on-hold_Id").html(sanitize(data.count));
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

function srName(params){
	return `<a target="_black" href="viewServiceRequest?srNumber=${params.data.custRequestId}">${params.data.srName}</a>` 
}

function primaryPerson(params) { 
    if (params.data.primaryPerson != null && params.data.primaryPerson != "" && 
        (params.data.primaryPerson == "HOME" || params.data.primaryPerson == "CONTRACTOR" || params.data.primaryPerson == "DEALER")) {
        return params.data.primaryPerson == "HOME" ? 'Homeowner' : 
               (params.data.primaryPerson == "CONTRACTOR" ? 'Contractor' : 'Dealer'); 
    } else {
        return ''; 
    } 
} 

function formatDate(data) {
    const value = data.value;
    if (value instanceof Date && !isNaN(value)) {
        return (new Date(value)).toLocaleDateString();
    } else {
        return value ? value : '';
    }
}

function scheduledDate(data) {
    return formatDate(data);
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


function homePhoneNumber(params) {
    if (params.data.homeOwnerPartyId && params.data.domainEntityId && params.value) {
        return `<i class="fa fa fa-phone fa-1"></i><a target="_blank" href="createPhoneCallActivity?partyId=${params.data.homeOwnerPartyId}&domainEntityType=${params.data.domainEntityType}&domainEntityId=${params.data.domainEntityId}&externalLoginKey=${params.data.externalLoginKey}">${params.value}</a>`;
    } else if (params.value) {
        return `${params.value}`;
    } else {
        return '';
    }
}

function offPhoneNumber(params) {
    if (params.data.homeOwnerPartyId && params.data.domainEntityId && params.value) {
        return `<i class="fa fa fa-phone fa-1"></i><a target="_blank" href="createPhoneCallActivity?partyId=${params.data.homeOwnerPartyId}&domainEntityType=${params.data.domainEntityType}&domainEntityId=${params.data.domainEntityId}&externalLoginKey=${params.data.externalLoginKey}">${params.value}</a>`;
    } else if (params.value) {
        return `${params.value}`;
    } else {
        return '';
    }
}

function mobileNumber(params) {
    if (params.data.homeOwnerPartyId && params.data.domainEntityId && params.value) {
        return `<i class="fa fa fa-phone fa-1"></i><a target="_blank" href="createPhoneCallActivity?partyId=${params.data.homeOwnerPartyId}&domainEntityType=${params.data.domainEntityType}&domainEntityId=${params.data.domainEntityId}&externalLoginKey=${params.data.externalLoginKey}">${params.value}</a>`;
    } else if (params.value) {
        return `${params.value}`;
    } else {
        return '';
    }
}

function contractorEmail(params) {
    if (params.data.contractorPartyId && params.data.domainEntityId && params.value) {
        return `<i class="fa fa fa-envelope fa-1"></i><a target="_blank" href="createEmailActivity?partyId=${params.data.contractorPartyId}&domainEntityId=${params.data.domainEntityId}&externalLoginKey=${params.data.externalLoginKey}">${params.value}</a>`;
    } else if (params.value) {
        return `${params.value}`;
    } else {
        return '';
    }
}

function contractorHomePhone(params) {
    if (params.data.contractorPartyId && params.data.domainEntityId && params.value) {
        return `<i class="fa fa fa-phone fa-1"></i><a target="_blank" href="createPhoneCallActivity?partyId=${params.data.contractorPartyId}&domainEntityType=${params.data.domainEntityType}&domainEntityId=${params.data.domainEntityId}&externalLoginKey=${params.data.externalLoginKey}">${params.value}</a>`;
    } else if (params.value) {
        return `${params.value}`;
    } else {
        return '';
    }
}

function contractorOffPhone(params) {
    if (params.data.contractorPartyId && params.data.domainEntityId && params.value) {
        return `<i class="fa fa fa-phone fa-1"></i><a target="_blank" href="createPhoneCallActivity?partyId=${params.data.contractorPartyId}&domainEntityType=${params.data.domainEntityType}&domainEntityId=${params.data.domainEntityId}&externalLoginKey=${params.data.externalLoginKey}">${params.value}</a>`;
    } else if (params.value) {
        return `${params.value}`;
    } else {
        return '';
    }
}

function contractorMobilePhone(params) {
    if (params.data.contractorPartyId && params.data.domainEntityId && params.value) {
        return `<i class="fa fa fa-phone fa-1"></i><a target="_blank" href="createPhoneCallActivity?partyId=${params.data.contractorPartyId}&domainEntityType=${params.data.domainEntityType}&domainEntityId=${params.data.domainEntityId}&externalLoginKey=${params.data.externalLoginKey}">${params.value}</a>`;
    } else if (params.value) {
        return `${params.value}`;
    } else {
        return '';
    }
}

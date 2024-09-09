//
//fagReady("UNASSIGNED_COMM_HISTORY", function(el, api, colApi, gridApi){
//    $("#unassigned-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#unassigned-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#unassigned-clear-filter-btn").click(function () {
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
//    $("#unassigned-export-btn").click(function () {
//    	gridApi.csvExport();
//    }); 
//   
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
//		let barId = $("#unassignedForm input[name=filterBy]").val();
//		if("unassigned_sms" === barId) {
//			let smsColunmdef = getGridInstance("UNASSIGNED_SMS_LIST");
//			api.setColumnDefs(smsColunmdef);
//			api.refreshHeader();
//		} else{
//			let colunmdef = getGridInstance("UNASSIGNED_COMM_HISTORY");
//			api.setColumnDefs(colunmdef);
//			api.refreshHeader();
//		}
//        loadUnassignedComHistory();
//    });

// 	$("#fetch-previous").click(function () {
//    	fetchPrevious();
//    	//loadSRGrid(gridApi, api);
//    	loadUnassignedComHistory(gridApi, api, colApi);
//    });
//    $("#fetch-next").click(function () {
//    	fetchNext();
//    	//loadSRGrid(gridApi, api);
//    	loadUnassignedComHistory(gridApi, api, colApi);
//    });
//    $("#fetch-first").click(function () {
//    	fetchFirst();
//    	//loadSRGrid(gridApi, api);
//    	loadUnassignedComHistory(gridApi, api, colApi);
//    });
//    $("#fetch-last").click(function () {
//    	fetchLast();
//    	//loadSRGrid(gridApi, api);
//    	loadUnassignedComHistory(gridApi, api, colApi);
//    });
//    
//    $('#goto-page').keypress(function(event){
//        var keycode = (event.keyCode ? event.keyCode : event.which);
//        if(keycode == '13'){
//        	if(goto()) loadUnassignedComHistory(gridApi,api, colApi);
//        		//loadSRGrid(gridApi, api); 
//        }
//    });
//    
//    $("#mark-read").click(function(event) {
//        event.preventDefault(); 
//        let communicationEventId = $('#commEventId').val();
//        console.log("communicationEventId------->"+communicationEventId);
//        
//        $.ajax({
//      	  async: false,
//      	  url:'/common-portal/control/markAsMailRead',
//      	  type:"POST",
//      	  data: {"communicationEventId": communicationEventId},
//      	  success: function(data){
//      		  showAlert('success', "Marked as read.");
//      		  loadUnassignedComHistory(gridApi, api, colApi);  
//      	  }
//      	});
//      	
//    });
//    
//    
//    loadUnassignedComHistory(gridApi,api, colApi);
//    
//});

function load_dynamic_data(id){
	$("#filterBy").val(id);
	$("#dashboard-filter").click();
}

function markAsRead(communicationEventId){
	$("#commEventId").val(communicationEventId);
	$("#mark-read").click();
}

function getGridInstance(instanceId) {
	var externalLoginKey = $("#externalLoginKey").val();
	let smsColumnDef = [];
	$.ajax({
		async: false,
		url:"/ofbiz-ag-grid/control/getGridUserConfig?userid=admin&instanceid="+instanceId+"&lastUpdateTime=-1&externalLoginKey="+externalLoginKey,
		type:"POST",
		data: {},
		success: function(data){
			const smsGridOptions = deserialize(data.admin);
			smsColumnDef = smsGridOptions.columnDefs;
			//console.log("smsColumnDef--->"+smsGridOptions.columnDefs);
		}
	});
	return smsColumnDef;
}

function getEmailDashboardDataCountList() {

	var formInput = $('#unassignedForm').serialize();
	$.ajax({
	  async: true,
	  url:"getEmailDashboardDataCountList",
	  type:"POST",
	  data: JSON.parse(JSON.stringify(formInput)),
	  success: function(data){
		  var dataList = data.list;
		  if(dataList != null && dataList != "" && dataList != "undefined"){
			  for (i = 0; i < dataList.length; i++) {
			    	var data = dataList[i];
			    	console.log(data.barId+","+data.count);
			    if("unassigned-emails"== data.barId){
			    	$("#unassigned-emails_Id").html(sanitize(data.count));
			    } else if("assigned-emails" == data.barId){
			    	$("#assigned-emails_Id").html(sanitize(data.count));
			    } else if("last7days" == data.barId){
			    	$("#last-7-days_Id").html(sanitize(data.count));
			    } else if("last24hours" == data.barId){
			    	$("#last-24-hours_Id").html(sanitize(data.count));
			    } else if("unassigned_sms"== data.barId){
			    	$("#unassigned_sms_Id").html(sanitize(data.count));
			    }
			  }
		  }
	  }
	});
	
}


//function loadUnassignedComHistory(gridApi, api, colApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	$.ajax({
//	  async: true,
//	  url:'/common-portal/control/getEmailActivities',
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify($("#unassignedForm, #limitForm").serialize())),
//	  success: function(data){
//		  gridApi.setRowData(data.list);
//		  data.list=[];
//		  paginateHandler(data);
//		  setTimeout(() => colApi.autoSizeAllColumns(), 1000);
//	  }
//	});
//}




$(function() {
	let unassignedCommHistoryInstanceId= "UNASSIGNED_COMM_HISTORY";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	$("#unassigned_sms").addClass("selected-element-b");
	$("#filterBy").val('unassigned_sms');
	const formDataObject = {};
	formDataObject.gridInstanceId = unassignedCommHistoryInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#unassigned-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, unassignedCommHistoryInstanceId, userId);
	});
	$('#unassigned-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, unassignedCommHistoryInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getAccountGridData();
		}
	});
	$('#sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#commHistory-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
    $(".filter-sr").click(function(event) {
        event.preventDefault(); 
		getUnassignedComHistoryGridData();  
    });
    $("#dashboard-filter").click(function(event) {
        event.preventDefault();
		let barId = $("#unassignedForm input[name=filterBy]").val();
		if("unassigned_sms" === barId) {
			$("#unassigned-comm-history-grid").hide();
			$("#unassigned-sms-grid").show();
			$("#unassigned-sms-tag").click();
		} else{
			unassignedCommHistoryInstanceId = "UNASSIGNED_COMM_HISTORY";
			$("#unassigned-comm-history-grid").show();
			$("#unassigned-sms-grid").hide();
		}
		getUnassignedComHistoryGridData();  
    });

 	$("#fetch-previous").click(function () {
    	fetchPrevious();
		getUnassignedComHistoryGridData();  
    });
    $("#fetch-next").click(function () {
    	fetchNext();
		getUnassignedComHistoryGridData();  
    });
    $("#fetch-first").click(function () {
    	fetchFirst();
		getUnassignedComHistoryGridData();  
    });
    $("#fetch-last").click(function () {
    	fetchLast();
		getUnassignedComHistoryGridData();  
    });
    
    $('#goto-page').keypress(function(event){
        var keycode = (event.keyCode ? event.keyCode : event.which);
        if(keycode == '13'){
        	if(goto())    getUnassignedComHistoryGridData();   
        }
    });
    
    $("#mark-read").click(function(event) {
        event.preventDefault(); 
        let communicationEventId = $('#commEventId').val();
        console.log("communicationEventId------->"+communicationEventId);
        
        $.ajax({
      	  async: false,
      	  url:'/common-portal/control/markAsMailRead',
      	  type:"POST",
      	  data: {"communicationEventId": communicationEventId},
      	  success: function(data){
      		  showAlert('success', "Marked as read.");
        		getUnassignedComHistoryGridData();  
      	  }
      	});
      	
    });

	
	function getUnassignedComHistoryGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/getEmailActivities";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#unassignedForm, #limitForm_UNASSIGNED_COMM_HISTORY";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getUnassignedComHistoryGridData();
	}
});
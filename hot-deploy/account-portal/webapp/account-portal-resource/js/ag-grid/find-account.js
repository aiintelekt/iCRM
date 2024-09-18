/*fagReady("ACCOUNTS", function(el, api, colApi, gridApi){
    $("#refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
	$("#sub-filter-clear-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    });
    $("#clear-filter-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    	gridApi.refreshUserPreferences();
    });
    $("#export-btn").click(function () {
    	gridApi.csvExport();
    });

    $("#update-btn").click(function () {
    	gridApi.saveUpdates();
        setTimeout(() => {  loadMainGrid(gridApi, api, colApi); }, 1000);
    })

    $("#main-search-btn").click(function () {
    	$("#limitForm input[name=VIEW_INDEX]").val(0);
    	loadMainGrid(gridApi, api, colApi);
    });
    $("#insert-btn").click(function () {
    	gridApi.insertNewRow()
    })
    $("#remove-btn").click(function () {
        //removeMainGrid(fag1, api);
    	gridApi.removeSelected();
        setTimeout(() => {  loadMainGrid(gridApi, api, colApi); }, 1000);
        
    });
    
    $("#fetch-previous").click(function () {
    	fetchPrevious();
    	//loadSRGrid(gridApi, api);
    	loadMainGrid(gridApi, api, colApi);
    });
    $("#fetch-next").click(function () {
    	fetchNext();
    	//loadSRGrid(gridApi, api);
    	loadMainGrid(gridApi, api, colApi);
    });
    $("#fetch-first").click(function () {
    	fetchFirst();
    	//loadSRGrid(gridApi, api);
    	loadMainGrid(gridApi, api, colApi);
    });
    $("#fetch-last").click(function () {
    	fetchLast();
    	//loadSRGrid(gridApi, api);
    	loadMainGrid(gridApi, api, colApi);
    });
    
    $('#goto-page').keypress(function(event){
        var keycode = (event.keyCode ? event.keyCode : event.which);
        if(keycode == '13'){
        	if(goto()) loadMainGrid(gridApi, api, colApi);
        		//loadSRGrid(gridApi, api); 
        }
    });
    
    $(".filter-account").click(function(event) {
        event.preventDefault(); 
        
        $("#account-grid-header-title").html($(this).attr("data-searchTypeLabel"));
        //alert($(this).attr("data-searchTypeLabel"));
        $("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));
        
        loadMainGrid(gridApi, api, colApi);
    });
    
    //To submit the form to while click the enter button
    $("#searchForm").on("keypress", function (event) {
        var keyPressed = event.keyCode || event.which; 
        if (keyPressed === 13) { 
        	loadMainGrid(gridApi, api, colApi);
            event.preventDefault(); 
            return false; 
        } 
    }); 
    
    $("#dashboard-filter").click(function(event) {
        event.preventDefault(); 
        console.log("dashboard-filter trigger");
        loadMainGrid(gridApi, api, colApi);
    });
    
	$('input[type=radio][name="filterType"]').on('change', function() {
	    $("#searchForm input[type=hidden][name='filterType']").val(this.value);
	    $("#filterBy").val("");
    	$(".box-animate").not(this).removeClass("selected-element-b");
		$("#account-active").addClass( "selected-element-b");
		load_dynamic_data('account-active');
		loadAccountDashboardCount();
    	//$("#searchForm").submit();
	});
    loadMainGrid(gridApi, api, colApi);
});
function loadMainGrid(gridApi, api, colApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	api.showLoadingOverlay();
	var formInput = $('#searchForm, #limitForm').serialize();
	$.ajax({
	  async: true,
	  url:'/account-portal/control/searchAccounts',
	  type:"POST",
	  data: JSON.parse(JSON.stringify(formInput)),
	  success: function(data){
		  gridApi.setRowData(data.list);
		  setTimeout(() => colApi.autoSizeAllColumns(), 1000);
		  data.list=[];
		  paginateHandler(data);
	  }
	});
}*/


$(function() {
	let accountInstanceId= "ACCOUNTS";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = accountInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#savePrefBtnId-account').click(function(){
		saveGridPreference(gridInstance, accountInstanceId, userId);
	});
	$('#clearFilterBtnId-account').click(function(){
		clearGridPreference(gridInstance, accountInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getAccountGridData();
		}
	});
	$('#subFltrClearId-account').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#account-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$("#grid-export-btn").click(function() {
		exportList('searchForm');
	});
	$('#main-search-btn').click(function(){
		getAccountGridData();
	});
    $("#fetch-previous").click(function () {
    	fetchPrevious();
    	getAccountGridData();
    });
    $("#fetch-next").click(function () {
    	fetchNext();
    	getAccountGridData();
    });
    $("#fetch-first").click(function () {
    	fetchFirst();
    	getAccountGridData();
    });
    $("#fetch-last").click(function () {
    	fetchLast();
    	getAccountGridData();
    });
    
    $('#goto-page').keypress(function(event){
        var keycode = (event.keyCode ? event.keyCode : event.which);
        if(keycode == '13'){
        	if(goto()) getAccountGridData();
        }
    });
    
    $(".filter-account").click(function(event) {
        event.preventDefault();
        $("#account-grid-header-title").html(DOMPurify.sanitize($(this).attr("data-searchTypeLabel")));
        $("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));
        getAccountGridData();
    });
    
    //To submit the form to while click the enter button
    $("#searchForm").on("keypress", function (event) {
        var keyPressed = event.keyCode || event.which; 
        if (keyPressed === 13) { 
        	getAccountGridData();
            event.preventDefault(); 
            return false; 
        } 
    }); 
    
    $("#dashboard-filter").click(function(event) {
        event.preventDefault(); 
        console.log("dashboard-filter trigger");
        getAccountGridData();
    });
    
	$('input[type=radio][name="filterType"]').on('change', function() {
	    $("#searchForm input[type=hidden][name='filterType']").val(this.value);
	    $("#filterBy").val("");
    	$(".box-animate").not(this).removeClass("selected-element-b");
		$("#account-active").addClass( "selected-element-b");
		load_dynamic_data('account-active');
		loadAccountDashboardCount();
	});
	
	function getAccountGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/account-portal/control/searchAccounts";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm, #limitForm_ACCOUNTS";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance && $("#main-search-btn").length){
		getAccountGridData();
	}
});

function load_dynamic_data(id) {
	console.log(id);
	$("#searchForm input[name=statusId]").val('');
	$("#filterBy").val(id);
	if (id.includes("account-")) {
		if (id == 'account-active') {
			$("#searchForm input[name=statusId]").val('ACTIVE');
		}
		$("#dashboard-filter").click();
	}
}
function loadAccountDashboardCount(){
	
	var formInput = $('#searchForm').serialize();
	$.ajax({
	  async: true,
	  url:"getAccountDashboardCountList",
	  type:"POST",
	  data: JSON.parse(JSON.stringify(formInput)),
	  success: function(data){
		  var dataList = data.list;
		  if(dataList != null && dataList != "" && dataList != "undefined"){
			  for (i = 0; i < dataList.length; i++) {
		    	var data = dataList[i];
		    	console.log(data.barId+","+data.count);
			    if("openAccount"== data.barId){
			    	$("#account-active_Id").html(sanitize(data.count));
			    }
			  }
			if ($("#account-active")) {
				load_dynamic_data('account-active');
			}
		  }
	  }
	});	
}

function groupName(params) {
	return '<a href="viewAccount?partyId=' + params.data.partyId + '">' + params.value + '</a>';
}
function contactNumber(params) {
	if (params.data.partyId && params.data.domainEntityId && params.value) {
		return `<i class="fa fa fa-phone fa-1"></i> <a target="_blank" href="createPhoneCallActivity?partyId=${params.data.partyId}&domainEntityType=${params.data.domainEntityType}&domainEntityId=${params.data.domainEntityId}&externalLoginKey=${params.data.externalLoginKey}">${params.value}</a>`;
	} else if (params.value) {
		return `${params.value}`;
	}
}
function primaryContactEmail(params) {
//	if (params.data.primaryContactId && params.data.domainEntityId && params.value) {
//		return `<i class="fa fa fa-envelope fa-1"></i> <a target="_blank" href="addEmail?partyId=${params.data.primaryContactId}&domainEntityType=${params.data.domainEntityType}&domainEntityId=${params.data.domainEntityId}&externalLoginKey=${params.data.externalLoginKey}">${params.value}</a>`;
//	} else {
//	}
	if (params.value) {
		return `<i class="fa fa fa-envelope fa-1"></i> ${params.value}`;
	}
}

function primaryContactPhone(params) {
//	if (params.data.primaryContactId && params.data.domainEntityId && params.value) {
//		return `<i class="fa fa fa-phone fa-1"></i> <a target="_blank" href="createPhoneCallActivity?partyId=${params.data.primaryContactId}&domainEntityType=${params.data.domainEntityType}&domainEntityId=${params.data.domainEntityId}&externalLoginKey=${params.data.externalLoginKey}">${params.value}</a>`;
//	} else {
//	}
	if (params.value) {
		return `<i class="fa fa fa-phone fa-1"></i> ${params.value}`;
	}
}

function infoString(params) {
	if (params.data.partyId && params.data.domainEntityId && params.value) {
		return `<i class="fa fa fa-envelope fa-1"></i> <a target="_blank" href="addEmail?partyId=${params.data.partyId}&domainEntityType=${params.data.domainEntityType}&domainEntityId=${params.data.domainEntityId}&externalLoginKey=${params.data.externalLoginKey}">${params.value}</a>`;
	} else if (params.value) {
		return `${params.value}`;
	}
}


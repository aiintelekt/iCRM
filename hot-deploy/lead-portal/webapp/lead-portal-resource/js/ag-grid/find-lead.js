//fagReady("LEADS", function(el, api, colApi, gridApi){
//    $("#lead-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#lead-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#lead-clear-filter-btn").click(function () {
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
//    $("#lead-export-btn").click(function () {
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
//    $("#insert-btn").click(function () {
//    	gridApi.insertNewRow()
//    })
//    $("#remove-btn").click(function () {
//        //removeMainGrid(fag1, api);
//    	gridApi.removeSelected();
//        setTimeout(() => {  loadMainGrid(gridApi, api, colApi); }, 1000);
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
//    
//    $('#goto-page').keypress(function(event){
//       var keycode = (event.keyCode ? event.keyCode : event.which);
//        if(keycode == '13'){
//        	if(goto())
//        	loadMainGrid(gridApi, api, colApi); 
//        }
//    });
//    
//    $(".filter-lead").click(function(event) {
//        event.preventDefault(); 
//        
//        $("#lead-grid-header-title").html($(this).attr("data-searchTypeLabel"));
//        //alert($(this).attr("data-searchTypeLabel"));
//        $("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));
//        
//        loadMainGrid(gridApi, api, colApi);
//    });
//    
//    $("#dashboard-filter").click(function(event) {
//        event.preventDefault(); 
//        console.log("dashboard-filter trigger");
//        loadMainGrid(gridApi, api, colApi);
//    });
//
//	$('input[type=radio][name="filterType"]').on('change', function() {
//	    $("#searchForm input[type=hidden][name='filterType']").val(this.value);
//	    $("#filterBy").val("");
//    	$(".box-animate").not(this).removeClass("selected-element-b");
//		$("#lead_universe").addClass( "selected-element-b");
//		load_dynamic_data('lead_universe');
//		loadLeadDashboardCount();
//    	//$("#searchForm").submit();
//	});
//	//To submit the form to while click the enter button
//    $("#searchForm").on("keypress", function (event) {
//        var keyPressed = event.keyCode || event.which; 
//        if (keyPressed === 13) { 
//        	loadMainGrid(gridApi, api, colApi);
//            event.preventDefault();
//            return false; 
//        } 
//    }); 
//    
//    loadMainGrid(gridApi, api, colApi);
//});

function load_dynamic_data(id) {
	console.log(id);
	$("#filterBy").val(id);
	if (id.includes("lead_")) {
		$("#searchForm input[name=statusId]").val(id);
		$("#dashboard-filter").click();
	}
}

//function loadMainGrid(gridApi, api, colApi) {
//	console.log('calling find method');
//	
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	
//	$.ajax({
//	  async: true,
//	  url:'/lead-portal/control/searchLeads',
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify($("#searchForm, #limitForm").serialize())),
//	  success: function(data){
//		  gridApi.setRowData(data.list);
//		  data.list=[];
//		  paginateHandler(data);
//	  }
//	});
//}

$(function() {
	let leadInstanceId= "LEADS";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = leadInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	

	gridInstance = prepareGridInstance(formDataObject);

	$('#lead-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, leadInstanceId, userId);
	});
	$('#lead-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, leadInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getLeadGridData();
		}
	});
	$("#lead-export-btn").click(function () {
	    gridInstance.exportDataAsCsv();
	});
	$('#lead-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#main-search-btn").click(function () {
		getLeadGridData();
	});
	$("#fetch-previous").click(function () {
		fetchPrevious();
		getLeadGridData();
	});
	$("#fetch-next").click(function () {
		fetchNext();
		getLeadGridData();
	});
	$("#fetch-first").click(function () {
		fetchFirst();
		getLeadGridData();
	});
	$("#fetch-last").click(function () {
		fetchLast();
		getLeadGridData();
	});

	$('#goto-page').keypress(function(event){
		var keycode = (event.keyCode ? event.keyCode : event.which);
		if(keycode == '13'){
			if(goto())
				getLeadGridData();
		}
	});

	$(".filter-lead").click(function(event) {
		event.preventDefault(); 
		$("#lead-grid-header-title").html(DOMPurify.sanitize($(this).attr("data-searchTypeLabel")));
		$("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));

		getLeadGridData();
	});

	$("#dashboard-filter").click(function(event) {
		event.preventDefault(); 
		console.log("dashboard-filter trigger");
		getLeadGridData();
	});

	$('input[type=radio][name="filterType"]').on('change', function() {
		$("#searchForm input[type=hidden][name='filterType']").val(this.value);
		$("#filterBy").val("");
		$(".box-animate").not(this).removeClass("selected-element-b");
		$("#lead_universe").addClass( "selected-element-b");
		load_dynamic_data('lead_universe');
		loadLeadDashboardCount();
	});
	//To submit the form to while click the enter button
	$("#searchForm").on("keypress", function (event) {
		var keyPressed = event.keyCode || event.which; 
		if (keyPressed === 13) { 
			getLeadGridData();
			event.preventDefault();
			return false; 
		} 
	}); 

	function getLeadGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/lead-portal/control/searchLeads";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm, #limitForm_LEADS";
		callCtx.ajaxResponseKey = "list";

		setGridData(gridInstance, callCtx);
	}
	if(gridInstance && $("#main-search-btn").length){
		getLeadGridData();
	}
});


function loadLeadDashboardCount(){

	var formInput = $('#searchForm').serialize();
	$.ajax({
		async: true,
		url:"getLeadDashboardCountList",
		type:"POST",
		data: JSON.parse(JSON.stringify(formInput)),
		success: function(data){
			var dataList = data.list;
			if(dataList != null && dataList != "" && dataList != "undefined"){
				for (i = 0; i < dataList.length; i++) {
					var data = dataList[i];
					console.log(data.barId+","+data.count);
					if("universe"== data.barId){
						$("#lead_universe_Id").html(sanitize(data.count));
					} else if("suspect" == data.barId){
						$("#lead_suspect_Id").html(sanitize(data.count));
					} else if("prospect" == data.barId){
						$("#lead_prospect_Id").html(sanitize(data.count));
					} else if("target" == data.barId){
						$("#lead_target_Id").html(sanitize(data.count));
					} else if("qualified" == data.barId){
						$("#lead_qualified_Id").html(sanitize(data.count));
					}
				}
				if ($("#lead_universe")) {
					load_dynamic_data('lead_universe');
				}
			}
		}
	});

}

function groupName(params) {
	return `<a href="/lead-portal/control/viewLead?partyId=${params.data.partyId}">${params.value}</a>`;
}

function infoString(params) {
	if (params.data.partyId && params.data.domainEntityId && params.value) { 
		return `<i class="fa fa fa-envelope fa-1"></i> <a target="_blank" href="addEmail?partyId=${params.data.partyId}&domainEntityType=${params.data.domainEntityType}&domainEntityId=${params.data.domainEntityId}&externalLoginKey=${params.data.externalLoginKey}">${params.value}</a>`; 
	} else if (params.value) { 
		return `${params.value}`; 
	} 
} 

function contactNumber(params) { 
	if (params.data.partyId && params.data.domainEntityId && params.value) { 
		return `<i class="fa fa fa-phone fa-1"></i> <a target="_blank" href="createPhoneCallActivity?partyId=${params.data.partyId}&domainEntityType=${params.data.domainEntityType}&domainEntityId=${params.data.domainEntityId}&externalLoginKey=${params.data.externalLoginKey}">${params.value}</a>`;
	} else if (params.value) { 
		return `${params.value}`; 
	} 
}


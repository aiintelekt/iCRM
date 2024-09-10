
$(function() {
	let activityListInstanceId = "ACTIVITES";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = activityListInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	//formDataObject.dataFetchCall = getRecentTransRowData;
	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	function getActivityGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/searchActivities";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#activity-search-form";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	
	
	if(gridInstance){
		getActivityGridData();
	}
	
	$('#activities-save-pref').click(function(){
		saveGridPreference(gridInstance, activityListInstanceId, userId);
	});
	
	$('#activities-clear-pref').click(function(){
		clearGridPreference(gridInstance, activityListInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getActivityGridData();
		}
	});
	$("#activity-export-btn").click(function () {
	    gridInstance.exportDataAsCsv();
	});
	$('#activities-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});
	
	$("#refresh-activity-btn").click(function () {
    	getActivityGridData();
    });
    
    $(".filter-activity").click(function(event) {
        event.preventDefault(); 
        
        $("#activity-grid-header-title").html(DOMPurify.sanitize($(this).attr("data-searchTypeLabel")));
        //alert($(this).attr("data-searchTypeLabel"));
        $("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));
        
        getActivityGridData();
    });    
   
    $("#closedchk").change(function(event) {
    	//console.log("Test  "+this.value);
    	if($(this).is(":checked")){
    		$("#activity-search-form #closed").val(this.value);
    	} else{
    		$("#activity-search-form #closed").val("");
    	}
    	
    	getActivityGridData();
    });
    
    $("#openchk").change(function(event) {
    	if($(this).is(":checked")){
    		$("#activity-search-form #open").val(this.value);
    	} else{
    		$("#activity-search-form #open").val("");
    	}
    	getActivityGridData();
    });
    $("#schedulechk").change(function(event) {
    	//console.log("Test  "+this.value);
    	if($(this).is(":checked")){
    		$("#activity-search-form #scheduled").val(this.value);
    	} else{
    		$("#activity-search-form #scheduled").val("");
    	}
    	getActivityGridData();
    });
    
    $("input[name=activity-cat]").change(function(event) {
    	console.log("activity-cat  "+this.value);
    	let activityCat = this.value;
    	if (activityCat=='PROG') {
    		$("#activity-search-form input[name=isChecklistActivity]").val('Y');
    	} else if (activityCat=='OTHER') {
    		$("#activity-search-form input[name=isChecklistActivity]").val("N");
    	}
    	getActivityGridData();
    });

    $("#act-search-btn").click(function () {
    	getActivityGridData();
    });

});
/*
fagReady("ACTIVITES", function(el, api, colApi, gridApi){
    $("#activity-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();

		if($('#openchk').length){
	    	$('#openchk').prop('checked', false);
	    	$("#activity-search-form #open").val("");
	    }
	    
		loadActivityGrid(api, gridApi);
    });
    $("#activity-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#activity-clear-filter-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    	gridApi.refreshUserPreferences();
    });
	$("#activity-sub-filter-clear-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    });
    $("#activity-export-btn").click(function () {
    	gridApi.csvExport();
    });

    $("#refresh-activity-btn").click(function () {
    	loadActivityGrid(api, gridApi);
    });
    
    $(".filter-activity").click(function(event) {
        event.preventDefault(); 
        
        $("#activity-grid-header-title").html($(this).attr("data-searchTypeLabel"));
        //alert($(this).attr("data-searchTypeLabel"));
        $("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));
        
        loadActivityGrid(api, gridApi);
    });    
   
    $("#closedchk").change(function(event) {
    	//console.log("Test  "+this.value);
    	if($(this).is(":checked")){
    		$("#activity-search-form #closed").val(this.value);
    	} else{
    		$("#activity-search-form #closed").val("");
    	}
    	
    	loadActivityGrid(api, gridApi);
    });
    
    $("#openchk").change(function(event) {
    	if($(this).is(":checked")){
    		$("#activity-search-form #open").val(this.value);
    	} else{
    		$("#activity-search-form #open").val("");
    	}
    	loadActivityGrid(api, gridApi);
    });
    $("#schedulechk").change(function(event) {
    	//console.log("Test  "+this.value);
    	if($(this).is(":checked")){
    		$("#activity-search-form #scheduled").val(this.value);
    	} else{
    		$("#activity-search-form #scheduled").val("");
    	}
    	loadActivityGrid(api, gridApi);
    });
    
    $("input[name=activity-cat]").change(function(event) {
    	console.log("activity-cat  "+this.value);
    	let activityCat = this.value;
    	if (activityCat=='PROG') {
    		$("#activity-search-form input[name=isChecklistActivity]").val('Y');
    	} else if (activityCat=='OTHER') {
    		$("#activity-search-form input[name=isChecklistActivity]").val("N");
    	}
    	loadActivityGrid(api, gridApi);
    });

    $("#act-search-btn").click(function () {
    	loadActivityGrid(api, gridApi);
    });

    postLoadGridData(api, gridApi, "sr-activities", loadActivityGrid);
    postLoadGridData(api, gridApi, "a-activities", loadActivityGrid);
    postLoadGridData(api, gridApi, "lead-activities", loadActivityGrid);
    postLoadGridData(api, gridApi, "contact-activities", loadActivityGrid);
    postLoadGridData(api, gridApi, "c-activities", loadActivityGrid);
    postLoadGridData(api, gridApi, "opportunity-activities", loadActivityGrid);
    postLoadGridData(api, gridApi, "activities", loadActivityGrid);
    
    //loadActivityGrid(gridApi);
});

function loadActivityGrid(api, gridApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	api.showLoadingOverlay();
	$.ajax({
	  async: false,
	  url:'/common-portal/control/searchActivities',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#activity-search-form").serialize())),
	  success: function(data){
		  gridApi.setRowData(data.list);
	  }
	});
}
*/



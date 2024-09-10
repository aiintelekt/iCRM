$(function() {
	let opportunityInstanceId= "OPPORTUNITYS";
	let gridInstance  = "";

	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = opportunityInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	//formDataObject.dataFetchCall = getCustBoughtRowData;
	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	function getOpportunityGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/searchOpportunitys";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#opportunity-search-form";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	
	if(gridInstance){
		getOpportunityGridData();
	}
	$('#opportunity-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, opportunityInstanceId, userId);
	});
	
	$('#opportunity-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, opportunityInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getOpportunityGridData();
		}
	});
	$('#opportunity-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#refresh-opportunity-btn").click(function () {
    	getOpportunityGridData();
    });
	$("#opportunity-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
    $(".filter-opportunity").click(function(event) {
        event.preventDefault(); 
        
        $("#opportunity-grid-header-title").html(DOMPurify.sanitize($(this).attr("data-searchTypeLabel")));
        $("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));
        
        getOpportunityGridData();
    });
    $(".oppo-status").change(function(event) {
    	var openStatus = $('input[name="oppoOpen"]:checked').val();
    	var closedStatus = $('input[name="oppoClosed"]:checked').val();
    	
    	if(openStatus != null && openStatus !="undefined" && openStatus != "")
    		$("#opportunity-search-form #statusOpen").val(openStatus);
    	else
    		$("#opportunity-search-form #statusOpen").val("");
    	
    	if(closedStatus != null && closedStatus !="undefined" && closedStatus != "")
    		$("#opportunity-search-form #statusClosed").val(closedStatus);
    	else
    		$("#opportunity-search-form #statusClosed").val("");
    	
    	getOpportunityGridData();
    });
    
    $("#estClosedDays").focusout(function() {
    	var estClosedDays = $("#estClosedDays").val();
    	if(estClosedDays != null && estClosedDays !="undefined" && estClosedDays != "")
    		$("#opportunity-search-form #estimatedClosedDays").val(estClosedDays);
    	else
    		$("#opportunity-search-form #estimatedClosedDays").val("");
    	
    	getOpportunityGridData();
    });
    
    $("#refresh-opportunity-btn").click(function () {
    	getOpportunityGridData();
    });
    
	$("#search-oppo-btn").click(function () {
    	getOpportunityGridData();
    });
    

    $('#refresh-sr-btn').on('click', function() {
    	getOpportunityGridData();
    });

	$('#service-req-btn').on('click', function() {
    	getOpportunityGridData();
    });
	
});

function opportunityName(params) { 
	return '<a href="/opportunity-portal/control/viewOpportunity?salesOpportunityId=' + params.data.salesOpportunityId + ' " target="_blank">' + params.data.opportunityName + '</a>' 
}



/*

fagReady("OPPORTUNITYS", function(el, api, colApi, gridApi){
    $("#opportunity-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
		if($('#oppoOpen').length){
	    	$('#oppoOpen').prop('checked', false);
	    	$("#opportunity-search-form #statusOpen").val("");
	    }
		loadOpportunityGrid(gridApi, api, colApi);
    });
    $("#opportunity-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#opportunity-clear-filter-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    	gridApi.refreshUserPreferences();
    });
	$("#sub-filter-clear-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    });
    $("#opportunity-export-btn").click(function () {
    	gridApi.csvExport();
    });

    $("#refresh-opportunity-btn").click(function () {
    	loadOpportunityGrid(gridApi, api, colApi);
    });
    
    $(".filter-opportunity").click(function(event) {
        event.preventDefault(); 
        
        $("#opportunity-grid-header-title").html($(this).attr("data-searchTypeLabel"));
        $("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));
        
        loadOpportunityGrid(gridApi, api, colApi);
    });
    $(".oppo-status").change(function(event) {
    	var openStatus = $('input[name="oppoOpen"]:checked').val();
    	var closedStatus = $('input[name="oppoClosed"]:checked').val();
    	
    	if(openStatus != null && openStatus !="undefined" && openStatus != "")
    		$("#opportunity-search-form #statusOpen").val(openStatus);
    	else
    		$("#opportunity-search-form #statusOpen").val("");
    	
    	if(closedStatus != null && closedStatus !="undefined" && closedStatus != "")
    		$("#opportunity-search-form #statusClosed").val(closedStatus);
    	else
    		$("#opportunity-search-form #statusClosed").val("");
    	
    	loadOpportunityGrid(gridApi, api, colApi);
    });
    
    $("#estClosedDays").focusout(function() {
    	var estClosedDays = $("#estClosedDays").val();
    	if(estClosedDays != null && estClosedDays !="undefined" && estClosedDays != "")
    		$("#opportunity-search-form #estimatedClosedDays").val(estClosedDays);
    	else
    		$("#opportunity-search-form #estimatedClosedDays").val("");
    	
    	loadOpportunityGrid(gridApi, api, colApi);
    });
    
    $("#refresh-opportunity-btn").click(function () {
    	loadOpportunityGrid(gridApi, api, colApi);
    });
    
	$("#search-oppo-btn").click(function () {
    	loadOpportunityGrid(gridApi, api, colApi);
    });
    
    postLoadGrid(api, gridApi, colApi, "c-opportunities", loadOpportunityGrid);
    postLoadGrid(api, gridApi, colApi, "a-opportunities", loadOpportunityGrid);
    postLoadGrid(api, gridApi, colApi, "contact-opportunities", loadOpportunityGrid);
});

function loadOpportunityGrid(gridApi, api, colApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	api.showLoadingOverlay();
	$.ajax({
	  async: false,
	  url:'/common-portal/control/searchOpportunitys',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#opportunity-search-form").serialize())),
	  success: function(data){
		  gridApi.setRowData(data.list);
		  setTimeout(() => colApi.autoSizeAllColumns(), 500);
	  }
	});
}
*/
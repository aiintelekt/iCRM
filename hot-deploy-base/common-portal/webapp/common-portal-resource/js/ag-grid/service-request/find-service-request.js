$(function() {
	let srListInstanceId= "SR_LIST";
	let gridInstance  = "";

	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = srListInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	//formDataObject.dataFetchCall = getCustBoughtRowData;
	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	function prepareGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/searchServiceRequests";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#sr-search-form";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	
	if(gridInstance){
		prepareGridData();
	}
	$('#service-request-save-pref').click(function(){
		saveGridPreference(gridInstance, srListInstanceId, userId);
	});
	$("#service-request-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#service-request-clear-pref').click(function(){
		clearGridPreference(gridInstance, srListInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			prepareGridData();
		}
	});
	$('#service-request-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});
	$(".filter-serviceRequest").click(function(event) {
        event.preventDefault(); 
        
        $("#serviceRequest-grid-header-title").html(DOMPurify.sanitize($(this).attr("data-searchTypeLabel")));
        //alert($(this).attr("data-searchTypeLabel"));
        $("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));
        
        prepareGridData();
    });    

    $(".sr-status").change(function(event) {
    	var openStatus = $('input[name="srOpen"]:checked').val();
    	var closedStatus = $('input[name="srClosed"]:checked').val();
    	var slaStRiskStatus = $('input[name="srSlaAtRisk"]:checked').val();
    	var overDueStatus = $('input[name="srSlaExpired"]:checked').val();
    	
    	if(openStatus != null && openStatus !="undefined" && openStatus != "")
    		$("#sr-search-form #open").val(openStatus);
    	else
    		$("#sr-search-form #open").val("");
    	
    	if(closedStatus != null && closedStatus !="undefined" && closedStatus != "")
    		$("#sr-search-form #closed").val(closedStatus);
    	else
    		$("#sr-search-form #closed").val("");
    	
    	if(slaStRiskStatus != null && slaStRiskStatus !="undefined" && slaStRiskStatus != "")
    		$("#sr-search-form #slaAtRisk").val(slaStRiskStatus);
    	else
    		$("#sr-search-form #slaAtRisk").val("");
    	
    	if(overDueStatus != null && overDueStatus !="undefined" && overDueStatus != "")
    		$("#sr-search-form #slaExpired").val(overDueStatus);
    	else
    		$("#sr-search-form #slaExpired").val("");
    	
    	prepareGridData();
    });
    

    $('#refresh-sr-btn').on('click', function() {
    	prepareGridData();
    });

	$('#service-req-btn').on('click', function() {
    	prepareGridData();
    });
	
});



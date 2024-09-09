/*
fagReady("PICKER_PARTY_LIST", function(el, api, colApi, gridApi){
    $("#partyPicker-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#partyPicker-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#partyPicker-clear-filter-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    	gridApi.refreshUserPreferences();
    });
	$("#partyPicker-sub-filter-clear-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    });
    $("#partyPicker-export-btn").click(function () {
    	gridApi.csvExport();
    });

    $("#partyPicker-search-btn").click(function () {
    	loadPartyPickerGrid(gridApi, api, colApi);
    });
    
    $('#partyPicker').on('shown.bs.modal', function (e) {
    	loadPartyPickerGrid(gridApi, api, colApi);
    	api.sizeColumnsToFit();
	});
    
    $("#partyPicker-refresh-btn").click(function () {
    	loadPartyPickerGrid(gridApi, api, colApi);
    });
    
    $("#cust-refresh").click(function () {
    	loadPartyPickerGrid(gridApi, api, colApi);
    });
     //function to search when enter key is pressed
    $("#findPartyForm").on("keypress", function (event) {
        var keyPressed = event.keyCode || event.which; 
        if (keyPressed === 13) { 
        	loadPartyPickerGrid(gridApi,api,colApi);
            event.preventDefault(); 
            return false; 
        } 
    });
});

function loadPartyPickerGrid(gridApi, api, colApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	api.showLoadingOverlay();
	
	$.ajax({
	  async: true,
	  url:'/common-portal/control/searchPartys',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#findPartyForm").serialize())),
	  success: function(data){
		  gridApi.setRowData(data);
	  }
	});
}
*/


$(function() {
	let partyInstanceId= "PICKER_PARTY_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = partyInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	

	gridInstance = prepareGridInstance(formDataObject);

	$('#partyPicker-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, partyInstanceId, userId);
	});
	$('#partyPicker-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, partyInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getPartyGridData();
		}
	});
	$('#partyPicker-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});

//	$("#partyPicker-refresh-pref-btn").click(function () {
//	gridInstance.refreshCells();
//	});
	$("#partyPicker-search-btn").click(function () {
		getPartyGridData();
	});

	$('#partyPicker').on('shown.bs.modal', function (e) {
		getPartyGridData();
	});

	$("#partyPicker-refresh-btn").click(function () {
		getPartyGridData();
	});

	$("#partyPicker-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$("#cust-refresh").click(function () {
		getPartyGridData();
	});
	//function to search when enter key is pressed
	$("#findPartyForm").on("keypress", function (event) {
		var keyPressed = event.keyCode || event.which; 
		if (keyPressed === 13) { 
			getPartyGridData()
			event.preventDefault(); 
			return false; 
		} 
	});

	function getPartyGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/searchPartys";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#findPartyForm";
		callCtx.ajaxResponseKey = "";

		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getPartyGridData();
	}
});

function pickerPartyList(params){
	return `<a href="#" onclick="setPickerWindowValue('${params.value}', '${params.data.partyId}');">${params.data.partyName}</a>`;
}
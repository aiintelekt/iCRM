var buStatusList = JSON.parse($('#buStatusList').val());
/*
fagReady('ORG_BUSINESS_UNIT',function(el, api, colApi, fag){
    $("#refresh-pref-btn").click(function () {
    	fag.refreshUserPreferences();
    });
    $("#save-pref-btn").click(function () {
    	fag.saveUserPreferences();
    });
    $("#clear-filter-btn").click(function () {
    	try{
    		fag.clearAllColumnFilters();
    	}catch(e){
    	}
    	fag.refreshUserPreferences();
    });
	$("#sub-filter-clear-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    });
    $("#export-btn").click(function () {
    	fag.csvExport();
    });

    $("#update-btn").click(function () {
    	fag.saveUpdates();
        setTimeout(() => {  loadBusinessUnit(fag, api); }, 1000);
    })

    $("#main-search-btn").click(function () {
        loadBusinessUnit(fag, api);
    });
    $("#insert-btn").click(function () {
    	fag.insertNewRow()
    })
    $("#remove-btn").click(function () {
    	fag.removeSelected();
        setTimeout(() => { loadBusinessUnit(fag, api); }, 1000);
        
    });
    $("#searchForm").on("keypress", function (event) {
        var keyPressed = event.keyCode || event.which; 
        if (keyPressed === 13) { 
        	event.preventDefault(); 
        	loadBusinessUnit(fag, api);
            return false; 
        } 
    }); 
    
	loadBusinessUnit(fag, api);
});
*/
$(function() {
	const businessUnitInstanceId= 'ORG_BUSINESS_UNIT';
	const externalLoginKey = $('#externalLoginKey').val();
	const formDataObject = {};
	const userId = $("#userId").val();
	let gridInstance  = "";
	let businessUnitUrl = "";

	formDataObject.gridInstanceId = businessUnitInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	formDataObject.dataFetchCall = getBusinessUnitRowData;

	gridInstance = prepareGridInstance(formDataObject);

	if(businessUnitUrl == ""){
		businessUnitUrl = getGridDataFetchUrl("ORG_BUSINESS_UNIT");
	}

	if(businessUnitUrl == "" || businessUnitUrl == null){
		businessUnitUrl = "/admin-portal/control/getBusinessUnit";
	}

	if(gridInstance){
		getBusinessUnitRowData();
	}
	$('#remove-btn').on('click', function(e) {
		event.preventDefault();
		var selectedData = gridInstance.getSelectedRows();
		var selectedRowData = [];
		if (selectedData && selectedData.length > 0) {
			var productStoreGroupIds = "";
			for (i = 0; i < selectedData.length; i++) {
				var data = selectedData[i];
				productStoreGroupIds += data.productStoreGroupId+",";
			}
			var inputData = { "productStoreGroupId": productStoreGroupIds};
			$.ajax({
				type: "POST",
				url: "/admin-portal/control/removeBusinessUnit",
				async: true,
				data: inputData,
				success: function(result) {
					if (result.responseMessage === "success") {
						showAlert("success", "Successfully removed");
						getBusinessUnitRowData();
						event.preventDefault();
					} else {
						showAlert("error", result.errorMessage);
						event.preventDefault();
					}
				},
				error: function() {
					console.log('Error occurred');
					showAlert("error", "Error occurred!");
				},
				complete: function() {
				}
			});
		} else {
			showAlert("error", "Please select at least one record in the list");
		}
	});
	function getBusinessUnitRowData() {
		const callCtx = {};
		callCtx.ajaxUrl = businessUnitUrl;
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm";
		callCtx.ajaxResponseKey = "";
		setGridData(gridInstance, callCtx);
	}

	$('#business-unit-save-pref').click(function(){
		saveGridPreference(gridInstance, businessUnitInstanceId, userId);
	});
	$("#business-unit-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#business-unit-clear-pref').click(function(){
		clearGridPreference(gridInstance, businessUnitInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getBusinessUnitRowData();
		}
	});

	$('#business-unit-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});

	$("#main-search-btn").click(function () {
		getBusinessUnitRowData();
	});

	$("#searchForm").on("keypress", function (event) {
		let keyPressed = event.keyCode || event.which;
		if(keyPressed === 13) {
			event.preventDefault();
			getBusinessUnitRowData();
			return false;
		}
	});
});

// ag grid custom dropdown implementation start
function extractValues(mappings) {
	return Object.keys(mappings);
}
function lookupValue(mappings, key) {
	return mappings[key];
}
function lookupKey(mappings, name) {
	for ( var key in mappings) {
		if (mappings.hasOwnProperty(key)) {
			if (name === mappings[key]) {
				return key;
			}
		}
	}
}

//ag grid custom dropdown implementation end
/*
var businessUnitUrl = "";
function loadBusinessUnit(gridApi, api) {
	if(businessUnitUrl == ""){
		businessUnitUrl = getGridDataFetchUrl("ORG_BUSINESS_UNIT");
	}
	var rowData =[];
	gridApi.setRowData(rowData);
	if(businessUnitUrl != null && businessUnitUrl != "" && businessUnitUrl !="undefined"){
		api.showLoadingOverlay();
		var formInput = $('#searchForm, #limitForm').serialize();
		$.ajax({
		  async: false,
		  url:businessUnitUrl,
		  type:"POST",
		  data: JSON.parse(JSON.stringify(formInput)),
		  success: function(data){
			  gridApi.setRowData(data);
			  //data.list=[];
			  //paginateHandler(data);
		  }
		});
	}
}
*/
function productStoreGroupName(params){
	return `<a href="viewBusinessUnits?productStoreGroupId=${params.data.productStoreGroupId}">${params.value}</a>`;
}
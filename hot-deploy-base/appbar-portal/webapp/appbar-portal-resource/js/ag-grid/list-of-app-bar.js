$(function() {
	const appBarListInstanceId= 'APP_BAR_LIST';
	const externalLoginKey = $("input[name='externalLoginKey']").val();
	const formDataObject = {};
	const userId = $("input[name='userId']").val();
	let gridInstance  = "";
	let appBarListUrl = "";

	formDataObject.gridInstanceId = appBarListInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	formDataObject.dataFetchCall = getAppBarListRowData;

	gridInstance = prepareGridInstance(formDataObject);

	if(appBarListUrl == ""){
		appBarListUrl = getGridDataFetchUrl("APP_BAR_LIST");
	}

	if(appBarListUrl == "" || appBarListUrl == null){
		appBarListUrl = "getAppBar"
	}

	if(gridInstance){
		getAppBarListRowData();
	}

	function getAppBarListRowData() {
		const callCtx = {};
		callCtx.ajaxUrl = appBarListUrl;
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm";
		callCtx.ajaxResponseKey = "";

		setGridData(gridInstance, callCtx);
	}

	$('#app-bar-list-save-pref').click(function(){
		saveGridPreference(gridInstance, appBarListInstanceId, userId);
	});

	$('#app-bar-list-clear-pref').click(function(){
		clearGridPreference(gridInstance, appBarListInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getAppBarListRowData();
		}
	});

	$('#app-bar-list-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#app-bar-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$("#main-search-btn").click(function () {
		getAppBarListRowData();
	});

	$("#searchForm").on("keypress", function (event) {
		let keyPressed = event.keyCode || event.which;
		if(keyPressed === 13) {
			event.preventDefault();
			getAppBarListRowData();
			return false;
		}
	});
});
function appBarName(params){
	return `<a href="viewAppBar?appBarId=${params.data.appBarId}&appBarTypeId=${params.data.appBarTypeId}">${params.value}</a>` ;
}
/*
fagReady('APP_BAR_LIST',function(el, api, colApi, fag){
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
        setTimeout(() => {  loadMainGrid(fag); }, 1000);
    })

    $("#main-search-btn").click(function () {
        loadMainGrid(fag);
    });
    $("#insert-btn").click(function () {
    	fag.insertNewRow()
    })
    $("#remove-btn").click(function () {
    	fag.removeSelected();
        setTimeout(() => {  loadMainGrid(fag); }, 1000);
        
    });
    $("#searchForm").on("keypress", function (event) {
        var keyPressed = event.keyCode || event.which; 
        if (keyPressed === 13) { 
        	event.preventDefault(); 
        	loadMainGrid(fag); 
            return false; 
        } 
    });
    
	loadMainGrid(fag);
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

function loadMainGrid(fag) {
	fag.setRowData([]);
	$.ajax({
	  async: false,
	  url:'getAppBar',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#searchForm").serialize())),
	  success: function(data){
			// console.log("data: ", data)
		  fag.setRowData(data);
	  }
	});
}
*/
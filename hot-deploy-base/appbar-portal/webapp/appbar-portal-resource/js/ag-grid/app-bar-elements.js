$(function() {
	const appBarElementsInstanceId= 'APP_BAR_ELEMENTS';
	const externalLoginKey = $("input[name='externalLoginKey']").val();
	const formDataObject = {};
	const userId = $("input[name='userId']").val();
	let gridInstance  = "";
	let appBarElementsUrl = "";

	formDataObject.gridInstanceId = appBarElementsInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	formDataObject.dataFetchCall = getAppBarElementsRowData;

	gridInstance = prepareGridInstance(formDataObject);

	if(appBarElementsUrl == ""){
		appBarElementsUrl = getGridDataFetchUrl("APP_BAR_ELEMENTS");
	}

	if(appBarElementsUrl == "" || appBarElementsUrl == null){
		appBarElementsUrl = "getAppBarElements"
	}

	if(gridInstance){
		getAppBarElementsRowData();
	}

	function getAppBarElementsRowData() {
		const callCtx = {};
		callCtx.ajaxUrl = appBarElementsUrl;
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#appBarElementForm";
		callCtx.ajaxResponseKey = "";

		setGridData(gridInstance, callCtx);
	}

	$('#app-bar-elements-save-pref').click(function(){
		saveGridPreference(gridInstance, appBarElementsInstanceId, userId);
	});

	$('#app-bar-elements-clear-pref').click(function(){
		clearGridPreference(gridInstance, appBarElementsInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getAppBarElementsRowData();
		}
	});
	$("#app-bar-elements-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#app-bar-elements-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});

	$("#main-search-btn").click(function () {
		getAppBarElementsRowData();
	});

	$("#appBarElementForm").on("keypress", function (event) {
		let keyPressed = event.keyCode || event.which;
		if(keyPressed === 13) {
			event.preventDefault();
			getAppBarElementsRowData();
			return false;
		}
	});
});


fagReady('APP_BAR_ELEMENTS',function(el, api, colApi, fag){
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
	  url:'getAppBarElements',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#appBarElementForm").serialize())),
	  success: function(data){
			// console.log("data: ", data)
		  fag.setRowData(data);
	  }
	});
}

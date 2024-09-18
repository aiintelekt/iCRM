$(function() {
	const uiLabelListInstanceId= 'UI_LABELS';
	const externalLoginKey = $("input[name='externalLoginKey']").val();
	const formDataObject = {};
	const userId = $("input[name='userId']").val();
	let gridInstance  = "";
	let uiLabelListUrl = "";

	formDataObject.gridInstanceId = uiLabelListInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	formDataObject.dataFetchCall = getUiLabelListRowData;

	gridInstance = prepareGridInstance(formDataObject);

	if(uiLabelListUrl == ""){
		uiLabelListUrl = getGridDataFetchUrl("UI_LABELS");
	}

	if(uiLabelListUrl == "" || uiLabelListUrl == null){
		uiLabelListUrl = "getUiLabels"
	}

	if(gridInstance){
		getUiLabelListRowData();
	}

	function getUiLabelListRowData() {
		const callCtx = {};
		callCtx.ajaxUrl = uiLabelListUrl;
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm";
		callCtx.ajaxResponseKey = "";

		setGridData(gridInstance, callCtx);
	}

	$('#ui-label-list-save-pref').click(function(){
		saveGridPreference(gridInstance, uiLabelListInstanceId, userId);
	});

	$('#ui-label-list-clear-pref').click(function(){
		clearGridPreference(gridInstance, uiLabelListInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getUiLabelListRowData();
		}
	});

	$('#ui-label-list-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#ui-label-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$("#find-btn").click(function () {
		getUiLabelListRowData();
	});

	$("#searchForm").on("keypress", function (event) {
		let keyPressed = event.keyCode || event.which;
		if(keyPressed === 13) {
			event.preventDefault();
			getUiLabelListRowData();
			return false;
		}
	});
});

/*
fagReady('UI_LABELS',function(el, api, colApi, fag){
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

    $("#find-btn").click(function () {
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
	  url:'getUiLabels',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#searchForm").serialize())),
	  success: function(data){
			// console.log("data: ", data)
		  fag.setRowData(data);
	  }
	});
}
*/

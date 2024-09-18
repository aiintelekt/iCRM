$(function() {
	const agGridAccessConfigInstanceId= 'AG_GRID_ACCESS_CONFIG';
	const externalLoginKey = $("input[name='externalLoginKey']").val();
	const formDataObject = {};
	const userId = $("input[name='userId']").val();
	let gridInstance  = "";
	let agGridAccessConfigUrl = "";

	formDataObject.gridInstanceId = agGridAccessConfigInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	formDataObject.dataFetchCall = getAgGridAccessConfigRowData;

	gridInstance = prepareGridInstance(formDataObject);

	if(agGridAccessConfigUrl == ""){
		agGridAccessConfigUrl = getGridDataFetchUrl("AG_GRID_ACCESS_CONFIG");
	}

	if(agGridAccessConfigUrl == "" || agGridAccessConfigUrl == null){
		agGridAccessConfigUrl = "getAllGridUserConfig"
	}

	if(gridInstance){
		getAgGridAccessConfigRowData();
	}

	function getAgGridAccessConfigRowData() {
		const callCtx = {};
		callCtx.ajaxUrl = agGridAccessConfigUrl;
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm";
		callCtx.ajaxResponseKey = "";

		setGridData(gridInstance, callCtx);
	}

	$('#ag-grid-access-config-save-pref').click(function(){
		saveGridPreference(gridInstance, agGridAccessConfigInstanceId, userId);
	});
	$("#ag-grid-access-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#ag-grid-access-config-clear-pref').click(function(){
		clearGridPreference(gridInstance, agGridAccessConfigInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getAgGridAccessConfigRowData();
		}
	});

	$('#ag-grid-access-config-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});

	$("#main-search-btn").click(function () {
		getAgGridAccessConfigRowData();
	});

	$("#searchForm").on("keypress", function (event) {
		let keyPressed = event.keyCode || event.which;
		if(keyPressed === 13) {
			event.preventDefault();
			getAgGridAccessConfigRowData();
			return false;
		}
	});
});
function edit(params){
	 return `<a class="btn btn-xs btn-primary" target="_blank" href="updateAgGridAccessConfig?instanceId=${params.data.instanceId}&groupId=${params.data.groupId}" title="Edit"><i class="fa fa-edit" aria-hidden="true"></i></a>`  ;
}
/*Ag Grid initial configuration*/
/*
fagReady("AG_GRID_ACCESS_CONFIG", function(el, api, colApi, fag){
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
    });

    $("#main-search-btn").click(function () {
        loadMainGrid(fag);
    });
    
    $("#insert-btn").click(function () {
    	fag.insertNewRow()
    });
    
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



function loadMainGrid(grid1) {
	var rowData =[];
	grid1.setRowData(rowData);
	$.ajax({
	  async: false,
	  url:'getAgGridAccessConfig',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#searchForm").serialize())),
	  success: function(data){
			// console.log("data: ", data)
		  grid1.setRowData(data);
	  }
	});
}
*/
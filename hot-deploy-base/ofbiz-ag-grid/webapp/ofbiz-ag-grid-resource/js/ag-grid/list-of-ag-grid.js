$(function() {
	const agGridListInstanceId= 'AG_GRID_LIST';
	const externalLoginKey = $("input[name='externalLoginKey']").val();
	const formDataObject = {};
	const userId = $("input[name='userLoginId']").val();
	let gridInstance  = "";
	let agGridListUrl = "";

	formDataObject.gridInstanceId = agGridListInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	formDataObject.dataFetchCall = getUiLabelListRowData;

	gridInstance = prepareGridInstance(formDataObject);

	if(agGridListUrl == ""){
		agGridListUrl = getGridDataFetchUrl("AG_GRID_LIST");
	}

	if(agGridListUrl == "" || agGridListUrl == null){
		agGridListUrl = "getAllGridUserConfig"
	}

	if(gridInstance){
		getUiLabelListRowData();
	}

	function getUiLabelListRowData() {
		const callCtx = {};
		callCtx.ajaxUrl = agGridListUrl;
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm";
		callCtx.ajaxResponseKey = "list";

		setGridData(gridInstance, callCtx);
	}

	$('#ag-grid-list-save-pref').click(function(){
		saveGridPreference(gridInstance, agGridListInstanceId, userId);
	});

	$('#ag-grid-list-clear-pref').click(function(){
		clearGridPreference(gridInstance, agGridListInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getUiLabelListRowData();
		}
	});

	$('#ag-grid-list-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});

	$("#main-search-btn").click(function () {
		getUiLabelListRowData();
	});
	$("#ag-grid-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
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
function name(params){
 return `<a class="" target="_blank" href="viewAgGrid?instanceId=${params.data.instanceId}&userId=${params.data.userId}&role=${params.data.role}" title="Edit">${params.value}</a>` ;
}
/*
fagReady("AG_GRID_LIST", function(el, api, colApi, gridApi){
    $("#refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#clear-filter-btn").click(function () {
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
    $("#export-btn").click(function () {
    	gridApi.csvExport();
    });

    $("#update-btn").click(function () {
    	gridApi.saveUpdates();
        setTimeout(() => {  loadMainGrid(gridApi, api); }, 1000);
    })

    $("#main-search-btn").click(function () {
    	loadMainGrid(gridApi, api);
    });
    $("#insert-btn").click(function () {
    	gridApi.insertNewRow()
    })
    $("#remove-btn").click(function () {
        //removeMainGrid(fag1, api);
    	gridApi.removeSelected();
        setTimeout(() => {  loadMainGrid(gridApi, api); }, 1000);
        
    });
    
    $("#fetch-previous").click(function () {
    	fetchPrevious();
    	loadMainGrid(gridApi, api);
    });
    $("#fetch-next").click(function () {
    	fetchNext();
    	loadMainGrid(gridApi, api);
    });
    $("#fetch-first").click(function () {
    	fetchFirst();
    	loadMainGrid(gridApi, api);
    });
    $("#fetch-last").click(function () {
    	fetchLast();
    	loadMainGrid(gridApi, api);
    });
    
    $("#goto-page").keyup(function () {
    	if(goto())
    		loadMainGrid(gridApi, api);
    });
    $("#searchForm").on("keypress", function (event) {
        var keyPressed = event.keyCode || event.which; 
        if (keyPressed === 13) { 
        	event.preventDefault(); 
        	loadMainGrid(gridApi, api); 
            return false; 
        } 
    }); 
    
    loadMainGrid(gridApi, api);
});


function loadMainGrid(gridApi, api) {
	var rowData =[];
	gridApi.setRowData(rowData);
	api.showLoadingOverlay();
	var formInput = $('#searchForm, #limitForm').serialize();
	$.ajax({
	  async: false,
	  url:'getAllGridUserConfig',
	  type:"POST",
	  data: JSON.parse(JSON.stringify(formInput)),
	  success: function(data){
		  gridApi.setRowData(data.list);
		  data.list=[];
		  paginateHandler(data);
	  }
	});
}
*/
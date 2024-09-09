//fagReady("API_LOG", function(el, api, colApi, gridApi) {
//    $("#refresh-pref-btn").click(function() {
//        gridApi.refreshUserPreferences();
//    });
//    $("#save-pref-btn").click(function() {
//        gridApi.saveUserPreferences();
//    });
//    $("#clear-filter-btn").click(function() {
//        try {
//            gridApi.clearAllColumnFilters();
//        } catch (e) {}
//        gridApi.refreshUserPreferences();
//    });
//    $("#export-btn").click(function() {
//        gridApi.csvExport();
//    });
//    $("#main-search-btn").click(function() {
//        loadMainGrid(gridApi, api, colApi)
//    });
//    $("#insert-btn").click(function() {
//        gridApi.insertNewRow()
//    });
//    $("#remove-btn").click(function() {
//        //removeMainGrid(fag1, api);
//        gridApi.removeSelected();
//        setTimeout(() => {
//            loadMainGrid(gridApi, api, colApi)
//        }, 1000);
//    });
//    $("#searchForm").on("keypress", function (event) {
//        var keyPressed = event.keyCode || event.which;
//        if (keyPressed === 13) {
//        	loadMainGrid(gridApi, api, colApi);
//            event.preventDefault();
//            return false;
//        }
//    });
//    loadMainGrid(gridApi, api, colApi)
//});
//function loadMainGrid(gridApi,api,colApi) {
//    var rowData = [];
//    gridApi.setRowData(rowData);
//    api.showLoadingOverlay();
//    var formInput = $('#searchForm, #limitForm').serialize();
//    $.ajax({
//        async: false,
//        url: '/admin-portal/control/getApiLogs',
//        type: "POST",
//        data: JSON.parse(JSON.stringify(formInput)),
//        success: function(data) {
//            gridApi.setRowData(data);
//            paginateHandler(data);
//        }
//    });
//}

$(function() {
	let apiLogInstanceId= "API_LOG";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = apiLogInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#api-log-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, apiLogInstanceId, userId);
	});
	$("#api-log-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#api-log-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, apiLogInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getApiLogGridData();
		}
	});
	$('#api-log-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	 $("#main-search-btn").click(function() {
		getApiLogGridData();
	});
	$("#searchForm").on("keypress", function (event) {
	  var keyPressed = event.keyCode || event.which;
	  if (keyPressed === 13) {
		  getApiLogGridData();
	      event.preventDefault();
	      return false;
	  }
	});
	function getApiLogGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/admin-portal/control/getApiLogs";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm, #limitForm_API_LOG";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getApiLogGridData();
	}
});
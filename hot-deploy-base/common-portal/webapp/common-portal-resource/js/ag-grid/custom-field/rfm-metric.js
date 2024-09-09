/*fagReady("RFM_METRIC_LIST", function(el, api, colApi, gridApi){
    $("#rfm-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#rfm-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#rfm-clear-filter-btn").click(function () {
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
    $("#rfm-export-btn").click(function () {
    	gridApi.csvExport();
    });

    $("#rfm-update-btn").click(function () {
    	gridApi.saveUpdates();
        setTimeout(() => {  loadRfmMetricGrid(gridApi, api, colApi); }, 1000);
    })

    $("#find-rfm-metric-btn").click(function () {
    	loadRfmMetricGrid(gridApi, api, colApi);
    	//gridApi.refreshUserPreferences();
    });
    
    //loadRfmMetricGrid(gridApi, api, colApi);
    
    postLoadGrid(api, gridApi, colApi, "rfmMetric", loadRfmMetricGrid);
    postLoadGrid(api, gridApi, colApi, "custome-rfmMetric", loadRfmMetricGrid);
    
});

var findRfmMetricUrl= "";
function loadRfmMetricGrid(gridApi, api, colApi) {
	if(findRfmMetricUrl == ""){
		resetGridStatusBar();
		findRfmMetricUrl = getGridDataFetchUrl("RFM_METRIC_LIST");
	}
	var rowData =[];
	gridApi.setRowData(rowData);
	if(findRfmMetricUrl != null && findRfmMetricUrl != "" && findRfmMetricUrl !="undefined"){
		api.showLoadingOverlay();
		var formInput = $('#find-rfm-metric-form').serialize();
		$.ajax({
		  async: true,
		  url:findRfmMetricUrl,
		  type:"POST",
		  data: JSON.parse(JSON.stringify(formInput)),
		  success: function(data){

			  gridApi.setRowData(data);
			  setTimeout(() => colApi.autoSizeAllColumns(), 1000);
		  }
		});
	}
}*/


$(function() {
	let rfmInstanceId= "RFM_METRIC_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = rfmInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#rfm-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, rfmInstanceId, userId);
	});
	
	
	$('#clearFilterBtnId-rfm').click(function(){
		clearGridPreference(gridInstance, rfmInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getRFMGridData();
		}
	});
	$('#subFltrClearId-rfm').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#exportBtnId-rfm").click(function () {
		gridInstance.exportDataAsCsv();
	});
	
    $("#find-rfm-metric-btn").click(function () {
    	getRFMGridData();
    });

	function getRFMGridData(){
		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("RFM_METRIC_LIST");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#find-rfm-metric-form";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getRFMGridData();
	}
});
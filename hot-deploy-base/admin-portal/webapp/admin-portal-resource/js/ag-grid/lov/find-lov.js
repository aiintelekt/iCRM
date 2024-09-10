$(function() {
	const lovListInstanceId= 'LOV_LIST';
	const externalLoginKey = $("input[name='externalLoginKey']").val();
	const formDataObject = {};
	const userId = $("input[name='userId']").val();
	let gridInstance  = "";
	let lovListUrl = "";

	formDataObject.gridInstanceId = lovListInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	formDataObject.dataFetchCall = getLovListRowData;

	gridInstance = prepareGridInstance(formDataObject);

	if(lovListUrl == ""){
		lovListUrl = getGridDataFetchUrl("LOV_LIST");
	}

	if(lovListUrl == "" || lovListUrl == null){
		lovListUrl = "/admin-portal/control/searchLovs"
	}

	if(gridInstance){
		getLovListRowData();
	}

	function getLovListRowData() {
		const callCtx = {};
		callCtx.ajaxUrl = lovListUrl;
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#lov-search-form";
		callCtx.ajaxResponseKey = "";

		setGridData(gridInstance, callCtx);
	}

	$('#lov-list-save-pref').click(function(){
		saveGridPreference(gridInstance, lovListInstanceId, userId);
	});

	$('#lov-list-clear-pref').click(function(){
		clearGridPreference(gridInstance, lovListInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getLovListRowData();
		}
	});

	$('#lov-list-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#lov-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$("#main-search-btn").click(function () {
		getLovListRowData();
	});

	$("#lov-search-form").on("keypress", function (event) {
		let keyPressed = event.keyCode || event.which;
		if(keyPressed === 13) {
			event.preventDefault();
			getLovListRowData();
			return false;
		}
	});
});
/*
fagReady("LOV_LIST", function(el, api, colApi, gridApi){
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
        setTimeout(() => {  loadMainGrid(gridApi); }, 1000);
    })
    
    $("#lovTypeId").change(function () {
    	loadMainGrid(gridApi);
    });

    $("#main-search-btn").click(function () {
    	loadMainGrid(gridApi);
    });
    
    loadMainGrid(gridApi);
});

function loadMainGrid(gridApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	$.ajax({
	  async: false,
	  url:'/admin-portal/control/searchLovs',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#lov-search-form").serialize())),
	  success: function(data){
		  gridApi.setRowData(data);
	  }
	});
}
*/
function lovIdParams(params){
	return `<a href="/admin-portal/control/viewLov?lovId=${params.data.lovId}&lovTypeId=${params.data.lovTypeId}">${params.data.lovId}</a>`;
}
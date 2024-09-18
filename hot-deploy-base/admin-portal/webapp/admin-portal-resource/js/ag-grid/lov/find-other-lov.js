$(function() {
	const otherLovListInstanceId= 'OTHER_LOV_LIST';
	const externalLoginKey = $("input[name='externalLoginKey']").val();
	const formDataObject = {};
	const userId = $("input[name='userId']").val();
	let gridInstance  = "";
	let otherLovListUrl = "";

	formDataObject.gridInstanceId = otherLovListInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	formDataObject.dataFetchCall = getOtherLovListRowData;

	gridInstance = prepareGridInstance(formDataObject);

	if(otherLovListUrl == ""){
		otherLovListUrl = getGridDataFetchUrl("OTHER_LOV_LIST");
	}

	if(otherLovListUrl == "" || otherLovListUrl == null){
		otherLovListUrl = "/admin-portal/control/searchOtherLovs"
	}

	if(gridInstance){
		getOtherLovListRowData();
	}

	function getOtherLovListRowData() {
		const callCtx = {};
		callCtx.ajaxUrl = otherLovListUrl;
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#lov-search-form";
		callCtx.ajaxResponseKey = "";

		setGridData(gridInstance, callCtx);
	}

	$('#other-lov-list-save-pref').click(function(){
		saveGridPreference(gridInstance, otherLovListInstanceId, userId);
	});

	$('#other-lov-list-clear-pref').click(function(){
		clearGridPreference(gridInstance, otherLovListInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getOtherLovListRowData();
		}
	});

	$('#other-lov-list-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});

	$("#main-search-btn").click(function () {
		getOtherLovListRowData();
	});
	$("#other-lov-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$("#lov-search-form").on("keypress", function (event) {
		let keyPressed = event.keyCode || event.which;
		if(keyPressed === 13) {
			event.preventDefault();
			getOtherLovListRowData();
			return false;
		}
	});
});

/*
fagReady("OTHER_LOV_LIST", function(el, api, colApi, gridApi){
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
    });
    
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
	  url:'/admin-portal/control/searchOtherLovs',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#lov-search-form").serialize())),
	  success: function(data){
		  gridApi.setRowData(data);
	  }
	});
}
*/
function lovIdParams(params){
	return `<a href="/admin-portal/control/viewOtherLov?lovId=${params.data.lovId}&lovTypeId=${params.data.lovTypeId}">${params.data.lovId}</a>`;
}
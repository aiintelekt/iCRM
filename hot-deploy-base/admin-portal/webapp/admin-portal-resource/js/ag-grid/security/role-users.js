$(function() {
	const roleUserInstanceId= 'ROLE_USERS';
	const externalLoginKey = $("input[name='externalLoginKey']").val();
	const formDataObject = {};
	const userId = $("input[name='userId']").val();
	let gridInstance  = "";
	let roleUserUrl = "";

	formDataObject.gridInstanceId = roleUserInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	formDataObject.dataFetchCall = getRoleUserRowData;

	gridInstance = prepareGridInstance(formDataObject);

	if(roleUserUrl == ""){
		roleUserUrl = getGridDataFetchUrl("ROLE_USERS");
	}

	if(roleUserUrl == "" || roleUserUrl == null){
		roleUserUrl = "/admin-portal/control/getRoleUsers"
	}

	if(gridInstance){
		getRoleUserRowData();
	}

	function getRoleUserRowData() {
		const callCtx = {};
		callCtx.ajaxUrl = roleUserUrl;
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm";
		callCtx.ajaxResponseKey = "";

		setGridData(gridInstance, callCtx);
	}

	$('#role-user-save-pref').click(function(){
		saveGridPreference(gridInstance, roleUserInstanceId, userId);
	});
	$("#role-user-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#role-user-clear-pref').click(function(){
		clearGridPreference(gridInstance, roleUserInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getRoleUserRowData();
		}
	});

	$('#role-user-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});

	$("#main-search-btn").click(function () {
		getRoleUserRowData();
	});

	$("#searchForm").on("keypress", function (event) {
		let keyPressed = event.keyCode || event.which;
		if(keyPressed === 13) {
			event.preventDefault();
			getRoleUserRowData();
			return false;
		}
	});
});

/*
fagReady("ROLE_USERS", function(el, api, colApi, gridApi){
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
        setTimeout(() => {  loadRoleUsers(gridApi, api, colApi); }, 1000);
    })

    $("#main-search-btn").click(function () {
    	loadRoleUsers(gridApi, api, colApi);
    });
    
    loadRoleUsers(gridApi, api, colApi);
});

var findRoleUserUrl= "";
function loadRoleUsers(gridApi, api, colApi) {
	if(findRoleUserUrl == ""){
		resetGridStatusBar();
		findRoleUserUrl = getGridDataFetchUrl("ROLE_USERS");
	}
	var rowData =[];
	gridApi.setRowData(rowData);
	if(findRoleUserUrl != null && findRoleUserUrl != "" && findRoleUserUrl !="undefined"){
		api.showLoadingOverlay();
		var formInput = $('#searchForm, #limitForm').serialize();
		$.ajax({
		  async: true,
		  url:findRoleUserUrl,
		  type:"POST",
		  data: JSON.parse(JSON.stringify(formInput)),
		  success: function(data){
			  gridApi.setRowData(data);
			  //data.list=[];
			  //paginateHandler(data);
			  setTimeout(() => colApi.autoSizeAllColumns(), 1000);
		  }
		});
	}
}
*/
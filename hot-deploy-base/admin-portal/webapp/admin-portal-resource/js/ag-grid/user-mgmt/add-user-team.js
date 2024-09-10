//fagReady("ADD_TEAM", function(el, api, colApi, gridApi){
//    $("#add-team-ref-pref").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#add-team-save-pref").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#add-team-clear-filter").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//	$("#sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#add-team-export").click(function () {
//    	gridApi.csvExport();
//    });
    /*
    $("#update-btn").click(function () {
    	gridApi.saveUpdates();
        setTimeout(() => {  loadAddUserTeam(gridApi, api, colApi); }, 1000);
    })

    $("#main-search-btn").click(function () {
    	loadAddUserTeam(gridApi,api);
    });
     */
//    $("#add-team-btn").click(function () {
//    	var selectedRows = api.getSelectedRows();
//    	 if(selectedRows.length>0){
//    		 var rows = JSON.stringify(selectedRows);
//    		 $("#selectedRows").val(rows);
//    		 $("#addTeamToUserForm").submit();
//          } else {
//             showAlert("error","Please select the team");
//          }
//    });
//    $("#add-submit-btn").click(function() {
//    	loadAddUserTeam(gridApi, api, colApi);
//    });
//    $("#addTeamToUser").on("show.bs.modal", function(e) {
// 	   var isNative = $(e.relatedTarget).data('is-native');
// 	   $('#isNative').val(isNative);
// 	  loadAddUserTeam(gridApi, api, colApi);
// 	});
    //loadAddUserTeam(gridApi, api, colApi);
//});

//var addTeamUrl = "";
//function loadAddUserTeam(gridApi, api, colApi) {
//	if(addTeamUrl == ""){
//		resetGridStatusBar();
//		addTeamUrl = getGridDataFetchUrl("ADD_TEAM");
//	}
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(addTeamUrl != null && addTeamUrl != "" && addTeamUrl !="undefined"){
//		api.showLoadingOverlay();
//		var formInput = $('#addTeamToUserForm, #limitForm').serialize();
//		$.ajax({
//		  async: false,
//		  url:addTeamUrl,
//		  type: "POST",
//		  data: JSON.parse(JSON.stringify(formInput)),
//		  success: function(data){
//			  gridApi.setRowData(data.list);
//			  setTimeout(() => colApi.autoSizeAllColumns(), 1000);
//			  //data.list=[];
//			  //paginateHandler(data);
//		  }
//		});
//	}
//}

$(function() {
	let addTeamInstanceId= "ADD_TEAM";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = addTeamInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	

	gridInstance = prepareGridInstance(formDataObject);

	$('#add-team-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, addTeamInstanceId, userId);
	});
	$("#add-team-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#add-team-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, addTeamInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getAddTeamListGridData();
		}
	});
	$('#add-team-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#add-team-btn").click(function () {
		var selectedRows = gridInstance.getSelectedRows();
		if(selectedRows.length>0){
			var rows = JSON.stringify(selectedRows);
			$("#selectedRows").val(rows);
			$("#addTeamToUserForm").submit();
		} else {
			showAlert("error","Please select the team");
		}
	});
	$("#add-submit-btn").click(function() {
		getAddTeamListGridData();
	});
	$("#addTeamToUser").on("show.bs.modal", function(e) {
		var isNative = $(e.relatedTarget).data('is-native');
		$('#isNative').val(isNative);
		getAddTeamListGridData();
	});
	function getAddTeamListGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("ADD_TEAM");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#addTeamToUserForm, #limitForm_ADD_TEAM";
		callCtx.ajaxResponseKey = "list";

		setGridData(gridInstance, callCtx);
		setTimeout(() => gridInstance.autoSizeAllColumns(true), 1000);
	}
	if(gridInstance){
		getAddTeamListGridData();
	}
});
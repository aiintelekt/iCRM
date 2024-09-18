//fagReady('ORG_USERS_LIST',function(el, api, colApi, fag){
//    
//    $("#add-member-btn").click(function (event) {
//    	event.preventDefault();
//    	var selectedRows = api.getSelectedRows();
//    	 if(selectedRows){
//    		 var rows = JSON.stringify(selectedRows);
//    		 $("#selectedRows").val(rows);
//    		 $("#addMembersToTeamForm").submit();
//          } else {
//             showAlert("error","Please select atleast one record in the list")
//          }
//    });
//    
//	$("#sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    loadMembersList(fag, api);
//});


//var membersUrl = "";
//function loadMembersList(gridApi, api) {
//	
//	var emplTeamId = $("#searchForm #emplTeamId").val();
//	
//	if(membersUrl == ""){
//		membersUrl = getGridDataFetchUrl("ORG_USERS_LIST");
//	}
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(membersUrl != null && membersUrl != "" && membersUrl !="undefined"){
//		api.showLoadingOverlay();
//		$.ajax({
//		  async: false,
//		  url:membersUrl,
//		  type:"POST",
//		  data: { "status": "Y", "roleTypeId": "SALES_REP,CUST_SERVICE_REP", "emplTeamId": emplTeamId },
//		  success: function(data){
//			  gridApi.setRowData(data);
//			  //data.list=[];
//			  //paginateHandler(data);
//		  }
//		});
//	}
//}


$(function() {
	let teamMemberListInstanceId= "ORG_USERS_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = teamMemberListInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#members-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, teamMemberListInstanceId, userId);
	});
	$('#members-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, teamMemberListInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getTeamMemberListGridData();
		}
	});
	$('#members-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#members-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$("#add-member-btn").click(function (event) {
    	event.preventDefault();
    	var selectedRows = gridInstance.getSelectedRows();
    	 if(selectedRows){
    		 var rows = JSON.stringify(selectedRows);
    		 $("#selectedRows").val(rows);
    		 $("#addMembersToTeamForm").submit();
          } else {
             showAlert("error","Please select atleast one record in the list")
          }
    });
	function getTeamMemberListGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("ORG_USERS_LIST");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#addMembersTeamForm";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getTeamMemberListGridData();
	}
});

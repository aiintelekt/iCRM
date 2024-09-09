/*
$("#loader").show();
$(function() {
	addRolesAgGrid();
});

var columnDefsAddRoles = [{ 
		"headerName": "Role",
		"field": "description",
		"sortable": true,
		"filter": true,
		"filter":"agTextColumnFilter",
		"checkboxSelection":true
	}];
var gridOptionsAddRole = null;

function addRolesAgGrid(){
	$("#addRolesGrid").empty();
	gridOptionsAddRole = {
			defaultColDef: {
				filter: true,
				sortable: true,
				resizable: true,
				// allow every column to be aggregated
				//enableValue: true,
				// allow every column to be grouped
				//enableRowGroup: true,
				// allow every column to be pivoted
				//enablePivot: true,
			},
			columnDefs: columnDefsAddRoles,
			rowData: addRolesGridData(),
			floatingFilter: true,
			//rowSelection: "multiple",
			editType: "fullRow",
			paginationPageSize: 10,
			domLayout:"autoHeight",
			pagination: true,
			onGridReady: function() {
				sizeToFitAddRole();
			}
	};

	//lookup the container we want the Grid to use
	var eGridDiv = document.querySelector("#addRolesGrid");

	// create the grid passing in the div to use together with the columns & data we want to use
	new agGrid.Grid(eGridDiv, gridOptionsAddRole);

}
function addRolesGridData() {
	var resultRes = null;
	var params = {}
	$.ajax({
		type: "POST",
		url: "getRoles",
		async: false,
		data:  { "parentTypeId": "DBS_ROLE" },
		success: function(data) {
			resultRes = data;
		}
	});
	return resultRes;
}
$("#loader").hide();
//data binding while loading the page
function onFirstDataRenderedRoles(params) {
	params.api.sizeColumnsToFit();
}
function clearData() {
	gridOptionsAddRole.api.setRowData([]);
}

function onAddRow() {
	var newItem = createNewRowData();
	var res = gridOptionsAddRole.api.updateRowData({
		add: [newItem]
	});
	printResult(res);
}

function addItems() {
	var newItems = [createNewRowData(), createNewRowData(), createNewRowData()];
	var res = gridOptionsAddRole.api.updateRowData({
		add: newItems
	});
	printResult(res);
}

function addItemsAtIndex() {
	var newItems = [createNewRowData(), createNewRowData(), createNewRowData()];
	var res = gridOptionsAddRole.api.updateRowData({
		add: newItems,
		addIndex: 2
	});
	printResult(res);
}

function updateItems() {
	// update the first 5 items
	var itemsToUpdate = [];
	gridOptionsAddRole.api.forEachNodeAfterFilterAndSort(function(rowNode, index) {
		// only do first 5
		if (index >= 5) {
			return;
		}

		var data = rowNode.data;
		data.price = Math.floor((Math.random() * 20000) + 20000);
		itemsToUpdate.push(data);
	});
	var res = gridOptionsAddRole.api.updateRowData({
		update: itemsToUpdate
	});
	printResult(res);
}

function onInsertRowAt2() {
	var newItem = createNewRowData();
	var res = gridOptionsAddRole.api.updateRowData({
		add: [newItem],
		addIndex: 2
	});
	printResult(res);
}

function onRemoveSelected() {
	var selectedData = gridOptionsAddRole.api.getSelectedRows();
	console.log(selectedData.length);
	for (i = 0; i <= selectedData.length; i++) {
	}
	var res = gridOptionsAddRole.api.updateRowData({
		remove: selectedData
	});

	printResult(res);
}

function removeSubmit() {
	var selectedData = gridOptionsAddRole.api.getSelectedRows();
}

function printResult(res) {
	console.log('---------------------------------------')
	if (res.add) {
		res.add.forEach(function(rowNode) {
			console.log('Added Row Node', rowNode);
		});
	}
	if (res.remove) {
		res.remove.forEach(function(rowNode) {
			console.log('Removed Row Node', rowNode);
		});
	}
}

function sizeToFitAddRole() {
	gridOptionsAddRole.api.sizeColumnsToFit();
}

function getSelectedUserRolesRows() {
	var selectedRows;
	selectedRows = this.gridOptionsAddRole.api.getSelectedRows();
	selectedRows.map((row)=>{
	});
	return selectedRows
}

function onBtExport() {
	var params = {
			skipHeader: false,
			allColumns: true,
			fileName: "User_Roles",
			exportMode: 'csv'
	};


	gridOptionsAddRole.api.exportDataAsExcel(params);
}
*/

//fagReady("ADD_ROLE", function(el, api, colApi, gridApi){
//    $("#add-role-ref-pref").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#add-rolesave-pref").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#add-role-clear-filter").click(function () {
//    	gridApi.clearAllColumnFilters();
//    });
//	$("#sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#add-role-export").click(function () {
//    	gridApi.csvExport();
//    });
//    /*
//    $("#update-btn").click(function () {
//    	gridApi.saveUpdates();
//        setTimeout(() => {  loadAddUserRole(gridApi, api); }, 1000);
//    })
//
//    $("#main-search-btn").click(function () {
//    	loadAddUserRole(gridApi,api);
//    });
//     */
//    $("#add-role-btn").click(function () {
//    	var selectedRows = api.getSelectedRows();
//    	 if(selectedRows){
//    		 var rows = JSON.stringify(selectedRows);
//    		 $("#selectedRowsUser").val(rows);
//    		 $("#addRoleToUserForm").submit();
//          } else {
//             showAlert("error","Please select atleast one record in the list")
//          }
//    });
//    loadAddUserRole(gridApi, api);
//});
//
//var addRoleUrl = "";
//function loadAddUserRole(gridApi, api) {
//	if(addRoleUrl == ""){
//		resetGridStatusBar();
//		addRoleUrl = getGridDataFetchUrl("ADD_ROLE");
//	}
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(addRoleUrl != null && addRoleUrl != "" && addRoleUrl !="undefined"){
//		api.showLoadingOverlay();
//		var formInput = $('#searchForm, #limitForm').serialize();
//		$.ajax({
//		  async: false,
//		  url:addRoleUrl,
//		  type:"POST",
//		  data:  { "parentTypeId": "SECURITY_ROLE" },
//		  success: function(data){
//			  gridApi.setRowData(data);
//			  //data.list=[];
//			  //paginateHandler(data);
//		  }
//		});
//	}
//}

$(function() {
	let addRoleInstanceId= "ADD_ROLE";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = addRoleInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#add-role-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, addRoleInstanceId, userId);
	});
	$("#add-role-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#add-role-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, addRoleInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getAddRoleListGridData();
		}
	});
	$('#add-role-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	  $("#add-role-btn").click(function () {
		var selectedRows = gridInstance.getSelectedRows();
		 if(selectedRows){
			 var rows = JSON.stringify(selectedRows);
			 $("#selectedRowsUser").val(rows);
			 $("#addRoleToUserForm").submit();
	      } else {
	         showAlert("error","Please select atleast one record in the list")
	      }
	});
	function getAddRoleListGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("ADD_ROLE");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#addRoleToUserForm";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getAddRoleListGridData();
	}
});
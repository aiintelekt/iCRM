//fagReady("USER_ROLE",function(el, api, colApi, gridApi){
//	$("#refresh-pref-btn").click(function(){
//		gridApi.refreshUserPreferences();
//	});
//	$("#save-pref-btn").click(function(){
//		gridApi.saveUserPreferences();
//	});
//	$("#clear-filter-btn").click(function(){
//		try{
//			gridApi.clearAllColumnFilters();
//		}catch(e){}
//		gridApi.refreshUserPreferences();
//	});
//	$("#sub-filter-clear-btn").click(function(){
//		try{
//			gridApi.clearAllColumnFilters();
//		}catch(e){}
//	});
//	$("#export-btn").click(function(){
//		gridApi.csvExport();
//	});
//	$("#update-btn").click(function(){
//		gridApi.saveUpdates();
//		setTimeout(() =>{
//			loadUser(gridApi, api);
//		}, 1000);
//	})
//	$("#main-search-btn").click(function(){
//		loadUser(gridApi, api);
//	});
//	$("#reset-party").click(function(){
//		loadUser(gridApi, api);
//	});
//	$("#searchForm").on("keypress", function(event){
//		var keyPressed = event.keyCode || event.which;
//		if (keyPressed === 13){
//			event.preventDefault();
//			loadUser(gridApi, api);
//			return false;
//		}
//	});
//	loadUser(gridApi, api);
//});
///*
//function loadUser(gridApi){
//	var rowData=[];
//	gridApi.setRowData(rowData);
//	$.ajax({
//		async:false,
//		url:'/admin-portal/control/getUsers',
//		type:"POST",
//		data:JSON.parse(JSON.stringify($("#searchForm").serialize())),
//		success:function(data){
//			gridApi.setRowData(data);
//		}
//	});
//}
//*/
//var usersUrl = "";
//
//function loadUser(gridApi, api){
//	if(usersUrl == ""){
//		resetGridStatusBar();
//		usersUrl=getGridDataFetchUrl("USER_ROLE");
//	}
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(usersUrl != null && usersUrl != "" && usersUrl != "undefined"){
//		api.showLoadingOverlay();
//		var formInput = $('#searchForm, #limitForm').serialize();
//		$.ajax({
//			async:false,
//			url:usersUrl,
//			type:"POST",
//			data: JSON.parse(JSON.stringify(formInput)),
//			success:function(data){
//				gridApi.setRowData(data);
//				//data.list=[];
//				//paginateHandler(data);
//			}
//		});
//	}
//}

$(function() {
	let userRoleInstanceId= "USER_ROLE";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = userRoleInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#user-role-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, userRoleInstanceId, userId);
	});
	$("#user-role-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#user-role-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, userRoleInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getUserRoleListGridData();
		}
	});
	$('#user-role-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#user-role-search-btn").click(function () {
		getUserRoleListGridData();
	});
	$("#main-search-btn").click(function(){
		getUserRoleListGridData();
	});
	$("#reset-party").click(function(){
		getUserRoleListGridData();
	});
	$("#searchForm").on("keypress", function(event){
		var keyPressed = event.keyCode || event.which;
		if (keyPressed === 13){
			event.preventDefault();
			getUserRoleListGridData();
			return false;
		}
	});
	function getUserRoleListGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("USER_ROLE");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm, #limitForm_USER_ROLE";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getUserRoleListGridData();
	}
});
function userName(params){
	return `<a href="viewUserDetail?userLoginId=${params.data.oneBankId}">${params.value}</a>` ;
}
/*
$("#loader").show();
$(function(){
	loadAgGrid();
});

var columnDefs =[
	{
		"headerName":"User Name ",
		"field":"userName",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter",
		"cellRenderer": params => `<a href="viewUserDetail?userLoginId=${params.data.oneBankId}">${params.value}</a>`
	},
	{
		"headerName":"1BankID",
		"field":"oneBankId",
		"sortable":true,
		"filter":true
	},
	{
		"headerName":"Business Unit",
		"field":"businessUnit",
		"sortable":true,
		"filter":true
	},
	{
		"headerName":"User Status",
		"field":"userStatus",
		"sortable":true,
		"filter":true
	}
];

function loadAgGrid() {
	$("#grid1").empty();
	var gridOptions = {
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
		columnDefs: columnDefs,
		rowData: getGridData(),
		floatingFilter: true,
		rowSelection: "multiple",
		editType: "fullRow",
		paginationPageSize: 10,
		domLayout: "autoHeight",
		pagination: true,
		onFirstDataRendered: onFirstDataRendered
	};
	//lookup the container we want the Grid to use
	var eGridDiv = document.querySelector("#grid1");

	// create the grid passing in the div to use together with the columns & data we want to use
	new agGrid.Grid(eGridDiv, gridOptions);

}
function getGridData(){
	var result = [];
	var resultRes = null;
	var params = {}
	var paramStr = $("#searchForm").serialize();

	console.log("formData--->"+JSON.stringify(paramStr));
	var fromData = JSON.stringify(paramStr);
	$.ajax({
		type: "POST",
		url: "getUsers",
		async: false,
		data: JSON.parse(fromData),
		success: function(data){
			resultRes = data;
			result.push(data);
			console.log("--result-----" + result);
		}
	});
	return resultRes;
}
$("#loader").hide();
//data binding while loading the page
function onFirstDataRendered(params){
	params.api.sizeColumnsToFit();
}
function clearData(){
	gridOptions.api.setRowData([]);
}

function onAddRow(){
	var newItem = createNewRowData();
	var res = gridOptions.api.updateRowData({
		add: [newItem]
	});
	printResult(res);
}

function addItems(){
	var newItems = [createNewRowData(), createNewRowData(), createNewRowData()];
	var res = gridOptions.api.updateRowData({
		add: newItems
	});
	printResult(res);
}

function addItemsAtIndex(){
	var newItems = [createNewRowData(), createNewRowData(), createNewRowData()];
	var res = gridOptions.api.updateRowData({
		add: newItems,
		addIndex: 2
	});
	printResult(res);
}

function updateItems(){
	// update the first 5 items
	var itemsToUpdate = [];
	gridOptions.api.forEachNodeAfterFilterAndSort(function(rowNode, index){
		// only do first 5
		if (index >= 5){
			return;
		}
		var data = rowNode.data;
		data.price = Math.floor((Math.random() * 20000) + 20000);
		itemsToUpdate.push(data);
	});
	var res = gridOptions.api.updateRowData({
		update: itemsToUpdate
	});
	printResult(res);
}

function onInsertRowAt2(){
	var newItem = createNewRowData();
	var res = gridOptions.api.updateRowData({
		add: [newItem],
		addIndex: 2
	});
	printResult(res);
}

function onRemoveSelected(){
	var selectedData = gridOptions.api.getSelectedRows();
	console.log(selectedData.length);
	for(i = 0; i <= selectedData.length; i++){
	}
	var res = gridOptions.api.updateRowData({
		remove: selectedData
	});
	printResult(res);
}

function removeSubmit(){
	var selectedData = gridOptions.api.getSelectedRows();
}

function printResult(res){
	console.log('---------------------------------------')
	if (res.add){
		res.add.forEach(function(rowNode){
		console.log('Added Row Node',rowNode);
		});
	}
	if (res.remove){
		res.remove.forEach(function(rowNode){
		console.log('Removed Row Node',rowNode);
		});
	}
}
function sizeToFit(){
	gridOptions.api.sizeColumnsToFit();
}

function getSelectedRows(){
	const selectedNodes = gridOptions.api.getSelectedNodes()
	const selectedData = selectedNodes.map(function(node){
		return node.data
	})
	const selectedDataStringPresentation = selectedData.map(function(node){
		return node.Owner + ' ' + node.Date_Due
	}).join(',')
	alert('Selected nodes: ' + selectedDataStringPresentation);
}

function onBtExport(){
	var params ={
	skipHeader:false,
	allColumns:true,
	fileName:"Users_And_Roles",
	exportMode:'csv'
	};
	gridOptions.api.exportDataAsCsv(params);
}
*/
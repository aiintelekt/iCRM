$(function() {
	loadAgGrid();
});
var columnDefsCallOutcome = [
	{
		"headerName": "Opportunity Call Outcome Description",
		"field": "description",
		"sortable": true,
		"filter": true,
		width:30,
		"cellRenderer":params => `<a href="editOpportunityCallOutCome?enumId=${params.data.enumId}">${params.value}</a>`
	},
	{
		"headerName": "Created On",
		"field": "createdOn",
		"sortable": true,
		"filter": true,
		width:15,
		resetButton:true
	},
	{
		"headerName": "Modified On",
		"field": "modifiedOn",
		"sortable": true,
		"filter": true,
		width:15
	}
	];
var gridOptions = null;
function loadAgGrid(){
	$("#gridCalloutcome").empty();
	gridOptions = {			
			columnDefs: columnDefsCallOutcome,
		
			rowData: getGridData(),
			floatingFilter: true,
			rowSelection: "multiple",
			editType: "fullRow",
			paginationPageSize: 10,
			domLayout:"autoHeight",
			pagination: true,
			onFirstDataRendered: onFirstDataRendered
	};

	//lookup the container we want the Grid to use
	var eGridDiv = document.querySelector("#gridCalloutcome");

	// create the grid passing in the div to use together with the columns & data we want to use
	new agGrid.Grid(eGridDiv, gridOptions);

}
function getGridData() {
	var result = [];
	var resultRes = null;
	var params = {}
	var paramStr = $("#searchForm").serialize();	
	var fromData = JSON.stringify(paramStr);
	$.ajax({
		type: "POST",
		url: "getOpportunityConfigData",
		async: false,
		data: JSON.parse(fromData),
		success: function(data) {
			resultRes = data;
			result.push(data);			
		}
	});
	return resultRes;
}
//data binding while loading the page
function onFirstDataRendered(params) {
	params.api.sizeColumnsToFit();
}
function clearData() {
	gridOptions.api.setRowData([]);
}

function onAddRow() {
	var newItem = createNewRowData();
	var res = gridOptions.api.updateRowData({
		add: [newItem]
	});
	printResult(res);
}

function addItems() {
	var newItems = [createNewRowData(), createNewRowData(), createNewRowData()];
	var res = gridOptions.api.updateRowData({
		add: newItems
	});
	printResult(res);
}

function addItemsAtIndex() {
	var newItems = [createNewRowData(), createNewRowData(), createNewRowData()];
	var res = gridOptions.api.updateRowData({
		add: newItems,
		addIndex: 2
	});
	printResult(res);
}

function updateItems() {
	// update the first 5 items
	var itemsToUpdate = [];
	gridOptions.api.forEachNodeAfterFilterAndSort(function(rowNode, index) {
		// only do first 5
		if (index >= 5) {
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

function onInsertRowAt2() {
	var newItem = createNewRowData();
	var res = gridOptions.api.updateRowData({
		add: [newItem],
		addIndex: 2
	});
	printResult(res);
}

function onRemoveSelected() {
	var selectedData = gridOptions.api.getSelectedRows();
	console.log(selectedData.length);
	for (i = 0; i <= selectedData.length; i++) {
	}
	var res = gridOptions.api.updateRowData({
		remove: selectedData
	});

	printResult(res);
}

function removeSubmit() {
	var selectedData = gridOptions.api.getSelectedRows();
}

function printResult(res) {
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




function sizeToFit() {
	gridOptions.api.sizeColumnsToFit();
}

function getSelectedRows() {
	const selectedNodes = gridOptions.api.getSelectedNodes()
	const selectedData = selectedNodes.map(function(node) {
		return node.data
	})
	const selectedDataStringPresentation = selectedData.map(function(node) {
		return node.Owner + ' ' + node.Date_Due
	}).join(', ')
	alert('Selected nodes: ' + selectedDataStringPresentation);
}

function onBtExport() {
	var params = {
			skipHeader: false,
			allColumns: true,
			fileName: "Opportunity Call Outcome List",
			exportMode: 'csv'
	};


	gridOptions.api.exportDataAsCsv(params);
}
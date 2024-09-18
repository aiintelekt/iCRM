
$(function() {
	getcount();
	loadAgGridMyCell();
	
	$('#reassignModal #close').click(function(){
		$('#reassignModal input[type=reset]').click();
	});
	$('#reassignModal #btnclose').click(function(){
		$('#reassignModal input[type=reset]').click();
	});

});
var searchSeleted="";

$("a.dropdown-item").click(function(){ 
	searchSeleted = $(this).attr('id');
	loadAgGridMyCell();


}); 

var columnDefs = [
	{ 
		"headerName":"Opportunity / Activity Number",
		"field":"salesOpportunityId",
		checkboxSelection:true,
		width: 240,
	    suppressAutoSize:true,
		cellRenderer : function(params){
			return `<a href="viewOpportunity?salesOpportunityId=${params.data.salesOpportunityId}">${params.value}</a>` 
		}
	},
	{ 
		"headerName":"Call",
		"field":"call",
		cellRenderer : function(params){
			return '<a  class = "fa fa-phone fa-1" href="#"></a>'
		}  
	},
	{
		"headerName" : "Phone #",
		"field" : "phoneNumber"

	},  
	{
		"headerName" : "CallBack Date",
		"field" : "callBackDate"
	}, 
	{
		"headerName" : "Customer Name",
		"field" : "firstName",
		cellRenderer: 
			function sumField(params) {
			return params.data.firstName + params.data.middleName + params.data.lastName;
		}
	},

	{
		"headerName" : "Customer CIN",
		"field" : "externalReferenceId"
	}, 
	{
		"headerName" : "Customer CIN Suffix",
		"field" : "customerSuffix"
	}, 
	{
		"headerName" : "Prospect Name",
		"field" : "prospectName"
	}, 
	{
		"headerName" : "Prospect CIN",
		"field" : "prospectPartyId"
	},
	{
		"headerName":"# Attempts",
		"field":"totalCallsByCamp"
	},
	{
		"headerName":"Outcome",
		"field":"callOutCome"
	},
	{
		"headerName":"Call Status",
		"field":"lastCallStatusId"
	},
	{
		"headerName":"Campaign Name",
		"field":"marketingCampaignName"
	},
	{
		"headerName":"Campaign Code",
		"field":"marketingCampaignCode"
	},
	{
		"headerName":"Start Date",
		"field":"startDate"
	},
	{
		"headerName":"End Date",
		"field":"endDate"
	},
	{
		"headerName":"Days Since Called",
		"field":"lastContactDate"
	},
	{
		"headerName":"Owner",
		"field":"ownerName"
	},
	{
		"headerName":"Team",
		"field":"emplTeamId"
	},
	{
		"headerName":"Business Unit",
		"field":"businessUnitName"
	},
	{
		"headerName":"Created Date",
		"field":"createdDate"
	},         
	{
		"headerName":"Planned Due",
		"field":"plannedDueDate"
	},
	{
		"headerName":"Subject",
		"field":"subject"
	},
	{
		"headerName":"Instruction/Comments",
		"field":""
	},
	{
		"headerName":"CRM Activity",
		"field":"workEffortTypeId"
	},
	{
		"headerName":"Activity Type",
		"field":"callActivityType"
	},
	{
		"headerName":"Activity Sub Type",
		"field":"callSubActivityId"
	},
	{
		"headerName":"Created By",
		"field":"createdBy"
	},
	{
		"headerName":"Last Updated",
		"field":"lastUpdatedTxStamp"
	},
	{
		"headerName":"Account Number",
		"field":"accountId"
	},
	{
		"headerName":"Regarding ID",
		"field":"regardingId"
	}


	];

var gridOptions = null;

function loadAgGridMyCell(){
	$("#myCellGrid").empty();
	gridOptions = {
			defaultColDef: {
				filter: true,
				sortable: true,
				resizable: true
			},
			columnDefs: columnDefs,
			//rowData: getRowData(),
			floatingFilter: true,
			rowSelection: "single",
			editType: "fullRow",
			paginationPageSize: 10,
			domLayout:"autoHeight",
			pagination: true,
			onGridReady: function() {
				getRowData();
			}
	}

	// lookup the container we want the Grid to use
	var eGridDiv = document.querySelector("#myCellGrid");
	// create the grid passing in the div to use together with the columns &
	// data we want to use
	new agGrid.Grid(eGridDiv, gridOptions);

}

function getAjaxResponse(callback) {
	var data1;
	var result = [];
	var resultRes = null;
	var params = {}
	var paramStr="";
	var fromData ="";

	if(searchSeleted ==""){
		paramStr= $("#teleSalesForm").serialize();
	}else{
		paramStr={"searchData": searchSeleted};
		searchSeleted="";
	}
	var fromData = JSON.stringify(paramStr);
	var errorMessage = null;
	var resultData = null;
	
	$.ajax({
		type: "POST",
		url: "getTeleSales",
		async: false,
		data: JSON.parse(fromData),
		success: function(data) {
			var result1 = data[0];
			if(data[0] != null || data[0] != undefined){
				errorMessage = data[0].errorMessage;
				resultData = data[0].errorResult;
			}
			if(errorMessage != null || errorMessage != undefined) {
				showAlert("error", errorMessage);
				callback(resultData);
			}else{
				callback(data.data);
			}
		},
		error: function() {
			console.log('Error occured');
			showAlert("error", "Error occured!");
			callback(result);
		},
		complete: function() {
			//$('#loader').hide();
		}
	});
}

function getRowData() {
	var result;
	result = getAjaxResponse(function(agdata) {
		gridOptions.api.setRowData(agdata);

	});
}

function sizeToFit() {
	gridOptions.api.sizeColumnsToFit();
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
	var selectedRows;
	selectedRows = this.gridOptions.api.getSelectedRows();
	selectedRows.map((row)=>{
	});
	return selectedRows
}

function onBtExport() {
	var params = {
			skipHeader: false,
			allColumns: true,
			fileName: "TeleSales List",
			exportMode: 'csv'
	};
	gridOptions.api.exportDataAsCsv(params);
}

function getcount(){
	$.ajax
	({
		url:"getMyCall",
		success: function(data) {
			var ownerCount =  data["ownerCount"];
			var ownerCountToday =  data["ownerCountToday"];
			var ownerTeamCount =  data["ownerTeamCount"];
			var teamCountToday =  data["teamCountToday"];

			if(ownerCount == null || ownerCount == undefined || ownerCount == ""){
				ownerCount = 0;
			}
			if(ownerCountToday == null || ownerCountToday == undefined || ownerCountToday == ""){
				ownerCountToday = 0;
			}
			if(ownerTeamCount == null || ownerTeamCount == undefined || ownerTeamCount == ""){
				ownerTeamCount = 0;
			}
			if(teamCountToday == null || teamCountToday == undefined || teamCountToday == ""){
				teamCountToday = 0;
			}

			document.getElementById("myCall").innerHTML = ownerCount;  
			document.getElementById("ownerCountToday").innerHTML = ownerCountToday; 
			document.getElementById("ownerTeamCount").innerHTML = ownerTeamCount; 
			document.getElementById("teamCountToday").innerHTML = teamCountToday;                    
		}
	});
}
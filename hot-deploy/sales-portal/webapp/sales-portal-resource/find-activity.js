$("#loader1").show();
$(function() {
	loadActivityGrid();
	$('#reassignModal #close').click(function(){
		$('#reassignModal input[type=reset]').click();
	});
	$('#reassignModal #btnclose').click(function(){
		$('#reassignModal input[type=reset]').click();
	});
});

var columnDefs = [{ 
	   "headerName":"Activity Number",
	   "field":"workEffortId",
	   "sortable":true,
	   "filter":true,
	   "filter":"agTextColumnFilter",
	   "checkboxSelection":true,
	   width: 240,
	   suppressAutoSize:true,
	   cellRenderer: function(params) {
        	return '<a href="viewActivity?workEffortId=' + params.data.workEffortId + '&seqId=' + params.data.workEffortId + '&entity=WorkEffortCallSummary" target="">' + params.data.workEffortId + '</a>'
        	}
    },
	{ 
	   "headerName":"Parent Activity",
	   "field":"workEffortParentId",
	   "sortable":true,
	   "filter":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Activity Type",
		"field":"workEffortServiceTypeDescription",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Activity Sub Type",
		"field":"workEffortSubServiceTypeDescription",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Subject",
		"field":"description",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Account #",
		"field":"accountNumber",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},

	{ 
		"headerName":"Business Unit Id",
		"field":"businessUnitId",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Business Unit",
		"field":"businessUnitName",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{
		"headerName":"Team",
		"field":"emplTeamId"
	},
	{
		"headerName":"Empl Team",
		"field":"emplTeamName"
	},
	{ 
		"headerName":"Planned Start",
		"field":"estimatedStartDate",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Campaign code",
		"field":"campaignCode",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
				   
	},
	{ 
		"headerName":"Product Name",
		"field":"productName",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Planned Duration",
		"field":"plannedDuration",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Planned Due",
		"field":"estimatedCompletionDate",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Actual Completion",
		"field":"actualCompletionDate",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Owner",
		"field":"primOwnerName",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Activity Status",
		"field":"statusDesc",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Once and Done",
		"field":"wfOnceDone",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Created by from Iserve",
		"field":"createdSourceBy",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Last updated by from Iserve",
		"field":"lastUpdatedSource",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Resolution",
		"field":"resolution",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Actual Start",
		"field":"actualStartDate",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Actual Duration",
		"field":"actualDuration",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Created By",
		"field":"createdByUserLogin",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Modified Date",
		"field":"lastModifiedDate",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Modifed By",
		"field":"lastModifiedByUserLogin",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Created Date",
		"field":"createdDate",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Overdue",
		"field":"overDue",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	}
	];

var gridOptions = null;
function loadActivityGrid(){
	$("#activityGrid").empty();
    gridOptions = {
        defaultColDef: {
            filter: true,
            sortable: true,
            resizable: true
        },
        columnDefs: columnDefs,
        //rowData: getRowData(),
        floatingFilter: true,
        rowSelection: "multiple",
        editType: "fullRow",
        paginationPageSize: 10,
        domLayout:"autoHeight",
        pagination: true,
        onGridReady: function() {
        	getRowData();
        	columnVisible();
        }
    }
    
    //lookup the container we want the Grid to use
    var eGridDiv = document.querySelector("#activityGrid");
    // create the grid passing in the div to use together with the columns & data we want to use
    new agGrid.Grid(eGridDiv, gridOptions);
    
}

function columnVisible(){	
	gridOptions.columnApi.setColumnsVisible(["emplTeamId","businessUnitId"], false);
}

function getAjaxResponse(callback) {
    var data1;
    var result = [];
    var resultRes = null;
    var params = {}
    var paramStr = $("#findActivity").serialize();
    var fromData = JSON.stringify(paramStr);
    var errorMessage = null;
    var resultData = null;
    $.ajax({
        type: "POST",
        url: "getActivity",
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
                console.log("--errorMessage-----" + errorMessage);
                callback(resultData);
            }else{
                callback(data.data);
            }
            
        },
        error: function() {
            console.log('Error occured');
            showAlert("error", "Error occured!");
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
        fileName: "Find activity",
        exportMode: 'csv'
    };

    gridOptions.api.exportDataAsCsv(params);
}

function getSelectedRowsForReassign() {
	var selectedRows;
	selectedRows = this.gridOptions.api.getSelectedRows();
	selectedRows.map((row)=>{
	});
	return selectedRows
}
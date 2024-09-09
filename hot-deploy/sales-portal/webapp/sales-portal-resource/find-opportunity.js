$("#loader1").show();
$(function() {
	//loadAgGrid();
	$('#reassignModal #close').click(function(){
		$('#reassignModal input[type=reset]').click();
	});
	$('#reassignModal #btnclose').click(function(){
		$('#reassignModal input[type=reset]').click();
	});
});

var columnDefs = [{ 
	   "headerName":"Opportunity Number",
	   "field":"salesOpportunityId",
	   "sortable":true,
	   "filter":true,
	   "filter":"agTextColumnFilter",
	   "checkboxSelection":true,
	   width: 240,
	   suppressAutoSize:true,
	   cellRenderer: function(params) {
        	return '<a href="viewOpportunity?salesOpportunityId=' + params.data.salesOpportunityId + ' &seqId=' + params.data.salesOpportunityId + '&entity=SalesOpportunitySummary" target="">' + params.data.salesOpportunityId + '</a>'
        	}
	},
	{ 
	   "headerName":"CIN/CIF",
	   "field":"customerCin",
	   "sortable":true,
	   "filter":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"CIN/CIF suffix",
		"field":"customerSuffix",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Prospect ID",
		"field":"prospectId",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
				   
	},
	{ 
		"headerName":"Customer",
		"field":"firstName",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
				   
	},

	{ 
		"headerName":"Call Outcome",
		"field":"callOutComeName",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
				   
	},
	{ 
		"headerName":"Empl Team",
		"field":"emplTeamId",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
				   
	},

	{ 
		"headerName":"Response Type",
		"field":"responseTypeName",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
				   
	},

	{ 
		"headerName":"Opportunity Status",
		"field":"opportunityStageDesc",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
				   
	},
	

	{ 
		"headerName":"Response Reason",
		"field":"responseReasonName",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
				   
	},
	

	{ 
		"headerName":"Total Sales Amount",
		"field":"estimatedAmount",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
				   
	},
	

	{ 
		"headerName":"Owner",
		"field":"ownerName",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
				   
	},
	

	{ 
		"headerName":"Business Unit (Owning User)",
		"field":"businessUnitName",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
				   
	},

	{ 
		"headerName":"Remarks",
		"field":"remarks",
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
		"headerName":"Modified By",
		"field":"modifiedByUserLogin",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
				   
	},

	{ 
		"headerName":"Currency",
		"field":"currencyUomId",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
				   
	}
	
	];

var gridOptions = null;
function loadAgGrid(){
	$("#opportunityGrid").empty();
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
    
    //lookup the container we want the Grid to use
    var eGridDiv = document.querySelector("#opportunityGrid");
    // create the grid passing in the div to use together with the columns & data we want to use
    new agGrid.Grid(eGridDiv, gridOptions);
    
}


function getAjaxResponse(callback) {
    var data1;
    var result = [];
    var resultRes = null;
    var params = {}
    var paramStr = $("#findOpportunity").serialize();
    var fromData = JSON.stringify(paramStr);
    var errorMessage = null;
    var resultData = null;
    $.ajax({
        type: "POST",
        url: "getOpportunity",
        async: true,
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
	var selectedRows;
	selectedRows = this.gridOptions.api.getSelectedRows();
	selectedRows.map((row)=>{
	});
	return selectedRows
}

function getSelectedRowsForReassign() {
    return gridOptions.api.getSelectedNodes()
}

function onBtExport() {
    var params = {
        skipHeader: false,
        allColumns: true,
        fileName: "Opportunity",
        exportMode: 'csv'
    };


    gridOptions.api.exportDataAsCsv(params);
}
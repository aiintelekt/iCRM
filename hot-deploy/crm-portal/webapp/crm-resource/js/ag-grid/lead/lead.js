$(function() {
	loadAgGrid();
});

var columnDefs = [{ 
	   "headerName":"Lead ID",
	    "field":"partyId",
	   "sortable":true,
	   "filter":"agTextColumnFilter",
		cellRenderer: params => 
			`<a href="viewLead?partyId=${params.data.partyId}" class="btn-xs ml-0 px-0">${params.data.partyId}</a>`
	},
	{  
		"headerName":"Company Name",
	    "field":"companyName",
		"sortable":true,
		"filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Lead Status",
	   "field":"leadStatus",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Lead Substatus",
	   "field":"leadSubStatus",
	   "sortable":false,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"# of Attempts",
	   "field":"noOfAttempt",
	   "sortable":false,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Lead Source",
	   "field":"dataSourceDesc",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Lead Assigned From",
	   "field":"leadAssignFromName",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Lead Assigned To",
	   "field":"leadAssignToName",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Last Called Date",
	   "field":"lastCalledDate",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Last Call Log Updated Date",
	   "field":"lastCallLogUpdatedDate",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Manager Name",
	   "field":"managerName",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Last Meeting Date",
	   "field":"lastMeetingDate",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Last Meeting Log Updated Date",
	   "field":"lastMeetingLogUpdateDate",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Lead Assigned to City",
	   "field":"cityName",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Days in Queue",
	   "field":"daysInQueue",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Lead Classification",
	   "field":"leadClassification",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"RM Re-Assign",
	   "headerCheckboxSelection":true,
	   "checkboxSelection":true,
	   "sortable":false,
	   "filter":false
	}
];

var gridOptions = null;
function loadAgGrid(){
	$("#myGrid").empty();
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
        //rowMultiSelectWithClick: true,
        //suppressRowClickSelection: true,
        onSelectionChanged: onSelectionChanged,
        editType: "fullRow",
        paginationPageSize: 10,
        domLayout:"autoHeight",
        pagination: true,
        onGridReady: function() {
        	sizeToFit();
        	getRowData();
        }
    }
    
    //lookup the container we want the Grid to use
    var eGridDiv = document.querySelector("#myGrid");
    // create the grid passing in the div to use together with the columns & data we want to use
    new agGrid.Grid(eGridDiv, gridOptions);
    
}

function onSelectionChanged() {
    var selectedRows = gridOptions.api.getSelectedRows();
    var selectedRowsString = '';

    selectedRows.forEach(function(selectedRow, index) {
        if (index > 0) {
            selectedRowsString += ', ';
        }
        selectedRowsString += selectedRow.partyId;
    });

    $('#partyList').val(selectedRowsString);
}


function getAjaxResponse(callback) {
    var data1;
    var result = [];
    var resultRes = null;
    var params = {}
    var paramStr = $("#searchForm").serialize();
    

    // validate the serialize form data
    var parameters =  $("#searchForm :input")
		    .filter(function(index, element) {
		        return $(element).val() != '';
		    }).serialize();
    
    //
    
    /*if(parameters == null || parameters == '' || parameters == 'undefined'){
    	callback(result);
    } else{*/
    	console.log("formData--->"+JSON.stringify(paramStr));
        var fromData = JSON.stringify(paramStr);
        var errorMessage = null;
        var resultData = null;
        $.ajax({
            type: "POST",
            url: "getLeadDetailsExt",
            async: true,
            data: JSON.parse(fromData),
            success: function(data) {
                var result1 = data[0];
                if(result1 != null || result1 != undefined){
                    errorMessage = result1.errorMessage;
                    resultData = result1.errorResult;
                }
                if(errorMessage != null || errorMessage != undefined) {
                    showAlert("error", errorMessage);
                    console.log("--errorMessage-----" + errorMessage);
                    callback(resultData);
                }else{
                    callback(data);
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
    //}
    
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
    console.log('---------------------------------------');
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
    const selectedNodes = gridOptions.api.getSelectedNodes();
    const selectedData = selectedNodes.map(function(node) {
        return node.data;
    });
    const selectedDataStringPresentation = selectedData.map(function(node) {
        return node.Owner + ' ' + node.Date_Due;
    }).join(', ');
    alert('Selected nodes: ' + selectedDataStringPresentation);
}

function onBtExport() {
    var params = {
        skipHeader: false,
        allColumns: true,
        fileName: "Lead",
        exportMode: 'xlsx'
    };


    gridOptions.api.exportDataAsExcel(params);
}
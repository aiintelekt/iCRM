$(function() {
	loadAgGrid();
});

var columnDefs = [{ 
	   "headerName":"Group Id",
	   "field":"groupId",
	   "filter":"agTextColumnFilter"
	},
	{  
		"headerName":"Group Name",
	    "field":"groupName",
	    "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Hide",
	   "field":"hide",
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Sequence",
	   "field":"sequence",
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"",
	   "sortable":false,
	   "filter":false,
	   width:100,
	   suppressAutoSize:true,
	   cellRenderer: params => `<a href="findContactField?groupId=${params.data.groupId}" class="btn btn-xs btn-info tooltips" data-original-title="Configuration"><i class="fa fa-cog info"></i></a>
				<a href="editContactFieldGroup?groupId=${params.data.groupId}" class="btn btn-xs btn-primary tooltips" data-original-title="Edit"><i class="fa fa-pencil info"></i></a>
				<a class="btn btn-xs btn-danger tooltips confirm-message" href="deleteContactFieldGroup?groupId=${params.data.groupId}" data-original-title="Remove"><i class="fa fa-times red"></i></a>`
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


function getAjaxResponse(callback) {
    var data1;
    var result = [];
    var resultRes = null;
    var params = {}
    var paramStr = $("#searchForm").serialize();
    
    var parameters =  $("#searchForm :input")
		    .filter(function(index, element) {
		        return $(element).val() != '';
		    }).serialize();
        
	console.log("formData--->"+JSON.stringify(paramStr));
    var fromData = JSON.stringify(paramStr);
    var errorMessage = null;
    var resultData = null;
    $.ajax({
        type: "POST",
        url: "getContactFieldGroup",
        async: true,
        data: JSON.parse(fromData),
        success: function(data) {console.log("data", data);
            var result1 = data;
            if(result1 != null || result1 != undefined){
                errorMessage = result1.errorMessage;
                resultData = result1.errorResult;
            }
            if(errorMessage != null || errorMessage != undefined) {
                showAlert("error", errorMessage);
                callback(resultData);
            }else{
                callback(data.data);
            }
            
        },
        error: function() {
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
        });
    }
    if (res.remove) {
        res.remove.forEach(function(rowNode) {
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
        fileName: "ContactFieldGroup",
        exportMode: 'xlsx'
    };


    gridOptions.api.exportDataAsExcel(params);
}
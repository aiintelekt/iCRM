$("#loader").show();

var dndLogColumnDefs = [{
	    "headerName": "Process Id",
	    "field": "importId",
	    "sortable": true,
	    "filter":"agTextColumnFilter"
	  }, {
	    "headerName": "Error Description",
	    "field": "codeDescription",
	    "sortable": false,
	    "filter":"agTextColumnFilter"
	  }, {
	    "headerName": "DND Number",
	    "field": "dndNumber",
	    "sortable": false,
	    "filter":"agTextColumnFilter"
	  }, {
	    "headerName": "DND Indicator",
	    "field": "dndIndicator",
	    "sortable": true,
	    "filter":"agTextColumnFilter"
	  } 	  	 
    ];

var dndLogGridOptions = null;
function loadDndLogAgGrid(importId){
	$("#dndLogGrid").empty();
    dndLogGridOptions = {
        defaultColDef: {
            filter: true,
            sortable: true,
            resizable: true
        },
        columnDefs: dndLogColumnDefs,
        //rowData: gridLogGetRowData(),
        floatingFilter: true,
        rowSelection: "multiple",
        editType: "fullRow",
        paginationPageSize: 10,
        domLayout:"autoHeight",
        pagination: true,
        onGridReady: function() {
        	sizeToFitLogGrid();
        	gridLogGetRowData(importId);
        }
    }

    //lookup the container we want the Grid to use
    var logGridDiv = document.querySelector("#dndLogGrid");
    // create the grid passing in the div to use together with the columns & data we want to use
    new agGrid.Grid(logGridDiv, dndLogGridOptions); 
}

function getDndLogAjaxResponse(callback, importId) {
    var data1;
    var result = [];
    var resultRes = null;
    var params = {}
    var paramStr = {"importId": importId};
    		
    	console.log("formData--->"+JSON.stringify(paramStr));
        var fromData = JSON.stringify(paramStr);
        var errorMessage = null;
        var resultData = null;
        $.ajax({
            type: "POST",
            url: "dndErrorLogsDetails",
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

function gridLogGetRowData(importId) {
	var result;
	result = getDndLogAjaxResponse(function(agdata) {
		dndLogGridOptions.api.setRowData(agdata);
    }, importId);
}

function sizeToFitLogGrid() {
    dndLogGridOptions.api.sizeColumnsToFit();
}

//data binding while loading the page
function onFirstDataRendered(params) {
    params.api.sizeColumnsToFit();
}
function clearData() {
    dndLogGridOptions.api.setRowData([]);
}

function onAddRow() {
    var newItem = createNewRowData();
    var res = dndLogGridOptions.api.updateRowData({
        add: [newItem]
    });
    printResult(res);
}

function addItems() {
    var newItems = [createNewRowData(), createNewRowData(), createNewRowData()];
    var res = dndLogGridOptions.api.updateRowData({
        add: newItems
    });
    printResult(res);
}

function addItemsAtIndex() {
    var newItems = [createNewRowData(), createNewRowData(), createNewRowData()];
    var res = dndLogGridOptions.api.updateRowData({
        add: newItems,
        addIndex: 2
    });
    printResult(res);
}

function updateItems() {
    // update the first 5 items
    var itemsToUpdate = [];
    dndLogGridOptions.api.forEachNodeAfterFilterAndSort(function(rowNode, index) {
        // only do first 5
        if (index >= 5) {
            return;
        }

        var data = rowNode.data;
        data.price = Math.floor((Math.random() * 20000) + 20000);
        itemsToUpdate.push(data);
    });
    var res = dndLogGridOptions.api.updateRowData({
        update: itemsToUpdate
    });
    printResult(res);
}

function onInsertRowAt2() {
    var newItem = createNewRowData();
    var res = dndLogGridOptions.api.updateRowData({
        add: [newItem],
        addIndex: 2
    });
    printResult(res);
}

function onRemoveSelected() {
    var selectedData = dndLogGridOptions.api.getSelectedRows();
    console.log(selectedData.length);
    for (i = 0; i <= selectedData.length; i++) {
    }
    var res = dndLogGridOptions.api.updateRowData({
        remove: selectedData
    });

    printResult(res);
}

function removeSubmit() {
    var selectedData = dndLogGridOptions.api.getSelectedRows();
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

function getSelectedRows() {
    const selectedNodes = dndLogGridOptions.api.getSelectedNodes();
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
        fileName: "LeadDndStatus",
        exportMode: 'xlsx'
    };


    dndLogGridOptions.api.exportDataAsExcel(params);
}
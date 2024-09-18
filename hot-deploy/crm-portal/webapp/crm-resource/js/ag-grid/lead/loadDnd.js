$("#loader").show();
$(function() {
	loadAgGrid();   
});

var columnDefs = [{
	    "headerName": "Process Id",
	    "field": "importId",
	    "sortable": true,
	    "filter":"agTextColumnFilter"
	  }, {
	    "headerName": "File Name",
	    "field": "actualFileName",
	    "sortable": false,
	    "filter":"agTextColumnFilter"
	  }, {
	    "headerName": "Status",
	    "field": "changeStatus",
	    "sortable": false,
	    "filter":"agTextColumnFilter",
	    cellRenderer: function(params) {
            var process = params.data.processed;
            if (process != null && process != "" && process != undefined) {
               if (process == "N"){ 
                data = "InProcess";
                }else if (process == "Y"){
                data = "Processed";
                }
            } else {
                data = '';
           }
            return data;
        }
	  }, {
	    "headerName": "Total Count",
	    "field": "totalCount",
	    "sortable": true,
	    "filter":"agTextColumnFilter",
	    cellRenderer: function(params) {
            var totalCount = params.data.totalCount;
            if (totalCount!= null && totalCount != "" && totalCount != undefined) {
            	data = totalCount;
            }
            return data;
        }
	  }, {
	    "headerName": "Error Count",
	    "field": "totalError",
	    "sortable": true,
	    "filter":"agTextColumnFilter",
	    cellRenderer: function(params) {
            var totalError = params.data.totalError;
            if (params.data.processed == "N") {
            	data = "";
            }
            return data;
        }
	  }, {
	    "headerName": "Error List",
	    "field": "changeDate",
	    "sortable": true,
	    "filter":false,
	    cellRenderer: params =>
	  		`<a onclick=viewImportError("${params.data.importId}")>
	  			<span class="btn btn-xs btn-primary ml-4" data-toggle="modal" href="#dndErrorLogsModal" 
	  			alt="View" title="View">View</span></a>`
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
    var gridDiv = document.querySelector("#myGrid");
    // create the grid passing in the div to use together with the columns & data we want to use
    new agGrid.Grid(gridDiv, gridOptions); 
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
            url: "dndImportDetails",
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
        fileName: "LeadDndStatus",
        exportMode: 'xlsx'
    };


    gridOptions.api.exportDataAsExcel(params);
}
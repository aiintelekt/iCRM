
$(function() {
BalanceCell();
});

var columnDefsOff = [
{
        "headerName":"Tenure",
        "field":"",
       },
     {
        "headerName":"Interest Rate",
        "field":""
     },
      {
    "headerName" : "Processing Fee",
    "field" : ""
     },  
     {
  "headerName" : "Effective Interest Rate",
   "field" : ""
     },
     {
  "headerName" : "Credit Plan(Cashline)",
  "field" : ""
     },

   {
 "headerName" : "Product Code(Cashline)",
 "field" : ""
   },
   {
 "headerName" : "Effective Interest Rate(Cards)",
 "field" : ""
   },
   {
 "headerName" : "Credit Plan(Cards)",
 "field" : ""
},
   {
 "headerName" : "Product Code(Cards)",
 "field" : ""
   }

];

var gridOptionsOffCell = null;
function BalanceCell(){
$("#Balance").empty();
gridOptionsOffCell = {
        defaultColDef: {
            filter: true,
            sortable: true,
            resizable: true
        },
        columnDefs: columnDefsOff,
        // rowData: getRowData(),
        floatingFilter: true,
        rowSelection: "multiple",
        editType: "fullRow",
        paginationPageSize: 10,
        domLayout:"autoHeight",
        pagination: true,
        onGridReady: function() {
        getRowDataOffCell2();
        sizeToFitCell2();
        }
    }
   
    // lookup the container we want the Grid to use
    var eGridDiv2 = document.querySelector("#Balance");
    // create the grid passing in the div to use together with the columns &
// data we want to use
    new agGrid.Grid(eGridDiv2, gridOptionsOffCell);
   
}


function getAjaxResponse(callback) {
    var data1;
    var result = [];
    var resultRes = null;
    var errorMessage = null;
    var resultData = null;
        $.ajax({
            type: "POST",
            url: "",
            async: false,
           // data: JSON.parse(fromData),
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
                callback(result);
            },
            complete: function() {
            //$('#loader').hide();
            }
        });
    //}
   
}

function getRowDataOffCell2() {
var result;
result = getAjaxResponse(function(agdata) {
	gridOptionsOffCell.api.setRowData(agdata);
    });
}

function sizeToFitCell2() {
	gridOptionsOffCell.api.sizeColumnsToFit();
}

//data binding while loading the page
function onFirstDataRendered(params) {
    params.api.sizeColumnsToFit();
}
function clearData() {
	gridOptionsOffCell.api.setRowData([]);
}

function onAddRow() {
    var newItem = createNewRowData();
    var res = gridOptionsOffCell.api.updateRowData({
        add: [newItem]
    });
    printResult(res);
}

function addItems() {
    var newItems = [createNewRowData(), createNewRowData(), createNewRowData()];
    var res = gridOptionsOffCell.api.updateRowData({
        add: newItems
    });
    printResult(res);
}

function addItemsAtIndex() {
    var newItems = [createNewRowData(), createNewRowData(), createNewRowData()];
    var res = gridOptionsOffCell.api.updateRowData({
        add: newItems,
        addIndex: 2
    });
    printResult(res);
}

function updateItems() {
    // update the first 5 items
    var itemsToUpdate = [];
    gridOptionsOffCell.api.forEachNodeAfterFilterAndSort(function(rowNode, index) {
        // only do first 5
        if (index >= 5) {
            return;
        }

        var data = rowNode.data;
        data.price = Math.floor((Math.random() * 20000) + 20000);
        itemsToUpdate.push(data);
    });
    var res = gridOptionsOffCell.api.updateRowData({
        update: itemsToUpdate
    });
    printResult(res);
}

function onInsertRowAt2() {
    var newItem = createNewRowData();
    var res = gridOptionsOffCell.api.updateRowData({
        add: [newItem],
        addIndex: 2
    });
    printResult(res);
}

function onRemoveSelected() {
    var selectedData = gridOptionsOffCell.api.getSelectedRows();
    for (i = 0; i <= selectedData.length; i++) {
    }
    var res = gridOptionsOffCell.api.updateRowData({
        remove: selectedData
    });

    printResult(res);
}

function removeSubmit() {
    var selectedData = gridOptionsOffCell.api.getSelectedRows();
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

function sizeToFitCell2() {
	gridOptionsOffCell.api.sizeColumnsToFit();
}

function getSelectedRows() {
    const selectedNodes = gridOptionsOffCell.api.getSelectedNodes()
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
        fileName: "Templates",
        exportMode: 'xlsx'
    };
    gridOptionsOffCell.api.exportDataAsExcel(params);
}
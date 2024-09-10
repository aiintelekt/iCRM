$(function() {
OfferCell();
});

var columnDefsOfferL = [
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
  "headerName" : "MID(Cashline)",
  "field" : ""
     },

   {
 "headerName" : "MID(Cards)",
 "field" : ""
   },
   {
 "headerName" : "MPS",
 "field" : ""
   }

];

var myOptions1 = null;
function OfferCell(){
$("#myOffer").empty();
    myOptions1 = {
        defaultColDef: {
            filter: true,
            sortable: true,
            resizable: true
        },
        columnDefs: columnDefsOfferL,
        // rowData: getRowData(),
        floatingFilter: true,
        rowSelection: "multiple",
        editType: "fullRow",
        paginationPageSize: 10,
        domLayout:"autoHeight",
        pagination: true,
        onGridReady: function() {
        getRowDataOffCell();
        sizeToFitOff();
        }
    }
   
    // lookup the container we want the Grid to use
    var offer1 = document.querySelector("#myOffer");
    // create the grid passing in the div to use together with the columns &
// data we want to use
    new agGrid.Grid(offer1, myOptions1);
   
}


function getAjaxResponse(callback) {
    var data1;
    var result = [];
    var resultRes = null;
    // var params = {}
    // var paramStr = $("#findTemplateForm").serialize();
   

    // validate the serialize form data
   
   
    /*
* if(parameters == null || parameters == '' || parameters == 'undefined'){
* callback(result); } else{
*/
    // console.log("formData--->"+JSON.stringify(paramStr));
        // var fromData = JSON.stringify(paramStr);
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

function getRowDataOffCell() {
var result;
result = getAjaxResponse(function(agdata) {
	myOptions1.api.setRowData(agdata);
    });
}

function sizeToFitOff() {
	myOptions1.api.sizeColumnsToFit();
}

//data binding while loading the page
function onFirstDataRendered(params) {
    params.api.sizeColumnsToFit();
}
function clearData() {
	myOptions1.api.setRowData([]);
}

function onAddRow() {
    var newItem = createNewRowData();
    var res = myOptions1.api.updateRowData({
        add: [newItem]
    });
    printResult(res);
}

function addItems() {
    var newItems = [createNewRowData(), createNewRowData(), createNewRowData()];
    var res = myOptions1.api.updateRowData({
        add: newItems
    });
    printResult(res);
}

function addItemsAtIndex() {
    var newItems = [createNewRowData(), createNewRowData(), createNewRowData()];
    var res = myOptions1.api.updateRowData({
        add: newItems,
        addIndex: 2
    });
    printResult(res);
}

function updateItems() {
    // update the first 5 items
    var itemsToUpdate = [];
    myOptions1.api.forEachNodeAfterFilterAndSort(function(rowNode, index) {
        // only do first 5
        if (index >= 5) {
            return;
        }

        var data = rowNode.data;
        data.price = Math.floor((Math.random() * 20000) + 20000);
        itemsToUpdate.push(data);
    });
    var res = myOptions1.api.updateRowData({
        update: itemsToUpdate
    });
    printResult(res);
}

function onInsertRowAt2() {
    var newItem = createNewRowData();
    var res = myOptions1.api.updateRowData({
        add: [newItem],
        addIndex: 2
    });
    printResult(res);
}

function onRemoveSelected() {
    var selectedData = myOptions1.api.getSelectedRows();
    console.log(selectedData.length);
    for (i = 0; i <= selectedData.length; i++) {
    }
    var res = myOptions1.api.updateRowData({
        remove: selectedData
    });

    printResult(res);
}

function removeSubmit() {
    var selectedData = myOptions1.api.getSelectedRows();
}

function printResult(res) {
    console.log('---------------------------------------')
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




function sizeToFitOff() {
	myOptions1.api.sizeColumnsToFit();
}

function getSelectedRows() {
    const selectedNodes = myOptions1.api.getSelectedNodes()
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


    myOptions1.api.exportDataAsExcel(params);
}
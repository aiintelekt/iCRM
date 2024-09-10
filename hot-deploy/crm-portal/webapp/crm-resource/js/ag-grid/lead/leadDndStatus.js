$("#loader").show();
$(function() {
	loadLeadDndStatusAgGrid();   
});

var leadDndStatusColumnDefs = [{
	    "headerName": "DND Sequence Id",
	    "field": "dndSeqId",
	    "sortable": true,
	    "filter":"agTextColumnFilter"
	  }, {
	    "headerName": "Change Status",
	    "field": "changeStatus",
	    "sortable": false,
	    "filter":"agTextColumnFilter"
	  }, {
	    "headerName": "DND Number",
	    "field": "dndNumber",
	    "sortable": true,
	    "filter":"agTextColumnFilter"
	  }, {
	    "headerName": "DND Indicator",
	    "field": "dndIndicator",
	    "sortable": true,
	    "filter":"agTextColumnFilter"
	  }, {
	    "headerName": "Changed Date",
	    "field": "changeDate",
	    "sortable": true,
	    "filter":"agTextColumnFilter"
	  }  	  	 
    ];

var leadDndStatusGridOptions = null;
function loadLeadDndStatusAgGrid(){
	$("#leadDndStatusGrid").empty();
	var leadDndStatusListData = $('#leadDndStatusListData').val(); 
    var leadDndStatusListDataJSON = [];
    if(leadDndStatusListData != ""){
    	leadDndStatusListDataJSON = JSON.parse(leadDndStatusListData);
	}
    leadDndStatusGridOptions = {
        defaultColDef: {
            filter: true,
            sortable: true,
            resizable: true
        },
        columnDefs: leadDndStatusColumnDefs,
        rowData: leadDndStatusListDataJSON, //getRowData(),
        floatingFilter: true,
        rowSelection: "multiple",
        editType: "fullRow",
        paginationPageSize: 10,
        domLayout:"autoHeight",
        pagination: true,
        onGridReady: function() {
        	sizeToFit();
        	//getRowData();
        }
    }

    //lookup the container we want the Grid to use
    var leadDndStatusGridDiv = document.querySelector("#leadDndStatusGrid");
    // create the grid passing in the div to use together with the columns & data we want to use
    new agGrid.Grid(leadDndStatusGridDiv, leadDndStatusGridOptions); 
	$('#leadDndStatusGrid').find("#donePage").val($('#donePage').val());
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
            //url: "",
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
		leadDndStatusGridOptions.api.setRowData(agdata);
    });
}

function sizeToFit() {
    leadDndStatusGridOptions.api.sizeColumnsToFit();
}

//data binding while loading the page
function onFirstDataRendered(params) {
    params.api.sizeColumnsToFit();
}
function clearData() {
    leadDndStatusGridOptions.api.setRowData([]);
}

function onAddRow() {
    var newItem = createNewRowData();
    var res = leadDndStatusGridOptions.api.updateRowData({
        add: [newItem]
    });
    printResult(res);
}

function addItems() {
    var newItems = [createNewRowData(), createNewRowData(), createNewRowData()];
    var res = leadDndStatusGridOptions.api.updateRowData({
        add: newItems
    });
    printResult(res);
}

function addItemsAtIndex() {
    var newItems = [createNewRowData(), createNewRowData(), createNewRowData()];
    var res = leadDndStatusGridOptions.api.updateRowData({
        add: newItems,
        addIndex: 2
    });
    printResult(res);
}

function updateItems() {
    // update the first 5 items
    var itemsToUpdate = [];
    leadDndStatusGridOptions.api.forEachNodeAfterFilterAndSort(function(rowNode, index) {
        // only do first 5
        if (index >= 5) {
            return;
        }

        var data = rowNode.data;
        data.price = Math.floor((Math.random() * 20000) + 20000);
        itemsToUpdate.push(data);
    });
    var res = leadDndStatusGridOptions.api.updateRowData({
        update: itemsToUpdate
    });
    printResult(res);
}

function onInsertRowAt2() {
    var newItem = createNewRowData();
    var res = leadDndStatusGridOptions.api.updateRowData({
        add: [newItem],
        addIndex: 2
    });
    printResult(res);
}

function onRemoveSelected() {
    var selectedData = leadDndStatusGridOptions.api.getSelectedRows();
    console.log(selectedData.length);
    for (i = 0; i <= selectedData.length; i++) {
    }
    var res = leadDndStatusGridOptions.api.updateRowData({
        remove: selectedData
    });

    printResult(res);
}

function removeSubmit() {
    var selectedData = leadDndStatusGridOptions.api.getSelectedRows();
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
    const selectedNodes = leadDndStatusGridOptions.api.getSelectedNodes();
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


    leadDndStatusGridOptions.api.exportDataAsExcel(params);
}
$("#loader").show();
$(function() {
	loadCallLogAgGrid();   
});

var callLogColumnDefs = [{
	    "headerName": "Subject",
	    "field": "workEffortName",
	    "sortable": true,
	    "filter":"agTextColumnFilter"
	  }, {
	    "headerName": "Purpose",
	    "field": "workEffortPurposeTypeId",
	    "sortable": false,
	    "filter":"agTextColumnFilter"
	  }, {
	    "headerName": "Message",
	    "field": "content",
	    "sortable": true,
	    "filter":"agTextColumnFilter"
	  }, {
	    "headerName": "Created Date",
	    "field": "createdDate",
	    "sortable": true,
	    "filter":"agTextColumnFilter"
	  }, {
	    "headerName": "Created By",
	    "field": "createdByUserLogin",
	    "sortable": true,
	    "filter":"agTextColumnFilter"
	  }, {
	    "headerName": "",
	    "sortable":false,
	    "filter":false,
		cellRenderer: params => 
			`<form method="post" action="deleteCallLog" name="deleteCallLogs_${params.data.workEffortId}">
                  <input name="workEffortId" value="${params.data.workEffortId}" type="hidden">
                  <input name="partyId" value="${params.data.partyId}" type="hidden">
                  <input name="donePage" id="donePage" value="" type="hidden">
                  <input type="hidden" name="activeTab" value="opportunites" />
               </form>
               <a class="btn btn-xs btn-secondary btn-danger tooltips confirm-message" 
               		href="javascript:document.deleteCallLogs_${params.data.workEffortId}.submit();" 
               		data-original-title="Delete"><i class="fa fa-times red"></i></a>`
	  }	  	  	 
    ];

var callLogGridOptions = null;
function loadCallLogAgGrid(){
	$("#callLogGrid").empty();
	var callLogListData = $('#callLogListData').val(); 
    var callLogListDataJSON = [];
    if(callLogListData != ""){
    	callLogListDataJSON = JSON.parse(callLogListData);
	}
    callLogGridOptions = {
        defaultColDef: {
            filter: true,
            sortable: true,
            resizable: true
        },
        columnDefs: callLogColumnDefs,
        rowData: callLogListDataJSON, //getRowData(),
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
    var callLogGridDiv = document.querySelector("#callLogGrid");
    // create the grid passing in the div to use together with the columns & data we want to use
    new agGrid.Grid(callLogGridDiv, callLogGridOptions); 
	$('#callLogGrid').find("#donePage").val($('#donePage').val());
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
		callLogGridOptions.api.setRowData(agdata);
    });
}

function sizeToFit() {
    callLogGridOptions.api.sizeColumnsToFit();
}

//data binding while loading the page
function onFirstDataRendered(params) {
    params.api.sizeColumnsToFit();
}
function clearData() {
    callLogGridOptions.api.setRowData([]);
}

function onAddRow() {
    var newItem = createNewRowData();
    var res = callLogGridOptions.api.updateRowData({
        add: [newItem]
    });
    printResult(res);
}

function addItems() {
    var newItems = [createNewRowData(), createNewRowData(), createNewRowData()];
    var res = callLogGridOptions.api.updateRowData({
        add: newItems
    });
    printResult(res);
}

function addItemsAtIndex() {
    var newItems = [createNewRowData(), createNewRowData(), createNewRowData()];
    var res = callLogGridOptions.api.updateRowData({
        add: newItems,
        addIndex: 2
    });
    printResult(res);
}

function updateItems() {
    // update the first 5 items
    var itemsToUpdate = [];
    callLogGridOptions.api.forEachNodeAfterFilterAndSort(function(rowNode, index) {
        // only do first 5
        if (index >= 5) {
            return;
        }

        var data = rowNode.data;
        data.price = Math.floor((Math.random() * 20000) + 20000);
        itemsToUpdate.push(data);
    });
    var res = callLogGridOptions.api.updateRowData({
        update: itemsToUpdate
    });
    printResult(res);
}

function onInsertRowAt2() {
    var newItem = createNewRowData();
    var res = callLogGridOptions.api.updateRowData({
        add: [newItem],
        addIndex: 2
    });
    printResult(res);
}

function onRemoveSelected() {
    var selectedData = callLogGridOptions.api.getSelectedRows();
    console.log(selectedData.length);
    for (i = 0; i <= selectedData.length; i++) {
    }
    var res = callLogGridOptions.api.updateRowData({
        remove: selectedData
    });

    printResult(res);
}

function removeSubmit() {
    var selectedData = callLogGridOptions.api.getSelectedRows();
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
    const selectedNodes = callLogGridOptions.api.getSelectedNodes();
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
        fileName: "CallLog",
        exportMode: 'xlsx'
    };


    callLogGridOptions.api.exportDataAsExcel(params);
}
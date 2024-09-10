$(function() {
	loadAgGrid();
});

var columnDefs = [{ 
	   "headerName":"Title",
	   "field":"personalTitle",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{  
		"headerName":"Customer Name",
		"sortable":true,
		cellRenderer:function(param){
			var customerName = "";
			if(param.data.lastName != null && param.data.lastName != "" && param.data.lastName != "undefined"){
				customerName = param.data.firstName +" "+ param.data.lastName;
			} else {
				customerName = param.data.firstName;
			}
			//return customerName;
			return '<a href="viewCustomer?partyId=' + param.data.partyId + '">' + customerName+ '(' + param.data.partyId + ')</a>';
		}
	},
	{ 
	   "headerName":"Status",
	   "field":"statusId",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Birth Date",
	   "field":"birthDate",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Created Date",
	   "field":"createdDate",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Phone Number",
	   "sortable":false,
	   "filter":"agTextColumnFilter",
	   cellRenderer:function(param){
			var phoneNumber = "";
			if(param.data.countryCode != null && param.data.countryCode != "" && param.data.countryCode != "undefined"){
				phoneNumber = param.data.countryCode
			} 
			if(param.data.areaCode != null && param.data.areaCode != "" && param.data.areaCode != "undefined") {
				if(phoneNumber.length == 0)
					phoneNumber = phoneNumber + param.data.areaCode;
				else
					phoneNumber = phoneNumber +" "+ param.data.areaCode;
			}
			if(param.data.contactNumber != null && param.data.contactNumber != "" && param.data.contactNumber != "undefined") {
				if(phoneNumber.length == 0)
					phoneNumber = phoneNumber + param.data.contactNumber;
				else
					phoneNumber = phoneNumber +" "+ param.data.contactNumber;
			}
			return phoneNumber;
		}
	},
	{ 
	   "headerName":"Email Address",
	   "field":"infoString",
	   "sortable":false,
	   "filter":"agTextColumnFilter"
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
        	//sizeToFit();
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
            url: "getCustomers",
            async: true,
            data: JSON.parse(fromData),
            success: function(data) {
                result = data.data;
                var responseMessage = data.responseMessage;
                if(responseMessage != null && responseMessage != undefined && responseMessage == 'errors'){
                    errorMessage = data.errorMessage;
                }
                if(errorMessage != null || errorMessage != undefined) {
                    showAlert("error", errorMessage);
                    console.log("--errorMessage-----" + errorMessage);
                    callback(result);
                }else{
                    callback(result);
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
        fileName: "Contact",
        exportMode: 'xlsx'
    };


    gridOptions.api.exportDataAsExcel(params);
}
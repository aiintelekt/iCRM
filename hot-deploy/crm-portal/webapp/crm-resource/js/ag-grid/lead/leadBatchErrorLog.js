$(function() {
	loadAgGrid();
});

var columnDefs = [{ 
	   "headerName":"Batch ID",
	   "field":"batchId",
	   "sortable":true,
	   "filter":"agTextColumnFilter",
		cellRenderer: params => 
			`<div class="text-left ml-1" ><a href="javascript: findLeadByBatchId('${params.data.batchId}')" 
				class="btn btn-xs btn-primary m5 tooltips"><strong>${params.data.batchId}</strong></a></div>`
	},
	{  
		"headerName":"Uploaded Date",
	    "field":"createdStamp",
		"sortable":true,
		"filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Uploaded By",
	   "field":"uploadedBy",
	   "sortable":true,
	   "filter":"agTextColumnFilter",
	   cellRenderer: params => 
		   `<div class="text-left ml-1" ><a href="javascript: findLeadByUploadedBy('${params.data.uploadedByUserLoginId}')" 
		   		class="btn btn-xs btn-primary m5 tooltips"><strong>${params.data.uploadedByUserLoginId}</strong></a></div>`
	},
	{ 
	   "headerName":"Company Name",
	   "field":"companyName",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Lead ID",
	   "field":"leadId",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"First Name",
	   "field":"firstName1",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Last Name",
	   "field":"lastName1",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Company Address",
	   "field":"address1",
	   "sortable":false,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Phone Number 1",
	   "field":"primaryPhoneNumber",
	   "sortable":false,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Error Description",
	   "field":"codeList",
	   "sortable":false,
	   "filter":"agTextColumnFilter",
	   cellRenderer: function(params) {
		   	var data = '';
		   	for (var key in params.data.codeList) {
        		data += '<span class="tooltips" title="'+params.data.codeList[key]+'">['+key+']</span> ';
        	}
	        return data;
		}
	},
	{ 
	   "headerName":"Action",
	   "field":"auditCount",
	   "sortable":false,
	   "filter":"agTextColumnFilter",
	   cellRenderer: function(params){
        	var auditTitle = "View Audit Messages";
        	if (params.data.auditCount == 0) {
        		auditTitle = "No Audit Log";
        	}
        	var exitMessage = '<a href="#" class="btn btn-xs btn-primary tooltips view-audit-message" ' + 
        		'data-leadId="'+params.data.leadId+'" title="'+auditTitle+'"><strong>'+params.data.auditCount+'</strong></a>';
            return '<div class="text-center ml-1">' + exitMessage + '</div>';
      	}
	},
	{ 
	   "headerName":"",
	   "sortable":false,
	   "filter": false,
	   "minWidth": "50px",
	   "maxWidth": "50px",
	   "suppressSizeToFit": true,
	   cellRenderer: function(params){
          	var data = '<div class="text-center ml-1" >';
            if(params.data.importStatusId && (params.data.importStatusId == 'DATAIMP_ERROR' || params.data.importStatusId == 'DATAIMP_FAILED' || params.data.importStatusId == 'DATAIMP_NOT_APPROVED')){
                data += '<a class="btn btn-xs btn-primary tooltips disabled" href="editDataImpLead?leadId='+params.data.leadId+'&backUrl=leadBatchErrorLog" data-original-title="Edit" ><i class="fa fa-pencil info"></i></a>';
            }
            if(params.data.isDisalbed){
            	var disableReason = params.data.disableReason;
            	disableReason += ". Want to Enable?";
                data += '<a class="btn btn-xs btn-primary tooltips" href="javascript: enableLead(\''+params.data.primaryPartyId+'\')" data-original-title="'+disableReason+'" ><i class="fa fa-eye info"></i></a>';
            }
            
            data += "</div>";
            return data;
		}
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
            url: "searchBatchLeads",
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
        fileName: "LeadBatchErrorLog",
        exportMode: 'xlsx'
    };


    gridOptions.api.exportDataAsExcel(params);
}
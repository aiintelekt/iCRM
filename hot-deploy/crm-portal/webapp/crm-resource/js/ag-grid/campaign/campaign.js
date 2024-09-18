$(function() {
	loadCampaignAgGrid();
	/*<#if requestt?contains("viewContact?")>
    <th>Account Name</th>
	if (actId != "null" && actId != ""  && actId != undefined){
        data = row.accountName + ' (' + row.accountId + ')';
        }else{
        data = "N/A";
        }
    </#if>*/
});

var campaignColumnDefs = [{ 
	   "headerName":"Campaign Name",
	    "field":"contactListId",
	   "sortable":true,
	   "filter":"agTextColumnFilter",
		cellRenderer: params => 
			`${params.data.contactListId} (${params.data.campaignId})`
	},
	{  
		"headerName":"Campaign Type",
	    "field":"campaignTypeDesc",
		"sortable":true,
		"filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Opened",
	   "field":"opened",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Not Opened",
	   "field":"notOpen",
	   "sortable":false,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Clicked",
	   "field":"clickCount",
	   "sortable":false,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Converted",
	   "field":"converted",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Subscribed",
	   "field":"subscribe",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Unsubscribed",
	   "field":"unSubscribe",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Bounced",
	   "field":"bounced",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},{ 
	   "headerName":"",
	   "field":"campaignId",
	   "sortable":false,
	   "filter":"agTextColumnFilter",
		cellRenderer: params => 
			`<a onclick=viewNote(${params.data.campaignId})>
				<span class="fa fa-sticky-note btn btn-xs btn-primary tooltips create-campaignNote" 
				data-toggle="modal" href="#noteCreateUpdate" alt="Note" title="Note"></span></a>`
	}
];

var campaignGridOptions = null;
function loadCampaignAgGrid(){
	$("#campaignGrid").empty();
	campaignGridOptions = {
        defaultColDef: {
            filter: true,
            sortable: true,
            resizable: true
        },
        columnDefs: campaignColumnDefs,
        //rowData: getRowData(),
        floatingFilter: true,
        rowSelection: "multiple",
        editType: "fullRow",
        paginationPageSize: 10,
        domLayout:"autoHeight",
        pagination: true,
        onGridReady: function() {
        	sizeToFit();
        	getRowDataCampaign();
        }
    }
    
    //lookup the container we want the Grid to use
    var campaignGridDiv = document.querySelector("#campaignGrid");
    // create the grid passing in the div to use together with the columns & data we want to use
    new agGrid.Grid(campaignGridDiv, campaignGridOptions);
    
}


function getAjaxResponseCampaign(callback) {
    var data1;
    var result = [];
    var resultRes = null;
    var params = {}
    var paramStr = {"partyId":$("#partyId").val()}; // $("#searchForm").serialize();
    

    // validate the serialize form data
    /*var parameters =  $("#searchForm :input")
		    .filter(function(index, element) {
		        return $(element).val() != '';
		    }).serialize();*/
    
    //
    
    /*if(parameters == null || parameters == '' || parameters == 'undefined'){
    	callback(result);
    } else{*/
    	console.log("formData--->"+JSON.stringify(paramStr));
        var fromData = JSON.stringify(paramStr);
        var errorMessage = null;
        var resultData = null;
        var url = "getPartyCampaignDetails";
        var requestt = $('#requestt').val();
        if(requestt.indexOf("viewAccount?") > -1)
        	url = "getAcctCampaignDetails";
        $.ajax({
            type: "POST",
            url: url,
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

function getRowDataCampaign() {
	var result;
	result = getAjaxResponseCampaign(function(agdata) {
		campaignGridOptions.api.setRowData(agdata);
    });
}

function sizeToFit() {
    campaignGridOptions.api.sizeColumnsToFit();
}

//data binding while loading the page
function onFirstDataRendered(params) {
    params.api.sizeColumnsToFit();
}
function clearData() {
    campaignGridOptions.api.setRowData([]);
}

function onAddRow() {
    var newItem = createNewRowData();
    var res = campaignGridOptions.api.updateRowData({
        add: [newItem]
    });
    printResult(res);
}

function addItems() {
    var newItems = [createNewRowData(), createNewRowData(), createNewRowData()];
    var res = campaignGridOptions.api.updateRowData({
        add: newItems
    });
    printResult(res);
}

function addItemsAtIndex() {
    var newItems = [createNewRowData(), createNewRowData(), createNewRowData()];
    var res = campaignGridOptions.api.updateRowData({
        add: newItems,
        addIndex: 2
    });
    printResult(res);
}

function updateItems() {
    // update the first 5 items
    var itemsToUpdate = [];
    campaignGridOptions.api.forEachNodeAfterFilterAndSort(function(rowNode, index) {
        // only do first 5
        if (index >= 5) {
            return;
        }

        var data = rowNode.data;
        data.price = Math.floor((Math.random() * 20000) + 20000);
        itemsToUpdate.push(data);
    });
    var res = campaignGridOptions.api.updateRowData({
        update: itemsToUpdate
    });
    printResult(res);
}

function onInsertRowAt2() {
    var newItem = createNewRowData();
    var res = campaignGridOptions.api.updateRowData({
        add: [newItem],
        addIndex: 2
    });
    printResult(res);
}

function onRemoveSelected() {
    var selectedData = campaignGridOptions.api.getSelectedRows();
    console.log(selectedData.length);
    for (i = 0; i <= selectedData.length; i++) {
    }
    var res = campaignGridOptions.api.updateRowData({
        remove: selectedData
    });

    printResult(res);
}

function removeSubmit() {
    var selectedData = campaignGridOptions.api.getSelectedRows();
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
    campaignGridOptions.api.sizeColumnsToFit();
}

function getSelectedRows() {
    const selectedNodes = campaignGridOptions.api.getSelectedNodes();
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
        fileName: "Campaign",
        exportMode: 'xlsx'
    };


    campaignGridOptions.api.exportDataAsExcel(params);
}
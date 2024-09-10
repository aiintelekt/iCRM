$(function() {
	const securityGroupInstanceId= 'SECURITY_GROUP';
	const externalLoginKey = $("input[name='externalLoginKey']").val();
	const formDataObject = {};
	const userId = $("input[name='userId']").val();
	let gridInstance  = "";
	let securityGroupUrl = "";

	formDataObject.gridInstanceId = securityGroupInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	formDataObject.dataFetchCall = getSecurityGroupRowData;

	gridInstance = prepareGridInstance(formDataObject);

	if(securityGroupUrl == ""){
		securityGroupUrl = getGridDataFetchUrl("SECURITY_GROUP");
	}

	if(securityGroupUrl == "" || securityGroupUrl == null){
		securityGroupUrl = "/admin-portal/control/getSecurityGroups"
	}

	if(gridInstance){
		getSecurityGroupRowData();
	}

	function getSecurityGroupRowData() {
		const callCtx = {};
		callCtx.ajaxUrl = securityGroupUrl;
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm";
		callCtx.ajaxResponseKey = "list";

		setGridData(gridInstance, callCtx);
	}

	$('#security-group-save-pref').click(function(){
		saveGridPreference(gridInstance, securityGroupInstanceId, userId);
	});
	$("#security-group-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#security-group-clear-pref').click(function(){
		clearGridPreference(gridInstance, securityGroupInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getSecurityGroupRowData();
		}
	});

	$('#security-group-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});

	$("#main-search-btn").click(function () {
		getSecurityGroupRowData();
	});

	$("#searchForm").on("keypress", function (event) {
		let keyPressed = event.keyCode || event.which;
		if(keyPressed === 13) {
			event.preventDefault();
			getSecurityGroupRowData();
			return false;
		}
	});
});

/*$(function() {
	loadAgGrid();
});

var columnDefs = [{ 
	   "headerName":"Group Id",
	   "field":"groupId",
	   "sortable":true,
	   "filter":true,
	   "filter":"agTextColumnFilter",
	   "headerCheckboxSelection":true,
	   "checkboxSelection":true
	},
	{ 
	   "headerName":"Security Type",
	   "field":"securityType",
	   "sortable":true,
	   "filter":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Description",
	   "field":"description",
	   "sortable":true,
	   "filter":true,
	   "filter":"agTextColumnFilter"
	},
	{  
		"headerName":"",
		"cellRenderer": params => `<a href="viewSecurityGroup?groupId=${params.data.groupId}" class="btn btn-primary btn-xs"> <i class="fa fa-eye"></i> Manage Permissions </a>`
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
    var paramStr = $("#findSecurityGroupForm").serialize();
    

    // validate the serialize form data
    var parameters =  $("#findSecurityGroupForm :input")
		    .filter(function(index, element) {
		        return $(element).val() != '';
		    }).serialize();
    
    //
    
    if(parameters == null || parameters == '' || parameters == 'undefined'){
    	callback(result);
    } else{
    	console.log("formData--->"+JSON.stringify(paramStr));
        var fromData = JSON.stringify(paramStr);
        var errorMessage = null;
        var resultData = null;
        $.ajax({
            type: "POST",
            url: "getSecurityGroups",
            async: true,
            data: JSON.parse(fromData),
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
    }
    
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




function sizeToFit() {
    gridOptions.api.sizeColumnsToFit();
}

function getSelectedRows() {
    const selectedNodes = gridOptions.api.getSelectedNodes()
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
        fileName: "Security_Group",
        exportMode: 'xlsx'
    };


    gridOptions.api.exportDataAsExcel(params);
}*/
/*
fagReady("SECURITY_GROUP", function(el, api, colApi, gridApi){
    $("#refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#clear-filter-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    	gridApi.refreshUserPreferences();
    });
	$("#sub-filter-clear-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    });
    $("#export-btn").click(function () {
    	gridApi.csvExport();
    });

    $("#main-search-btn").click(function () {
    	loadSecurityGrps(gridApi, api, colApi);
    });
   */
   /* 	
    $("#update-btn").click(function () {
    	gridApi.saveUpdates();
        setTimeout(() => {  loadTeam(gridApi, api); }, 1000);
    })

    
    $("#insert-btn").click(function () {
    	gridApi.insertNewRow()
    })
    $("#remove-btn").click(function () {
        //removeMainGrid(fag1, api);
    	gridApi.removeSelected();
        setTimeout(() => {  loadTeam(gridApi, api); }, 1000);
        
    });
    
    $("#fetch-previous").click(function () {
    	fetchPrevious();
    	loadTeam(gridApi, api);
    });
    $("#fetch-next").click(function () {
    	fetchNext();
    	loadTeam(gridApi, api);
    });
    $("#fetch-first").click(function () {
    	fetchFirst();
    	loadTeam(gridApi, api);
    });
    $("#fetch-last").click(function () {
    	fetchLast();
    	loadTeam(gridApi, api);
    });
    
    $("#goto-page").keyup(function () {
    	if(goto())
    		loadTeam(gridApi, api);
    });
    */
    /*$("#searchForm").on("keypress", function (event) {
        var keyPressed = event.keyCode || event.which; 
        if (keyPressed === 13) { 
        	event.preventDefault(); 
        	loadSecurityGrps(gridApi, api, colApi); 
            return false; 
        } 
    });
    loadSecurityGrps(gridApi, api, colApi);
});



var getSecurityGroupsUrl = "";

function loadSecurityGrps(gridApi, api, colApi) {
	if(getSecurityGroupsUrl == ""){
		//resetGridStatusBar();
		getSecurityGroupsUrl = getGridDataFetchUrl("SECURITY_GROUP");
	}
	var rowData =[];
	gridApi.setRowData(rowData);
	if(getSecurityGroupsUrl != null && getSecurityGroupsUrl != "" && getSecurityGroupsUrl !="undefined"){
		api.showLoadingOverlay();
		var formInput = $('#searchForm, #limitForm').serialize();

		$.ajax({
		  async: false,
		  url:getSecurityGroupsUrl,
		  type:"POST",
		  data: JSON.parse(JSON.stringify(formInput)),
		  success: function(data){

			  gridApi.setRowData(data.list);
			  //data.list=[];
			  //paginateHandler(data);
			  setTimeout(() => colApi.autoSizeAllColumns(), 1000);
		  }
		});
	}
}
*/
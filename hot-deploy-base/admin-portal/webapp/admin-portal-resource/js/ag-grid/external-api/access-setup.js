/*$("#loader").show();
$(function() {
	loadAgGrid();
});

var columnDefs = [
	{ 
        "headerName":"System Names",
        "field":"systemName",
        "headerCheckboxSelection":true,
        "checkboxSelection":true,
        "sortable":true,
        "filter":true,
        "filter":"agTextColumnFilter"
     },
     { 
        "headerName":"URL Access",
        "field":"urlAccess",
        "sortable":true,
        "filter":true,
        "filter":"agTextColumnFilter",
        "cellRenderer": params => `<a href="${ params.value }" >${params.value} <i class="fa fa-share" aria-hidden="true"></i></a>`
     },
     { 
        "headerName":"User ID",
        "field":"userId",
        "sortable":true,
        "filter":true,
        "filter":"agTextColumnFilter"
     },
     { 
        "headerName":"Password",
        "field":"password",
        "sortable":true,
        "filter":true,
        "filter":"agTextColumnFilter"
     },
     { 
        "headerName":"Auth Method",
        "field":"authMethod",
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
        "headerName":"Last Modified",
        "field":"lastModified",
        "sortable":true,
        "filter":true,
        "filter":"agTextColumnFilter"
     },
     { 
        "headerName":"Status",
        "field":"status",
        "sortable":true,
        "filter":true,
        "filter":"agTextColumnFilter"
     }
    ];

function loadAgGrid(){
	$("#grid1").empty();
	var gridOptions = {
		    defaultColDef: {
		        filter: true,
		        sortable: true,
		        resizable: true,
		        // allow every column to be aggregated
		        //enableValue: true,
		        // allow every column to be grouped
		        //enableRowGroup: true,
		        // allow every column to be pivoted
		        //enablePivot: true,
		    },
		    columnDefs: columnDefs,
		    rowData: getGridData(),
		    floatingFilter: true,
		    rowSelection: "multiple",
		    editType: "fullRow",
		    paginationPageSize: 10,
		    domLayout:"autoHeight",
		    pagination: true,
		    onFirstDataRendered: onFirstDataRendered
		};

		//lookup the container we want the Grid to use
		var eGridDiv = document.querySelector("#grid1");

		// create the grid passing in the div to use together with the columns & data we want to use
		new agGrid.Grid(eGridDiv, gridOptions);

}
function getGridData() {
    var result = [];
    var resultRes = null;
    var params = {}
    var paramStr = $("#searchForm").serialize();
    /*$('#searchForm :input:hidden').each(function(){
    	params[this.name] = this.value;
    }); */
   /* console.log("formData--->"+JSON.stringify(paramStr));
    var fromData = JSON.stringify(paramStr);
    $.ajax({
        type: "POST",
        url: "getAccessSetup",
        async: false,
        data: JSON.parse(fromData),
        success: function(data) {
            resultRes = data;
            result.push(data);
            console.log("--result-----" + result);
        }
    });
    return resultRes;
}
$("#loader").hide();
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
        fileName: "Access_Setup",
        exportMode: 'csv'
    };


    gridOptions.api.exportDataAsExcel(params);
}*/
//fagReady("ACCESS_SETUP", function(el, api, colApi, gridApi) {
//    $("#refresh-pref-btn").click(function() {
//        gridApi.refreshUserPreferences();
//    });
//    $("#save-pref-btn").click(function() {
//        gridApi.saveUserPreferences();
//    });
//    $("#clear-filter-btn").click(function() {
//        try {
//            gridApi.clearAllColumnFilters();
//        } catch (e) {}
//        gridApi.refreshUserPreferences();
//    });
//    $("#export-btn").click(function() {
//        gridApi.csvExport();
//    });
//    $("#main-search-btn").click(function() {
//        loadMainGrid(gridApi, api, colApi)
//    });
//    $("#insert-btn").click(function() {
//        gridApi.insertNewRow()
//    });
//    $("#remove-btn").click(function() {
//        //removeMainGrid(fag1, api);
//        gridApi.removeSelected();
//        setTimeout(() => {
//            loadMainGrid(gridApi, api, colApi)
//        }, 1000);
//    });
//    $("#searchForm").on("keypress", function (event) {
//        var keyPressed = event.keyCode || event.which;
//        if (keyPressed === 13) {
//        	loadMainGrid(gridApi, api, colApi);
//            event.preventDefault();
//            return false;
//        }
//    });
//    loadMainGrid(gridApi, api, colApi)
//});
//function loadMainGrid(gridApi,api,colApi) {
//    var rowData = [];
//    gridApi.setRowData(rowData);
//    api.showLoadingOverlay();
//    var formInput = $('#searchForm, #limitForm').serialize();
//    $.ajax({
//        async: false,
//        url: '/admin-portal/control/getAccessSetup',
//        type: "POST",
//        data: JSON.parse(JSON.stringify(formInput)),
//        success: function(data) {
//            gridApi.setRowData(data);
//            paginateHandler(data);
//        }
//    });
//}

$(function() {
	let accessSetupInstanceId= "ACCESS_SETUP";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = accessSetupInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#access-setup-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, accessSetupInstanceId, userId);
	});

	$('#access-setup-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, accessSetupInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getAccessSetupGridData();
		}
	});
	$('#access-setup-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	 $("#main-search-btn").click(function() {
		  getAccessSetupGridData();
	});
	$("#access-setup-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	 $("#searchForm").on("keypress", function (event) {
	  var keyPressed = event.keyCode || event.which;
	  if (keyPressed === 13) {
		  getAccessSetupGridData();
	      event.preventDefault();
	      return false;
	  }
	});
	function getAccessSetupGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/admin-portal/control/getAccessSetup";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm, #limitForm_ACCESS_SETUP";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getAccessSetupGridData();
	}
});
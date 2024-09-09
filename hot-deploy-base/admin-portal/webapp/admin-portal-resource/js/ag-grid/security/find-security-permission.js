$(function() {
	const securityPermissionInstanceId= 'SECURITY_PERMISSION';
	const externalLoginKey = $("input[name='externalLoginKey']").val();
	const formDataObject = {};
	const userId = $("input[name='userId']").val();
	let gridInstance  = "";
	let securityPermissionUrl = "";

	formDataObject.gridInstanceId = securityPermissionInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	formDataObject.dataFetchCall = getSecurityPermissionRowData;

	gridInstance = prepareGridInstance(formDataObject);

	if(securityPermissionUrl == ""){
		securityPermissionUrl = getGridDataFetchUrl("SECURITY_PERMISSION");
	}

	if(securityPermissionUrl == "" || securityPermissionUrl == null){
		securityPermissionUrl = "/admin-portal/control/getSecurityPermissions"
	}

	if(gridInstance){
		getSecurityPermissionRowData();
	}

	function getSecurityPermissionRowData() {
		const callCtx = {};
		callCtx.ajaxUrl = securityPermissionUrl;
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm";
		callCtx.ajaxResponseKey = "list";

		setGridData(gridInstance, callCtx);
	}

	$('#security-permission-save-pref').click(function(){
		saveGridPreference(gridInstance, securityPermissionInstanceId, userId);
	});
	$("#security-permission-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#security-permission-clear-pref').click(function(){
		clearGridPreference(gridInstance, securityPermissionInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getSecurityPermissionRowData();
		}
	});

	$('#security-permission-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});

	$("#main-search-btn").click(function () {
		getSecurityPermissionRowData();
	});

	$("#searchForm").on("keypress", function (event) {
		let keyPressed = event.keyCode || event.which;
		if(keyPressed === 13) {
			event.preventDefault();
			getSecurityPermissionRowData();
			return false;
		}
	});
});

/*
fagReady("SECURITY_PERMISSION", function(el, api, colApi, gridApi){
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

    $("#update-btn").click(function () {
    	gridApi.saveUpdates();
        setTimeout(() => {  loadSecurityPermission(gridApi, api, colApi); }, 1000);
    })

    $("#main-search-btn").click(function () {
    	loadSecurityPermission(gridApi, api, colApi);
    });
    $("#searchForm").on("keypress", function (event) {
        var keyPressed = event.keyCode || event.which; 
        if (keyPressed === 13) { 
        	event.preventDefault(); 
        	loadSecurityPermission(gridApi, api, colApi); 
            return false; 
        } 
    });
    loadSecurityPermission(gridApi, api, colApi);
});


var securityPermissionUrl = "";
function loadSecurityPermission(gridApi, api, colApi) {
	if(securityPermissionUrl == ""){
		resetGridStatusBar();
		securityPermissionUrl = getGridDataFetchUrl("SECURITY_PERMISSION");
	}
	var rowData =[];
	gridApi.setRowData(rowData);
	if(securityPermissionUrl != null && securityPermissionUrl != "" && securityPermissionUrl !="undefined"){
		api.showLoadingOverlay();
		var formInput = $('#searchForm, #limitForm').serialize();
		$.ajax({
		  async: false,
		  url:securityPermissionUrl,
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
/*
$("#loader").show();
$(function() {
	loadAgGrid();
});

var columnDefs = [{
    "headerName": "Permission ID",
    "field": "permissionId",
    "sortable": true,
    "filter": true,
    "filter": "agTextColumnFilter"
  }, {
    "headerName": "Description",
    "field": "description",
    "sortable": true,
    "filter": true,
    "filter": "agTextColumnFilter"
  },
  {
    headerName: "",
    "cellRenderer": params => `<a href="updateSecurityPermission?permissionId=${params.data.permissionId}" class="btn btn-xs btn-danger" title="Update"> <i class="fa fa-edit" aria-hidden="true"></i> </a>`,
    width: 100,
    pinned: 'right'
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
    var resultRes = null;
    var params = {}
    var paramStr = $("#findPermissionForm").serialize();
    
    console.log("formData--->"+JSON.stringify(paramStr));
    var fromData = JSON.stringify(paramStr);
    $.ajax({
        type: "POST",
        url: "getSecurityPermissions",
        async: false,
        data: JSON.parse(fromData),
        success: function(data) {
            resultRes = data;
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
        fileName: "Service_Request_Type",
        exportMode: 'csv'
    };


    gridOptions.api.exportDataAsCsv(params);
}
*/
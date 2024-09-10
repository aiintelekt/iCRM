$(function() {
	const securityGroupPermissionsInstanceId= 'SECURITY_GRP_PERM';
	const externalLoginKey = $("input[name='externalLoginKey']").val();
	const formDataObject = {};
	const userId = $("input[name='userId']").val();
	let gridInstance  = "";
	let securityGroupPermissionsUrl = "";

	formDataObject.gridInstanceId = securityGroupPermissionsInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	formDataObject.dataFetchCall = getSecurityGroupPermissionsRowData;

	gridInstance = prepareGridInstance(formDataObject);

	if(securityGroupPermissionsUrl == ""){
		securityGroupPermissionsUrl = getGridDataFetchUrl("SECURITY_GRP_PERM");
	}

	if(securityGroupPermissionsUrl == "" || securityGroupPermissionsUrl == null){
		securityGroupPermissionsUrl = "/admin-portal/control/getPermissionsForSecurityGroups"
	}

	if(gridInstance){
		getSecurityGroupPermissionsRowData();
	}

	function getSecurityGroupPermissionsRowData() {
		const callCtx = {};
		callCtx.ajaxUrl = securityGroupPermissionsUrl;
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm";
		callCtx.ajaxResponseKey = "";

		setGridData(gridInstance, callCtx);
	}

	$('#security-group-save-pref').click(function(){
		saveGridPreference(gridInstance, securityGroupPermissionsInstanceId, userId);
	});
	$("#security-group-permissions-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#security-group-clear-pref').click(function(){
		clearGridPreference(gridInstance, securityGroupPermissionsInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getSecurityGroupPermissionsRowData();
		}
	});

	$('#security-group-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});

	$("#main-search-btn").click(function () {
		getSecurityGroupPermissionsRowData();
	});

	$("#searchForm").on("keypress", function (event) {
		let keyPressed = event.keyCode || event.which;
		if(keyPressed === 13) {
			event.preventDefault();
			getSecurityGroupPermissionsRowData();
			return false;
		}
	});
});

/*$("#loader").show();
$(function() {
	loadAgGrid();
});

var columnDefs = [{ 
	   "headerName":"Permission ID",
	   "field":"permissionId",
	   "sortable":true,
	   "filter":true,
	   "filter":"agTextColumnFilter",
	   "suppressSizeToFit":true,
	   "headerCheckboxSelection":true,
	   "checkboxSelection":true,
	   "width":779
	},
	{ 
	   "headerName":"Description",
	   "field":"description",
	   "sortable":true,
	   "filter":true,
	   "filter":"agTextColumnFilter",
	   "suppressSizeToFit":true,
	   "width":779
	}
];
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
function loadAgGrid(){
	$("#grid1").empty();
		//lookup the container we want the Grid to use
		var eGridDiv = document.querySelector("#grid1");

		// create the grid passing in the div to use together with the columns & data we want to use
		new agGrid.Grid(eGridDiv, gridOptions);

}
function getGridData() {
    var resultRes = null;
    var params = {}
   // var paramStr = $("#findSecurityRoleForm").serialize();
    /*
    $('#findPermissionForm :input:hidden').each(function(){
        params[this.name] = this.value;
    }); *
    //console.log("formData--->"+JSON.stringify(paramStr));
    //var fromData = JSON.stringify(paramStr);
    $.ajax({
        type: "POST",
        url: "getPermissionsForSecurityGroups",
        async: false,
        data:  { "groupId": $("#groupId").val() },
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
    if(selectedData !=null && selectedData != "" && selectedData != 'undefined'){
        $('#removeSecurityPermissionForm input[name=permissionIds]').val(JSON.stringify(selectedData));
        $('#removeSecurityPermissionForm').submit();
    } else{
        showAlert("error","Please select atleast one row");
    }
    
    
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
        fileName: "Security_Groups",
        exportMode: 'csv'
    };


    gridOptions.api.exportDataAsExcel(params);
}

*/
/*
fagReady("SECURITY_GRP_PERM", function(el, api, colApi, gridApi){
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
    	loadSecurityPermissions(gridApi, api, colApi);
    });
    $("#remove-per-btn").click(function () {
    	var selectedRows=api.getSelectedRows();
    	if(selectedRows!= undefined && selectedRows != null&& selectedRows.length>0){
    		gridApi.removeSelected();
            setTimeout(() => { loadSecurityPermissions(gridApi, api, colApi); }, 1000);
    	} else {
            showAlert("error","Please select atleast one record in the list");
        }
    });
    loadSecurityPermissions(gridApi, api, colApi);
});



var getSecurityPermUrl = "";
function loadSecurityPermissions(gridApi, api, colApi) {
	if(getSecurityPermUrl == ""){
		//resetGridStatusBar();
		getSecurityPermUrl = getGridDataFetchUrl("SECURITY_GRP_PERM");

	}
	var rowData =[];
	gridApi.setRowData(rowData);
	if(getSecurityPermUrl != null && getSecurityPermUrl != "" && getSecurityPermUrl !="undefined"){
		api.showLoadingOverlay();
		var formInput = $('#searchForm, #limitForm').serialize();

		$.ajax({
		  async: false,
		  url:getSecurityPermUrl,
		  type:"POST",
		  data: JSON.parse(JSON.stringify(formInput)),
		  success: function(data){

			  gridApi.setRowData(data);
			  //data.list=[];
			  //paginateHandler(data);
			  setTimeout(() => colApi.autoSizeAllColumns(), 1000);
		  }
		});
	}
}
*/

/*$("#loader1").show();
$(function() {
	loadSecurityAgGrid();
});

var columnSecurityDefs = [
	{ 
	   "headerName":"Group Id",
	   "field":"groupId",
	   "sortable":true,
	   "filter":true
	},
	{ 
	   "headerName":"Security Type",
	   "field":"securityType",
	   "sortable":true,
	   "filter":true
	},
	{ 
	   "headerName":"Description",
	   "field":"description",
	   "sortable":true,
	   "filter":true
	}
	];

function loadSecurityAgGrid(){
	$("#grid2").empty();
	var gridSecurityOptions = {
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
		    columnDefs: columnSecurityDefs,
		    rowData: getSecurityGridData(),
		    floatingFilter: true,
		    rowSelection: "multiple",
		    editType: "fullRow",
		    paginationPageSize: 10,
		    domLayout:"autoHeight",
		    pagination: true,
		    onFirstDataRendered: onFirstSecurityDataRendered
		};

		//lookup the container we want the Grid to use
		var eGridDiv = document.querySelector("#grid2");

		// create the grid passing in the div to use together with the columns & data we want to use
		new agGrid.Grid(eGridDiv, gridSecurityOptions);

}
function getSecurityGridData() {
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
        url: "getDerivedSecurity",
        async: false,
        data:  { "partyId": $("#partyId").val() },
        success: function(data) {
            resultRes = data;
        }
    });
    return resultRes;
}
$("#loader1").hide();
//data binding while loading the page
function onFirstSecurityDataRendered(params) {
    params.api.sizeColumnsToFit();
}
function clearData() {
    gridSecurityOptions.api.setRowData([]);
}

function onAddRow() {
    var newItem = createNewRowData();
    var res = gridSecurityOptions.api.updateRowData({
        add: [newItem]
    });
    printResult(res);
}

function addItems() {
    var newItems = [createNewRowData(), createNewRowData(), createNewRowData()];
    var res = gridSecurityOptions.api.updateRowData({
        add: newItems
    });
    printResult(res);
}

function addItemsAtIndex() {
    var newItems = [createNewRowData(), createNewRowData(), createNewRowData()];
    var res = gridSecurityOptions.api.updateRowData({
        add: newItems,
        addIndex: 2
    });
    printResult(res);
}

function updateItems() {
    // update the first 5 items
    var itemsToUpdate = [];
    gridSecurityOptions.api.forEachNodeAfterFilterAndSort(function(rowNode, index) {
        // only do first 5
        if (index >= 5) {
            return;
        }

        var data = rowNode.data;
        data.price = Math.floor((Math.random() * 20000) + 20000);
        itemsToUpdate.push(data);
    });
    var res = gridSecurityOptions.api.updateRowData({
        update: itemsToUpdate
    });
    printResult(res);
}

function onInsertRowAt2() {
    var newItem = createNewRowData();
    var res = gridSecurityOptions.api.updateRowData({
        add: [newItem],
        addIndex: 2
    });
    printResult(res);
}

function onRemoveSelected() {
    var selectedData = gridSecurityOptions.api.getSelectedRows();
    console.log(selectedData.length);
    for (i = 0; i <= selectedData.length; i++) {
    }
    var res = gridSecurityOptions.api.updateRowData({
        remove: selectedData
    });

    printResult(res);
}

function removeSubmit() {
    var selectedData = gridSecurityOptions.api.getSelectedRows();
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
    gridSecurityOptions.api.sizeColumnsToFit();
}

function getSelectedRows() {
    const selectedNodes = gridSecurityOptions.api.getSelectedNodes()
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
        fileName: "User_Securities",
        exportMode: 'csv'
    };


    gridSecurityOptions.api.exportDataAsExcel(params);
}
*/


//fagReady("DERIVED_SECURITY", function(el, api, colApi, gridApi){
//    $("#derived-security-ref-pref").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#derived-security-save-pref").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#derived-security-clear-filter").click(function () {
//    	gridApi.clearAllColumnFilters();
//    });
//    $("#derived-security-export").click(function () {
//    	gridApi.csvExport();
//    });
//	$("#sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
    /*
    $("#update-btn").click(function () {
    	gridApi.saveUpdates();
        setTimeout(() => {  loadDerivedSecurity(gridApi, api); }, 1000);
    })

    $("#main-search-btn").click(function () {
    	loadDerivedSecurity(gridApi, api);
    });
    $("#insert-btn").click(function () {
    	gridApi.insertNewRow()
    })
    $("#remove-btn").click(function () {
        //removeMainGrid(fag1, api);
    	gridApi.removeSelected();
        setTimeout(() => {  loadDerivedSecurity(gridApi, api); }, 1000);
        
    }); */
//    loadDerivedSecurity(gridApi, api);
//});
//
//var derivedSecurityUrl = "";
//function loadDerivedSecurity(gridApi, api) {
//	if(derivedSecurityUrl == ""){
//		//resetGridStatusBar();
//		derivedSecurityUrl = getGridDataFetchUrl("DERIVED_SECURITY");
//	}
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(derivedSecurityUrl != null && derivedSecurityUrl != "" && derivedSecurityUrl !="undefined"){
//		api.showLoadingOverlay();
//		var formInput = $('#searchForm, #limitForm').serialize();
//		$.ajax({
//		  async: false,
//		  url:derivedSecurityUrl,
//		  type:"POST",
//		  data: { "partyId": $("#partyId").val()},
//		  success: function(data){
//			  gridApi.setRowData(data);
//			  //data.list=[];
//			  //paginateHandler(data);
//		  }
//		});
//	}
//}

$(function() {
	let derivedSecurityInstanceId= "DERIVED_SECURITY";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = derivedSecurityInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#derived-security-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, derivedSecurityInstanceId, userId);
	});
	$("#derived-security-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#dervied-security-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, derivedSecurityInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getDerviedSecurityListGridData();
		}
	});
	$('#derived-security-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
    $("#main-search-btn").click(function () {
		getDerviedSecurityListGridData();
    });
	function getDerviedSecurityListGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("DERIVED_SECURITY");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#customSecurityAndDerivedForm";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getDerviedSecurityListGridData();
	}
});
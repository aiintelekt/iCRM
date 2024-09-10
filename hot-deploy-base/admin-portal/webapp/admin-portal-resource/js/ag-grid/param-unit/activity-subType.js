
fagReady("PARAM_ACT_SUB_TYP", function(el, api, colApi, gridApi){
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
        setTimeout(() => {  loadMainGrid(gridApi); }, 1000);
    })

    $("#main-search-btn").click(function () {
    	loadMainGrid(gridApi);
    });
    
    loadMainGrid(gridApi);
});

function loadMainGrid(gridApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	$.ajax({
	  async: false,
	  url:'/admin-portal/control/getActivitySubType',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#searchForm").serialize())),
	  success: function(data){
		  gridApi.setRowData(data);
	  }
	});
}

/*
$("#loader").show();
$(function() {
	loadAgGrid();
});

var columnDefs = [
	{ 	"headerName":"Activity Parent", 
		"field":"grandparentValue",
		"sortable":true,
		"filter":"agTextColumnFilter" 
	},
	{ 	"headerName":"Activity Type", 
		"field":"parentValue", 
		"sortable":true, 
		"filter":true 
	}, 
	{ 	"headerName":"Activity Sub Type", 
		"field":"activitySubTypeDesc",
		"sortable":true, 
		"filter":true, 
		"cellRenderer":params => `<a href="viewActivitySubTypes?enumId=${params.data.enumId}">${params.value}</a>`
	}, 
	{ 	"headerName":"Sequence Number",
		"field":"sequenceId", 
		"sortable":true, 
		"filter":true 
	},
	{ 	"headerName":"Created On", 
		"field":"createdOn",
		"sortable":true,
		"filter":true
	},
	{	"headerName":"Created By", 
		"field":"createdBy",
		"sortable":true, 
		"filter":true 
	}, 
	{ 	"headerName":"Modified On", 
		"field":"modifiedOn", 
		"sortable":true, 
		"filter":true 
	}, 
	{ 	"headerName":"Modified By", 
		"field":"modifiedBy", 
		"sortable":true, 
		"filter":true 
	}, 
	{ 	"headerName":"Status", 
		"field":"statusDesc", 
		"sortable":true,
		"filter":true 
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
    
    console.log("formData--->"+JSON.stringify(paramStr));
    var fromData = JSON.stringify(paramStr);
    $.ajax({
        type: "POST",
        url: "getActivitySubType",
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
        fileName: "Activity_Sub_Type",
        exportMode: 'csv'
    };


    gridOptions.api.exportDataAsCsv(params);
}
*/
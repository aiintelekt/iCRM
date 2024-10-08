$("#loader").show();
$(function() {
	loadAgGrid();
});

var columnDefs = [
	{
	    "headerName": "Business Unit Name",
	    "field": "businessUnitName",
	    "sortable": true,
	    "filter": true,
		cellRenderer: params =>
	     `<a href="viewBusinessUnits_old?productStoreGroupId=${params.data.productStoreGroupId}">${params.data.productStoreGroupName}</a>`
	  },{
	    "headerName": "Parent BU",
	    "field": "parentBu",
	    "sortable": true,
	    "filter": true
	  }, {
	    "headerName": "BU ID",
	    "field": "buId",
	    "sortable": true,
	    "filter": true
	  }, {
	    "headerName": "BU Type",
	    "field": "buType",
	    "sortable": true,
	    "filter": true
	  }, {
	    "headerName": "BU Status",
	    "field": "buStatus",
	    "sortable": true,
	    "filter": true
	  }, {
	    "headerName": "Website",
	    "field": "website",
	    "sortable": true,
	    "filter": true
	  }, {
	    "headerName": "City",
	    "field": "city",
	    "sortable": true,
	    "filter": true
	  }, {
	    "headerName": "State",
	    "field": "state",
	    "sortable": true,
	    "filter": true
	  }, {
	    "headerName": "Country",
	    "field": "country",
	    "sortable": true,
	    "filter": true
	  }, {
	    "headerName": "Created By",
	    "field": "createdBy",
	    "sortable": true,
	    "filter": true
	  }, {
	    "headerName": "Created On",
	    "field": "createdOn",
	    "sortable": true,
	    "filter": true
	  }, {
	    "headerName": "Modified By",
	    "field": "modifiedBy",
	    "sortable": true,
	    "filter": true
	  }, {
	    "headerName": "Modified On",
	    "field": "modifiedOn",
	    "sortable": true,
	    "filter": true
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
    console.log("formData--->"+JSON.stringify(paramStr));
    var fromData = JSON.stringify(paramStr);
    $.ajax({
        type: "POST",
        url: "getBusinessUnit",
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
        fileName: "Business-Unit",
        exportMode: 'csv'
    };


    gridOptions.api.exportDataAsCsv(params);
}
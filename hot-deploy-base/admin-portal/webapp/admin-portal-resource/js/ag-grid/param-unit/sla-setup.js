
//fagReady("PARAM_SLA_STP", function(el, api, colApi, gridApi){
//    $("#refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#clear-filter-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    	gridApi.refreshUserPreferences();
//    });
//	$("#sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//
//    $("#update-btn").click(function () {
//    	gridApi.saveUpdates();
//        setTimeout(() => {  loadSlaSetup(gridApi, api); }, 1000);
//    })
//
//    $("#main-search-btn").click(function () {
//    	loadSlaSetup(gridApi, api);
//    });
//    $("#searchForm").on("keypress", function (event) {
//        var keyPressed = event.keyCode || event.which; 
//        if (keyPressed === 13) { 
//        	event.preventDefault(); 
//        	loadSlaSetup(gridApi, api); 
//            return false; 
//        } 
//    });
//    loadSlaSetup(gridApi, api);
//});
//
//var srTypeAuditLogUrl = "";
//function loadSlaSetup(gridApi, api) {
//	if(srTypeAuditLogUrl == ""){
//		srTypeAuditLogUrl = getGridDataFetchUrl("PARAM_SLA_STP");
//	}
//	
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(srTypeAuditLogUrl != null && srTypeAuditLogUrl != "" && srTypeAuditLogUrl !="undefined"){
//		api.showLoadingOverlay();
//		var formInput = $('#searchForm').serialize();
//		$.ajax({
//		  async: false,
//		  url:srTypeAuditLogUrl,
//		  type:"POST",
//		  data: JSON.parse(JSON.stringify(formInput)),
//		  success: function(data){
//			  gridApi.setRowData(data);
//			  //data.list=[];
//			  //paginateHandler(data);
//		  }
//		});
//	}
//}

/*
$("#loader").show();
$(function() {
	loadAgGrid();
});

var columnDefs = [{
    "headerName": "SR Type",
    "field": "srTypeId",
    "headerCheckboxSelection": true,
    "checkboxSelection": true,
    "sortable": true,
    "filter": true,
    "filter": "agTextColumnFilter",
    "cellRenderer": params => `<a href=viewSlaSetup?slaConfigId=${params.data.slaConfigId} id="viewSlaSetup">${params.value}</a>`    
},
  {
    "headerName": "SR Priority",
    "field": "srPriority",
    "sortable": true,
    "filter": true,
    "filter": "agTextColumnFilter"
  },
  {
    "headerName": "SR Category",
    "field": "srCategoryId",
    "sortable": true,
    "filter": true,
    "filter": "agTextColumnFilter"
  },
  {
    "headerName": "SR Sub Category",
    "field": "srSubCategoryId",
    "sortable": true,
    "filter": true,
    "filter": "agTextColumnFilter"
  },
  {
    "headerName": "Status",
    "field": "status",
    "sortable": true,
    "filter": true,
    "filter": "agTextColumnFilter"
  },
  {
    "headerName": "SLA for Resolution",
    "field": "slaPeriodUnit",
    "sortable": true,
    "filter": true,
    "filter": "agTextColumnFilter"
  },
  {
    "headerName": "SR Resolution Unit",
    "field": "slaPeriodLvl",
    "sortable": true,
    "filter": true,
    "filter": "agTextColumnFilter"
  },
  {
    "headerName": "SLA for First Escalation",
    "field": "slaEscPeriodHrsLvl1",
    "sortable": true,
    "filter": true,
    "filter": "agTextColumnFilter"
  },
  {
    "headerName": "First Escalation Unit",
    "field": "slaPeriodLvl1",
    "sortable": true,
    "filter": true,
    "filter": "agTextColumnFilter"
  },
  {
    "headerName": "SLA for Second Escalation",
    "field": "slaEscPeriodHrsLvl2",
    "sortable": true,
    "filter": true,
    "filter": "agTextColumnFilter"
  },
  {
    "headerName": "Second Escalation Unit",
    "field": "slaPeriodLvl2",
    "sortable": true,
    "filter": true,
    "filter": "agTextColumnFilter"
  },
  {
    "headerName": "SLA for Third Escalation",
    "field": "slaEscPeriodHrsLvl3",
    "sortable": true,
    "filter": true,
    "filter": "agTextColumnFilter"
  },
  {
    "headerName": "Third Escalation Unit",
    "field": "slaPeriodLvl3",
    "sortable": true,
    "filter": true,
    "filter": "agTextColumnFilter"
  },
  {
    "headerName": "Created On",
    "field": "createdDate",
    "sortable": true,
    "filter": true,
    "filter": "agTextColumnFilter"
  },
  {
    "headerName": "Created By",
    "field": "createdBy",
    "sortable": true,
    "filter": true,
    "filter": "agTextColumnFilter"
  },
  {
    "headerName": "Modified On",
    "field": "modifiedDate",
    "sortable": true,
    "filter": true,
    "filter": "agTextColumnFilter"
  },
  {
    "headerName": "Modified By",
    "field": "modifiedBy",
    "sortable": true,
    "filter": true,
    "filter": "agTextColumnFilter"
  }
];
var gridOptions = null;
function loadAgGrid(){
	$("#grid1").empty();
	gridOptions = {
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
        url: "getSlaSetups",
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
        fileName: "Sla_setup",
        exportMode: 'csv'
    };


    gridOptions.api.exportDataAsCsv(params);
}
*/

$(function() {
	let paramSlaInstanceId= "PARAM_SLA_STP";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = paramSlaInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#sla-setup-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, paramSlaInstanceId, userId);
	});
	$("#sla-setup-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#sla-setup-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, paramSlaInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getParamSlaGridData();
		}
	});
	$('#sla-setup-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#main-search-btn").click(function () {
		getParamSlaGridData();
	});
	$("#searchForm").on("keypress", function (event) {
	    var keyPressed = event.keyCode || event.which; 
	    if (keyPressed === 13) { 
	    	event.preventDefault(); 
	    	getParamSlaGridData();
	        return false; 
	    } 
	});
	function getParamSlaGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("PARAM_SLA_STP");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getParamSlaGridData();
	}
});

//fagReady("PARAM_CAL_NW_DAY", function(el, api, colApi, gridApi){
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
//        setTimeout(() => {  loadCalenderSetup(gridApi, api); }, 1000);
//    })
//
//    $("#main-search-btn").click(function () {
//    	loadCalenderSetup(gridApi, api);
//    });
//    $("#searchForm").on("keypress", function (event) {
//        var keyPressed = event.keyCode || event.which; 
//        if (keyPressed === 13) { 
//        	event.preventDefault(); 
//        	loadCalenderSetup(gridApi, api); 
//            return false; 
//        } 
//    });
//    loadCalenderSetup(gridApi, api);
//});
//
//var calenderSetupUrl = "";
//function loadCalenderSetup(gridApi, api) {
//	if(calenderSetupUrl == ""){
//		calenderSetupUrl = getGridDataFetchUrl("PARAM_CAL_NW_DAY");
//	}
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(calenderSetupUrl != null && calenderSetupUrl != "" && calenderSetupUrl !="undefined"){
//		api.showLoadingOverlay();
//		var formInput = $('#searchForm, #limitForm').serialize();
//		$.ajax({
//		  async: false,
//		  url:calenderSetupUrl,
//		  type:"POST",
//		  data: JSON.parse(JSON.stringify(formInput)),
//		  success: function(data){
//			  gridApi.setRowData(data);
//		  }
//		});
//	}
//}

/*
$("#loader").show();
$(function() {
	loadAgGrid();
});

var columnDefs = [ 
    { 
        "headerName":"Non Working Date",
        "field":"nonWorkingDate",
        "headerCheckboxSelection":true,
        "checkboxSelection":true,
        "sortable":true,
        "filter":"agTextColumnFilter",
        "cellRenderer":params => `<a href=viewNonWorkingDay?holidayConfigId=${params.data.holidayConfigId} id="viewNonWorkingDay">${params.value}</a>`
     },
     { 
        "headerName":"Non Working Date Desc",
        "field":"holidayDescription",
        "sortable":true,
        "filter":"agTextColumnFilter"
     },
     { 
        "headerName":"Holiday",
        "field":"isHoliday",
        "sortable":true,
        "filter":"agTextColumnFilter"
     },
     { 
        "headerName":"Weekend",
        "field":"isWeekend",
        "sortable":true,
        "filter":"agTextColumnFilter"
     },
     { 
        "headerName":"Non Working Day",
        "field":"isNonWorkingDay",
        "sortable":true,
        "filter":"agTextColumnFilter"
     },
    
     { 
        "headerName":"Status",
        "field":"status",
        "sortable":true,
        "filter":"agTextColumnFilter"
     },
     { 
        "headerName":"Created On",
        "field":"createdDate",
        "sortable":true,
        "filter":"agTextColumnFilter"
     },
     { 
        "headerName":"Created By",
        "field":"createdBy",
        "sortable":true,
        "filter":"agTextColumnFilter"
     },
     { 
        "headerName":"Modified On",
        "field":"modifiedDate",
        "sortable":true,
        "filter":"agTextColumnFilter"
     },
     { 
        "headerName":"Modified By",
        "field":"modifiedBy",
        "sortable":true,
        "filter":"agTextColumnFilter"
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
    //var paramStr = $("#searchForm").serialize();
    var query = window.location.search.substring(1);
    var parsed_qs = parse_query_string(query);
    var nonWorkingDate = parsed_qs.nonWorkingDate;
    var status = parsed_qs.status;
    
    if(nonWorkingDate == undefined){
    	nonWorkingDate = $("#nonWorkingDate").val();
    }
    if(status == undefined){
    	status = $("#status").val();
    }
    //console.log("formData--->"+JSON.stringify(paramStr));
    //var fromData = JSON.stringify(paramStr);
    $.ajax({
        type: "POST",
        url: "getCalenderHolidayList",
        async: false,
        //data: JSON.parse(fromData),
        data: { "nonWorkingDate": nonWorkingDate,"status":status},
        success: function(data) {
            resultRes = data;
            result.push(data);
            console.log("--result-----" + result);
        }
    });
    return resultRes;
}
function parse_query_string(query) {
    var vars = query.split("&");
    var query_string = {};
    for (var i = 0; i < vars.length; i++) {
      var pair = vars[i].split("=");
      var key = decodeURIComponent(pair[0]);
      var value = decodeURIComponent(pair[1]);
      // If first entry with this name
     if (typeof query_string[key] === "undefined") {
       query_string[key] = decodeURIComponent(value);
       // If second entry with this name
     } else if (typeof query_string[key] === "string") {
       var arr = [query_string[key], decodeURIComponent(value)];
       query_string[key] = arr;
       // If third or later entry with this name
     } else {
       query_string[key].push(decodeURIComponent(value));
     }
   }
   return query_string;
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
        fileName: "Calender_setup",
       exportMode: 'csv'
    };


    gridOptions.api.exportDataAsCsv(params);
}
*/

$(function() {
	let paramCalInstanceId= "PARAM_CAL_NW_DAY";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = paramCalInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#cal-holiday-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, paramCalInstanceId, userId);
	});
	$("#cal-holiday-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#cal-holiday-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, paramCalInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getParamNonWorkingGridData();
		}
	});
	$('#cal-holiday-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#main-search-btn").click(function () {
		getParamNonWorkingGridData();
	});
	$("#searchForm").on("keypress", function (event) {
	    var keyPressed = event.keyCode || event.which; 
	    if (keyPressed === 13) { 
	    	event.preventDefault(); 
	    	getParamNonWorkingGridData();
	        return false; 
	    } 
	});
	function getParamNonWorkingGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("PARAM_CAL_NW_DAY");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm, #limitForm_PARAM_CAL_NW_DAY";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getParamNonWorkingGridData();
	}
});
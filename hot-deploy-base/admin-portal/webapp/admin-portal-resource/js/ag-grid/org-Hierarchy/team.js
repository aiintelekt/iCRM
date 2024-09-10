/*$("#loader").show();
$(function() {
	loadAgGrid();
});

var columnDefs = [
	{
	    "headerName": "Team Name",
	    "field": "teamName",
	    "sortable": true,
	    "filter": true,
	    "cellRenderer": params => `<a href="viewTeam?emplTeamId=${params.data.emplTeamId}">${params.value}</a>`
	  }, {
	    "headerName": "Team ID",
	    "field": "emplTeamId",
		"sortable": true,
	    "filter": true
	  }, {
	    "headerName": "Business Unit",
	    "field": "buName",
		"sortable": true,
	    "filter": true
	  }, {
	    "headerName": "Team Status",
	    "field": "status",
		"sortable": true,
	    "filter": true
	  }, {
	    "headerName": "Created On",
	    "field": "createdDate",
	    "sortable": true,
	    "filter": true
	  }, {
	    "headerName": "Created By",
	    "field": "created",
	    "sortable": true,
	    "filter": true
	  }, {
	    "headerName": "Modified On",
	    "field": "modifiedDate",
	    "sortable": true,
	    "filter": true
	  }, {
	    "headerName": "Modified By",
	    "field": "modified",
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
    $('#searchForm :input:hidden').each(function(){
    	params[this.name] = this.value;
    }); 
    console.log("formData--->"+JSON.stringify(paramStr));
    var fromData = JSON.stringify(paramStr);
    $.ajax({
        type: "POST",
        url: "getTeam",
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
        fileName: "Teams",
        exportMode: 'csv'
    };


    gridOptions.api.exportDataAsCsv(params);
}

*/


//fagReady("ORG_TEAM", function(el, api, colApi, gridApi){
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
//    $("#main-search-btn").click(function () {
//    	loadTeam(gridApi, api);
//    });
   
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
//    $("#searchForm").on("keypress", function (event) {
//        var keyPressed = event.keyCode || event.which; 
//        if (keyPressed === 13) { 
//        	event.preventDefault(); 
//        	loadTeam(gridApi, api);
//            return false; 
//        } 
//    });
//    loadTeam(gridApi, api);
//});



//var getTeamUrl = "";
//function loadTeam(gridApi, api) {
//	if(getTeamUrl == ""){
//		//resetGridStatusBar();
//		getTeamUrl = getGridDataFetchUrl("ORG_TEAM");
//	}
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(getTeamUrl != null && getTeamUrl != "" && getTeamUrl !="undefined"){
//		api.showLoadingOverlay();
//		var formInput = $('#searchForm, #limitForm').serialize();
//		$.ajax({
//		  async: false,
//		  url:getTeamUrl,
//		  type:"POST",
//		  data: JSON.parse(JSON.stringify(formInput)),
//		  success: function(data){
//			  gridApi.setRowData(data.list);
//			  //data.list=[];
//			  //paginateHandler(data);
//		  }
//		});
//	}
//}

$(function() {
	let teamsListInstanceId= "ORG_TEAM";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = teamsListInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#teams-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, teamsListInstanceId, userId);
	});
	$('#teams-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, teamsListInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getTeamsListGridData();
		}
	});
	$('#teams-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#teams-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
    $("#searchForm").on("keypress", function (event) {
        var keyPressed = event.keyCode || event.which; 
        if (keyPressed === 13) { 
        	event.preventDefault(); 
    		getTeamsListGridData();
            return false; 
        } 
    });
    $("#main-search-btn").click(function () {
		getTeamsListGridData();
    });
	function getTeamsListGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("ORG_TEAM");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm, #limitForm_ORG_TEAM";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getTeamsListGridData();
	}
});
function teamName(params){
	return `<a href="viewTeam?emplTeamId=${params.data.emplTeamId}">${params.value}</a>`;
}
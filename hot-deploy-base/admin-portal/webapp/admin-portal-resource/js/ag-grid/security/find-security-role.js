$(function() {
	const securityRoleInstanceId= 'SECURITY_ROLE';
	const externalLoginKey = $("input[name='externalLoginKey']").val();
	const formDataObject = {};
	const userId = $("input[name='userId']").val();
	let gridInstance  = "";
	let securityRoleUrl = "";

	formDataObject.gridInstanceId = securityRoleInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	formDataObject.dataFetchCall = getSecurityGroupPermissionsRowData;

	gridInstance = prepareGridInstance(formDataObject);

	if(securityRoleUrl == ""){
		securityRoleUrl = getGridDataFetchUrl("SECURITY_ROLE");
	}

	if(securityRoleUrl == "" || securityRoleUrl == null){
		securityRoleUrl = "/admin-portal/control/getRoles"
	}

	if(gridInstance){
		getSecurityGroupPermissionsRowData();
	}

	function getSecurityGroupPermissionsRowData() {
		const callCtx = {};
		callCtx.ajaxUrl = securityRoleUrl;
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm";
		callCtx.ajaxResponseKey = "";

		setGridData(gridInstance, callCtx);
	}

	$('#security-group-save-pref').click(function(){
		saveGridPreference(gridInstance, securityRoleInstanceId, userId);
	});

	$('#security-group-clear-pref').click(function(){
		clearGridPreference(gridInstance, securityRoleInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getSecurityGroupPermissionsRowData();
		}
	});
	$("#security-role-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
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
/*
fagReady("SECURITY_ROLE", function(el, api, colApi, gridApi){
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
        setTimeout(() => {  loadSecurityRoles(gridApi, api, colApi); }, 1000);
    })

    $("#main-search-btn").click(function () {
    	loadSecurityRoles(gridApi, api, colApi);
    });
    
    loadSecurityRoles(gridApi, api, colApi);
});

var findSecurityRoleUrl= "";
function loadSecurityRoles(gridApi, api, colApi) {
	if(findSecurityRoleUrl == ""){
		resetGridStatusBar();
		findSecurityRoleUrl = getGridDataFetchUrl("SECURITY_ROLE");
	}
	var rowData =[];
	gridApi.setRowData(rowData);
	if(findSecurityRoleUrl != null && findSecurityRoleUrl != "" && findSecurityRoleUrl !="undefined"){
		api.showLoadingOverlay();
		var formInput = $('#searchForm, #limitForm').serialize();
		$.ajax({
		  async: true,
		  url:findSecurityRoleUrl,
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
/*
$("#loader").show();
$(function() {
	loadAgGrid();
});

var columnDefs = [{
    "headerName": "Roles",
    "field": "roleTypeId",
    "sortable": true,
    "filter": true,
    "filter": "agTextColumnFilter",
     "cellRenderer": params =>
      `<a href="viewSecurityRole?roleTypeId=${params.data.roleTypeId}">${params.value}</a>`
  },{
	    "headerName": "Role Description",
	    "field": "description",
	    "sortable": true,
	    "filter": true,
	    "filter": "agTextColumnFilter"
  },
  {
    "headerName": "View Users",
    "filter": "agTextColumnFilter",
    "cellRenderer": params =>
      `<a href="viewRoleUsers?roleTypeId=${params.data.roleTypeId}" class="btn btn-primary btn-xs"> <i class="fa fa-eye"></i> </a>`
  }, {
    "headerName": "View Operations",
    "filter": "agTextColumnFilter",
    "cellRenderer": params =>
      `<a href="configureOperationLevels?roleTypeId=${params.data.roleTypeId}" class="btn btn-primary btn-xs"> <i class="fa fa-lock"></i></a>`
  }];

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
    
    var query = window.location.search.substring(1);
    var parsed_qs = parse_query_string(query);
    var role = parsed_qs.role;
    if(role == undefined){
    	role = $("#role").val();
    }
    $.ajax({
        type: "POST",
        url: "getRoles",
        async: false,
        //data: JSON.parse(fromData),
        data: { "roleTypeId": role},
        success: function(data) {
            resultRes = data;
        }
    });
    return resultRes;
}
$("#loader").hide();
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
        fileName: "Security_Roles",
        exportMode: 'xlsx'
    };


    gridOptions.api.exportDataAsExcel(params);
}
*/
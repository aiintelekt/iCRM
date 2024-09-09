/*$("#loader").show();
$(function() {
	loadRolesAgGrid();
});
 
var columnDefsRoles = [
	{ 
	   "headerName":"Role",
	   "field":"roleTypeId",
	   "sortable":true,
	   "filter":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Created On",
	   "field":"createdOn",
	   "sortable":true,
	   "filter":true
	},
	{ 
	   "headerName":"Created By",
	   "field":"createdBy",
	   "sortable":true,
	   "filter":true,
	   "sortable":true,
	   "filter":true
	},
	{ 
	   "headerName":"Manage Operations",
	   "filter":"agTextColumnFilter",
	   cellRenderer:function(params){
		   return '<div class="text-left"><a href="configureOperationLevels?roleTypeId=' + params.data.roleTypeId + '" class="btn btn-primary btn-xs" data-original-title="Manage Operations" title="Manage Operations" target="_blank"><i class="fa fa-eye"></i></a><a class="btn btn-primary btn-xs" id="remove_'+ params.data.roleTypeId +'" data-toggle="confirmation" alt="Remove Role" title="Are you sure? Do you want to Remove"  data-original-title="Remove"><i class="fa fa-times red"></i></a></div>'
	   }   
	}	
];

var gridOptionsRole = null;

function loadRolesAgGrid(){
	$("#grid1").empty();
	gridOptionsRole = {
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
		    columnDefs: columnDefsRoles,
		    rowData: getRolesGridData(),
		    floatingFilter: true,
		    rowSelection: "multiple",
		    editType: "fullRow",
		    paginationPageSize: 10,
		    domLayout:"autoHeight",
		    pagination: true,
		    //onFirstDataRendered: onFirstDataRenderedRoles
		    onGridReady: function() {
		    	sizeToFitRole();
			}
		};

		//lookup the container we want the Grid to use
		var eGridDiv = document.querySelector("#grid1");

		// create the grid passing in the div to use together with the columns & data we want to use
		new agGrid.Grid(eGridDiv, gridOptionsRole);

}
function getRolesGridData() {
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
        url: "getUserRoles",
        async: false,
        data:  { "partyId": $("#partyId").val() },
        success: function(data) {
            resultRes = data;
        }
    });
    return resultRes;
}
$("#loader").hide();
//data binding while loading the page
function onFirstDataRenderedRoles(params) {
    params.api.sizeColumnsToFit();
}
function clearData() {
    gridOptionsRole.api.setRowData([]);
}

function onAddRow() {
    var newItem = createNewRowData();
    var res = gridOptionsRole.api.updateRowData({
        add: [newItem]
    });
    printResult(res);
}

function addItems() {
    var newItems = [createNewRowData(), createNewRowData(), createNewRowData()];
    var res = gridOptionsRole.api.updateRowData({
        add: newItems
    });
    printResult(res);
}

function addItemsAtIndex() {
    var newItems = [createNewRowData(), createNewRowData(), createNewRowData()];
    var res = gridOptionsRole.api.updateRowData({
        add: newItems,
        addIndex: 2
    });
    printResult(res);
}

function updateItems() {
    // update the first 5 items
    var itemsToUpdate = [];
    gridOptionsRole.api.forEachNodeAfterFilterAndSort(function(rowNode, index) {
        // only do first 5
        if (index >= 5) {
            return;
        }

        var data = rowNode.data;
        data.price = Math.floor((Math.random() * 20000) + 20000);
        itemsToUpdate.push(data);
    });
    var res = gridOptionsRole.api.updateRowData({
        update: itemsToUpdate
    });
    printResult(res);
}

function onInsertRowAt2() {
    var newItem = createNewRowData();
    var res = gridOptionsRole.api.updateRowData({
        add: [newItem],
        addIndex: 2
    });
    printResult(res);
}

function onRemoveSelected() {
    var selectedData = gridOptionsRole.api.getSelectedRows();
    console.log(selectedData.length);
    for (i = 0; i <= selectedData.length; i++) {
    }
    var res = gridOptionsRole.api.updateRowData({
        remove: selectedData
    });

    printResult(res);
}

function removeSubmit() {
    var selectedData = gridOptionsRole.api.getSelectedRows();
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

function sizeToFitRole() {
	gridOptionsRole.api.sizeColumnsToFit();
}

function getSelectedRows() {
    const selectedNodes = gridOptionsRole.api.getSelectedNodes()
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
        fileName: "User_Roles",
        exportMode: 'csv'
    };


    gridOptionsRole.api.exportDataAsExcel(params);
}

*/

//fagReady("USER_ROLE_LIST", function(el, api, colApi, gridApi){
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
//        setTimeout(() => {  loadUserRoles(gridApi, api); }, 1000);
//    })
//
//    $("#main-search-btn").click(function () {
//    	loadUserRoles(gridApi, api);
//    });
//    $("#insert-btn").click(function () {
//    	gridApi.insertNewRow()
//    })
//    $("#remove-btn").click(function () {
//    	gridApi.removeSelected();
//        setTimeout(() => {  loadUserRoles(gridApi, api); }, 1000);
//        
//    });
//    
//    loadUserRoles(gridApi, api);
//});
//
//var userRolesUrl = "";
//function loadUserRoles(gridApi, api) {
//	if(userRolesUrl == ""){
//		resetGridStatusBar();
//		userRolesUrl = getGridDataFetchUrl("USER_ROLE_LIST");
//	}
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	if(userRolesUrl != null && userRolesUrl != "" && userRolesUrl !="undefined"){
//		api.showLoadingOverlay();
//		var formInput = $('#searchForm, #limitForm').serialize();
//		$.ajax({
//		  async: false,
//		  url:userRolesUrl,
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
	let securityRoleInstanceId= "USER_ROLE_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = securityRoleInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#security-role-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, securityRoleInstanceId, userId);
	});
	$("#security-role-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#security-role-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, securityRoleInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getSecurityRoleListGridData();
		}
	});
	$('#security-role-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$('#remove-btn').on('click', function(e) {
        e.preventDefault();
        var selectedData = gridInstance.getSelectedRows();
        var selectedRowData = [];
        if (selectedData && selectedData.length > 0) {
       	 	var partyIdVal = "";
       	 	var roleTypeIdVal = "";
 		    for (i = 0; i < selectedData.length; i++) {
 		    	var data = selectedData[i];
 		    	partyIdVal += data.partyId;
 		    	roleTypeIdVal += data.roleTypeId;
 		    }
 	        var inputData = {
 	               "partyId": partyIdVal,"roleTypeId": roleTypeIdVal
 	           };
            $.ajax({
                type: "POST",
                url: "/admin-portal/control/removeUserRole",
                async: true,
                data: inputData,
                success: function(result) {
                    if (result.responseMessage === "success") {
                        showAlert("success", "Role removed successfully");
                        getSecurityRoleListGridData();
                    } else {
                        showAlert("error", result.message);
                        e.preventDefault();
                    }
                },
                error: function() {
                    console.log('Error occurred');
                    showAlert("error", "Error occurred!");
                },
                complete: function() {
                }
            });
        } else {
            showAlert("error", "Please select at least one record in the list");
        }
    });

	function getSecurityRoleListGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = getGridDataFetchUrl("USER_ROLE_LIST");
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#securityRoleForm";
		callCtx.ajaxResponseKey = "";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getSecurityRoleListGridData();
	}
});


function partyIdParams(params){
	if(params && params.data && params.data.roleTypeId){
		return `<div class="text-left"><a href="configureOperationLevels?roleTypeId=' + params.data.roleTypeId + '" class="btn btn-primary btn-xs" data-original-title="Manage Operations" title="Manage Operations" target="_blank"><i class="fa fa-eye"></i></a></div>`;
	}else{
		return ``;
	}
}
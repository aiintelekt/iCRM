//$("#loader").show();
 $("#findAttributeGroupForm").on("keypress", function(event) {
    	var keyPressed = event.keyCode || event.which;
    	if (keyPressed === 13) {
    		loadFindSrvsAgGrid();
    		event.preventDefault();
    		return false;
    	}
    });
$(function() {
	loadFindSrvsAgGrid();
});

var columnDefsMkt=[
	{
        "headerName": "Group Id",
        "filter": "agTextColumnFilter",
        "field": "groupId",
    },{
        "headerName": "Grouping Code",
        "filter": "agTextColumnFilter",
        "field": "groupingCodeName"
    },{
        "headerName": "Group Name",
        "filter": "agTextColumnFilter",
        "field": "groupName"
    },{
        "headerName": "Hide",
        "field": "hide"
    },{
        "headerName": "Sequence",
        "filter": "agTextColumnFilter",
        "field": "sequence"
    },{
        "headerName": "Action",
        "field": "sequenceNumber",
        "filter":false,
      cellRenderer: function(params) {
        	return '<a href="customFieldForGroup?groupId='+params.data.groupId+'" class="btn btn-xs btn-primary" title="Configuration"><i class="fa fa-cog info"></i></a> <a class="btn btn-xs btn-primary" href="editCustomFieldGroup?groupId='+params.data.groupId+'" title="Edit"><i class="fa fa-pencil info"></i></a>  <a href="#" onclick="deleteCustom(\'deleteCustomFieldGroup?groupId='+params.data.groupId+'\')" class="btn btn-xs btn-danger confirm-message" title="Remove"><i class="fa fa-times red"></i></a>'
        	}
    },
	];
//To submit the form to while click the enter button
$("#findAttributeGroupForm").on("keypress", function (event) {
    var keyPressed = event.keyCode || event.which; 
    if (keyPressed === 13) { 
    	loadFindSrvsAgGrid();
        event.preventDefault();
        return false; 
    } 
}); 

var gridOptionsMkt = null;
function loadFindSrvsAgGrid(){
	$("#findAttributeGroupgrid").empty();
	gridOptionsMkt = {
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
			columnDefs: columnDefsMkt,
			//rowData: getGridData(),
			floatingFilter: true,
			rowSelection: "multiple",
			editType: "fullRow",
			paginationPageSize: 15,
			domLayout:"autoHeight",
			pagination: true,
			onGridReady: function() {
				sizeToFitDrip();
				getGridRowData();
			}
	}
	
	//lookup the container we want the Grid to use
	var eGridDiv = document.querySelector("#findAttributeGroupgrid");
	// create the grid passing in the div to use together with the columns & data we want to use
	new agGrid.Grid(eGridDiv, gridOptionsMkt);
}

function getGridRowData() {
	var result;
	result = getGridAjaxResponse(function(agdata) {
		gridOptionsMkt.api.setRowData(agdata);
	});
}

function getGridAjaxResponse(callback) {
    var result = [];
    var resultRes = null;
    var params = {}
    var paramStr = $("#findAttributeGroupForm").serialize();
    
    var fromData = JSON.stringify(paramStr);
    $.ajax({
        type: "POST",
        url: "findAttributeGroup",
        async: false,
        data: JSON.parse(fromData),
        success: function(data) {
            resultRes = data;
            result.push(data);
        },error: function(data) {
			showAlert("error", "Error occured!");
			resultRes=data;
		},
		complete: function() {
			$('#loader').hide();
		}
    });
    callback(resultRes);
    //return resultRes;
}

function sizeToFitDrip() {
	gridOptionsMkt.api.sizeColumnsToFit();
}
$("#loader").hide();
//data binding while loading the page
function clearData() {
	gridOptionsMkt.api.setRowData([]);
}

function onBtExport() {
    var params = {
        skipHeader: false,
        allColumns: true,
        fileName: "Services",
        exportMode: 'csv'
    };
    gridOptionsMkt.api.exportDataAsCsv(params);
}

function deleteCustom(href){
	var message = $(this).data('message');
	
	if (!$.trim(message)) {
		message = "Are you sure?";
	}
	
	bootbox.confirm(message, function(result) {
		if (result) {
			window.location.href = href;
		}
    });
}

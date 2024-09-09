$("#loader").show();
$("#findCustomFieldsForm").on("keypress", function(event) {
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
        "headerName": "Attribute Field Name",
        "filter": "agTextColumnFilter",
        "field": "customFieldName"
    },{
        "headerName": "Attribute Group",
        "filter": "agTextColumnFilter",
        "field": "groupName"
    },{
        "headerName": "Attribute Field Type",
        "filter": "agTextColumnFilter",
        "field": "customFieldType"
    },{
        "headerName": "Attribute Field Format",
        "filter": "agTextColumnFilter",
        "field": "customFieldFormat"
    },{
        "headerName": "Field Length",
        "filter": "agTextColumnFilter",
        "field": "customFieldLength"
    },{
        "headerName": "Hide",
        "filter": "agTextColumnFilter",
        "field": "hide"
    },{
        "headerName": "Coupon Campaign",
        "filter": "agTextColumnFilter",
        "field": "productPromoCodeGroupId"
    },{
        "headerName": "Sequence No",
        "filter": "agTextColumnFilter",
        "field": "sequenceNumber"
    },{
        "headerName": "Action",
        "field": "customFieldId",
        "filter":false,
        
        cellRenderer: function(params) {
        	var str = "deleteCustomField";
        	/*if(params.data.groupId!=null && params.data.groupId!=""){
        		str="deleteCustomFieldForGroup";
        	}*/
        		
        	return '<a href="editCustomField?customFieldId='+params.data.customFieldId+'&groupId='+params.data.groupId+'"  class="btn btn-xs btn-primary  <if !groupId?has_content>disabled</#if> " title="View Segment Values">  <i class="fa fa-pencil info"></i> </a> <a href="#" onclick="deleteCustom(\''+str+'?customFieldId='+params.data.customFieldId+'&groupId='+params.data.groupId+'\')"  class="btn btn-xs btn-danger tooltips confirm-message" title="Remove">  <i class="fa fa-times red info"></i> </a>';
        
        	}
    
    },
	];

var gridOptionsMkt = null;
function loadFindSrvsAgGrid(){
	$("#findCustomFieldGrid").empty();
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
		//rowSelection: 'single',
		//onSelectionChanged: onSelectionChanged,
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
	var eGridDiv = document.querySelector("#findCustomFieldGrid");
	// create the grid passing in the div to use together with the columns & data we want to use
	new agGrid.Grid(eGridDiv, gridOptionsMkt);
}
//To submit the form to while click the enter button
$("#findCustomFieldsForm").on("keypress", function (event) {
    var keyPressed = event.keyCode || event.which; 
    if (keyPressed === 13) { 
    	loadFindSrvsAgGrid();
        event.preventDefault();
        return false; 
    } 
}); 
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
    var paramStr = $("#findCustomFieldsForm").serialize();
    var fromData = JSON.stringify(paramStr);
    $.ajax({
        type: "POST",
        url: "findCustomFieldsEvent",
        async: false,
        data: JSON.parse(fromData),
        success: function(data) {
            resultRes = data;
            result.push(data);
        },error: function(data) {
			showAlert("error", "Error occured!");
			//result.push(data);
			resultRes=data;
		},
		complete: function() {
			$('#loader').hide();
		}
    });
    callback(resultRes);
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

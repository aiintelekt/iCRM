//$("#loader").show();
$(function() {
	loadFindSrvsAgGrid();
});

$('#btnFind').click(function(event){
	event.preventDefault();
	loadFindSrvsAgGrid();
});

var columnDefsMkt=[	
	{
		"colId": "groupingCode",
        "headerName": "Grouping Code",
        "filter": "agTextColumnFilter",
        "field": "groupingCodeName"
    },{
    	"colId": "groupName",
        "headerName": "Segment Code",
        "filter": "agTextColumnFilter",
        "field": "groupName"
    },{
    	"colId": "customFieldId",
        "headerName": "Segment Value Id",
        "field": "customFieldId"
    },{
    	"colId": "customFieldName",
        "headerName": "Segment Value Name",
        "filter": "agTextColumnFilter",
        "field": "customFieldName"
    },{
    	"colId": "valueMin",
        "headerName": "Min Value",
        "filter": "agTextColumnFilter",
        "field": "valueMin"
    },{
    	"colId": "valueMax",
        "headerName": "Max Value",
        "filter": "agTextColumnFilter",
        "field": "valueMax"
    },{
    	"colId": "valueData",
        "headerName": "Data",
        "filter": "agTextColumnFilter",
        "field": "valueData"
    },{
    	"colId": "sequenceNumber",
        "headerName": "Sequence No",
        "filter": "agTextColumnFilter",
        "field": "sequenceNumber"
    },{
    	"colId": "productPromoCodeGroupId",
        "headerName": "Coupon Campaign",
        "filter": "agTextColumnFilter",
        "field": "productPromoCodeGroupId"
    },{
    	"colId": "isEnabled",
        "headerName": "Enabled",
        "filter": "agTextColumnFilter",
        "field": "isEnabled"
    },{
    	"colId": "isDefault",
        "headerName": "Default",
        "filter": "agTextColumnFilter",
        "field": "isDefault"
    },{
        "headerName": "Action",
        "field": "sequenceNumber",
        "filter": false,
        "sort": false,
      cellRenderer: function(params) {
        	return '<a href="segmentValueCustomer?customFieldId='+params.data.customFieldId+'&groupId='+params.data.groupId+'" class="btn btn-xs btn-primary tooltips <if !groupId?has_content>disabled</#if>" data-original-title="Manage Customers"><i class="fa fa-plus info"></i></a> <a class="btn btn-xs btn-primary tooltips" href="editSegmentValue?customFieldId='+params.data.customFieldId+'&groupId='+params.data.groupId+'" data-original-title="Edit"><i class="fa fa-pencil info"></i></a>'
        	}
    }
	];
var gridOptionsMkt = null;
function loadFindSrvsAgGrid(){
	$("#findsegmentgrid").empty();
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
			rowData: getGridData(),
			floatingFilter: true,
			rowSelection: "multiple",
			editType: "fullRow",
			paginationPageSize: 15,
			domLayout:"autoHeight",
			pagination: true,
			onGridReady: function() {
				columnVisible();
				sizeToFitDrip();
				//getGridData();
			}
	}
	

	//lookup the container we want the Grid to use
	var eGridDiv = document.querySelector("#findsegmentgrid");
	// create the grid passing in the div to use together with the columns & data we want to use
	new agGrid.Grid(eGridDiv, gridOptionsMkt);
}



function getGridData() {
    var result = [];
    var resultRes = null;
    var params = {}
    var paramStr = $("#findsegmentRequestForm").serialize();
    
    var fromData = JSON.stringify(paramStr);
    $.ajax({
        type: "POST",
        url: "findsegment",
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
    return resultRes;
}
//$("#loader").hide();

/*function serviceRequestHomeData(callback) {
	var data1;
	var result = [];
	var resultRes = null;
	var params = {}
	var paramStr = $("#findServiceRequestForm").serialize();


	// validate the serialize form data
	var parameters =  $("#findServiceRequestForm :input")
	.filter(function(index, element) {		
		return $(element).val() != '';
	}).serialize();

	var fromData = JSON.stringify(paramStr);
	var errorMessage = null;
	var resultData = null;
	$.ajax({
		type: "POST",
		url: "serviceRequestHomeData",
		async: false,
		data: JSON.parse(fromData),
		success: function(data) {
			var result1 = data;
			if(data != null || data != undefined){
				errorMessage = data[0].errorMessage;
				resultData = data;
			}
			if(errorMessage != null || errorMessage != undefined) {
				showAlert("error", errorMessage);
				console.log("--errorMessage-----" + errorMessage);
				callback(resultData);
			}else{
				callback(data);
			}

		},
		error: function(data) {
			console.log('Error occured');
			showAlert("error", "Error occured!");
			callback(data);
		},
		complete: function() {
			$('#loader').hide();
		}
	});

}*/
function getRowDataMkt() {
	var result;
	result = getAjaxResponse(function(agdata) {
		gridOptionsMkt.api.setRowData(agdata);
	});
}
function sizeToFitDrip() {
	gridOptionsMkt.api.sizeColumnsToFit();
}
function columnVisible(){	
	if($('#valueCapture').val() == "SINGLE"){
		gridOptionsMkt.columnApi.setColumnsVisible(["groupingCode","groupName","valueMax", "valueMin"], false);
	}
	else if($('#valueCapture').val() == "MULTIPLE"){
		gridOptionsMkt.columnApi.setColumnsVisible(["groupingCode","groupName","valueMax", "valueMin"], false);
	}
	else if($('#valueCapture').val() == "RANGE"){
		gridOptionsMkt.columnApi.setColumnsVisible(["groupingCode","groupName","valueData"], false);
	}
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

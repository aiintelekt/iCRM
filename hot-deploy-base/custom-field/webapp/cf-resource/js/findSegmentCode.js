$("#loader").show();
$(function() {
	loadFindSrvsAgGrid();
});
$('#findSegmentCode').click(function(event){
	event.preventDefault();
	loadFindSrvsAgGrid();
});

var columnDefsMkt=[
	{
        "headerName": "Grouping Code",
        "filter": "agTextColumnFilter",
        "field": "groupingCode"
    },{
        "headerName": "Segment Code Id",
        "filter": "agTextColumnFilter",
        "field": "segmentCodeId"
    },{
        "headerName": "Segment Code Name",
        "filter": "agTextColumnFilter",
        "field": "segmentCodeName"
    },{
        "headerName": "Active",
        "filter": "agTextColumnFilter",
        "field": "active"
    },{
        "headerName": "Sequence No",
        "filter": "agTextColumnFilter",
        "field": "sequenceNo"
    },{
        "headerName": "Action",
        "field": "segmentCodeId",
        "filter": false,
        "sort": false,
        cellRenderer: function(params) {
        	return '<a href="viewSegmentValueForGroup?groupId='+params.data.segmentCodeId+'"  class="btn btn-xs btn-primary tooltips  <if !groupId?has_content>disabled</#if> "  data-original-title="View Segment Values">  <i class="fa fa-eye info"></i> </a> <a href="segmentValueForGroup?groupId='+params.data.segmentCodeId+'"  class="btn btn-xs btn-primary tooltips  <if !groupId?has_content>disabled</#if> "  data-original-title="View Segment Values">  <i class="fa fa-plus info"></i> </a> <a href="editSegmentCode?groupId='+params.data.segmentCodeId+'"  class="btn btn-xs btn-primary tooltips  <if !groupId?has_content>disabled</#if> "  data-original-title="View Segment Values">  <i class="fa fa-pencil info"></i> </a>'
        	}
    
    },
    
	];

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
			rowData: getGridData(),
			floatingFilter: true,
			rowSelection: "multiple",
			editType: "fullRow",
			paginationPageSize: 15,
			domLayout:"autoHeight",
			pagination: true,
			onGridReady: function() {
				sizeToFitDrip();
				//getGridData();
			}
	}
	

	//lookup the container we want the Grid to use
	var eGridDiv = document.querySelector("#findAttributeGroupgrid");
	// create the grid passing in the div to use together with the columns & data we want to use
	new agGrid.Grid(eGridDiv, gridOptionsMkt);

}

function getGridData() {
    var result = [];
    var resultRes = null;
    var params = {}
    var paramStr = $("#findAttributeGroupForm").serialize();
    
    var fromData = JSON.stringify(paramStr);
    $.ajax({
        type: "POST",
        url: "findSegmentCodeEvent",
        async: false,
        data: JSON.parse(fromData),
        success: function(data) {
            resultRes = data;
            result.push(data);
        },error: function(data) {
			//console.log('Error occured');
			showAlert("error", "Error occured!");
			//result.push(data);
			resultRes=data;
		},
		complete: function() {
			$('#loader').hide();
		}
    });
    return resultRes;
}


function getRowDataMkt() {
	var result;
	result = getAjaxResponse(function(agdata) {
		gridOptionsMkt.api.setRowData(agdata);
	});
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
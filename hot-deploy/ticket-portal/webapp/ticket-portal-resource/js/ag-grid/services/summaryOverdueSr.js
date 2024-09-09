$("#loader").show();
$(function() {
	loadAgGrid();
});

var columnDefs = [
     {
        "headerName":"S+3 Days",
        "field":"s3Days",
        "sortable":true,
        //"width": 130,
        cellRenderer: function(params) {
        	return '<a href="findServiceRequests?dueDate='+ params.data.sysDate3+'&createdOn='+ params.data.sysDate+'" target="">' + params.data.s3Days+ '</a>'
        	}
     },
     {
        "headerName":"S+4 to 10 Days",
        "field":"s10Days",
        "sortable":true,
        //"width": 130,
        cellRenderer: function(params) {
        	return '<a href="findServiceRequests?dueDate='+ params.data.sysDate10+'&createdOn='+ params.data.sysDate3+'" target="">' + params.data.s10Days+ '</a>'
        	}
     },
     {
        "headerName":"S+11 to 15 Days",
        "field":"s11Days",
        "sortable":true,
        //"width": 130,
        cellRenderer: function(params) {
        	return '<a href="findServiceRequests?dueDate='+ params.data.sysDate15+'&createdOn='+ params.data.sysDate10+'" target="">' + params.data.s11Days+ '</a>'
        	}
     },
     {
        "headerName":"S+16 to 20 Days",
        "field":"s16Days",
        "sortable":true,
        //"width": 130,
        cellRenderer: function(params) {
        	return '<a href="findServiceRequests?dueDate='+ params.data.sysDate20+'" target="">' + params.data.s16Days+ '</a>'
        	}
     },
     {
        "headerName":"S+21 to 30 Days",
        "field":"s21Days",
        "sortable":true,
        //"width": 130,
        cellRenderer: function(params) {
        	return '<a href="findServiceRequests?dueDate='+ params.data.sysDate30+'&createdOn='+ params.data.sysDate20+'" target="">' + params.data.s21Days+ '</a>'
        	}
     },
     {
        "headerName":"S+31 to 50 Days",
        "field":"s31Days",
        "sortable":true,
        //"width": 130,
        cellRenderer: function(params) {
        	return '<a href="findServiceRequests?dueDate='+ params.data.sysDate50+'&createdOn='+ params.data.sysDate30+'" target="">' + params.data.s31Days+ '</a>'
        	}
     },
     {
        "headerName":"S+>50 Days",
        "field":"s51Days",
        "sortable":true,
        //"width": 130,
        cellRenderer: function(params) {
        	return '<a href="findServiceRequests?dueDate='+ params.data.sysDate50+'" target="">' + params.data.s51Days+ '</a>'
        	}
     },
     {
        "headerName":"Total",
        "field":"total",
        "sortable":true,
        //"width": 130,
     }
  ];
var gridOptions = null;
function loadAgGrid(){
	$("#grid1").empty();
	gridOptions = {
		    defaultColDef: {
		        sortable: true,
		        resizable: false,
		        unSortIcon: true,
		        // allow every column to be aggregated
		        //enableValue: true,
		        // allow every column to be grouped
		        //enableRowGroup: true,
		        // allow every column to be pivoted
		        //enablePivot: true,
		    },
		    columnDefs: columnDefs,
		    rowData: getGridData(),
		    floatingFilter: false,
		    rowSelection: "multiple",
		    editType: "fullRow",
		    paginationPageSize: 10,
		    domLayout:"autoHeight",
		    pagination: true,
		    onGridReady: function(){
		    	sizeToFit();
		    }
		    //onFirstDataRendered: onFirstDataRendered
		};

		//lookup the container we want the Grid to use
		var eGridDiv = document.querySelector("#grid1");

		// create the grid passing in the div to use together with the columns & data we want to use
		new agGrid.Grid(eGridDiv, gridOptions);

}
function sizeToFit() {	
	gridOptions.api.sizeColumnsToFit();
}
function getGridData() {
    var result = [];
    var resultRes = null;//[{"s3Days":"155","s10Days":"155","s11Days":"155","s16Days":"155","s21Days":"155","s31Days":"155","s51Days":"155","Total":"12345"}];
    var params = {}
    var paramStr = $("#searchForm").serialize();
    // var query = window.location.search.substring(1);
    //var parsed_qs = parse_query_string(query);
    var businessUnit = "";
    if(businessUnit == undefined|| businessUnit ==""){
        businessUnit = $("#ownerBu").val();
    }
    //console.log("--businessUnit-----" + businessUnit);
    /*$('#searchForm :input:hidden').each(function(){
    	params[this.name] = this.value;
    }); */
    console.log("formData--->"+JSON.stringify(paramStr));
    var fromData = JSON.stringify(paramStr);
    $.ajax({
        type: "POST",
        url: "getSrOverDueSummary",
        async: false,
        data: { "businessUnit": businessUnit},
        success: function(data) {
            //sresultRes = data;
            result.push(data[0]);
           // console.log("--result-----" + JSON.stringify(result));
            $("#loader").hide();
        }
    });
    return result;
}
$("#loader").hide();

function onBtExport() {
    var params = {
        skipHeader: false,
        allColumns: true,
        fileName: "Open-Close_SR",
        exportMode: 'csv'
    };


    gridOptions.api.exportDataAsCsv(params);
}
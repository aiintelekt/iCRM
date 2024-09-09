

$(function() {
	
	emailGrid();
	
});



var columnDefsCus = [
		{
			"headerName": "Grouping Code",
			"field": "groupingCode"
		}, {
			"headerName": "Sequence No",
			"field": "sequenceNumber"
		}, {
			"headerName": "Description",
			"field": "description"
		}, { 
	 	   "headerName":"Action",
	 	   "sortable":false,
	 	   "filter":false,
	 	   "field":"customFieldGroupingCodeId",
	 	   width:90,
	 	   suppressAutoSize:true,
	 	   cellRenderer: function (params) {  return  `<a href="editGroupingCode?groupingCodeId=${params.value}"  class="btn btn-xs btn-primary tooltips" title="Edit"><i class="fa fa-pencil info"></i></a>
		 		<a id="deleteGroup"  class="btn btn-xs btn-danger tooltips confirm-message" onclick="deleteGroupCode(\'deleteGroupingCode?groupingCodeId=${params.value}\')" href="#" title="Remove"><i class="fa fa-times red"></i></a>`
		 	   }
 	   }
 ];

function deleteGroupCode(href){
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


var gridOptionsCus = null;

function emailGrid(){
$("#groupCodeAgGrid").empty();
    gridOptionsCus = {
        defaultColDef: {
        width:244,
            filter: true,
            sortable: true,
            resizable: true
        },
        columnDefs: columnDefsCus,
        floatingFilter: true,
        rowSelection: "multiple",
        editType: "fullRow",
        paginationPageSize: 10,
        domLayout:"autoHeight",
        pagination: true,
        onGridReady: function() {
        getRowDataCus();
        sizeToFitCus();
        }
    }
   

    var eGridDiv = document.querySelector("#groupCodeAgGrid");
    new agGrid.Grid(eGridDiv, gridOptionsCus);
   
}


function getAjaxResponses(callback) {
    var data1;
    var result = [];
    var resultRes = null;
    

    const url=window.location.search;
    const urlParam=new URLSearchParams(url);
    const urlSaleId=$("input[name='groupType']").val();
        
        var errorMessage = null;
        var resultData = null;
        $.ajax({
            type: "POST",
            url: "getGroupSegmentCode",
            async: false,
            data: "groupType="+urlSaleId,
            success: function(data) {
                var result1 = data[0];
                if(data[0] != null || data[0] != undefined){
                    errorMessage = data[0].errorMessage;
                    resultData = data[0].errorResult;
                }
                if(errorMessage != null || errorMessage != undefined) {
                    showAlert("error", errorMessage);
                    console.log("--errorMessage-----" + errorMessage);
                    callback(resultData);
                }else{
                    callback(data);
                }
               
            },
            error: function() {
                console.log('Error occured');
                showAlert("error", "Error occured!");
                callback(result);
            },
            complete: function() {
            //$('#loader').hide();
            }
        });
    //}
   
}

function getRowDataCus() {
var result;
result = getAjaxResponses(function(agdata) {
	
gridOptionsCus.api.setRowData(agdata);
    });
}

function sizeToFitCus() {
    gridOptionsCus.api.sizeColumnsToFit();
}











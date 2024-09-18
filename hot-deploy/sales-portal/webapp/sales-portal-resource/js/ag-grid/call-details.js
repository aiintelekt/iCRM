

$(function() {
	//$("#curRep").onclick= emailGrid();
});

var columnDefsCus = [
	  {
			 "headerName" : "User Id",
			 "field" : "userId"
	     },
		  {
	        "headerName":"Activity Start Date",
	        "field":"activityStartDate"
	     },
	       {
	        "headerName":"Call Outcome",
	        "field":"callOutCome"
	     },
	      {
	         "headerName":"Response Type",
	         "field":"responseType"
	     },
	      {
	          "headerName":"Response Reason",
	          "field":"reponseReason"
	     },
	       {
	           "headerName":"Opportunity Status",
	           "field":"opportunityStatusIdDesc"
	      }
	     
   
 ];

var gridOptionsCus = null;
function emailGrid(){
$("#ContactAgGrid1").empty();
    gridOptionsCus = {
        defaultColDef: {
        width:244,
       floatingFilter: false,
            filter: false,
            sortable: false,
            resizable: true
        },
        columnDefs: columnDefsCus,
        floatingFilter: false,
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
   
    // lookup the container we want the Grid to use
    var eGridDiv = document.querySelector("#ContactAgGrid1");
    // create the grid passing in the div to use together with the columns &
// data we want to use
    new agGrid.Grid(eGridDiv, gridOptionsCus);
   
}


function getAjaxResponses(callback) {
    var data1;
    var result = [];
    var resultRes = null;
    

    const url=window.location.search;
    const urlParam=new URLSearchParams(url);
    const urlSaleId=urlParam.get("salesOpportunityId");
        
        var errorMessage = null;
        var resultData = null;
        $.ajax({
            type: "POST",
            url: "getCallDetails",
            async: false,
            data: "salesOpportunityId="+urlSaleId,
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


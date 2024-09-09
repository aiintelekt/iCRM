$("#loader1").show();
$(function() {
	//loadRelatedOpportunityGrid();
});

var  totalStatus;


var columnDefsRelatedOpportunity = [{ 
	   "headerName":"Opportunity Number",
	   "field":"opportunityNumber",
	   "sortable":true,
	   "filter":true,
	   "filter":"agTextColumnFilter",
	   "width": 300,
	   "checkboxSelection":true,
	    cellRenderer : function(params){
	        totalStatus= params.data.total;
			return `<a href="viewOpportunity?salesOpportunityId=${params.data.opportunityNumber}">${params.value}</a>` 
		}
	},
	{ 
		"headerName":"Opportunity Status",
		"field":"opportunityStatus",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	{ 
		"headerName":"Campaign Code",
		"field":"campaignCode",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
	},
	
	{ 
		"headerName":"Campaign Description",
		"field":"campaignDescription",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
				   
	},
	{ 
		"headerName":"Campaign Start Date",
		"field":"campaignStartDate",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
				   
	},

	{ 
		"headerName":"Campaign End Date",
		"field":"campaignEndDate",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
				   
	},

	{ 
		"headerName":"CIN",
		"field":"cIN",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
				   
	},

	{ 
		"headerName":"CIN Suffix(Customer)",
		"field":"cINCustomer",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
				   
	},
	
	{ 
		"headerName":"Customer",
		"field":"customer",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
				   
	},
	{ 
		"headerName":"CIN (Prospect)",
		"field":"prospectCin",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
				   
	},
	
	{ 
		"headerName":"Prospect",
		"field":"prospectName",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
				   
	},
	{ 
		"headerName":"Official Phone",
		"field":"phone",
		"sortable":true,
		"filter":true,
		"filter":"agTextColumnFilter"
				   
	}
];

var gridOptionsRelatedOpportunity = null;
        
function loadRelatedOpportunityGrid(){
	$("#relatedOpportunityGrid").empty();
    gridOptionsRelatedOpportunity = {
        defaultColDef: {
            filter: true,
            sortable: true,
            resizable: true
        },
        columnDefs: columnDefsRelatedOpportunity,
        floatingFilter: true,
        rowSelection: "multiple",
        editType: "fullRow",
        paginationPageSize: 10,
        domLayout:"autoHeight",
        pagination: true,
        onGridReady: function() {
        	getRowDataRelatedOpportunity();
        }
    }
    
    //lookup the container we want the Grid to use
    var eGridDiv = document.querySelector("#relatedOpportunityGrid");
    // create the grid passing in the div to use together with the columns & data we want to use
    new agGrid.Grid(eGridDiv, gridOptionsRelatedOpportunity);
    
}


function getAjaxResponseRelatedOpportunity(callback) {
    var data1;
    var result = [];
    var resultRes = null;
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    var salesOpportunityId = urlParams.get('salesOpportunityId')
    var errorMessage = null;
    var resultData = null;
    var params = {}
    var paramStr = $("#relatedOpportunityForm").serialize();
   
    var today = new Date();
    var dd = String(today.getDate()).padStart(2, '0');
    var mm = String(today.getMonth() + 1).padStart(2, '0'); //January is 0!
    var yyyy = today.getFullYear();
    today = yyyy + '-' + mm + '-' +  dd;
	
    var formData = JSON.stringify(paramStr);
    
        formData=formData.substring(0, formData.length - 1)+"&salesOpportunityId="+salesOpportunityId+"&currentDate="+today+"\"";
    $.ajax({
        type: "POST",
        url: "getRelatedOpportunityData",
        async: false,
        data: JSON.parse(formData),
        success: function(data) {
            var result1 = data[0];
            if(data[0] != null || data[0] != undefined){
                errorMessage = data[0].errorMessage;
                resultData = data[0].errorResult;
            }
            if(errorMessage != null || errorMessage != undefined) {
                showAlert("error", errorMessage);
                callback(resultData);
            }else{
                callback(data);
               document.getElementById("totalStatus").innerHTML =  DOMPurify.sanitize(JSON.stringify(data[0]["total"]));
               document.getElementById("won").innerHTML =  DOMPurify.sanitize(JSON.stringify(data[0]["won"]));
               document.getElementById("lost").innerHTML =  DOMPurify.sanitize(JSON.stringify(data[0]["lost"]));
               document.getElementById("open").innerHTML =  DOMPurify.sanitize(JSON.stringify(data[0]["open"]));
               document.getElementById("percentageWon").innerHTML =  DOMPurify.sanitize(JSON.stringify(data[0]["percentageWon"]));
               document.getElementById("percentageLost").innerHTML =  DOMPurify.sanitize(JSON.stringify(data[0]["percentageLost"]));
            }
            
        },
        error: function() {
            console.log('Error occured');
            showAlert("error", "Error occured!");
        },
        complete: function() {
        	//$('#loader').hide();
        	
        }
    });
}

function getRowDataRelatedOpportunity() {
	var result;
	result = getAjaxResponseRelatedOpportunity(function(agdata) {
		gridOptionsRelatedOpportunity.api.setRowData(agdata);
    });
}

//data binding while loading the page
function onFirstDataRendered(params) {
    params.api.sizeColumnsToFit();
}
function clearData() {
    gridOptionsRelatedOpportunity.api.setRowData([]);
}



function onBtExport() {
    var params = {
        skipHeader: false,
        allColumns: true,
        fileName: "Find Related Opportunity",
        exportMode: 'csv'
    };


    gridOptionsRelatedOpportunity.api.exportDataAsCsv(params);
}
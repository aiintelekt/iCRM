
// Ag grid for Contacts Modal
var columnContDefs=[
	
	{headerName:"Member", field:"nameWithId",
		cellRenderer: function(params) {
			var roleTypeId = params.data.roleTypeId;
			var nameWithId = params.data.nameWithId;
			var partyId = params.data.partyId;
			if (roleTypeId != null && roleTypeId != "" && roleTypeId == "CONTACT") { 
				return  '<a target="_blank" href="/contact-portal/control/viewContact?partyId='+partyId+'">'+nameWithId+'</a>';
			} else if (roleTypeId != null && roleTypeId != "" && roleTypeId == "LEAD") {
				return  '<a target="_blank" href="/lead-portal/control/viewLead?partyId='+partyId+'">'+nameWithId+'</a>';
			} else {
				return  nameWithId;
			}                	
		}
	},
	{headerName:"Phone Number", field:"phoneNumber"},
	{headerName:"Email Address", field:"infoString"},
	{headerName:"Status", field:"statusId"}
];

var gridOptions2 = null;

function loadContactModalAgGrid(groupId, customFieldId){
	$("#modalContactGrid").empty();
	gridOptions2 = {
		    defaultColDef: {
		        filter: true,
		        sortable: true,
		        resizable: true,
		    },
		    columnDefs: columnContDefs,
		    floatingFilter: true,
		    rowData: getModalContactGridData(groupId,customFieldId),
		    rowSelection: "multiple",
		    paginationPageSize: 10,
		    domLayout:"autoHeight",
		    pagination: true,
		    onGridReady: function() {
	        	sizeToFit2();
	        }
		};
	//lookup the container we want the Grid to use
	var eGridDiv2 = document.querySelector("#modalContactGrid");

	// create the grid passing in the div to use together with the columns & data we want to use
	new agGrid.Grid(eGridDiv2, gridOptions2);

}

function getModalContactGridData(groupId,customFieldId) {
    var result = [];
    var resultRes = null;
    var dataSet = {};
    var url = "searchCampaignPartys?campaignId="+groupId+"&customFieldId="+customFieldId;
    
    $.ajax({
        type: "POST",
        url: url,
        async: false,
        data: dataSet,
        success: function(result) {
            resultRes = result["data"];
           // result.push(data);
        }
    });
    return resultRes;
}

function onFirstDataRendered(params) {
    params.api.sizeColumnsToFit();
}
function clearData() {
    gridOptions2.api.setRowData([]);
}

function sizeToFit2() {
    gridOptions2.api.sizeColumnsToFit();
}


	
	
	
	
	
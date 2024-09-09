

var columnDefs=[
	
	{headerName:"Member", field:"nameWithId",
		cellRenderer: function(params) {
			var roleTypeIdFrom = params.data.roleTypeIdFrom;
			var nameWithId = params.data.nameWithId;
			var partyId = params.data.partyId;
			if (roleTypeIdFrom != null && roleTypeIdFrom != "" && roleTypeIdFrom == "ACCOUNT") {
				return  '<a target="_blank" href="/crm/control/viewAccount?partyId='+partyId+'">'+nameWithId+'</a>';
			} else if (roleTypeIdFrom != null && roleTypeIdFrom != "" && roleTypeIdFrom == "CONTACT") {
				return  '<a target="_blank" href="/crm/control/viewContact?partyId='+partyId+'">'+nameWithId+'</a>';
			} else if (roleTypeIdFrom != null && roleTypeIdFrom != "" && roleTypeIdFrom == "LEAD") {
				return  '<a target="_blank" href="/crm/control/viewLead?partyId='+partyId+'">'+nameWithId+'</a>';
			} else {
				return  nameWithId;
			}                	
		}
	},
	{headerName:"Phone Number", field:"phoneNumber"},
	{headerName:"Email Address", field:"infoString"},
	{headerName:"Status", field:"statusId"}
];

var gridOptions = null;

function loadModalAgGrid(groupId, customFieldId){
	$("#modalGrid").empty();
	 gridOptions = {
		    defaultColDef: {
		        filter: true,
		        sortable: true,
		        resizable: true,
		    },
		    columnDefs: columnDefs,
		    floatingFilter: true,
		    rowData: getModalGridData(groupId,customFieldId),
		    rowSelection: "multiple",
		    paginationPageSize: 10,
		    domLayout:"autoHeight",
		    pagination: true,
		    onGridReady: function() {
	        	sizeToFit();
	        }
		};
	//lookup the container we want the Grid to use
	var eGridDiv = document.querySelector("#modalGrid");

	// create the grid passing in the div to use together with the columns & data we want to use
	new agGrid.Grid(eGridDiv, gridOptions);

}

function getModalGridData(groupId,customFieldId) {
    var result = [];
    var resultRes = null;
    var dataSet = {};
    var url = "searchSegmentCustomers?groupId="+groupId+"&customFieldId="+customFieldId;
    
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
    gridOptions.api.setRowData([]);
}

function sizeToFit() {
    gridOptions.api.sizeColumnsToFit();
}


	
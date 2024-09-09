//fagReady("CONTACTS", function(el, api, colApi, gridApi){
//    $("#refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#clear-filter-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    	gridApi.refreshUserPreferences();
//    });
//	$("#sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//
//    $("#update-btn").click(function () {
//    	gridApi.saveUpdates();
//        setTimeout(() => {  loadMainGrid(gridApi, api, colApi) }, 1000);
//    })
//
//    $("#main-search-btn").click(function () {
//    	loadMainGrid(gridApi, api, colApi)
//    });
//    $("#insert-btn").click(function () {
//    	gridApi.insertNewRow()
//    })
//    $("#remove-btn").click(function () {
//        //removeMainGrid(fag1, api);
//    	gridApi.removeSelected();
//        setTimeout(() => {  loadMainGrid(gridApi, api, colApi) }, 1000);
//        
//    });
//    
//    $("#searchForm").on("keypress", function (event) {
//        var keyPressed = event.keyCode || event.which; 
//        if (keyPressed === 13) { 
//        	loadMainGrid(gridApi, api, colApi);
//            event.preventDefault(); 
//            return false; 
//        } 
//    }); 
//    
//    $("#fetch-previous").click(function () {
//    	fetchPrevious();
//    	loadMainGrid(gridApi, api, colApi);
//    });
//    $("#fetch-next").click(function () {
//    	fetchNext();
//    	loadMainGrid(gridApi, api, colApi);
//    });
//    $("#fetch-first").click(function () {
//    	fetchFirst();
//    	loadMainGrid(gridApi, api, colApi);
//    });
//    $("#fetch-last").click(function () {
//    	fetchLast();
//    	loadMainGrid(gridApi, api, colApi);
//    });
//    
//    $('#goto-page').keypress(function(event){
//        var keycode = (event.keyCode ? event.keyCode : event.which);
//        if(keycode == '13'){
//        	if(goto()) loadMainGrid(gridApi, api, colApi);
//        }
//    });
//    
//    loadMainGrid(gridApi, api, colApi)
//});
//
//function loadMainGrid(gridApi, api, colApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	var formInput = $('#searchForm, #limitForm').serialize();
//	$.ajax({
//	  async: true,
//	  url:'/contact-portal/control/searchContacts',
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify(formInput)),
//	  success: function(data){
//		  gridApi.setRowData(data.list);
//		  setTimeout(() => colApi.autoSizeAllColumns(), 1000);
//		  data.list=[];
//		  paginateHandler(data);
//	  }
//	});
//}

/*
$(function() {
	loadContactGrid();
});

var columnContactDefs = [
{ 
	   "headerName":"Title",
	   "field":"generalProfTitle",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{  
		"headerName":"Contact Name",
		"sortable":true,
		cellRenderer: params => `<a href="viewContact?partyId=${params.data.partyId}" class="btn-xs ml-0 px-0">${params.data.name} (${params.data.partyId})</a>`
	},
	{ 
	   "headerName":"Account Name",
	   "field":"groupName",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"LCIN",
	   "field":"partyIdTo",
	   "sortable":false,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"CIN",
	   "field":"cin",
	   "sortable":false,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Status",
	   "field":"statusDescription",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"Phone Number",
	   "field":"contactNumber",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	},
	{ 
	   "headerName":"E-Mail Address",
	   "field":"infoString",
	   "sortable":true,
	   "filter":"agTextColumnFilter"
	}
    
];

$(document).ready(function() {
	
	
});

var gridOptionsContact = null;
function loadContactGrid() {
	$("#contact-grid").empty();
	gridOptionsContact = {
		defaultColDef : {
			filter : true,
			sortable : true,
			resizable : true
		},
		columnDefs : columnContactDefs,
		floatingFilter : true,
		rowSelection : "multiple",
		editType : "fullRow",
		paginationPageSize : 10,
		domLayout : "autoHeight",
		pagination : true,
		onGridReady : function() {
			//sizeToFitContact();
			getContactRowData();
		}
	}

	// lookup the container we want the Grid to use
	var eGridDiv = document.querySelector("#contact-grid");
	// create the grid passing in the div to use together with the columns &
	// data we want to use
	new agGrid.Grid(eGridDiv, gridOptionsContact);

}

function getContactAjaxResponse(callback) {
	var result = [];
	var resultRes = null;
	var params = {}
	var inputData = {};
	
	var paramStr = $("#contact-search-form").serialize();
	
	//var componentMountPoint = $("#componentMountPoint").val(); 
	
	//inputData = {"componentMountPoint": componentMountPoint};
	
	console.log("formData--->"+JSON.stringify(paramStr));
    inputData = JSON.parse(JSON.stringify(paramStr));
	
	var errorMessage = null;
	var resultData = null;
	$.ajax({
		type : "POST",
		url : "searchContacts",
		async : false,
		data : inputData,
		success : function(data) {
			var result1 = data[0];
			if (data[0] != null || data[0] != undefined) {
				errorMessage = data[0].errorMessage;
				resultData = data[0].errorResult;
			}
			if (errorMessage != null || errorMessage != undefined) {
				showAlert("error", errorMessage);
				console.log("--errorMessage-----" + errorMessage);
				callback(resultData);
			} else {
				callback(data);
			}

		},
		error : function() {
			console.log('Error occured');
			showAlert("error", "Error occured!");
			callback(result);
		},
		complete : function() {
		}
	});

}

function sizeToFitContact() {
	gridOptionsContact.api.sizeColumnsToFit();
}

function getContactRowData() {
	var result;
	result = getContactAjaxResponse(function(agdata) {
		gridOptionsContact.api.setRowData(agdata);
	});
}
*/



$(function() {
	let contactInstanceId= "CONTACTS";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = contactInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#contact-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, contactInstanceId, userId);
	});
	$('#contact-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, contactInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getContactGridData();
		}
	});
	$("#contact-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$("#main-search-btn").click(function () {
		getContactGridData();
	});
	 $("#searchForm").on("keypress", function (event) {
	        var keyPressed = event.keyCode || event.which; 
	        if (keyPressed === 13) { 
	        	getContactGridData();
	            event.preventDefault(); 
	            return false; 
	        } 
	    }); 
	    
	    $("#fetch-previous").click(function () {
	    	fetchPrevious();
	    	getContactGridData();
	    });
	    $("#fetch-next").click(function () {
	    	fetchNext();
	    	getContactGridData();
	    });
	    $("#fetch-first").click(function () {
	    	fetchFirst();
	    	getContactGridData();
	    });
	    $("#fetch-last").click(function () {
	    	fetchLast();
	    	getContactGridData();
	    });
	    
	    $('#goto-page').keypress(function(event){
	        var keycode = (event.keyCode ? event.keyCode : event.which);
	        if(keycode == '13'){
	        	if(goto()) getContactGridData();
	        }
	    });
	function getContactGridData(){
		const callCtx = {};
		callCtx.ajaxUrl = "/contact-portal/control/searchContacts";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm, #limitForm_CONTACTS";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getContactGridData();
	}
});

function firstName(params) { 
	var name = params.data.firstName; var lastName = params.data.lastName; 
	if (lastName != null && lastName != null && lastName != "undefined") { 
		name = name + " " + lastName; 
	} 
	return '<a href="viewContact?partyId=' + params.data.partyId + '" target="">' + name + '</a>' 
}


function infoString(params) { 
	if (params.data.partyId && params.data.domainEntityId && params.value) { 
		return `<i class="fa fa fa-envelope fa-1"></i> <a target="_blank" href="addEmail?partyId=${params.data.partyId}&domainEntityType=${params.data.domainEntityType}&domainEntityId=${params.data.domainEntityId}&externalLoginKey=${params.data.externalLoginKey}">${params.value}</a>`; 
	} else if (params.value) { 
		return `${params.value}`; 
	} 
} 

function contactNumber(params) { 
	if (params.data.partyId && params.data.domainEntityId && params.value) { 
		return `<i class="fa fa fa-phone fa-1"></i> <a target="_blank" href="createPhoneCallActivity?partyId=${params.data.partyId}&domainEntityType=${params.data.domainEntityType}&domainEntityId=${params.data.domainEntityId}&externalLoginKey=${params.data.externalLoginKey}">${params.value}</a>`; 
	} else if (params.value) { 
		return `${params.value}`; 
	} 
} 

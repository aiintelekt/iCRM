/*fagReady("INVITE_USERS_LIST", function(el, api, colApi, gridApi){
    $("#inviteUsers-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#inviteUsers-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#inviteUsers-clear-filter-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    	gridApi.refreshUserPreferences();
    });
	$("#sub-filter-clear-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    });
    $("#inviteUsers-export-btn").click(function () {
    	gridApi.csvExport();
    });

    $("#refresh-inviteUsers-btn").click(function () {
    	loadInviteUserGrid(gridApi);
    });
    
    $(".filter-inviteUsers").click(function(event) {
        event.preventDefault(); 
        
        $("#inviteUsers-grid-header-title").html($(this).attr("data-searchTypeLabel"));
        //alert($(this).attr("data-searchTypeLabel"));
        $("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));
        
        loadInviteUserGrid(gridApi);
    });   
    
    $('#invite-user-form-submit').on('click', function (e) {

		var partyId= $("#inviteUser #partyId").val();
		var primaryContactId= $("#inviteUser #primaryContactId").val();
		$.ajax({
        type: "POST",
        url: "createUserLoginForContact",
        data: { 
	            "domainEntityType":$("#domainEntityType").val(),
	            "partyId": partyId,
	            "primaryContactId":primaryContactId,
        },
        sync: true,
        success: function(data) {
            $("#invite-user-modal").modal('hide');
             var message = data.errMsg;
          	  showAlert ("success", message);
          	  location.reload();
           	loadInviteUserGrid(gridApi); 
				
        }

    });
	e.preventDefault();
});
    
    loadInviteUserGrid(gridApi);
});

function loadInviteUserGrid(gridApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	$.ajax({
	  async: false,
	  url:'/common-portal/control/searchInviteUsers',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#invite-user").serialize())),
	  success: function(data){
		  gridApi.setRowData(data.data);
	  }
	});
}*/


/*$(function() {
	loadInviteUserGrid();
});


var columnInviteUserDefs = [
{
	"headerName": "Contact Name",
	"field": "name",
	"sortable": true,
	"filter": true,
	"filter": "agTextColumnFilter",
	cellRenderer: function(params) {
		return '<a target="_blank" href="/contact-portal/control/viewContact?partyId=' + params.data.invitePartyId + '&domainEntityType=' + "CONTACT" + '">' + params.data.name + '</a>';
		
	}
	
  },{
	  "headerName": "Primary",
		"field": "isPrimary",
		"sortable": true,
		"filter": true,
		"filter": "agTextColumnFilter"
  },{
	  "headerName": "Primary Email",
		"field": "primaryContactEmail",
		"sortable": true,
		"filter": true,
		"filter": "agTextColumnFilter"
  },{
	  "headerName": "Primary Phone",
		"field": "primaryContactPhone",
		"sortable": true,
		"filter": true,
		"filter": "agTextColumnFilter"
},{
	  "headerName": "Designation",
		"field": "designation",
		"sortable": true,
		"filter": true,
		"filter": "agTextColumnFilter"
  },{
	  "headerName": "Enable/Disable Login",
	  "sortable":false,
	    "filter":false,
		 cellRenderer: function(params) {
		    	if(params.data.enabled != null && params.data.enabled != "" && params.data.enabled == "Y")
		    		
		    		return '<a  href="#onboarding" class="btn btn-xs btn-primary" onclick="disableLogin(\''+params.data.invitePartyId+'\')"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i>' + " Disable Login" +  '</a>';
		    	else
		    		return '<a  href="#onboarding" class="btn btn-xs btn-primary" onclick="enableLogin(\''+params.data.invitePartyId+'\')"><i class="fa fa-chevron-circle-right" aria-hidden="true"></i>' + " Enable Login" + '</a>';
		    }
  },{
	  "headerName": "Reset Password",
		"field": "",
		"sortable":false,
	    "filter":false,
		cellRenderer: function(params) {
			if(params.data.enabled != null && params.data.enabled != "" && params.data.enabled == "Y")
				//return '<span class=" btn btn-xs btn-primary" title="" onclick="resetPasswordForContact(\''+params.data.invitePartyId+'\')"></span>'
				return '<a  href="#onboarding" class="btn btn-xs btn-primary" onclick="resetPasswordForContact(\''+params.data.invitePartyId+'\')"><i class="fa fa-key" aria-hidden="true"></i>' + " Reset Password" +  '</a>';
	    }
  }  	  	 
    
];

$(document).ready(function() {
	
	
});

var gridOptionsInviteUsers = null;
function loadInviteUserGrid() {
	$("#invite-user-grid").empty();
	gridOptionsInviteUsers = {
		defaultColDef : {
			filter : true,
			sortable : true,
			resizable : true
		},
		columnDefs : columnInviteUserDefs,
		floatingFilter : true,
		rowSelection : "multiple",
		editType : "fullRow",
		paginationPageSize : 10,
		domLayout : "autoHeight",
		pagination : true,
		onGridReady : function() {
			//sizeToFitNote();
			getInviteUserRowData();
		}
	}

	// lookup the container we want the Grid to use
	var eGridDiv = document.querySelector("#invite-user-grid");
	// create the grid passing in the div to use together with the columns &
	// data we want to use
	new agGrid.Grid(eGridDiv, gridOptionsInviteUsers);

}

function getInviteUserAjaxResponse(callback) {
	var result = [];
	var resultRes = null;
	var params = {}
	var inputData = {};
	
	var paramStr = $("#note-search-form").serialize();
	
	//var componentMountPoint = $("#componentMountPoint").val(); 
	
	//inputData = {"componentMountPoint": componentMountPoint};
	
	console.log("formData--->"+JSON.stringify(paramStr));
    inputData = JSON.parse(JSON.stringify(paramStr));
	
	var errorMessage = null;
	var resultData = null;
	$.ajax({
		type : "POST",
		url : "/common-portal/control/searchInviteUsers",
		async : true,
		data : inputData,
		success : function(data) {
			var result1 = data[0];
			console.log("--result1-----" + result1);
			if (data[0] != null || data[0] != undefined) {
				errorMessage = data[0].errorMessage;
				resultData = data[0].errorResult;
			}
			if (errorMessage != null || errorMessage != undefined) {
				showAlert("error", errorMessage);
				console.log("--errorMessage-----" + errorMessage);
				callback(resultData);
			} else {
				//alert("in else");
				callback(data.data);
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

function sizeToFitNote() {
	gridOptionsInviteUsers.api.sizeColumnsToFit();
}

function getInviteUserRowData() {
	var result;
	result = getInviteUserAjaxResponse(function(agdata) {
		gridOptionsInviteUsers.api.setRowData(agdata);
	});
 //alert(result);
}
*/


$(function() {
	let onboardInstanceId= "INVITE_USERS_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = onboardInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#inviteUsers-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, onboardInstanceId, userId);
	});
	
	
	$('#inviteUsers-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, onboardInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getOnboardGridData();
		}
	});
	$('#sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	
	
	$("#refresh-inviteUsers-btn").click(function () {
		getOnboardGridData();
    });
    
    $(".filter-inviteUsers").click(function(event) {
        event.preventDefault(); 
        
        $("#inviteUsers-grid-header-title").html(DOMPurify.sanitize($(this).attr("data-searchTypeLabel")));
        $("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));
        getOnboardGridData();
    });   
    
    $('#invite-user-form-submit').on('click', function (e) {

		var partyId= $("#inviteUser #partyId").val();
		var primaryContactId= $("#inviteUser #primaryContactId").val();
		$.ajax({
        type: "POST",
        url: "createUserLoginForContact",
        data: { 
	            "domainEntityType":$("#domainEntityType").val(),
	            "partyId": partyId,
	            "primaryContactId":primaryContactId,
        },
        sync: true,
        success: function(data) {
            $("#invite-user-modal").modal('hide');
             var message = data.errMsg;
          	  showAlert ("success", message);
          	  location.reload();
          	getOnboardGridData(gridApi); 
				
        }

    });
	e.preventDefault();
});

	function getOnboardGridData(){
		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/searchInviteUsers";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#invite-user";
		callCtx.ajaxResponseKey = "data";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getOnboardGridData();
	}
});
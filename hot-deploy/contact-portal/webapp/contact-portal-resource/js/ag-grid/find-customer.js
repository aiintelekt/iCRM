let findCustomerGridInstanceId = $("#searchForm #findCustomerGridInstanceId").val();
//
//fagReady(findCustomerGridInstanceId, function(el, api, colApi, gridApi){
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
//        setTimeout(() => {  loadMainGrid(gridApi, api, colApi); }, 1000);
//    })
//
//    $("#main-search-btn").click(function () {
//    	loadMainGrid(gridApi, api, colApi);
//    });
//    $("#insert-btn").click(function () {
//    	
//    	gridApi.insertNewRow()
//    })
//    $("#remove-btn").click(function () {
//        //removeMainGrid(fag1, api);
//    	gridApi.removeSelected();
//        setTimeout(() => {  loadMainGrid(gridApi, api, colApi); }, 1000);
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
//    
//    $(".filter-customer").click(function(event) {
//        event.preventDefault(); 
//        
//        $("#customer-grid-header-title").html($(this).attr("data-searchTypeLabel"));
//       // alert($(this).attr("data-searchTypeLabel"));
//        $("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));
//        
//        loadMainGrid(gridApi, api, colApi);
//    });
//    //To submit the form to while click the enter button
//    $("#searchForm").on("keypress", function (event) {
//        var keyPressed = event.keyCode || event.which; 
//        if (keyPressed === 13) { 
//        	loadMainGrid(gridApi, api, colApi);
//            event.preventDefault();
//            return false; 
//        } 
//    }); 
//    
//    $("#grid-export-btn").click(function () {
//		exportList();
//	});
//    
//    loadMainGrid(gridApi, api, colApi);
//});

//onSelectionChanged: onSelectionChanged, 
const onSelectionChanged = (params) => {
  const selectedNodes = params.api.getSelectedNodes();
  const currentPageNodes = params.api.getRenderedNodes();

  // Deselect rows that are not on the current page
  selectedNodes.forEach((node) => {
    if (currentPageNodes.indexOf(node) === -1) {
      params.api.deselectNode(node);
    }
  });
};


//function loadMainGrid(gridApi, api, colApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	var formInput = $('#searchForm, #limitForm').serialize();
//	$.ajax({
//	  async: true,
//	  url:'/common-portal/control/searchCustomers',
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




$(function() {
	let customerCallListInstanceId= findCustomerGridInstanceId;
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = customerCallListInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	//gridInstance.hideOverlay();
	$('#customer-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, customerCallListInstanceId, userId);
	});
	$('#customer-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, customerCallListInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getCustomerCalllistGridData();
		}
	});
	$('#customer-sub-filter-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
    $("#main-search-btn").click(function () {
		getCustomerCalllistGridData();
    });
    $("#grid-export-btn").click(function () {
    	//gridInstance.exportDataAsCsv();
    	exportList('searchForm');
	});
    $("#fetch-previous").click(function () {
    	fetchPrevious();
		getCustomerCalllistGridData();
    });
    $("#fetch-next").click(function () {
    	fetchNext();
		getCustomerCalllistGridData();
    });
    $("#fetch-first").click(function () {
    	fetchFirst();
		getCustomerCalllistGridData();
    });
    $("#fetch-last").click(function () {
    	fetchLast();
		getCustomerCalllistGridData();
    });
    
    $('#goto-page').keypress(function(event){
        var keycode = (event.keyCode ? event.keyCode : event.which);
        if(keycode == '13'){
        	if(goto()){
        		getCustomerCalllistGridData();
        	}
        }
    });
    $(".filter-customer").click(function(event) {
        event.preventDefault(); 
        $("#customer-grid-header-title").html(DOMPurify.sanitize($(this).attr("data-searchTypeLabel")));
        $("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));
		getCustomerCalllistGridData();
    });
    //To submit the form to while click the enter button
	$("#searchForm").on("keypress", function (event) {
		var keyPressed = event.keyCode || event.which; 
		if (keyPressed === 13) {
			getCustomerCalllistGridData();
			event.preventDefault();
			return false; 
		} 
	});
	function getCustomerCalllistGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/searchCustomers";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm, #limitForm_"+customerCallListInstanceId;
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getCustomerCalllistGridData();
	}
});


function exportList() {
	$.ajax({
		async: true,
		url:'/common-portal/control/exportData',
		type:"POST",
		data: JSON.parse(JSON.stringify($("#searchForm").serialize())),
		success: function(data){
			showAlert("success", "Export in-progress, please check download screen.");
		}
	});
}

function name(params) {
	return `<a href="viewCustomer?partyId=${params.data.partyId}" class="btn-xs ml-0 px-0" target="_blank">${params.value}</a>`;
}

function infoString(params) { 
	if (params.data.partyId && params.data.domainEntityId && params.value) { 
		return `<i class="fa fa fa-envelope fa-1"></i><a target="_blank" href="addEmail?partyId=${params.data.partyId}&domainEntityType=${params.data.domainEntityType}&domainEntityId=${params.data.domainEntityId}&externalLoginKey=${params.data.externalLoginKey}">${params.value}</a>`; 
	} else if (params.value) { 
		return `${params.value}`; 
	} 
}

function contactNumber(params) { 
	if (params.data.partyId && params.data.domainEntityId && params.value) { 
		return `<i class="fa fa fa-phone fa-1"></i><a target="_blank" href="createPhoneCallActivity?partyId=${params.data.partyId}&domainEntityType=${params.data.domainEntityType}&domainEntityId=${params.data.domainEntityId}&externalLoginKey=${params.data.externalLoginKey}">${params.value}</a>`; 
	} else if (params.value) { 
		return `${params.value}`; 
	} 
}
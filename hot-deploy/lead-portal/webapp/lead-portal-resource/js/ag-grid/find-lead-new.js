
fagReady("LEADS_LIST", function(el, api, colApi, gridApi){
    $("#refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#clear-filter-btn").click(function () {
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
    $("#export-btn").click(function () {
    	gridApi.csvExport();
    });

    $("#update-btn").click(function () {
    	gridApi.saveUpdates();
        setTimeout(() => {  loadMainGrid(gridApi, api); }, 1000);
    })

    $("#main-search-btn").click(function () {
    	resetGridStatusBar();
    	loadMainGrid(gridApi, api);
    });
    $("#insert-btn").click(function () {
    	gridApi.insertNewRow()
    })
    $("#remove-btn").click(function () {
        //removeMainGrid(fag1, api);
    	gridApi.removeSelected();
        setTimeout(() => {  loadMainGrid(gridApi, api); }, 1000);
        
    });
    
    $(".filter-lead").click(function(event) {
        event.preventDefault(); 
        
        $("#lead-grid-header-title").html(DOMPurify.sanitize($(this).attr("data-searchTypeLabel")));
        //alert($(this).attr("data-searchTypeLabel"));
        $("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));
        
        loadMainGrid(gridApi, api);
    });
    
    $("#fetch-previous").click(function () {
    	fetchPrevious();
    	loadMainGrid(gridApi, api);
    });
    $("#fetch-next").click(function () {
    	fetchNext();
    	loadMainGrid(gridApi, api);
    });
    $("#fetch-first").click(function () {
    	fetchFirst();
    	loadMainGrid(gridApi, api);
    });
    $("#fetch-last").click(function () {
    	fetchLast();
    	loadMainGrid(gridApi, api);
    });
    
    $('#goto-page').keypress(function(event){
        var keycode = (event.keyCode ? event.keyCode : event.which);
        if(keycode == '13'){
        	if(goto())
        		loadMainGrid(gridApi, api); 
        }
    });
    /*
    $("#goto-page").keyup(function () {
    	
    	if(goto())
    		loadMainGrid(gridApi, api);
    }); */
    
    // to load the data at the initial request
    loadMainGrid(gridApi, api);
});

var urlPath = "";
function loadMainGrid(gridApi, api) {
	if(urlPath == ""){
		resetGridStatusBar();
		urlPath = getGridDataFetchUrl("LEADS_LIST");
	}
	var rowData =[];
	gridApi.setRowData(rowData);
	if(urlPath != null && urlPath != "" && urlPath !="undefined"){
		api.showLoadingOverlay();
		var formInput = $('#searchForm, #limitForm').serialize();
		$.ajax({
		  async: true,
		  url:urlPath,
		  type:"POST",
		  data: JSON.parse(JSON.stringify(formInput)),
		  success: function(data){
			  gridApi.setRowData(data.list);
			  data.list=[];
			  paginateHandler(data);
		  }
		});
	}
}

function viewLead(params) {
	return `<a href="/lead-portal/control/viewLead?partyId=${params.data.partyId}">${params.value}</a>`;
}

function getRmName(params) {
	var partyFirstName = params.data.partyFirstName;
	var partyLastName = params.data.partyLastName;
	var rm = "";
	if (partyFirstName != null && partyFirstName != "") {
		rm = partyFirstName;
	}
	if (partyLastName != null && partyLastName != "") {
		rm = partyFirstName + ' ' + partyLastName;
	}
	return rm;
}
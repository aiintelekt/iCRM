fagReady("LIST_ORDERS_MAIN", function(el, api, colApi, gridApi){
    $("#order-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#order-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#order-clear-filter-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    	gridApi.refreshUserPreferences();
    });
	$("#order-sub-filter-clear-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    });
    $("#order-export-btn").click(function () {
    	gridApi.csvExport();
    });

    $("#refresh-order-btn").click(function () {
    	loadOrderGrid(gridApi, api, colApi);
    });
    
    $(".filter-order").click(function(event) {
        event.preventDefault(); 
        
        $("#order-grid-header-title").html(DOMPurify.sanitize($(this).attr("data-searchTypeLabel")));
        //alert($(this).attr("data-searchTypeLabel"));
        $("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));
        
        loadOrderGrid(gridApi, api, colApi);
    });  
    
    //loadOrderGrid(gridApi, api, colApi);
    postLoadGrid(api, gridApi, colApi, "sr-orders", loadOrderGrid);
    postLoadGrid(api, gridApi, colApi, "a-orders", loadOrderGrid);
    postLoadGrid(api, gridApi, colApi, "c-orders", loadOrderGrid);
    postLoadGrid(api, gridApi, colApi, "orders", loadOrderGrid);
    
    //api.sizeColumnsToFit();
});

function loadOrderGrid(gridApi, api, colApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	api.showLoadingOverlay();
	$.ajax({
	  async: false,
	  url:'/common-portal/control/searchMainOrders',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#order-search-form").serialize())),
	  success: function(result){
		  gridApi.setRowData(result.data);
		  setTimeout(() => colApi.autoSizeAllColumns(), 1000);
	  }
	});
}

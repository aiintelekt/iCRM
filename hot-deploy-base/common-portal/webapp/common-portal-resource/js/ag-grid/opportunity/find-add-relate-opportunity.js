
fagReady("OPPORTUNITYS", function(el, api, colApi, gridApi){
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

    $("#add-relate-opp-search-btn").click(function () {
    	loadAddRelateOpportunityGrid(gridApi);
    });
    
    $('#add-relate-opp-modal').on('shown.bs.modal', function (e) {
    	api.sizeColumnsToFit();
	});
   
    $("#add-relate-to-opportunity-btn").click(function () {
    	
    	var selectedData = gridApi.selectedRowData;
    	//alert(selectedData)
    	
    	if (selectedData.salesOpportunityId) {
    		var targetSalesOpportunityId = $("#add-relate-opportunity-search-form input[name=domainEntityId]").val();
        	
        	var selectedSalesOpportunityIds = "";
        	selectedSalesOpportunityIds += selectedData.salesOpportunityId;
        	
        	var inputData = {"targetSalesOpportunityId": targetSalesOpportunityId, "selectedSalesOpportunityIds": selectedSalesOpportunityIds};
		    
		    $.ajax({
				type : "POST",
				url : "/common-portal/control/relateOpportunity",
				async : true,
				data : inputData,
				success : function(data) {
					if (data.code == 200) {
						//showAlert ("success", "Successfully related opportunity# "+selectedSalesOpportunityIds);
						showAlert ("success", data.message);
						loadAddRelateOpportunityGrid(gridApi);
					} else {
						showAlert ("error", data.message);
					}
				},
				error : function() {
					console.log('Error occured');
					showAlert("error", "Error occured!");
				},
				complete : function() {
				}
			});
    	}
    	
    	
    });
    
    loadAddRelateOpportunityGrid(gridApi);
});

function loadAddRelateOpportunityGrid(gridApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	$.ajax({
	  async: true,
	  url:'/common-portal/control/searchOpportunitys',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#add-relate-opportunity-search-form").serialize())),
	  success: function(data){
		  gridApi.setRowData(data);
	  }
	});
}

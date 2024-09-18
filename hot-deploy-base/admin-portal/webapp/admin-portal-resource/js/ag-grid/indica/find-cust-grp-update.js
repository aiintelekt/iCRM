fagReady("CUST_GRP_UPDATE_LIST", function(el, api, colApi, gridApi){
    $("#custgrpup-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#custgrpup-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#custgrpup-clear-filter-btn").click(function () {
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
    $("#custgrpup-export-btn").click(function () {
    	gridApi.csvExport();
    });

    $("#fetch-previous").click(function () {
    	fetchPrevious();
    	loadMainGrid(gridApi, api, colApi);
    });
    $("#fetch-next").click(function () {
    	fetchNext();
    	loadMainGrid(gridApi, api, colApi);
    });
    $("#fetch-first").click(function () {
    	fetchFirst();
    	loadMainGrid(gridApi, api, colApi);
    });
    $("#fetch-last").click(function () {
    	fetchLast();
    	loadMainGrid(gridApi, api, colApi);
    });
    $('#goto-page').keypress(function(event){
        var keycode = (event.keyCode ? event.keyCode : event.which);
        if(keycode == '13'){
        	if(goto()) {
        		loadMainGrid(gridApi, api, colApi);
        	}
        }
    });
    $("#custgrpup-search-form").on("keypress", function (event) {
        var keyPressed = event.keyCode || event.which; 
        if (keyPressed === 13) { 
        	event.preventDefault(); 
        	loadMainGrid(gridApi, api, colApi); 
            return false; 
        } 
    }); 

    $("#custgrpup-refresh-btn").click(function () {
    	loadMainGrid(gridApi, api, colApi);
    });
    
    $("#main-search-btn").click(function () {
    	loadMainGrid(gridApi, api, colApi);
    });
    
    $("#custgrpup-repost-btn").click(function () {
		var selectedData = api.getSelectedRows();
		if (selectedData.length > 0) {
			
			console.log(selectedData);
			
		    var selectedSeqIds = "";
		    for (i = 0; i < selectedData.length; i++) {
		    	var data = selectedData[i];
		    	selectedSeqIds += data.seqId+",";
		    }
		    if (selectedSeqIds && selectedData.length > 0){
			    selectedSeqIds = selectedSeqIds.substring(0, selectedSeqIds.length - 1);
			    
			    var inputData = {"selectedSeqIds": selectedSeqIds};
			    
			    $.ajax({
					type : "POST",
					url : "/admin-portal/control/repostCustGrpUpdate",
					async : true,
					data : inputData,
					success : function(result) {
						if (result.code == 200) {
							showAlert ("success", "Successfully reposted!");
							loadMainGrid(gridApi, api, colApi);
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
		} else {
			showAlert("error", "Please select atleast one row to be reposted!");
		}
    });
    
    loadMainGrid(gridApi, api, colApi);
});

function loadMainGrid(gridApi, api, colApi) {
	console.log('calling find method');
	
	var rowData =[];
	gridApi.setRowData(rowData);
	api.showLoadingOverlay();
	
	$.ajax({
	  async: true,
	  url:'/admin-portal/control/searchCustGrpUpdates',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#custgrpup-search-form").serialize())),
	  success: function(data){
		  gridApi.setRowData(data.list);
		  data.list=[];
		  paginateHandler(data);
	  }
	});
}
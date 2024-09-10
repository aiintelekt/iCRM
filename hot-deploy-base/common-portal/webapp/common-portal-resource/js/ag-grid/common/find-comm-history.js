/*
fagReady("COMM_HISTORY", function(el, api, colApi, gridApi){
    $("#comm-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#comm-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#comm-clear-filter-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    	gridApi.refreshUserPreferences();
    });
    $("#comm-export-btn").click(function () {
    	gridApi.csvExport();
    }); 
   
    postLoadGrid(api, gridApi, colApi, "a-communicationHistory", loadIssueMaterialGrid);
    //loadIssueMaterialGrid(gridApi,api, colApi);
    
});


function loadCommunicationHistory(gridApi, api, colApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	api.showLoadingOverlay();
	$.ajax({
	  async: false,
	  url:'/common-portal/control/getEmailActivities',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#comm-history-search-form").serialize())),
	  success: function(data){
		  gridApi.setRowData(data.list);
		  setTimeout(() => colApi.autoSizeAllColumns(), 1000);
	  }
	});
}

*/


function loadCommunicationHistoryOld(){
	
	$.ajax({
	  async: false,
	  url:'/common-portal/control/getEmailActivities',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#comm-history-search-form").serialize())),
	  success: function(data){
		  let resultList = data.list;
		  if(resultList != null){
			let htmlStr = "";
			for ( var i = 0, l = resultList.length; i < l; i++ ) {
				let commData = resultList[ i ];
				htmlStr = htmlStr +'<div class="col-md-12 col-lg-12 col-sm-12" style="background-color: #02829d;"> <div style="color: white;"> <b> ${fromPartyName!} said... </b> <span class="float-right"> <b> ${entryDate!} </b> </span> </div></div>';
				htmlStr = htmlStr +'<div class="row"><div class="col-md-10 col-lg-10 col-sm-10" >'+description+'</div><div class="col-md-2 col-lg-2 col-sm-2" >${toPartyName!}</div></div>';
				
				console.log("subject------>"+commData.subject);
				
			}
		  }
	  }
	});
}
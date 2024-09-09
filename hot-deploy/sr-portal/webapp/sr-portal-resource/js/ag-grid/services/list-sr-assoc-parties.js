
var yesNoOptions = JSON.parse('{"":"","Y":"Yes","N":"No"}');

fagReady("SR_ASSOC_PARTY_LIST", function(el, api, colApi, gridApi){
    $("#sr-assoc-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#sr-assoc-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#sr-assoc-clear-filter-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    	gridApi.refreshUserPreferences();
    });
	$("#sr-assoc-sub-filter-clear-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    });
    $("#sr-assoc-export-btn").click(function () {
    	gridApi.csvExport();
    });
    $("#sr-assoc-update-btn").click(function () {
    	gridApi.saveUpdates();
        setTimeout(() => {  loadSRAssocPartiesGrid(api, gridApi); gridApi.refreshUserPreferences(); }, 1000);
    });

    $("#sr-assoc-remove-btn").click(function () {
    	var selectedRows=api.getSelectedRows();
    	if(selectedRows!= undefined && selectedRows != null&& selectedRows.length>0){
    		gridApi.removeSelected();
            setTimeout(() => { loadSRAssocPartiesGrid(api, gridApi); }, 1000);
    	} else {
            showAlert("error","Please select atleast one record in the list");
        }
    });
    
    postLoadGridData(api, gridApi, "associated-parties", loadSRAssocPartiesGrid);
    
    //loadSRAssocPartiesGrid(gridApi, api, colApi);
});

var findSRAssocUrl= "";
function loadSRAssocPartiesGrid(api, gridApi) {
	var srNumber = $("#srNumberUrlParam").val();
	if(findSRAssocUrl == ""){
		resetGridStatusBar();
		findSRAssocUrl = getGridDataFetchUrl("SR_ASSOC_PARTY_LIST");
	}
	var rowData =[];
	gridApi.setRowData(rowData);
	if(findSRAssocUrl != null && findSRAssocUrl != "" && findSRAssocUrl !="undefined"){
		api.showLoadingOverlay();
		var formInput = $('#searchForm, #limitForm').serialize();
		$.ajax({
		  async: true,
		  url:findSRAssocUrl,
		  type:"POST",
		  data: {"srNumber": srNumber},
		  success: function(data){
			  gridApi.setRowData(data.list);
			  data.list=[];
			  paginateHandler(data);
		  }
		});
	}
}

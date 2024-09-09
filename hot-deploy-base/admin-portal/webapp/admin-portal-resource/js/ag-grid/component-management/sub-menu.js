var subGridApi ="";
var subApi = "";

fagReady("SUB_MENUS", function(el, api, colApi, gridApi){
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
    $("#export-btn").click(function () {
    	gridApi.csvExport();
    });
    $("#update-btn").click(function () {
    	gridApi.saveUpdates();
        setTimeout(() => { loadSubMenus(gridApi, api); }, 1000);
    });
    subGridApi = gridApi;
    subApi = api;
 });

function SubMenu(tabId) {
	loadSubMenus(subGridApi,subApi,tabId);
}

var subMenusUrl = "";

function loadSubMenus(gridApi,api,tabId) {
	if(subMenusUrl == ""){
		subMenusUrl = getGridDataFetchUrl("SUB_MENUS");
	}
		var rowData =[];
	gridApi.setRowData(rowData);
	if(subMenusUrl !=null  && subMenusUrl !="" && subMenusUrl !="undefined"){
		api.showLoadingOverlay();
		var formInput = $('#menuForm, #limitForm').serialize();
		$.ajax({
		  async: false,
		  url:subMenusUrl,
		  type:"POST",
		  data: { "componentId": $("#componentId").val(),"tabId": tabId },
		  success: function(data){
			  gridApi.setRowData(data);
		  }
		});
	}
}

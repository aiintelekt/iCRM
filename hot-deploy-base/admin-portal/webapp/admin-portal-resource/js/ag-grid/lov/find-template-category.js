$(function() {
	const templateCategoryInstanceId= 'TEMPLATE_CATEGORY_LIST';
	const externalLoginKey = $("input[name='externalLoginKey']").val();
	const formDataObject = {};
	const userId = $("input[name='userId']").val();
	let gridInstance  = "";
	let templateCategoryUrl = "";

	formDataObject.gridInstanceId = templateCategoryInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	formDataObject.dataFetchCall = getTemplateCategoryRowData;

	gridInstance = prepareGridInstance(formDataObject);

	if(templateCategoryUrl == ""){
		templateCategoryUrl = getGridDataFetchUrl("TEMPLATE_CATEGORY_LIST");
	}

	if(templateCategoryUrl == "" || templateCategoryUrl == null){
		templateCategoryUrl = "/admin-portal/control/searchTemplateCategory"
	}

	if(gridInstance){
		getTemplateCategoryRowData();
	}

	function getTemplateCategoryRowData() {
		const callCtx = {};
		callCtx.ajaxUrl = templateCategoryUrl;
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#template-search-form";
		callCtx.ajaxResponseKey = "";

		setGridData(gridInstance, callCtx);
	}

	$('#template-category-save-pref').click(function(){
		saveGridPreference(gridInstance, templateCategoryInstanceId, userId);
	});
	$("#template-category-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#template-category-clear-pref').click(function(){
		clearGridPreference(gridInstance, templateCategoryInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getTemplateCategoryRowData();
		}
	});

	$('#template-category-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});

	$("#main-search-btn").click(function () {
		getTemplateCategoryRowData();
	});

	$("#template-search-form").on("keypress", function (event) {
		let keyPressed = event.keyCode || event.which;
		if(keyPressed === 13) {
			event.preventDefault();
			getTemplateCategoryRowData();
			return false;
		}
	});
});

/*
fagReady("TEMPLATE_CATEGORY_LIST", function(el, api, colApi, gridApi){
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
        setTimeout(() => {  loadMainGrid(gridApi); }, 1000);
    })

    $("#main-search-btn").click(function () {
    	loadMainGrid(gridApi);
    });
    $("#template-search-form").on("keypress", function (event) {
        var keyPressed = event.keyCode || event.which; 
        if (keyPressed === 13) { 
        	event.preventDefault(); 
        	loadMainGrid(gridApi); 
            return false; 
        } 
    });
    loadMainGrid(gridApi);
});

function loadMainGrid(gridApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	$.ajax({
	  async: false,
	  url:'/admin-portal/control/searchTemplateCategory',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#template-search-form").serialize())),
	  success: function(data){
		  gridApi.setRowData(data);
	  }
	});
}
*/
function templateCategoryId(params){
	return `<a href="/admin-portal/control/viewTemplateCategory?parentTemplateCategoryId=${params.data.parentTemplateCategoryId}&templateCategoryId=${params.data.templateCategoryId}">${params.data.templateCategoryId}</a>`
}	
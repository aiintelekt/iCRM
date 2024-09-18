//fagReady("LIST_PROMO_CAMPAIGN", function(el, api, colApi, gridApi){
//	$("#pc-refresh-pref-btn").click(function () {
//		gridApi.refreshUserPreferences();
//	});
//	$("#pc-save-pref-btn").click(function () {
//		gridApi.saveUserPreferences();
//	});
//	$("#pc-clear-filter-btn").click(function () {
//		try{
//			gridApi.clearAllColumnFilters();
//		}catch(e){
//		}
//		gridApi.refreshUserPreferences();
//	});
//	$("#sub-filter-clear-btn").click(function () {
//		try{
//			gridApi.clearAllColumnFilters();
//		}catch(e){
//		}
//	});
//	$("#pc-export-btn").click(function () {
//		gridApi.csvExport();
//	});
//
//	$("#promoCampaignPicker-search-btn").click(function () {
//		loadPromoCampaignPickerGrid(gridApi,api,colApi);
//	});
//	$('#promoCampaignPicker').on('shown.bs.modal', function (e) {
//		loadPromoCampaignPickerGrid(gridApi,api,colApi);
//		api.sizeColumnsToFit();
//	});
//
//	$("#find-promo-search-btn").click(function () {
//		loadTemplatePickerGrid(gridApi, api, colApi);
//	});
//	$("#find_promo_camp_trigger").click(function(event) {
//		event.preventDefault();
//		promo_campaign_picker_instance = $('#promo_campaign_picker_instance').val();
//		loadPromoCampaignPickerGrid(gridApi, api, colApi);
//		colApi.autoSizeAllColumns();
//	});
//});
//
//function loadPromoCampaignPickerGrid(gridApi,api,colApi) {
//	var rowData =[];
//	var externalLoginKey =  $('form[name="'+promo_campaign_picker_instance+'_Form"] input[name="externalLoginKey"]').val();
//	gridApi.setRowData(rowData);
//	api.showLoadingOverlay();
//	$.ajax({
//		async: true,
//		url: "/loyalty-portal/control/getPromoCampaigns?externalLoginKey="+externalLoginKey,
//		type:"POST",
//		data: JSON.parse(JSON.stringify($("#"+promo_campaign_picker_instance+"_Form").serialize())),
//		success: function(data){
//			gridApi.setRowData(data.listVal);
//			setTimeout(() => colApi.autoSizeAllColumns(), 1000);
//		}
//	});
//}

let promo_campaign_picker_instance ="";
$(function() {
	let promoCampaignInstanceId= "LIST_PROMO_CAMPAIGN";
	let gridInstance  = "";
	//var externalLoginKey =  $('form[name="'+promo_campaign_picker_instance+'_Form"] input[name="externalLoginKey"]').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = promoCampaignInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#pc-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, promoCampaignInstanceId, userId);
	});
	$('#pc-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, promoCampaignInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getPCGridData();
		}
	});
    $('#pc-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#pc-export-btn").click(function () {
	    gridInstance.exportDataAsCsv();
	});
	$("#promoCampaignPicker-search-btn").click(function () {
		getPCGridData();
	});
	$('#promoCampaignPicker').on('shown.bs.modal', function (e) {
		getPCGridData();
		//gridInstance.sizeColumnsToFit();
	});

	$("#find-promo-search-btn").click(function () {
		getPCGridData();
	});
	$("#find_promo_camp_trigger").click(function(event) {
		event.preventDefault();
		//promo_campaign_picker_instance = $('#promo_campaign_picker_instance').val();
		getPCGridData();
		//colApi.autoSizeAllColumns();
	});
	function getPCGridData(){
		gridInstance.showLoadingOverlay();
		promo_campaign_picker_instance = $('#promo_campaign_picker_instance').val();

		const callCtx = {};
		callCtx.ajaxUrl = "/loyalty-portal/control/getPromoCampaigns";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#"+promo_campaign_picker_instance+"_Form";
		callCtx.ajaxResponseKey = "listVal";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getPCGridData();
	}
});

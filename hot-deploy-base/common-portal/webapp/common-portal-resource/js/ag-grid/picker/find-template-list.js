var find_temp_picker_instance_id = "";
$(function() {
	let findTemplatelistInstanceId= "FIND_TEMPLATE_LIST";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = findTemplatelistInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#find-template-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, findTemplatelistInstanceId, userId);
	});
	$("#find-temp-list-search-btn").click(function () {
		getFindTemplateGridData();
	});
	
	$("#find_temp_list_trigger").click(function(event) {
        event.preventDefault();
        getFindTemplateGridData();
    });
	$('#find-template-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, findTemplatelistInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getFindTemplateGridData();
		}
	});
	$('#find-template-sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	
	function getFindTemplateGridData(){
		gridInstance.showLoadingOverlay();
		find_temp_picker_instance_id = $('#find_temp_picker_instance').val();
		console.log("find_temp_picker_instance_id=="+find_temp_picker_instance_id);
		
		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/findTemplatesAjax";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#"+find_temp_picker_instance_id+"_Form";
		callCtx.ajaxResponseKey = "data";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getFindTemplateGridData();
	}
});

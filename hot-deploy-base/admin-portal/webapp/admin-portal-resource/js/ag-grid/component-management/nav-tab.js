$(function() {
	const listNavTabsInstanceId= 'LIST_NAV_TABS';
	const externalLoginKey = $("input[name='externalLoginKey']").val();
	const formDataObject = {};
	const userId = $("input[name='userId']").val();
	let gridInstance  = "";
	let listNavTabsUrl = "";

	formDataObject.gridInstanceId = listNavTabsInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	formDataObject.dataFetchCall = getListNavTabsRowData;

	gridInstance = prepareGridInstance(formDataObject);

	if(listNavTabsUrl == ""){
		listNavTabsUrl = getGridDataFetchUrl("LIST_NAV_TABS");
	}

	if(listNavTabsUrl == "" || listNavTabsUrl == null){
		listNavTabsUrl = "getNavigationTabList"
	}

	if(gridInstance){
		getListNavTabsRowData();
	}

	function getListNavTabsRowData() {
		const callCtx = {};
		callCtx.ajaxUrl = listNavTabsUrl;
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#searchForm";
		callCtx.ajaxResponseKey = "";

		setGridData(gridInstance, callCtx);
	}

	$('#list-nav-tabs-save-pref').click(function(){
		saveGridPreference(gridInstance, listNavTabsInstanceId, userId);
	});

	$('#list-nav-tabs-clear-pref').click(function(){
		clearGridPreference(gridInstance, listNavTabsInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getListNavTabsRowData();
		}
	});

	$('#list-nav-tabs-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});

	$("#nav-tab-search-btn").click(function () {
		getListNavTabsRowData();
	});
	$("#list-nav-tabs-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$("#nav-tab-refresh-button").click(function () {
		getListNavTabsRowData();
	});
	$("#searchForm").on("keypress", function (event) {
		let keyPressed = event.keyCode || event.which;
		if(keyPressed === 13) {
			event.preventDefault();
			getListNavTabsRowData();
			return false;
		}
	});
	$("#update-nav-tab-btn").click(function () {
		event.preventDefault();
		var componentId = $("#updateNavTab #updateComponentId").val();
		var tabConfigId = $("#updateNavTab #updateTabConfigId").val();
		var tabId = $("#updateNavTab #updateTabId").val();
		var tabName = $("#updateNavTab #tabName").val();
		var isEnabled = $("#updateNavTab input[name=isEnabled]:checked").val();
		var sequenceNo = $("#updateNavTab #sequenceNo").val();
		$.ajax({
			type: "POST",
			url: "updateNavTabDetails",
			async: true,
			data:{
				"componentId": componentId,
				"tabConfigId": tabConfigId,
				"tabId":tabId,
				"tabName":tabName,
				"isEnabled":isEnabled,
				"sequenceNo":sequenceNo
			},
			success: function(data) {
				if(data.success == "success"){
					$('#update-nav-tab-modal').modal("hide");
					showAlert("success","Navigation tab updated successfully");
					getListNavTabsRowData();
				}else{
					showAlert("error","Navigation tab cannot be updated");
				}
			}
		});
		return false;
	});
});

/*
fagReady("LIST_NAV_TABS", function(el, api, colApi, gridApi){
	$("#nav-tab-refresh-pref-btn").click(function () {
		gridApi.refreshUserPreferences();
	});
	$("#nav-tab-save-pref-btn").click(function () {
		gridApi.saveUserPreferences();
	});
	$("#nav-tab-clear-filter-btn").click(function () {
		try{
			gridApi.clearAllColumnFilters();
		}catch(e){
		}
		gridApi.refreshUserPreferences();
	});
	//function to search when enter key is pressed
	$("#searchForm").on("keypress", function (event) {
		var keyPressed = event.keyCode || event.which;
		if (keyPressed === 13) {
			loadNavigationTabs(gridApi, api);
			event.preventDefault();
			return false; 
		}
	});
	$("#nav-tab-sub-filter-clear-btn").click(function () {
		try{
			gridApi.clearAllColumnFilters();
		}catch(e){
		}
	});
	$("#nav-tab-export-btn").click(function () {
		gridApi.csvExport();
	});
	$("#nav-tab-refresh-button").click(function () {
		loadNavigationTabs(gridApi, api);
	});
	$("#nav-tab-search-btn").click(function () {
		loadNavigationTabs(gridApi, api);
	});
	//update nav tab details
	$("#update-nav-tab-btn").click(function () {
		event.preventDefault();
		var componentId = $("#updateNavTab #updateComponentId").val();
		var tabConfigId = $("#updateNavTab #updateTabConfigId").val();
		var tabId = $("#updateNavTab #updateTabId").val();
		var tabName = $("#updateNavTab #tabName").val();
		var isEnabled = $("#updateNavTab input[name=isEnabled]:checked").val();
		//var favIcon = $("#updateNavTab #favIcon").val();
		//var tabContent = $("#updateNavTab #tabContent").val();
		var sequenceNo = $("#updateNavTab #sequenceNo").val();
		$.ajax({
			type: "POST",
			url: "updateNavTabDetails",
			async: true,
			data:{
				"componentId": componentId,
				"tabConfigId": tabConfigId,
				"tabId":tabId,
				"tabName":tabName,
				"isEnabled":isEnabled,
				"sequenceNo":sequenceNo
			},
			success: function(data) {
				if(data.success == "success"){
					$('#update-nav-tab-modal').modal("hide");
					showAlert("success","Navigation tab updated successfully");
					loadNavigationTabs(gridApi, api);
				}else{
					showAlert("error","Navigation tab cannot be updated");
				}
			}
		});
		return false;
	});
	$("#nav-tab-remove-btn").click(function() {
		var selectedRows = api.getSelectedRows();
		if (selectedRows != undefined && selectedRows != null && selectedRows.length > 0) {
			gridApi.removeSelected();
			setTimeout(() => {
				loadNavigationTabs(gridApi, api);
			}, 1000);
		} else {
			showAlert("error", "Please select atleast one record in the list");
		}
	});
	loadNavigationTabs(gridApi, api);
});
*/
//create new nav tab
function submitCreateNavTab() {
	event.preventDefault();
	var componentId = $('#mainFrom #componentId').val();
	var configId = $('#mainFrom #tabConfigId').val();
	var tabId = $('#mainFrom #tabId').val();
	if(componentId && configId && tabId){
	$.ajax({
		type: "POST",
		url: "createNewNavTab",
		async: true,
		data:JSON.parse(JSON.stringify($("#mainFrom").serialize())),
		success: function(data) {
			if(data.success == "success"){
				showAlert("success","Navigation tab created successfully");
				$('#mainFrom').find('input[type=text],input[type=number]').val('');
				$('#mainFrom #componentId').dropdown('clear');
				$("#nav-tab-refresh-button").click();
				return true;
			}else{
				showAlert("error","Navigation tab cannot be created");
			}
		}
	});
	$('#create-nav-tab-modal').modal("hide");
	}
	return false;
}
/*
var navigationTabUrl = "";
function loadNavigationTabs(gridApi, api) {
	if(navigationTabUrl == ""){
		resetGridStatusBar();
		navigationTabUrl = getGridDataFetchUrl("LIST_NAV_TABS");
	}
	var rowData =[];
	gridApi.setRowData(rowData);
	if(navigationTabUrl != null && navigationTabUrl != "" && navigationTabUrl !="undefined"){
		api.showLoadingOverlay();
		$.ajax({
			async: false,
			url:navigationTabUrl,
			type:"POST",
			data:JSON.parse(JSON.stringify($('#searchForm').serialize())),
			success: function(data){
				gridApi.setRowData(data);
			}
		});
	}
}
*/
//popup to create nav tabs
function enableCreateNavPopup(){
	$("#mainFrom #componentId_error").html('');
	$("#mainFrom #tabConfigId_error").html('');
	$("#mainFrom #tabId_error").html('');
	$('#mainFrom').find('input[type=text],input[type=number]').val('');
	$('#mainFrom #componentId').dropdown('clear');
	$('#create-nav-tab-modal').modal("show");
}
//popup to update nav tabs
function enableEditNavPopup(componentId,tabConfigId,tabId){
	var url = "getDetailsForEditNavPopup";
	$.ajax({
		type: "POST",
		url: url,
		async: true,
		data: {
			"componentId": componentId,
			"tabConfigId": tabConfigId,
			"tabId":tabId
		},
		success: function(data) {
			if(data != null){
				$('#updateComponentId').val(componentId);
				$('#updateNavTab #updateTabConfigId').val(tabConfigId);
				$('#updateNavTab #updateTabId').val(tabId);
				$('#componentId_value').html(DOMPurify.sanitize(data.description));
				$('#updateNavTab #tabConfigId_value').html(DOMPurify.sanitize(tabConfigId));
				$('#updateNavTab #tabId_value').html(DOMPurify.sanitize(tabId));
				$('#updateNavTab #tabName').val(data.tabName);
				$('#updateNavTab input:radio[name=isEnabled][value="'+ data.isEnabled +'"]').prop('checked', true);
				$('#updateNavTab #favIcon').html(DOMPurify.sanitize(data.favIcon));
				$('#updateNavTab #tabContent').html(DOMPurify.sanitize(data.tabContent));
				$('#updateNavTab #sequenceNo').val(data.sequenceNo);
				$('#update-nav-tab-modal_des_title').html("Update Navigation Tab");
				$('#update-nav-tab-modal').modal("show");
			}
			else{
				$('#updateNavTab #updateComponentId').val(componentId);
				$('#updateNavTab #updateTabConfigId').val(tabConfigId);
				$('#updateNavTab #updateTabId').val(tabId);
				$('#updateNavTab #componentId_value').html(DOMPurify.sanitize(data.description));
				$('#updateNavTab #tabConfigId_value').html(tabConfigId);
				$('#updateNavTab #tabId_value').html(tabId);
				$('#update-nav-tab-modal_des_title').html("Update Navigation Tab");
				$('#update-nav-tab-modal').modal("show");
			}
		}
	});
}
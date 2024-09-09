$(function() {
	const apvlTemplateInstanceId= 'SQLGRP_LIST';
	const externalLoginKey = $("input[name='externalLoginKey']").val();
	const formDataObject = {};
	const userId = $("input[name='userId']").val();
	let gridInstance  = "";
	let sqlGroupUrl = "";

	formDataObject.gridInstanceId = apvlTemplateInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	formDataObject.dataFetchCall = getSqlGroupRowData;

	gridInstance = prepareGridInstance(formDataObject);

	if(sqlGroupUrl == ""){
		sqlGroupUrl = getGridDataFetchUrl("SQLGRP_LIST");
	}

	if(sqlGroupUrl == "" || sqlGroupUrl == null){
		sqlGroupUrl = "/admin-portal/control/searchSqlGroups"
	}

	if(gridInstance){
		getSqlGroupRowData();
	}

	function getSqlGroupRowData() {
		const callCtx = {};
		callCtx.ajaxUrl = sqlGroupUrl;
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#sql-group-search-form";
		callCtx.ajaxResponseKey = "list";

		setGridData(gridInstance, callCtx);
	}

	$('#sql-group-save-pref').click(function(){
		saveGridPreference(gridInstance, apvlTemplateInstanceId, userId);
	});

	$('#sql-group-clear-pref').click(function(){
		clearGridPreference(gridInstance, apvlTemplateInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getSqlGroupRowData();
		}
	});

	$('#sql-group-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});

	$("#main-search-btn").click(function () {
		getSqlGroupRowData();
	});
	$("#sql-group-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$("#sql-group-search-form").on("keypress", function (event) {
		let keyPressed = event.keyCode || event.which;
		if(keyPressed === 13) {
			event.preventDefault();
			getSqlGroupRowData();
			return false;
		}
	});
});

function viewSqlGroup (params){
	return `<a href="/admin-portal/control/viewSqlGroup?sqlGroupId=${params.data.sqlGroupId}">${params.data.sqlGroupId}</a>`;
}
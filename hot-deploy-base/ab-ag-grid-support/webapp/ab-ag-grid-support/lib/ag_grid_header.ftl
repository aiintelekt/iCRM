<link rel="stylesheet" href="/ab-ag-grid-support-resource/css/ag-grid.css" />
<link rel="stylesheet" href="/ab-ag-grid-support-resource/css/fio-ag-grid.css" />
<link rel="stylesheet" href="/ab-ag-grid-support-resource/css/ag-theme-balham-dark.css" />
 
${setContextField("isInitiateFioAgGrid", "Y")}

<#include "component://ab-ag-grid-support/webapp/ab-ag-grid-support/lib/agGridMacro.ftl"/>
<!-- IT HAS A VERSION PLEASE TAKE NOTE -->
<script type="text/javascript" src="/ab-ag-grid-support-resource/js/fio-ag-grid-3.3.2.js"></script>

<#--
<script>

jQuery(document).ready(function() {

var mainGridContainer = '<span class="btn btn-xs btn-primary" id="main-insert-new-row" title="${uiLabelMap.Insert!}"><i class="fa fa-insert" aria-hidden="true"></i> ${uiLabelMap.Insert!}</span>'
	+ '<span class="btn btn-xs btn-primary" id="main-saveUpdatesBtn" title="${uiLabelMap.Update!}"><i class="fa fa-edit" aria-hidden="true"></i> ${uiLabelMap.Update!}</span>'
	+ '<span class="btn btn-xs btn-primary" id="main-grid-refresh-pref-btn" title="${uiLabelMap.refreshGridPreference!}"><i class="fa fa-refresh" aria-hidden="true"></i> ${uiLabelMap.refreshPreference!}</span>'
	+ '<span class="btn btn-xs btn-primary" id="main-grid-save-pref-btn" title="${uiLabelMap.saveGridPreference!}"><i class="fa fa-edit" aria-hidden="true"></i> ${uiLabelMap.savePreference!}</span>'
	+ '<span class="btn btn-xs btn-primary" id="main-grid-clear-filter-btn" title="${uiLabelMap.clearGridFilter!}"><i class="fa fa-eraser" aria-hidden="true"></i> ${uiLabelMap.clearFilter!}</span>'
	+ '<span class="btn btn-xs btn-primary" id="main-grid-export-btn" title="${uiLabelMap.exportGridData!}"><i class="fa fa-file-excel-o" aria-hidden="true"></i> ${uiLabelMap.export!}</span>'
;

$("#main-grid-action-container").append(mainGridContainer);

fagReady(function(el, api, colApi, fag){
		
	$("#main-grid-refresh-pref-btn").click(function(){
		fag.refreshUserPreferences();
	});
	$("#main-grid-save-pref-btn").click(function(){
		fag.saveUserPreferences();
	});
	$("#main-grid-clear-filter-btn").click(function(){
		fag.clearAllColumnFilters();
	});
	$("#main-grid-export-btn").click(function(){
		fag.csvExport();
	});
	
	$("#main-saveUpdatesBtn").click(function(){
		fag.saveUpdates();
	})
	
	$("#main-search-btn").click(function(){
		loadMainGrid(fag);
	});
	$("#main-insert-new-row").click(function(){
		fag.insertNewRow()
	})
	$("#main-grid-remove-btn").click(function(){
		removeMainGrid(fag, api);
	});
	
	loadMainGrid(fag);
	
});
	
});

function loadMainGrid(fag) {
}
function removeMainGrid(fag, api) {
}

</script>

-->


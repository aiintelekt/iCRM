
<#--
NB: all parameter values are strings, in the case where json is expected {"property":"value"} must be enclosed in "" only 
since compelete paramater value will be enclosed in ''
Multiple grid instances per page have been disabled in favor of a simplier js interface

##Required Paramaters:
userid 
instanceid 

##Optional Paramaters:
These have defaults defined in the webcomponent
gridoptions="none" 
endpointverb="post" 
requestbody="none"
endpoint="/ab-ag-grid-support/control/performFindList" 
configendpointfetch="/ab-ag-grid-support/control/getGridUserConfig" 
configendpointsave="/ab-ag-grid-support/control/saveGridUserConfig" 
autosizeallcol="false" 
debug="false" 
showuserprefspinner="false"
datacreate="none"
dataupdate="none"
dataremove="none"
aggridthemeclass="ag-theme-balham-dark"
buttonbarbuttons="none"
shownotifications="false"
hidebuttonbar="false"

##On Wrapper Ready Callback
fagReady(<instanceId>,function(el, api, colApi, fag){
 //el is the dom element that you can add known listeners to 
 //eg: notifications or buttonBarClickEvent (for built in buttons or internal buttons set with buttonbarbuttons=...)

 //api is the ag grid api object for the grid instance
 //colApi is the ag grid column api object for the grid instance
 //fag is the wrapper itself, if you have hidden the button bar with hidebuttonbar="true" you can call the internal buttons using there methods
})

##Notifications
shownotifications="false"
fagReady(<instanceId>, function(el, api, colApi, fag){
  el.addEventListener("notifications", (evt) => {
		console.log('notifications evt: ', evt.detail)
    //detail: {type: "danger", message: "This is a test notification"}

	})
})

## wrapper spinner events
fagReady(<instanceId>,function(el, api, colApi, fag){
el.addEventListener("spinnerEvents", (evt) => {
  console.log('wrapper spinnerEvents evt: ', evt)
	
	})
  })

##buttonbarbuttons Input is an array of type ButtonBarButton:
interface ButtonBarButton {
  label: string;
  clickEventId: string;
  styleClass: string;
}
eg: buttonbarbuttons='[{"label":"Btn 1", "clickEventId":"btn1", "styleClass":"btn btn-primary"}, {"label":"Btn 2", "clickEventId":"btn2", "styleClass":"btn btn-primary"}]'

##Exposed Grid Functions (see grid examples):

within fagReady you can call the following methods on the wrapper:

 fag.saveUserPreferences()
 fag.refreshUserPreferences()
 fag.saveUpdates()
 fag.removeSelected()
 fag.insertNewRow()
 fag.csvExport()
 fag.getRowData()
 fag.showAllColumns()
 fag.setRowData(rowData) //need to use this with edit mode so data is prepped

##Exposed Button Bar Event Listener (see grid examples):

where instanceid eg: 0002 is the target you provide
fagReady(<instanceId>,function(el, api, colApi, fag){
  el.addEventListener("buttonBarClickEvent", (evt) => {
		//evt.detail.clickEventId will allow you to react to whatever the clickEventId was set
    //internal clickEventIds (hidebuttonbar="false") are builtin_saveUserPreferences, builtin_refreshUserPreferences, builtin_saveUpdates, builtin_removeSelected, builtin_insertNewRow, builtin_csvExport   
	})
})

## [Not Available] ExcelExportOptions can be set into gridOptions: custom.excelExportOptions either in gridOptions json or via admin ui editor else defaults will be used
NB: set ag grid ent license key with window.fioaggridentlicensekey = ''//your key
interface CsvExportOptions {
  skipHeader?: boolean;
  columnGroups?: boolean;
  skipFooters?: boolean;
  skipGroups?: boolean;
  skipPinnedTop?: boolean;
  skipPinnedBottom?: boolean;
  allColumns?: boolean;
  onlySelected?: boolean;
  fileName?: string;
  exportMode?: "csv";
}

##Parameters that can be override by saved grid user config (admin role):
gridoptions="none" 
endpointverb="post" 
requestbody="none"
endpoint="/ab-ag-grid-support/control/performFindList" 
configendpointfetch="/ab-ag-grid-support/control/getGridUserConfig" 
configendpointsave="/ab-ag-grid-support/control/saveGridUserConfig" 
datacreate="none"
dataupdate="none"
dataremove="none"

##Parameters that can be override by saved grid user config (user role):
gridoptions="none" 

##Required Assets:
For AdabtableBlotter and AgGrid to display correctly the following css assets need to be loaded:
<link rel="stylesheet" href="assets/css/ag-theme-balham-dark.css" />
<link rel="stylesheet" href="assets/css/slate/bootstrap.min.css" />
<link rel="stylesheet" href="assets/css/fio-ag-grid.css" />
For the fio-ag-ab-grid webcomponent to function the following javascript asset needs to be loaded
<script type="text/javascript" src="assets/fio-agab-grid-ver.js"></script>
-->
<link rel="stylesheet" href="/bootstrap/css/ag-grid.css" />
<link rel="stylesheet" href="/bootstrap/css/ag-theme-balham.css">
<link rel="stylesheet" href="/bootstrap/css/fio-ag-grid.css" />

<#macro gridHeader title id 
	insertBtn=false insertBtnClass="btn-xs btn-primary" insertBtnId="insert-btn" insertBtnLabel="${uiLabelMap.Insert!}"
	updateBtn=false updateBtnClass="btn-xs btn-primary" updateBtnId="update-btn" updateBtnLabel="${uiLabelMap.Update!}"
	removeBtn=false removeBtnClass="btn-xs btn-primary" removeBtnId="remove-btn" removeBtnLabel="${uiLabelMap.Remove!}"
	refreshPrefBtn=false refreshPrefBtnClass="btn-xs btn-primary" refreshPrefBtnId="refresh-pref-btn" refreshPrefBtnLabel="${uiLabelMap.RefreshGridPreference!}"
	savePrefBtn=false savePrefBtnClass="btn-xs btn-primary" savePrefBtnId="save-pref-btn" savePrefBtnLabel="${uiLabelMap.SaveGridPreference!}"
	clearFilterBtn=false clearFilterBtnClass="btn-xs btn-primary" clearFilterBtnId="clear-filter-btn" clearFilterBtnLabel="${uiLabelMap.ClearGridFilter!}"
	exportBtn=false exportBtnClass="btn-xs btn-primary" exportBtnId="export-btn" exportBtnLabel="${uiLabelMap.ExportGridData!}"
	>
	<div class="page-header border-b pt-2">
	    <@headerH2 title=title! class="float-left"/>
	    <div class="float-right" id="${id}">
			<#if insertBtn><span class="btn ${insertBtnClass!}" id="${insertBtnId!}" title="${insertBtnLabel!}"><i class="fa fa-plus" aria-hidden="true"></i> ${insertBtnLabel!}</span> </#if>
			<#if updateBtn><span class="btn ${updateBtnClass!}" id="${updateBtnId!}" title="${updateBtnLabel!}"><i class="fa fa-edit" aria-hidden="true"></i> ${updateBtnLabel!}</span> </#if>
			<#if removeBtn><span class="btn ${removeBtnClass!}" id="${removeBtnId!}" title="${removeBtnLabel!}"><i class="fa fa-remove" aria-hidden="true"></i> ${removeBtnLabel!}</span> </#if>
			<#if refreshPrefBtn><span class="btn ${refreshPrefBtnClass!}" id="${refreshPrefBtnId}" title="${refreshPrefBtnLabel!}"><i class="fa fa-refresh" aria-hidden="true"></i> ${refreshPrefBtnLabel!}</span> </#if>
			<#if savePrefBtn><span class="btn ${savePrefBtnClass!}" id="${savePrefBtnId!}" title="${savePrefBtnLabel!}"><i class="fa fa-save" aria-hidden="true"></i> ${savePrefBtnLabel!}</span> </#if>
			<#if clearFilterBtn><span class="btn ${clearFilterBtnClass!}" id="${clearFilterBtnId}" title="${clearFilterBtnLabel!}"><i class="fa fa-eraser" aria-hidden="true"></i> ${clearFilterBtnLabel!}</span> </#if>
			<#if exportBtn><span class="btn ${exportBtnClass}" id="${exportBtnId}" title="${exportBtnLabel!}"><i class="fa fa-file-excel-o" aria-hidden="true"></i> ${exportBtnLabel!}</span> </#if>
	    </div>
	    <div class="clearfix"></div>
	</div>
</#macro>

<#macro AgGrid userid instanceid buttonbarbuttons="none" aggridthemeclass="ag-theme-balham" autosizeallcol="false" debug="false" datacreate="none" dataupdate="none" dataremove="none" gridoptions="none"  endpoint="none" endpointverb="none" requestbody="none" preferenceendpointfetch="/ab-ag-grid-support/control/getGridUserConfig" preferenceendpointsave="/ab-ag-grid-support/control/saveGridUserConfig" showuserprefspinner="false" shownotifications="false" hidebuttonbar="true">
	<fio-ag-grid id='${instanceid}'
		hidebuttonbar='${hidebuttonbar}'
		shownotifications='${shownotifications}'
		showuserprefspinner='${showuserprefspinner}'
		userid='${userid}'
		instanceid='${instanceid}'
		requestbody='${requestbody}'
		autosizeallcol='${autosizeallcol}'
		debug='${debug}'
		preferenceendpointfetch='${preferenceendpointfetch}'
		preferenceendpointsave='${preferenceendpointsave}'
		endpoint='${endpoint}'
		gridoptions='${gridoptions}'
		endpointverb='${endpointverb}'
		datacreate = '${datacreate}'
		dataupdate = '${dataupdate}'
		dataremove = '${dataremove}'
		aggridthemeclass = '${aggridthemeclass}'
		buttonbarbuttons = '${buttonbarbuttons}'
		>
	</fio-ag-grid>
</#macro>

<script type="text/javascript" src="/bootstrap/js/fio-ag-grid-3.3.2.js"></script>
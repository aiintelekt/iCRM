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
<#macro gridHeader title id="" extra="" extraLeft="" extraRight="" headertitleid="" instanceid="" userid=""
	colmgmtBtn=true colmgmtBtnClass="btn-xs btn-primary" colmgmtBtnId="colmgmt-btn" colmgmtBtnLabel="${uiLabelMap.ColumnManagement!}"
	insertBtn=true insertBtnClass="btn-xs btn-primary" insertBtnId="insert-btn" insertBtnLabel="${uiLabelMap.Insert!}"
	updateBtn=true updateBtnClass="btn-xs btn-primary" updateBtnId="update-btn" updateBtnLabel="${uiLabelMap.Update!}"
	removeBtn=true removeBtnClass="btn-xs btn-primary" removeBtnId="remove-btn" removeBtnLabel="${uiLabelMap.Remove!}"
	refreshPrefBtn=true refreshPrefBtnClass="btn-xs btn-primary" refreshPrefBtnId="refresh-pref-btn" refreshPrefBtnLabel="${uiLabelMap.RefreshGridPreference!}"
	savePrefBtn=true savePrefBtnClass="btn-xs btn-primary" savePrefBtnId="save-pref-btn" savePrefBtnLabel="${uiLabelMap.SaveGridPreference!}"
	clearFilterBtn=true clearFilterBtnClass="btn-xs btn-primary" clearFilterBtnId="clear-filter-btn" clearFilterBtnLabel="${uiLabelMap.ClearGridFilter!}"
	exportBtn=true exportBtnClass="btn-xs btn-primary" exportBtnId="export-btn" exportBtnLabel="${uiLabelMap.ExportGridData!}"
	helpBtn=false helpBtnClass="btn-xs btn-primary" helpBtnId="help-btn" helpBtnLabel="${uiLabelMap.Help!}" helpUrl="#"
	serversidepaginate=false statusBar=true columnConfig=false
	subFltrClearBtn = true subFltrClearBtnClass="btn-xs btn-primary" subFltrClearId="sub-filter-clear-btn" subFltrClearBtnLabel = "Clear Sub Filter"
	>
	<div class="popup-agtitle">
	    <@headerH2 title=title! id=headertitleid! class="float-left sub-txt"/>
	    ${extraLeft!}
	    <div class="float-right noselect mt-2 mb-2" id="${id!}">
	    	${extra!}
	    	<#-- <#if colmgmtBtn><a href="columnManagement?gridInstanceId=${instanceid!}" class="btn ${colmgmtBtnClass!}" id="${colmgmtBtnId!}" title="${colmgmtBtnLabel!}"><i class="fa fa-plus" aria-hidden="true"></i> ${colmgmtBtnLabel!}</a> </#if> -->
			<#if insertBtn><span class="btn ${insertBtnClass!}" id="${insertBtnId!}" title="${insertBtnLabel!}"><i class="fa fa-plus" aria-hidden="true"></i> ${insertBtnLabel!}</span> </#if>
			<#if subFltrClearBtn><span class="btn ${subFltrClearBtnClass!}" id="${subFltrClearId!}" title="${subFltrClearBtnLabel!}"><i class="fa fa-refresh" aria-hidden="true"></i> ${subFltrClearBtnLabel!}</span> </#if>
			<#if updateBtn><span class="btn ${updateBtnClass!}" id="${updateBtnId!}" title="${updateBtnLabel!}"><i class="fa fa-edit" aria-hidden="true"></i> ${updateBtnLabel!}</span> </#if>
			<#if removeBtn><span class="btn ${removeBtnClass!}" id="${removeBtnId!}" data-toggle="confirmation" title="${uiLabelMap.RemoveConfirmation!}"><i class="fa fa-remove" aria-hidden="true"></i> ${removeBtnLabel!}</span> </#if>
			<#if refreshPrefBtn><span class="btn ${refreshPrefBtnClass!}" id="${refreshPrefBtnId}" title="${refreshPrefBtnLabel!}"><i class="fa fa-refresh" aria-hidden="true"></i> ${refreshPrefBtnLabel!}</span> </#if>
			<#if savePrefBtn><span class="btn ${savePrefBtnClass!}" id="${savePrefBtnId!}" title="${savePrefBtnLabel!}"><i class="fa fa-save" aria-hidden="true"></i> ${savePrefBtnLabel!}</span> </#if>
			<#if clearFilterBtn><span class="btn ${clearFilterBtnClass!}" id="${clearFilterBtnId}" data-toggle="confirmation" title="Do you want to remove user preference?" title="${clearFilterBtnLabel!}"><i class="fa fa-eraser" aria-hidden="true"></i> ${clearFilterBtnLabel!}</span> </#if>
			<#if exportBtn><span class="btn ${exportBtnClass}" id="${exportBtnId}" title="${exportBtnLabel!}"><i class="fa fa-file-excel-o" aria-hidden="true"></i> ${exportBtnLabel!}</span> </#if>
			<#if helpBtn><span class="btn ${helpBtnClass}" id="${helpBtnId}" title="${helpBtnLabel!}"><i class="fa fa-question-circle" aria-hidden="true"></i> <a target="_blank" class="btn-primary" href="${helpUrl!'#'}">${helpBtnLabel!}</a></span> </#if>
			${extraRight!}
			<#if serversidepaginate>
	    		<#assign gridInstanceAttribute = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("attrValue").from("GridInstanceAttribute").where("instanceId",instanceid!,"attrName","fio.grid.fetch.limit").queryOne())?if_exists />
	    		<#if gridInstanceAttribute?has_content>
	    			<#local fetchLimit = gridInstanceAttribute?if_exists.attrValue! />
	    		<#else>
	    			<#local fetchLimit = 1000 />
	    		</#if>
	    		<#-- 
	    		<span class="btn btn-xs btn-primary" id="fetch-previous" title="Fetch Previous ${fetchLimit!}"><i class="fa fa-arrow-circle-left" aria-hidden="true"></i></span>
	    		<span class="btn btn-xs btn-primary" id="fetch-next" title="Fetch Next ${fetchLimit!}"><i class="fa fa-arrow-circle-right" aria-hidden="true"></i></span>
	    		-->
	    		<span class="btn btn-xs btn-primary" id="fetch-first" title="First"><i class="fa fa-angle-double-left" aria-hidden="true"></i></span>
	    		<span class="btn btn-xs btn-primary" id="fetch-previous" title="Fetch Previous ${fetchLimit!}"><i class="fa fa-angle-left" aria-hidden="true"></i></span>
	    		<span class="btn-xs"><input type="text" class="form-control goto-btn" value="1" id="goto-page" name="goto-page" autocomplete="off" maxlength="3"></span>
	    		<span class="btn btn-xs btn-primary" id="fetch-next" title="Fetch Next ${fetchLimit!}"><i class="fa fa-angle-right " aria-hidden="true"></i></span>
	    		<span class="btn btn-xs btn-primary" id="fetch-last" title="Last"><i class="fa fa-angle-double-right" aria-hidden="true"></i></span>
	    		<style>
	    			.goto-btn {
	    			    display: inline-block;
					    width: 35px;
					    height: 25px;
					    text-align: center;
					    vertical-align: bottom;
					}
	    		</style>
	    		
	    	<form id="limitForm" name="limitForm" action="#" method="">
    			<@inputHidden id="TOTAL_CHUNK" value="" />
    			<@inputHidden id="VIEW_INDEX" value="0" />
    			<@inputHidden id="VIEW_SIZE" value="${fetchLimit!}" />
    		</form>	
	    	</#if>
	    	<#-- 
	    	<#assign userGridInstance = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).from("GridUserPreferences").where("instanceId",instanceid!,"role","USER","userId",userid).queryOne())?if_exists />
	    	<#if columnConfig && userGridInstance?has_content>
	    		<a class="settings" target="_blank" href="/ofbiz-ag-grid/control/userColumnManagement?gridInstanceId=${instanceid!}&gridUserId=${userid!}&externalLoginKey=${requestAttributes.externalLoginKey!}" id="columnManagement" name="columnManagement"><i class="fa fa-cog" aria-hidden="true"></i></a>
	    	</#if> -->
	    </div>
	    <div class="clearfix"></div>
	</div>
<script>
initiateDefaultEvents();
</script>
</#macro>

<#macro FioAgGrid userid instanceid buttonbarbuttons="none" aggridthemeclass="ag-theme-balham" autosizeallcol="false" debug="false" datacreate="none" dataupdate="none" dataremove="none" gridoptions="none"  endpoint="none" endpointverb="none" requestbody="none" preferenceendpointfetch="/ofbiz-ag-grid/control/getGridUserConfig" preferenceendpointsave="/ofbiz-ag-grid/control/saveGridUserConfig" showuserprefspinner="false" shownotifications="false" hidebuttonbar="true">
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
  ></fio-ag-grid>
</#macro>


<#macro AgGrid userid instanceid gridheadertitle="" gridheaderid="" buttonbarbuttons="none" aggridthemeclass="ag-theme-balham" autosizeallcol="false" debug="false" 
datacreate="none" dataupdate="none" dataremove="none" gridoptions="none"  endpoint="none" endpointverb="none" requestbody="none" 
preferenceendpointfetch="/ofbiz-ag-grid/control/getGridUserConfig" preferenceendpointsave="/ofbiz-ag-grid/control/saveGridUserConfig" showuserprefspinner="false" shownotifications="false" hidebuttonbar="true"
colmgmtBtn=true colmgmtBtnClass="btn-xs btn-primary" colmgmtBtnId="colmgmt-btn" colmgmtBtnLabel="${uiLabelMap.ColumnManagement!}"
insertBtn=true insertBtnClass="btn-xs btn-primary" insertBtnId="insert-btn" insertBtnLabel="${uiLabelMap.Insert!}"
updateBtn=true updateBtnClass="btn-xs btn-primary" updateBtnId="update-btn" updateBtnLabel="${uiLabelMap.Update!}"
removeBtn=true removeBtnClass="btn-xs btn-primary" removeBtnId="remove-btn" removeBtnLabel="${uiLabelMap.Remove!}"
refreshPrefBtn=true refreshPrefBtnClass="btn-xs btn-primary" refreshPrefBtnId="refresh-pref-btn" refreshPrefBtnLabel="${uiLabelMap.RefreshGridPreference!}"
savePrefBtn=true savePrefBtnClass="btn-xs btn-primary" savePrefBtnId="save-pref-btn" savePrefBtnLabel="${uiLabelMap.SaveGridPreference!}"
clearFilterBtn=true clearFilterBtnClass="btn-xs btn-primary" clearFilterBtnId="clear-filter-btn" clearFilterBtnLabel="${uiLabelMap.ClearGridFilter!}"
exportBtn=true exportBtnClass="btn-xs btn-primary" exportBtnId="export-btn" exportBtnLabel="${uiLabelMap.ExportGridData!}"
helpBtn=false helpBtnClass="btn-xs btn-primary" helpBtnId="help-btn" helpBtnLabel="${uiLabelMap.Help!}" helpUrl="#"
headerextra="" headerextraleft="" headerextraright="" headertitleid=""
serversidepaginate=false statusBar=false columnConfig=true
subFltrClearBtn = true subFltrClearBtnClass="btn-xs btn-primary" subFltrClearId="sub-filter-clear-btn" subFltrClearBtnLabel = "Clear Sub Filter"
>
	<#assign hasGridAccess = true />
	<#assign hasInsert = insertBtn! />
	<#assign hasUpdate = updateBtn! />
	<#assign hasRemove = removeBtn! />
	<#assign hasRefreshPref = refreshPrefBtn! />
	<#assign hasSavePref = savePrefBtn! />
	<#assign hasClearFilter = clearFilterBtn! />
	<#assign hasExport = exportBtn! />
	<#assign hasColumnConfig = columnConfig! />
	
	<#assign agGridAccess = Static["org.fio.ag.grid.util.DataUtil"].hasGridAccess(delegator,request,instanceid,userid)?if_exists />
	<#if agGridAccess?has_content>
		<#if agGridAccess.GRID_ACCESS?exists && agGridAccess.GRID_ACCESS?has_content && "N"== agGridAccess.GRID_ACCESS! >
			<#assign hasGridAccess = false />
		<#else>
			<#assign hasGridAccess = true />
		</#if>
		<#if agGridAccess.GRID_INSERT?exists && agGridAccess.GRID_INSERT?has_content && "N"== agGridAccess.GRID_INSERT! >
			<#assign hasInsert = false />
		<#else>
			<#assign hasInsert = true />
		</#if>
		<#if agGridAccess.GRID_UPDATE?exists && agGridAccess.GRID_UPDATE?has_content && "N"== agGridAccess.GRID_UPDATE! >
			<#assign hasUpdate = false />
		<#else>
			<#assign hasUpdate = true />
		</#if>
		
		<#if agGridAccess.GRID_REMOVE?exists && agGridAccess.GRID_REMOVE?has_content && "N"== agGridAccess.GRID_REMOVE! >
			<#assign hasRemove = false />
		<#else>
			<#assign hasRemove = true />
		</#if>
		
		<#if agGridAccess.REFRESH_GRID_PREF?exists && agGridAccess.REFRESH_GRID_PREF?has_content && "N"== agGridAccess.REFRESH_GRID_PREF! >
			<#assign hasRefreshPref = false />
		<#else>
			<#assign hasRefreshPref = true />
		</#if>
		<#if agGridAccess.SAVE_GRID_PREF?exists && agGridAccess.SAVE_GRID_PREF?has_content && "N"== agGridAccess.SAVE_GRID_PREF! >
			<#assign hasSavePref = false />
		<#else>
			<#assign hasSavePref = true />
		</#if>
		<#if agGridAccess.CLEAR_GRID_FILTER?exists && agGridAccess.CLEAR_GRID_FILTER?has_content && "N"== agGridAccess.CLEAR_GRID_FILTER! >
			<#assign hasClearFilter = false />
		<#else>
			<#assign hasClearFilter = true />
		</#if>
		<#if agGridAccess.EXPORT_GRID_DATA?exists && agGridAccess.EXPORT_GRID_DATA?has_content && "N"== agGridAccess.EXPORT_GRID_DATA! >
			<#assign hasExport = false />
		<#else>
			<#assign hasExport = true />
		</#if>
		<#if agGridAccess.GRID_COLUMN_CONFIG?exists && agGridAccess.GRID_COLUMN_CONFIG?has_content && "N"== agGridAccess.GRID_COLUMN_CONFIG! >
			<#assign hasColumnConfig = false />
		<#else>
			<#assign hasColumnConfig = true />
		</#if>
	</#if>
	<#assign headerTitle = gridheadertitle! />
	<#-- <#if !gridheadertitle?has_content>
		<#assign headerTitle = instanceid+" Header" />
	</#if> -->	
	<#if hasGridAccess>
		<@gridHeader title=headerTitle! id=gridheaderid! instanceid=instanceid! userid=userid!
			colmgmtBtn=colmgmtBtn! colmgmtBtnClass=colmgmtBtnClass! colmgmtBtnId=colmgmtBtnId! colmgmtBtnLabel=colmgmtBtnLabel!
			insertBtn=hasInsert! insertBtnClass=insertBtnClass! insertBtnId=insertBtnId! insertBtnLabel=insertBtnLabel!
			updateBtn=hasUpdate! updateBtnClass=updateBtnClass! updateBtnId=updateBtnId! updateBtnLabel=updateBtnLabel!
			removeBtn=hasRemove! removeBtnClass=removeBtnClass! removeBtnId=removeBtnId! removeBtnLabel=removeBtnLabel!
			refreshPrefBtn=hasRefreshPref! refreshPrefBtnClass=refreshPrefBtnClass! refreshPrefBtnId=refreshPrefBtnId! refreshPrefBtnLabel=refreshPrefBtnLabel!
			savePrefBtn=hasSavePref! savePrefBtnClass=savePrefBtnClass! savePrefBtnId=savePrefBtnId! savePrefBtnLabel=savePrefBtnLabel!
			clearFilterBtn=hasClearFilter! clearFilterBtnClass=clearFilterBtnClass! clearFilterBtnId=clearFilterBtnId! clearFilterBtnLabel=clearFilterBtnLabel!
			exportBtn=hasExport! exportBtnClass=exportBtnClass! exportBtnId=exportBtnId! exportBtnLabel=exportBtnLabel!
			helpBtn=helpBtn! helpBtnClass=helpBtnClass! helpBtnId=helpBtnId! helpBtnLabel=helpBtnLabel! helpUrl=helpUrl!
			extra=headerextra! extraLeft=headerextraleft! extraRight=headerextraright! headertitleid=headertitleid! serversidepaginate=serversidepaginate!
			columnConfig=hasColumnConfig!
			subFltrClearBtn=subFltrClearBtn! subFltrClearBtnClass=subFltrClearBtnClass! subFltrClearId=subFltrClearId! subFltrClearBtnLabel=subFltrClearBtnLabel!
			/>
		
		<@FioAgGrid userid=userid! instanceid=instanceid! buttonbarbuttons=buttonbarbuttons! aggridthemeclass=aggridthemeclass! 
			autosizeallcol=autosizeallcol! debug=debug! datacreate=datacreate! dataupdate=dataupdate! dataremove=dataremove! 
			gridoptions=gridoptions!  endpoint=endpoint! endpointverb=endpointverb! requestbody=requestbody! 
			preferenceendpointfetch=preferenceendpointfetch! preferenceendpointsave=preferenceendpointsave! 
			showuserprefspinner=showuserprefspinner! shownotifications=shownotifications! hidebuttonbar=hidebuttonbar! />
		
		<div class="">
			<#assign isDisplayGridName = Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "DISPLAY_GRID_NAME", "Y") />
		    <#if "Y" == isDisplayGridName>
			    <#assign gridInstance = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).from("GridUserPreferences").where("instanceId", instanceid!, "userId", "admin", "role", "ADMIN").queryOne())?if_exists />
			    <#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "GRID_PREF_VIEW")?if_exists />
			    <#if gridInstance?has_content>
				    <#assign gridName = "" />
				    <#if hasPermission>
				    	<#assign gridName = "<a target='_blank' href='/ofbiz-ag-grid/control/viewAgGrid?instanceId=${instanceid!}&userId=admin&role=ADMIN&externalLoginKey=${requestAttributes.externalLoginKey!}'>${gridInstance.getString('name')!}</a>" />
				    <#else>
				    	<#assign gridName = gridInstance.getString("name")! />
				    </#if>
				    <#if gridName?has_content>
				   		<span class="pl-0" style="font-size: 16px;font-weight: bold;font-family: 'frutigernextltmedium';">Grid Name : ${gridName!}</span>
				   	</#if>
			    </#if>
		    </#if>
			<#if statusBar && serversidepaginate>
				<span class="pl-3" id="totalRecord" title="${totalRecordLabel!}"><i class="fa fa-clipboard" aria-hidden="true"></i> Total Records: <b id="totalRecordCount">0</b></span>
				<span class="pl-3" id="timeElapsed" title="${timeTakenLabel!}"><i class="fa fa-clock-o" aria-hidden="true"></i> Time Elapsed: <b id="timeTaken">0</b> seconds</span>
				<span class="pl-3" id="gridChunkSize" title="${chunkSizeLabel!}"><i class="fa fa-cube" aria-hidden="true"></i> Chunk Size: <b id="chunkSize">0</b></span>
				<span class="pl-3" id="chunks" title="${chunksLabel!}"><i class="fa fa-cubes" aria-hidden="true"></i> Chunks: <b id="chunkCount">0</b></span>
			</#if>
			<span id="fio_grid_dynamic_status_bar" class="fio_grid_dynamic_status_bar">
			</span>
		</div>
	<#else>
		<div class="row justify-content-md-center">
			<div class="pt-2">
				<div class="alert alert-danger fade show">
					<strong>Warning!</strong> Access Denied!
				</div>	
			</div>
   		</div>	
	</#if>
	
<script>
	<#if clearFilterBtn && clearFilterBtnId?has_content>
		$("#${clearFilterBtnId}").click( function(){
			$.ajax({
			  async: false,
			  url:'/ofbiz-ag-grid/control/adminRemoveGrid',
			  type:"POST",
			  data: {
			  	instanceId: "${instanceid!}",
			  	userId: "${userid!}",
			  	role: "USER"
			  },
			  success: function(data){
				 console.log("User preference cleared!");
			  }
			});
		});
	</#if>
</script>

</#macro>


<#assign requestURI = request.getRequestURI()/>
<#if !context.agGridResourceLoad?exists || "N" == context.agGridResourceLoad!>
<#--  <#if requestURI.contains("addservicerequest") || requestURI.contains("updateServiceRequest") || requestURI.contains("createTaskActivity") || requestURI.contains("updateActivity")>
<#else> -->
<link rel="stylesheet" href="/ofbiz-ag-grid-resource/css/ag-grid.css" />
<link rel="stylesheet" href="/ofbiz-ag-grid-resource/css/ag-theme-balham.css">
<link rel="stylesheet" href="/ofbiz-ag-grid-resource/css/fio-ag-grid.css" />
<script type="text/javascript" src="/ofbiz-ag-grid-resource/js/fio-grid-3.3.2.js"></script>
<script type="text/javascript" src="/ofbiz-ag-grid-resource/js/grid-common.js"></script>
${setContextField("agGridResourceLoad", "Y")}
<#--</#if> -->
</#if>
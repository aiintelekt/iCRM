<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row" style="width:100%" id="listof-lead">
	
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
<#assign readOnlyPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermissionWoFullPerm(request, "READ_ONLY_PERM")?if_exists />
<#assign rightContent='' />
<#if readOnlyPermission!>
<#else>
	<#assign rightContent='<span id="sr-mapit-btn" title="Map It" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-map" aria-hidden="true"></i> Map It </span> ' />
	<#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "SR_CSR")?if_exists />
	<#if hasPermission>
		<#assign rightContent= rightContent + '<a title="Create" href="/sr-portal/control/addservicerequest" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
	</#if>
</#if>

    
<#-- <@AgGrid
	gridheadertitle=uiLabelMap.ListOfSRs
	gridheaderid="lead-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=false
	headerextra=rightContent
	
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="SERVICE_REQUEST_LIST" 
    autosizeallcol="true"
    debug="false"
    statusBar=true
    serversidepaginate=false
    refreshPrefBtnId="sr-refresh-pref-btn"
    savePrefBtnId="sr-save-pref-btn"
    subFltrClearId="sr-sub-filter-clear-btn"
    clearFilterBtnId="sr-clear-filter-btn"
    exportBtnId="sr-export-btn"
    
    serversidepaginate=true
	statusBar=true
    />    
         
<script type="text/javascript" src="/sr-portal-resource/js/ag-grid/services/find-service-request.js"></script>-->
    <input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="sr-grid-header-title"
			instanceId="SERVICE_REQUEST_LIST"
			jsLoc="/sr-portal-resource/js/ag-grid/services/find-service-request.js"
			headerLabel=uiLabelMap.ListOfSRs
			headerId="sr-grid-action-container"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			subFltrClearBtn=true
			savePrefBtnId ="sr-save-pref-btn"
			clearFilterBtnId ="sr-clear-filter-btn"
			subFltrClearId="sr-sub-filter-clear-btn"
		    serversidepaginate=true
    		statusBar=true
    		headerExtra=rightContent!
    		exportBtnId="sr-list-export-btn"
			/>
</div>
  	
</div>
	
<script>     
$(document).ready(function() {

});

</script>
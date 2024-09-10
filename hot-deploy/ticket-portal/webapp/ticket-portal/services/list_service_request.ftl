<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row" style="width:100%">

<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

<#assign rightContent='<a title="Create" href="/ticket-portal/control/addservicerequest" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
    
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
    instanceid="SR_BASE_LIST_GEN" 
    autosizeallcol="true"
    debug="false"
    statusBar=true
    serversidepaginate=false
    refreshPrefBtnId="sr-refresh-pref-btn"
    savePrefBtnId="sr-save-pref-btn"
    clearFilterBtnId="sr-clear-filter-btn"
    exportBtnId="sr-export-btn"
    
    serversidepaginate=true
	statusBar=true
    />    
         
<script type="text/javascript" src="/ticket-portal-resource/js/ag-grid/services/find-service-request.js"></script>-->
			<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
			<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="listOfSRs"
			instanceId="SR_BASE_LIST_GEN"
			jsLoc="/ticket-portal-resource/js/ag-grid/services/find-service-request.js"
			headerLabel=uiLabelMap.ListOfSRs
			headerId="sr-grid-action-container"
			subFltrClearId="sr-sub-filter-clear-btn"
			savePrefBtnId="sr-save-pref-btn"
			clearFilterBtnId="sr-clear-filter-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			headerExtra=rightContent!
			exportBtnId="sr-list-export-btn"
			serversidepaginate=true
			statusBar=true
			/>
</div>
  	
</div>
	
<script>     
$(document).ready(function() {

});

</script>
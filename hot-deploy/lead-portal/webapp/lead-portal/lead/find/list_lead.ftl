<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<style>
	.ag-picker-field > .ag-wrapper > div{
		flex:  1 !important;
	}
</style>
<div class="row" style="width:100%" id="listof-lead">
	
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

<#assign rightContent='<a title="Create" href="/lead-portal/control/createLead" class="btn btn-primary btn-xs ml-2" target="_blank"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
   
<#-- <@AgGrid
	gridheadertitle=uiLabelMap.LeadsList
	gridheaderid="lead-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=false
	headerextra=rightContent
	refreshPrefBtnId="lead-refresh-pref-btn"
	savePrefBtnId="lead-save-pref-btn"
	clearFilterBtnId="lead-clear-filter-btn"
	exportBtnId="lead-export-btn"
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="LEADS" 
    autosizeallcol="true"
    debug="false"
    
    serversidepaginate=true
    statusBar=true
    />                   
         
<script type="text/javascript" src="/lead-portal-resource/js/ag-grid/find-lead.js"></script>-->
<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="lead"
			instanceId="LEADS"
			jsLoc="/lead-portal-resource/js/ag-grid/find-lead.js"
			headerLabel=uiLabelMap.LeadsList!
			headerId="lead-grid-action-container"
			clearFilterBtnId="lead-clear-filter-btn"
			subFltrClearId="lead-sub-filter-clear-btn"
			savePrefBtnId="lead-save-pref-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=true
			clearFilterBtn=true
			subFltrClearBtn = true
			exportBtn=true
			exportBtnId="lead-export-btn"
			headerExtra=rightContent!
			serversidepaginate=true
		    statusBar=true
			/>
</div>
  	
</div>
	
<script>     
$(document).ready(function() {

});

</script>
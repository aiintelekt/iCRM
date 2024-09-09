<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "rebates") /> 
<#assign rightContent='<span class="btn btn-xs btn-primary" title="Create"><i class="fa fa-plus" aria-hidden="true"></i><a href="/rebate-portal/control/createRebate?domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank" class="btn-primary"> Create </a></span>
	<span class="btn btn-xs btn-primary" title="Help"><i class="fa fa-question-circle" aria-hidden="true"></i> <a target="_blank" class="btn-primary" href="${helpUrl!\'#\'}">Help</a></span>
	' />   

<div class="row">
	
<div class="col-lg-12 col-md-12 col-sm-12">

	<#-- <@AgGrid
	gridheadertitle="Rebates"
	gridheaderid="rebate-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=false
	headerextra=rightContent
	refreshPrefBtnId="rebate-refresh-pref-btn"
	savePrefBtnId="rebate-save-pref-btn"
	clearFilterBtnId="rebate-clear-filter-btn"
	exportBtnId="rebate-export-btn"
	removeBtnId="rebate-remove-btn"
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="REBATE_LIST" 
    autosizeallcol="true"
    debug="false"
    />    
  	<script type="text/javascript" src="/common-portal-resource/js/ag-grid/rebate/find-rebate.js"></script>-->
  	<input type="hidden" id="userId" value="${userLogin.userLoginId}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

	 				<@fioGrid 
						id="rebateGrid"
						instanceId="REBATE_LIST"
						jsLoc="/common-portal-resource/js/ag-grid/rebate/find-rebate.js"
						headerLabel="Rebates"
						headerId="rebate-grid-action-container"
						headerBarClass="grid-header-no-bar"
						savePrefBtn=false
						clearFilterBtn=false
						subFltrClearBtn=false
						exportBtn=true
						subFltrClearId="rebate-sub-filter-btn"
						savePrefBtnId="rebate-save-pref-btn"
						clearFilterBtnId="rebate-clear-filter-btn"
						exportBtnId="rebate-export-btn"
						headerExtra=rightContent!
						/>
</div>
  	
</div>

<script>

</script>
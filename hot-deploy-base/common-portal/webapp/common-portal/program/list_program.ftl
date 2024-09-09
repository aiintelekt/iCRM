<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "rebates") /> 
<#assign rightContent='
	<button id="refresh-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-refresh" aria-hidden="true"></i></button>
	<span class="btn btn-xs btn-primary" title="Create"><i class="fa fa-plus" aria-hidden="true"></i><a href="/service-portal/control/createSr?customerId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank" class="btn-primary"> Create </a></span>
	<span class="btn btn-xs btn-primary" title="Help"><i class="fa fa-question-circle" aria-hidden="true"></i> <a target="_blank" class="btn-primary" href="${helpUrl!\'#\'}">Help</a></span>
	' />   

<div class="row">
	
<div class="col-lg-12 col-md-12 col-sm-12">

	<@AgGrid
	gridheadertitle="Programs"
	gridheaderid="program-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=false
	headerextra=rightContent
	refreshPrefBtnId="program-refresh-pref-btn"
	savePrefBtnId="program-save-pref-btn"
	clearFilterBtnId="program-clear-filter-btn"
	exportBtnId="program-export-btn"
	removeBtnId="program-remove-btn"
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="SR_BASE_LIST" 
    autosizeallcol="true"
    debug="false"
    />    
  	<script type="text/javascript" src="/common-portal-resource/js/ag-grid/program/find-program.js"></script>
  	
</div>
  	
</div>

<script>

</script>
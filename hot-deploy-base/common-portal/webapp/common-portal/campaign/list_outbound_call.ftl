<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "CallList") />
<#assign rightContent='
		<button id="refresh-outboundcall-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-refresh" aria-hidden="true"></i></button>
		' />
<div class="row">
<div class="col-lg-12 col-md-12 col-sm-12">
                 
<#-- <@AgGrid
	gridheadertitle="OutBound Call List"
	gridheaderid="outboundcall-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=false
	headerextra=rightContent!
	helpBtn=true
	helpUrl=helpUrl!
	refreshPrefBtnId="outboundcall-refresh-pref-btn"
	savePrefBtnId="outboundcall-save-pref-btn"
	clearFilterBtnId="outboundcall-clear-filter-btn"
	exportBtnId="outboundcall-export-btn"
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="COMM_OUT_BOUND_CALL_LIST" 
    autosizeallcol="true"
    debug="false"
    />    
<script type="text/javascript" src="/common-portal-resource/js/ag-grid/campaign/find-outbound-call.js"></script>-->
    <input type="hidden" id="userId" value="${userLogin.userLoginId}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

	 				<@fioGrid 
						id="outboundcall-Grid"
						instanceId="COMM_OUT_BOUND_CALL_LIST"
						jsLoc="/common-portal-resource/js/ag-grid/campaign/find-outbound-call.js"
						headerLabel="OutBound Call List"
						headerId="outboundcall-grid-action-container"
						savePrefBtn=false
						clearFilterBtn=false
						subFltrClearBtn=false
						headerBarClass="grid-header-no-bar"
						clearFilterBtnId="outboundcall-clear-pref-btn"
						subFltrClearId="outboundcall-sub-filter-clear-btn"
						savePrefBtnId="outboundcall-save-filter-btn"
						headerExtra=rightContent!
						exportBtn=true
						exportBtnId="outboundcall-export-btn"
						helpBtn=true
						helpUrl=helpUrl!
						/>
</div>
</div>

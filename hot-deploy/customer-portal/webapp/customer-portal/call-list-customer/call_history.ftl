<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<style>
	.clearfix {
	    margin-top: -10px;
	}
</style>
<form></form>
<div class="row">
<form id="callStatusHistoryForm"  method="post">
	<@inputHidden id="partyId" value="${partyId!}" />
	<@inputHidden id="externalLoginKey" value="${requestAttributes.externalLoginKey!}" />
</form>
<div class="col-lg-12 col-md-12 col-sm-12">
	<#assign rightContent='
		<button id="refresh-callStatus-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-refresh" aria-hidden="true"></i></button>
		' />
<#assign exportBtn=true>
<#if isPhoneCampaignEnabled?has_content && isPhoneCampaignEnabled?if_exists=="Y">
<#assign exportBtn=false>
</#if>
		<#-- <@AgGrid
			gridheadertitle=""
			gridheaderid="call-status-history-grid-action-container"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=exportBtn
			insertBtn=false
			updateBtn=false
			removeBtn=false
			headerextra=rightContent!
			savePrefBtnId="call-status-history-save-pref-btn"
			clearFilterBtnId="call-status-history-clear-filter-btn"
			exportBtnId="call-status-history-export-btn"
			userid="${userLogin.userLoginId}" 
			shownotifications="true" 
			instanceid="CALL_STATUS_HISTORY" 
			autosizeallcol="true"
			debug="false"
			serversidepaginate=false
			statusBar=false
			/>
		<script type="text/javascript" src="/campaign-resource/js/ag-grid/outBoundCallList/call-status-history.js"></script>-->
		 <input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
 	 	 <input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

							<@fioGrid 
								id="call-status-history-grid-header-title"
								instanceId="CALL_STATUS_HISTORY"
								jsLoc="/campaign-resource/js/ag-grid/outBoundCallList/call-status-history.js"
								headerLabel=""
								headerId="call-status-history-grid-action-container"
								savePrefBtnId="call-status-history-save-pref-btn"
								clearFilterBtnId="call-status-history-clear-filter-btn"
								subFltrClearId="call-status-history-sub-filter-clear-btn"
								headerBarClass="grid-header-no-bar"
								exportBtnId="call-status-history-list-export-btn"	
								savePrefBtn=true
								clearFilterBtn=true
								exportBtn=true
								subFltrClearBtn=true
								headerExtra=rightContent!
								/>
</div>

</div>
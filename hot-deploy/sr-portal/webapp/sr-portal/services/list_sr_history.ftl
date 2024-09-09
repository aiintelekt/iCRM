<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#assign srNumberUrlParam = requestParameters.srNumber!>
<div class="row">
	
	<form method="post" id="sr-history-search-form" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
		<input type="hidden" name="clientPortal" id="clientPortal" value="">
		<input type="hidden" name="custRequestId" id="custRequestId" value="${srNumberUrlParam!}" />
		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	</form>

<div class="col-lg-12 col-md-12 col-sm-12">
<@sectionFrameHeaderTab title="FSR History" tabId="ListSRHistory" helpBtnStyle="margin-top: -7px;"/> 
<div class="clearfix" style="margin-bottom: 10px"></div>
<#-- <@AgGrid
	gridheadertitle=""
	gridheaderid="sr-history-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=false
	refreshPrefBtnId="srhistory-refresh-pref-btn"
	savePrefBtnId="srhistory-save-pref-btn"
	clearFilterBtnId="srhistory-clear-filter-btn"
	subFltrClearId="srhistory-sub-filter-clear-btn"
	exportBtnId="srhistory-export-btn"
	
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="SR_HISTORY_LIST" 
    autosizeallcol="true"
    debug="false"
    />    
         
<script type="text/javascript" src="/sr-portal-resource/js/ag-grid/services/find-sr-history.js"></script>-->
<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="srHistoryGrid"
			instanceId="SR_HISTORY_LIST"
			jsLoc="/sr-portal-resource/js/ag-grid/services/find-sr-history.js"
			headerLabel=""
			headerId="sr-history-grid-action-container"
			clearFilterBtnId="srhistory-clear-filter-btn"
			exportBtnId="srhistory-list-export-btn"
			savePrefBtnId="srhistory-save-pref-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn=false
			/>
</div>
  	
</div>
	
<script>     
$(document).ready(function() {

});

</script>
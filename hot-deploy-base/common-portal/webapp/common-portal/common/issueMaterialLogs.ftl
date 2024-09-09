<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<style>
.ag-row .ag-cell {
  display: grid;
  //justify-content: center; /* align horizontal */
  align-items: center;
}
</style>
<div class="row">
  	<div class="col-lg-12 col-md-12 col-sm-12">
  	<#-- <@AgGrid
		gridheadertitle="Issued Materials"
		gridheaderid="time-entry-container"
		insertBtn=false
		updateBtn=false
		removeBtn=false
		headerextra=rightContent!
		refreshPrefBtnId="issue-material-refresh-pref-btn"
		savePrefBtnId="issue-material-save-pref-btn"
		clearFilterBtnId="issue-material-clear-filter-btn"
		exportBtnId="issue-material-export-btn"
	    userid="${userLogin.userLoginId}" 
	    shownotifications="true" 
	    instanceid="ISSUE_MATERIAL_LOGS" 
	    autosizeallcol="true"
	    debug="false"
	    /> 
  	<script type="text/javascript" src="/common-portal-resource/js/ag-grid/activity/issue-material-logs.js"></script>-->
  	<form id="issue-material-form">
	  	<input type="hidden" id="workEffortId" value="${workEffortId!}"/>
	</form>
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="issue-material-grid"
			instanceId="ISSUE_MATERIAL_LOGS"
			jsLoc="/common-portal-resource/js/ag-grid/activity/issue-material-logs.js"
			headerLabel="Issued Materials"
			headerId="issue-material-grid-action-container"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=false
			subFltrClearBtn = false
			savePrefBtnId ="issue-material-save-pref-btn"
			clearFilterBtnId ="issue-material-clear-filter-btn"
			subFltrClearId="issue-material-sub-filter-clear-btn"
			headerExtra=rightContent!
			/>
  	</div>
</div>
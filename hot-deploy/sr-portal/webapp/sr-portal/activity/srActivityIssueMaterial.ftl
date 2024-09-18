<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#-- 
<@sectionFrameHeaderTab title="Issue Material" tabId="issueMaterial"/>   
-->
<style>
.ag-row .ag-cell {
  display: grid;
  //justify-content: center; /* align horizontal */
  align-items: center;
}
</style>
<div>
	<#-- <@AgGrid
		gridheadertitle="Issued Materials"
		gridheaderid="issue-mat-container"
		insertBtn=false
		updateBtn=false
		removeBtn=false
		headerextra=rightContent
		refreshPrefBtnId="issue-material-refresh-pref-btn"
		savePrefBtnId="issue-material-save-pref-btn"
		clearFilterBtnId="issue-material-clear-filter-btn"
		subFltrClearId="issue-material-sub-filter-clear-btn"
		exportBtnId="issue-material-export-btn"
	    userid="${userLogin.userLoginId}" 
	    shownotifications="true" 
	    instanceid="SR_ACT_ISU_MATERIAL" 
	    autosizeallcol="true"
	    debug="false"
	    /> 
	<script type="text/javascript" src="/sr-portal-resource/js/ag-grid/activity/sr-activity-issue-material.js"></script>-->
	<form id="issueMaterialsForm">
	  	<input type="hidden" id="srNumber" value="${custRequestId!}"/>
	</form>
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="issue-material-grid"
			instanceId="SR_ACT_ISU_MATERIAL"
			jsLoc="/sr-portal-resource/js/ag-grid/activity/sr-activity-issue-material.js"
			headerLabel="Issued Materials"
			headerId="issue-material-grid-action-container"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn = false
			savePrefBtnId ="issue-material-save-pref-btn"
			clearFilterBtnId ="issue-material-clear-filter-btn"
			subFltrClearId="issue-material-sub-filter-clear-btn"
			exportBtnId="issue-material-list-export-btn"
			headerExtra=rightContent!
			/>
</div>
<#-- 
<div class="col-md-12 col-lg-12 col-sm-12">
	
</div>
-->
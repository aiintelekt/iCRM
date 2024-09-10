<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
	<div class="col-lg-12 col-md-12 col-sm-12">
	<#assign extra='<button title="View Event" class="btn btn-primary btn-xs ml-2" id="view_event"> <i class="fa fa-history" aria-hidden="true"></i> Event History </button>' />
	<#-- <@AgGrid
		gridheadertitle="Lead History"
		gridheaderid="lead-history-grid-action-container"
		savePrefBtn=true
		clearFilterBtn=true
		exportBtn=true
		insertBtn=false
		updateBtn=false
		removeBtn=false
		headerextra=extra!
		refreshPrefBtnId="lead-history-refresh-pref-btn"
		savePrefBtnId="lead-history-save-pref-btn"
		clearFilterBtnId="lead-history-clear-filter-btn"
		exportBtnId="lead-history-export-btn"
		userid="${userLogin.userLoginId}" 
		shownotifications="true" 
		instanceid="LEAD_HISTORY_LIST"
		autosizeallcol="true"
		debug="false"
		/>
	<script type="text/javascript" src="/lead-portal-resource/js/ag-grid/find-lead-history.js"></script>  -->
<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="lead-history"
			instanceId="LEAD_HISTORY_LIST"
			jsLoc="/lead-portal-resource/js/ag-grid/find-lead-history.js"
			headerLabel="Lead History"
			headerId="lead-history-grid-action-container"
			clearFilterBtnId="lead-history-clear-filter-btn"
			savePrefBtnId="lead-history-save-pref-btn"
			exportBtnId="lead-history-export-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			subFltrClearBtn=false
			exportBtn=true
			headerExtra=extra!
			/>
	</div>
</div>


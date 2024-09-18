<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<style></style>
<div class="" style="width:100%" id="listof-audit">
	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
		<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
		<@fioGrid 
			id="audit-log-grid"
			instanceId="AUDIT_LOG"
			jsLoc="/admin-portal-resource/js/ag-grid/audit-log/audit-log.js"
			headerLabel="${uiLabelMap.ListOfAuditLog!}"
			headerId="audit-grid-action-container"
			clearFilterBtnId="audit-clear-filter-btn"
			subFltrClearId="audit-sub-filter-clear-btn"
			savePrefBtnId="audit-save-pref-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=true
			clearFilterBtn=true
			subFltrClearBtn = true
			exportBtn=true
			exportBtnId="audit-export-btn"
			headerExtra=""
			serversidepaginate=false
			/>
	</div>
</div>
<script>
	$(document).ready(function() {
	
	});
</script>
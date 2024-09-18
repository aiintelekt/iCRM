<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row" style="width:100%">
	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		<#-- <@AgGrid
			gridheadertitle=uiLabelMap.emailTrackingList
			gridheaderid="${instanceId!}-grid-action-container"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			insertBtn=false
			updateBtn=false
			removeBtn=false
			headerextra="" 
			userid=""
			removeBtnId=""
			refreshPrefBtnId="email-track-refresh-pref-btn"
			savePrefBtnId="email-track-save-pref-btn"
			clearFilterBtnId="email-track-clear-filter-btn"
			subFltrClearId="email-track-sub-filter-clear-btn"
			exportBtnId="email-track-export-btn"
			shownotifications="true"
			instanceid="VIEW_EMAIL_TRACKING"
			autosizeallcol="true"
			debug="false"
			/>
		<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/email-track/find_email_tracking.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="emailTrackingList-grid"
			instanceId="VIEW_EMAIL_TRACKING"
			jsLoc="/admin-portal-resource/js/ag-grid/email-track/find_email_tracking.js"
			headerLabel=uiLabelMap.emailTrackingList!
			headerId="${instanceId!}-grid-action-container"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn = false
			savePrefBtnId="email-track-save-pref-btn"
			clearFilterBtnId="email-track-clear-filter-btn"
			subFltrClearId="email-track-sub-filter-clear-btn"
			exportBtnId="email-track-export-btn"
			/>
	</div>
</div>
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/activity/modal_window.ftl"/>
<#assign readOnlyPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermissionWoFullPerm(request, "READ_ONLY_PERM")?if_exists />
<#assign rightContent=''/>
<#assign removeBtn = true />
<#assign rightContent='<span class="btn btn-xs btn-primary" id="resavail-remove-btn" data-toggle="confirmation" title="" data-original-title="Are you sure to REMOVE ?"><i class="fa fa-remove" aria-hidden="true"></i> Remove</span>'/>
<#if readOnlyPermission!>
	<#assign removeBtn = false />
	<#assign rightContent=''/>
<#else>
	<@actScheduleModal 
	instanceId="act-schedule-modal"
	/>	
</#if>
<div class="row">
  	<div class="col-lg-12 col-md-12 col-sm-12">
  	<#-- <@AgGrid
		gridheadertitle="List of Schedule"
		gridheaderid="act-schedule-container"
		insertBtn=false
		updateBtn=false
		removeBtn=removeBtn!
		headerextra=rightContent!
		removeBtnId="resavail-remove-btn"
		refreshPrefBtnId="act-schedule-refresh-pref-btn"
		savePrefBtnId="act-schedule-save-pref-btn"
		clearFilterBtnId="act-schedule-clear-filter-btn"
		exportBtnId="act-schedule-export-btn"
	    userid="${userLogin.userLoginId}" 
	    shownotifications="true" 
	    instanceid="ACT_RES_AVAIL_LIST" 
	    autosizeallcol="true"
	    debug="false"
	    /> 
  	<script type="text/javascript" src="/common-portal-resource/js/ag-grid/activity/act-schedule.js"></script>-->
    <input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="act-schedule-grid"
			instanceId="ACT_RES_AVAIL_LIST"
			jsLoc="/common-portal-resource/js/ag-grid/activity/act-schedule.js"
			headerLabel="List of Schedule"
			headerId="act-schedule-grid-action-container"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn = false
			exportBtnId="act-schedule-list-export-btn"
			savePrefBtnId ="act-schedule-save-pref-btn"
			clearFilterBtnId ="act-schedule-clear-filter-btn"
			subFltrClearId="act-schedule-sub-filter-clear-btn"
			headerExtra=rightContent!
			/>
  	</div>
</div>
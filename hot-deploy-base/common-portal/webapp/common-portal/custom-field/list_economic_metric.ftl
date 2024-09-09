<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row">
<#assign rightContent ="">
	<#assign isEnableRemoveBtn = false>
	<#if partyStatusId?if_exists != "PARTY_DISABLED">
		<#assign isEnableRemoveBtn = true>
		<#assign rightContent = '<span class="btn btn-xs btn-primary" id="economic-metric-remove-btn" data-toggle="confirmation" title="" data-original-title="Are you sure to REMOVE ?"><i class="fa fa-remove" aria-hidden="true"></i> Remove</span>'/>
	</#if>
	<#assign exportBtn=true>
	<#if Static["org.fio.homeapps.util.DataUtil"].isPhoneCampaignEnabled(delegator)?if_exists=="Y">
	<#assign exportBtn=false>
	</#if>
	<div class="col-lg-12 col-md-12 col-sm-12">

	<#-- <@AgGrid
		gridheadertitle=""
		gridheaderid="economic-metric-grid-action-container"
		savePrefBtn=true
		clearFilterBtn=true
		exportBtn=exportBtn
		insertBtn=false
		updateBtn=false
		removeBtn=isEnableRemoveBtn
		headerextra=rightContent!
		refreshPrefBtnId="economic-metric-refresh-pref-btn"
		savePrefBtnId="economic-metric-save-pref-btn"
		clearFilterBtnId="economic-metric-clear-filter-btn"
		exportBtnId="economic-metric-export-btn"
		removeBtnId="economic-metric-remove-btn"
	    userid="${userLogin.userLoginId}" 
	    shownotifications="true" 
	    instanceid="ECONOMIC_METRICS" 
	    autosizeallcol="true"
	    debug="false"
	    /> 
  		<script type="text/javascript" src="/common-portal-resource/js/ag-grid/custom-field/find-economic-metric.js"></script>-->
<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="economic-metric-grid"
			instanceId="ECONOMIC_METRICS"
			jsLoc="/common-portal-resource/js/ag-grid/custom-field/find-economic-metric.js"
			headerLabel=""
			headerId="economic-metric-grid-action-container"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=exportBtn!
			subFltrClearBtn=false
			headerBarClass="grid-header-no-bar"
			savePrefBtnId="economic-metric-save-pref-btn"
			clearFilterBtnId="economic-metric-clear-filter-btn"
			subFltrClearId="economic-metric-sub-filter-btn"
			exportBtnId ="economic-metric-export-btn"
			headerExtra=rightContent!
			/>
  	 </div>
	
</div>

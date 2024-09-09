<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "listCampaigns") />
<#assign rightContent='
		<button id="refresh-sms-campaign-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-refresh" aria-hidden="true"></i></button>
		' />	
<form method="post" action="" id="findSmsMarketingCampaigns" name="findSmsMarketingCampaigns" novalidate="novalidate" data-toggle="validator">
	<input type="hidden" name="partyId" value="${partyId!}">
	<input type="hidden" name="isCampaignForParty" value="Y">
	<input type="hidden" name="campaignType" value="SMS">
</form>
<#assign exportBtn=true>
<#assign helpBtn=true>
<#if Static["org.fio.homeapps.util.DataUtil"].isPhoneCampaignEnabled(delegator)?if_exists=="Y">
<#assign exportBtn=false>
<#assign helpBtn=false>
</#if>	
<div class="col-lg-12 col-md-12 col-sm-12">
<#-- <@AgGrid
	gridheadertitle="Sms Campaigns"
	gridheaderid="sms-campaign-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=exportBtn
	insertBtn=false
	updateBtn=false
	removeBtn=false
	headerextra=rightContent!
	helpBtn=helpBtn
	helpUrl=helpUrl!
	refreshPrefBtnId="sms-campaign-refresh-pref-btn"
	savePrefBtnId="sms-campaign-save-pref-btn"
	clearFilterBtnId="sms-campaign-clear-filter-btn"
	exportBtnId="sms-campaign-export-btn"
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="CUST_SMS_CAMPAIGN_GRID" 
    autosizeallcol="true"
    debug="false"
    />    
	<script type="text/javascript" src="/common-portal-resource/js/ag-grid/campaign/find-sms-campaign.js"></script>-->
	
	<input type="hidden" id="userId" value="${userLogin.userLoginId}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

	 				<@fioGrid 
						id="smsCampaignsGrid"
						instanceId="CUST_SMS_CAMPAIGN_GRID"
						jsLoc="/common-portal-resource/js/ag-grid/campaign/find-sms-campaign.js"
						headerLabel="Sms Campaigns"
						headerId="sms-campaign-grid-action-container"
						savePrefBtn=false
						clearFilterBtn=false
						subFltrClearBtn=false
						exportBtn=exportBtn
						exportBtnId="sms-campaign-export-btn"
						headerBarClass="grid-header-no-bar"
						savePrefBtnId="sms-campaign-save-pref-btn"
						clearFilterBtnId="sms-campaign-clear-filter-btn"
						subFltrClearId="sms-campaign-sub-filter-btn"
						headerExtra=rightContent!
						helpBtn=helpBtn
						helpUrl=helpUrl!
						/>
</div>
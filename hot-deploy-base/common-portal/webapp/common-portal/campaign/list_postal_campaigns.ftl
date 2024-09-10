<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "listCampaigns") />
<#assign rightContent='
		<button id="refresh-postal-campaign-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-refresh" aria-hidden="true"></i></button>
		' />
<form method="post" action="" id="findPostalMarketingCampaigns" name="findPostalMarketingCampaigns" novalidate="novalidate" data-toggle="validator">
	<input type="hidden" name="partyId" value="${partyId!}">
	<input type="hidden" name="isCampaignForParty" value="Y">
	<input type="hidden" name="campaignType" value="POSTAL">
</form>
<#assign helpBtn=true>
<#assign exportBtn=true>
<#if Static["org.fio.homeapps.util.DataUtil"].isPhoneCampaignEnabled(delegator)?if_exists=="Y">
<#assign exportBtn=false>
<#assign helpBtn=false>
</#if>	
<div class="col-lg-12 col-md-12 col-sm-12">
<#-- <@AgGrid
	gridheadertitle="Postal Campaigns"
	gridheaderid="postal-campaign-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=exportBtn
	insertBtn=false
	updateBtn=false
	removeBtn=false
	headerextra=rightContent!
	helpBtn=helpBtn
	helpUrl=helpUrl!
	refreshPrefBtnId="postal-campaign-refresh-pref-btn"
	savePrefBtnId="postal-campaign-save-pref-btn"
	clearFilterBtnId="postal-campaign-clear-filter-btn"
	exportBtnId="postal-campaign-export-btn"
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="CUST_POSTAL_CAMPAIGN_GRID" 
    autosizeallcol="true"
    debug="false"
    />    
	<script type="text/javascript" src="/common-portal-resource/js/ag-grid/campaign/find-postal-campaign.js"></script>-->
	
	<input type="hidden" id="userId" value="${userLogin.userLoginId}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

	 				<@fioGrid 
						id="postalCampaignsGrid"
						instanceId="CUST_POSTAL_CAMPAIGN_GRID"
						jsLoc="/common-portal-resource/js/ag-grid/campaign/find-postal-campaign.js"
						headerLabel="Postal Campaigns"
						headerId="postal-campaign-grid-action-container"
						headerBarClass="grid-header-no-bar"
						savePrefBtn=false
						clearFilterBtn=false
						subFltrClearBtn=false
						exportBtn=exportBtn
						exportBtnId="postal-campaign-export-btn"
						savePrefBtnId="postal-campaign-save-pref-btn"
						clearFilterBtnId="postal-campaign-clear-filter-btn"
						subFltrClearId="postal-campaign-sub-filter-btn"
						headerExtra=rightContent!
						helpBtn=helpBtn
						helpUrl=helpUrl!
						/>
</div>
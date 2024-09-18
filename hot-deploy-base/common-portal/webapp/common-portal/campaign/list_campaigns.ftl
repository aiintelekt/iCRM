<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#if !showCampaignTabs?has_content || showCampaignTabs?if_exists=="N">
<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "listCampaigns") />
<#assign rightContent='
		<button id="refresh-campaign-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-refresh" aria-hidden="true"></i></button>
		' />
<div class="row">
<div class="col-lg-12 col-md-12 col-sm-12">
                 
<@AgGrid
	gridheadertitle="Campaigns"
	gridheaderid="campaign-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=false
	headerextra=rightContent!
	helpBtn=true
	helpUrl=helpUrl!
	refreshPrefBtnId="campaign-refresh-pref-btn"
	savePrefBtnId="campaign-save-pref-btn"
	clearFilterBtnId="campaign-clear-filter-btn"
	exportBtnId="campaign-export-btn"
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="PROF_EMAIL_CAMPAIGN_GRID" 
    autosizeallcol="true"
    debug="false"
    />    
<script type="text/javascript" src="/common-portal-resource/js/ag-grid/campaign/findCampaign.js"></script>

</div>
</div>

</#if>
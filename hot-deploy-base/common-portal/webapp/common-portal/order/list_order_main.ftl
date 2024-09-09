<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
	
<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "listOrders") />	
<#assign rightContent='
		<button id="refresh-order-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-refresh" aria-hidden="true"></i></button>
		' />
<#if isEnableIUCInt?has_content && isEnableIUCInt=="Y">
<#assign rightContent= rightContent + '<a title="Create" href="${iucUrl!}sales/control/createOrderMainScreen?domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&partyId=${mainAssocPartyId!}&token=${token!}" target="_blank" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
</#if>
<#assign exportBtn=true>
<#assign helpBtn=true>
<#if Static["org.fio.homeapps.util.DataUtil"].isPhoneCampaignEnabled(delegator)?if_exists=="Y">
<#assign exportBtn=false>
<#assign helpBtn=false>
</#if>			
<div class="row">
<div class="col-lg-12 col-md-12 col-sm-12">
	<@AgGrid
	gridheadertitle="Orders"
	gridheaderid="order-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=exportBtn
	insertBtn=false
	updateBtn=false
	removeBtn=false
	helpBtn=helpBtn
	helpUrl=helpUrl!
	headerextra=rightContent!
	refreshPrefBtnId="order-refresh-pref-btn"
	savePrefBtnId="order-save-pref-btn"
	subFltrClearId="order-sub-filter-clear-btn"
	clearFilterBtnId="order-clear-filter-btn"
	exportBtnId="order-export-btn"
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="LIST_ORDERS_MAIN" 
    autosizeallcol="true"
    debug="false"
    />    
  	<script type="text/javascript" src="/common-portal-resource/js/ag-grid/order/find-order-main.js"></script>
</div>
</div>	

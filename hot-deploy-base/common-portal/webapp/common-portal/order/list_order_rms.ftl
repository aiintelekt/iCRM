<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
	
<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "listOrders") />	
<#assign rightContent='
		<button id="refresh-order-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-refresh" aria-hidden="true"></i></button>
		' />
<#if isLoyaltyEnable?has_content && isLoyaltyEnable=="Y">
	<#assign rightContent= rightContent + '
		<button id="send-ereceipt-btn" type="button" class="btn btn-xs btn-primary m5" data-toggle="confirmation" title="Are you sure to send ereceipt?"><i class="fa fa-envelope" aria-hidden="true"></i> Send Ereceipt</button>
		' />
</#if>		
<#assign exportBtn=true>
<#assign helpBtn=true>
<#if Static["org.fio.homeapps.util.DataUtil"].isPhoneCampaignEnabled(delegator)?if_exists=="Y">
<#assign exportBtn=false>
<#assign helpBtn=false>
</#if>									
<div class="row">
<div class="col-lg-12 col-md-12 col-sm-12">

	<@fioGrid 
		instanceId="LIST_ORDERS"
		jsLoc="/common-portal-resource/js/ag-grid/order/find-order.js"
		headerLabel="Orders"
		headerId="order_list_tle"
		headerExtra=rightContent!
		subFltrClearBtn = true
		headerBarClass="grid-header-no-bar"
		savePrefBtnId="order-save-pref"
		clearFilterBtnId="order-clear-filter-btn"
		subFltrClearId="order-clear-sub-ftr"
		exportBtn=exportBtn!
		exportBtnId="order-export-btn"
		helpBtn=helpBtn!
		helpUrl=helpUrl!
		savePrefBtn=true
		clearFilterBtn=true
		subFltrClearBtn=true
		/> 
	<#-- 
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
    instanceid="LIST_ORDERS" 
    autosizeallcol="true"
    debug="false"
    />    
  	<script type="text/javascript" src="/common-portal-resource/js/ag-grid/order/find-order.js"></script>
  	-->
</div>
</div>

<div id="invoice-receipt-popup" class="modal fade">
  <div class="modal-dialog modal-lg" style="width:fit-content">
    <!-- Modal content-->
    <div class="modal-content modal-content-sig">
      <div class="modal-header modal-header-sig">
        <h3 class="modal-title modal-title-sig"></h3>
        <button type="button" role="button" class="close" data-dismiss="modal" style="color:#000000;">x</button>
      </div>
      <div class="modal-body" id="invoiceReceiptHtmlContent"></div>
      <div class="modal-footer"></div>
    </div>
  </div>
</div>
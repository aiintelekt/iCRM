<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "listCampaigns") />
<#assign rightContent='
		<button id="refresh-invoice-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-refresh" aria-hidden="true"></i></button>
		' />

<#if isLoyaltyEnable?has_content && isLoyaltyEnable=="Y">
	<#assign rightContent= rightContent + '
		<button id="send-ereceipt-btn" type="button" class="btn btn-xs btn-primary m5" data-toggle="confirmation" title="Are you sure to send ereceipt?"><i class="fa fa-envelope" aria-hidden="true"></i> Send Ereceipt</button>
		' />
</#if>

<div class="row">
	
	<div class="col-lg-12 col-md-12 col-sm-12">
	<#-- 
	<@AgGrid
		gridheadertitle="Invoices"
		gridheaderid="invoice-grid-action-container"
		savePrefBtn=true
		clearFilterBtn=true
		exportBtn=true
		insertBtn=false
		updateBtn=false
		removeBtn=false
		helpBtn=true
		helpUrl=helpUrl!
		headerextra=rightContent!
		refreshPrefBtnId="invoice-refresh-pref-btn"
		savePrefBtnId="invoice-save-pref-btn"
		clearFilterBtnId="invoice-clear-filter-btn"
		exportBtnId="invoice-export-btn"
	    userid="${userLogin.userLoginId}" 
	    shownotifications="true" 
	    instanceid="INVOICE_LIST" 
	    autosizeallcol="true"
	    debug="false"
	    />    
	<script type="text/javascript" src="/common-portal-resource/js/ag-grid/invoice/find-invoices.js"></script>
	-->
	<@fioGrid
		instanceId="INVOICE_LIST"
		jsLoc="/common-portal-resource/js/ag-grid/invoice/find-invoices.js"
		headerLabel="Invoices"
		headerId="invoice_tle"
		headerExtra=rightContent!
		headerBarClass="grid-header-no-bar"
		headerExtraLeft = extraLeft!
		savePrefBtnId="invoice-save-pref"
		clearFilterBtnId="invoice-clear-pref"
		subFltrClearId="invoice-clear-sub-ftr"
		serversidepaginate=false
		statusBar=false
		exportBtn=true
		exportBtnId="invoice-export-btn"
		savePrefBtn=false
		clearFilterBtn=false
		subFltrClearBtn=false
		/>
	
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
<script>     
$(document).ready(function() {

});

</script>
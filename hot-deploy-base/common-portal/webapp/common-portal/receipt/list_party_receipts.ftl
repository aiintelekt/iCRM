<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/receipt/modal_window.ftl"/>

<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "listCampaigns") />

<div class="row">
<div class="col-lg-12 col-md-12 col-sm-12">
                 
<#-- <@AgGrid
	gridheadertitle="Receipts"
	gridheaderid="receipts-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=false
	helpBtn=true
	helpUrl=helpUrl!
	refreshPrefBtnId="receipts-refresh-pref-btn"
	savePrefBtnId="receipts-save-pref-btn"
	clearFilterBtnId="receipts-clear-filter-btn"
	exportBtnId="receipts-export-btn"
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="RECEIPT_LIST" 
    autosizeallcol="true"
    debug="false"
    />    
<script type="text/javascript" src="/common-portal-resource/js/ag-grid/receipt/find-receipts.js"></script>-->
      <input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	  <input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

        					<@fioGrid 
								id="ReceiptsGrid"
								instanceId="RECEIPT_LIST"
								jsLoc="/common-portal-resource/js/ag-grid/receipt/find-receipts.js"
								headerLabel="Receipts"
								headerId="receipts-grid-action-container"
								savePrefBtnId="receipts-save-pref-btn"
								clearFilterBtnId="receipts-clear-filter-btn"
								headerBarClass="grid-header-no-bar"
								savePrefBtn=false
								clearFilterBtn=false
								exportBtn=true
								subFltrClearBtn=false
								subFltrClearId="receipts-sub-filter-clear-btn"
								exportBtnId="receipts-export-btn"
								helpBtn=true
								helpUrl=helpUrl!
								/>
</div>
</div>

<@showReceipt 
	instanceId="receipt-popup"
	/>
	
<script>
$(document).ready(function() {

});
</script>
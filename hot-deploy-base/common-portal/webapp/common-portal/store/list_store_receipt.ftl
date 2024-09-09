<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "listStoreReceipts") />
<#assign rightContent='
		<button id="refresh-storereceipt-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-refresh" aria-hidden="true"></i></button>
		<button id="add-storereceipt-btn" type="button" data-toggle="modal" data-target="#add-store-receipt-modal" class="btn btn-xs btn-primary m5"><i class="fa fa-plus" aria-hidden="true"></i> Add</button>
		<button id="remove-storereceipt-btn" type="button" data-toggle="confirmation" title="Are you sure to remove ?" class="btn btn-xs btn-primary m5"><i class="fa fa-times" aria-hidden="true"></i> Remove</button>
		' />
<div class="row">
	
<div class="col-lg-12 col-md-12 col-sm-12">
                 
<#-- <@AgGrid
	gridheadertitle="Store Receipts"
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
	refreshPrefBtnId="storereceipt-refresh-pref-btn"
	savePrefBtnId="storereceipt-save-pref-btn"
	clearFilterBtnId="storereceipt-clear-filter-btn"
	exportBtnId="storereceipt-export-btn"
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="STORE_RECEIPTS_LIST"
    autosizeallcol="true"
    debug="false"
    />   
     
<script type="text/javascript" src="/common-portal-resource/js/ag-grid/store/find-store-receipt.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

	 				<@fioGrid 
						id="storeReceiptGrid"
						instanceId="STORE_RECEIPTS_LIST"
						jsLoc="/common-portal-resource/js/ag-grid/store/find-store-receipt.js"
						headerLabel="Store Receipts"
						headerId="storeReceipt-grid-action-container"
						savePrefBtn=false
						clearFilterBtn=false
						subFltrClearBtn=false
						headerBarClass="grid-header-no-bar"
						clearFilterBtnId="storeReceipt-clear-pref-btn"
						subFltrClearId="storeReceipt-sub-filter-clear-btn"
						savePrefBtnId="storeReceipt-save-filter-btn"
						headerExtra=rightContent!
						exportBtn=true
						exportBtnId="storereceipt-export-btn"
						helpBtn=true
						helpUrl=helpUrl!
						/>
</div>
  	
</div>
	
<script>     
$(document).ready(function() {

});

</script>
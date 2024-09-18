<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row" style="width:100%" id="listof-lead">
	
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

<#assign rightContent='
	<a title="Create" href="/admin-portal/control/storeReceipt" target="_blank" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>
	'/>   
    
<#-- <@AgGrid
	gridheadertitle="Store Receipts List"
	gridheaderid="storeReceipt-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=false
	headerextra=rightContent
	refreshPrefBtnId="refresh-pref-btn"
	savePrefBtnId="save-pref-btn"
	clearFilterBtnId="clear-filter-btn"
	exportBtnId="export-btn"
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="STORE_RECEIPTS_LIST" 
    autosizeallcol="true"
    debug="false"
    
    serversidepaginate=false
    statusBar=false
    />    
         
<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/store/find-store-receipts.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

	 				<@fioGrid 
						id="storeReceiptGrid"
						instanceId="STORE_RECEIPTS_LIST"
						jsLoc="/admin-portal-resource/js/ag-grid/store/find-store-receipts.js"
						headerLabel="Store Receipts List"
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
						exportBtnId="export-btn"
						/>

</div>
  	
</div>

<script>
$(document).ready(function() {
	
});
</script>
<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row" style="width:100%" id="listof-lead">
	
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

<#assign rightContent='
	<button title="Refresh" class="btn btn-primary btn-xs ml-2" id="storeFailed-refresh"> <i class="fa fa-refresh" aria-hidden="true"></i> </button>
	'/>   
    
<#-- <@AgGrid
	gridheadertitle="Store Receipts Failed"
	gridheaderid="storeFailed-grid-action-container"
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
    instanceid="STORE_UPLOAD_FAILED" 
    autosizeallcol="true"
    debug="false"
    
    serversidepaginate=false
    statusBar=false
    />    
         
<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/store/store-upload-failed.js"></script>-->
<form id="searchForm"></form>
	<input type="hidden" id="userId" value="${userLogin.userLoginId}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

	 				<@fioGrid 
						id="storeUploadFailedGrid"
						instanceId="STORE_UPLOAD_FAILED"
						jsLoc="/admin-portal-resource/js/ag-grid/store/store-upload-failed.js"
						headerLabel="Store Receipts Failed"
						headerId="storeUploadFailed-grid-action-container"
						savePrefBtn=false
						clearFilterBtn=false
						subFltrClearBtn=false
						headerBarClass="grid-header-no-bar"
						clearFilterBtnId="storeUploadFailed-clear-pref-btn"
						subFltrClearId="storeUploadFailed-sub-filter-clear-btn"
						savePrefBtnId="storeUploadFailed-save-filter-btn"
						headerExtra=rightContent!
						exportBtn=true
						exportBtnId="storeUploadFailed-export-btn"
						/>
</div>
  	
</div>

<script>
$(document).ready(function() {
	
});
</script>
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row" style="width:100%" id="listof-lead">
	
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

<#assign rightContent='' />
<#if isGridExpEnabled?has_content && isGridExpEnabled=='Y'>
<#assign rightContent='
		<button id="grid-export-btn" type="button" class="btn btn-xs btn-primary m5" title="Export Complete Data" data-toggle="confirmation" title="Are you sure to export complete data ?"><i class="fa fa-file-excel-o"></i> Export</button>
		<button id="grid-exp-download-btn" type="button" class="btn btn-xs btn-primary m5" title="Download Complete Data"><i class="fa fa-file-excel-o"></i> Download</button>
		' />
</#if>	
<#assign rightContent=rightContent + '<a title="Create" href="/account-portal/control/createAccount" target="_blank" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
 
<#-- <@AgGrid
	gridheadertitle=uiLabelMap.ListOfAccounts
	gridheaderid="account-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=false
	headerextra=rightContent
	
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="ACCOUNTS" 
    autosizeallcol="true"
    debug="false"    
    serversidepaginate=true
	statusBar=true
    />   
         
<script type="text/javascript" src="/account-portal-resource/js/ag-grid/find-account.js"></script>-->
<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="accountGrid"
			instanceId="ACCOUNTS"
			jsLoc="/account-portal-resource/js/ag-grid/find-account.js"
			headerLabel="Accounts"
			headerId="account-grid-action-container"
			subFltrClearId="subFltrClearId-account"
			clearFilterBtnId="clearFilterBtnId-account"
			savePrefBtnId="savePrefBtnId-account"
			headerBarClass="grid-header-no-bar"
			exportBtnId="account-export-btn"
			subFltrClearBtn = true
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=false
			headerExtra=rightContent!
			serversidepaginate=true
			statusBar=true
			/>
</div>
  	
</div>
	
<script>     
$(document).ready(function() {
/*
var accountGridContainer = '<a title="Create" href="<@ofbizUrl>createAccount</@ofbizUrl>" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>'
;
$("#account-grid-action-container").append(accountGridContainer);
*/
});

</script>
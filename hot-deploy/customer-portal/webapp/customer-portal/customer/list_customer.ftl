<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row" style="width:100%" id="listof-lead">
	
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
<#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "CSTMR_PRFL_CC")?if_exists />
<#assign rightContent='' />
<#if isGridExpEnabled?has_content && isGridExpEnabled=='Y'>
<#assign rightContent='
		<button id="grid-export-btn" type="button" class="btn btn-xs btn-primary m5" title="Export Complete Data" data-toggle="confirmation" title="Are you sure to export complete data ?"><i class="fa fa-file-excel-o"></i> Export</button>
		<button id="grid-exp-download-btn" type="button" class="btn btn-xs btn-primary m5" title="Download Complete Data"><i class="fa fa-file-excel-o"></i> Download</button>
		' />
</#if>		
<#if hasPermission>
	<#assign rightContent = rightContent + '<a title="Create" href="/customer-portal/control/createCustomer" target="_blank" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
<#else>
	<#assign rightContent='' />
</#if>
<#assign exportBtn=true>
<#assign findCustomerGridInstanceId ="CUSTOMERS" />
<#if isPhoneCampaignEnabled?has_content && isPhoneCampaignEnabled?if_exists=="Y">
<#assign exportBtn=false>
<#assign findCustomerGridInstanceId ="CUSTOMERS_CALL_LIST" />
</#if>
<#-- <@AgGrid
	gridheadertitle="List Of Customers"
	gridheaderid="customer-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=false
	insertBtn=false
	updateBtn=false
	removeBtn=false
	headerextra=rightContent!
	
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid=findCustomerGridInstanceId!
    autosizeallcol="true"
    debug="false"
    
    serversidepaginate=true
	statusBar=true
    /> 
  
<script type="text/javascript" src="/contact-portal-resource/js/ag-grid/find-customer.js"></script>-->
 <input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
 <input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="customer-grid-header-title"
			instanceId=findCustomerGridInstanceId!
			jsLoc="/contact-portal-resource/js/ag-grid/find-customer.js"
			headerLabel="List Of Customers"
			headerId="customer-grid-action-container"
			savePrefBtnId="customer-save-pref-btn"
			clearFilterBtnId="customer-clear-filter-btn"
			subFltrClearId="customer-sub-filter-btn"
			headerBarClass="grid-header-no-bar"
			exportBtnId ="grid-export-btn"
			headerExtra=rightContent!
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=false
			serversidepaginate=true
			statusBar=true
			/>
</div>
  	
</div>
	
<script>     
$(document).ready(function() {

});
</script>
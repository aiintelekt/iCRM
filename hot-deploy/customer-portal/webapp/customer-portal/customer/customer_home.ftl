<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row">
<div id="main" role="main">
	<#--<@sectionFrameHeader title="Customer" />-->
${screens.render("component://customer-portal/widget/CustomerScreens.xml#KpiMetric")}

<div class="col-lg-12 col-md-12 col-sm-12">
<#assign exportBtn=true>
<#assign findCustomerGridInstanceId ="CUSTOMERS" />
<#if isPhoneCampaignEnabled?has_content && isPhoneCampaignEnabled?if_exists=="Y">
<#assign findCustomerGridInstanceId ="CUSTOMERS_CALL_LIST" />
<#assign exportBtn=false>
</#if>
<form method="post" id="searchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
	<input type="hidden" name="searchType" value="my-active-customer">
	<input type="hidden" name="loginPartyId" value="${userLogin.partyId?if_exists}">
	<input type="hidden" id="findCustomerGridInstanceId" value="${findCustomerGridInstanceId!}"/>
								<@inputHidden id="expFileTemplateId" value="OUTB_CL_EXP_TPL"/>
								<@inputHidden id="exportDataType" value="${findCustomerGridInstanceId!}"/>
</form>

<@inputHidden id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

</div>

<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

<#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "CSTMR_PRFL_CC")?if_exists />
<#if hasPermission>
	<#assign rightContent='<a title="Create" href="/customer-portal/control/createCustomer" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
<#else>
	<#assign rightContent='' />
</#if>
<#-- 
<#assign leftContent='<span id="filter-customer" class="text-dark btn" data-toggle="dropdown" title="Filter Customers" aria-expanded="false"> 
		<i class="fa fa-arrow-down fa-1" aria-hidden="true"></i>   
	</span>
	<div class="dropdown-menu" aria-labelledby="filter-customer">
		 <span class="dropdown-item filter-customer" style="cursor:pointer" data-searchType="my-active-customer" data-searchTypeLabel="My Customers"><i class="fa fa-list" aria-hidden="true"></i> My Customers</span>
		<span class="dropdown-item filter-customer" style="cursor:pointer" data-searchType="my-team-customer" data-searchTypeLabel="My Team Customers"><i class="fa fa-list" aria-hidden="true"></i> My Team Customers</span> 
	   
    </div>' />
    -->
   <#assign leftContent='' />
  <#--  <@AgGrid
	headertitleid="customer-grid-header-title"
	gridheadertitle='List of Customers'
	gridheaderid="customer-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=exportBtn
	insertBtn=false
	updateBtn=false
	removeBtn=false
	headerextra=rightContent!
	headerextraleft=leftContent
	
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid= findCustomerGridInstanceId!
    autosizeallcol="true"
    debug="false"
    serversidepaginate=false
	statusBar=false
    
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
			exportBtnId ="grid-export-btn"
			headerBarClass="grid-header-no-bar"
			headerExtra=rightContent!
			headerExtraLeft=leftContent!
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			serversidepaginate=false
			statusBar=false
			/>
</div>

</div>
</div>
<script>     
$(document).ready(function() {
/*
$(".filter-account").click(function(event) {
    event.preventDefault(); 
    
    $("#account-grid-header-title").html($(this).attr("data-searchTypeLabel"));
    //alert($(this).attr("data-searchTypeLabel"));
    $("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));
    
    loadScreenTemplateGrid();
});
*/	
});
</script>
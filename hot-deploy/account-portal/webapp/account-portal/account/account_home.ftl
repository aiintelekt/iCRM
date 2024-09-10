<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row">
<div id="main" role="main">
	<#--  <@sectionFrameHeader title="Account" />-->
${screens.render("component://account-portal/widget/account/AccountScreens.xml#KpiMetric")}

<div class="col-lg-12 col-md-12 col-sm-12">

<form method="post" id="searchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
	<@inputHidden id="filterBy" value="${requestParameters.filterBy!''}"/>
	<@inputHidden id="filterType" value="${requestParameters.filterType!}"/>
	<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
</form>

</div>

<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
<span id="dashboard-filter"></span>

<#assign rightContent='<a title="Create" href="/account-portal/control/createAccount" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
 
<#assign leftContent='<span id="filter-account" class="text-dark btn" data-toggle="dropdown" title="Filter Accounts" aria-expanded="false"> 
		<i class="fa fa-arrow-down fa-1" aria-hidden="true"></i>   
	</span>
	<div class="dropdown-menu" aria-labelledby="filter-account">
		 <span class="dropdown-item filter-account" style="cursor:pointer" data-searchType="my-active-account" data-searchTypeLabel="My Accounts"><i class="fa fa-list" aria-hidden="true"></i> My Accounts</span>
		<span class="dropdown-item filter-account" style="cursor:pointer" data-searchType="my-team-accounts" data-searchTypeLabel="My Team Accounts"><i class="fa fa-list" aria-hidden="true"></i> My Team Accounts</span> 
	   
    </div>' /> 
 <#assign leftContent= ""/>
       
<#-- <@AgGrid
	headertitleid="account-grid-header-title"
	gridheadertitle='Accounts'
	gridheaderid="account-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=false
	headerextra=rightContent
	headerextraleft=leftContent
	
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="ACCOUNTS" 
    autosizeallcol="true"
    debug="false"
    
    serversidepaginate=true
	statusBar=true
    />    
         
<script type="text/javascript" src="/account-portal-resource/js/ag-grid/find-account.js"></script>-->
<input type="hidden" id="userId" value="${userLogin.userLoginId}"/>
<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="account-grid-header-title"
			instanceId="ACCOUNTS"
			jsLoc="/account-portal-resource/js/ag-grid/find-account.js"
			headerLabel="Accounts"
			headerId="account-grid-action-container"
			subFltrClearBtn = true
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			headerBarClass="grid-header-no-bar"
			subFltrClearId="subFltrClearId-account"
			clearFilterBtnId="clearFilterBtnId-account"
			savePrefBtnId="savePrefBtnId-account"
			exportBtnId="account-export-btn"
			headerExtra=rightContent!
			headerExtraLeft=leftContent!
			serversidepaginate=true
			statusBar=true
			/>

</div>

</div>
</div>
	
<script>     
/*
$(document).ready(function() {

	$(".filter-account").click(function(event) {
	    event.preventDefault(); 
	    
	    $("#account-grid-header-title").html(DOMPurify.sanitize($(this).attr("data-searchTypeLabel")));
	    //alert($(this).attr("data-searchTypeLabel"));
	    $("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));
	    
	    loadScreenTemplateGrid();
	});

});
*/	
$(function(){
	$("#searchForm input[type=hidden][name='filterType']").val($('input[type=radio][name="filterType"]').val());
	
	$("#account-active").addClass( "selected-element-b");
	load_dynamic_data('account-active');
	
	loadAccountDashboardCount();
});
</script>
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<style>
	.ag-picker-field > .ag-wrapper > div{
		flex:  1 !important;
	}
</style>
<div class="row">
<div id="main" role="main">
<#-- <@sectionFrameHeader title="Lead" /> -->
${screens.render("component://lead-portal/widget/lead/LeadScreens.xml#KpiMetric")}

<div class="col-lg-12 col-md-12 col-sm-12">

<form method="post" id="searchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
	<@inputHidden id="filterBy" value="${requestParameters.filterBy!''}"/>
	<@inputHidden id="filterType" value="${requestParameters.filterType!}"/>
	<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	<@inputHidden id="statusId" value=""/>
</form>

</div>

<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
<span id="dashboard-filter"></span>

<#assign rightContent='<a title="Create" href="/lead-portal/control/createLead" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   

<#assign leftContent='<span id="filter-lead" class="text-dark btn" data-toggle="dropdown" title="Filter Leads" aria-expanded="false"> 
		<i class="fa fa-arrow-down fa-1" aria-hidden="true"></i>   
	</span>
	<div class="dropdown-menu" aria-labelledby="filter-lead">
		<span class="dropdown-item filter-lead" style="cursor:pointer" data-searchType="my-active-lead" data-searchTypeLabel="My Leads"><i class="fa fa-list fa-1" aria-hidden="true"></i> My Leads</span> 
	    <span class="dropdown-item filter-lead" style="cursor:pointer" data-searchType="my-team-leads" data-searchTypeLabel="My Team Leads"><i class="fa fa-list fa-1" aria-hidden="true"></i> My Team Leads</span>
    </div>' />    
<#-- <#assign leftContent= "" />
      
		<@AgGrid
			gridheadertitle=uiLabelMap.LeadsList
			gridheaderid="lead-grid-action-container"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			insertBtn=false
			updateBtn=false
			removeBtn=false
			headerextra=rightContent
			refreshPrefBtnId="lead-refresh-pref-btn"
			savePrefBtnId="lead-save-pref-btn"
			clearFilterBtnId="lead-clear-filter-btn"
			exportBtnId="lead-export-btn"
		    userid="${userLogin.userLoginId}" 
		    shownotifications="true" 
		    instanceid="LEADS" 
		    autosizeallcol="true"
		    debug="false"
		    
		    serversidepaginate=true
		    statusBar=true
		    />           
         
<script type="text/javascript" src="/lead-portal-resource/js/ag-grid/find-lead.js"></script>-->
<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="lead"
			instanceId="LEADS"
			jsLoc="/lead-portal-resource/js/ag-grid/find-lead.js"
			headerLabel=uiLabelMap.LeadsList!
			headerId="lead-grid-action-container"
			clearFilterBtnId="lead-clear-filter-btn"
			subFltrClearId="lead-sub-filter-clear-btn"
			savePrefBtnId="lead-save-pref-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			exportBtnId="lead-export-btn"
			headerExtra=rightContent!
			serversidepaginate=true
		    statusBar=true
			/>
</div>

</div>
</div>
	
<script>     
$(document).ready(function() {
	$("#searchForm input[type=hidden][name='filterType']").val($('input[type=radio][name="filterType"]').val());
	$("#lead_universe").addClass("selected-element-b");	
	loadLeadDashboardCount();
});
</script>
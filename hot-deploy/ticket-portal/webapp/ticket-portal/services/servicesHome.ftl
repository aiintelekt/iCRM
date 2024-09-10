<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
	<div id="main" role="main">
<#assign requestURI = ""/>
<#-- 
<#if request.getRequestURI().contains("main")>
<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "main") />
</#if>
-->
<#assign helpUrl = "" />
<#assign extraLeft = ""/>
		
		<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
			<#assign extraLeft = '<div class="form-check-inline">
				    <label for="filterTypeMy">
				    <input type="radio" id="filterTypeMy" name="filterType" value="my-sr" class="form-check-input" checked>
				    <span></span>
				    <span class="check"></span>
				    <span class="box"></span>
				 	My Requests</label>
				</div>
				<div class="form-check-inline">
				    <label for="filterTypeTeam">
				    <input type="radio" id="filterTypeTeam" name="filterType" value="my-teams-sr" class="form-check-input">
				    <span></span>
				    <span class="check"></span>
				    <span class="box"></span>
				    My Team\'s Requests</label>
				</div>
				'/>
			
			<@sectionFrameHeader title="${uiLabelMap.ServiceRequests!}"  extraLeft=extraLeft! isShowHelpUrl="Y" leftCol="col-lg-7 col-md-12 col-sm-12" rightCol="col-lg-5 col-md-12 col-sm-12" />
						
			<@AppBar 
				appBarId="MY_SR_DASH"
				appBarTypeId="DASHBOARD"
				id="appbar1"
				isEnableUserPreference=true
				animateEffect="bounce"
				/>
		</div>
		
		<div class="col-lg-12 col-md-12 col-sm-12">
			<#assign includeFsr = Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "IS_FSR_INCLUDE_IN_TICKET_PORTAL","Y")?if_exists>
			<form method="post" id="searchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
				<#-- <input type="hidden" name="searchType" value="my-open-srs"> -->
				<@inputHidden id="filterBy" value="${requestParameters.filterBy!}"/>
				<@inputHidden id="filterType" value="${requestParameters.filterType!}"/>
				<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
				 <#if includeFsr=="N">
				 <input type="hidden" name="custRequestDomainType" value="SERVICE">
				 </#if>
				
			</form>

		</div>
	
		<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
   			<span id="dashboard-filter"></span>
   			<#assign readOnlyPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermissionWoFullPerm(request, "READ_ONLY_PERM")?if_exists />
			<#assign rightContent='' />
			<#if readOnlyPermission!>
			<#else>
				<#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "SR_CSR")?if_exists />
				<#-- <#assign rightContent = '<span id="sr-mapit-btn" title="Map It" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-map" aria-hidden="true"></i> Map It </span> ' /> -->
				
				<#if hasPermission>
					<#assign rightContent=rightContent+'<a title="Create" href="/ticket-portal/control/addservicerequest" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
				<#else>
					<#assign rightContent='' />
				</#if>
			</#if>
			
			<#-- <@AgGrid
				gridheadertitle='List of FSRs'
				headertitleid="sr-grid-header-title"
				gridheaderid="sr-grid-action-container"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=true
				insertBtn=false
				updateBtn=false
				removeBtn=false
				headerextra=rightContent!
				
			    userid="${userLogin.userLoginId}" 
			    shownotifications="true" 
			    instanceid="SR_BASE_LIST_GEN" 
			    autosizeallcol="true"
			    debug="false"
			    statusBar=true
			    serversidepaginate=true
			    refreshPrefBtnId="sr-refresh-pref-btn"
			    subFltrClearId="sr-sub-filter-clear-btn"
			    savePrefBtnId="sr-save-pref-btn"
			    clearFilterBtnId="sr-clear-filter-btn"
			    exportBtnId="sr-export-btn"
			    />    
			         
			<script type="text/javascript" src="/ticket-portal-resource/js/ag-grid/services/find-sr-home.js"></script>-->
			<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
			<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="listOfSRs"
			instanceId="SR_BASE_LIST_GEN"
			jsLoc="/ticket-portal-resource/js/ag-grid/services/find-sr-home.js"
			headerLabel="List of FSRs"
			headerId="sr-grid-action-container"
			subFltrClearId="sr-sub-filter-clear-btn"
			savePrefBtnId="sr-save-pref-btn"
			clearFilterBtnId="sr-clear-filter-btn"
			headerBarClass="grid-header-no-bar"
			exportBtnId="sr-list-export-btn"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			headerExtra=rightContent!
			serversidepaginate=true
			statusBar=true
			/>
		</div>
	
   </div>
</div>

<script>
$(function(){
	$("#searchForm input[type=hidden][name='filterType']").val($('input[type=radio][name="filterType"]').val());
	
	loadSRDashboardCount();
	
	$("#sr-open").addClass( "selected-element-b");
	load_dynamic_data('sr-open');
	
});
</script>
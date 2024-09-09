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
			<#if isFsr?has_content && isFsr>
			<#assign extraLeft = extraLeft + '<div class="form-check-inline">
				    <label for="filterTypeBu">
				    <input type="radio" id="filterTypeBu" name="filterType" value="my-bu-sr" class="form-check-input">
				    <span></span>
				    <span class="check"></span>
				    <span class="box"></span>
				    Company Requests</label>
				</div>' />
			</#if>	
			<#if realCoordinator?exists && realCoordinator?has_content>
				<#assign extraLeft = extraLeft + '<div class="form-check-inline">
					    <label for="filterTypeBkpReq">
					    <input type="radio" id="filterTypeBkpReq" name="filterType" value="my-backup-sr" class="form-check-input">
					    <span></span>
					    <span class="check"></span>
					    <span class="box"></span>
					    Backup Requests</label>
					</div>' />
			</#if>	
						
			<#assign locationdd = '' />
			<#assign locationdd = locationdd+'<div class="ui dropdown" id="location-dd">
    		<div class="text title">Select Location</div>
	    		<i class="dropdown icon"></i>' />
	    		<#if storeGroupList?has_content>
	    		<#assign locationdd = locationdd+'<div class="menu">' />
	    		<#list storeGroupList as storeGroup>
	    			<#assign locations = locList.get(storeGroup.productStoreGroupId)/>
	      			<#assign locationdd = locationdd+'<div class="header">${storeGroup.productStoreGroupName!}</div>' />
	      			<#list locations as loc>
	      			<#assign locationdd = locationdd+'<div class="item location-filter" data-storeId="${loc.productStoreId!}">${loc.storeName!}</div>' />
	      			</#list>
				    <#assign locationdd = locationdd+'<div class="divider"></div>' />
				</#list>    
	    		<#assign locationdd = locationdd+'</div>' />
	    		</#if>
  			<#assign locationdd = locationdd+'</div>' />
			
			<#-- 
			<#assign locationdd = '' />
			<#if storeGroupList?has_content>
			<#assign locationdd = locationdd+'<div class="ui dropdown button">
			  <span class="text">Select Location</span>
			  <i class="dropdown icon"></i>
			  <div class="menu">' />
			  	
			  	<#list storeGroupList as storeGroup>
			  	<#assign locations = locList.get(storeGroup.productStoreGroupId)/>
			    <#assign locationdd = locationdd+'<div class="item">' />
			      <#if locations?has_content><#assign locationdd = locationdd+'<i class="dropdown icon"></i>' /></#if>
			      <#assign locationdd = locationdd+'<span class="text">${storeGroup.productStoreGroupName!}</span>' />
			      <#if locations?has_content><#assign locationdd = locationdd+'<div class="menu">' /></#if>
			      	<#list locations as loc>
			        <#assign locationdd = locationdd+'<div class="item location-filter" data-storeId="${loc.productStoreId!}">${loc.storeName!}</div>' />
			        </#list>
			      <#if locations?has_content><#assign locationdd = locationdd+'</div>' /></#if>
			    <#assign locationdd = locationdd+'</div>' />
			    </#list>
			    
			  <#assign locationdd = locationdd+'</div>
			</div>' />
			</#if>
			 -->
				
			<#-- 	
			<#assign locationdd = '<select class="ui dropdown" style="margin-bottom: 5px;" id="srLocation" name="srLocation"><option value="">Select location</option>' />
			<#if locationList?exists && locationList?has_content>
				<#list locationList as location>
					<#assign locationdd = locationdd+'<option value="${location.productStoreId!}">${location.storeName!}</option>' />
				</#list>
			</#if>
			<#assign locationdd = locationdd+"</select>">
			 -->
			
			<#assign extraLeft = extraLeft + locationdd />
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

			<form method="post" id="searchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
				<#-- <input type="hidden" name="searchType" value="my-open-srs"> -->
				<@inputHidden id="filterBy" value="${requestParameters.filterBy!}"/>
				<@inputHidden id="filterType" value="${requestParameters.filterType!}"/>
				<@inputHidden id="srLocation" value="${requestParameters.srLocation!}"/>
				<@inputHidden id="realCoordinator" value="${realCoordinator!}"/>
				<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
			</form>

		</div>
	
		<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
   			<span id="dashboard-filter"></span>
   			<#assign readOnlyPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermissionWoFullPerm(request, "READ_ONLY_PERM")?if_exists />
			<#assign rightContent='' />
			<#if readOnlyPermission!>
			<#else>
				<#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "SR_CSR")?if_exists />
				<#assign rightContent = '<span id="sr-mapit-btn" title="Map It" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-map" aria-hidden="true"></i> Map It </span> ' />
				
				<#if hasPermission>
					<#assign rightContent=rightContent+'<a title="Create" href="/sr-portal/control/addservicerequest" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
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
			    instanceid="SERVICE_REQUEST_LIST" 
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
			         
			<script type="text/javascript" src="/sr-portal-resource/js/ag-grid/services/find-sr-home.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="sr-grid-header-title"
			instanceId="SERVICE_REQUEST_LIST"
			jsLoc="/sr-portal-resource/js/ag-grid/services/find-sr-home.js"
			headerLabel="List of FSRs"
			headerId="sr-grid-action-container"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			subFltrClearBtn=true
			savePrefBtnId ="sr-save-pref-btn"
			clearFilterBtnId ="sr-clear-filter-btn"
			subFltrClearId="sr-sub-filter-clear-btn"
			exportBtnId="sr-list-export-btn"
		    serversidepaginate=true
    		statusBar=true
    		headerExtra=rightContent!
			/>
		</div>
	
   </div>
</div>

<script>
$(function(){
	$("#searchForm input[type=hidden][name='filterType']").val($('input[type=radio][name="filterType"]').val());
	
	$("#searchForm input[type=hidden][name='srLocation']").val($("#srLocation option:selected").val());
	loadSRDashboardCount();
	
	$("#sr-open").addClass( "selected-element-b");
	load_dynamic_data('sr-open');
	
});
</script>
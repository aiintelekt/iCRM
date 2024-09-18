<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main">
        <#--<@sectionFrameHeader title="${uiLabelMap.Activity!}" />-->
		<#-- ${screens.render("component://activity-portal/widget/activity/ActivityScreens.xml#KpiMetric")} -->
		<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		<#assign hideDashPageFilter = Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "HIDE_ACT_DASH_FILTER")?if_exists>
		<#if !hideDashPageFilter?has_content || (hideDashPageFilter?has_content && hideDashPageFilter !="Y")>
			<#assign extraLeft = '<div class="form-check-inline">
				    <label for="filterTypeMy">
				    <input type="radio" id="filterTypeMy" name="filterType" value="my-activities" class="form-check-input" checked>
				    <span></span>
				    <span class="check"></span>
				    <span class="box"></span>
				 	My Activities</label>
				</div>
				<div class="form-check-inline">
				    <label for="filterTypeTeam">
				    <input type="radio" id="filterTypeTeam" name="filterType" value="my-teams-activities" class="form-check-input">
				    <span></span>
				    <span class="check"></span>
				    <span class="box"></span>
				    My Team\'s Activities</label>
				</div>
				'/>
			<#if isFsr?has_content && isFsr>
			<#assign extraLeft = extraLeft + '<div class="form-check-inline">
				    <label for="filterTypeBu">
				    <input type="radio" id="filterTypeBu" name="filterType" value="my-bu-activities" class="form-check-input">
				    <span></span>
				    <span class="check"></span>
				    <span class="box"></span>
				    Company Activities</label>
				</div>
				' />
			</#if>
			
			<#if realCoordinator?exists && realCoordinator?has_content>
				<#assign extraLeft = extraLeft + '<div class="form-check-inline">
					    <label for="filterTypeBkpAct">
					    <input type="radio" id="filterTypeBkpAct" name="filterType" value="my-backup-activities" class="form-check-input">
					    <span></span>
					    <span class="check"></span>
					    <span class="box"></span>
					    Backup Activities</label>
					</div>' />
			</#if>
			
			<#assign locationdd = '<select class="ui dropdown" style="margin-bottom: 5px;" id="location" name="location"><option value="">Select location</option>' />
			<#if locationList?exists && locationList?has_content>
				<#list locationList as location>
					<#assign locationdd = locationdd+'<option value="${location.productStoreId!}">${location.storeName!}</option>' />
				</#list>
			</#if>
			<#assign locationdd = locationdd+"</select>">
			<#assign extraLeft = extraLeft+locationdd />
			</#if>
			<@sectionFrameHeader title="Activity Home"  extraLeft=extraLeft! extra=helpUrl?if_exists leftCol="col-lg-7 col-md-12 col-sm-12" rightCol="col-lg-5 col-md-12 col-sm-12"/>
			
			
			<@AppBar 
				appBarId="ACTIVITY_DASH"
				appBarTypeId="DASHBOARD"
				id="appbar1"
				isEnableUserPreference=true
				animateEffect="bounce"
				/>
		</div>
		<div class="col-lg-12 col-md-12 col-sm-12">

			<form method="post" id="searchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
				<@inputHidden id="filterBy" value="${requestParameters.filterBy!}"/>
				<@inputHidden id="filterType" value="${requestParameters.filterType!}"/>
				<@inputHidden id="defaultActType" value="${defaultActType!}" />
				<@inputHidden id="location" value="${requestParameters.location!}"/>
				<@inputHidden id="realCoordinator" value="${realCoordinator!}"/>
				<input type="hidden" name="isSrActivityOnly" value="N">
				<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
				<input type="hidden" name="isChecklistActivity" value="N">
			</form>

		</div>
		
        <div class="col-lg-12 col-md-12 col-sm-12">
            <form method="post" id="activity-search-form" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
                <input type="hidden" name="searchType" value="my-open-activity">
                <input type="hidden" name="owner" value="${loggedUserPartyId!}">
                <#if activityTypeList?has_content>
                <#list activityTypeList.entrySet() as entry>  
                <input type="hidden" name="defaultActivityTypes" value="${entry.key!}">
                </#list>
                </#if>
                
            </form>
        </div>
        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
        	<span id="dashboard-filter"></span>
            <#assign rightContent='<span id="create-activity" title="Create" class="btn btn-primary btn-xs ml-2" data-toggle="dropdown" aria-expanded="false"> <i class="fa fa-plus" aria-hidden="true"></i> Create </span>
            <div class="dropdown-menu" aria-labelledby="create-activity">
                <a class="dropdown-item" href="/activity-portal/control/createTask?externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-clipboard" aria-hidden="true"></i> Task</a>
                <a class="dropdown-item" href="/activity-portal/control/createApnt?externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-calendar" aria-hidden="true"></i> Appointment</a>
            </div>
            ' />  
          <#-- <@AgGrid
	            headertitleid="activity-grid-header-title"
	            gridheadertitle='List of Activities'
	            gridheaderid="activity-grid-action-container"
	            savePrefBtn=true
	            clearFilterBtn=true
	            exportBtn=true
	            insertBtn=false
	            updateBtn=false
	            removeBtn=false
	            headerextra=""
	            headerextraleft=""
	            userid="${userLogin.userLoginId}" 
	            shownotifications="true" 
	            instanceid="ACTIVITY_GEN_LIST" 
	            autosizeallcol="true"
	            debug="false"
	            />    
            <script type="text/javascript" src="/activity-portal-resource/js/ag-grid/dash-activity-gen.js"></script>-->
            <input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
			<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="activity-grid-header-title"
			instanceId="ACTIVITY_GEN_LIST"
			jsLoc="/activity-portal-resource/js/ag-grid/dash-activity-gen.js"
			headerLabel=uiLabelMap.ListOfActivitys
			headerId="activity-grid-action-container"
			savePrefBtnId="save-pref-btn"
			clearFilterBtnId="clear-filter-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			subFltrClearBtn = true
			exportBtnId="activity-gen-export-btn"
			subFltrClearId="activity-sub-filter-clear-btn"
			/>
        </div>
    </div>
</div>
<script>     
$(function(){
	$("#searchForm input[type=hidden][name='filterType']").val($('input[type=radio][name="filterType"]').val());
	$("#searchForm input[type=hidden][name='location']").val($("#location option:selected").val());
	loadActivityDashboardCount();
	
	$("#overdue").addClass( "selected-element-b");
	load_dynamic_data('overdue');
	
});
</script>
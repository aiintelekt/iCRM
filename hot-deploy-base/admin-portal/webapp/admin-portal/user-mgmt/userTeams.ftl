<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl" />
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl" />

<div>
<form id="userTeamForm">
    <input type="hidden" name="partyId" id="partyId" value="${userData.partyId!}"/>
</form>
</div>
<#assign teamExtra='<a href="" data-toggle="modal" data-target="#addTeamToUser" class="btn btn-primary btn-xs ml-0"> <i class="fa fa-plus fa-1"></i>${uiLabelMap.AddTeam!}</a> <a href="" data-toggle="modal" data-target="#addTeamToUser" data-is-native="Y" class="btn btn-primary btn-xs ml-0"> <i class="fa fa-plus fa-1"></i>${uiLabelMap.AddNativeTeam!}</a>' />
<#assign teamExtra=	teamExtra+'<span class="btn btn-xs btn-primary" id="user-team-remove-btn" data-toggle="confirmation" title="" data-original-title="Are you sure to REMOVE ?"><i class="fa fa-remove" aria-hidden="true"></i> Remove</span>'/>
	 <#-- <@AgGrid
		userid="${userLogin.userLoginId}"
		instanceid="USER_TEAM"
		shownotifications="true"
		autosizeallcol="true"
		debug="false"
		insertBtn=false
		updateBtn=false
		removeBtn=true
		gridheadertitle=uiLabelMap.ListofTeam!
		gridheaderid="listOfUsersBtns"
		headerextra=teamExtra!
		statusBar=true
		serversidepaginate=false
		refreshPrefBtnId="user-team-ref-pref"
		removeBtnId="user-team-remove-btn"
		savePrefBtnId="user-team-save-pref"
		clearFilterBtnId="user-team-clear-filter"
		exportBtnId="user-team-export"
		/>
<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/user-mgmt/view-user-team.js"></script>-->
    <input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="listOfUsersBtns-Grid"
			instanceId="USER_TEAM"
			jsLoc="/admin-portal-resource/js/ag-grid/user-mgmt/view-user-team.js"
			headerLabel=uiLabelMap.ListofTeam!
			headerId="listOfUsersBtns-grid-action-container"
			subFltrClearId="user-team-sub-filter-clear-btn"
			savePrefBtnId="user-team-save-pref-btn"
			clearFilterBtnId="user-team-clear-filter-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn=false
			exportBtnId="user-team-list-export-btn"
			headerExtra=teamExtra!
			/>
<div class="clearfix"></div>
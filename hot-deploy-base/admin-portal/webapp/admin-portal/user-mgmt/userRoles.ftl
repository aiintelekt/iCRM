<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl" />
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl" />
<div>
<form id="securityRoleForm">
    <input type="hidden" name="partyId" id="partyId" value="${userData.partyId!}"/>
</form>
</div>
<div>
	<#assign extra='<a href="" data-toggle="modal" data-target="#addrole" class="btn btn-primary btn-xs ml-0"> <i class="fa fa-plus fa-1"></i>${uiLabelMap.Add!}</a>' />
	<#assign extra=extra+'<span class="btn btn-xs btn-primary" id="remove-btn" data-toggle="confirmation" title="" data-original-title="Are you sure to REMOVE ?" aria-describedby="confirmation605880"><i class="fa fa-remove" aria-hidden="true"></i> Remove</span>'/>
	<#--<@AgGrid 
		userid="${userLogin.userLoginId}" 
		instanceid="USER_ROLE_LIST"
		shownotifications="true"
		autosizeallcol="true"
		debug="false"
		insertBtn=false
		updateBtn=false
		removeBtn=true
		gridheadertitle=uiLabelMap.SecurityRole!
		gridheaderid="securityRoleBtns"
		statusBar=true
		serversidepaginate=false
		headerextra=extra!
		/>
	<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/user-mgmt/view-user-role.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="securityRoleBtns-Grid"
			instanceId="USER_ROLE_LIST"
			jsLoc="/admin-portal-resource/js/ag-grid/user-mgmt/view-user-role.js"
			headerLabel=uiLabelMap.SecurityRole!
			headerId="securityRoleBtns-grid-action-container"
			subFltrClearId="security-role-sub-filter-clear-btn"
			savePrefBtnId="security-role-save-pref-btn"
			clearFilterBtnId="security-role-clear-filter-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			subFltrClearBtn=true
			exportBtnId="security-role-list-export-btn"
			headerExtra=extra!
			/>
</div>
<div>
	<#assign extra='<a href="" data-toggle="modal" data-target="#addRolesToUser" class="btn btn-primary btn-xs ml-0"> <i class="fa fa-plus fa-1"></i>${uiLabelMap.Add!}</a>' />
	<#assign extra = extra+'<span class="btn btn-xs btn-primary" id="user-roles-list-remove-btn" data-toggle="confirmation" title="" data-original-title="Are you sure to REMOVE ?"><i class="fa fa-remove" aria-hidden="true"></i> Remove</span>'/>
	<#-- <@AgGrid
		userid="${userLogin.userLoginId}" 
		instanceid="USER_ROLES_LIST"
		shownotifications="true"
		autosizeallcol="true"
		debug="false"
		insertBtn=false
		updateBtn=false
		removeBtn=true
		gridheadertitle=uiLabelMap.ListOfUsersAndRoles!
		gridheaderid="listOfUsersBtns"
		statusBar=true
		serversidepaginate=false
		headerextra=extra!
		removeBtnId="user-roles-list-remove-btn"
		refreshPrefBtnId="user-roles-list-ref-pref"
		savePrefBtnId="user-roles-list-save-pref"
		clearFilterBtnId="user-roles-list-clear-filter"
		exportBtnId="user-roles-list-export"
		/>
	<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/user-mgmt/view-user-roles-list.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="ListOfUsersAndRoles-Grid"
			instanceId="USER_ROLES_LIST"
			jsLoc="/admin-portal-resource/js/ag-grid/user-mgmt/view-user-roles-list.js"
			headerLabel=uiLabelMap.SecurityRole!
			headerId="ListOfUsersAndRoles-grid-action-container"
			subFltrClearId="user-roles-sub-filter-clear-btn"
			savePrefBtnId="user-roles-save-pref-btn"
			clearFilterBtnId="user-roles-clear-filter-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn=false
			exportBtnId="user-roles-list-export-btn"
			headerExtra=extra!
			/>
	<div class="clearfix"></div>
</div>
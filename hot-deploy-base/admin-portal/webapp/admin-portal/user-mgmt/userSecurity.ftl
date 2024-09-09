<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl" />
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl" />
<form id="customSecurityAndDerivedForm">
	<@inputHidden id="partyId" value="${userData.partyId!}"/>
    <@inputHidden id="userLoginId" value="${requestParameters.userLoginId!}" />
</form>
<div id="derived">
	<#-- <@AgGrid
		userid="${userLogin.userLoginId}"
		instanceid="DERIVED_SECURITY"
		shownotifications="true"
		autosizeallcol="true"
		debug="false"
		insertBtn=false
		updateBtn=false
		removeBtn=false
		gridheadertitle=uiLabelMap.DerivedSecurityGroup!
		gridheaderid="listOfUsersBtns"
		statusBar=true
		serversidepaginate=false
		refreshPrefBtnId="derived-security-ref-pref"
		savePrefBtnId="derived-security-save-pref"
		clearFilterBtnId="derived-security-clear-filter"
		exportBtnId="derived-security-export"
		/>
	<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/user-mgmt/view-user-security.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="DerivedSecurityGroup-Grid"
			instanceId="DERIVED_SECURITY"
			jsLoc="/admin-portal-resource/js/ag-grid/user-mgmt/view-user-security.js"
			headerLabel=uiLabelMap.DerivedSecurityGroup!
			headerId="DerivedSecurityGroup-grid-action-container"
			subFltrClearId="derived-security-sub-filter-clear-btn"
            savePrefBtnId="derived-security-save-pref"
            clearFilterBtnId="derived-security-clear-filter"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn=false
			exportBtnId="derived-security-list-export-btn"
			/>
</div>
<div id="custom">
	<#assign addSecurityGroup='<a href="" data-toggle="modal" data-target="#addsecurity" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus fa-1"></i> Add</a>' />
	<#-- <@AgGrid
		userid="${userLogin.userLoginId}"
		instanceid="CUSTOM_SECURITY"
		shownotifications="true"
		autosizeallcol="true"
		debug="false"
		insertBtn=false
		updateBtn=false
		removeBtn=false
		gridheadertitle=uiLabelMap.CustomSecurityGroup!
		gridheaderid="listCustomSecurityBtns"
		statusBar=true
		serversidepaginate=false
		headerextra=addSecurityGroup!
		refreshPrefBtnId="custom-security-ref-pref"
		savePrefBtnId="custom-security-save-pref"
		clearFilterBtnId="custom-security-clear-filter"
		exportBtnId="custom-security-export"
		/>
	<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/user-mgmt/view-user-custom-security.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="listCustomSecurity-Grid"
			instanceId="CUSTOM_SECURITY"
			jsLoc="/admin-portal-resource/js/ag-grid/user-mgmt/view-user-custom-security.js"
			headerLabel=uiLabelMap.CustomSecurityGroup!
			headerId="listCustomSecurity-grid-action-container"
			subFltrClearId="custom-security-sub-filter-clear-btn"
            savePrefBtnId="custom-security-save-pref"
            clearFilterBtnId="custom-security-clear-filter"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn=false
			headerExtra=addSecurityGroup!
			exportBtnId="custom-security-list-export-btn"
			/>
	<div class="clearfix"></div>
</div>
<#include "component://ab-ag-grid-support/webapp/ab-ag-grid-support/lib/ag_grid_header.ftl" />
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl" />
<body>
	<script>
		function clearFields() {
			document.getElementById("createUser").reset();
		}
	</script>
	<div class="row">
		<div id="main" role="main">
			<#assign extra='
	<a href="findTabShortcuts" class="btn btn-xs btn-primary">
	<i class="fa fa-chevron-circle-right" aria-hidden="true"></i> Back </a>' />
	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel" style="padding-bottom:7px;">
		<@sectionFrameHeader title="Update Sub Menus" extra=extra?if_exists />
		<div class="row pt-2">
			<div class="col-md-12 col-lg-6 col-sm-12 ">
				<#assign subMenu=delegator.findOne("OfbizTabSecurityShortcut", {"tabId", parameters.tabId,"pageId",parameters.pageId,"permissionId",parameters.permissionId,"componentId",parameters.componentId,"shortcutId",parameters.shortcutId}, false)?if_exists />
				<@inputRow 
					id="tabId" 
					name="tabId" 
					label="Tab Id" 
					readonly=true 
					value="${subMenu?if_exists.tabId?if_exists}" />
				<@inputRow 
					id="tabName" 
					name="tabName" 
					label="Tab Name" 
					readonly=true 
					value="${subMenu?if_exists.uiLabels?if_exists}" />
				<form id="createUser" name="updateUser" action="updateSubMenus" method="POST">
					<@inputHidden id="tabId" name="tabId" value="${subMenu?if_exists.tabId?if_exists}" />
					<@inputHidden id="tabName" name="tabName" value="${subMenu?if_exists.uiLabels?if_exists}" />
					<@inputHidden id="componentId" name="componentId" value="${subMenu?if_exists.componentId?if_exists}" />
					<@inputHidden id="pageId" name="pageId" value="${subMenu?if_exists.pageId?if_exists}" />
					<@inputHidden id="permissionId" name="permissionId" value="${subMenu?if_exists.permissionId?if_exists}" />
					<@inputHidden id="shortcutId" name="shortcutId" value="${subMenu?if_exists.shortcutId?if_exists}" />
					<@inputRow 
						id="requestUri" 
						name="requestUri" 
						label="Request Uri" 
						required=false 
						placeholder="Request Uri" 
						value="${subMenu?if_exists.requestUri?if_exists}" />
					<#assign isDisabled=delegator.findAll("OfbizTabSecurityShortcut",false)?if_exists />
					<#assign IsDisabled=Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(isDisabled, "isDisabled" ,"isDisabled")?if_exists />
					<@dropdownCell 
						id="isDisabled" 
						name="isDisabled" 
						label="Is Disabled" 
						allowEmpty=true 
						placeholder="IsDisabled" 
						value="${subMenu?if_exists.isDisabled?if_exists}" 
						options=IsDisabled />
					<div class="text-right ml-3">
						<@submit label="${uiLabelMap.Save!}" onclick="javascript:return onSubmitValidate(this);" />
						<@reset label="${uiLabelMap.Clear!}" onclick="javascript:clearFields();" />
					</div>
				</form>
			</div>
		</div>
	</div>
</div>
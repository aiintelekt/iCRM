<#include "component://ab-ag-grid-support/webapp/ab-ag-grid-support/lib/ag_grid_header.ftl" />
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl" />
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
		<@sectionFrameHeader title="Update Tab Shortcuts" extra=extra?if_exists />
		<div class="row pt-2">
			<div class="col-md-12 col-lg-6 col-sm-12 ">
				<#assign tabShortCut=delegator.findOne("OfbizPageSecurity", {"tabId", parameters.tabId,"pageId",parameters.pageId,"permissionId",parameters.permissionId,"componentId",parameters.componentId}, false)?if_exists />
				<@inputRow 
					id="tabId" 
					name="tabId" 
					label="Tab Id" 
					readonly=true 
					value="${tabShortCut?if_exists.tabId!}" />
				<@inputRow 
					id="tabName" 
					name="tabName" 
					label="Tab Name" 
					readonly=true 
					value="${tabShortCut?if_exists.uiLabels!}" />
				<form id="createUser" name="updateUser" action="updateTabShortcuts" method="POST">
					<@inputHidden id="tabId" name="tabId" value="${tabShortCut?if_exists.tabId!}" />
					<@inputHidden id="tabName" value="${tabShortCut?if_exists.uiLabels!}" />
					<@inputHidden id="componentId" name="componentId" value="${tabShortCut?if_exists.componentId!}" />
					<@inputHidden id="pageId" name="pageId" value="${tabShortCut?if_exists.pageId!}" />
					<@inputHidden id="permissionId" name="permissionId" value="${tabShortCut?if_exists.permissionId!}" />

					<@inputRow 
						id="requestUri" 
						name="requestUri" 
						label="Request Uri" 
						placeholder="Request Uri" 
						value="${tabShortCut?if_exists.requestUri?if_exists}" />
					<#assign isDisabled=delegator.findAll("OfbizPageSecurity",false)?if_exists />
					<#assign IsDisabled=Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(isDisabled, "isDisabled" ,"isDisabled")?if_exists />
					<@dropdownCell 
						id="isDisabled" 
						name="isDisabled" 
						label="Is Disabled" 
						allowEmpty=true 
						placeholder="Is Disabled" 
						value="${tabShortCut?if_exists.isDisabled?if_exists}" 
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
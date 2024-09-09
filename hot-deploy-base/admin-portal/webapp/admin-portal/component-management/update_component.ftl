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
	<a href="findComponents" class="btn btn-xs btn-primary">
	<i class="fa fa-chevron-circle-right" aria-hidden="true"></i> Back </a>' />
	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel" style="padding-bottom:7px;">
		<@sectionFrameHeader title="Update Components" extra=extra?if_exists />
		<div class="row pt-2">
			<div class="col-md-12 col-lg-6 col-sm-12 ">
				<#assign components=delegator.findOne("OfbizComponentAccess", {"componentId", parameters.componentId}, false)?if_exists />
				<@inputRow
					id="componentId"
					name="componentId"
					label="Component Id"
					readonly=true
					value="${components?if_exists.componentId?if_exists}"
					required=true />

				<@inputRow 
					id="uiLabels"
					name="uiLabels"
					label="Component Name"
					readonly=true
					value="${components?if_exists.uiLabels?if_exists}"
					required=true />

				<form id="createUser" name="updateUser" action="updateComponents" method="POST">
					<@inputHidden id="componentId" name="componentId" value="${components?if_exists.componentId?if_exists}" />
					<@inputHidden id="uiLabels" name="uiLabels" value="${components?if_exists.uiLabels?if_exists}" />

					<@inputRow
						id="requestUri"
						name="requestUri"
						label="Request Uri"
						placeholder="Request Uri"
						value="${components?if_exists.requestUri?if_exists}" />

					<#assign request=delegator.findAll("OfbizComponentAccess",false)?if_exists />
					<#assign IsDisabled=Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(request, "isHide" ,"isHide")?if_exists />
					<@dropdownCell 
						id="isHide"
						name="isHide"
						label="Is Hide"
						placeholder="Is Hide"
						value="${components?if_exists.isHide?if_exists}"
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
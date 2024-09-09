<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl" />
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl" />
<div class="col-md-12 col-lg-12 col-sm-12">
	<div class="row pt-2">
		<div class="col-md-12 col-lg-6 col-sm-12 ">
			<@displayCell
				label="${uiLabelMap.FirstName}"
				value="${userData.firstName!uiLabelMap.NotAvailable}"
				/>
			<@displayCell
				label="${uiLabelMap.LastName}"
				value="${userData.lastName!uiLabelMap.NotAvailable}"
				/>
			<@displayCell
				label="${uiLabelMap.Username}"
				value="${userData.userLoginId!uiLabelMap.NotAvailable}"
				/>
			<@displayCell
				label="${uiLabelMap.BU}"
				value="${userData.businessUnitName!uiLabelMap.NotAvailable}"
				/>
			<@displayCell
				label="${uiLabelMap.Gender}"
				value="${userData.gender!uiLabelMap.NotAvailable}"
				/>
			<@displayCell
				label="${uiLabelMap.Salutation}"
				value="${userData.salutation!uiLabelMap.NotAvailable}"
				/>
		</div>
		<div class="col-md-12 col-lg-6 col-sm-12">
			<@displayCell
				label="${uiLabelMap.DateOfBirth}"
				value="${inputContext.bDate!uiLabelMap.NotAvailable}"
				/>
			<@displayCell
				label="${uiLabelMap.JobTitle}"
				value="${userData.jobTitle!uiLabelMap.NotAvailable}"
				/>
		<#--<@displayCell label="${uiLabelMap.Nationality}"
				value="${userData.nationality!uiLabelMap.NotAvailable}"
				/>
			<@displayCell
				label="${uiLabelMap.RelationshipBranch}"
				value="${userData.relationshipBranch!uiLabelMap.NotAvailable}"
				/>
			<@displayCell
				label="${uiLabelMap.Manager}"
				value="${userData.manager!uiLabelMap.NotAvailable}"
				/>-->
			<@displayCell
				label="${uiLabelMap.OccupationalGroup}"
				value="${userData.occupationalGroup!uiLabelMap.NotAvailable}"
				/>
			<@displayCell
				label="${uiLabelMap.UserStatus}"
				value="${userData.userStatusDetail!uiLabelMap.NotAvailable}"
				/>
			<@displayCell
				label="${uiLabelMap.Location}"
				value="${inputContext.locationDesc!uiLabelMap.NotAvailable}"
				/>
		</div>
	</div>
</div>
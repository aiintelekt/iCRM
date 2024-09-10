<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl" />
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl" />
<div class="col-md-12 col-lg-12 col-sm-12">
	<div class="row pt-2">
		<div class="col-md-12 col-lg-6 col-sm-12 ">
			<@displayCell
				label="Address Line 1"
				value="${inputContext.generalAddress1!uiLabelMap.NotAvailable}"
				/>
			<@displayCell
				label="Address Line 2"
				value="${inputContext.generalAddress2!uiLabelMap.NotAvailable}"
				/>
			<@displayCell
				label="Zip/Postal Code"
				value="${inputContext.generalPostalCode!uiLabelMap.NotAvailable}"
				/>
			<@displayCell
				label="Zip/Postal Code Extension"
				value="${inputContext.generalPostalCodeExt!uiLabelMap.NotAvailable}"
				/>
		</div>
		<div class="col-md-12 col-lg-6 col-sm-12">
			<@displayCell
				label="City"
				value="${inputContext.generalCity!uiLabelMap.NotAvailable}"
				/>
			<@displayCell
				label="State/Provience"
				value="${inputContext.state!uiLabelMap.NotAvailable}"
				/>
			<@displayCell
				label="Country"
				value="${inputContext.country!uiLabelMap.NotAvailable}"
				/>
			<@displayCell
				label="Primary Email Address"
				value="${inputContext.primaryEmail!uiLabelMap.NotAvailable}"
				/>
			<@displayCell
				label="Primary Contact Number"
				value="${inputContext.primaryPhoneNumber!uiLabelMap.NotAvailable}"
				/>
		</div>
	</div>
</div>
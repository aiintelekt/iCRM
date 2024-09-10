<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<div id="tab1" class="tab-pane fade active show">
	<div class="col-lg-12 col-md-12 col-sm-12 pd-lr-0">
		<form name="updateBusinessUnit" id="updateBusinessUnit" method="post" action="<@ofbizUrl>businessUnitUpdation</@ofbizUrl>" data-toggle="validator" class="form-horizontal" >
			<@dynaScreen
				instanceId="UPDATE_BUSINESS_UNITS"
				modeOfAction="CREATE"
				/>
			<div class="offset-md-2 col-sm-10">
				<input type="submit" class="btn btn-sm btn-primary" onclick="return formSubmission();" value="Update">
				<a href="viewBusinessUnits?productStoreGroupId=${productStoreGroupId!}"class="btn btn-sm btn-secondary"> Cancel</a>
			</div>
		</form>
			<@parentBUPicker 
				instanceId="parentBUPicker"
				/> 
	</div>
</div>
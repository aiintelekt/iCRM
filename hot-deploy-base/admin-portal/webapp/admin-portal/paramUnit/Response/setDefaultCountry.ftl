<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	<h2 class="float-left">Set Default Country</h2>
	<div class="clearfix"></div>
	<form id="mainForm" method="post" action="<@ofbizUrl>createAndUpdateDefaultValues</@ofbizUrl>" data-toggle="validator">
		<@inputHidden id="parameterName" value="Default Country" />
		<@inputHidden id="parameterId" value="DEFAULT_COUNTRY" />
		<@inputHidden id="sections" value="${inputContext.enumId?if_exists}" />
		<@dynaScreen
			instanceId="DEFAULT_ADDR_CONFIG"
			modeOfAction="CREATE"
			/>
		<div class="clearfix"></div>
		<div class="offset-md-2 col-sm-10 p-2">
			<@submit
				class="btn btn-sm btn-primary navbar-dark"
				id="saveModal"
				label="${uiLabelMap.Save}"
				/>
		</div>
	</form>
</div>

<script>

$(document).ready(function() {
/*
var loggedInUserRole  = $("#userLoginRole").val();
var loggedInUserId  = $("#loggedInUserId").val();
var loggedInUserName  = $("#userName").val();

getTeamMembersPRF(loggedInUserId,loggedInUserRole,loggedInUserName);

$('#generalCountryGeoId').change(function(e, data) {
	getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName','${stateValue!}');
});
*/	
});

function getTeamMembersPRF(loggedInUserId,loggedInUserRole,loggedInUserName) {
	var userOptionList;
	userOptionList = '<option value="'+loggedInUserId+'">'+loggedInUserName+'</option>';
	 
   	$("#personResponsible").html(userOptionList);
}

</script>
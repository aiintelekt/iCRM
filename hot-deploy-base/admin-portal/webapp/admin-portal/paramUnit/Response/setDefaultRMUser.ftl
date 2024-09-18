<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	<h2 class="float-left">Set Default RM</h2>
	<div class="clearfix"></div>
	<form id="mainForm" method="post" action="<@ofbizUrl>createAndUpdateDefaultValues</@ofbizUrl>" data-toggle="validator">
		<@inputHidden id="parameterName" value="Default RM User"/>
		<@inputHidden id="parameterId" value="DEFAULT_RM_USER"/>
		<@inputHidden id="sections" value="${inputContext.enumId?if_exists}"/>
		<@inputHidden id="loggedInUserId" value="${inputContext.ownerPartyId?if_exists}"/>
		<#assign userName = userLogin.userLoginId>
		<#assign findMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", userLogin.partyId)>
		<#assign person = delegator.findOne("Person", findMap, true)!>
		<#if person?has_content>
		<#assign userName = (person.firstName!) + " " + (person.middleName!) + " " + person.lastName!>
		<@inputHidden id="userName" value="${userName!}"/>
		</#if>
		<@dynaScreen
			instanceId="DEFAULT_RM_USER"
			modeOfAction="CREATE"
			/>
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

$(".ownerPartyId-input").one( "click",function(){
	getOppoOwner();
});

//getOppoOwner();	
});

function getOppoOwner(){
	var loggedInUserId = document.getElementById("loggedInUserId").value;
	var userName = document.getElementById("loggedInUserId").value;
	dataSourceOptions = '';
	$.ajax({
		type: "POST",
     	url:'/common-portal/control/getUsersList',
        data: {"externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        async: false,
        success: function(data) {
        	dataSourceOptions = '<option value="'+loggedInUserId+'">'+userName+'</option>';
        	for (var i = 0; i < data.length; i++) {
                var type = data[i];
                	dataSourceOptions += '<option value="'+type.userName+'">'+type.userName+'</option>';
                
            }
        }
        
	});    
	
	$("#ownerPartyId").html( dataSourceOptions );
			
	$("#ownerPartyId").dropdown('refresh');
}
</script>	



 

	
	



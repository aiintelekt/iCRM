<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row pt-2">
	<div class="col-md-6 col-lg-6 col-sm-6 ">
		<@pageSectionHeader title="${uiLabelMap.AgentConfig!}" extra="" />
		<form id="messenger-agent-config-form" method="POST" data-toggle="validator">
			<@inputHidden 
              	id="partyId"
              	value=userLoginPartyId!
              	/>
            <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
            <div class="col-lg-12 col-md-12 col-sm-12">
            	<#if activeMessType?has_content && activeMessType=='RING_CENTRAL'>
            		<#if activeRcMessAPI?has_content && activeRcMessAPI='NICE'>
            			<@inputRow 
						id="agentId"
						type='text'
						label="${uiLabelMap.agentId}"
						placeholder="${uiLabelMap.agentId}"
						value="${asAgentId!}"
						/>
            		</#if>
            	</#if>
            
            	<@inputRow 
				id="stationId"
				type='text'
				label="${uiLabelMap.stationId}"
				placeholder="${uiLabelMap.stationId}"
				value="${stationId!}"
				/>
            </div>
            
            <div class="clearfix"></div>
            <div class="offset-md-4 col-sm-10">
            
            <@formButton
            	btn1id="messenger-setup-btn"
				btn1type="button"
                btn1label="${uiLabelMap.Save}"
             	/>
         	
            </div>
		</form>
	</div>
</div>

<script>
$(document).ready(function() {

$("#messenger-setup-btn").click(function () {
	
	$.ajax({
		type: "POST",
     	url: "/messenger-portal/control/updateAgentConfig",
        data: JSON.parse(JSON.stringify($("#messenger-agent-config-form").serialize())),
        async: false,
        success: function (data) {
            if (data.code == 200) {
            	showAlert('success', 'Successfully updated');
            } else {
            	showAlert('error', data.message);
            }
        }
	});
	
});

});
</script>
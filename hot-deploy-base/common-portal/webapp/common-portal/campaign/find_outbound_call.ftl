<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="pt-2 align-lists">
	<form id="outboundcall-search-form" name="outboundcall-search-form" method="post">
		
		<#-- <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
		<input type="hidden" name="marketingCampaignId" value="${marketingCampaignId?if_exists}"/> -->
		
		<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
		<input type="hidden" name="domainEntityId" value="${domainEntityId!}">
				
		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	</form>
</div>

<#if activeMessType?has_content>
	<#include "component://messenger-portal/webapp/messenger-portal/messenger/place_call.ftl"/>
</#if>	

<script>
jQuery(document).ready(function() {
	
});
</script>
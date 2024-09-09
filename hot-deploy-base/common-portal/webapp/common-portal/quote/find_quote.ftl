<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="pt-2">
	<form method="post" id="quote-search-form" novalidate="novalidate" data-toggle="validator">
		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
		<input type="hidden" name="partyId" value="${inputContext.partyId?if_exists}"/>
		<input type="hidden" name="oppoId" value="${salesOpportunityId?if_exists}"/>
	</form>
</div>
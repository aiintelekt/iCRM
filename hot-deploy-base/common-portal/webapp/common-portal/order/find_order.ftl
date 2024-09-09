<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/order/modal_window.ftl"/>

<div class="pt-2">
<form method="post" id="order-search-form" novalidate="novalidate" data-toggle="validator">	
	<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>	
	<input type="hidden" name="partyId" value="${inputContext.partyId?if_exists}"/>
	<input type="hidden" name="srNumber" value="${srNumber?if_exists}"/>
	<input type="hidden" name="orderByColumn" value="orderDate DESC"/>
	<#if request.getRequestURI().contains("viewOpportunity")>
		<input type="hidden" name="salesOpportunityId" value="${salesOpportunityId?if_exists}"/>
	</#if>
</form>
<form method="post" id="send-order-ereceipt-form">
	<input type="hidden" name="transactionNumber" value=""/>
	<input type="hidden" name="isRmsOrder" value="Y"/>
	<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
</form>
</div>

<@showDescription 
	instanceId="show-des-modal"
	/>

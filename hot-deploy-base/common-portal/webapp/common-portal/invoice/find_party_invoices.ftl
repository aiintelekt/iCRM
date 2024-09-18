<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/order/modal_window.ftl"/>
<#assign contextPath = request.getContextPath()/>
<#assign partyId= request.getParameter("partyId")! />
<#if !partyId?has_content & mainAssocPartyId?has_content>
<#assign partyId= mainAssocPartyId! />
</#if>
<div class="pt-2 align-lists">
	<form method="post" action="" id="findInvoices" name="findInvoices" novalidate="novalidate" data-toggle="validator">
		<input type="hidden" name="partyId" value="${partyId!}">
		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	</form>
	<form method="post" id="send-ereceipt-form">
		<input type="hidden" name="transactionNumber" value=""/>
		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	</form>
</div>
<@showDescription 
	instanceId="show-inv-des-modal"
 />
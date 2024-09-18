<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/opportunity/modal_window.ftl"/>

<#assign partyId= request.getParameter("partyId")! />

<div class="pt-2 align-lists">
<form method="post" id="opportunity-search-form" name="opportunity-search-form" novalidate="true" data-toggle="validator">	
	<input type="hidden" name="partyId" value="${partyId?if_exists}"/>
	<input type="hidden" name="roleTypeId" value="${partyRoleTypeId?if_exists}"/>
	<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	<input type="hidden" name="statusOpen" id="statusOpen" value="OPPO_OPEN" />
	<input type="hidden" name="statusClosed" id="statusClosed" value="" />
	<input type="hidden" name="estimatedClosedDays" id="estimatedClosedDays" value="" />
	<input type="hidden" name="salesOpportunityTypeId" id="salesOpportunityTypeId" value="" />
	<input type="hidden" name="userId" id="userId" value="${userLogin.userLoginId?if_exists}" />
</form>
</div>
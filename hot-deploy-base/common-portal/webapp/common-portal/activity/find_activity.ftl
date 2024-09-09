<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#if mainAssocPartyId?has_content>
<#assign partyId= mainAssocPartyId />
<#else>
<#assign partyId= request.getParameter("partyId")! />
<#assign salesOpportunityId = '${requestParameters.salesOpportunityId!}' >
</#if>
<#assign srStatusId= context.get("currentSrStatusId")?if_exists />

<div class="row pt-2 align-lists">

	<div class="col-lg-6 col-md-6 col-sm-12">
		<form method="post" id="activity-search-form" novalidate="true" data-toggle="validator">

		<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
		<input type="hidden" name="domainEntityId" value="${domainEntityId!}">
		
		<input type="hidden" name="partyId" value="${partyId?if_exists}"/>
		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/> 		
		<input type="hidden" name="open" id="open" value="IA_OPEN"/>
		<input type="hidden" name="closed" id="closed" value=""/>
		<input type="hidden" name="scheduled" id="scheduled" value=""/> 		
		<input type="hidden" name="workEffortTypeIdNotIn" value="WORK_FLOW">
		<#if isEnableProgramAct?has_content && isEnableProgramAct="Y">	
		<input type="hidden" name="isChecklistActivity" value="N">	
		</#if>
		</form>
	
	</div>
</div>
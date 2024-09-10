<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/activity/modal_window.ftl"/>

<#if mainAssocPartyId?has_content>
<#assign partyId= mainAssocPartyId />
<#else>
<#assign partyId= request.getParameter("partyId")! />
</#if>
<#assign partySummaryDetailsView = (delegator.findOne("PartySummaryDetailsView", {"partyId" : partyId}, false))?if_exists/>
<div class="pt-2 align-lists">
	
	<form method="post" id="activity-search-form" class="form-horizontal" novalidate="true" data-toggle="validator">	
		
		<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
		<input type="hidden" name="domainEntityId" value="${domainEntityId!}">
		
		<input type="hidden" name="partyId" value="${partyId?if_exists}"/>
		<input type="hidden" id="open" name="open" value="${partyId?if_exists}"/>
		<input type="hidden" id="closed" name="closed" value="${partyId?if_exists}"/>
		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/> 
	
	</form>

</div>

<script>

jQuery(document).ready(function() {

$('.activity-status').change(function(){
	getActivityRowData();
});

$('#refresh-activity-btn').on('click', function() {
	getActivityRowData();
});

/*
$('#create-activity-btn').on('click', function() {
	$('#create-activity-modal').modal("show");
});
*/

});

</script>
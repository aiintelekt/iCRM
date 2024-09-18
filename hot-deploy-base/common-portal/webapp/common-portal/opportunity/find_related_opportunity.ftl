<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/opportunity/modal_window.ftl"/>

<#assign partyId= request.getParameter("partyId")! />

<div class="page-header border-b pt-2 align-lists">

<form method="post" id="opportunity-search-form" class="form-horizontal" novalidate="true" data-toggle="validator">	

<input type="hidden" name="domainEntityType" value="RELATED_OPPORTUNITY">
<input type="hidden" name="domainEntityId" value="${domainEntityId!}">

<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

<div class="col-lg-12 col-md-12 col-sm-12 check-list">
	<h2 class="right-icones">Related Opportunities</h2>
	<span id="relate-opportunity-btn" class="text-dark btn" title="Relate Opportunity" data-toggle="modal" data-target="#add-relate-opp-modal"> 
		<i class="fa fa-plus fa-1" aria-hidden="true"></i>   
	</span>
</div>

</form>

</div>

<@addRelateOpportunity 
	instanceId="add-relate-opp-modal"
	/>

<script>

jQuery(document).ready(function() {
/*
$('#relate-opportunity-btn').on('click', function() {
	$('#create-activity-modal').modal("show");
});
*/
});

</script>
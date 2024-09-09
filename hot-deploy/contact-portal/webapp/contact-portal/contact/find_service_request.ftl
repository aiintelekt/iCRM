<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/service-request/modal_window.ftl"/>

<#assign partyId= request.getParameter("partyId")! />
<#assign partySummaryDetailsView = (delegator.findOne("PartySummaryDetailsView", {"partyId" : partyId}, false))?if_exists/>
<div class="pt-2 align-lists">
<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "serviceRequest") />  
<form method="post" id="sr-search-form" class="form-horizontal" novalidate="true" data-toggle="validator">	

<input type="hidden" name="partyId" value="${partyId?if_exists}"/>
<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
<input type="hidden" name="domainEntityId" value="${domainEntityId!}">
<input type="hidden" name="open" id="open" value="" />
<input type="hidden" name="closed" id="closed" value="" />
<input type="hidden" name="slaAtRisk" id="slaAtRisk" value="" />
<input type="hidden" name="slaExpired" id="slaExpired" value="" />
</form>

</form>

</div>

<@createServiceRequestModal 
	instanceId="create-sr-modal"
	/>

<script>

jQuery(document).ready(function() {

/*
$('.sr-status').change(function(){
	//alert(this.checked);
	getServiceRequestRowData();
});

$('#refresh-sr-btn').on('click', function() {
	getServiceRequestRowData();
});

$('#create-sr-btn').on('click', function() {
	//$('#create-sr-modal').modal("show");
});
*/
});

</script>
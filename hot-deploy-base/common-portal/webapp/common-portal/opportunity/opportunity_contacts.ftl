<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/contact/modal_window.ftl"/>

<#assign salesOpportunityId= request.getParameter("salesOpportunityId")! />

<div class="page-header border-b pt-2">
	<h2 class="d-inline-block">Contacts</h2>
	<ul class="flot-icone">
		<#-- <a href="<@ofbizUrl>createContact?accountPartyId=${accountPartyId?if_exists}&tabId=account</@ofbizUrl>" class="btn btn-xs btn-primary m5" > ${uiLabelMap.createNew}</a> --> 
    	<button type="button" class="btn btn-xs btn-primary m5" data-toggle="modal" data-target="#add-opportunity-contact-modal">${uiLabelMap.addFromExisting}</button>
	</ul>
</div>

<#-- ${screens.render("component://common-portal/widget/contact/ContactScreens.xml#ContactAndPartyAssoc")} -->
<form method="post" id="searchContactsForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">

<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
<input type="hidden" name="domainEntityId" value="${domainEntityId!}">

</form>

<@addPartyContact 
	instanceId="add-opportunity-contact-modal"
	/>

<script>

jQuery(document).ready(function() {

/*
$("#find-order").click(function(event) {
    getOrderRowData();
});
*/

});

</script>
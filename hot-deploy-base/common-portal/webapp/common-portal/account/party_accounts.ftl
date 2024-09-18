<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/account/modal_window.ftl"/>

<#assign partyId= request.getParameter("partyId")! />
<#assign partySummaryDetailsView = (delegator.findOne("PartySummaryDetailsView", {"partyId" : partyId}, false))?if_exists/>
<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "releatedParties") />  
<div class="pt-2">
	<h2 class="d-inline-block">Related Parties</h2> 
	<#if partySummaryDetailsView.statusId?if_exists != "PARTY_DISABLED">
	<ul class="flot-icone">
	    <#-- <a target="_blank" href="/contact-portal/control/createContact?partyId=${partyId?if_exists}&tabId=account" class="btn btn-xs btn-primary m5" > ${uiLabelMap.createNew}</a> --> 
    	<button type="button" class="btn btn-xs btn-primary m5" data-toggle="modal" data-target="#add-related-party">${uiLabelMap.addFromExisting}</button>
	<span>${helpUrl?if_exists}</span>
	</ul>
	</#if>
</div>

${screens.render("component://common-portal/widget/contact/ContactScreens.xml#ContactAndPartyAssoc")}

<@addParty
	instanceId="add-related-party"
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
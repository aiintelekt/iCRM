<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/contact/modal_window.ftl"/>

<#assign partyId= request.getParameter("partyId")! />
 <#assign srStatusId= context.get("currentSrStatusId")?if_exists />
	<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "releatedParties") />  
<div class="pt-2">
	<h2 class="d-inline-block">${uiLabelMap.relatedParties!}</h2>
	<#if srStatusId?has_content && ("SR_CLOSED" == srStatusId || "SR_CANCELLED" == srStatusId)>
			
	<#else>
		<#if partyStatusId?if_exists != "PARTY_DISABLED">
		<ul class="flot-icone">
		<#if requestURI?has_content && requestURI=="viewLead" || requestURI=="viewAccount">
			<a target="_blank" href="/contact-portal/control/createContact?accountPartyId=${partyId?if_exists}&externalLoginKey=${requestAttributes.externalLoginKey!}" class="btn btn-xs btn-primary m5" > ${uiLabelMap.createContact}</a> 
		</#if>
    	 <button type="button" class="btn btn-xs btn-primary m5" data-toggle="modal" data-target="#add-contact-modal">${uiLabelMap.addFromExisting}</button>
    	 <span>${helpUrl?if_exists}</span>
		</ul>
		</#if>
	</#if>
</div>

${screens.render("component://common-portal/widget/contact/ContactScreens.xml#ContactAndPartyAssoc")}

<@addPartyContact 
	instanceId="add-contact-modal"
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
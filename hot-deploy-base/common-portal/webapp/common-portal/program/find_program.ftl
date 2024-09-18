<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/rebate/modal_window.ftl"/>

<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "rebates") />  
<#assign contextPath = request.getContextPath()/>

<div class="row">
	<div class="pt-2 align-lists">
		<form id="program-search-form" name="program-search-form" method="post">	
        <input type="hidden" name="custRequestDomainType" value="PROGRAM">
        <input type="hidden" name="partyId" value="${domainEntityId!}">
        <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
    	</form>	
	</div>
</div>

<script>
jQuery(document).ready(function() {
	

});
</script>
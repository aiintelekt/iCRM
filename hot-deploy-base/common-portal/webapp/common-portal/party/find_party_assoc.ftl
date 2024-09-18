<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/party/modal_window.ftl"/>
<#-- <#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "notes") />  -->
<#assign contextPath = request.getContextPath()/>
<div class="pt-2 align-lists">
	
    <form id="partyassoc-search-form" name="partyassoc-search-form" method="post">	
    	<input type="hidden" id="partyassoc-domainEntityType" name="domainEntityType" value="${domainEntityType!}">
        <input type="hidden" id="partyassoc-domainEntityId" name="domainEntityId" value="${domainEntityId!}">
        <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
    </form>
    
</div>
<@createPartyAssocModal 
instanceId="create-partyassoc-modal"
/>
<script>
jQuery(document).ready(function() {
	
$('#create-partyassoc-btn').on('click', function() {
	$('#create-partyassoc-modal').modal("show");
});

});
</script>
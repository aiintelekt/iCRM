<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/service-request/modal_window.ftl"/>

<#assign partyId= request.getParameter("partyId")! />

<div class="pt-2 align-lists">
 	<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "serviceRequest") />  

<form method="post" id="sr-search-form" data-toggle="validator">	

<input type="hidden" name="partyId" value="${partyId?if_exists}"/>
<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
<input type="hidden" name="open" id="open" value="" />
<input type="hidden" name="closed" id="closed" value="" />
<input type="hidden" name="slaAtRisk" id="slaAtRisk" value="" />
<input type="hidden" name="slaExpired" id="slaExpired" value="" />
<input type="hidden" name="userId" id="userId" value="${userLogin.userLoginId?if_exists}" />
</form>

</div>

<@createServiceRequestModal 
	instanceId="create-sr-modal"
	/>

<script>

jQuery(document).ready(function() {

/*$('.sr-status').change(function(){
	//alert(this.checked);
	loadSrGrid();
});

$('#refresh-sr-btn').on('click', function() {
	loadSrGrid();
});*/

$('#create-sr-btn').on('click', function() {
	//$('#create-sr-modal').modal("show");
});

});

</script>
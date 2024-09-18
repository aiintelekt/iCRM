<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://lead-portal/webapp/lead-portal/history/modal_window.ftl"/>
<div class="row">
		<form method="post" id="lead-history-search-form" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
			<input type="hidden" name="orderByColumn" value="createdStamp" />
			<input type="hidden" name="partyId" value="${inputContext.partyId!}" />
			<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
		</form>
<@leadEventHistory
instanceId="leadEventHistory" leadName="${inputContext.leadName!}"
/>

<script>

jQuery(document).ready(function() {
	//$('#leadEventHistory_event-ref-btn').click();
	$("#view_event").click(function(){
		$("#leadEventHistory").modal('show');
	});	
});
	
</script>

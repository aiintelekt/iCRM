<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>

<div class="page-header">
	<h2 class="float-left">${uiLabelMap.List} ${uiLabelMap.WebhookConfig}</h2>
</div>
			
<div class="table-responsive">
	<table class="table table-hover" id="config-webhook">
	<thead>
	<tr>
		<th>${uiLabelMap.url!}</th>
		<th>${uiLabelMap.authKey!}</th>
		<th>${uiLabelMap.serviceName!}</th>
		<th>${uiLabelMap.isEnabled!}</th>
		<th>${uiLabelMap.sequence!}</th>
		<th class="text-center">Action</th>
	</tr>
	</thead>
	<tbody>
	
	<#if webhookList?has_content>
		
	<#list webhookList as ec>
	<tr>
		<td>${ec.webhookUrl!}</td>
		<td>${ec.authKey!}</td>
		<td>${ec.serviceName!}</td>
		<td>${ec.isEnabled!}</td>
		<td>${ec.webhookSeqNum!}</td>
		<td class="text-center">
			<div class="">
				<a href="editWebhookConfig?configId=${ec.customFieldWebhookConfigId}" class="btn btn-xs btn-primary tooltips" data-original-title="Edit"><i class="fa fa-pencil info"></i></a>
				<a class="btn btn-xs btn-secondary btn-danger tooltips confirm-message" href="deleteWebhookConfig?configId=${ec.customFieldWebhookConfigId}" data-original-title="Remove"><i class="fa fa-times red"></i></a>
			</div>
		</td>	
	</tr>
	
	</#list>
		
	</#if>
	
	</tbody>
	</table>
</div>
			
<script type="text/javascript">

jQuery(document).ready(function() {

	$('#config-webhook').DataTable({
  		"order": [],
  		"fnDrawCallback": function( oSettings ) {
      		resetDefaultEvents();
    	}
	});
	
});		
</script>
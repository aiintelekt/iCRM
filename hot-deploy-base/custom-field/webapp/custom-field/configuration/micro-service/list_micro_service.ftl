<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>

<div class="page-header">
	<h2 class="float-left">${uiLabelMap.List} ${uiLabelMap.MicroServiceConfig}</h2>
</div>
			
<div class="table-responsive">
	<table class="table table-hover" id="config-mc">
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
	
	<#if microServiceList?has_content>
		
	<#list microServiceList as ec>
	<tr>
		<td>${ec.microUrl!}</td>
		<td>${ec.authKey!}</td>
		<td>${ec.serviceName!}</td>
		<td>${ec.isEnabled!}</td>
		<td>${ec.microSeqNum!}</td>
		<td class="text-center">
			<div class="">
				<a href="editMicroServiceConfig?configId=${ec.customFieldMicroServiceConfigId}" class="btn btn-xs btn-primary tooltips" data-original-title="Edit"><i class="fa fa-pencil info"></i></a>
				<a class="btn btn-xs btn-secondary btn-danger tooltips confirm-message" href="deleteMicroServiceConfig?configId=${ec.customFieldMicroServiceConfigId}" data-original-title="Remove"><i class="fa fa-times red"></i></a>
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
		
	$('#config-mc').DataTable({
  		"order": [],
  		"fnDrawCallback": function( oSettings ) {
      		resetDefaultEvents();
    	}
	});
	
});	
</script>
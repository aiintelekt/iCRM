<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>

<div class="page-header">
	<h2 class="float-left">${uiLabelMap.List} ${uiLabelMap.ValueConfig}</h2>
</div>
			
<div class="table-responsive">
	<table class="table table-hover" id="config-value">
	<thead>
	<tr>
		<th>${uiLabelMap.segmentCode!}</th>
		<th>${uiLabelMap.segmentValue!}</th>
		<th>${uiLabelMap.valueCapture!}</th>
		<th>${uiLabelMap.sequence!}</th>
		<th>${uiLabelMap.valueMin!}</th>
		<th>${uiLabelMap.valueMax!}</th>
		<th>${uiLabelMap.valueData!}</th>
		<th class="text-center">Action</th>
	</tr>
	</thead>
	<tbody>
	
	<#if valueConfigList?has_content>
		
	<#list valueConfigList as ec>
	<tr>
		<td>${ec.getRelatedOne("CustomFieldGroup").get("groupName", locale)!}</td>
		<td>${ec.getRelatedOne("CustomField").get("customFieldName", locale)!}</td>
		<td>${ec.valueCapture!}</td>
		<td>${ec.valueSeqNum!}</td>
		<td>${ec.valueMin!}</td>
		<td>${ec.valueMax!}</td>
		<td>${ec.valueData!}</td>
		<td class="text-center">
			<div class="">
				<a href="editValueConfig?groupId=${ec.groupId}&customFieldId=${ec.customFieldId}&valueCapture=${ec.valueCapture}&valueSeqNum=${ec.valueSeqNum}" class="btn btn-xs btn-primary tooltips" data-original-title="Edit"><i class="fa fa-pencil info"></i></a>
				<a class="btn btn-xs btn-secondary btn-danger tooltips confirm-message" href="deleteValueConfig?groupId=${ec.groupId}&customFieldId=${ec.customFieldId}&valueCapture=${ec.valueCapture}&valueSeqNum=${ec.valueSeqNum}" data-original-title="Remove"><i class="fa fa-times red"></i></a>
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

	$('#config-value').DataTable({
  		"order": [],
  		"fnDrawCallback": function( oSettings ) {
      		resetDefaultEvents();
    	}
	});

});			
</script>
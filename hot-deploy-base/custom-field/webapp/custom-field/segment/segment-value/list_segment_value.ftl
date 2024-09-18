<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<script type="text/javascript" language="javascript" src="/bootstrap/js/jquery.dataTables.min.js"></script>
<@pageSectionHeader title="${uiLabelMap.List} ${uiLabelMap.SegmentValue}" />
<#if groupId?has_content>
	<#assign deleteActionUrl = "deleteSegmentValueForGroup"/>
<#else>
	<#assign deleteActionUrl = "deleteSegmentValue"/>
</#if>
	
<div class="table-responsive">
	<table class="table table-hover" id="list-segment-value">
	<thead>
	<tr>
		
		<#if !customFieldGroup.groupId?has_content>
		<th>${uiLabelMap.groupingCode!}</th>
		<th>${uiLabelMap.segmentCode!}</th>
		</#if>
		
		<th>${uiLabelMap.segmentValueId!}</th>
		<th>${uiLabelMap.segmentValueName!}</th>
		<th>${uiLabelMap.isEnabled!}</th>
		<th>${uiLabelMap.isDefault!}</th>
		
		<#if customFieldGroup.valueCapture?has_content && customFieldGroup.valueCapture == "SINGLE">
			<th>${uiLabelMap.valueData!}</th>
		<#elseif customFieldGroup.valueCapture?has_content && customFieldGroup.valueCapture == "RANGE">
			<th>${uiLabelMap.valueMin!}</th>
			<th>${uiLabelMap.valueMax!}</th>
		<#elseif customFieldGroup.valueCapture?has_content && customFieldGroup.valueCapture == "MULTIPLE">
			<th class="text-center">${uiLabelMap.MultiValue!}</th>	
		<#else>
			<th>${uiLabelMap.valueMin!}</th>
			<th>${uiLabelMap.valueMax!}</th>
			<th>${uiLabelMap.valueData!}</th>
		</#if>
		
		<th>${uiLabelMap.sequence!}</th>
				
		<th class="text-center">Action</th>
	</tr>
	</thead>
	<tbody>
	
	<#if customFieldList?has_content>
		
	<#list customFieldList as ec>
	
	<tr>
		
		<#if !customFieldGroup.groupId?has_content>
		<td>
			${ec.groupingCodeName!}
		</td>
		<td>${ec.groupName!}</td>
		</#if>
		
		<td>${ec.customFieldId!}</td>
		<td>${ec.customFieldName!}</td>
		<td>${ec.isEnabled!}</td>
		<td>${ec.isDefault!}</td>
		
		<#if customFieldGroup.valueCapture?has_content && customFieldGroup.valueCapture == "SINGLE">
			<td>${ec.valueData!}</td>
		<#elseif customFieldGroup.valueCapture?has_content && customFieldGroup.valueCapture == "RANGE">
			<td>${ec.valueMin!}</td>
			<td>${ec.valueMax!}</td>
		<#elseif customFieldGroup.valueCapture?has_content && customFieldGroup.valueCapture == "MULTIPLE">	
			<td class="text-center">
			<div class="">
				<a href="#" class="btn btn-xs btn-primary tooltips view-multi-value " data-segmentValueName="${ec.customFieldName!}" data-groupId="${ec.groupId!}" data-customFieldId="${ec.customFieldId!}" data-original-title="View multi values for ${ec.customFieldName!}"><i class="fa fa-eye info"></i></a>
			</div>
			</td>	
		<#else>
			<td>${ec.valueMin!}</td>
			<td>${ec.valueMax!}</td>
			<td>${ec.valueData!}</td>
		</#if>
		
		<td>${ec.sequenceNumber!}</td>
		
		<td class="text-center">
			<div class="">
				<#if !showDripCampaign?has_content>
				<a href="segmentValueCustomer?customFieldId=${ec.customFieldId}&groupId=${ec.groupId!}" class="btn btn-xs btn-primary tooltips <#if !ec.groupId?has_content>disabled</#if> " data-original-title="${uiLabelMap.ManageCustomers!}"><i class="fa fa-plus info"></i></a>
				<a href="editSegmentValue?customFieldId=${ec.customFieldId}&groupId=${ec.groupId!}" class="btn btn-xs btn-primary tooltips" data-original-title="Edit"><i class="fa fa-pencil info"></i></a>
				<#-- <a class="btn btn-xs btn-danger btn-danger tooltips confirm-message" href="${deleteActionUrl}?customFieldId=${ec.customFieldId}&groupId=${ec.groupId!}" data-original-title="Remove"><i class="fa fa-times red"></i></a> -->
				<#elseif showDripCampaign?has_content>
				<a href="/custom-field/control/editSegmentValueCampaignConfig?customFieldId=${ec.customFieldId}&groupId=${ec.groupId!}&marketingCampaignId=${marketingCampaignId!}${StringUtil.wrapString(externalKeyParam)}" class="btn btn-xs btn-primary tooltips" data-original-title="Edit"><i class="fa fa-pencil info"></i></a>
				</#if>
			</div>
		</td>	
	</tr>
	
	</#list>
		
	</#if>
	
	</tbody>
	</table>
</div>

<div id="modalMulValueView" class="modal fade" >
  <div class="modal-dialog modal-lg">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <h2 class="modal-title">${uiLabelMap.List} ${uiLabelMap.MultiValue} for [ <span id="multi-segment-value-title"></span> ]</h2>
        <button type="reset" class="close" data-dismiss="modal">&times;</button>
      </div>
      <div class="modal-body">
        
        <div class="clearfix"></div>
		<#-- <div class="page-header">
			<h2 class="float-left"></h2>
			<div class="float-right">
		  			
			</div>
		</div> -->
		<div class="table-responsive">
			<table id="view-multi-value-list" class="table table-striped">
				<thead>
					<tr>
						<th>${uiLabelMap.fieldValue!}</th>
						<th>${uiLabelMap.description!}</th>
						<th>${uiLabelMap.hide!}</th>
						<th>${uiLabelMap.sequence!}</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
		</div>
        
      </div>
      <div class="modal-footer">
        <button type="sbmit" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
</div>

<script type="text/javascript">

var customFieldId, groupId;

jQuery(document).ready(function() {	

	$('#list-segment-value').DataTable({
  		"order": [],
  		"fnDrawCallback": function( oSettings ) {
      		resetDefaultEvents();
    	}
	});
	
	$('.view-multi-value').on('click', function(){
		
		$('#modalMulValueView').modal("show");
		
		customFieldId = $(this).attr("data-customFieldId");
		groupId = $(this).attr("data-groupId");
		var segmentValueName = $(this).attr("data-segmentValueName");
		
		$('#multi-segment-value-title').html( segmentValueName );								
																										
	});
	
	$('#modalMulValueView').on('shown.bs.modal', function (e) {
	  	findViewMultiValues(groupId, customFieldId);
	});

});

function findViewMultiValues(groupId, customFieldId) {
	
	//var customFieldId = $('#customFieldId').val();
	//var groupId = $('#groupId').val();
	
   	var url = "getSegmentValueMultiValues?customFieldId="+customFieldId+"&groupId="+groupId;
   	
	$('#view-multi-value-list').DataTable( {
		    "processing": true,
		    "destroy": true,
		    "ajax": {
	            "url": url,
	            "type": "POST"
	        },
	        "pageLength": 10,
	        "order": [[ 3, "asc" ]],
	        	      
	        "columns": [
	        	
	            { "data": "fieldValue" },
	            { "data": "description" },
	            { "data": "hide" },
	            { "data": "sequenceNumber" },
	            
	        ],
	        "fnDrawCallback": function( oSettings ) {
	      		resetDefaultEvents();
	    	}
		});
	
}
		
</script>
<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>

<div class="page-header">
	<h2 class="float-left">${uiLabelMap.List} ${uiLabelMap.CustomField}</h2>
</div>

<#if groupId?has_content>
	<#assign deleteActionUrl = "deleteCustomFieldForGroup"/>
<#else>
	<#assign deleteActionUrl = "deleteCustomField"/>
</#if>

<div class="row padding-r">
	
	<#if showCustomSearch?has_content>
	<div class="col-md-12 card-header mt-2 mb-3">
	   <form method="post" class="form-horizontal" data-toggle="validator">
	      <div class="row">
	         <div class="col-md-2 col-sm-2">
				<div class="form-group row mr">
					<select class="ui dropdown search form-control input-sm tooltips" id="hide" name="searchRoleTypeId" data-original-title="Role Type">
						<option value="">Please Select</option>
						<#if roleTypeList?has_content>
						<#list roleTypeList.entrySet() as entry>  
  							<option value="${entry.key}" <#if searchRoleTypeId?exists && searchRoleTypeId == entry.key>selected</#if> >${entry.value!}</option>
						</#list>
    					</#if>
					</select>
				</div>
			 </div>
	         <div class="col-md-1 col-sm-1">
	            <input type="submit" class="btn btn-sm btn-primary" id="find-customer-button" value="Find"/>
	         </div>
	      </div>
	   </form>
	   <div class="clearfix"> </div>
	</div>
	</#if>
				
	<div class="table-responsive">
		<table class="table table-hover" id="list-custom-field">
		<thead>
		<tr>
			<#-- <th>${uiLabelMap.roleTypeId!}</th> -->
			<th>${uiLabelMap.customFieldName!}</th>
			<th>${uiLabelMap.customGroup!}</th>
			<th>${uiLabelMap.customFieldType!}</th>
			<th>${uiLabelMap.customFieldFormat!}</th>
			<th>${uiLabelMap.sequence!}</th>
			<th>${uiLabelMap.fieldLength!}</th>
			<th>${uiLabelMap.hide!}</th>
			<th class="text-center">Action</th>
		</tr>
		</thead>
		<tbody>
		
		<#if customFieldList?has_content>
			
		<#list customFieldList as ec>
		<tr>
			<#-- <td>
			<#assign roleType = delegator.findOne("RoleType",Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId", ec.roleTypeId), false)?if_exists/>
			${roleType.description!}
			</td> -->
			<td>${ec.customFieldName!}</td>
			<td>${ec.groupName!}</td>
			<td>${ec.customFieldType!}</td>
			<td>${ec.customFieldFormat!}</td>
			<td>${ec.sequenceNumber!}</td>
			<td>
				<#if ec.customFieldLength?has_content && ec.customFieldLength == -1>
					Unlimited
				<#else>
					${ec.customFieldLength!}
				</#if>
			</td>
			<td>${ec.hide!}</td>
			<td class="text-center">
				<div class="">
					<a href="editCustomField?customFieldId=${ec.customFieldId}&groupId=${ec.groupId!}" class="btn btn-xs btn-primary tooltips" data-original-title="Edit"><i class="fa fa-pencil info"></i></a>
					<a class="btn btn-xs btn-danger tooltips confirm-message" href="${deleteActionUrl}?customFieldId=${ec.customFieldId}&groupId=${ec.groupId!}" data-original-title="Remove"><i class="fa fa-times red"></i></a>
				</div>
			</td>	
		</tr>
		
		</#list>
			
		</#if>
		
		</tbody>
		</table>
	</div>

</div>

<script type="text/javascript">

jQuery(document).ready(function() {

	$('#list-custom-field').DataTable({
  		"order": [],
  		"fnDrawCallback": function( oSettings ) {
      		resetDefaultEvents();
    	}
	});
	
});	
	
</script>
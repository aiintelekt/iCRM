<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

  
<#--   		
<div class="table-responsive">
	<table class="table table-hover" id="grouping-code-list">
	<thead>
	<tr>
		<th>${uiLabelMap.groupingCode!}</th>
		<th>${uiLabelMap.description!}</th>
		<th>${uiLabelMap.sequence!}</th>
		<th class="text-center">Action</th>
	</tr>
	</thead>
	<tbody>
	
	<#if groupingCodeList?has_content>
		
	<#list groupingCodeList as ec>
	<tr>
		<td>${ec.groupingCode!}</td>
		<td>${ec.description!}</td>
		<td>${ec.sequenceNumber!}</td>
		<td class="text-center">
			<div class="">
				<a href="editGroupingCode?groupingCodeId=${ec.customFieldGroupingCodeId}" class="btn btn-xs btn-primary tooltips" data-original-title="Edit"><i class="fa fa-pencil info"></i></a>
				<a class="btn btn-xs btn-secondary btn-danger tooltips confirm-message" href="deleteGroupingCode?groupingCodeId=${ec.customFieldGroupingCodeId}" data-original-title="Remove"><i class="fa fa-times red"></i></a>
			</div>
		</td>	
	</tr>
	
	</#list>
		
	</#if>
	
	</tbody>
	</table>
</div>
 
-->	
<div class="row" style="width:100%" id="listof-lead">
  <div  class="col-lg-12 col-md-12 col-sm-12 dash-panel">
  <@pageSectionHeader title="${uiLabelMap.List} ${uiLabelMap.GroupingCode}" />
   
             
     <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/ag-grid/21.2.2/ag-grid-community.min.js"></script>

  
          <div id="groupCodeAgGrid"  class="ag-theme-balham"> </div>

     <script type="text/javascript" src="/cf-resource/js/ag-grid/group-code.js"></script>

    

  </div>

 </div>
<#--  

<script type="text/javascript">
jQuery(document).ready(function() {	

	$('#grouping-code-list').DataTable({
  		"order": [],
  		"fnDrawCallback": function( oSettings ) {
      		resetDefaultEvents();
    	}
	});
	
});	
</script>

-->
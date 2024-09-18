<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
	<#-- <div class="float-right">
		<div class="form-group row">
			<div class="col-sm-5">
				<input type="text" class="form-control input-sm" placeholder="">
			</div>
			<div class="col-sm-3">
				<button type="reset" class="btn btn-xs btn-primary m5">Search</button>
			</div>
			<div class="col-sm-4">
				<a href="" class="btn btn-xs btn-primary m5" alt="Report"
					title="Report">Clear All Filters</a>
			</div>
	
		</div>
	
	</div> -->


  <#--  

<div class="table-responsive">
	<table class="table table-hover" id="ca">
	<thead>
	<tr>
		<th>${uiLabelMap.groupingCode!}</th>
		<th>${uiLabelMap.economicMetricId!}</th>
		<th>${uiLabelMap.economicMetricName!}</th>
		<th>${uiLabelMap.Active!}</th>
		
		<th>${uiLabelMap.sequence!}</th>
				
		<th class="text-center">Action</th>
	</tr>
	</thead>
	<tbody>
	
	<#if customFieldGroupList?has_content>
		
	<#list customFieldGroupList as ec>
	
	<#assign groupingCodeRef = ec.getRelatedOne("CustomFieldGroupingCode", false)! />
	
	<tr>
		<td>${groupingCodeRef.groupingCode!}</td>
		
		<td>${ec.groupId!}</td>
		<td>${ec.groupName!}</td>
		<td>${ec.isActive!}</td>
		
		<td>${ec.sequence!}</td>
		
		<td class="text-center">
			<div class="">
				<a href="viewEconomicValueForGroup?groupId=${ec.groupId}" class="btn btn-xs btn-primary tooltips " data-original-title="View Economic Metrics"><i class="fa fa-eye info"></i></a>
				<a href="economicValueForGroup?groupId=${ec.groupId}" class="btn btn-xs btn-primary tooltips " data-original-title="Add Economic Metrics"><i class="fa fa-plus info"></i></a>
				<a href="editEconomicMetric?groupId=${ec.groupId}" class="btn btn-xs btn-primary tooltips" data-original-title="Edit"><i class="fa fa-pencil info"></i></a>
			</div>
		</td>	
	</tr>
	
	</#list>
		
	</#if>
	
	</tbody>
	</table>
</div>


-->

<#-- 
<div class="row padding-r">
	<div class="col-md-12">
		<div class="portlet-body form">
			
			<div class="panel-group" id="accordionMenu" role="tablist"
				aria-multiselectable="true">
				
				<#if customFieldGroupList?has_content>
					
				<#list customFieldGroupList as ec>
				
				<div class="panel panel-default">
					<div class="panel-heading" role="tab" id="headingTwo">
						<h4 class="panel-title">
							<a role="button" data-toggle="collapse" data-parent="#accordionMenu"
								href="#accordionGroup${ec_index+1}" aria-expanded="false"
								aria-controls="collapseOne"> ${ec.groupName!} </a>
						</h4>
					</div>
					<div id="accordionGroup${ec_index+1}" class="panel-collapse collapse"
						role="tabpanel" aria-labelledby="headingOne">
						<div class="panel-body">
							<p class="float-right">
								<a href="editEconomicMetric?groupId=${ec.groupId}" class="btn btn-xs btn-primary mt tooltips" data-original-title="Edit">Update</a>	
								<a href="viewEconomicValueForGroup?groupId=${ec.groupId}"
									class="btn btn-xs btn-primary mt">View Economic Values</a>
								<a href="economicValueForGroup?groupId=${ec.groupId}"
									class="btn btn-xs btn-primary mt">Add Economic Values</a>
							</p>
							
						</div>
					</div>
				</div>
				
				</#list>
					
				</#if>
				
			</div>
			
		</div>
	</div>
	
</div>
 -->
 <div  class="" style="width:100%">
   <div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
   <@pageSectionHeader title="${uiLabelMap.List} ${uiLabelMap.EconomicMetric}" />

    
      
         
     <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/ag-grid/21.2.2/ag-grid-community.min.js"></script>

  
          <div id="findAttributeGroupgrid"  class="ag-theme-balham"> </div>

     <script type="text/javascript" src="/cf-resource/js/ag-grid/econometric-code.js"></script>

    </div>

  </div>
  
  <#--  
 
<script type="text/javascript">

jQuery(document).ready(function() {	

	$('#ca').DataTable({
  		"order": [],
  		"fnDrawCallback": function( oSettings ) {
      		resetDefaultEvents();
    	}
	});

});			
</script>
 -->
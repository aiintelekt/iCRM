<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>

<div class="page-header border-b">
	<h1 class="float-left">Configuration ${uiLabelMap.EconomicsMetrics}</h1>
	<div class="float-right">
		
	</div>
</div>

<div class="card-header mt-2 mb-3">
   <form method="post" class="form-horizontal" data-toggle="validator">
   		
   		<input type="hidden" name="activeTab" value="economicsMetrics" />	
   		
      <div class="row">
      	
         <div class="col-md-2 col-sm-2">
         	<@simpleDropdownInput 
				id="groupingCode"
				options=groupingCodeList
				required=false
				value=metricIndicator.groupingCode
				allowEmpty=true
				tooltip = uiLabelMap.groupingCode
				emptyText = uiLabelMap.groupingCode
				dataLiveSearch=true
				/>
         </div>
         
         <@fromSimpleAction id="" showCancelBtn=false isSubmitAction=true submitLabel="Find"/>
        	
      </div>
   </form>
   <div class="clearfix"> </div>
</div>

<div class="page-header">
	<h2 class="float-left"></h2>
	<div class="float-right">
		<input class="btn btn-xs btn-primary mt-2 mr-1" id="batch-update-button" value="Batch Update" type="button">
	</div>
</div>

<form id="batchUpdateForm" method="post" action="<@ofbizUrl>batchUpdatePartyMetricIndicator</@ofbizUrl>">
<div class="table-responsive">
	<table class="table table-hover" id="list-metric-indicator">
	<thead>
	<tr>
		<th>${uiLabelMap.groupingCode!}</th>
		<th>${uiLabelMap.propertyName!}</th>
		<th>${uiLabelMap.propertyValue!}</th>
		<th>${uiLabelMap.sequence!}</th>
		<#-- <th><div class="ml-1"><input id="add-select-all" type="checkbox"></div></th> -->
	</tr>
	</thead>
	<tbody>
	
	
	
	<#if metricIndicatorList?has_content>
		
	<#list metricIndicatorList as ec>
	
	
	
	<tr>
		<td>${ec.groupingCode!}</td>
		<td>${ec.propertyName!}</td>
		<td>${ec.propertyValue!}</td>
		<td>
			<input type="hidden" name="partyId" value="${ec.partyId!}">
			<input type="hidden" name="groupingCode" value="${ec.groupingCode!}">
			<input type="hidden" name="propertyName" value="${ec.propertyName!}">
			<@simpleInput 
				id="sequenceNumber"
				placeholder=uiLabelMap.sequence
				value=ec.sequenceNumber
				tooltip = uiLabelMap.sequence
				inputType="number"
				required=false
				min=1
				/>	
		</td>
		<#-- <td><div class="ml-1"><input type="checkbox" name="customers"></div></td> -->
	</tr>
	
	</#list>
		
	</#if>
	
	
	
	</tbody>
	</table>
</div>
</form>

<script type="text/javascript">

jQuery(document).ready(function() {	

	$('#list-metric-indicator').DataTable({
  		"order": [],
  		"columnDefs": [ 
        	{
				"targets": 3,
				"orderable": false
			} 
		],
	});
	
$("#add-select-all").change(function(){  
    var status = this.checked; 
    $('input[name="customers"]').each(function(){ 
        this.checked = status; 
    });
});	

$('#batch-update-button').on('click', function(){
	
	$.post('batchUpdatePartyMetricIndicator', $('#batchUpdateForm').serialize(), function(returnedData) {

		if (returnedData.code == 200) {
			
			showAlert ("success", returnedData.message)
			//location.reload();
			$('#list-metric-indicator').DataTable().ajax.reload();
			
		} else {
			showAlert ("error", returnedData.message)
		}
		
	});
	
});

});
		
</script>

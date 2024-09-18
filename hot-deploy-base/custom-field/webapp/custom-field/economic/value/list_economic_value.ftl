<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row" style="width:100%">
	
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

  	<@pageSectionHeader title="${uiLabelMap.List} ${uiLabelMap.EconomicValue}" />
    <div id="table-responsive">
		<div id="economicMetricAgGrid"  class="ag-theme-balham"> </div>
     	<script type="text/javascript" src="/cf-resource/js/ag-grid/economic-metric.js"></script>
    </div>
</div>
</div>



<#--  

<script type="text/javascript">

var customFieldId, groupId;

jQuery(document).ready(function() {	

	$('#list-economic-value').DataTable({
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

-->
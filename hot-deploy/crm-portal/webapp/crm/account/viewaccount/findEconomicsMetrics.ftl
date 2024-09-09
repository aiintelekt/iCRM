<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<@pageSectionHeader title=uiLabelMap.EconomicsMetrics />

<div class="card-header mt-2 mb-3">
   <form method="post" class="form-horizontal" data-toggle="validator">
   		
   		<input type="hidden" name="activeTab" value="economicsMetrics" />	
   		
      <div class="row">
      	
         <div class="col-md-2 col-sm-2">
         	<@dropdownCell 
				id="groupingCode"
				options=groupingCodeList
				required=false
				value=metricIndicator.groupingCode
				allowEmpty=true
				tooltip = uiLabelMap.groupingCode
				placeholder = uiLabelMap.groupingCode
				dataLiveSearch=true
				/>
         </div>
         
         <div class="col-md-2 col-sm-2">
         	<@dropdownCell 
				id="economicCodeId"
				required=false
				value=metricIndicator.economicCodeId
				allowEmpty=true
				tooltip = uiLabelMap.economicCode
				placeholder = uiLabelMap.economicCode
				dataLiveSearch=true
				/>
         </div>
         <div class="clearfix"> </div>
			<div class="col-md-1 col-sm-1 pl-0">
         	<@submit label="Find"/>
         	</div>
		 <div class="clearfix"> </div>
        	
      </div>
   </form>
   <div class="clearfix"> </div>
</div>

<@inputHidden id="metricListData" value=metricIndicatorListStr />
<div class="table-responsive">				
	<div id="metricGrid" style="width: 100%;" class="ag-theme-balham"></div>   			
</div>
<script type="text/javascript" src="/crm-resource/js/ag-grid/economicMetric/economicMetric.js"></script>

<script type="text/javascript">

jQuery(document).ready(function() {
	loadEconomicCodeList();
	$("#groupingCode").change(function() {
		loadEconomicCodeList()
	});
});

function loadEconomicCodeList() {
	var nonSelectContent = "<span class='nonselect'>Select ${uiLabelMap.economicCode!}</span>";
	var groupNameOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Select ${uiLabelMap.economicCode!}</option>';		
		
	if ( $("#groupingCode").val() ) {
		
		$.ajax({
			      
			type: "POST",
	     	url: "getCustomFieldGroups",
	        data:  {"groupingCode": $("#groupingCode").val()},
	        async: false,
	        success: function (data) {   
	            
	            if (data.code == 200) {
	            
	            	for (var i = 0; i < data.groups.length; i++) {
	            		var group = data.groups[i];
	            		groupNameOptions += '<option value="'+group.groupId+'">'+group.groupName+'</option>';
	            	}
	            	
	            }
				    	
	        }
	        
		});    
		
		$("#economicCodeId").html( groupNameOptions );
		
		<#if metricIndicator.economicCodeId?has_content>
		$("#economicCodeId").val( "${metricIndicator.economicCodeId}" );
		</#if>
		
		$('#economicCodeId').dropdown('refresh');
	}
		
}
		
</script>

